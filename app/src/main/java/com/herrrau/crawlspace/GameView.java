package com.herrrau.crawlspace;

public interface GameView {

    public void setupLevel(int columns, int rows, int numberOfPlayers, int depth, boolean useLimitedVision);
    public void displayGame(int playerID);
    public void showCharacter(int id, int position, int comesFrom);
    public void showTile(int id, String type);
    public void showSound(int id, String name);
    public void showEffect(int id, int position, EffectType type, String argument);
    public void displayStats(int playerID, String stats, PlaceableType type, String typeStats);
    public void displayOpponent(int playerID, String playerStats, PlaceableType opponent, String opponentStats);
    public void displayGameOver(int playerID, String message);
    public void showObject(int position, PlaceableType t);
    public void showObjectTo(int playerID, int position, PlaceableType t); //only for CentralAdapter
    public void removeObject(int position);
    public void removeCharacter(int id);
    public void displayFootnote(int playerID, String s);
    public void displayMessage(int playerID, String s);
    public void displayLocalTimer(int playerID, int seconds);
    public void displayPlaceableType(int playerID, String message, PlaceableType type, String stats);






}
