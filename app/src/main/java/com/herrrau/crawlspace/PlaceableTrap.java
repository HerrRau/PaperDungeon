package com.herrrau.crawlspace;

public class PlaceableTrap extends Placeable {

    public PlaceableTrap(int visibleTo) {
        setType(PlaceableType.PLACEABLE_TRAP);
        setUsedAutomatically(true);
        setVisibleOnlyTo(visibleTo);
        setDisappearsAfterInteraction(true);
    }


}



