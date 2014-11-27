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
        
        stage.show();
        
        // TESTING: This didn't work when it was sent before stage.show()
        // but in some machines it did work even then... weird.
//        ChatMessage msg = new ChatMessage();
//        msg.setChatMessage("Hello there!");
//        backEnd.sendMessage(msg);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
