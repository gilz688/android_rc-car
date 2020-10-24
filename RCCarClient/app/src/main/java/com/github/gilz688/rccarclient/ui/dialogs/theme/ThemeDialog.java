package com.github.gilz688.rccarclient.ui.dialogs.theme;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.github.gilz688.rccarclient.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import static android.content.Context.MODE_PRIVATE;

public class ThemeDialog extends DialogFragment {
    public static final String TAG = "SelectThemeDialog";
    public static final String PREF_NAME = "SharedPrefs";
    public static final String PREF_KEY_APP_THEME = "appTheme";
    public static final int DEFAULT_APP_THEME = 2;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int appThemeValue = sharedPreferences.getInt(PREF_KEY_APP_THEME, DEFAULT_APP_THEME);

        String[] appThemes = requireContext().getResources().getStringArray(R.array.app_themes);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(
                requireContext(),
                R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        );
        builder.setTitle(getString(R.string.text_app_theme));
        builder.setIcon(R.drawable.ic_theme);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.item_app_theme,
                appThemes
        );

        builder.setNegativeButton(
                android.R.string.cancel,
                (DialogInterface dialog, int val) -> dialog.dismiss());

        builder.setSingleChoiceItems(arrayAdapter, appThemeValue, (DialogInterface dialog, int value) -> {
            dialog.dismiss();
            sharedPreferences.edit().putInt(PREF_KEY_APP_THEME, value).apply();
            switch (value) {
                case 0:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case 1:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }
        });

        return builder.create();
    }
}