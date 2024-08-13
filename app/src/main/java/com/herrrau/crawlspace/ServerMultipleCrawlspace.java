package com.herrrau.crawlspace;

public class ServerMultipleCrawlspace extends ServerMultiple implements CentralSender {
    private int cols, rows, numberOfPlayers;
    private CentralAdapter adapter;

    public ServerMultipleCrawlspace(int numberOfPlayers, int port) {
        super(numberOfPlayers);
        this.numberOfPlayers = numberOfPlayers;
        this.cols = Setup.getStandardCols();
        this.rows = Setup.getStandardRows();
        this.portNumber = port;
    }

    @Override
    protected void verarbeiteAnmeldung(int id) {
        String answer = "Hi, " + anzahlAktuell; // original protocol
        answer = answer + ":" +
                this.numberOfPlayers + ":" +
                this.cols + ":" +
                this.rows + ":" +
                "0"; // depth
        System.out.println("Sende an client: "+answer);
        sendeAnClient(anzahlAktuell, answer); // "Hi, 0:4:6:7:0"
        if (anzahlAktuell == numberOfPlayers - 1) {
            adapter = new CentralAdapter(this);
            adapter.createAndSetup(this.cols, this.rows, numberOfPlayers);
        }
    }

    @Override
    protected void verarbeiteAbmeldung(int id) {
        super.verarbeiteAbmeldung(id);
        adapter.receiveCommand(id, "exit"); // macht der das dann doppelt?
        if (getNumberOfActiveClients()==0) {
            ActivityServer.selfReference.serverHasNoMoreClients();
        }

    }

    @Override
    public void empfangeVonClient(int nr, String message) {
        adapter.receiveCommand(nr, message);
    }

    @Override protected void print(String s) {
        if (verbose) {
            System.out.println(s);
            ActivityServer.selfReference.showTextSafely(s);
        }
    }

}
