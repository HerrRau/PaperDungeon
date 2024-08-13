package com.herrrau.crawlspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.LinkedList;

public class ActivityAllCharacters extends AppCompatActivity {

    static LinkedList<String> characterFingerprints;
    LinearLayout layoutCharacters;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);

        layoutCharacters = findViewById(R.id.layoutCharacters);

        Button buttonCharactersBack = findViewById(R.id.buttonCharactersBack);
        buttonCharactersBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button buttonCharactersCreate = findViewById(R.id.buttonCharactersCreate);
        buttonCharactersCreate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(1);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        characterFingerprints = new LinkedList<>();
        settings = getSharedPreferences("xxx", Context.MODE_PRIVATE);
        editor = settings.edit();
        displayStoredCharacters();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    void goToActivity(int i) {
        Intent intent = null;
        Bundle bundle = null;
        switch (i) {
            case 0:
                intent = new Intent(this, ActivityStart.class);
                bundle = intent.getExtras();
                break;
            case 1:
                intent = new Intent(this, ActivityCharacterCreation.class);
                bundle = intent.getExtras();
        }
        startActivity(intent);
    }

    private void loadStoredCharacters() {
        characterFingerprints = Setup.loadCharacters(this);
    }

    private void displayStoredCharacters() {
        loadStoredCharacters();
        System.out.println(characterFingerprints.size() + " characters");
        layoutCharacters.removeAllViews();
        //create and prepare data array for stored characters
        String[] characterData = new String[characterFingerprints.size()];
        for (int i = 0; i < characterData.length; i++) {
            characterData[i] = characterFingerprints.get(i);
        }
        //for every character
        for (int i = 0; i < characterFingerprints.size(); i++) {
            int j = i;

            //create horizontal layout
            LinearLayout l = new LinearLayout(this);
            l.setOrientation(LinearLayout.HORIZONTAL);

            //create buttons for viewing, choosing, deleting
            Button buttonView = new Button(this);
            buttonView.setText(R.string.characters_all_view);
            buttonView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String s = characterFingerprints.get(j);
                    Intent intent = new Intent(ActivityAllCharacters.this, ActivitySingleCharacter.class);
                    intent.putExtra(ActivitySingleCharacter.KEY_EXTRA, s);
                    startActivity(intent);
                }
            });
            l.addView(buttonView);

            Button buttonChoose = new Button(this);
            buttonChoose.setText(R.string.characters_all_choose);
            buttonChoose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    characterFingerprints.addFirst(characterFingerprints.remove(j));
                    neuDurchzaehlen();
                    //to do: highlight, oder finish:
                    Setup.activeCharacter = Character.getCharacterFromStats(characterFingerprints.get(0));
                    System.out.println("Active character: " + Setup.activeCharacter.name);
                    finish();
                    //goToActivity(0);
                }
            });
            l.addView(buttonChoose);

            Button buttonDelete = new Button(this);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (true || characterFingerprints.size() > 1) {
                        System.out.println("Trying to delete... " + j);
                        characterFingerprints.remove(j);
                        neuDurchzaehlen();
                        displayStoredCharacters(); //->
                    }
                    if (false && characterFingerprints.size() == 1) {
                        Setup.activeCharacter = Character.getCharacterFromStats(characterFingerprints.get(0));
                    }
                }
            });
            buttonDelete.setText(R.string.characters_all_delete);
            l.addView(buttonDelete);

            layoutCharacters.addView(l);

            //add text view for name
            TextView t = new TextView(this);

            //t.setText(CharacterAttributes.values()[0].getFormattedCharacterDataBrief(characterFingerprints.get(i)));
            t.setText(Character.getFormattedCharacterDataBrief(characterFingerprints.get(i)));

            layoutCharacters.addView(t);
            //l.addView(t);

        }
    }

    private void neuDurchzaehlen() {
        Setup.saveCharacters(this, characterFingerprints);
    }

}