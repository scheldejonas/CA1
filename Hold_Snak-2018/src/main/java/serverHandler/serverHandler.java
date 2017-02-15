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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author William Pfaffe
 */
public class serverHandler extends Thread {

    private ArrayList<clientEnt> bingo;

    public boolean userExistsUsername(String username) {
        for (clientEnt c : bingo) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean userExistsSocket(Socket client) {
        for (clientEnt c : bingo) {
            if (c.getSock().equals(client)) {
                return true;
            }
        }
        return false;
    }

    public void startServer(String host, int port) throws IOException {
        // Create a new unbound socket
        ServerSocket socket = new ServerSocket();
        // Bind to a port number
        socket.bind(new InetSocketAddress(host, port));

        System.out.println("Server listening on port " + port);

        // Wait for a connection
        Socket connection;
        while ((connection = socket.accept()) != null) {
            // Handle the connection in the #handleConnection method below
            handleConnection(connection);

            // Now the connection has been handled and we've sent our reply
            // -- So now the connection can be closed so we can open more
            //    sockets in the future
        }
        System.out.println("HandleConnection has ended!");
    }

    public void sendMessageAll(String msg) throws IOException {
        for (clientEnt c : bingo) {
            sendMessage(msg, c.getSock());

        }
        System.out.println("Message has been sent to all users!");
    }

    public clientEnt returnClientUsername(String username) {
        clientEnt ent = null;
        for (clientEnt c : bingo) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                ent = c;
                break;
            }
        }
        return ent;
    }

    public clientEnt returnClientSocket(Socket sock) {
        clientEnt ent = null;
        for (clientEnt c : bingo) {
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
        for (clientEnt c : bingo) {
            if (!c.getUsername().equalsIgnoreCase(username)) {
                sendMessage("UPDATE#" + username, c.getSock());
            }

        }
        System.out.println("Message has been sent to all users!");
    }

    public void deleteUser(String username) throws IOException {
        clientEnt cc = null;
        for (clientEnt c : bingo) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                cc = c;
            } else {
                sendMessage("DELETE#" + username, c.getSock());
            }
        }
        bingo.remove(cc);
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
                    client = new clientEnt(connection, username);
                    bingo.add(client);
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
                        sendMessageAll(line);
                    } else {
                        clientEnt ent = returnClientUsername(username);
                        if (ent == null) {
                            sendMessage("User does not exist!", connection);
                            return;
                        }
                        sendMessage("MSG#" + username + "#" + message, returnClientUsername(username).getSock());
                    }
                }
                break;
        }
    }

    public String removeClient(Socket s) {
        clientEnt ent = null;
        String user = null;
        for (clientEnt c : bingo) {
            if (c.getSock().equals(s)) {
                ent = c;
                user = c.getUsername();
                break;
            }

        }
        bingo.remove(ent);
        return user;
    }

    public void handleConnection(Socket connection) throws IOException {

        if (bingo == null) {
            bingo = new ArrayList();
        }
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
//                        System.out.println("Waiting for message...");
                        String msg = readMessage(connection);
                        System.out.println("Received Message: " + msg);
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

    public String getUsers(Socket s) {
        String result = "OK";
        for (clientEnt c : bingo) {
            result += "#" + c.getUsername();
        }
        return result;
    }

    public void sendToClient(Socket conn) throws IOException {
        OutputStream output = conn.getOutputStream();
        InputStream input = conn.getInputStream();
        if (bingo == null) {
            bingo = new ArrayList();
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
            startServer("10.8.117.67", 8081);
        } catch (IOException ex) {
            Logger.getLogger(serverHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
