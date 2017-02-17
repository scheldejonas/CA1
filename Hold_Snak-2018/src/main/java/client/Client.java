package client;

import client.ClientGUI;
import exception.UsernameInUseException;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A client which connects to a server.
 */
public class Client {

    private final String host;
    private final int port;
    private Socket clientSocket;
    private ClientGUI gui;
    private String name;

    public Client(String host, int port, String name, ClientGUI gui) throws IOException, UsernameInUseException {
        this.host = host;
        this.port = port;
        this.name = name;
        this.gui = gui;
        String msg = open();
        if (msg.contains("Username is already in")) {
            throw new UsernameInUseException(msg);
        }
        handleMessage(msg);
        readThread();
    }

    public String getName() {
        return name;
    }
    
    public boolean isConnected() {
        return clientSocket.isConnected();
    }
    
    public String open() throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(host, port));
        System.out.println("Client connected to server on port " + port);
        sendMessage("LOGIN#" + name);
        String msg = readMessage();
        if (msg.equals("FAIL")) {
            return "Username is already in use!\n" + 
                       "Please enter another and try again.";
        }
        return msg;
    }

    public void addGUI(ClientGUI gui) {
        this.gui = gui;
    }
    
    /**
     * Sends a message to the server by opening a socket, writing to the input and reading from the output.
     *
     * @param message The message to send
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        // Write to the server
        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(output, "UTF-8")), true);
        writer.println(message);
        writer.flush();
    }

    /**
     * Reads a message from the server, if connected.
     *
     * @return A message from the server.
     * @throws IOException
     */
    public String readMessage() throws IOException {
        // Read from the server
        InputStream input = clientSocket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        String fromServer;
        while ((fromServer = reader.readLine()) == null) {
            // Wait until the server says something interesting
        }
        return fromServer;
    }
    
    public void readThread() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (clientSocket != null && clientSocket.isConnected()) {
                        try {
                        //System.out.println("Waiting for repsonse...");
                            String message = (readMessage());
                            System.out.println("Received message: " + message);
                            handleMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
    
    public void handleMessage(String received) {
        System.out.println("[Client.handleMessage]: " + received);
        String[] split = received.split("#");
        if (split.length > 1) {
            String command = split[0];
            switch (command.toUpperCase()) {
                case "OK":
                    for (int i = 1; i < split.length; i++) {
                        gui.addUserToList(split[i]);
                        System.out.println("Adding user: " + split[i]);
                    }
                    break;
                
                case "UPDATE":
                    gui.addUserToList(split[1]);
                    break;
                    
                case "MSG":
                    String sender = split[1];
                    String msg = split[2];
                    String secretChar = " ";
                    if (msg.contains(secretChar)) {
                        gui.readMessage("*P* From " + sender + ": " + msg.replaceAll(secretChar, "") + System.lineSeparator());
                        break;
                    }
                    gui.readMessage(sender + ": " + msg + System.lineSeparator());
                    break;
                    
                case "DELETE":
                    gui.removeUserFromList(split[1]);
                    break;
                    
                default:
                    
                    break;
                
            }
        }
    }
    
    public void writeConsoleThread() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //sendMessage("Test message #" + count++);
                        Scanner scan = new Scanner(System.in);
                        sendMessage(scan.nextLine());
                        //System.out.println("Sent message to server....");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }
    
    public void writeThread() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                int milli = new Random().nextInt(2000) + 2000;
                long total = start + milli;
                int count = 0;
                while (true) {
                    if (total < System.currentTimeMillis()) {
                        try {
                            //sendMessage("Test message #" + count++);
                            String s = System.console().readLine();
                            sendMessage(s);
                            //System.out.println("Sent message to server....");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        start = System.currentTimeMillis();
                        milli = new Random().nextInt(2000) + 3000;
                        total = start + milli;
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    public static void main(String[] args) throws IOException, UsernameInUseException {
        Client client = new Client("83.95.174.124", 8081, "Tester", new ClientGUI());
        client.open();
        //System.out.println("1) Opened connection....");
        //client.sendMessage("LOGIN#TEST");
        //System.out.println("2) Sent message....");
        client.readThread();
        //client.writeThread();
        client.writeConsoleThread();
    }

}