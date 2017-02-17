/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverHandler;

import server.clientEnt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author William Pfaffe
 */
public class serverHandler extends Thread {

    
    /*
    Store all incoming connections inside an ArrayList called arrClients, which 
    saves the username, and the socket used as they connect to the server
    */
    private ArrayList<clientEnt> arrClients;
    private String timeStamp = new SimpleDateFormat("dd-MM-yyyy --- HH:mm:ss").format(Calendar.getInstance().getTime());

    public boolean userExistsUsername(String username) {
        for (clientEnt c : arrClients) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
    
    /*
    Checks if the given socket exists inside the array @arrClients. 
    Returns a boolean, true in case the element already exists, false otherwise
    */

    public boolean userExistsSocket(Socket client) {
        for (clientEnt c : arrClients) {
            if (c.getSock().equals(client)) {
                return true;
            }
        }
        return false;
    }

    /*
    Starts the server. Given a host (IP) and the port which the server runs on. 
    Only once given a connection, the method @accept, which is a subclass from 
    the socket class, will return true, which calls the @handleConnection(Socket) method
    */
    
    public void startServer(String host, int port) throws IOException {
        ServerSocket socket = new ServerSocket();
        socket.bind(new InetSocketAddress(host, port));

        System.out.println(timeStamp + " Server listening on port " + port);

        Socket connection;
        while ((connection = socket.accept()) != null) {
            handleConnection(connection);
        }
        System.out.println(timeStamp + " HandleConnection has ended!");
    }
    
    
    
    public void sendMessageAll(String msg, Socket s) throws IOException {
        for (clientEnt c : arrClients) {
            if(!c.getSock().equals(s)){
                sendMessage(msg, c.getSock());
            }
        }
        System.out.println(timeStamp + " Message has been sent to all users!");
    }

    public clientEnt returnClientUsername(String username) {
        clientEnt ent = null;
        for (clientEnt c : arrClients) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                ent = c;
                break;
            }
        }
        return ent;
    }

    public clientEnt returnClientSocket(Socket sock) {
        clientEnt ent = null;
        for (clientEnt c : arrClients) {
            if (c.getSock().equals(sock)) {
                ent = c;
                break;
            }
        }
        return ent;
    }

    public void sendMessage(String message, Socket clientSocket) throws IOException {
        // Write to the server
        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output);
        writer.println(message);
        writer.flush();
    }

    public void sendUpdateUser(String username) throws IOException {
        for (clientEnt c : arrClients) {
            if (!c.getUsername().equalsIgnoreCase(username)) {
                sendMessage("UPDATE#" + username, c.getSock());
            }

        }
        System.out.println(timeStamp + " Message has been sent to all users!");
    }

    public void deleteUser(String username) throws IOException {
        clientEnt cc = null;
        for (clientEnt c : arrClients) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                cc = c;
            } else {
                sendMessage("DELETE#" + username, c.getSock());
            }
        }
        arrClients.remove(cc);
    }

    public void handleMessage(String line, Socket connection) throws IOException {
//        System.out.println("Preparing respons");
//        System.out.println("Handle message: 1");
        if (line.equalsIgnoreCase("exit")) {
            clientEnt cl = returnClientSocket(connection);
            if (cl == null) {
                connection.close();
                return;
            }
            deleteUser(cl.getUsername());
            connection.close();
            return;
        }

        if (!line.contains("#")) {
            sendMessage("Incorrect Command!", connection);
//            System.out.println("Handle  message  2");
            return;
        }

        clientEnt client;

        String[] split = line.split("#");

        String command = split[0];
        String username = split[1];

        switch (command) {
            case "LOGIN":
                if (userExistsUsername(username)) {
                    sendMessage("FAIL", connection);
                    connection.close();
                    return;
                } else if (username.equalsIgnoreCase("ALL")) {
                    sendMessage("Username invalid!", connection);
                } else if (username.equalsIgnoreCase("EVERYONE")) {
                    sendMessage("Username Invalid!", connection);
                } else {
                    sendMessage(getUsers(username), connection);
                    client = new clientEnt(connection, username);
                    arrClients.add(client);
                    sendUpdateUser(username);
                }
                break;
            case "MSG":
                if (!userExistsSocket(connection)) {
                    sendMessage("Please login first!", connection);
                    connection.close();
                    return;
                }
                if (split.length > 2) {
                    String message = split[2];
                    if (username.equalsIgnoreCase("ALL")) {
                        sendMessageAll("MSG#" + returnClientSocket(connection).getUsername() + "#" + message, connection);
                    } else {
                        clientEnt ent = returnClientUsername(username);
                        if (ent == null) {
                            sendMessage("User does not exist!", connection);
                            return;
                        }
                        sendMessage("MSG#" + "*P* From " + returnClientSocket(connection).getUsername() + "#" + message, returnClientUsername(username).getSock());
                    }
                }
                break;
        }
    }

    public String removeClient(Socket s) {
        clientEnt ent = null;
        String user = null;
        for (clientEnt c : arrClients) {
            if (c.getSock().equals(s)) {
                ent = c;
                user = c.getUsername();
                break;
            }

        }
        arrClients.remove(ent);
        return user;
    }

    public void handleConnection(Socket connection) throws IOException {

        if (arrClients == null) {
            arrClients = new ArrayList();
        }
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
//                        System.out.println("Waiting for message...");
                        String msg = readMessage(connection);
                        System.out.println(timeStamp + " Received Message: " + msg);
                        handleMessage(msg, connection);
                    } catch (IOException ex) {

                        String user = removeClient(connection);
                        if (user == null) {
                            break;
                        }
                        try {
                            deleteUser(user);
//                            sendMessageAll("MSG#ALL#User " + user + " has disconnected!");
                        } catch (IOException ex1) {
                            Logger.getLogger(serverHandler.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                        break;
                    }
                }
            }
        });
        t1.start();
//        // Read whatever comes in
//        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//        String line = reader.readLine();
//        String message = "";
//        
//        System.out.println("heyhey der er connected");
//        

    }

    public String getUsers(String username) {
        String result = "OK";
        for (clientEnt c : arrClients) {
            if(c.getUsername().equalsIgnoreCase(username)){
                continue;
            }
            result += "#" + c.getUsername();
        }
        return result;
    }

    public void sendToClient(Socket conn) throws IOException {
        OutputStream output = conn.getOutputStream();
        InputStream input = conn.getInputStream();
        if (arrClients == null) {
            arrClients = new ArrayList();
        }
        // Read whatever comes in
        String message = "";
        PrintStream writer = new PrintStream(output);
        writer.println("hej chris");
    }

    public String readMessage(Socket conn) throws IOException {
        // Read from the server
        InputStream input = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String fromServer;
        while ((fromServer = reader.readLine()) == null) {
            // Wait until the server says something interesting
        }
        return fromServer;
    }

    @Override
    public void run() {
        try {
            startServer("localhost", 8081);
        } catch (IOException ex) {
            Logger.getLogger(serverHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
