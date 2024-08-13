package com.herrrau.crawlspace;

public class PlaceableSage extends Placeable {

    public PlaceableSage() {
        setDisappearsAfterInteraction(false);
        setType(PlaceableType.PLACEABLE_SAGE);
    }

    @Override
    public String getStats() {
        return ActivityGame.selfreferenceActivityGame.getString(R.string.description_sage);
    }

    public String getPieceOfWisdom() {
        return "Alas...";
    }

}
