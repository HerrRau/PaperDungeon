package com.herrrau.crawlspace;

public class FactoryObject {

    String prefix = "gen3_";

    public FactoryObject() {
    }

    boolean useVariants = true;

    public int getID(String name) {
/*
        if (useVariants) {
            if (name.equals("placeable_opponent")) {
                if (Math.random()<0.5) {
                    name = name + 'c';
                }
            }
        }
*/
        int id = ActivityGame.selfreferenceActivityGame.getResources().getIdentifier(prefix+name, "drawable", Setup.packageName);
        if (id<0) id = ActivityGame.selfreferenceActivityGame.getResources().getIdentifier(prefix+"potion", "drawable", Setup.packageName);
        return id;
    }


}
