
package server;

import entity.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import javafx.beans.InvalidationListener;


// Observable?? bingo bango
public class ChatServer implements Runnable
{
    private String host;
    private final int port = 8081;
    BlockingQueue<String> users;

    public ChatServer(String host) 
    {
        this.host = host;
    }
    
    
    //Startserver
    @Override
    public void run() 
    {
        try
        {
            ServerSocket socket = new ServerSocket();
            // Bind to a port number
            socket.bind(new InetSocketAddress(host, port));
            System.out.println("Hold_Snak-2018 Server listening on port " + port);
            Socket connection;
            while ((connection = socket.accept()) != null) 
            {
                // Handle the connection in the #handleConnection method below
                handleConnection(connection);
                // Now the connection has been handled and we've sent our reply
                // -- So now the connection can be closed so we can open more
                //    sockets in the future
                connection.close();
            }
        }
        catch(Exception e)
        {
            System.out.println("Oy m8 sometin, went wron m8\n" + e.getMessage());
        }
    }
    
    private void handleConnection(Socket connection)
    {
        try
        {
            OutputStream output = connection.getOutputStream();
            InputStream input = connection.getInputStream();
            
            // Read whatever comes in
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line = reader.readLine();

            // Print the same line we read to the client
            PrintStream writer = new PrintStream(output);
            if (!line.contains("#")) 
            {
                writer.println("ERROR#No hashtag");
            }

            String[] split = line.split("#");
            if (split[0].equalsIgnoreCase("LOGIN")) 
            {
                // addObserver(); ???
                writer.print(split[1].toUpperCase());
            } 
            else if (split[0].equalsIgnoreCase("LOWER")) 
            {
                writer.print(split[1].toLowerCase());
            } 
            connection.close();
        }
        catch(Exception e)
        {
            System.out.println("Oy m8 sometin, went wron m8\n" + e.getMessage());
        }
    }
    
    
    
    
    
}
