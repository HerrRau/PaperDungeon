package com.herrrau.crawlspace;

public class OldProtocol {


    public static String translateMessageFromServer(String messageBody) {
        char intro = messageBody.charAt(0);
        if (intro == 'H') { //"Hi, 0" -> "Hi, 0:4:6:7" bzw "Hi, 0"
            return messageBody+":4:11:10"; //hardcoded, hardly useful
        } else if (intro == 'n') { //nachricht:text:11 ->m:text:11
            return "m:" + messageBody.substring(9);
        } else if (messageBody.substring(0, 4).equals("raum")) { // raum:1001turm:82 -> "t:82:1001t"
            String[] data = messageBody.split(":");
            if (data[1].substring(0, 4).equals("burl")) data[1] = "unknown";
            if (data[1].length()>5) data[1] = data[1].substring(0, 5); // 1001turm -> 1001t
            return "t:" + data[2] + ":" + data[1];
        } else if (messageBody.substring(0, 4).equals("spie")) { // spieler:1:2 (id 1 nach pos2) -> c:1:2:-1
            String[] data = messageBody.split(":");
            return "c:" + data[1] + ":" + data[2] + ":" + "-1";
        } else return messageBody;
    }

}
