package com.herrrau.crawlspace;

public class DelegatedFight {
    CentralModel model;
    GameView view;
    CentralController controller;

    public DelegatedFight(CentralModel model, GameView view, CentralController controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    public void attemptActionFight(int playerID, ActionTypeFight type) {
        //get parameters
        Character character = model.getCharacter(playerID);
        int pos = character.getPosition();
        PlaceableOpponent opponent = (PlaceableOpponent) model.getPlaceableObjectAt(pos);
        if (opponent == null) {
            controller.dealWithDisappearedObject(playerID);
            return;
        }
        //deal with action fight
        if (type == ActionTypeFight.FIGHT) {
            boolean successful = Math.random() < 0.5;
            if (successful) {
                opponent.strength--;
                opponent.strength--;
                opponent.strength--;
                if (opponent.strength <= 0) {
                    model.removeObjectAndSend(playerID, pos);
                    view.displayMessage(playerID, ActivityGame.selfreferenceActivityGame.getString(R.string.fight_battle_won));
                } else {
                    view.displayOpponent(playerID, character.getStats(), PlaceableType.PLACEABLE_OPPONENT, opponent.getStats());
                    view.displayFootnote(playerID, ActivityGame.selfreferenceActivityGame.getString(R.string.fight_round_won));
                }
            } else {
                character.strength--;
                character.strength--;
                character.strength--;
                if (character.strength <= 0) {
                    controller.delegatedConsequences.youAreDead(playerID, ActivityGame.selfreferenceActivityGame.getString(R.string.fight_dead));
                } else {
                    view.displayOpponent(playerID, character.getStats(), PlaceableType.PLACEABLE_OPPONENT, opponent.getStats());
                    view.displayFootnote(playerID, ActivityGame.selfreferenceActivityGame.getString(R.string.fight_round_lost));
                }
            }
        }
        //deal with action flee
        else if (type == ActionTypeFight.FLEE) {
            boolean successful = Math.random() < 0.8;
            if (successful) {
                controller.getView().displayFootnote(playerID, ActivityGame.selfreferenceActivityGame.getString(R.string.fight_flee_successful));
                character.strength--;
                view.displayGame(playerID);
                int newPos = model.getRandomLegalNeighbourPosition(character.getPosition());
                controller.attemptMove(playerID, newPos, character.getPosition());
                if (character.strength <= 0) {
                    controller.delegatedConsequences.youAreDead(playerID,ActivityGame.selfreferenceActivityGame.getString(R.string.fight_dead));
                } else {
                    view.displayFootnote(playerID, ActivityGame.selfreferenceActivityGame.getString(R.string.fight_flee_successful));
                }
            } else {
                controller.getView().displayFootnote(playerID, ActivityGame.selfreferenceActivityGame.getString(R.string.fight_flee_unsuccessful));
                view.displayOpponent(playerID, character.getStats(), PlaceableType.PLACEABLE_OPPONENT, opponent.getStats());
                character.strength--;
                if (character.strength <= 0) {
                    controller.delegatedConsequences.youAreDead(playerID,ActivityGame.selfreferenceActivityGame.getString(R.string.fight_dead));
                } else {
                    view.displayOpponent(playerID, character.getStats(), PlaceableType.PLACEABLE_OPPONENT, opponent.getStats());
                    view.displayFootnote(playerID, ActivityGame.selfreferenceActivityGame.getString(R.string.fight_flee_unsuccessful));
                }
            }
        }
    }
}
