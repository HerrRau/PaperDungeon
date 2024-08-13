package com.herrrau.crawlspace;

public enum PlaceableType {
    PLACEABLE_CHEST,
    PLACEABLE_TRAP,
    PLACEABLE_OPPONENT,
    WARRIOR,
    PLACEABLE_SAGE,
    WEAPON,
    PLACEABLE_DOWN, PLACEABLE_UP,
    PLACEABLE_STAIRS;

    String getName() {
        switch(this) {
            case PLACEABLE_CHEST: return ActivityGame.selfreferenceActivityGame.getString(R.string.placeable_chest);
            case PLACEABLE_TRAP: return ActivityGame.selfreferenceActivityGame.getString(R.string.placeable_trap);
            case PLACEABLE_SAGE: return ActivityGame.selfreferenceActivityGame.getString(R.string.placeable_sage);
            case PLACEABLE_OPPONENT: return ActivityGame.selfreferenceActivityGame.getString(R.string.placeable_opponent);
            case PLACEABLE_STAIRS: return ActivityGame.selfreferenceActivityGame.getString(R.string.placeable_stairs);
            case PLACEABLE_DOWN: return ActivityGame.selfreferenceActivityGame.getString(R.string.placeable_down);
            case PLACEABLE_UP: return ActivityGame.selfreferenceActivityGame.getString(R.string.placeable_up);
        }
        return this.toString().substring(10);
    }

    String getFileName() {
        switch(this) {
            case PLACEABLE_CHEST: return "placeable_chest";
            case PLACEABLE_TRAP: return "placeable_trap";
            case PLACEABLE_SAGE: return "placeable_sage";
            case PLACEABLE_OPPONENT: return "placeable_opponent";
            case PLACEABLE_STAIRS: return "placeable_stairs";
            case PLACEABLE_DOWN: return "placeable_down";
            case PLACEABLE_UP: return "placeable_up";
        }
        return "";
    }
}
