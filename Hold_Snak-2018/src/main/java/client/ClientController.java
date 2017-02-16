package client;

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
        client = new Client(host, port, name);
        if (client.isConnected()) {
            gui = new ClientGUI();
            gui.populateUserList();
            gui.addClient(client);
            client.addGUI(gui);
            gui.setVisible(true);
            return true;
        }
        System.out.println("Error, client not connected!");
        return false;
    }
    
}