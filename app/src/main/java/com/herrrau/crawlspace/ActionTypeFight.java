package com.herrrau.crawlspace;

public enum ActionTypeFight {
    FLEE,
    FIGHT;

    String getName() {
        switch(this) {
            case FLEE: return ActivityGame.selfreferenceActivityGame.getString(R.string.action_flee);
            case FIGHT: return ActivityGame.selfreferenceActivityGame.getString(R.string.action_fight);
        }
        return "";
    }

}
