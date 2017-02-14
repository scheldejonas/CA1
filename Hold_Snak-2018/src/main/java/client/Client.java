
package client;

import entity.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;



public class Client
{
    private User user;
    private String host;
    private int port;
    private Socket clientSocket;
    
    public Client(String host, int port) 
    {
        this.host = host;
        this.port = port;
    }

    public void open()
    {
        try
        {
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(host, port));
            System.out.println("Client connected to server on port " + port);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Sends a message to the server by opening a socket, writing to the input and reading from the output.
     *
     * @param message The message to send
     * @throws IOException
     */
    public void sendMessage(String message)
    {
        try
        {
            // Write to the server
            OutputStream output = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output);
            writer.println(message);
            writer.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Reads a message from the server, if connected.
     *
     * @return A message from the server.
     * @throws IOException
     */
    public String readMessage() 
    {
        try
        {
            // Read from the server
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String fromServer;
            while ((fromServer = reader.readLine()) == null) 
            {
                // Wait until the server says something interesting
            }
            return fromServer;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    



    
}
