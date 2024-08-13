package com.herrrau.crawlspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityServer extends AppCompatActivity {

    static ServerMultiple server;
    Button buttonServerBack, buttonServerStart, buttonServerStop, buttonServerConnect, buttonServerCheck;
    TextView textViewServerStatus;
    TextView textViewServerData;
    EditText inputIP, inputPort, numberPlayers;

    //NumberPicker numberX, numberY, numberPlayers;

    static ActivityServer selfReference; //workaround for server class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        selfReference = this;

        buttonServerBack = findViewById(R.id.buttonServerBack);
        buttonServerBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Setup.saveServer(Integer.parseInt(inputPort.getText().toString()));
                finish();
            }
        });


        numberPlayers = findViewById(R.id.editTextServerNumberPlayers);
        numberPlayers.setText("1");

        textViewServerStatus = findViewById(R.id.textViewServerStatus);
        textViewServerData = findViewById(R.id.textViewServerInfo);

        buttonServerStart = findViewById(R.id.buttonServerStart);
        buttonServerStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //runLocalButton.setEnabled(false);
                Setup.useMockServerForOnePlayer = false; // #### necessary?
                startServer();
            }
        });

        buttonServerStop = findViewById(R.id.buttonServerStop);
        buttonServerStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopServer();
            }
        });
        buttonServerStop.setEnabled(false);


        buttonServerConnect = findViewById(R.id.buttonServerConnect);
        buttonServerConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Setup.useMockServerForOnePlayer = false;
                connectToServer();
            }
        });

        buttonServerCheck = findViewById(R.id.buttonServerStatus);
        buttonServerCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = "";
                if (ActivityServer.server == null) {
                    message = "Activity.server: null";
                } else {
                    message = ActivityServer.server.getNumberOfActiveClients() +
                            " active clients.";
                }
                textViewServerStatus.setText(message);
            }
        });


        inputPort = findViewById(R.id.editTextServerPort);
        inputIP = findViewById(R.id.editTextServerIP);

        inputIP.setText(Setup.serverIP);
        inputPort.setText("" + Setup.serverPortNumber);

        if (ActivityServer.server != null) {
            if (ActivityServer.server.serverAktiv) {
                buttonServerStart.setEnabled(false);
                buttonServerStop.setEnabled(true);
            } else {
                buttonServerStart.setEnabled(true);
                buttonServerStop.setEnabled(false);
            }
        } else {
            textViewServerStatus.setText("Activity.server is null");

        }

        loadServer();

        if (ActivityServer.server != null) {
            if (ActivityServer.server.getNumberOfActiveClients() == 0) { //############
                textViewServerStatus.setText("Server stopped. 0 clients.");
                buttonServerStop.setEnabled(false);
                buttonServerStart.setEnabled(true);
                AsyncTask<Void, Void, Void> temp = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            ActivityServer.server.stoppeServer(); // network activity on thread
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                temp.execute();
            }
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("AS START");
    }

    @Override
    public void onBackPressed() {
        stopServer();
        Setup.saveServer(Integer.parseInt(inputPort.getText().toString()));
        this.finish();
    }

    @Override
    public void onResume() {
        //wenn der Server-Android aber nicht mitspielt oder nicht als letzter geht, wird das
        //schliessen nie aufgerufen; es muesste also der SMC seinerseits das hier aufrufen...
        super.onResume();
        System.out.println("AS RESUME");
    }

    public void serverHasNoMoreClients() { //called by server
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (server != null) {
                    System.out.println("AS Server active: " + server.serverAktiv);
                    int i = server.getNumberOfActiveClients();
                    System.out.println("AS # of active clients: " + i);
                    if (i == 0) {
                        ActivityServer.selfReference.stopServer();
                    }
                }
            }
        }
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("AS PAUSE");
    }

    public void onStop() {
        super.onStop();
        System.out.println("AS STOP");
        //leaveGame(); // ist aber nicht nur beim Zurückblättern, auch wenn App in Hintergrund
    }

    public void onRestart() {
        super.onRestart();
        System.out.println("AS RESTART");
    }

    public void onDestroy() {
        super.onDestroy();
        System.out.println("AS DESTROY");
    }

    private void switchToGameActivity() {
        //remove keyboard focus, not always necessary
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(inputIP.getWindowToken(), 0);
        mgr.hideSoftInputFromWindow(inputPort.getWindowToken(), 0);

        Intent intent = new Intent(this, ActivityGame.class);
        startActivity(intent);
    }

    private void connectToServer() {
        Setup.serverPortNumber = Integer.parseInt(inputPort.getText().toString());
        Setup.serverIP = inputIP.getText().toString();
        Setup.useMockServerForOnePlayer = false;
        switchToGameActivity();
    }

    private void stopServer() {
        buttonServerStart.setEnabled(true);
        buttonServerStop.setEnabled(false);
        textViewServerData.setText("");
        //runLocalButton.setEnabled(true);
        if (ActivityServer.server != null) {
            AsyncTask<Void, Void, Void> temp = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        server.stoppeServer();
                        showTextSafely("Server stopped.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            temp.execute();
        }
    }


    private void startServer() {
        buttonServerStop.setEnabled(true);
        buttonServerStart.setEnabled(false);
        int numberOfPlayers = Integer.parseInt(numberPlayers.getText().toString());
        if (numberOfPlayers < 1) numberOfPlayers = 1;
        if (numberOfPlayers >= 4) numberOfPlayers = 4;
        int portNumber = Integer.parseInt(inputPort.getText().toString());
        Setup.serverPortNumber = portNumber;
        if (ActivityServer.server != null) {
            ActivityServer.server.stoppeServer();
        }
        ActivityServer.server = new ServerMultipleCrawlspace(numberOfPlayers, portNumber);
        ActivityServer.server.starteServer();

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        if (ipAddress.equals("0.0.0.0")) ipAddress = "127.0.0.1"; //"10.0.2.15";
        Setup.serverIP = ipAddress;

        textViewServerData.setText(Setup.serverIP + ":" + Setup.serverPortNumber);
        inputIP.setText(Setup.serverIP); //?

        buttonServerStart.setEnabled(false);
    }

    private void loadServer() {
        Setup.loadServer(this);
    }

    private void saveServer() {
        SharedPreferences settings = getSharedPreferences("xxx", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.apply();
    }

    public void showTextSafely(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String current = textViewServerStatus.getText().toString();
                if (current.length() < 200) {
                    textViewServerStatus.setText(current + "\n\n" + s);
                } else {
                    textViewServerStatus.setText(s);
                }
                textViewServerStatus.setText(current + "\n\n" + s);
            }
        });
    }


}