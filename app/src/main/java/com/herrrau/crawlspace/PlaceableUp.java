package com.herrrau.crawlspace;

public class PlaceableUp extends Placeable {

    public PlaceableUp() {
        setDisappearsAfterInteraction(false);
        setType(PlaceableType.PLACEABLE_UP);
    }

    public void interactWith(Character c) {
        System.out.println("UP USED");
    }

}


