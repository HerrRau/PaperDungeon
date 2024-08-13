package com.herrrau.crawlspace;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class DelegatedMapTraversal {
    CentralModel model;
    GameView view;
    CentralController controller;

    public DelegatedMapTraversal(CentralModel model, GameView view, CentralController controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    // ########################################################
    // Walk map
    // ########################################################

    public void walkMap(int playerID, int start) {
        int type = 1;
        switch (type) {
            case 0: { //kann sein, dass exit nicht erscheint, je nach Art des Zählens der
                //open exits; oder es werden nicht alle rotator-Öffnungen geschlossen
                // das müsste man wohl manuell machen
                //model.turnOffRotation = true;
                walkMapWithoutRotators(start);
            }
            break;
            case 1: { // -> fkt, weiß aber nicht mehr ob mit oder ohne rot. besset
                //model.turnOffRotation = true;
                walkMapWithRotators(start);
            }
            break;
            case 2: { // -> fkt NICHT
                //model.turnOffRotation = true;
                walkMapBreadthFirstRecursiveWithoutRotators(start);
            }
            break;
        }

        // what happens afterwards...
        if (!Setup.strictMode) {
            if (model.wayDownHasAppeared()) {
                //jump to way down
                model.makeMoveAndSend(playerID, model.wayDownPosition);
            } else {
                //jump to beginning
                model.makeMoveAndSend(playerID, start);
            }
        } else {
            //jump to beginning
            model.makeMoveAndSend(playerID, start);
        }
        model.turnOffRotation = false;

        // untested ##########################
        for (int i = 0; i < 3; i++) {
            System.out.println("Checking rotation: " + i);
            if (!isReachable(playerID, model.wayDownPosition)) {
                model.rotateAllRotatorsAndSend();
                System.out.println("Rotating: " + i);
                view.displayFootnote(playerID, "rotating: " + i);
            } else {
                break;
            }
        }
        // ###################################

        view.displayGame(playerID);
    }

    void walkMapDepthFirstOnlyOnUnset(int startingPosition) {
        //System.out.println("Pos "+startingPosition+" has "+neighbours.size()+" neighbours:");
        for (int neighbour : model.getAllLegalNeighbourPositions(startingPosition)) {
            //System.out.println(startingPosition+" checks neighbour "+neighbour);
            if (model.tiles[neighbour] == null) {
                //System.out.println(" is null");
                controller.attemptMove(0, neighbour, startingPosition);
                //besser: make move and dont send? oder wird eh nur neues tile gesendet?
                // dann bleibt immer noch character sending
                //System.out.println(" is now: "+model.tiles[neighbour].getName()+", continuing from here");
                walkMapDepthFirstOnlyOnUnset(neighbour);
                this.model.makeMoveAndSend(0, startingPosition);
            }
        }
    }

    private void walkMapBreadthFirstWithoutRotators(int start) {
        Deque<Integer> nodes = new LinkedList<>();
        nodes.addFirst(start);
        while (nodes.size() > 0) {
            int neu = nodes.removeLast();
            for (int nachbar : model.getAllLegalNeighbourPositions(neu)) {
                if (model.tiles[nachbar] == null) {
                    model.getCharacter(0).setPosition(neu);
                    controller.attemptMove(0, nachbar, neu);
                    nodes.addFirst(nachbar);
                }
            }
        }
    }

    private void walkMapBreadthFirstRecursiveWithoutRotators(int start) {
        Deque<Integer> nodes = new LinkedList<>();
        boolean[] discovered = new boolean[model.columns * model.rows];
        nodes.addFirst(start);
        walkMapBreadthFirstRecursiveWithoutRotators(nodes, discovered);
    }

    private void walkMapBreadthFirstRecursiveWithoutRotators(Deque<Integer> q, boolean[] discovered) {
        if (q.size() == 0) return;
        int steps = 0;
        int next = q.poll();
        for (int neighbour : model.getAllLegalNeighbourPositions(next)) {
            if (!discovered[neighbour]) {
                model.getCharacter(0).setComesFrom(next); //doesnt work as intended
                discovered[neighbour] = true;
                q.addFirst(neighbour);
                //model.makeMoveAndSend(0, neighbour);
                model.makeMoveAndSendTilesOnly(0, neighbour);
                //controller.attemptMove(0, next, neighbour);
                steps++;
            }
        }
        if (true) {
            for (int i = 0; i < steps % 4; i++) {
                if (i < steps % 4 - 1) model.rotateAllRotators();
                else model.rotateAllRotatorsAndSend();
            }
        }

        walkMapBreadthFirstRecursiveWithoutRotators(q, discovered);
    }

    private void walkMapWithoutRotators(int start) { //breadth first
        int steps = 0;

        //assumes rotators never change -> can make goal unreachable
        boolean showOnlyNullTilesFromCurrentPosition = false; // was heisst das????????????????????????????
        boolean[] visited = new boolean[model.columns * model.rows];
        Deque<Integer> nodes = new LinkedList<>();
        nodes.addFirst(start);
        visited[start] = true;
        while (nodes.size() > 0) {
            //int neu = nodes.removeLast();
            int neu = nodes.removeFirst();
            for (int nachbar : model.getAllLegalNeighbourPositions(neu)) {
                if (!showOnlyNullTilesFromCurrentPosition) {
                    if (!visited[nachbar]) {
                        visited[nachbar] = true;
                        model.getCharacter(0).setPosition(neu);
                        model.makeMoveAndSendTilesOnly(0, nachbar);
                        steps++;
                        nodes.addFirst(nachbar);
                    }
                } else {
                    if (model.tiles[nachbar] == null) {
                        model.getCharacter(0).setPosition(neu);
                        model.makeMoveAndSendTilesOnly(0, nachbar);
                        steps++;
                        nodes.addFirst(nachbar);
                    }
                }
            }
        }
        System.out.println("Map: steps%4: " + steps % 4);
    }

    private void walkMapWithRotators(int start) {
        int comesFrom = model.getCharacter(0).getComesFrom();
        int isAt = model.getCharacter(0).getPosition();
        //breadth first
        boolean[] visited = new boolean[model.columns * model.rows];
        Deque<Integer> nodes = new LinkedList<>();
        nodes.addFirst(start);
        visited[start] = true;
        while (nodes.size() > 0) {
            int neu = nodes.removeLast();
            //System.out.println("Map at "+neu);
            for (Direction dir : Direction.values()) {
                //System.out.println("Checking towards "+dir);
                if (model.tiles[neu].isOpenInDirection(dir) || model.tiles[neu].getSpecialFunction() == Tile.ROTATOR_ANTICLOCKWISE || model.tiles[neu].getSpecialFunction() == Tile.ROTATOR_CLOCKWISE) {
                    //System.out.println(" "+neu+" is open towards "+dir+" (or rotator)");
                    int nachbar = model.getPositionInDirection(dir, neu);
                    if (nachbar == CentralModel.OUT_OF_BOUNDS) continue;
                    if (!visited[nachbar]) {
                        //System.out.println(" "+nachbar+" is unvisited");
                        visited[nachbar] = true;
                        model.getCharacter(0).setPosition(neu); //unnoetig?
                        //System.out.println(" character comes from "+model.getCharacter(0).getComesFrom());
                        model.makeMoveAndSendTilesOnly(0, nachbar);//alternativ: model.makeMoveAndDontSend - was soll mit Objekten geschehen?
                        nodes.addFirst(nachbar);
                    }
                } else {
                    //System.out.println(" "+neu+" is not open towards "+dir);
                }
            }
        }
        model.getCharacter(0).setPosition(isAt);
        model.getCharacter(0).setComesFrom(comesFrom);
    }

    // ########################################################
    // Count open ends
    // ########################################################

    // do not take into account hidden tiles!

    public int countOpenEnds(int playerID) {
        int selector = 1;
        switch (selector) {
            case 0:
                // hidden tiles not yet tested
                return countOpenEndsFromPosition(model.getCharacter(playerID).getPosition());
            case 1:
                //includes hidden tiles
                return countOpenEndsFromPositionWithRotatorsCountingFour(model.getCharacter(playerID).getPosition());
            case 2:
                // hidden tiles not yet tested
                return countAllOpenEndsEverywhere(); //rotators do not count as four here
            case 3:
                // hidden tiles not yet tested
                return countAllOpenEndsEverywhereWithRotatorsAsFour();
        }
        return -1; // shouldn't happen
    }

    int countOpenEndsFromPosition(int startingPosition) {
        //breadth first
        int numberOfExits = 0;
        boolean[] visited = new boolean[model.columns * model.rows];
        Deque<Integer> nodes = new LinkedList<>();
        nodes.addFirst(startingPosition);
        visited[startingPosition] = true;
        while (nodes.size() > 0) {
            int neu = nodes.removeLast();
            ArrayList<Integer> continueWith;
            continueWith = model.getAllLegalNeighbourPositions(neu);
            for (int nachbar : continueWith) {
                if (model.isUnsetOrHidden(nachbar)) {
                    //if (model.isUnset(nachbar)) {
                    numberOfExits++;
                    continue;
                }
                if (!visited[nachbar]) {
                    visited[nachbar] = true;
                    nodes.addFirst(nachbar);
                }
            }
        }
        return numberOfExits;
    }


    int countOpenEndsFromPositionWithRotatorsCountingFour(int startingPosition) {
        //breadth first
        int numberOfExits = 0;
        boolean[] visited = new boolean[model.columns * model.rows];
        Deque<Integer> nodes = new LinkedList<>();
        nodes.addFirst(startingPosition);
        visited[startingPosition] = true;
        int current = startingPosition;
        while (nodes.size() > 0) {
            int neu = nodes.removeLast();
            ArrayList<Integer> continueWith;
            for (Direction dir : Direction.values()) {
                int i = model.getPositionInDirection(dir, neu);
                if (i == model.OUT_OF_BOUNDS) continue;

                if (model.tiles[neu].isOpenInDirection(dir)) {
                    //####################################################
                    if (model.tiles[i] == null || model.tiles[i].isHidden()) {
                        //if (model.tiles[i] == null) {
                        numberOfExits++;
                        continue;
                    } else if (model.tiles[i].isOpenInDirection(dir.getOpposite())) {
                        //add
                    } else if (model.tiles[i].getSpecialFunction() == Tile.ROTATOR_CLOCKWISE || model.tiles[i].getSpecialFunction() == Tile.ROTATOR_ANTICLOCKWISE) {
                        //add
                    } else {
                        continue;
                    }
                } else {
                    if (model.tiles[neu].getSpecialFunction() == Tile.ROTATOR_CLOCKWISE || model.tiles[neu].getSpecialFunction() == Tile.ROTATOR_ANTICLOCKWISE) {
                        if (model.tiles[i] == null) {
                            numberOfExits++;
                            continue;
                        } else if (model.tiles[i].isOpenInDirection(dir.getOpposite())) {
                            //add
                        }
                    } else {
                        continue;
                    }

                }
                if (!visited[i]) {
                    visited[i] = true;
                    nodes.addFirst(i);
                }

            }
        }
        return numberOfExits;
    }

    int countAllOpenEndsEverywhere() {
        int result = 0;
        for (int i = 0; i < model.tiles.length; i++) {
            if (model.tiles[i] == null) continue;
            for (Direction dir : Direction.values()) {
                if (model.isUnsetOrHidden(model.getPositionInDirection(dir, i)) && model.tiles[i].isOpenInDirection(dir)) {
                    //if (model.isUnset(model.getPositionInDirection(dir, i)) && model.tiles[i].isOpenInDirection(dir)) {
                    result++;
                }
                // NEW, NEEDS MORE TESTS
                else if (model.isHidden(model.getPositionInDirection(dir, i)) && model.tiles[i].isOpenInDirection(dir)) {
                    result++;
                }

            }
        }
        return result;
    }

    int countAllOpenEndsEverywhereWithRotatorsAsFour() {
        int result = 0;
        for (int i = 0; i < model.tiles.length; i++) {
            if (model.tiles[i] == null) continue;
            for (Direction dir : Direction.values()) {
                if (model.isUnsetOrHidden(model.getPositionInDirection(dir, i)) || model.isHidden(model.getPositionInDirection(dir, i))) {
                    //if (model.isUnset(model.getPositionInDirection(dir, i)) || model.isHidden(model.getPositionInDirection(dir, i))) {
                    if (model.tiles[i].isOpenInDirection(dir)) {
                        result++;
                    } else if (model.tiles[i].getSpecialFunction() == Tile.ROTATOR_CLOCKWISE || model.tiles[i].getSpecialFunction() == Tile.ROTATOR_ANTICLOCKWISE) {
                        result++;
                    }
                }
                System.out.println();
            }
        }
        return result;
    }

    // ########################################################
    // Is reachable position
    // ########################################################

    public boolean isReachableForAllOtherPlayers(int playerID) {
        for (int i = 0; i < model.characters.length; i++) {
            if (i != playerID && model.characters[i].isActive && !isReachable(playerID, model.characters[i].getPosition())) {
                return false;
            }
        }
        return true;
    }

    private boolean[] isVisited;

    boolean isReachable(int playerID, int goalPosition) {
        int type = 1;
        switch (type) {
            case 0: //works, as far as it goes
                return isReachableCurrently(playerID, goalPosition);
            case 1: //works
                return isReachableWithRotators(playerID, goalPosition);
        }
        return false;
    }

    private boolean isReachableCurrently(int playerID, int goalPosition) {
        isVisited = new boolean[model.columns * model.rows];
        for (int i = 0; i < isVisited.length; i++) {
            isVisited[i] = false;
        }
        int startingPosition = model.getCharacter(playerID).getPosition();
        Deque<Integer> nodes = new LinkedList<>();
        isVisited[startingPosition] = true;
        nodes.push(startingPosition);
        while (nodes.size() > 0) {
            int next = nodes.pop();
            for (int neighbour : model.getAllLegalNeighbourPositions(next)) {
                if (neighbour == goalPosition) return true;
                if (!isVisited[neighbour]) {
                    isVisited[neighbour] = true;
                    nodes.addFirst(neighbour);
                }
            }
        }
        return false;
    }

    private boolean isReachableWithRotators(int playerID, int goalPosition) {
        isVisited = new boolean[model.columns * model.rows];
        for (int i = 0; i < isVisited.length; i++) {
            isVisited[i] = false;
        }
        int startingPosition = model.getCharacter(playerID).getPosition();
        Deque<Integer> nodes = new LinkedList<>();
        isVisited[startingPosition] = true;
        nodes.push(startingPosition);
        while (nodes.size() > 0) {
            int next = nodes.pop();
            for (int neighbour : model.getAllPOTENTIALLYLegalNeighbourPositions(next)) {
                if (neighbour == goalPosition) return true;
                if (!isVisited[neighbour]) {
                    isVisited[neighbour] = true;
                    nodes.addFirst(neighbour);
                }
            }
        }
        return false;
    }

    //######################################
    // is reachable state
    //######################################

    boolean[][] isReachable;
    int reachableState;
    boolean hasChanged;

    boolean calculateReachable(int startPosition, int goalPosition) {
        reachableState = 0; //new, untested
        int numberOfRotations = 0;
        hasChanged = false;
        isReachable = new boolean[4][model.columns * model.rows];
        markNeighboursReachable(startPosition, reachableState);
        while (hasChanged) {
            hasChanged = false;
            model.rotateAllRotators();
            numberOfRotations++;
            for (int i = 0; i < model.tiles.length; i++) {
                if (isReachable[(reachableState)][i]) {
                    markNeighboursReachable(i, (reachableState + 1) % 4);
                }
            }
            reachableState = (reachableState + 1) % 4;
        }

        //return to original state
        if (numberOfRotations % 4 == 1) {
            model.rotateAllRotators();
            model.rotateAllRotators();
            model.rotateAllRotators();
        } else if (numberOfRotations % 4 == 2) {
            model.rotateAllRotators();
            model.rotateAllRotators();
        } else if (numberOfRotations % 4 == 3) {
            model.rotateAllRotators();
        }

        //calculate
        int distanceHorizontal = Math.abs(model.getX(startPosition) - model.getX(goalPosition));
        int distanceVertical = Math.abs(model.getY(startPosition) - model.getY(goalPosition));
        boolean isDistanceEven = distanceHorizontal % 2 == distanceVertical % 2;

        //print
        for (int i = 0; i < 4; i++) {
            if (isDistanceEven && i % 2 == 0) continue;
            if (!isDistanceEven && i % 2 != 0) continue;
            boolean printEverything = false;
            if (printEverything) {
                for (int y = 0; y < model.rows; y++) {
                    for (int x = 0; x < model.columns; x++) {
                        boolean b = isReachable[i][y * model.columns + x];
                        if (b) System.out.print("T");
                        else System.out.print("F");
                    }
                    System.out.println();
                }
            }
        }


        //result
        return isReachable[0][goalPosition] || isReachable[2][goalPosition] || isReachable[1][goalPosition] || isReachable[3][goalPosition];

//        if (!isDistanceEven) {
//            return isReachable[0][goalPosition] || isReachable[2][goalPosition];
//        } else {
//            return isReachable[1][goalPosition] || isReachable[3][goalPosition];
//        }

    }

    void markNeighboursReachable(int position, int state) {
        for (int neighbour : model.getAllLegalNeighbourPositions(position)) {
            //System.out.println("Checking out neighbour "+neighbour+" in state "+state);
            if (!isReachable[state][neighbour]) {
                //System.out.println("Marked reachable in state " + state + ": " + neighbour);
                isReachable[state][neighbour] = true;
                hasChanged = true;
            }
        }

    }


}
