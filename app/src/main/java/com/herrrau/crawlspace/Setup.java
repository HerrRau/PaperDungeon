package com.herrrau.crawlspace;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

public class Setup {
    private static int standardCols = 7;
    private static int standardRows = 7;
    public static int serverPortNumber = 54000;
    public static String serverIP = "192.168.178.38";
    public static boolean useOldProtocolForServer = false;
    public static boolean useMockServerForOnePlayer = false;
    public final static String packageName = "com.herrrau.crawlspace"; //used to find resources programmatically
    public static Character activeCharacter = new Character();
    public static boolean strictMode = true;
    public static boolean debug = false;

    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    public static int getStandardRows() {
        return standardRows;
    }

    public static void setStandardRows(int standardRows) {
        Setup.standardRows = standardRows;
        System.out.println(" Setup standard row: " + standardRows);
    }

    public static int getStandardCols() {
        return standardCols;
    }

    public static void setStandardCols(int standardCols) {
        Setup.standardCols = standardCols;
        System.out.println(" Setup standard cols: " + standardCols);
    }

    public static void saveCharacterCreation(AppCompatActivity a, Character c) {
        SharedPreferences settings = a.getSharedPreferences("xxx", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        String characterString = c.getStats();
        editor.putString("char" + ActivityAllCharacters.characterFingerprints.size(), characterString);
        ActivityAllCharacters.characterFingerprints.add(characterString);
        editor.apply();
    }

    public static void loadStart(Context c) {
        //SharedPreferences settings = a.getSharedPreferences("xxx", Context.MODE_PRIVATE);
        settings = c.getSharedPreferences("xxx", Context.MODE_PRIVATE);
        editor = settings.edit();
        Setup.setStandardCols(settings.getInt("cols", Setup.getStandardCols()));
        Setup.setStandardRows(settings.getInt("rows", Setup.getStandardRows()));
        Setup.strictMode = settings.getBoolean("strict", Setup.strictMode);
        FactoryTile.setStyle(settings.getInt("style", 0));

        if (false)
        {
            activeCharacter = new Character();
            return;
        }
        activeCharacter = Character.getCharacterFromStats(
                settings.getString("char0",
                        new Character().getStats()
                )
        );
    }

    public static void loadServer(AppCompatActivity a) {
        //SharedPreferences settings = a.getSharedPreferences("xxx", Context.MODE_PRIVATE);
        Setup.setStandardCols(settings.getInt("cols", Setup.getStandardCols()));
        Setup.setStandardRows(settings.getInt("rows", Setup.getStandardCols()));
        Setup.serverPortNumber = settings.getInt("port", Setup.serverPortNumber);
    }
    public static void saveServer(int port) {
        editor.putInt("port", port);
    }

    static void loadSettings(AppCompatActivity a) {
        Setup.strictMode = settings.getBoolean("strict", true);
    }

    static void saveSettings(AppCompatActivity a, int cols, int rows, int style, boolean strictMode) {
        Setup.setStandardCols(cols);
        Setup.setStandardRows(rows);
        Setup.strictMode = strictMode;
        editor.putInt("cols", cols);
        editor.putInt("rows", rows);
        editor.putBoolean("strict", strictMode);
        editor.putInt("style", style);
        editor.apply();
        //editor.commit();
    }

    static void saveCharacters(AppCompatActivity a, LinkedList<String> characterList) {
        for (int i = 0; i < 10; i++) {
            editor.remove("char" + i);
        }
        for (int i = 0; i < characterList.size(); i++) {
            String s = characterList.get(i);
            editor.putString("char" + i, characterList.get(i));
        }
        editor.apply();
    }
    static void saveActiveCharacterAs0() {
        editor.putString("char0", activeCharacter.getStats());
        editor.apply();
    }

    static LinkedList loadCharacters(AppCompatActivity a) {
        LinkedList characterList = new LinkedList<>();
        String first = settings.getString("char0", "");
        if (false && first.equals("")) {
            editor.putString("char0", Setup.activeCharacter.getStats());
            editor.apply();
        }
        for (int i = 0; i < 10; i++) {
            String s = settings.getString("char" + i, "");
            if (!s.equals("")) {
                characterList.addLast(s);
                System.out.println("Character " + i + " added.");
            } else {
                break;
            }
        }
        return characterList;
    }

}


