package com.app.ll.page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

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
        CheckBox checkBox = view.findViewById(R.id.enable_vibrations_checkbox);
        checkBox.setOnCheckedChangeListener((b, checked) -> mSettingsManager.store());
    }

    @Override
    protected String name() {
        return NAME;
    }
}
