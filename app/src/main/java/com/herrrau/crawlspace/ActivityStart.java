package com.herrrau.crawlspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityStart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        resources = getResources();

    Button buttonStart = findViewById(R.id.buttonStartGame);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(0);
            }
        });
        Button buttonServer = findViewById(R.id.buttonServer);
        buttonServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(1);
            }
        });
        Button buttonCharacters = findViewById(R.id.buttonCharacters);
        buttonCharacters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(2);
            }
        });
        Button buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(3);
            }
        });
        Button buttonRules = findViewById(R.id.buttonRules);
        buttonRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(4);
            }
        });
        Button buttonInformation = findViewById(R.id.buttonInformation);
        buttonInformation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                goToActivity(5);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        loadStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveStart();
    }


    public void onBackPressed() {
        finishAndRemoveTask();
    }

    void goToActivity(int i) {
        Intent intent = null;
        Bundle bundle = null;
        switch (i) {
            case (0):
                Setup.useMockServerForOnePlayer = true;
                Setup.useOldProtocolForServer = false;
                //switchToGameActivity();
                intent = new Intent(this, ActivityGame.class);
                bundle = intent.getExtras();
                break;
            case (1):
                intent = new Intent(this, ActivityServer.class);
                bundle = intent.getExtras();
                break;
            case (2):
                intent = new Intent(this, ActivityAllCharacters.class);
                bundle = intent.getExtras();
                break;
            case (3):
                intent = new Intent(this, ActivitySettings.class);
                bundle = intent.getExtras();
                break;
            case (4):
                intent = new Intent(this, ActivityRules.class);
                bundle = intent.getExtras();
                break;
            case (5):
                intent = new Intent(this, ActivityInformation.class);
                bundle = intent.getExtras();
                break;
        }
        saveStart();
        startActivity(intent);
    }

    private void loadStart() {
        System.out.println("Start sets: ");
        Setup.loadStart(this);
    }

    private void saveStart() {
//        // save
//        // All objects are from android.context.Context
//        //SharedPreferences settings = getSharedPreferences("width", 0);
//        SharedPreferences settings = getPreferences(0);
//        SharedPreferences.Editor editor = settings.edit();
//        //editor.commit();
//        editor.apply();
    }

    private static Resources resources;

    public static Resources getAppResources() {
        return resources;
    }

}
