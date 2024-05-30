package org.derpfest.systemui.qs.tiles;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraCharacteristics.Key;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.qs.QSTile.BooleanState;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.qs.QSHost;
import com.android.systemui.qs.QsEventLogger;
import com.android.systemui.qs.logging.QSLogger;
import com.android.systemui.qs.tiles.FlashlightTile;
import com.android.systemui.statusbar.policy.FlashlightController;

import org.derpfest.systemui.qs.tileimpl.TouchableQSTile;

import javax.inject.Inject;

public class FlashlightStrengthTile extends FlashlightTile implements TouchableQSTile {

    public static final String TILE_SPEC = "flashlight";

    private static final Key<Integer> FLASHLIGHT_MAX_BRIGHTNESS_CHARACTERISTIC =
            CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL;
    private static final Key<Integer> FLASHLIGHT_DEFAULT_BRIGHTNESS_CHARACTERISTIC =
            CameraCharacteristics.FLASH_INFO_STRENGTH_DEFAULT_LEVEL;

    private static final String FLASHLIGHT_BRIGHTNESS_SETTING = "flashlight_brightness";

    private final CameraManager mCameraManager;
    private final FlashlightController mFlashlightController;
    private boolean mSupportsSettingFlashLevel;
    private int mDefaultLevel;
    private int mMaxLevel;
    private float mCurrentPercent;
    private boolean mClicked = true;

    @Nullable private String mCameraId;

    private final View.OnTouchListener mTouchListener =
            new View.OnTouchListener() {
                float initX = 0;
                float initPct = 0;
                boolean moved = false;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (!mSupportsSettingFlashLevel || !mState.value) return false;

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN -> {
                            initX = motionEvent.getX();
                            initPct = initX / view.getWidth();
                            mClicked = false;
                            return true;
                        }
                        case MotionEvent.ACTION_MOVE -> {
                            float newPct = motionEvent.getX() / view.getWidth();
                            float deltaPct = Math.abs(newPct - initPct);
                            if (deltaPct > .03f) {
                                view.getParent().requestDisallowInterceptTouchEvent(true);
                                moved = true;
                                mCurrentPercent = Math.max(0.01f, Math.min(newPct, 1));
                                Settings.System.putFloat(
                                        mContext.getContentResolver(),
                                        FLASHLIGHT_BRIGHTNESS_SETTING,
                                        mCurrentPercent);
                                handleClick(view);
                            }
                            return true;
                        }
                        case MotionEvent.ACTION_UP -> {
                            if (moved) {
                                moved = false;
                                Settings.System.putFloat(
                                        mContext.getContentResolver(),
                                        FLASHLIGHT_BRIGHTNESS_SETTING,
                                        mCurrentPercent);
                            } else {
                                mClicked = true;
                                handleClick(view);
                            }
                            return true;
                        }
                    }
                    return true;
                }
            };

    @Inject
    public FlashlightStrengthTile(
            QSHost host,
            QsEventLogger qsEventLogger,
            @Background Looper backgroundLooper,
            @Main Handler mainHandler,
            FalsingManager falsingManager,
            MetricsLogger metricsLogger,
            StatusBarStateController statusBarStateController,
            ActivityStarter activityStarter,
            QSLogger qsLogger,
            FlashlightController flashlightController) {
        super(
                host,
                qsEventLogger,
                backgroundLooper,
                mainHandler,
                falsingManager,
                metricsLogger,
                statusBarStateController,
                activityStarter,
                qsLogger,
                flashlightController);
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        mFlashlightController = flashlightController;
        try {
            mCameraId = getCameraId();
            CameraCharacteristics characteristics =
                    mCameraManager.getCameraCharacteristics(mCameraId);
            mSupportsSettingFlashLevel =
                    flashlightController.isAvailable()
                            && mCameraId != null
                            && characteristics.get(FLASHLIGHT_MAX_BRIGHTNESS_CHARACTERISTIC) > 1;
            mMaxLevel = (int) characteristics.get(FLASHLIGHT_MAX_BRIGHTNESS_CHARACTERISTIC);
            mDefaultLevel = (int) characteristics.get(FLASHLIGHT_DEFAULT_BRIGHTNESS_CHARACTERISTIC);
        } catch (CameraAccessException | NullPointerException e) {
            Log.d("FlashlightStrengthTile", "Setting to non-controllable defaults");
            mCameraId = null;
            mSupportsSettingFlashLevel = false;
            mMaxLevel = 1;
            mDefaultLevel = 0;
        }
        float defaultPercent = ((float) mDefaultLevel) / ((float) mMaxLevel);
        mCurrentPercent =
                Settings.System.getFloat(
                        mContext.getContentResolver(),
                        FLASHLIGHT_BRIGHTNESS_SETTING,
                        defaultPercent);
    }

    @Override
    public View.OnTouchListener getTouchListener() {
        return mSupportsSettingFlashLevel ? mTouchListener : null;
    }

    @Override
    public String getSettingsSystemKey() {
        return "flashlight_brightness";
    }

    @Override
    public float getSettingsDefaultValue() {
        return ((float) mDefaultLevel) / ((float) mMaxLevel);
    }

    @Override
    protected void handleClick(@Nullable View view) {
        boolean newState = !mClicked || !mState.value;
        if (mSupportsSettingFlashLevel && newState) {
            try {
                int level = (int) (mCurrentPercent * ((float) mMaxLevel));
                // Not all devices has 100 light level so in that case, it will attain level 0
                // before 0%. We don't want flashlight is getting off other than 0%.
                // Make sure level won't go below 1.
                level = Math.max(level, 1);
                // Current percent won't below 1%
                // for just in case.
                float percent = mCurrentPercent * 100f;
                if (percent == 0f) {
                    mFlashlightController.setFlashlight(false);
                    newState = false;
                } else {
                    mCameraManager.turnOnTorchWithStrengthLevel(mCameraId, level);
                }
            } catch (CameraAccessException e) {
            }
        } else {
            mFlashlightController.setFlashlight(newState);
        }
        refreshState(newState);
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
        super.handleUpdateState(state, arg);
        if (mSupportsSettingFlashLevel) {
            String label = mHost.getContext().getString(R.string.quick_settings_flashlight_label);
            if (state.value) {
                label = String.format(
                        "%s - %s%%",
                        mHost.getContext().getString(R.string.quick_settings_flashlight_label),
                        Math.round(mCurrentPercent * 100f));
            }
            state.label = label;
        }
    }

    private String getCameraId() throws CameraAccessException {
        String[] ids = mCameraManager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
            if (flashAvailable != null
                    && flashAvailable
                    && lensFacing != null
                    && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return null;
    }
}
