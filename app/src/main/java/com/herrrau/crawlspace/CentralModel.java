package com.herrrau.crawlspace;

import java.util.ArrayList;

public class CentralModel {

    int rows, columns, rowsAtBeginning, columnsAtBeginning;
    Tile[] tiles;
    Character[] characters;
    private GameView view;
    DelegatedLevels levels; //make private, used by central adapter new!
    private int currentDepth = 0;
    int wayDownPosition = OUT_OF_BOUNDS;
    double chanceOfChest = 0.5;
    double chanceOfOpponent = 0;
    double chanceOfOpponentInChest = 0;
    double chanceOfRotator = 0.5;
    double chanceOfSpontaneousRotation = 0;
    private int numberOfOpenEnds;
    static final int OUT_OF_BOUNDS = Integer.MAX_VALUE;
    boolean closeExitsOnBorders = true;
    boolean useManualCountingOfOpenEnds = false; //true won't work in mutliplayer, false works somewhat
    boolean turnOffRotation = false;

    /* Constructors and setup */

    public CentralModel(int columns, int rows, int numberOfPlayers) {
        // helper instance
        levels = new DelegatedLevels(this);
        // characters
        characters = new Character[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            characters[i] = new Character();
        }
        //################################################################ nicht fuer 0, fuer alle
        characters[0] = Setup.activeCharacter;
        //################################################################ nicht fuer 0, fuer alle
        // columns and rows
        this.columns = columns;
        this.rows = rows;
        this.columnsAtBeginning = columns;
        this.rowsAtBeginning = rows;
        // other fields initialised by setup
    }

    public void setup(int level) {
        wayDownPosition = OUT_OF_BOUNDS;
        numberOfOpenEnds = characters.length * 2;
        //delegated to levels
        levels.setupLevel(level);
    }

    /* Setters and getters */

    public void setView(GameView v) {
        view = v;
    }

    public GameView getView() {
        return view;
    }

    public boolean wayDownHasAppeared() {
        return wayDownPosition != OUT_OF_BOUNDS;
    }

    public int getNumberOfOpenEnds(int playerID) {
        if (useManualCountingOfOpenEnds) {
            //add only new or changed tiles
            return numberOfOpenEnds;
        } else return ((CentralAdapter) view).centralController.countOpenEnds(playerID);
    }

    public void increaseNumberOfOpenEnds(int i) {
        numberOfOpenEnds += i;
    }

    public Character getCharacter(int id) {
        return characters[id];
    }

    public Placeable getPlaceableObjectAt(int position) {
        return tiles[position].getObject(); // doesnt check for null tile
    }

    public int getCurrentDepth() {
        return currentDepth;
    }

    public void setCurrentDepth(int currentDepth) {
        this.currentDepth = currentDepth;
    }

    public int getRandomLegalNeighbourPosition(int position) {
        ArrayList<Integer> neighbours = getAllLegalNeighbourPositions(position);
        if (neighbours.size() > 0) {
            return neighbours.get((int) (Math.random() * neighbours.size()));
        } else return position;//OUT_OF_BOUNDS; //if there are no neighbours, stay where you are
    }

    public ArrayList<Integer> getAllLegalNeighbourPositions(int position) {
        ArrayList<Integer> neighbours = new ArrayList<>(4);
        for (Direction dir : Direction.values()) {
            int newPos = getPositionInDirection(dir, position);
            int hardcodedPlayerID = 0;
            if (isLegalMove(hardcodedPlayerID, position, newPos)) neighbours.add(newPos);
        }
        return neighbours;
    }

    public ArrayList<Integer> getAllPOTENTIALLYLegalNeighbourPositions(int position) {
        ArrayList<Integer> neighbours = new ArrayList<>(4);
        for (Direction dir : Direction.values()) {
            int newPos = getPositionInDirection(dir, position);
            int hardcodedPlayerID = 0;
            if (isPOTENTIALLYLegalMove(hardcodedPlayerID, position, newPos)) neighbours.add(newPos);
        }
        return neighbours;
    }

