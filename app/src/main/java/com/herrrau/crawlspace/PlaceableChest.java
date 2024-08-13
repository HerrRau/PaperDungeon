package com.herrrau.crawlspace;

public class PlaceableChest extends Placeable {

    ItemPack treasure;

    public PlaceableChest() {
        treasure = ItemPack.getRandomTreasureTrove();
        setType(PlaceableType.PLACEABLE_CHEST);
        setDisappearsAfterInteraction(true);
    }


    @Override public String getStats() {
        return treasure.getItems();
    }

    public void interactWith(Character c) {
        c.backpack.mergeWith(treasure);
    }


}
