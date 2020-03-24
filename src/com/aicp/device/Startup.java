/*
* Copyright (C) 2013 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.aicp.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import static com.aicp.device.KeyHandler.GESTURE_MUSIC_PLAYBACK_SETTINGS_VARIABLE_NAME;
import com.aicp.device.GestureSettings;

public class Startup extends BroadcastReceiver {
    private static void restore(String file, boolean enabled) {
        if (file == null) {
            return;
        }
        Utils.writeValue(file, enabled ? "1" : "0");
    }

    private static void restore(String file, String value) {
        if (file == null) {
            return;
        }
        Utils.writeValue(file, value);
    }

    private static String getGestureFile(String key) {
        return GestureSettings.getGestureFile(key);
    }

    private void maybeImportOldSettings(Context context) {
        boolean imported = Settings.System.getInt(context.getContentResolver(), "omni_device_setting_imported", 0) != 0;
        if (!imported) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_SRGB_SWITCH, false);
            Settings.System.putInt(context.getContentResolver(), SRGBModeSwitch.SETTINGS_KEY, enabled ? 1 : 0);

            enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_HBM_SWITCH, false);
            Settings.System.putInt(context.getContentResolver(), HBMModeSwitch.SETTINGS_KEY, enabled ? 1 : 0);

            enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_DCD_SWITCH, false);
            Settings.System.putInt(context.getContentResolver(), DCDModeSwitch.SETTINGS_KEY, enabled ? 1 : 0);

            enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_DCI_SWITCH, false);
            Settings.System.putInt(context.getContentResolver(), DCIModeSwitch.SETTINGS_KEY, enabled ? 1 : 0);

            enabled = sharedPrefs.getBoolean(DeviceSettings.KEY_WIDE_SWITCH, false);
            Settings.System.putInt(context.getContentResolver(), WideModeSwitch.SETTINGS_KEY, enabled ? 1 : 0);

            String vibrSystemStrength = sharedPrefs.getString(DeviceSettings.KEY_SYSTEM_VIBSTRENGTH, VibratorSystemStrengthPreference.getDefaultValue());
            Settings.System.putString(context.getContentResolver(), VibratorSystemStrengthPreference.SETTINGS_KEY, vibrSystemStrength);

            String vibrCallStrength = sharedPrefs.getString(DeviceSettings.KEY_CALL_VIBSTRENGTH, VibratorCallStrengthPreference.getDefaultValue());
            Settings.System.putString(context.getContentResolver(), VibratorCallStrengthPreference.SETTINGS_KEY, vibrCallStrength);

            String vibrNotifStrength = sharedPrefs.getString(DeviceSettings.KEY_NOTIF_VIBSTRENGTH, VibratorNotifStrengthPreference.getDefaultValue());
            Settings.System.putString(context.getContentResolver(), VibratorNotifStrengthPreference.SETTINGS_KEY, vibrNotifStrength);

            Settings.System.putInt(context.getContentResolver(), "omni_device_setting_imported", 1);
        }
    }

    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        maybeImportOldSettings(context);
        restoreAfterUserSwitch(context);
    }

    public static void restoreAfterUserSwitch(Context context) {
        // music playback
        final boolean musicPlaybackEnabled = Settings.System.getInt(context.getContentResolver(),
                "Settings.System."+com.aicp.device.KeyHandler.GESTURE_MUSIC_PLAYBACK_SETTINGS_VARIABLE_NAME, 0) == 1;
        GestureSettings.setMusicPlaybackGestureEnabled(musicPlaybackEnabled);

        // circle -> camera
        String mapping = GestureSettings.DEVICE_GESTURE_MAPPING_1;
        String value = Settings.System.getString(context.getContentResolver(), mapping);
        if (TextUtils.isEmpty(value)) {
            value = AppSelectListPreference.CAMERA_ENTRY;
            Settings.System.putString(context.getContentResolver(), mapping, value);
        }
        boolean enabled = !value.equals(AppSelectListPreference.DISABLED_ENTRY);
        restore(getGestureFile(GestureSettings.KEY_CIRCLE_APP), enabled);

        // down arrow -> flashlight
        mapping = GestureSettings.DEVICE_GESTURE_MAPPING_2;
        value = Settings.System.getString(context.getContentResolver(), mapping);
        if (TextUtils.isEmpty(value)) {
            value = AppSelectListPreference.TORCH_ENTRY;
            Settings.System.putString(context.getContentResolver(), mapping, value);
        }
        enabled = !value.equals(AppSelectListPreference.DISABLED_ENTRY);
        restore(getGestureFile(GestureSettings.KEY_DOWN_ARROW_APP), enabled);

        // M Gesture
        value = Settings.System.getString(context.getContentResolver(), GestureSettings.DEVICE_GESTURE_MAPPING_3);
        enabled = !TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY);
        restore(getGestureFile(GestureSettings.KEY_M_GESTURE_APP), enabled);

        // down swipe
        value = Settings.System.getString(context.getContentResolver(), GestureSettings.DEVICE_GESTURE_MAPPING_6);
        enabled = !TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY);
        restore(getGestureFile(GestureSettings.KEY_DOWN_SWIPE_APP), enabled);

        // up swipe
        value = Settings.System.getString(context.getContentResolver(), GestureSettings.DEVICE_GESTURE_MAPPING_7);
        enabled = !TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY);
        restore(getGestureFile(GestureSettings.KEY_UP_SWIPE_APP), enabled);

        // left swipe
        value = Settings.System.getString(context.getContentResolver(), GestureSettings.DEVICE_GESTURE_MAPPING_8);
        enabled = !TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY);
        restore(getGestureFile(GestureSettings.KEY_LEFT_SWIPE_APP), enabled);

        // right swipe
        value = Settings.System.getString(context.getContentResolver(), GestureSettings.DEVICE_GESTURE_MAPPING_9);
        enabled = !TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY);
        restore(getGestureFile(GestureSettings.KEY_RIGHT_SWIPE_APP), enabled);

        // S Gesture
        value = Settings.System.getString(context.getContentResolver(), GestureSettings.DEVICE_GESTURE_MAPPING_10);
        enabled = !TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY);
        restore(getGestureFile(GestureSettings.KEY_S_GESTURE_APP), enabled);

        // W Gesture
        value = Settings.System.getString(context.getContentResolver(), GestureSettings.DEVICE_GESTURE_MAPPING_11);
        enabled = !TextUtils.isEmpty(value) && !value.equals(AppSelectListPreference.DISABLED_ENTRY);
        restore(getGestureFile(GestureSettings.KEY_W_GESTURE_APP), enabled);

        enabled = Settings.System.getInt(context.getContentResolver(), SRGBModeSwitch.SETTINGS_KEY, 0) != 0;
        restore(SRGBModeSwitch.getFile(context), enabled);

        enabled = Settings.System.getInt(context.getContentResolver(), DCDModeSwitch.SETTINGS_KEY, 0) != 0;
        restore(DCDModeSwitch.getFile(context), enabled);

        enabled = Settings.System.getInt(context.getContentResolver(), DCIModeSwitch.SETTINGS_KEY, 0) != 0;
        restore(DCIModeSwitch.getFile(context), enabled);

        enabled = Settings.System.getInt(context.getContentResolver(), WideModeSwitch.SETTINGS_KEY, 0) != 0;
        restore(WideModeSwitch.getFile(context), enabled);

        enabled = Settings.System.getInt(context.getContentResolver(), HBMModeSwitch.SETTINGS_KEY, 0) != 0;
        restore(HBMModeSwitch.getFile(context), enabled);
    }
}