    public ArrayList<Integer> getAllAdjacentPositions(int position) {
        ArrayList<Integer> neighbours = new ArrayList<>(4);
        for (Direction dir : Direction.values()) {
            if (!isBorderInDirection(dir, position)) {
                neighbours.add(getPositionInDirection(dir, position));
            }
        }
        return neighbours;
    }

    /* Helper */

    int getIndex(int x, int y) {
        return y * columns + x;
    }

    int getX(int position) {
        return position % columns;
    }

    int getY(int position) {
        return position / columns;
    }


    boolean isEveryActivePlayerAt(int pos) {
        for (Character c : characters) {
            if (c.getPosition() != pos && c.isActive) return false;
        }
        return true;
    }

    boolean isEveryActivePlayerAt(PlaceableType type) {
        for (Character c : characters) {
            if (!c.isActive) continue;
            Placeable p = tiles[c.getPosition()].getObject();
            if (p==null && type!=null) return false;
            if (p==null && type==null) return true;
            if (p.getType() != type) return false;
        }
        return true;
    }

/*    boolean isEveryActivePlayerAt(Placeable obj) {
        for (Character c : characters) {
            if (tiles[c.getPosition()].getObject() != obj && c.isActive) return false;
        }
        return true;
    }*/

    private void setMatchingRandomTile(int playerID, int position, int positionComesFrom) {
        //get new tile...
        tiles[position] = Tile.getRandomTile();

        //...and shuffle it
        int times = (int) (Math.random() * 4);
        for (int i = 0; i < times; i++) {
            tiles[position].rotateTileClockwise();
        }
        //turn it until it fits
        Direction dir = getDirectionOfNeighbourPosition(position, positionComesFrom);
        //System.out.println("CM "+positionComesFrom+" is to the "+dir+"from "+position);
        if (dir == null) {
            view.displayFootnote(playerID, "CM NAN AFTER JUMP?");
            //System.out.println("CM NAN AFTER JUMP?");
        }
        for (int i = 0; i < 3; i++) {
            if (!isOpenInDirection(dir, position)) {
                tiles[position].rotateTileClockwise();
            } else break;
        }

//        for (int i = 0; i < 3; i++) {
//            if (!isLegalMove(playerID, positionComesFrom, position)) {
//                tiles[position].rotateTileClockwise();
//            } else break;
//        }
    }

    private boolean isBorderInDirection(Direction dir, int position) {
        return getPositionInDirection(dir, position) == OUT_OF_BOUNDS;
    }

    public int getPositionInDirection(Direction direction, int position) {
        if (direction == Direction.NORTH) {
            if (getY(position) == 0) return OUT_OF_BOUNDS;
            else return position - columns;
        } else if (direction == Direction.EAST) {
            if (getX(position) == columns - 1) return OUT_OF_BOUNDS;
            else return position + 1;
        } else if (direction == Direction.SOUTH) {
            if (getY(position) == rows - 1) return OUT_OF_BOUNDS;
            else return position + columns;
        } else if (direction == Direction.WEST) {
            if (getX(position) == 0) return OUT_OF_BOUNDS;
            else return position - 1;
        }
        return OUT_OF_BOUNDS;
    }

    public Direction getDirectionOfNeighbourPosition(int posStart, int posNeighbour) {
        for (Direction dir : Direction.values()) {
            if (isAdjacentInDirection(dir, posStart, posNeighbour)) return dir;
        }
        return null;
    }

    private boolean isAdjacentInDirection(Direction dir, int position, int potentialNeighbourPos) {
        return getPositionInDirection(dir, position) == potentialNeighbourPos;
    }

    private boolean isEastOf(int pos1, int pos2) {
        return (getX(pos1) == getX(pos2) + 1 && getY(pos1) == getY(pos2));
    }

    private boolean isWestOf(int pos1, int pos2) {
        return (getX(pos1) == getX(pos2) - 1 && getY(pos1) == getY(pos2));
    }

