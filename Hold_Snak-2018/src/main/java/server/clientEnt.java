import java.net.Socket;
 
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
/**
 *
 * @author William Bingo
 */
public class clientEnt {
    Socket sock;
    String username;
 
    public clientEnt(Socket sock, String username) {
        this.sock = sock;
        this.username = username;
    }
 
    public Socket getSock() {
        return sock;
    }
 
    public void setSock(Socket sock) {
        this.sock = sock;
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
   
   
}
