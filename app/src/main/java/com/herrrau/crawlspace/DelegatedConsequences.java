package com.herrrau.crawlspace;

public class DelegatedConsequences {
    CentralModel model;
    GameView view;
    CentralController controller;

    public DelegatedConsequences(CentralModel model, GameView view, CentralController controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    public void setOffTrap(int playerID, int position) {
        model.removeObjectAndSend(playerID, position);
        boolean isRegularTrap = Math.random()<0.5;
        if (isRegularTrap) {
            view.displayMessage(playerID, ActivityStart.getAppResources().getString(R.string.consequence_trap));
            model.getCharacter(playerID).strength--;
            if (model.getCharacter(playerID).strength < 1) {
                this.youAreDead(playerID, ActivityStart.getAppResources().getString(R.string.consequence_trap_death));
                //view.displayGameOver(playerID, "Sorry...  a trap killed you.");
            }
        } else {
            this.doATeleport(playerID);
        }
    }

    public void anObjectIsFound(int playerID, int position) {
        if (model.getPlaceableObjectAt(position) == null) {
            Placeable obj = new PlaceableChest();
            model.setObjectAndSend(position, obj);
            view.displayStats(playerID, model.getCharacter(playerID).getStats(), obj.getType(), obj.getStats());
        }
    }

    public void aChestOfItemsIsFound(int playerID, PlaceableChest obj) {
        String stats = obj.getStats();
        int pos = model.getCharacter(playerID).getPosition();
        model.removeObjectAndSend(playerID, pos);
        model.getView().displayPlaceableType(playerID, ActivityStart.getAppResources().getString(R.string.consequence_chest), obj.getType(), stats);
        obj.interactWith(model.getCharacter(playerID)); // ONLY case of this being called
    }

    public void youAreDead(int playerID, String message) {
        controller.attemptRemoveCharacter(playerID);
        view.displayGameOver(playerID, message);
    }

    public void anOpponentAppears(int playerID, int position) {
        model.removeObjectAndSend(playerID, position);
        model.setObjectAndSend(position, new PlaceableOpponent());
        view.displayOpponent(playerID,
                model.getCharacter(playerID).getStats(),
                model.getPlaceableObjectAt(position).getType(),
                model.getPlaceableObjectAt(position).getStats()
        );
    }

    public void swapSetTileForNewOpenTile(int playerID, int position) {
        Tile oldTile = model.tiles[position];
        Placeable obj = oldTile.getObject();
        Tile newTile = new Tile(0);
        newTile.setObject(obj);
        model.adjustNumberOfOpenEnds(newTile, position, oldTile);
        model.setTileAndSend(position, newTile);
        view.displayGame(playerID);
    }



    public void doATeleport(int playerID) {
        int random = (int) (Math.random() * model.columns * model.rows);
        //look for tile without object, can be set or unset
        while ((model.tiles[random] != null && model.tiles[random].getObject() != null) || model.getCharacter(playerID).getPosition() == random) {
            //while (model.objects[random]!=null || model.getCharacter(playerID).getPosition()==random) {
            random = (int) (Math.random() * model.columns * model.rows);
        }
        model.makeMoveAndSend(playerID, random); //## what about adjusting exits?
        view.displayGame(playerID);
        controller.checkForObjectEncounter(playerID, random);
    }


}