    private boolean isNorthOf(int pos1, int pos2) {
        return (getX(pos1) == getX(pos2) && getY(pos1) == getY(pos2) - 1);
    }

    private boolean isSouthOf(int pos1, int pos2) {
        return (getX(pos1) == getX(pos2) && getY(pos1) == getY(pos2) + 1);
    }

    private boolean isOpenInDirection(Direction direction, int position) {
        if (tiles[position] == null) return true;
        return tiles[position].isOpenInDirection(direction);
    }

    private boolean isPOTENTIALLYOpenInDirection(Direction direction, int position) {
        if (tiles[position] == null) return true;
        return tiles[position].isPOTENTIALLYOpenInDirection(direction);
    }

    public boolean isUnset(int position) { //and settable
        if (position < 0 || position > rows * columns - 1) return false;
        return tiles[position] == null;
    }

    public boolean isUnsetOrHidden(int position) {
        if (position < 0 || position > rows * columns - 1) return false;
        return (tiles[position] == null || tiles[position].isHidden());
    }

    public boolean isHidden(int position) {
        if (position < 0 || position > rows * columns - 1) return false;
        if (tiles[position] == null) return false;
        return tiles[position].isHidden();
    }


    /* Send messages to view */

    void sendTile(int position) {
        if (isUnset(position)) {
            //don't show unset tiles, leave that to local view
            return;
        }
        view.showTile(position, tiles[position].getName());
    }

    void sendCharacter(int id) {
        view.showCharacter(id, characters[id].getPosition(), characters[id].getComesFrom());
    }

    void sendStats(int playerID) {
        Placeable obj = getPlaceableObjectAt(characters[playerID].getPosition());
        if (obj == null) {
            view.displayStats(playerID, characters[playerID].getStats(), null, "");
        } else if (obj.getType() == PlaceableType.PLACEABLE_CHEST) {
            view.displayStats(playerID, characters[playerID].getStats(), obj.getType(), "");
        } else {
            view.displayStats(playerID, characters[playerID].getStats(), obj.getType(), obj.getStats());
            System.out.println(obj.getType());
        }
    }

    public void sendEffect(int playerID, int position, EffectType type, String argument) {
        view.showEffect(playerID, position, type, argument);
    }

    public void sendEffect(int playerID, int position, EffectType type) {
        sendEffect(playerID, position, type, null);
    }

    /* Set and send */

    void setTileAndSend(int x, int y, Tile tile) {
        setTileAndSend(getIndex(x, y), tile);
    }

    void setTileAndSend(int position, Tile tile) {

        //if previous tile has object, take it, slight workaround
        if (tiles[position]!=null) {
            if (tile.getObject()==null) {
                tile.setObject(tiles[position].getObject());
            }
        }

        tiles[position] = tile;
        sendTile(position);
    }

    void setTile(int position, Tile tile) {
        Tile oldTile = tiles[position];
        tiles[position] = tile;
        tiles[position].setHidden(true);
    }


    void setObject(int position, Placeable object) {
        //remove previous object from view - view keeps old objects otherwise!
        if (tiles[position].getObject() != null) {
            view.removeObject(position);
        }
        tiles[position].setObject(object);
        object.setHasAppeared(false);
    }


    void setObjectAndSend(int position, Placeable object) {
        setObject(position, object);
        sendObject(position, object);
    }

    void sendObject(int position, Placeable object) {

        if (object==null) return; //shouldn't happen

        object.setHasAppeared(true);
        int onlyVisibleTo = object.getVisibleOnlyTo();
        if (onlyVisibleTo != -1) {
            //only one view
            view.showObjectTo(onlyVisibleTo, position, object.getType());
        } else {
            //all views
            view.showObject(position, object.getType());
        }
    }


