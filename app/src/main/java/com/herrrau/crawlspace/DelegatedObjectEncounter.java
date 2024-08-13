package com.herrrau.crawlspace;

public class DelegatedObjectEncounter {
    CentralModel model;
    GameView view;
    CentralController controller;
    static boolean avoidAutomaticEncounters = false;

    public DelegatedObjectEncounter(CentralModel model, GameView view, CentralController controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    public void dealWithObjectEncounter(int playerID, int position, Placeable obj) {
        //shouldn't happen
        if (obj==null) return;
        //quick info
        view.displayFootnote(playerID, "You found: " + obj.getType());
        //opponents enforce popup
        if (obj.getType() == PlaceableType.PLACEABLE_OPPONENT && !avoidAutomaticEncounters) {
            view.displayOpponent(playerID, model.getCharacter(playerID).getStats(), PlaceableType.PLACEABLE_OPPONENT, obj.getStats());
        }
        if (obj.getType() == PlaceableType.PLACEABLE_TRAP) {
            controller.delegatedConsequences.setOffTrap(playerID, position);
        }
        // auto-use: use and remove automatically
        else if (obj.isUsedAutomatically() && !avoidAutomaticEncounters) {
            obj.interactWith(model.getCharacter(playerID));
            model.removeObjectAndSend(playerID, position);
        }
    }
}