package client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
 
/**
 * A client which connects to a server.
 */
public class Client {
 
    private final String host;
    private final int port;
    private Socket clientSocket;
 
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        readThread();
    }
 
    public void open() throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(new InetSocketAddress(host, port));
        System.out.println("Client connected to server on port " + port);
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
        PrintWriter writer = new PrintWriter(output);
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
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
                    try {
                        //System.out.println("Waiting for repsonse...");
                        System.out.println(readMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
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
 
    public static void main(String[] args) throws IOException {
        Client client = new Client("83.95.174.124", 8081);
        client.open();
        //System.out.println("1) Opened connection....");
        //client.sendMessage("LOGIN#TEST");
        //System.out.println("2) Sent message....");
        client.readThread();
        //client.writeThread();
        client.writeConsoleThread();
    }
 
}