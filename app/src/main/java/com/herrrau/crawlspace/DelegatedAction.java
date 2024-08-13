package com.herrrau.crawlspace;

public class DelegatedAction {
    CentralModel model;
    GameView view;
    CentralController controller;

    public DelegatedAction(CentralModel model, GameView view, CentralController controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    public void attemptAction(int playerID, ActionType type) {
        if (type == ActionType.IS_REACHABLE) {
            if (!model.wayDownHasAppeared()) return;
            boolean reachable = controller.delegatedMapTraversal.calculateReachable(model.getCharacter(playerID).getPosition(), model.wayDownPosition);
            if (model.wayDownHasAppeared()) {
                view.displayFootnote(playerID, "Is reachable: " + reachable + " Exit at: " + model.wayDownPosition);
                model.makeMoveAndSend(playerID, model.wayDownPosition);
            }
            view.displayGame(playerID);
        } else if (type == ActionType.DIG) {
            int pos = model.getCharacter(playerID).getPosition();
            controller.delegatedConsequences.swapSetTileForNewOpenTile(playerID, pos);
        } else if (type == ActionType.SEARCH) {
            int pos = model.getCharacter(playerID).getPosition();
            if (model.getPlaceableObjectAt(pos) == null) {
                controller.delegatedConsequences.anObjectIsFound(playerID, pos);
            }
        } else if (type == ActionType.ROTATE) {
            model.rotateAllRotatorsAndSend();
            view.displayGame(playerID);
        }
    }

}
