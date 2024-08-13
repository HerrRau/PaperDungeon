package com.herrrau.crawlspace;

public enum ActionType {
    DIG,
    //X,Y,Z,A,B,C,D,E,F,G,H,I,
    SEARCH,
    ROTATE,
    IS_REACHABLE;

    String getName() {
        switch(this) {
            case DIG: return ActivityGame.selfreferenceActivityGame.getString(R.string.action_dig);
            case SEARCH: return ActivityGame.selfreferenceActivityGame.getString(R.string.action_search);
            case ROTATE: return "Turn tilesXXX";
            case IS_REACHABLE: return ActivityGame.selfreferenceActivityGame.getString(R.string.action_xxx);
        }
        return this.toString(); //###########
    }


}
