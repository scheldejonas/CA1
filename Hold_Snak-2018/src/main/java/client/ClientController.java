
package client;

import java.util.ArrayList;
import javax.swing.JOptionPane;


public class ClientController implements Runnable
{
    private ClientGUI gui;
    private String name;

    public ClientController()
    {
        this.name = "";
        gui = new ClientGUI(this);
    }
    
    @Override
    public void run() 
    {
        String error = "";
        ArrayList<String> bingo = new ArrayList<>();
        gui.addUsersToList(bingo);
        gui.setVisible(true);
    }
    
    public static void main(String[] args) 
    {
        ClientController cc = new ClientController();
        Thread t = new Thread(cc);
        t.run();
    }
    
    public void printMessage(String msg)
    {
        gui.populateMessageToChat(msg);
    }

    
}
