package com.herrrau.crawlspace;

public class ItemPack {
    int[] amounts;

    public ItemPack() {
        amounts = new int[ItemType.values().length];
    }

    public static ItemPack getRandomTreasureTrove() {
        ItemPack treasure = new ItemPack();
        if (Setup.strictMode) {
            //start with one random type
            int random = (int) (Math.random()*treasure.amounts.length-1);
            // switch if it is not available
            while (!ItemType.values()[random].isAvailabe()) {
                random = (int) (Math.random()*treasure.amounts.length-1);
            }
            treasure.amounts[random] = 1;
        } else {
            //for each item type
            for (int i = 0; i < treasure.amounts.length; i++) {
                //pick a random amount
                //treasure.amounts[i] = (int) (Math.random() * 6 - 3);
                treasure.amounts[i] = (int) (Math.random() * 3 + 3);
                //set to 0 if unavailable
                if (treasure.amounts[i] < 0) treasure.amounts[i] = 0;
                //set to 0 if amount <0
                if (!ItemType.values()[i].isAvailabe()) treasure.amounts[i] = 0;
            }
        }
        return treasure;
    }

    public static ItemPack createItemPackFromString(String stats) {
        String[] data = stats.split("#");
        ItemPack pack = new ItemPack();
        for (int i = 0; i < ItemType.values().length; i++) {
            pack.amounts[i] = Integer.parseInt(data[i]);
        }
        return pack;
    }

    public static String createItemPackDescriptionFromString(String stats) {
        String[] data = stats.split("#");
        String result = "";
        for (int i = 0; i < ItemType.values().length; i++) {
            result = result + "name" + ": "+ Integer.parseInt(data[i]) + "\n";
        }
        return result;
    }

    public void mergeWith(ItemPack treasure) {
        for (int i = 0; i < amounts.length; i++) {
            amounts[i] += treasure.amounts[i];
        }
    }


    public void changeItemAmount(ItemType type, int change) {
        for (int i = 0; i < ItemType.values().length; i++) {
            if (type.equals(ItemType.values()[i])) {
                amounts[i]+=change;
                return;
            }
        }
    }


    public int getNumberOfItems(ItemType type) {
        for (int i = 0; i < amounts.length; i++) {
            if (ItemType.values()[i] == type) return amounts[i];
        }
        return 0; //unknown item
    }

    public String getItems() {
        String result = "";
        for (int a : amounts) {
            result += a + "#";
        }
        return result;
    }
}
