package com.herrrau.crawlspace;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ActivitySettings extends AppCompatActivity {

    NumberPicker pickColumns, pickRows, pickStyle;
    RadioButton radioButton1, radioButton2, radioButton3;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //removes keyboard (necessary, but not always sufficient)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //radio buttons for color background, mainly unused
        radioButton1 = findViewById(R.id.radioButtonSettings1);
        radioButton2 = findViewById(R.id.radioButtonSettings2);
        radioButton3 = findViewById(R.id.radioButtonSettings3);
        radioButton1.setChecked(true);

        //check box for strict mode, used
        checkBox = findViewById(R.id.checkBox);
        checkBox.setChecked(true);

        //back button
        Button buttonSettingsBack = findViewById(R.id.buttonSettingsGameBack);
        buttonSettingsBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //customize column & row picker
        pickColumns = findViewById(R.id.numberPickerSettingsColumns);
        pickColumns.setMinValue(2);
        pickColumns.setMaxValue(12);
        pickColumns.setValue(Setup.getStandardCols());
        pickColumns.setWrapSelectorWheel(false);
        pickColumns.setBackgroundColor(Color.GREEN);
        pickRows = findViewById(R.id.numberPickerSettingsRows);
        pickRows.setMinValue(2);
        pickRows.setMaxValue(12);
        pickRows.setValue(Setup.getStandardRows());
        pickRows.setWrapSelectorWheel(false);
        pickRows.setBackgroundColor(Color.RED);
        if (!Setup.debug) {
            LinearLayout layoutSize = findViewById(R.id.layoutSize);
            layoutSize.setVisibility(View.GONE);
            RadioGroup radioBackground = findViewById(R.id.radioGroupBackground);
            radioBackground.setVisibility(View.GONE);
        }

        //customize style picker
        pickStyle = findViewById(R.id.numberPickerStyles);
        pickStyle.setMinValue(0);
        String[] styleNames = new String[FactoryTileStyle.values().length];
        for (int i = 0; i < styleNames.length; i++) {
            styleNames[i] = FactoryTileStyle.values()[i].getName();
        }
        pickStyle.setMaxValue(styleNames.length - 1);
        pickStyle.setDisplayedValues(styleNames);
        //pickStyle.setWrapSelectorWheel(false);
        pickStyle.setValue(4-4); // i.e. new grey
//        loadPresets();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAndApplySettings();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveAndApplySettings();
    }

    private void loadAndApplySettings() {
        System.out.println("Activity Settings loads: ");
        Setup.loadSettings(this);
        pickColumns.setValue(Setup.getStandardCols());
        pickRows.setValue(Setup.getStandardRows());
        checkBox.setChecked(Setup.strictMode);

    }

    private void saveAndApplySettings() {
        System.out.println("Activity Settings sets: ");
        FactoryTile.setStyle(pickStyle.getValue());
        if (radioButton1.isChecked()) ActivityGame.useBackgroundColor = true;
        else {
            ActivityGame.useBackgroundColor = false;
            if (radioButton2.isChecked()) ActivityGame.useFittedBackgroundImage = false;
            else ActivityGame.useFittedBackgroundImage = true;
        }
        Setup.saveSettings(this, pickColumns.getValue(), pickRows.getValue(), pickStyle.getValue(),checkBox.isChecked());
    }


}