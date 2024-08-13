package com.herrrau.crawlspace;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalAdapterRemote extends LocalAdapter {
    private final LocalAdapterRemote clientAdapterRemoteSelfReference; //workaround
    Client client;

    public LocalAdapterRemote() {
        clientAdapterRemoteSelfReference = this; // um als static unten verwendet werden zu können
    }

    // forward messages to server
    // ONLY communication in this direction

    public void registerWithServer(String ip, int port) {
        if (true) {
            registerWithServerXXX(ip, port);
            return;
        }
        //localView.displayFootnote(0,"LAR Trying to connect with server...");
        System.out.println("LAR Trying to connect with server...");
        //hier erfolgt die Anmeldung am Server
        AsyncTask<Void, Void, Void> temp = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    System.out.println("LAR trying: " + Setup.serverIP + " " + Setup.serverPortNumber);
                    localView.displayFootnote(playerID, Setup.serverIP + " " + Setup.serverPortNumber);
                    client = new ClientCrawlspace(Setup.serverIP, Setup.serverPortNumber, clientAdapterRemoteSelfReference);
                    client.starteClient();
                    localView.displayFootnote(0, "LAR starts client");
                    System.out.println("LAR done!"); // only ever reached when successful!
                } catch (Exception e) {
                    System.out.println("LAR Fehler!");
                    e.printStackTrace();
                    localView.displayFootnote(0, "LAR Fehler!");
                    System.out.println("LAR done?"); // never reached
                }
                System.out.println("LAR done.");
                return null;
            }
        };
        temp.execute();
    }

    public void registerWithServerXXX(String ip, int port) {
        //localView.displayFootnote(0,"LAR Trying to connect with server...");
        System.out.println("LAR Trying to connect with server...");
        //hier erfolgt die Anmeldung am Server
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("LAR trying: " + Setup.serverIP + " " + Setup.serverPortNumber);
                    localView.displayFootnote(playerID, Setup.serverIP + " " + Setup.serverPortNumber);
                    client = new ClientCrawlspace(Setup.serverIP, Setup.serverPortNumber, clientAdapterRemoteSelfReference);
                    client.starteClient();
                    localView.displayFootnote(0, "LAR starts client");
                    System.out.println("LAR done."); // only ever reached when successful!
                } catch (Exception e) {
                    System.out.println("LAR Fehler!");
                    e.printStackTrace();
                    localView.displayFootnote(0, "LAR Fehler!");
                    System.out.println("LAR done?"); // never reached
                }
                System.out.println("LAR done!!!");

            }
        });
        thread.start();
    }


    protected void sendToServer(String message) {
        AsyncTask<Void, Void, Void> temp = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    client.sendeAnServer(message);
                    //print("LA trying to send to server (2): " + message);
                } catch (Exception e) {
                    if (client.socket == null) {
                        System.out.println("LocalAdapterRemote Error, client socket was null anyway. (Just go on.)");
                    } else {
                        System.out.println("LocalAdapterRemote Error, trying to close socket manually...");
                        e.printStackTrace();
                        try {
                            client.socket.close();
                            System.out.println("LocalAdapterRemote ...closed socket manually worked!");
                        } catch (Exception f) {
                            System.out.println(f);
                            System.out.println("LocalAdapterRemote ...closing socket did not work properly.");
                        }
                    }
                }
                return null;
            }

        };
        temp.execute();
    }


}