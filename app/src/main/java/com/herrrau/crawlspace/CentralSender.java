package com.herrrau.crawlspace;

public interface CentralSender {
    void sendeAnAlle(String message);
    void sendeAnClient(int id, String message);

}
