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
import javafx.application.Platform;
import message.Chat;

import message.ChatMessage; // our very own library :)
import message.ChatUserList;

/**
 *
 * @author Ohjelmistokehitys
 */
public class ClientBackEnd implements Runnable {

    // Client <-> Server communication
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    private FXMLDocumentController controller;
    
    // NOTE: This stuff is run in UI thread. Don't pass "this" anywhere
    // in constructor.
    public ClientBackEnd(FXMLDocumentController controller) {
 
        try {
            // DNS name "localhost" equals inet address "127.0.0.1"
            // Note that in Java new Socket(...) immediately connects
            // if don't want that use Socket()
            clientSocket = new Socket("localhost", 3010);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        this.controller = controller;
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
                // NOTE: Needs to be final because of runLater
                //                     deserialization
                final ChatMessage msg = (ChatMessage)input.readObject();
               
                // Move execution of updateTextArea to UI thread.
                // This is safer, otherwise TextArea might have lost
                // some events.
                // - "What is do to UI should be done in UI thread"
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.updateTextArea((ChatMessage)msg);
                    }
                });

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
