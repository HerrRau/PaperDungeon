package com.herrrau.crawlspace;

public class DelegatedObjectInteraction {
    CentralModel model;
    GameView view;
    CentralController controller;

    public DelegatedObjectInteraction(CentralModel model, GameView view, CentralController controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    public void attemptInteractWithObject(int playerID) {
        //get object
        int pos = model.getCharacter(playerID).getPosition();
        Placeable obj = model.getPlaceableObjectAt(pos);
        //maybe the object has disappeared
        if (obj == null) {
            controller.dealWithDisappearedObject(playerID);
            return;
        }
        //extra consequences
        if (obj.getType() == PlaceableType.PLACEABLE_TRAP) {
            controller.delegatedConsequences.setOffTrap(playerID, pos);
        } else if (obj.getType() == PlaceableType.PLACEABLE_CHEST) {
            if (Math.random() < model.chanceOfOpponentInChest) {
                if (Math.random() < 0.5) {
                    controller.delegatedConsequences.setOffTrap(playerID, pos);
                } else {
                    controller.delegatedConsequences.anOpponentAppears(playerID, pos);
                }
            } else {
                controller.delegatedConsequences.aChestOfItemsIsFound(playerID, (PlaceableChest) obj);
            }
        }
        if (obj.getType() == PlaceableType.PLACEABLE_DOWN) {
            //if (model.isEveryActivePlayerAt(pos)) {
            if (model.isEveryActivePlayerAt(PlaceableType.PLACEABLE_DOWN)) {
                model.setCurrentDepth((model.getCurrentDepth() + 1));
                model.setup(model.getCurrentDepth());
            } else {
                model.getView().displayGame(playerID);
            }
        } else if (obj.getType() == PlaceableType.PLACEABLE_STAIRS) {
            //if (model.isEveryActivePlayerAt(pos)) {
            if (model.isEveryActivePlayerAt(PlaceableType.PLACEABLE_STAIRS)) {
                model.setCurrentDepth(0);
                model.setup(model.getCurrentDepth());
            }
        } else if (obj.getType() == PlaceableType.PLACEABLE_SAGE) {
            boolean readyForExit = true;
            int maxPossibleDepth = Integer.MAX_VALUE;
            for (Character c : model.characters) {
                if (c.maxDepth < maxPossibleDepth) maxPossibleDepth = c.maxDepth;
                //wenn alle aktiven spieler an diesem ort sind - spaeter aendern zu: bei einem sage?
                if (c.getPosition() != pos && c.isActive) {
                    readyForExit = false;
                    break;
                }
            }
            if (readyForExit) {
                int nextDepth = maxPossibleDepth;
                model.setCurrentDepth(nextDepth);
                model.setup(model.getCurrentDepth());
            }
        } else if (obj.getType() == PlaceableType.PLACEABLE_OPPONENT) {
            //can only happen when invisible AND wilfully interacting -> displays incorrectly
            //view.displayOpponent(playerID, model.getCharacter(playerID).getStats(), obj.getType(), obj.getStats());
            view.displayOpponent(playerID, model.getCharacter(playerID).getStats(), PlaceableType.PLACEABLE_OPPONENT, obj.getStats());
        }
    }

}
