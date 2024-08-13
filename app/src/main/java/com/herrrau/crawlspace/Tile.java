package com.herrrau.crawlspace;

public class Tile {
    static final char NONE = 'N', ROTATOR_CLOCKWISE = 'R', ROTATOR_ANTICLOCKWISE = 'L', TOWER = 'T', DRAGON = 'D';
    private int exits;
    private char visualVariant;
    static final char CHASM_SLASH = 'a';
    static final char CHASM_BACKSLASH = 'l';
    static final char CHAMBER1 = 'c', CHAMBER2 = 'e', CHAMBER3 = 'f', CHAMBER4 = 'g';
    static final char GREEN = 'd', ORANGE = 'r', PINK = 't', NORMAL = 'n';

    private char specialFunction;
    private Placeable object;
    private boolean isHidden = false;
    double chanceOfChest = 0.5;
    double chanceOfOpponent = 0.0;
    double chanceOfOpponentInChest = 0;


    public Tile(int exits) {
        this.exits = exits;
        visualVariant = NORMAL;
        specialFunction = NONE;
    }

    public Tile(int exits, char variant) {
        this.exits = exits;
        this.visualVariant = variant;
    }

    public Tile(int exits, char variant, char specialFunction) {
        this.exits = exits;
        this.visualVariant = variant;
        this.specialFunction = specialFunction;
    }

    public void closeExit(Direction dir) {
        if (!isOpenInDirection(dir)) return;
        if (dir == Direction.NORTH) {
            exits = exits + 1000;
        } else if (dir == Direction.EAST) {
            exits = exits + 100;
        } else if (dir == Direction.SOUTH) {
            exits = exits + 10;
        } else if (dir == Direction.WEST) {
            exits = exits + 1;
        }
    }

    public void openExit(Direction dir) {
        if (isOpenInDirection(dir)) return;
        if (dir == Direction.NORTH) {
            exits = exits - 1000;
        } else if (dir == Direction.EAST) {
            exits = exits - 100;
        } else if (dir == Direction.SOUTH) {
            exits = exits - 10;
        } else if (dir == Direction.WEST) {
            exits = exits - 1;
        }
    }



    public void setVisualVariant(char c) {
        visualVariant = c;
    }

    public char getVisualVariant() {
        return visualVariant;
    }

    public void setSpecialFunction(char c) {
        specialFunction = c;
    }

    public char getSpecialFunction() {
        return specialFunction;
    }

    public Placeable getObject() {
        return object;
    }

    public void setObject(Placeable object) {
        this.object = object;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean b) {
        isHidden = b;
    }

    public String getName() {
        return String.format("%04d", exits) + visualVariant;
    }

    public String getNameWithoutVariant() {
        return String.format("%04d", exits);
    }

    public boolean isOpenInDirection(Direction direction) {
        if (direction == Direction.NORTH && (exits / 1000) == 0) return true;
        else if (direction == Direction.EAST && (exits / 100) % 2 == 0) return true;
        else if (direction == Direction.SOUTH && (exits / 10) % 2 == 0) return true;
        else if (direction == Direction.WEST && (exits) % 2 == 0) return true;
        else return false;
    }

    public boolean isPOTENTIALLYOpenInDirection(Direction direction) {
        if (specialFunction==ROTATOR_CLOCKWISE || specialFunction==ROTATOR_CLOCKWISE ) return true;
        else return isOpenInDirection(direction);
    }

    public void rotateTileClockwise() {
        exits = exits / 10 + exits % 2 * 1000;
    }
    public void rotateTileAnticlockwise() {
        for (int i=0; i<3; i++) {
            rotateTileClockwise();
        }
    }

    public boolean canBeTraversed(Direction from, Direction to) {
        if (!isOpenInDirection(from) || !isOpenInDirection(to)) return false;
        if (visualVariant == CHASM_SLASH) {
            if (from == Direction.NORTH && to == Direction.WEST) return true;
            if (from == Direction.WEST && to == Direction.NORTH) return true;
        } else if (visualVariant == CHASM_BACKSLASH) {
            if (from == Direction.NORTH && to == Direction.EAST) return true;
            if (from == Direction.EAST && to == Direction.NORTH) return true;
        }
        return false;
    }

    int getExits() {
        return exits;
    }

    static Tile getRandomTile() {
        double random = Math.random();
        if (random < 0.3) {
            return new Tile(1); //dreier
        }
        if (random < 0.5) {
            return new Tile(101); //gerade
        }
        if (random < 0.65) {
            return new Tile(1001); // Kurve
        }
        if (random < 0.40) {
            return new Tile(111); // Sackgasse
        }
        return new Tile(0); //vierer
    }

    static Tile getRandomTileORG() {
        double random = Math.random();
        if (random < 0.3) {
            return new Tile(1); //dreier
        }
        if (random < 0.5) {
            return new Tile(101); //gerade
        }
        if (random < 0.65) {
            return new Tile(1001); // Kurve
        }
        if (random < 0.70) {
            return new Tile(111); // Sackgasse
        }
        return new Tile(0); //vierer
    }

    public Tile clone() {
        Tile newTile = new Tile(getExits(), getVisualVariant());
        newTile.isHidden = this.isHidden;
        newTile.specialFunction = this.specialFunction;
        newTile.chanceOfChest = this.chanceOfChest;
        newTile.chanceOfOpponent = this.chanceOfOpponent;
        newTile.chanceOfOpponentInChest = this.chanceOfOpponentInChest;
        newTile.object = this.object;
        return newTile;

    }
}
