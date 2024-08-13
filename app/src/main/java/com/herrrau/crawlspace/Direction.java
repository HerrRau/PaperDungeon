package com.herrrau.crawlspace;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public Direction getOpposite() {
        if (this == NORTH) return SOUTH;
        if (this == EAST) return WEST;
        if (this == SOUTH) return NORTH;
        return EAST;
    }

}
