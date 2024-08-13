package com.herrrau.crawlspace;

public class CentralAdapter implements GameView {
    CentralController centralController;
    CentralSender sender;

    // Setup

    public CentralAdapter(CentralSender sender) {
        this.sender = sender;
        centralController = new CentralController();
    }

    public void createAndSetup(int columns, int rows, int numberOfPlayers) {
        centralController.createAndSetup(columns, rows, numberOfPlayers, this);
    }

    // Messages FROM central system

    //private void sendeAnAlle(String message) {
    //sender.sendeAnAlle(message);
    //}
    private void sendeAnClient(int id, String message) {
        sender.sendeAnClient(id, message);
    }

    public void displayFootnote(int id, String message) {
        sender.sendeAnClient(id, "m:"+message);
    }
    public void displayMessage(int id, String message) {
        sender.sendeAnClient(id, "M:"+message);
    }
    public void displayLocalTimer(int id, int seconds) {
        sender.sendeAnClient(id, "h:"+seconds);
    } // new!
    public static boolean timerIsRunning;

    public void showTile(int position, String tile) {
        sender.sendeAnAlle("t:"+position+":"+tile);
    }
    public void showCharacter(int characterID, int position, int comesFrom) {
        sender.sendeAnAlle("c:"+characterID+":"+position+":"+comesFrom);
    }
    public void showObject(int position, PlaceableType type) {
        sender.sendeAnAlle("o:"+position+":"+type);
    }
    public void showObjectTo(int playerID, int position, PlaceableType type) {
        sender.sendeAnClient(playerID, "o:"+position+":"+type);
    }
    public void removeObject(int position) {
        sender.sendeAnAlle("r:"+position);
    }
    public void removeCharacter(int id) {
        sender.sendeAnAlle("R:"+id);
    }

    public void displayGame(int id) {sender.sendeAnClient(id, "g");}
    public void displayPlaceableType(int playerID, String message, PlaceableType type, String objectStats) {
        sender.sendeAnClient(playerID, "i:"+message+":"+type+":"+objectStats);
    }
    public void displayStats(int playerID, String characterStats, PlaceableType placeableType, String placeableStats) {
        System.out.println("CA sends stats: "+characterStats+" and "+placeableStats);
        sender.sendeAnClient(playerID, "s:"+placeableType+"#"+placeableStats+":"+characterStats); // type can be null!
//#################
//#################
//#################
//#################
//#################
//#################
// #################

    }
    public void displayOpponent(int playerID, String characterStats,
                                PlaceableType opponentType, String opponentStats) {
        sender.sendeAnClient(playerID,
                "f:"+opponentType+":"+opponentStats+":"+characterStats);
        //diese reihenfolge, weil opponent stats fixe kurze laenge, waehrend player stats unbestimmt lang und mit
        //spaeter zu trennenden attributen
    }
    public void displayGameOver(int playerID, String message) {
        sender.sendeAnClient(playerID, "x:"+message);
    }
    public void showEffect(int playerID, int position, EffectType type, String argument) {
        sender.sendeAnAlle("e:"+playerID+":"+position+":"+type+":"+argument);
        //"e:1:82:explosion"
    }
    public void showSound(int playerID, String name) {
        //
    }
    public void setupLevel(int columns, int rows, int numberOfPlayers, int depth, boolean useLimitedVision) {
        sender.sendeAnAlle("n:"+columns+":"+rows+":"+numberOfPlayers+":"+depth+":"+useLimitedVision);
    }


    // Messages from local view FOR central system


    //synchronized?
    public void receiveCommand(int playerNumber, String message) {
        char intro = message.charAt(0);
        if (intro=='N') {
            centralController.attemptMoveNorth(playerNumber);
        }
        else if (intro=='E') {
            centralController.attemptMoveEast(playerNumber);
        }
        else if (intro=='S') {
            centralController.attemptMoveSouth(playerNumber);
        }
        else if (intro=='W') {
            centralController.attemptMoveWest(playerNumber);
        }
        else if (intro=='s') { //stats
            //centralController.displayStats(playerNumber, Integer.parseInt(message.substring(6)));
            centralController.attemptDisplayStats(playerNumber);
        }
        else if (intro=='m') { //message:kjfhkgdhkahga)
            String [] data = message.split(":");
            centralController.attemptShowMessage(playerNumber, data[1]);
        }
        else if (intro=='t') { //t:0:1 (player wants to go from 0 to 1)
            String [] data = message.split(":");
            centralController.attemptSelectTile(playerNumber, Integer.parseInt(data[2]));
        }
        else if (intro=='o') { //object
            centralController.attemptInteractWithObject(playerNumber);
        }
        else if (intro=='i') { //item:type number //##########################
            ItemType type  = ItemType.values()[Integer.parseInt(message.substring(5))];
            centralController.attemptUseItem(playerNumber, type);
        }
        else if (message.equals("exit")) {
            centralController.attemptRemoveCharacter(playerNumber);
        }
        else if (intro=='a') { //act:DIG
            centralController.attemptAction(playerNumber, ActionType.valueOf(message.substring(4)));
        }
        else if (intro=='f') { //fight:FLEE
            centralController.attemptActionFight(playerNumber, ActionTypeFight.valueOf(message.substring(6)));
        }
        else if (message.equals("begin")) { //tell server that level has started
            centralController.hasStarted(playerNumber);
        }
    }
}
