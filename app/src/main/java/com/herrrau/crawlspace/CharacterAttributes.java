package com.herrrau.crawlspace;

import android.app.Application;

public enum CharacterAttributes  {
    NAME,
    STRENGTH,
    MAX_STRENGTH,
    AGILITY,
    MAX_DEPTH_REACHED,
    SOLO_EXCURSIONS;

    String getName() {
        switch(this) {
            case NAME: return ActivityStart.getAppResources().getString(R.string.attribute_name);
            case STRENGTH: return ActivityStart.getAppResources().getString(R.string.attribute_strength);
            case MAX_STRENGTH: return ActivityStart.getAppResources().getString(R.string.attribute_max_strength);
            case AGILITY: return ActivityStart.getAppResources().getString(R.string.attribute_agility);
            case MAX_DEPTH_REACHED: return ActivityStart.getAppResources().getString(R.string.attribute_max_depth);
            case SOLO_EXCURSIONS: return ActivityStart.getAppResources().getString(R.string.attribute_solo_excursions);
        }
        return "";
    }

    static String getName(int index) {
        if (index<values().length) return values()[index].getName();
        else return "n/a";
    }


}
