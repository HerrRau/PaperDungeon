package com.herrrau.crawlspace;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActivitySingleCharacter extends AppCompatActivity {

    public static final String KEY_EXTRA = "com.example.yourapp.KEY_BOOK";
    TextView characterView;

    String characterFingerprint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        Button buttonRulesBack  = findViewById(R.id.buttonCharacterBack);
        buttonRulesBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        characterView = findViewById(R.id.textViewCharacterSingle);

        if (getIntent().hasExtra(KEY_EXTRA)) {
            characterFingerprint = getIntent().getStringExtra(KEY_EXTRA);
            //characterView.setText(CharacterAttributes.values()[0].getFormattedCharacterDataFull(characterFingerprint));
            characterView.setText(Character.getFormattedCharacterDataFull(characterFingerprint));
            //characterView.setText(characterFingerprint);
        } else {
            throw new IllegalArgumentException("Activity cannot find  extras " + KEY_EXTRA);
        }
    }




}