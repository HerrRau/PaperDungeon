package com.herrrau.crawlspace;

public class PlaceableOpponent extends Placeable {

    int strength= 3;
    static int numberCreated;
    String name;

    static String[] wikingersilben;
    static {
    wikingersilben = new String[12];
    wikingersilben[0] = "hra";
    wikingersilben[1] = "krim";
    wikingersilben[2] = "rolf";
    wikingersilben[3] = "rog";
    wikingersilben[4] = "nar";
    wikingersilben[5] = "ha";
    wikingersilben[6] = "kon";
    wikingersilben[7] = "wulf";
    wikingersilben[8] = "her";
    wikingersilben[9] = "gard";
    wikingersilben[10] = "brun";
    wikingersilben[11] = "hild";
    }

    public PlaceableOpponent() {
        this(ActivityGame.selfreferenceActivityGame.getString(R.string.placeable_opponent)+" "+ numberCreated, 3+(int)(Math.random()*4));
        numberCreated++;
        name = createName();
    }
    public PlaceableOpponent(String name, int strength) {
        this.name = name;
        this.strength = strength;
        setType(PlaceableType.PLACEABLE_OPPONENT);
        setDisappearsAfterInteraction(false);
    }

    //public String getStats() { return name+"#Strength "+strength; }
    public String getStats() { return name+"#"+ActivityGame.selfreferenceActivityGame.getString(R.string.attribute_strength)+" "+strength; }
    // no line breaks, careful with colon!!!

    private static String createName() {
        int wikingersilbenzahl = 3;
        String name = "";
        for (int i=0;i<wikingersilbenzahl;i=i+1) {
            name = name + wikingersilben[(int)(Math.random()*wikingersilben.length)];
        }
        return name.substring(0,1).toUpperCase() + name.substring(1,name.length());
    }



}
