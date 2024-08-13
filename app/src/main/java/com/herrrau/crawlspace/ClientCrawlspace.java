package com.herrrau.crawlspace;

public class ClientCrawlspace extends Client {

    private LocalAdapterRemote adapter;

    public ClientCrawlspace(String serverIP, int port, LocalAdapterRemote adapter) {
        super(serverIP, port);
        this.adapter = adapter;
    }

    protected void empfangeVonServer(String s) {
        print("ClientCrawlspace receives from server: " + s);
        adapter.receiveFromServer(s);
    }

    public void sendeAnServer(String s) {
        print("ClientCrawlspace trying to send: " + s);
        super.sendeAnServer(s); //NetworkOnMainThreadException
    }

    private boolean verbose = false;

    private void print(String s) {
        if (verbose) {
            System.out.println(s);
        }
    }
}