    public void removeObjectAndSend(int removingPlayerID, int position) {
        tiles[position].setObject(null);
        view.removeObject(position);
        for (int i = 0; i < characters.length; i++) {
            //check any characters at this position
            if (characters[i].getPosition() == position) {
                //clear any popups of characters that are at this position, unless active one
                if (i != removingPlayerID) {
                    System.out.println("CM removes object, calls SHOW GAME");
                    view.displayGame(i);
                    //is called even when character is not in action
                    if (false) {
                        //even when player is in game, which would not be good
                        view.displayMessage(i, "The thing that was here is gone.");
                    } else {
                        //even if player is in stats, which is good!
                        view.displayGame(i);
                        view.displayFootnote(i, "It's gone.");
                    }
                }
                //active character: feedback must come elsewhere, i.e. delegates
                else {
                    System.out.println("CM removes object, SHOW GAME *later* to be called separately");
                    //view.removeObject(position);
                    //view.showGame(i);
                    //view.showMessage(i, "YOU HAVE MADE IT GONE!");
                }
            }
        }
    }

    public void rotateAllRotatorsAndSend() {
        rotateAllRotators(true);
    }

    public void rotateAllRotators() {
        rotateAllRotators(false);
    }

    private void rotateAllRotators(boolean send) { //can be done more efficiently?
        if (turnOffRotation) return;
        for (int i = 0; i < tiles.length; i++) {
            if (!isUnset(i) && !tiles[i].isHidden()) {
                if (tiles[i].getSpecialFunction() == Tile.ROTATOR_CLOCKWISE) {
                    Tile oldTile = tiles[i].clone(); // VERY inefficient
                    tiles[i].rotateTileClockwise();
                    if (send) sendTile(i);
                    adjustNumberOfOpenEnds(tiles[i], i, oldTile); // VERY inefficient
                } else if (tiles[i].getSpecialFunction() == Tile.ROTATOR_ANTICLOCKWISE) {
                    Tile oldTile = tiles[i].clone(); // VERY inefficient
                    tiles[i].rotateTileAnticlockwise();
                    if (send) sendTile(i);
                    adjustNumberOfOpenEnds(tiles[i], i, oldTile); // VERY inefficient
                }
                }
        }
    }


    /* Movement */

