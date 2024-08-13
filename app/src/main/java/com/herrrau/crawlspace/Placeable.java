package com.herrrau.crawlspace;

public abstract class Placeable {

    //private CentralController controller;
    private PlaceableType type;
    private int visibleTo = -1;
    public boolean hasAppeared = true;
    private boolean usedAutomatically = false;
    private boolean disappearsAfterInteraction = true;

    public PlaceableType getType() { return type; }

    public void setType(PlaceableType p) { type = p; }
    public boolean isUsedAutomatically() { return usedAutomatically; }
    public void setUsedAutomatically(boolean b) { usedAutomatically = b;}
    public int getVisibleOnlyTo() { return visibleTo; }
    public void setVisibleOnlyTo(int id) { visibleTo = id; }
    public String getStats() { return ""; }
    public void interactWith(Character c) {}
    public boolean disappearsAfterInteraction() {
        return disappearsAfterInteraction;
    }
    public void  setDisappearsAfterInteraction(boolean b) {
        disappearsAfterInteraction = b;
    }
    public boolean hasAppeared() {
        return hasAppeared;
    }
    public void setHasAppeared(boolean b) {
        hasAppeared = b;
    }

}
