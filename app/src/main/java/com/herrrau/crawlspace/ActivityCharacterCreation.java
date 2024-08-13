package com.herrrau.crawlspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityCharacterCreation extends AppCompatActivity {

    Button buttonCharacterCreationSave, buttonCharacterCreationBack, buttonCharacterCreationCreate;
    TextView textViewResult;
    EditText editTextCharacterName;
    Character c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_creation);

        //removes keyboard (necessary, but not always sufficient)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        buttonCharacterCreationSave = findViewById(R.id.buttonCharacterCreationSave);
        buttonCharacterCreationBack = findViewById(R.id.buttonCharacterCreateBack);
        buttonCharacterCreationCreate = findViewById(R.id.buttonCharacterCreationCreate);
        editTextCharacterName = findViewById(R.id.editTextCharacterName);
        textViewResult = findViewById(R.id.textViewCharacterCreationResult);
        Character c;


        buttonCharacterCreationBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        buttonCharacterCreationCreate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createCharacter();
                //removes keyboard (necessary, but not always sufficient)
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                //remove keyboard focus, not always necessary
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editTextCharacterName.getWindowToken(), 0);
            }
        });

        buttonCharacterCreationSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveCharacter();
            }
        });

        buttonCharacterCreationSave.setEnabled(false);
        textViewResult.setText("");
    }


    private void createCharacter() {
        if (ActivityAllCharacters.characterFingerprints.size()>= 10) {
            textViewResult.setText("No more empty slots.");
        } else {
            buttonCharacterCreationSave.setEnabled(true);
            c = new Character();
            String name = editTextCharacterName.getText().toString();
            if (name.equals("")) c.name = "Gloria";
            if (name.contains(":")) name = name.replace(":", "");
            c.name = name;
            //textViewResult.setText(c.getStats());
            textViewResult.setText(Character.getFormattedCharacterDataFull(c.getStats()));
            editTextCharacterName.setText("");
        }
    }

    private void saveCharacter() {
        buttonCharacterCreationSave.setEnabled(false);
        if (ActivityAllCharacters.characterFingerprints.size()>= 10) {
            textViewResult.setText("No more empty slots."); //shouldnt happen
        } else {
            Setup.saveCharacterCreation(this, c);
            textViewResult.setText("Saved."+(ActivityAllCharacters.characterFingerprints.size()-1));
            finish();
        }
    }
}