package com.herrrau.crawlspace;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CentralController {
    private GameView view;
    private CentralModel model;
    private DelegatedFight delegatedFight;
    private DelegatedUseItem delegatedUseItem;
    private DelegatedAction delegatedAction;
    private DelegatedObjectInteraction delegatedObjectInteraction;
    private DelegatedObjectEncounter delegatedObjectEncounter;
    DelegatedConsequences delegatedConsequences;
    DelegatedMapTraversal delegatedMapTraversal;

    public CentralController() {
    }

    public GameView getView() {
        return view;
    }

    public void createAndSetup(int columns, int rows, int numberOfPlayers, GameView view) {
        this.view = view;
        model = new CentralModel(columns, rows, numberOfPlayers);
        model.setView(this.view);
        model.setup(0);
        delegatedFight = new DelegatedFight(model, view, this);
        delegatedUseItem = new DelegatedUseItem(model, view, this);
        delegatedObjectInteraction = new DelegatedObjectInteraction(model, view, this);
        delegatedObjectEncounter = new DelegatedObjectEncounter(model, view, this);
        delegatedAction = new DelegatedAction(model, view, this);
        delegatedConsequences = new DelegatedConsequences(model, view, this);
        delegatedMapTraversal = new DelegatedMapTraversal(model, view, this);
    }


    /* Messages FROM (Local) view */

    public void attemptSelectTile(int playerID, int position) {
        int start = model.getCharacter(playerID).getPosition();
        attemptMove(playerID, position, start);
    }

    public void attemptAction(int playerID, ActionType type) {
        delegatedAction.attemptAction(playerID, type);
    }

    public void attemptActionFight(int playerID, ActionTypeFight type) {
        delegatedFight.attemptActionFight(playerID, type);
    }

    void dealWithDisappearedObject(int playerID) {
        System.out.println("CC disappearing object... calls SHOW GAME");
        view.displayFootnote(playerID, "IT'S GONE");
        view.displayGame(playerID);
    }

    public void attemptUseItem(int playerID, ItemType type) {
        delegatedUseItem.attemptUseItem(playerID, type);
        tempUseItemAndAttemptMove(playerID);
    }

    public void attemptInteractWithObject(int playerID) {
        delegatedObjectInteraction.attemptInteractWithObject(playerID);
    }


    public void attemptRemoveCharacter(int id) {
        System.out.println("CC attempting to remove character id " + id);
        model.getCharacter(id).isActive = false;
        view.removeCharacter(id);
    }

    public void attemptShowMessage(int id, String message) {
        view.displayMessage(id, message);
    }

    public void attemptMoveNorth(int playerID) {
        int fromPosition = model.getCharacter(playerID).getPosition();
        int toPosition = fromPosition - model.columns;
        attemptMove(playerID, toPosition, fromPosition);
    }

    public void attemptMoveEast(int playerID) {
        int fromPosition = model.getCharacter(playerID).getPosition();
        int toPosition = fromPosition + 1;
        attemptMove(playerID, toPosition, fromPosition);
    }

    public void attemptMoveSouth(int playerID) {
        int fromPosition = model.getCharacter(playerID).getPosition();
        int toPosition = fromPosition + model.columns;
        attemptMove(playerID, toPosition, fromPosition);
    }

    public void attemptMoveWest(int playerID) {
        int fromPosition = model.getCharacter(playerID).getPosition();
        int toPosition = fromPosition - 1;
        attemptMove(playerID, toPosition, fromPosition);
    }

    public void attemptDisplayStats(int playerID) {
        model.sendStats(playerID);
    }

    public void hasStarted(int playerID) {
        model.levels.startTimer();
    }

    /* Making the move */

    void attemptMove(int playerID, int toPosition, int fromPosition) {
        if (model.isLegalMove(playerID, fromPosition, toPosition)) {
            if (model.isAttemptedLegalMoveSuccessful(playerID, fromPosition, toPosition)) {
                //sendTextMessageTo(playerID,"Move successful (from " + toPosition + " to " + fromPosition + ")");
                model.makeMoveAndSend(playerID, toPosition);
                checkForObjectEncounter(playerID, toPosition); //evetyhing else is in model...
                tempUseItemAndAttemptMove(playerID);
            } else {
                //sendTextMessageTo(playerID, "Unsuccessful attempt (from " + fromPosition + " to " + toPosition + ")");
            }
        } else {
            //sendTextMessageTo(playerID, "Illegal move (from " + fromPosition + " to " + toPosition + ")");
        }
    }


    public void checkForObjectEncounter(int playerID, int position) {
        Placeable obj = model.getPlaceableObjectAt(position);
        if (obj != null) {
            delegatedObjectEncounter.dealWithObjectEncounter(playerID, position, obj);
        }
    }

    /* Timer */

    Timer timer = new Timer();

    ScheduledExecutorService executorService = Executors
            .newSingleThreadScheduledExecutor();

    public void startTimer(Runnable runnable, long delay) {
        if (true) {
            executorService.schedule(runnable, delay, TimeUnit.MILLISECONDS);
        } else {
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    (new Thread(runnable)).start();
                }
            };
            //timer.scheduleAtFixedRate(timerTask, 0, 1000); // every 1 seconds.
            timer.schedule(timerTask, delay);
        }
    }

    /* Map and exits */


    public void showMap(int playerID, int startingPosition) {
        delegatedObjectEncounter.avoidAutomaticEncounters = true;
        delegatedMapTraversal.walkMap(playerID, startingPosition);
        delegatedObjectEncounter.avoidAutomaticEncounters = false;
    }

    boolean isReachableForAllOtherPlayers(int playerID) {
        return delegatedMapTraversal.isReachableForAllOtherPlayers(playerID);
    }

    public int countOpenEnds(int playerID) {
        int i = delegatedMapTraversal.countOpenEnds(playerID);
        return i;
    }

    public void tempUseItemAndAttemptMove(int playerID) {
        int pos = model.getCharacter(playerID).getPosition();
        int i0 = delegatedMapTraversal.countOpenEndsFromPosition(pos);
        int i1 = delegatedMapTraversal.countOpenEndsFromPositionWithRotatorsCountingFour(pos);
        int i2 = delegatedMapTraversal.countAllOpenEndsEverywhere();
        int i3 = delegatedMapTraversal.countAllOpenEndsEverywhereWithRotatorsAsFour();
        if (model.wayDownHasAppeared()) {
            boolean b = delegatedMapTraversal.calculateReachable(model.getCharacter(playerID).getPosition(), model.wayDownPosition);
            view.displayFootnote(playerID, "Open: "+i0+"/"+i1+"/"+i2+"/"+i3+" - exit: "+b);
        } else {
            boolean b = delegatedMapTraversal.isReachable(0, model.columns*model.rows-1);
            view.displayFootnote(playerID, "Open: "+i0+"/"+i1+"/"+i2+"/"+i3+" - corner: "+b);
        }

    }
}



