package com.herrrau.crawlspace;

public class LocalAdapterMock extends LocalAdapter implements CentralSender
{
    private CentralAdapter adapter;
    private CentralController controller;

    @Override public void registerWithServer(String ip, int port) {
        int depth = 0;
        localView.setupLevel(Setup.getStandardCols(), Setup.getStandardRows(), 1, depth, false); // 1 player only
        adapter = new CentralAdapter(this);
        adapter.createAndSetup(Setup.getStandardCols(), Setup.getStandardRows(), 1);  // 1 player only
        controller = ((CentralAdapter)adapter).centralController;
    }

    @Override protected void sendToServer(String message) { adapter.receiveCommand(0, message); }
    @Override
    public void sendeAnAlle(String message) {
        this.receiveFromServer(message);
    }
    @Override public void sendeAnClient(int id, String message) {
        this.receiveFromServer(message);
    }


    //############### Umgehe der Kodierung -> schneller

    /*
    @Override public void tryMoveWest() {
        //((CentralAdapter)adapter).centralController.attemptMoveWest(0);
        controller.attemptMoveWest(0);
    }
    */
}