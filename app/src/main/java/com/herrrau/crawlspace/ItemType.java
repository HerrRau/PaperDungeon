package com.herrrau.crawlspace;

public enum ItemType {
    ITEM_OPENER,
    ITEM_POTION,
    ITEM_MAP,
    ITEM_TRAP,
    ITEM_GHOST_WALK,
    ITEM_INVISIBILITY,
    ITEM_GOLD,
    ITEM_INVALUABLE_EXPERIENCE,
    ITEM_LESSONS_LEARNED;

    String getName() {
        switch (this) {
            case ITEM_OPENER:
                return ActivityStart.getAppResources().getString(R.string.item_opener);
            case ITEM_TRAP:
                return ActivityStart.getAppResources().getString(R.string.item_trap);
            case ITEM_GHOST_WALK:
                return ActivityStart.getAppResources().getString(R.string.item_ghost_walk);
            case ITEM_INVALUABLE_EXPERIENCE:
                return ActivityStart.getAppResources().getString(R.string.item_experience);
            case ITEM_GOLD:
                return ActivityStart.getAppResources().getString(R.string.item_gold);
            case ITEM_LESSONS_LEARNED:
                return ActivityStart.getAppResources().getString(R.string.item_lessons);
            case ITEM_POTION:
                return ActivityStart.getAppResources().getString(R.string.item_potion);
            case ITEM_MAP:
                return ActivityStart.getAppResources().getString(R.string.item_clairvoyance);
            case ITEM_INVISIBILITY:
                return ActivityStart.getAppResources().getString(R.string.item_invisibility);
        }
        return "";
    }


    boolean isAvailabe(int index) {
        return values()[index].isAvailabe();
    }
    boolean isAvailabe() {
        switch (this) {
            case ITEM_OPENER:
                return true;
            case ITEM_TRAP:
                return true;
            case ITEM_GHOST_WALK:
                return true;
            case ITEM_POTION:
                return true;
            case ITEM_MAP:
                return true;
            case ITEM_INVISIBILITY:
                return true;
        }
        return false;
    }


    String getDescription() {
        String description = "";
        switch (this) {
            case ITEM_OPENER:
                description = ActivityStart.getAppResources().getString(R.string.item_opener_description);
                break;
            case ITEM_MAP:
                description = ActivityStart.getAppResources().getString(R.string.item_clairvoyance_description);
                break;
            case ITEM_GHOST_WALK:
                description = ActivityStart.getAppResources().getString(R.string.item_ghost_walk_description);
                break;
            case ITEM_POTION:
                description = ActivityStart.getAppResources().getString(R.string.item_potion_description);
                break;
            case ITEM_TRAP:
                description = ActivityStart.getAppResources().getString(R.string.item_trap_description);
                break;
            default:
                description = getName();
                break;
        }
        return description;
    }

    boolean canBeUsedInPresenceOfOpponent() {
        switch (this) {
            case ITEM_POTION:
                return true;
            case ITEM_INVISIBILITY:
                return true;
            case ITEM_OPENER:
                return false; // legt ja ein neues tile, und damit verschwaenden auch alle Objekte dort
        }
        return false;
    }

    String getFileName() {
        switch (this) {
            case ITEM_OPENER:
                return "item_opener";
            case ITEM_TRAP:
                return "item_trap";
            case ITEM_GHOST_WALK:
                return "item_ghost_walk";
            case ITEM_GOLD:
                return "item_gold";
            case ITEM_INVALUABLE_EXPERIENCE:
                return "item_invaluable_experience";
            case ITEM_LESSONS_LEARNED:
                return "item_lessons_learned";
            case ITEM_POTION:
                return "item_potion";
            case ITEM_MAP:
                return "item_map";
            case ITEM_INVISIBILITY:
                return "item_invisibility";
        }
        return "";
    }


    static ItemType getItemType(int id) {
        return values()[id];
    }
}
