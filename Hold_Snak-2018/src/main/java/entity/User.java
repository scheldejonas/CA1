
package entity;

import java.util.Observable;
import java.util.Observer;


public class User implements Observer
{
    private String name;
    private String ip;
    private int port;
    private boolean shouldWrite;

    public User(String name, String ip, int port) 
    {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getName() 
    {
        return name;
    }

    public String getIp() 
    {
        return ip;
    }

    public int getPort() 
    {
        return port;
    }

    @Override
    public void update(Observable o, Object arg) 
    {
        String msg = (String) arg;
        
    }
    
    
    
    
}
