package com.herrrau.crawlspace;

import java.io.*;
import java.net.*;

public class Client extends Thread {
    String hostName = "";
    int portNumber = -1;
    Socket socket;
    PrintWriter out;
    BufferedReader in;

    public Client(String ipAdresse, int port ) //throws IOException
    {
        super();
        hostName = ipAdresse;
        portNumber = port;
    }

    public void starteClient() {
        try {
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
            System.out.println("C Verbindung zum Server hergestellt...");
            this.start();
        } catch (UnknownHostException e) {
            System.err.println("Client: Don't know about host " + hostName);
            System.out.println("Client: Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Client: Couldn't get I/O for the connection to " +
                    hostName);
            System.out.println("Client: Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Client: "+e);
        }
    }

    //final
    public void sendeAnServer(String s) //throws IOException
    {
        out.println(s);
    }

    protected void empfangeVonServer(String s) //throws IOException
    {
        System.out.println("Client empfaengt von Server: " + s);
    }

    public void beendeVerbindung()
    {
        sendeAnServer("Bye?");
    }

    final public void run() //throws IOException
    {
        try {
            String fromServer;
            while((fromServer = in.readLine()) != null) {
                empfangeVonServer(fromServer);
                if (fromServer.equals("Bye!"))
                {
                    socket.close();
                    System.out.println("Client: has closed socket");
                    break;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }

    }


}
