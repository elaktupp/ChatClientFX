/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import message.ChatMessage;

/**
 *
 * @author Ohjelmistokehitys
 */
public class ChatClientFX extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        
        ClientBackEnd backEnd = new ClientBackEnd();
        Thread backThread = new Thread(backEnd);
        // Tell JVM this is background thread,
        // so JVM can kill at exit.
        backThread.setDaemon(true);
        backThread.start(); // this will eventually call backEnd's run()
        
        // TESTING
        ChatMessage msg = new ChatMessage();
        msg.setChatMessage("Hello there!");
        backEnd.sendMessage(msg);
        
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
