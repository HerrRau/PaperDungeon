package com.herrrau.crawlspace;

public class FactoryTile {

    private static FactoryTileStyle currentStyle;
    private boolean useVariants = true;

    public FactoryTile() {
        if (currentStyle==null) currentStyle = FactoryTileStyle.TILE; //can happen if invoked before loading settings
    }

    public static void setStyle(int styleSelector) {
        FactoryTile.currentStyle = FactoryTileStyle.values()[styleSelector];
    }

    public int getID(String name) {
        String newNameWithPrefix;
        if (useVariants && name.charAt(4) == 'n') {
            newNameWithPrefix = currentStyle.getNameWithVariantAndPrefix(name);
        } else {
            newNameWithPrefix = currentStyle.getNameWithPrefix(name);
        }
        int id = ActivityGame.selfreferenceActivityGame.getResources().getIdentifier(newNameWithPrefix, "drawable", Setup.packageName);
        //hier koennte man abfangen, wenn Bildname unbekannt
        return id;
    }

    public int getID(int exits, char variant) {
        String name = currentStyle.getPrefix() + String.format("%04d", exits) + variant;
        return ActivityGame.selfreferenceActivityGame.getResources().getIdentifier(name, "drawable", Setup.packageName);
    }

    public int getID(int exits) {
        String name = currentStyle.getPrefix() + String.format("%04d", exits) + currentStyle.getVariant(exits);
        return ActivityGame.selfreferenceActivityGame.getResources().getIdentifier(name, "drawable", Setup.packageName);
    }

    private char getVariant(int exits) {
        return currentStyle.getVariant(exits);
    }

}
