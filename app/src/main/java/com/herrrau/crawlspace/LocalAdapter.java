package com.herrrau.crawlspace;

public abstract class LocalAdapter implements LocalController {

    protected GameView localView;
    protected int playerID = 0;

    public abstract void registerWithServer(String ip, int port);

    protected abstract void sendToServer(String message);

    public void setView(GameView view) {
        this.localView = view;
    }

    public int getPlayerID() {
        return playerID;
    }

    // method calls FROM view

    public void tryTile(int position) {
        if (Setup.useOldProtocolForServer) {
            sendToServer("feldnummer:" + position);
        } else {
            sendToServer("tile:" + playerID + ":" + position);
        }
    }

    public void tryAction(ActionType action) {
        sendToServer("act:" + action.toString());
    }

    public void tryActionFight(ActionTypeFight action) {
        sendToServer("fight:" + action.toString());
    }

    public void tryDisplayStats() {
        sendToServer("stats");
    }

    public void tryItem(int typeNumber) {
        sendToServer("item:" + typeNumber);
    }
    public void tryMessage(String message) {
        sendToServer("message:" + message);
    }

    public void tryInteractWithObject() {
        sendToServer("obj");
    }

    public void tryMoveWest() {
        sendToServer("W");
    }

    public void tryMoveEast() {
        sendToServer("E");
    }

    public void tryMoveNorth() {
        sendToServer("N");
    }

    public void tryMoveSouth() {
        sendToServer("S");
    }

    public void tryStart() { sendToServer("begin"); }


    // messages from server translated FOR view


    private boolean verbose = true;

    public void receiveFromServer(String messageBody) {
        if (Setup.useOldProtocolForServer) {
            messageBody = OldProtocol.translateMessageFromServer(messageBody);
        }
        char intro = messageBody.charAt(0);
        if (intro == 't') { //"t:0:0111" -> show tile
            String[] data = messageBody.split(":");
            localView.showTile(Integer.parseInt(data[1]), data[2]);
        } else if (intro == 'c') { //"c:0:1:2" -> show character
            String[] data = messageBody.split(":");
            int id = Integer.parseInt(data[1]);
            int pos = Integer.parseInt(data[2]);
            int from = Integer.parseInt(data[3]);
            localView.showCharacter(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
        } else if (intro == 'o') { // o:82:CHEST oder o:0:TRAP -> object
            String[] data = messageBody.split(":");
            localView.showObject(Integer.parseInt(data[1]), PlaceableType.valueOf(data[2]));
        } else if (intro == 'r') { // r:82 -> remove object
            localView.removeObject(Integer.parseInt(messageBody.substring(2)));
        } else if (intro == 'n') { // n:5:3:4:1:true -> neuer Level 5 breit, 3 hoch, 4 players, depth 1
            String[] data = messageBody.split(":");
            localView.setupLevel(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), Boolean.parseBoolean(data[5]));
        } else if (intro == 's') { //s:CHEST#:Glorietta#12#10#0#0#1#2 -> show stats
            System.out.println("LA receives stats: "+messageBody);
            String[] data = messageBody.split(":", 3);
            if (data[1].equals("null#")) {
                localView.displayStats(playerID, data[2], null, "");
            } else {
                String [] placeableData = data[1].split("#", 2);
                //localView.displayStats(playerID, data[2], PlaceableType.valueOf(data[1]), "");
                localView.displayStats(playerID, data[2], PlaceableType.valueOf(placeableData[0]), placeableData[1]);

                //##############
                //##############
                //##############
                //##############
                //##############
                //##############



            }
        } else if (intro == 'f') { //f:MONSTER:Name#10:Glorietta:12:10:0:0:1:2 -> show opponent/fight
            String[] data = messageBody.split(":", 4);
            localView.displayOpponent(playerID, data[3], PlaceableType.valueOf(data[1]), data[2]);
        } else if (intro == 'g') { //g - show game
            localView.displayGame(playerID);
        } else if (intro == 'x') { //x:Message -> show exit
            localView.displayGameOver(playerID, messageBody.substring(2));
        } else if (intro == 'm') { //"message:mdngfgfdn"
            localView.displayFootnote(playerID, messageBody.substring(2));
        } else if (intro == 'M') { //"M:mdngfgfdn"
            localView.displayMessage(playerID, messageBody.substring(2));
        } else if (intro == 'i') { //"i:You found:CHEST:0#1#2#3#0#
            String[] data = messageBody.split(":", 4);
            localView.displayPlaceableType(playerID, data[1], PlaceableType.valueOf(data[2]), data[3] );
        } else if (intro == 'p') { //"p:ping" -> show sound
            String[] data = messageBody.split(":");
            localView.showSound(playerID, data[1]);
        } else if (intro == 'e') { //"e:1:82:EXPLOSION:argument" -> show effect
            String[] data = messageBody.split(":");
            localView.showEffect(Integer.parseInt(data[1]), Integer.parseInt(data[2]), EffectType.valueOf(data[3]), data[4]);
        } else if (intro == 'h') { //"h" -> show timer
            String[] data = messageBody.split(":");
            localView.displayLocalTimer(playerID, Integer.parseInt(data[1])); // new!
        } else if (intro == 'R') { //"R" -> remove character
            String[] data = messageBody.split(":");
            localView.removeCharacter(Integer.parseInt(data[1]));
        } else if (intro == 'H') { //"Hi, 0:2:6:7:0" bzw "Hi, 0" - id, numberofplayers, cols, rows, depth (fixed at 0)
            Setup.useOldProtocolForServer = messageBody.length() == 5;
            if (Setup.useOldProtocolForServer)
                messageBody = OldProtocol.translateMessageFromServer(messageBody);
            String[] data = messageBody.split(":");
            playerID = Integer.parseInt(messageBody.substring(4, 5));
            int players = Integer.parseInt(data[1]);
            int cols = Integer.parseInt(data[2]);
            int rows = Integer.parseInt(data[3]);
            int depth = Integer.parseInt(data[4]);
            //localView.setupLevel(cols, rows, players, depth);
            //will be called when all players are here
            localView.displayFootnote(playerID, playerID+1 + "/" + players+", waiting");
        } else if (intro == 'B') { //Bye!
            ((ActivityGame) localView).doLeaveGame();
        }
    }


}
