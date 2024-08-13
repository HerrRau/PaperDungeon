package com.herrrau.crawlspace;

public class Character {

    private int position;
    int strength, maxStrength, agility, maxDepth, numberOfSoloExcursions;
    String name;
    boolean isActive = true;
    private int startingPosition;
    private int comesFrom = 999;
    ItemPack backpack;
    ActionPack actionpack;

    public Character() {
        maxDepth = 0;
        strength = 16;
        maxStrength = 18;
        agility = 10;
        name = "Glorietta";
        if (Setup.strictMode) {
            //backpack = new ItemPack(); //empty
            backpack = ItemPack.getRandomTreasureTrove();
        } else {
            backpack = ItemPack.getRandomTreasureTrove();
        }
        actionpack = new ActionPack();
    }

    public void changeItemAmount(ItemType type, int amount) {
        backpack.changeItemAmount(type, amount);
    }

    private String getItems() {
        return backpack.getItems();
    }

    public String getStats() {
        return name + "#" + strength + "#" + maxStrength + "#" + agility + "#" + maxDepth + "#" + numberOfSoloExcursions + "#" + getItems() + actionpack.getActions();
    }

    public static Character getCharacterFromStats(String stats) {
        //Reihenfolge muss der Reihenfolge der Enums entsprechen!
        Character c = new Character();
        String[] data = stats.split("#");
        c.name = data[0];
        c.strength = Integer.parseInt(data[1]);
        c.maxStrength = Integer.parseInt(data[2]);
        c.agility = Integer.parseInt(data[3]);
        c.maxDepth = Integer.parseInt(data[4]);
        c.numberOfSoloExcursions = Integer.parseInt(data[5]);
        c.backpack = new ItemPack();
        for (int i = 0; i < ItemType.values().length; i++) {
            c.backpack.amounts[i] = Integer.parseInt(data[6 + i]);
        }
        c.actionpack = new ActionPack();
        for (int i = 0; i < CharacterAttributes.values().length; i++) {
            //c.backpack.amounts[i] = Integer.parseInt(data[5+ItemType.values().length+i]);
        }
        return c;
    }

    public void setStartingPosition(int position) {
        this.startingPosition = position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setComesFrom(int comesFrom) {
        this.comesFrom = comesFrom;
    }

    public int getComesFrom() {
        return comesFrom;
    }

    public int getNumberOfItems(ItemType type) {
        return backpack.getNumberOfItems(type);
    }

    public void increaseStrength(int i) {
        strength += i;
        if (strength>maxStrength) strength = maxStrength;
    }

    static String getFormattedCharacterDataBrief(String characterFingerprint) {
        String [] characterTraits = characterFingerprint.split("#");
        String result = CharacterAttributes.getName(0)+": "+characterTraits[0] + "\n";
        for (int i=1; i<characterTraits.length-1; i++) {
            if (characterTraits[i].equals("0") && i>= CharacterAttributes.values().length) continue; //skip 0
            result = result + characterTraits[i]+", ";
        }
        result = result + characterTraits[characterTraits.length-1]+"\n\n";
        return result;
    }

    static String getFormattedCharacterDataFull(String characterFingerprint) {
        String [] characterTraits = characterFingerprint.split("#");
        String result = "";
        for (int i=0; i<CharacterAttributes.values().length; i++) {
            result = result + CharacterAttributes.getName(i) +": "+ characterTraits[i]+"\n";
        }
        result = result + "\n";
        for (int i=0; i<ItemType.values().length; i++) {
            String nextValue = characterTraits[i+CharacterAttributes.values().length];
            if (nextValue.equals("0")) continue;
            result = result + ItemType.values()[i].getName() +": "+ characterTraits[i+CharacterAttributes.values().length]+"\n";
        }
        result = result + "\n\n";
        return result;
    }




}
