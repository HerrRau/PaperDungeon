package com.herrrau.crawlspace;

public class ActionPack {

    boolean[] canDoAction;

    public ActionPack() {
        canDoAction = new boolean[ActionType.values().length];
        for (int i=0; i<ActionType.values().length; i++) {
            canDoAction[i] = true;
        }
    }

    public String getActions() {
        String results = "";
        for (int i = 0; i < canDoAction.length; i++) {
            if (canDoAction[i]) results += "1#";
            else results += "0#"; //0:
        }
        return results;
    }
}

