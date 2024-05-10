/*
 * Copyright (C) 2023 The LibreMobileOS foundation
 * Copyright (C) 2024 DerpFest AOSP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.derpfest.systemui.qs.tileimpl;

import com.android.systemui.qs.tileimpl.QSTileImpl

import org.derpfest.systemui.qs.tiles.AmbientDisplayTile
import org.derpfest.systemui.qs.tiles.AODTile
import org.derpfest.systemui.qs.tiles.CaffeineTile
import org.derpfest.systemui.qs.tiles.CellularTile
import org.derpfest.systemui.qs.tiles.DataSwitchTile
import org.derpfest.systemui.qs.tiles.FastChargeTile
import org.derpfest.systemui.qs.tiles.FlashlightStrengthTile
import org.derpfest.systemui.qs.tiles.HeadsUpTile
import org.derpfest.systemui.qs.tiles.PowerShareTile
import org.derpfest.systemui.qs.tiles.ReadingModeTile
import org.derpfest.systemui.qs.tiles.SyncTile
import org.derpfest.systemui.qs.tiles.UsbTetherTile
import org.derpfest.systemui.qs.tiles.VpnTile
import org.derpfest.systemui.qs.tiles.VPNTetheringTile
import org.derpfest.systemui.qs.tiles.WifiTile

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
interface DerpFestQSModule {
    /** Inject AmbientDisplayTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(AmbientDisplayTile.TILE_SPEC)
    fun bindAmbientDisplayTile(ambientDisplayTile: AmbientDisplayTile): QSTileImpl<*>

    /** Inject AODTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(AODTile.TILE_SPEC)
    fun bindAODTile(aodTile: AODTile): QSTileImpl<*>

    /** Inject CaffeineTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(CaffeineTile.TILE_SPEC)
    fun bindCaffeineTile(caffeineTile: CaffeineTile): QSTileImpl<*>

    /** Inject CellularTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(CellularTile.TILE_SPEC)
    fun bindCellularTile(cellularTile: CellularTile): QSTileImpl<*>

    /** Inject DataSwitchTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(DataSwitchTile.TILE_SPEC)
    fun bindDataSwitchTile(dataSwitchTile: DataSwitchTile): QSTileImpl<*>

    /** Inject FastChargeTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(FastChargeTile.TILE_SPEC)
    fun bindFastChargeTile(fastChargeTile: FastChargeTile): QSTileImpl<*>

    /** Inject FlashlightStrengthTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(FlashlightStrengthTile.TILE_SPEC)
    fun bindFlashlightStrengthTile(flashlightStrengthTile: FlashlightStrengthTile): QSTileImpl<*>

    /** Inject HeadsUpTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(HeadsUpTile.TILE_SPEC)
    fun bindHeadsUpTile(headsUpTile: HeadsUpTile): QSTileImpl<*>

    /** Inject PowerShareTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(PowerShareTile.TILE_SPEC)
    fun bindPowerShareTile(powerShareTile: PowerShareTile): QSTileImpl<*>

    /** Inject ReadingModeTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(ReadingModeTile.TILE_SPEC)
    fun bindReadingModeTile(readingModeTile: ReadingModeTile): QSTileImpl<*>

    /** Inject SyncTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(SyncTile.TILE_SPEC)
    fun bindSyncTile(syncTile: SyncTile): QSTileImpl<*>

    /** Inject UsbTetherTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(UsbTetherTile.TILE_SPEC)
    fun bindUsbTetherTile(usbTetherTile: UsbTetherTile): QSTileImpl<*>

    /** Inject VpnTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(VpnTile.TILE_SPEC)
    fun bindVpnTile(vpnTile: VpnTile): QSTileImpl<*>

    /** Inject VPNTetheringTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(VPNTetheringTile.TILE_SPEC)
    fun bindVPNTetheringTile(vpnTetheringTile: VPNTetheringTile): QSTileImpl<*>

    /** Inject WifiTile into tileMap in QSModule */
    @Binds
    @IntoMap
    @StringKey(WifiTile.TILE_SPEC)
    fun bindWifiTile(wifiTile: WifiTile): QSTileImpl<*>

}
