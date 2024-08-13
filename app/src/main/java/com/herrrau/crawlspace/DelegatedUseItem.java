package com.herrrau.crawlspace;

public class DelegatedUseItem {
    CentralModel model;
    GameView view;
    CentralController controller;
    boolean clairvoyanceJumpToExit = true;

    public DelegatedUseItem(CentralModel model, GameView view, CentralController controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    public void attemptUseItem(int playerID, ItemType type) {
        //get info
        Character character = model.getCharacter(playerID);
        int pos = character.getPosition();
        Placeable obj = model.getPlaceableObjectAt(pos);
        //must have resources
        if (character.getNumberOfItems(type) <= 0) return;
        //not in presence of opponent
        boolean withOpponent = obj != null && obj.getType() == PlaceableType.PLACEABLE_OPPONENT;
        if (!type.canBeUsedInPresenceOfOpponent() && withOpponent) return;

        //what to do after use
        //case: opener
        if (type == ItemType.ITEM_OPENER) {
            character.changeItemAmount(type, -1);
            //model.adjustNumberOfOpenEnds(0, pos, model.tiles[pos]); //??
            Tile tileOld = model.tiles[pos];
            model.setTileAndSend(pos, new Tile(0));
            model.sendEffect(playerID, pos, EffectType.EXPLOSION);
            int level = model.getCurrentDepth();
            controller.startTimer(new Runnable() {
                public void run() {
                    if (model.getCurrentDepth() == level) {
                        model.setTileAndSend(pos, tileOld);
                    }
                    //model.adjustNumberOfOpenEnds(tileOld, pos, model.tiles[pos]);
                }
            }, 5000);
            if (!withOpponent) view.displayGame(playerID);
        } else if (type == ItemType.ITEM_GHOST_WALK) {
            character.changeItemAmount(type, -1);
            controller.delegatedConsequences.doATeleport(playerID);
        } else if (type == ItemType.ITEM_POTION) {
            if (character.strength >= character.maxStrength) return;
            character.changeItemAmount(type, -1);
            character.increaseStrength(2);
            //update view, do NOT show game
            if (obj == null) {
                view.displayStats(playerID, model.getCharacter(playerID).getStats(), null, "");
            } else if (withOpponent) {
                view.displayOpponent(playerID, model.getCharacter(playerID).getStats(), obj.getType(), obj.getStats());
            } else {
                view.displayStats(playerID, model.getCharacter(playerID).getStats(), obj.getType(), obj.getStats());
            }
        } else if (type == ItemType.ITEM_MAP) {
            character.changeItemAmount(type, -1);
            controller.showMap(playerID, pos);
        } else if (type == ItemType.ITEM_INVISIBILITY) {
            character.changeItemAmount(type, -1);
            DelegatedObjectEncounter.avoidAutomaticEncounters = true;
            controller.startTimer(new Runnable() {
                public void run() {
                    DelegatedObjectEncounter.avoidAutomaticEncounters = false;
                }
            }, 5000);
            view.displayGame(playerID);
        } else if (type == ItemType.ITEM_TRAP) {
            if (model.getPlaceableObjectAt(pos) == null) {
                character.changeItemAmount(type, -1);
                Placeable trap = new PlaceableTrap(playerID);
                trap.setHasAppeared(true);
                model.setObjectAndSend(pos, trap);
                view.displayGame(playerID);
            }
        } else if (type == ItemType.ITEM_INVALUABLE_EXPERIENCE) {
            if (!withOpponent) {
                character.changeItemAmount(type, -1);
                character.changeItemAmount(ItemType.ITEM_LESSONS_LEARNED, +1);
                view.displayMessage(playerID, "A funny thing happened on the way to the forum...");
            }
        } else if (type == ItemType.ITEM_LESSONS_LEARNED) {
            if (character.getNumberOfItems(ItemType.ITEM_LESSONS_LEARNED) < 20) {
                view.displayFootnote(playerID, "You need at least 20 to use this item.");
            }
        }

    }

}
