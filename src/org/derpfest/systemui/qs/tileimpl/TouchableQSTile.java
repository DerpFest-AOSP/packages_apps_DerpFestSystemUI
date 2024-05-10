package org.derpfest.systemui.qs.tileimpl;

import android.view.View;

// For use with SliderQSTileViewImpl
public interface TouchableQSTile {

    View.OnTouchListener getTouchListener();

    String getSettingsSystemKey();
}
