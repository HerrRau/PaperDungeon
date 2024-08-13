package com.herrrau.crawlspace;


import java.net.*;
import java.io.*;

//wg IP
import java.util.Enumeration;
import java.net.NetworkInterface;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerMultiple extends Thread
{
    protected int portNumber = 54000;
    protected ServerSocket serverSocket;

    protected Socket[] clientSocket;
    protected boolean[] clientsToIgnore;


    protected PrintWriter[] out;
    protected BufferedReader[] in;

    protected int anzahlAktuell;
    boolean serverAktiv;

    public ServerMultiple() //throws IOException
    {
        this(4);
    }

    public ServerMultiple(int anzahl) //throws IOException
    {
        serverAktiv = true;
        anzahlAktuell = 0;

        clientSocket = new Socket[anzahl];
        clientsToIgnore = new boolean[anzahl];
        for (int i = 0; i < clientsToIgnore.length; i++) {
            clientsToIgnore[i] = false;
        }
        out = new PrintWriter[anzahl];
        in = new BufferedReader[anzahl];
    }

    public void starteServer() {
        start();
    }

    public void sendeAnClient(int nr, String s) {
        if (clientsToIgnore[nr]) return;
        if (out[nr] == null) {
            print("ServerMultiple: connection does not exist");
            return; // heisst: es gibt weniger Clients, als vorgesehen!!!!!!!!!
        }
        out[nr].println(s);
    }

    public void sendeAnAlle(String s) {
        for (int i = 0; i < clientSocket.length; i++) {
            sendeAnClient(i, s);
        }
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(portNumber);
            while (anzahlAktuell < clientSocket.length) {
                clientSocket[anzahlAktuell] = serverSocket.accept();
                out[anzahlAktuell] = new PrintWriter(clientSocket[anzahlAktuell].getOutputStream(), true);
                in[anzahlAktuell] = new BufferedReader(new InputStreamReader(clientSocket[anzahlAktuell].getInputStream()));

                Thread t = new Thread() {
                    int nummer = anzahlAktuell;

                    public void run() {
                        print("ServerMultiple: Thread for client "+nummer+" has started.");
                        String inputLine;
                        try {
                            while ((inputLine = in[nummer].readLine()) != null && serverAktiv) {
                                empfangeVonClient(nummer, inputLine);

                                if (inputLine.equals("Bye?")) {
                                    sendeAnClient(nummer, "Bye!");
                                    verarbeiteAbmeldung(nummer);
                                    break;
                                }
                            }
                            print("ServerMultiple: Thread for client " + nummer + " has run its course.");
                        } catch (IOException e) {
                            print("ServerMultiple: Exception caught when trying to listen on port "
                                    + portNumber + " or listening for a connection");
                            print("#1" + e.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace(System.out);
                            print("ServerMultiple: Anonymous Thread in ServerMultiple: Fehler.");
                            print("#2" + e);
                        }
                    }

                };
                verarbeiteAnmeldung(anzahlAktuell);
                anzahlAktuell++;
                t.start();
            }
            print("ServerMultiple: Server closes because everybody's here.");
            serverSocket.close(); // wenn alle erwarteten Clients da sind, schliesse Socket
        } catch (IOException e) {
            print("ServerMultiple: Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            print("#3" + e.getMessage()); //"Socket closed"
        } catch (Exception e) {
            e.printStackTrace(System.out);
            print("ServerMultiple: Fehler!");
            print("#4 " + e);
        }

    }

    public void stoppeServer() {
        try {
            if (serverAktiv) {
                sendeAnAlle("Bye!");
                serverAktiv = false;
                serverSocket.close(); // wahrscheinlich schon geschlossen, wenn alle erwarteten Spieler da waren;
                print("ServerMultiple: Server is closing down.");

                //close all client sockets
                for(Socket s : clientSocket) {
                    if (s!=null) {
                        print("ServerMultiple: client socket closed");
                        s.close();
                    }
                }
            } else {
                print("ServerMultiple: Server was already shut down.");
            }
        } catch (Exception e) {
            print("ServerMultiple: Fehler!");
            print("#5 " + e);
        }
    }

    // von Unterklasse zu ueberschreiben
    public void empfangeVonClient(int nr, String s) {
        print("ServerMultiple: Client " + nr + " sendet: " + s);
    }

    // von Unterklasse zu ueberschreiben
    protected void verarbeiteAnmeldung(int id) {
        print("ServerMultiple: Client " + id + " ist angemeldet.");
    }

    // von Unterklasse zu ueberschreiben
    protected void verarbeiteAbmeldung(int id) {
        try {
            clientSocket[id].close();
            print("ServerMultiple: Client " + id + " ist abgemeldet.");
        } catch (Exception e) {
            print(e.toString());
        }
    }

    protected String getAddresses() {
        String result = "";
        String ip;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1
                if (iface.isLoopback()) {
                    continue;
                }
                // filters out inactive interfaces
                if (!iface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    ip = addr.getHostAddress();
                    result = result + iface.getDisplayName() + " " + ip + "\n";
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    protected boolean verbose = true;

    protected void print(String s) {
        if (verbose) {
            System.out.println(s);
        }
    }

    public int getNumberOfActiveClients() {
        if (clientSocket==null) return 0;
        int i=clientSocket.length;
        for (Socket s : clientSocket) {
            if (s==null) {
                i--;
                print("null");
            }
            else if (s.isClosed()) {
                i--;
                print("SM info: is closed: "+s.getInetAddress().toString());
            }
            else {
                print("SM info: not closed: "+s.getInetAddress().toString());
                try {
                    //s.close();
                    //print("NOW closed: "+s.getInetAddress().toString());
                } catch (Exception e) {
                    print(e.toString());
                }
            }
        }
        return i;
    }

}