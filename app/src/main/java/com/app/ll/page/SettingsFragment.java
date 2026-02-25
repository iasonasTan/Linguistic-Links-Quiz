package com.app.ll.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.ll.R;
import com.app.ll.SettingsManager;

public class SettingsFragment extends AbstractPage {
    public static final String NAME = "ll.page.settings";

    private SettingsManager mSettingsManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSettingsManager = new SettingsManager(requireContext(), view);
        mSettingsManager.load();

        addStoreListeners(view);
    }

    private void addStoreListeners(View view) {
        final OnCheckedChangeListener listener = (b, checked) -> mSettingsManager.store();

        CheckBox vibrationsCheckbox = view.findViewById(R.id.enable_vibrations_checkbox);
        vibrationsCheckbox.setOnCheckedChangeListener(listener);

        CheckBox scoreCheckbox = view.findViewById(R.id.enable_score_checkbox);
        scoreCheckbox.setOnCheckedChangeListener(listener);
    }

    @Override
    protected String name() {
        return NAME;
    }
}
