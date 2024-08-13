package com.herrrau.crawlspace;

public class DelegatedLevels {
    CentralModel model;

    public DelegatedLevels(CentralModel model) {
        this.model = model;
    }

    public void setupLevel(int levelNumber) {
        CentralAdapter c = (CentralAdapter) model.getView();
        String openingMessage;
        String openingFootnote;
        openingFootnote = "";
        openingMessage = "";
        useTimerInThisLevel = false;
        //model.chanceOfRotator = 0; //standard setting
        int [] startingPositions = null;
        int [] startingTiles = null;
        //levelNumber = 5; //shortcut
        switch (levelNumber) {
            case (0): {
                boolean everybodyHasBeenDownBefore = true;
                for (Character ch : model.characters) {
                    if (ch.maxDepth == 0 && ch.isActive) {
                        everybodyHasBeenDownBefore = false;
                        break;
                    }
                }
                if (everybodyHasBeenDownBefore) {
                    openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level0_return);
                } else {
                    openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level0, Setup.activeCharacter.name);
                }
                //openingMessage = ActivityGame.selfreference.getText(R.string.level0).toString();
                //openingMessage = "<b>Willkommen!</b> Der Tempel des Phaidros ist schon lange verlassen. Aber die Jugend des Dorfes sucht ihn ab und zu noch auf. Es ist eine Art Mutprobe. Manche sagen auch, dass es da noch viel zu entdecken gibt.";
                openingFootnote = "Swipe right, swipe down, double tap";
                prepareModelAndView(4, 2, levelNumber, false);
                model.setTileAndSend(0, new Tile(1001, Tile.PINK));
                model.setTileAndSend(1, new Tile(0, Tile.GREEN));
                model.setTileAndSend(2, new Tile(0, Tile.GREEN));
                model.setTileAndSend(3, new Tile(0, Tile.GREEN));
                model.setTileAndSend(4, new Tile(0, Tile.GREEN));
                model.setTileAndSend(5, new Tile(0, Tile.GREEN));
                model.setTileAndSend(6, new Tile(0, Tile.GREEN));
                model.setTileAndSend(7, new Tile(110));
                boolean test = false;
                if (test) {
                    model.setObjectAndSend(0, new PlaceableDown());
                    model.wayDownPosition = 0;
                } else {
                    model.setObjectAndSend(model.rows * model.columns - 1, new PlaceableDown());
                    model.wayDownPosition = model.rows * model.columns - 1;
                }
                if (everybodyHasBeenDownBefore) {
                    //if (Setup.activeCharacter.maxDepth > 0) {
                    PlaceableSage p = new PlaceableSage();
                    if (true) {
                        p.setHasAppeared(false);
                        model.setObject(2, p);
                    } else {
                        //p.setHasAppeared(true); //not important
                        model.setObjectAndSend(2, p);
                    }
                }
                break;
            }
            case (1): {
                openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level1);
                model.chanceOfOpponent = 0;
                model.chanceOfChest = 0;
                model.chanceOfRotator = 0;
                prepareModelAndView(4, 3, levelNumber, false);
                //setupTimer(levelNumber);
                break;
            }
            case (2): {
                openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level2);
                model.chanceOfOpponent = 0;
                model.chanceOfChest = 0;
                model.chanceOfRotator = 0;
                prepareModelAndView(4, 4, levelNumber, false);
                model.setTileAndSend(1, new Tile(0000));
                model.increaseNumberOfOpenEnds(1);
                PlaceableChest pc = new PlaceableChest();
                pc.treasure = new ItemPack();
                pc.treasure.amounts[0] = 2;
                //pt.hasAppeared = true;
                model.setObjectAndSend(1, pc);
                if (false) {
                    PlaceableChest pt2 = new PlaceableChest();
                    pt2.treasure = new ItemPack();
                    pt2.treasure.amounts[0] = 2;
                    model.setTile(4, new Tile(111)); // Problem mit last exit##################
                    model.tiles[4].setHidden(true);
                    model.setObject(4, pt2);
                    model.increaseNumberOfOpenEnds(-1); //workaround -> on last but one
                }
                break;
            }
            case (3): {
                openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level3);;
                model.chanceOfOpponent = 0;
                model.chanceOfChest = 0.05;
                model.chanceOfRotator = 0;
                prepareModelAndView(6, 5, levelNumber, false);
                model.setTileAndSend(0, new Tile(1001, Tile.PINK));
                this.setupTimer(levelNumber);
                break;
            }
            case (4): {
                openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level4);
                model.chanceOfOpponent = 0;
                model.chanceOfChest = 0.05;
                model.chanceOfRotator = 0;
                prepareModelAndView(6, 5, levelNumber, true);
                //prepareModelAndView(6, 5, levelNumber, false);
                model.setTileAndSend(0, new Tile(1001, Tile.PINK));
                break;
            }
            case (5): {
                openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level5);
                model.chanceOfOpponent = 0;
                model.chanceOfChest = 0;
                model.chanceOfRotator = 0;
                prepareModelAndView(4, 4, levelNumber, false);
                model.setTile(0, new Tile(1001));
                model.setTile(1, new Tile(1010));
                model.setTile(2, new Tile(1000, Tile.ORANGE));
                model.tiles[2].setSpecialFunction(Tile.ROTATOR_ANTICLOCKWISE);
                model.setTile(3, new Tile(1100));
                model.setTile(4, new Tile(111));
                model.setTile(5, new Tile(1011));
                model.setTile(6, new Tile(0));
                model.setTile(7, new Tile(110));
                model.setTile(8, new Tile(1011));
                model.setTile(9, new Tile(1010));
                model.setTile(10, new Tile(1,  Tile.GREEN));
                model.tiles[10].setSpecialFunction(Tile.ROTATOR_ANTICLOCKWISE);
                model.setTile(11, new Tile(1100));
                model.setTile(14, new Tile(11));
                model.setTile(15, new Tile(110));
                for (int i=1; i<12; i++) {
                    model.tiles[i].setHidden(true);
                }
                model.tiles[14].setHidden(true);
                model.tiles[15].setHidden(true);
                break;
            }
            case (6): {
                openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level6);
                model.chanceOfOpponent = 0.1;
                model.chanceOfChest = 0.05;
                model.chanceOfRotator = 0;
                prepareModelAndView(6, 5, levelNumber, false);
                model.setTileAndSend(0, new Tile(1001, Tile.PINK));
                break;
            }
            case (7): {
                openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level7);
                prepareModelAndView(7, 7, levelNumber, false);
                for (int pos = 0; pos < model.rows * model.columns - 1; pos++) {
                    int type;
                    double r = Math.random();
                    if (r < 0.25) type = 1;
                    else if (r < 0.5) type = 10;
                    else if (r < 0.75) type = 100;
                    else type = 1000;
                    model.setTileAndSend(pos, new Tile(type, Tile.ORANGE, Tile.ROTATOR_CLOCKWISE));
                }
                model.setTileAndSend(1, new Tile(1000, Tile.ORANGE, Tile.ROTATOR_CLOCKWISE));
                model.setTileAndSend(model.columns, new Tile(100, Tile.ORANGE, Tile.ROTATOR_CLOCKWISE));
                model.setTileAndSend(model.columns * model.rows - 1, new Tile(110));
                model.setObjectAndSend(model.rows * model.columns - 1, new PlaceableDown());
                model.wayDownPosition = model.rows * model.columns - 1;
            break;
            }
            case (8): {
                openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level8);
                prepareModelAndView(2, 5, levelNumber, false);
                model.setTileAndSend(0, new Tile(1001, Tile.PINK));
                model.setTileAndSend(1, new Tile(1100));
                model.setTileAndSend(2, new Tile(0001));
                model.setTileAndSend(3, new Tile(100));
                model.setTileAndSend(4, new Tile(11));
                model.setTileAndSend(5, new Tile(110));
                model.setObjectAndSend(4, new PlaceableStairs());
                model.setObjectAndSend(5, new PlaceableDown());
                model.wayDownPosition = 5;
                startingPositions = new int [4];
                startingPositions[0]=0;
                startingPositions[1]=1;
                startingPositions[2]=3;
                startingPositions[3]=2;
                break;
            }
            case (9): {
                openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level9);
                model.chanceOfOpponentInChest = 0.1;
                model.chanceOfOpponent = 0.05;
                model.chanceOfChest = 0.05;
                model.chanceOfRotator = 0.1;
                prepareModelAndView(7, 6, levelNumber, false);
                break;
            }
            default:
                int periodOfPauseLevels = 7;
                if (levelNumber % periodOfPauseLevels == periodOfPauseLevels - 1) { //alle n level, starting after 9
                    openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level8);
                    boolean useNumberLevels = true;
                    if (useNumberLevels) {
                        setNumberLevel(levelNumber);
                        startingPositions = new int[]{0, model.columns - 1, model.columns * model.rows - 1, model.columns * (model.rows - 1)};
                        startingTiles = new int[]{1001, 1100, 110, 11 };
                        if (levelNumber>99 && levelNumber<200) {
                            startingTiles[0] = 1101;
                            startingPositions[0] = startingPositions[0]+1;
                            startingPositions[2] = startingPositions[2]+1;
                        }
                        else if (levelNumber / 10 == 1) {  //zehner
                            startingTiles[0] = 1101;
                            startingPositions[0] = 1;
                            startingPositions[2] = 9;
                        } else if (levelNumber / 10 == 3) {
                            startingTiles[0] = 1011;
                        } else if (levelNumber / 10 == 4) {
                            model.setTileAndSend(0, new Tile(1101));
                            startingPositions[2] = 9;
                        } else if (levelNumber / 10 == 7) {
                            startingPositions[2] = 9;
                        }
                    }
                    else {
                        prepareModelAndView(2, 5, levelNumber, false);
                        model.setTileAndSend(0, new Tile(1001, Tile.PINK));
                        model.setTileAndSend(1, new Tile(1100));
                        model.setTileAndSend(2, new Tile(0001));
                        model.setTileAndSend(3, new Tile(110));
                        model.setTileAndSend(4, new Tile(1));
                        model.setTileAndSend(5, new Tile(1110));
                        model.setObjectAndSend(4, new PlaceableStairs());
                        model.setObjectAndSend(5, new PlaceableDown());
                        model.wayDownPosition = 5;
                    }
                } else {
                    openingMessage = ActivityGame.selfreferenceActivityGame.getString(R.string.level_default);
                    model.chanceOfOpponent = 0.05;
                    model.chanceOfChest = 0.05;
                    model.chanceOfRotator = 0.05;
                    boolean useSetDimensions = false;
                    if (useSetDimensions) prepareModelAndView(model.columnsAtBeginning, model.rowsAtBeginning, levelNumber, false);
                    else {
                        int cols = 2;
                        cols = cols + (int)(Math.random()*6); // 2-7, Erw. 5,5
                        cols = cols + (int)(Math.random()*2);
                        cols = cols + (int)(Math.random()*2);
                        cols = cols + (int)(Math.random()*2);
                        cols = cols + (int)(Math.random()*2);

                        int rows = 3;
                        rows = rows + (int)(Math.random()*5);
                        rows = rows + (int)(Math.random()*5);
                        rows = rows + (int)(Math.random()*5);
                        // 3-18, Erwartungsert 9
                        if (cols<2) cols = 2;
                        if (rows<2) rows = 3;
                        if (cols>12) cols = 12;
                        if (rows>12) rows = 18;
                        prepareModelAndView(cols, rows, levelNumber, false);
                    }
                }
        }
        if (startingPositions==null) startingPositions = new int[]{0, model.columns - 1, model.columns * model.rows - 1, model.columns * (model.rows - 1)};
        if (startingTiles==null) startingTiles = new int[]{1001, 1100, 110, 11 };
        setCharactersAndSend(startingPositions);
        setStartingTilesAndSend(startingPositions, startingTiles);
        sendMessageAndStart(openingMessage, openingFootnote);
    }

    private void sendMessageAndStart(String message, String footnote) {
        for (int id = 0; id < model.characters.length; id++) {
            if (!message.equals("")) {
                model.getView().displayMessage(id, message);
            } else {
                model.getView().displayGame(id);
            }
            if (!message.equals("")) model.getView().displayFootnote(id, footnote);
        }

    }

    private void prepareModelAndView(int cols, int rows, int level, boolean useLimitedVision) {
        model.columns = cols;
        model.rows = rows;
        // strictly speaking, new tiles arrays only necessary when rows or columns have changed
        model.tiles = new Tile[model.rows * model.columns];
        model.getView().setupLevel(model.columns, model.rows, model.characters.length, level, useLimitedVision);
        //necessary for initalising tiles
        for (int i = 0; i < model.rows * model.columns; i++) {
            //unset werden nicht an view weiter geschickt
            model.setTileAndSend(i, null);
        }
    }

    private void setupCenter() {
        if (model.columns % 2 == model.rows % 2) {
            if (model.columns > model.rows) {
                model.setTileAndSend(model.columns / 2, model.rows / 2 - 0, new Tile(0, Tile.GREEN));
                model.setTileAndSend(model.columns / 2, model.rows / 2 - 1, new Tile(0, Tile.GREEN));
            } else {
                model.setTileAndSend(model.columns / 2 - 0, model.rows / 2, new Tile(0, Tile.GREEN));
                model.setTileAndSend(model.columns / 2 - 1, model.rows / 2, new Tile(0, Tile.GREEN));
            }
        } else if (model.columns % 2 == 1 && model.rows % 2 == 0) {
            model.setTileAndSend(model.columns / 2, model.rows / 2 - 0, new Tile(0, Tile.GREEN));
            model.setTileAndSend(model.columns / 2, model.rows / 2 - 1, new Tile(0, Tile.GREEN));
        } else {
            model.setTileAndSend(model.columns / 2 - 0, model.rows / 2, new Tile(0, Tile.GREEN));
            model.setTileAndSend(model.columns / 2 - 1, model.rows / 2, new Tile(0, Tile.GREEN));
        }
    }

    private void setupTimer(int startedInLevel) {
        this.useTimerInThisLevel = true;
        this.timerStartedInLevel = startedInLevel;
    }

    private int timerStartedInLevel;
    private boolean useTimerInThisLevel = false;
    private boolean timerIsRunning = false;
    private boolean disallowTimerInMultiplayerMode = false;

    void startTimer() {
        if (disallowTimerInMultiplayerMode && model.characters.length>1) return;
        if (!useTimerInThisLevel) return;
        if (timerIsRunning) return;
        timerIsRunning = true;
        int seconds = 30;
        //for each active character
        for (int id = 0; id < model.characters.length; id++) {
            //display timer for each player view (shows time)
            model.getView().displayLocalTimer(id, seconds);
            CentralAdapter c = (CentralAdapter) model.getView();
            //start one central timer per player (player timer is just for info)
            //sends info to player at end
            c.centralController.startTimer(
                    //cannot be stopped!
                    new Runnable() {
                        @Override
                        public void run() {
                            timerIsRunning = false;
                            for (int i = 0; i < model.characters.length; i++) {
                                if (model.getCurrentDepth() == timerStartedInLevel) {
                                    model.getView().displayFootnote(i, "Time Out");
                                    model.setup(timerStartedInLevel);
                                }
                            }
                        }
                    }, seconds * 1000);
        }
    }



    void setStartingTilesAndSend(int [] startingPositions, int [] startingTiles) {
        for (int i=0; i<model.characters.length; i++) {
            model.setTileAndSend(startingPositions[i], new Tile(startingTiles[i], Tile.PINK));
        }
    }

    void setCharactersAndSend(int[] startingPositions) {
        for (int i=0; i<model.characters.length; i++) {
            model.characters[i].setPosition(startingPositions[i]);
            model.characters[i].setStartingPosition(startingPositions[i]);
            model.characters[i].setComesFrom(-1);
            model.sendCharacter(i);
        }
    }

    private void setNumberLevel(int level) {
        model.chanceOfOpponent = 0;
        model.chanceOfChest = 0;
        //two digits
        if (level<100) {
            prepareModelAndView(4, 3, level, false);
            setDigit(level / 10, 0);
            setDigit(level % 10, 2);
        }
        //three digits
        else {
            prepareModelAndView(6, 3, level, false);
            setDigit(level / 100, 0);
            setDigit((level / 10) % 10, 2);
            setDigit(level % 10, 4);
        }
        model.setObjectAndSend(model.columns+1, new PlaceableStairs());
        model.setObjectAndSend(model.columns+3, new PlaceableStairs());
        model.setObjectAndSend(model.columns*2+1, new PlaceableDown());
        model.setObjectAndSend(model.columns*2+3, new PlaceableDown());
        model.wayDownPosition = model.columns*2+1;
    }

    private void setDigit(int digit, int startPos) {
        switch (digit) {
            case 0: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1001));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1100));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(101));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(101));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(11));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(110));
            }
            break;
            case 1: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1111));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1101));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(1111));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(101));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(1111));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(111));
            }
            break;
            case 2: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1001));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1100));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(1001));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(110));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(11));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(1110));
            }
            break;
            case 3: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1011));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1100));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(1011));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(100));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(1011));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(110));
            }
            break;
            case 4: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1101));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1101));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(11));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(100));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(1111));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(111));
            }
            break;
            case 5: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1001));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1110));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(11));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(1100));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(1011));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(110));
            }
            break;
            case 6: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1001));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1110));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(1));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(1100));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(11));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(110));
            }
            break;
            case 7: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1011));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1100));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(1111));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(101));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(1111));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(111));
            }
            break;
            case 8: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1001));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1100));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(1));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(100));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(11));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(110));
            }
            break;
            case 9: {
                model.setTileAndSend(startPos + 0 + 0 * model.columns, new Tile(1001));
                model.setTileAndSend(startPos + 1 + 0 * model.columns, new Tile(1100));
                model.setTileAndSend(startPos + 0 + 1 * model.columns, new Tile(11));
                model.setTileAndSend(startPos + 1 + 1 * model.columns, new Tile(100));
                model.setTileAndSend(startPos + 0 + 2 * model.columns, new Tile(1011));
                model.setTileAndSend(startPos + 1 + 2 * model.columns, new Tile(110));
            }
            break;
        }
    }


}

