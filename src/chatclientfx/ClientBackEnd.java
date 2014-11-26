/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientfx;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import message.ChatMessage; // our very own library :)

/**
 *
 * @author Ohjelmistokehitys
 */
public class ClientBackEnd implements Runnable {

    // Client <-> Server communication
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    // NOTE: This stuff is run in UI thread. Don't pass "this" anywhere
    // in constructor.
    public ClientBackEnd() {
 
        try {
            // "localhost" equals "127.0.0.1"
            clientSocket = new Socket("localhost", 3010);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    // NOTE: This is executed in our own thread, once this run method is called.
    // This is called only once!
    @Override
    public void run() {
        
        try {
            // NOTE: Order matters!!! Output must be created first because
            // same Socket is used for both output and input. This is because
            // getOutputStream already opens stream in other words stream
            // that has been opened for writing cannot be opened for reading.
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
                
            // Read and write from Socket - polling until closed
            while(true) {
                //               deserialization
                ChatMessage msg = (ChatMessage)input.readObject();
                // TESTING
                System.out.println(msg.getChatMessage());
            }   
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

    }
    
    public void sendMessage(ChatMessage msg) {
        try {
            output.writeObject(msg);
            output.flush(); // makes sure data is sent, not must have
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
