import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 * A server which simply just echoes whatever it receives
 */
public class ChatServer extends Thread{
 
    private final String host;
    private final int port;
    private ArrayList<clientEnt> bingo;
 
    public ChatServer(String host, int port) {
        this.host = host;
        this.port = port;
    }
 
    /**
     * Starts running the server.
     *
     * @throws IOException If network or I/O or something goes wrong.
     */
    public void startServer() throws IOException{
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
 
    /**
     * Handles a connection from a client by simply echoing back the same thing the client sent.
     *
     * @param connection The Socket connection which is connected to the client.
     * @throws IOException If network or I/O or something goes wrong.
     */
    public boolean userExists(String username){
        for(clientEnt c : bingo){
            if(c.getUsername().equalsIgnoreCase(username)){
            return true;
            }
        }
        return false;
    }
   
    public void sendMessageAll(String msg) throws IOException{
        for(clientEnt c : bingo){
            sendMessage(msg, c.getSock());
           
        }
        System.out.println("Message has been sent to all users!");
    }
   
    public void sendMessage(String message, Socket clientSocket) throws IOException {
        // Write to the server
        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output);
        writer.println(message);
        writer.flush();
    }
   
    private void handleMessage(String line, Socket connection) throws IOException{
//        System.out.println("Preparing respons");
//        System.out.println("Handle message: 1");
        if(!line.contains("#")){
            sendMessage("Incorrect Command!", connection);
//            System.out.println("Handle  message  2");
            return;
        }
       
        String[] msg = line.split("#");
        if(msg[0].equalsIgnoreCase("LOGIN")){
            if(this.userExists(msg[1]) == true){
                sendMessage("FAIL", connection);
//                System.out.println("handle message 3");
            }
            clientEnt cl = new clientEnt(connection, msg[1]);
            bingo.add(cl);
//            System.out.println("Handle message 4");
            sendMessage(getUsers(connection), connection);
//sendMessage("You have successfully connected to the chat! Info: " + connection.getRemoteSocketAddress().toString(), connection);
        }else if(msg[0].equalsIgnoreCase("MSG")){
                sendMessageAll(msg[1]);
                }else{
//            System.out.println("handle message 5");
            sendMessage("Unknown Command!", connection);
        }
        // Print the same line we read to the client
    }
   
    public String removeClient(Socket s){
        clientEnt ent = null;
        String user = null;
        for(clientEnt c : bingo){
            if(c.getSock().equals(s)){
                ent = c;
                user = c.getUsername();
                break;
            }
           
        }
        bingo.remove(ent);
        return user;
    }
   
    private void handleConnection(Socket connection) throws IOException {
       
        if(bingo == null){
            bingo = new ArrayList();
        }
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
//                        System.out.println("Waiting for message...");
                        String msg = readMessage(connection);
                        System.out.println("Received Message: " + msg);
                        handleMessage(msg, connection);
                    } catch (IOException ex) {
                        String user = removeClient(connection);
                        //Send message to all users that X has disconnected
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
   
    public String getUsers(Socket s){
        String result = "OK";
        for(clientEnt c : bingo){
            result += "#" + c.getUsername();
        }
        return result;
    }
   
    public void sendToClient(Socket conn) throws IOException{
        OutputStream output = conn.getOutputStream();
        InputStream input = conn.getInputStream();
        if(bingo == null){
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
   
    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer("10.50.130.234", 8081);
        // This method will block, forever!
        server.startServer();
       
        Thread threadWrite = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException ex) {
                    }
                }
              }
        });
    }
 
   
}