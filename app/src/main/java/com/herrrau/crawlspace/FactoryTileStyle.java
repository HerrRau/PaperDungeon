package com.herrrau.crawlspace;

public enum FactoryTileStyle {
    DOT_GREY,
    ORIGINAL,
    IMPROVED,
    IMPROVED_ALPHA,
    RAW,
    TILE,
    ALPHA,
    RANDOM; //muss letztes sein

    public String getName() {
        switch (this) {
            case DOT_GREY:
                return "Dot Grey";
            case ORIGINAL:
                return "The Original";
            case IMPROVED:
                return "Slightly Improved";
            case IMPROVED_ALPHA:
                return "Improved Alpha";
            case RAW:
                return "RAW";
            case TILE:
                return "Tile";
            case ALPHA:
                return "Alpha";
            case RANDOM:
                return "Random";
        }
        return "";
    }

    public String getPrefix() {
        switch (this) {
            case DOT_GREY:
                return "dot_grey_";
            case ORIGINAL:
                return "original_tile_";
            case IMPROVED:
                return "improved_tile_";
            case IMPROVED_ALPHA:
                return "improved_alpha_tile_";
            case RAW:
                return "raw_tile_";
            case TILE:
                return "tile_";
            case ALPHA:
                return "alpha_tile_";
            case RANDOM:
                return getRandomStyle().getPrefix();
        }
        return ""; // bei RANDOM
    }

    private FactoryTileStyle getRandomStyle() {
        int random = (int) (Math.random() * values().length - 1);
        return values()[random];
    }

    public String getNameWithPrefix(String nameWithVariant) {
        return getPrefix() + nameWithVariant;
    }

    public String getNameWithVariantAndPrefix(String nameWithVariant) {
        switch (this) {
            case RANDOM:
                return getRandomStyle().getNameWithVariantAndPrefix(nameWithVariant);
            case TILE: {
                String exits = nameWithVariant.substring(0, 4);
                if (exits.equals("0000")) {
                    double random = Math.random();
                    if (random < 0.2) return getPrefix() + exits + 'c'; //one of: cefg
                    else if (random < 0.4) return getPrefix() + exits + 'e'; //one of: cefg
                    else if (random < 0.6) return getPrefix() + exits + 'f'; //one of: cefg
                    else if (random < 0.8) return getPrefix() + exits + 'g'; //one of: cefg
                }
            }
            case DOT_GREY: {
                String exits = nameWithVariant.substring(0, 4);
                if (exits.equals("0000")) {
                    double random = Math.random();
                    if (random < 0.5) return getPrefix() + exits + 'c'; //one of: c
                }
            }
        }
        return getPrefix() + nameWithVariant;
    }

    public char getVariant(int exits) {
        switch (this) {
            case RANDOM:
                return getRandomStyle().getVariant(exits);
            case TILE: {
                if (exits == 0) {
                    return 'c'; //one of: cefg
                }
            }
        }
        return 'n';
    }
}