    public boolean isPOTENTIALLYLegalMove(int playerID, int fromPosition, int toPosition) {
        //OUT OF BOUNDS, necessary
        if (toPosition < 0 || toPosition >= columns * rows)
            return false;

        //check all directions for adjacency
        Direction goingTowards = null;
        for (Direction dir : Direction.values()) {
            if (isAdjacentInDirection(dir, fromPosition, toPosition)) {
                goingTowards = dir;
                break;
            }
        }
        if (goingTowards == null) return false;

        //chasm, UNTESTED
        if (tiles[fromPosition] != null && tiles[fromPosition].getExits() == 0 && (tiles[fromPosition].getVisualVariant() == Tile.CHASM_SLASH || tiles[fromPosition].getVisualVariant() == Tile.CHASM_BACKSLASH)) {
            Direction comesFromSide = null;
            int comesFrom = getCharacter(playerID).getComesFrom();
            if (isNorthOf(comesFrom, fromPosition)) comesFromSide = Direction.SOUTH;
            else if (isEastOf(comesFrom, fromPosition)) comesFromSide = Direction.WEST;
            else if (isSouthOf(comesFrom, fromPosition)) comesFromSide = Direction.NORTH;
            else if (isWestOf(comesFrom, fromPosition)) comesFromSide = Direction.EAST;
            if (!tiles[fromPosition].canBeTraversed(comesFromSide, goingTowards)) {
                return false;
            }
        }

        //can get out
        if (isPOTENTIALLYOpenInDirection(goingTowards, fromPosition)) {
            //can get in
            if (isPOTENTIALLYOpenInDirection(goingTowards.getOpposite(), toPosition)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLegalMove(int playerID, int fromPosition, int toPosition) {
        if (toPosition < 0 || toPosition >= columns * rows)
            return false; //OUT OF BOUNDS, necessary
        for (Direction dir : Direction.values()) {
            //is goal in neighbourly position?
            if (isAdjacentInDirection(dir, fromPosition, toPosition)) {
                //is start open in this direction?
                if (!isOpenInDirection(dir, fromPosition)) {
                    return false;
                }
                //is a chasm in the way?
                if (true) {
                    if (tiles[fromPosition] != null && tiles[fromPosition].getExits() == 0 && tiles[fromPosition].getVisualVariant() == Tile.CHASM_BACKSLASH) {
                        //kommt von S, will nach N, O return false
                        //kommt von N, will nach W, S return false
                        //kommt von O, will nach W, S return false
                        //kommt von W, will nach N, O return false
                        int comesFrom = characters[playerID].getComesFrom();
                        if (isSouthOf(comesFrom, fromPosition) || isWestOf(comesFrom, fromPosition)) {
                            if (isNorthOf(toPosition, fromPosition)) return false;
                            if (isEastOf(toPosition, fromPosition)) return false;
                        } else if (isNorthOf(comesFrom, fromPosition) || isEastOf(comesFrom, fromPosition)) { //ok
                            if (isWestOf(toPosition, fromPosition)) return false;
                            if (isSouthOf(toPosition, fromPosition)) return false;
                        }
                    } else if (tiles[fromPosition] != null && tiles[fromPosition].getExits() == 0 && tiles[fromPosition].getVisualVariant() == Tile.CHASM_SLASH) {
                        int comesFrom = characters[playerID].getComesFrom();
                        //kommt von S, will nach N, W return false
                        //kommt von N, will nach O, S return false
                        //kommt von O, will nach W, N return false
                        //kommt von W, will nach S, O return false
                        if (isSouthOf(comesFrom, fromPosition) || isEastOf(comesFrom, fromPosition)) {
                            if (isWestOf(toPosition, fromPosition)) return false;
                            if (isNorthOf(toPosition, fromPosition)) return false;
                        } else if (isWestOf(comesFrom, fromPosition) || isNorthOf(comesFrom, fromPosition)) {
                            if (isEastOf(toPosition, fromPosition)) return false;
                            if (isSouthOf(toPosition, fromPosition)) return false;
                        }
                    }
                } else {
                    if (tiles[fromPosition] != null && tiles[fromPosition].getExits() == 0 && (tiles[fromPosition].getVisualVariant() == Tile.CHASM_SLASH || tiles[fromPosition].getVisualVariant() == Tile.CHASM_BACKSLASH)) {
                        Direction comesFromSide = null;
                        int comesFrom = getCharacter(playerID).getComesFrom();
                        if (isNorthOf(comesFrom, fromPosition)) comesFromSide = Direction.SOUTH;
                        else if (isEastOf(comesFrom, fromPosition)) comesFromSide = Direction.WEST;
                        else if (isSouthOf(comesFrom, fromPosition)) comesFromSide = Direction.NORTH;
                        else if (isWestOf(comesFrom, fromPosition)) comesFromSide = Direction.EAST;
                        if (!tiles[fromPosition].canBeTraversed(comesFromSide, dir)) {
                            return false;
                        }
                    }

                }
                //is goal UNSET?
                //if (isUnset(toPosition)) return true;
                //is goal open in opposing direction?
                return isOpenInDirection(dir.getOpposite(), toPosition); //includeds unset tile
            }
        }
        return false;
    }


    public boolean isAttemptedLegalMoveSuccessful(int id, int start, int goal) {
        //have character perform strength check or such
        return true;
    }

    void adjustNumberOfOpenEnds(Tile newTile, int position, Tile oldTile) {
        int old = numberOfOpenEnds;
        boolean verbose = false;
        char newTileSpecial = tiles[position].getSpecialFunction();

        // tile auf unset
        if (oldTile == null) {
            // es ist kein rotator
            if (true || newTileSpecial != Tile.ROTATOR_CLOCKWISE) {
                //fuer alle Richtungen
                for (Direction dir : Direction.values()) {
                    // wenn nicht am Rand in dieser Richtung
                    if (!isBorderInDirection(dir, position)) {
                        if (verbose) System.out.println("CM nicht am Rand in Richtung " + dir);
                        //wenn das eigene feld offen ist in dieser richtung
                        if (isOpenInDirection(dir, position)) {
                            if (verbose) System.out.println("CM offen Richtung " + dir);
                            //wenn das feld in dieser richtung unbelegt ist
                            if (isUnset(getPositionInDirection(dir, position)))
                            //if (getTileInDirection(dir, position) == null)
                            {
                                if (verbose)
                                    System.out.println("CM unbelegt Richtung " + dir + " ++");
                                numberOfOpenEnds++;
                            }
                        }
                        //wenn das feld in dieser richtung offen zum aktuellen feld ist
                        //Tile t = getTileInDirection(dir, position);
                        int posOpp = getPositionInDirection(dir, position);

                        if (!isUnset(posOpp) && tiles[posOpp].isOpenInDirection(dir.getOpposite())) {
                            //if (!isUnset(posOpp) && tiles[posOpp].isOpenInDirection(dir.getOpposite())) {
                            if (verbose)
                                System.out.println("CM verbindet oder verschliesst Richtung " + dir + " --");
                            numberOfOpenEnds--;
                        }
                    } else {
                        if (verbose) System.out.println("CM am Rand in  Richtung " + dir + " --");
                    }
                }
            }
            // es ist rotator
            else {


            }
        }
        // tile auf ein anderes
        else {
            //neu ist nicht-rotator
            if (true || newTileSpecial != Tile.ROTATOR_CLOCKWISE) {
                if (newTile.getExits() == 0) { // i.e. after opener
                    for (Direction dir : Direction.values()) {
                        if (!oldTile.isOpenInDirection(dir)) {
                            if (isUnset(getPositionInDirection(dir, position))) {
                                //if (!isBorderInDirection(dir, position)) {
                                //if (getTileInDirection(dir, position) == null) {
                                numberOfOpenEnds++;
                                if (verbose)
                                    System.out.println("CM one more exit, now: " + numberOfOpenEnds);
                                //}
                            }
                        }
                    }
                } else {
                    // wenn ein vorhandenes tile, das nicht 0000 ist, ersetzt wird
                    // zB rotation eines nicht-rotators
                }
            }
            //neu ist rotator
            else {


            }
        }

        //view.displayFootnote(0, "Exits was " + old + ", now " + numberOfOpenEnds);
    }


    public void makeMoveAndSend(int playerID, int position) {
        makeMoveAndSendTilesOnly(playerID, position);
        sendCharacter(playerID);
    }

    public void makeMoveAndSendTilesOnly(int playerID, int goalPosition) {
        //update position attributes and view
        int positionComesFrom = characters[playerID].getPosition();
        characters[playerID].setComesFrom(positionComesFrom);
        characters[playerID].setPosition(goalPosition);
        //if tile was unset, set it, and deal with consequences
        if (isUnset(goalPosition)) {
            setUnsetTile(playerID, goalPosition, positionComesFrom);
            rotateAllRotatorsAndSend();
            adjustNumberOfOpenEnds(tiles[goalPosition], goalPosition, null);
            if (Math.random() < chanceOfSpontaneousRotation && tiles[goalPosition].getExits() != 0) {
                doSpontaneousRotationAndAdjustOpenEnds(goalPosition);
            }
            checkObjectAppearanceOnNewlySetTileAndSend(goalPosition);
            sendTile(goalPosition);
            checkDownAppearanceAndSend(playerID, goalPosition);
        }
        //otherwise, character has moved to set tile, hidden or otherwise
        else {
            rotateAllRotatorsAndSend();
            checkUnappearedObjectAndSend(goalPosition);
            if (isHidden(goalPosition)) {
                //may not work and make problems, might overwrite AppearanceOnNewlySetTile!
                tiles[goalPosition].setHidden(false);
                adjustNumberOfOpenEnds(tiles[goalPosition], goalPosition, null);
                sendTile(goalPosition);
                checkDownAppearanceAndSend(playerID, goalPosition);
            }
            if (Math.random() < chanceOfSpontaneousRotation && tiles[goalPosition].getExits() != 0) {
                doSpontaneousRotationAndAdjustOpenEnds(goalPosition);
                sendTile(goalPosition);
            }
        }
    }


    private void checkDownAppearanceAndSend(int playerID, int position) {
        if (!wayDownHasAppeared()) {
            int i = getNumberOfOpenEnds(playerID);
            //view.displayFootnote(0, "Exits: " + i);
            //if (getNumberOfOpenEnds() <= 0 && (!exitHasAppeared)) {// && exitIsAt == OUT_OF_BOUNDS)) {
            if (i <= 0) {
                if (!wayDownHasAppeared()) {// && exitIsAt == OUT_OF_BOUNDS)) {
                    setObjectAndSend(position, new PlaceableDown()); // removes previous objects
                    wayDownPosition = position;
                }
            }
        } else {
            view.displayFootnote(0, "Exits... " + getNumberOfOpenEnds(playerID));
        }
    }

    private void setUnsetTile(int playerID, int playerPosition, int positionComesFrom) {
        //set matching random tile
        setMatchingRandomTile(playerID, playerPosition, positionComesFrom);
        //check reachability - change tile if necessary to keep connection to other players
        if (!((CentralAdapter) view).centralController.isReachableForAllOtherPlayers(playerID)) {
            setTile(playerPosition, new Tile(0));
        }
        //if on border, close exits that lead to border - only if not rotator!
        if (closeExitsOnBorders) {
            if (tiles[playerPosition].getSpecialFunction() != Tile.ROTATOR_ANTICLOCKWISE && tiles[playerPosition].getSpecialFunction() != Tile.ROTATOR_CLOCKWISE) {
                closeExitsToBordersAndMakeNormal(playerPosition);
            }
        }
        //maybe make it a rotator
        if (Math.random() < chanceOfRotator) {
            if (tiles[playerPosition].getExits() == 1 || tiles[playerPosition].getExits() == 10 || tiles[playerPosition].getExits() == 100 || tiles[playerPosition].getExits() == 1000) {
                if (Math.random()<0.5) {
                    tiles[playerPosition].setVisualVariant(Tile.ORANGE);
                    tiles[playerPosition].setSpecialFunction(Tile.ROTATOR_CLOCKWISE);
                } else {
                    tiles[playerPosition].setVisualVariant(Tile.GREEN);
                    tiles[playerPosition].setSpecialFunction(Tile.ROTATOR_ANTICLOCKWISE);
                }
            }
        }
        //maybe introduce chasm;
        double chanceOfChasm = 0;
        if (tiles[playerPosition].getExits() == 0 && Math.random() < chanceOfChasm) {
            if (Math.random() < 0.5) tiles[playerPosition].setVisualVariant(Tile.CHASM_BACKSLASH);
            else tiles[playerPosition].setVisualVariant(Tile.CHASM_SLASH);
        }
    }

    private void closeExitsToBordersAndMakeNormal(int position) {
        for (Direction dir : Direction.values()) {
            if (isBorderInDirection(dir, position)) {
                tiles[position].closeExit(dir);
                tiles[position].setVisualVariant(Tile.NORMAL); //oder das alte lassen?
            }
        }
    }

    private void doSpontaneousRotationAndAdjustOpenEnds(int position) {
        Tile oldTile = tiles[position];
        tiles[position].rotateTileClockwise();
        adjustNumberOfOpenEnds(tiles[position], position, oldTile); // NOT YET IMPLEMENTED
    }


    private void checkObjectAppearanceOnNewlySetTileAndSend(int position) {
        //random chance for treasure
        if (Math.random() < chanceOfChest) {
            setObjectAndSend(position, new PlaceableChest());
        }
        //random chance for monster
        else if (Math.random() < chanceOfOpponent) {
            setObjectAndSend(position, new PlaceableOpponent());
        }
    }

    private void checkUnappearedObjectAndSend(int position) {
        //check for preset objects that have not yet appeared
        Placeable object = getPlaceableObjectAt(position);
        if (object != null && !object.hasAppeared()) {
            object.setHasAppeared(true);
            sendObject(position, object);
        }
    }


}
