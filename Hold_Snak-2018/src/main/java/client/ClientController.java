package client;

import exception.UsernameInUseException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ClientController {

    private ClientGUI gui;
    private Client client;

    private String name, host;
    private int port;
    
    public ClientController(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public boolean run() {
        String error = null;
        try {
            gui = new ClientGUI();
            client = new Client(host, port, name, gui);
        } catch (IOException ex) {
            //Server offline/Stuff
            error = "Error connecting to server!\n" +
                       "Server might be offline, try again later.";
        } catch (UsernameInUseException ex) {
            error = "Username is already in use!\n" +
                       "Please enter another and try again.";
        }
        if (error != null) {
            JOptionPane.showMessageDialog(null, error);
            return false;
        }
        if (client.isConnected()) {
            gui.populateUserList();
            gui.addClient(client);
            client.addGUI(gui);
            gui.setVisible(true);
            return true;
        }
        error = "Unknown error ocurred, please try again.";
        System.out.println(error);
        JOptionPane.showMessageDialog(null, error);
        return false;
    }
    
}