/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import serverHandler.serverHandler;


/**
 *
 * @author William Pfaffe
 */
public class ChatServer {
    
    private static final serverHandler servHandle = new serverHandler();
    
    public static void main(String[] args) {
        servHandle.start();
    }
}
