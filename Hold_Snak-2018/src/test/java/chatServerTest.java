/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import client.Client;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import serverHandler.serverHandler;

/**
 *
 * @author William Pfaffe
 */
public class chatServerTest {
    
    Client cl, cl2;
    serverHandler sh;

    public chatServerTest() {
        this.cl = new Client("51.15.56.53", 8081, "Tester");
        this.cl2 = new Client("51.15.56.53", 8081, "Tester2");
        this.sh = new serverHandler();
    }
    
    @Test
    public void testChat() throws IOException{
        System.out.println("Testing return of chat commands");
        cl.sendMessage("MSG#Tester2#JUnitTest");
        System.out.println("Sent Message");
        String msg = cl2.readMessage();
        System.out.println("Message received: " + msg);
        assertEquals("MSG#[*P* From Tester]#JUnitTest", msg);
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
