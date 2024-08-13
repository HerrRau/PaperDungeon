package com.herrrau.crawlspace;

public interface LocalController {
    public void setView(GameView view);
    public void registerWithServer(String ipAddress, int portNumber);
    public void tryTile(int position);
    public void tryAction(ActionType action);
    public void tryActionFight(ActionTypeFight action);
    public void tryItem(int typeNumber);
    public void tryMessage(String message);
    public void tryInteractWithObject();
    public void tryMoveEast();
    public void tryMoveWest();
    public void tryMoveNorth();
    public void tryMoveSouth();
    public void tryDisplayStats();
    public void tryStart(); // new!


}
