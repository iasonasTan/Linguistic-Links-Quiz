package com.app.ll;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;

public final class SettingsManager {
    public static final String SETTINGS_PREFERENCES_NAME = "ll.settings.main";
    public static final String ACTION_LOAD_SETTINGS      = "ll.settings.update";
    public static final String ENABLE_VIBRATIONS_NAME    = "ll.settings.vibrations";

    private final Context context;
    private final View mRoot;

    public SettingsManager(Context ctx, View root) {
        context = ctx;
        mRoot = root;
    }

    public void store() {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE);

        CheckBox enableVibrationsCheckBox = mRoot.findViewById(R.id.enable_vibrations_checkbox);
        preferences.edit()
                .putBoolean(ENABLE_VIBRATIONS_NAME, enableVibrationsCheckBox.isChecked())
                .apply();

        Intent reloadSettingsIntent = new Intent(ACTION_LOAD_SETTINGS).setPackage(context.getPackageName());
        context.sendBroadcast(reloadSettingsIntent);
    }

    public void load() {
        SharedPreferences preferences = context.getSharedPreferences(SETTINGS_PREFERENCES_NAME, Context.MODE_PRIVATE);

        CheckBox enableVibrationsCheckBox = mRoot.findViewById(R.id.enable_vibrations_checkbox);
        enableVibrationsCheckBox.setChecked(preferences.getBoolean(ENABLE_VIBRATIONS_NAME, true));
    }
}
