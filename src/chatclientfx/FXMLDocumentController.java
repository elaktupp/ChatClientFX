/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientfx;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import message.ChatMessage;
import message.ChatUserList;

/**
 *
 * @author Ohjelmistokehitys
 */
public class FXMLDocumentController implements Initializable {
    
    private ClientBackEnd backEnd;
    private Thread backThread;
    
    @FXML
    private TextField chatMessage;
    
    @FXML
    private TextArea chatMessageArea;
    
    @FXML
    private TextField userName;
    
    @FXML
    private Button buttonConnect;
    
    @FXML
    private ListView userList;
    
    private ChatMessage createMessage() {
        ChatMessage msg = new ChatMessage();
        msg.setChatMessage(chatMessage.getText());
        msg.setUserName((userName.getText().isEmpty())?("Anonymous"):(userName.getText()));
        chatMessage.clear();
        return msg;
    }
    
//    @FXML
//    private void sendChatMessage(ActionEvent event) {
//        backEnd.sendMessage(createMessage());
//    }
    
    @FXML
    private void sendMessageOnKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (chatMessage.getText().isEmpty() == false) {
                backEnd.sendMessage(createMessage());
            }
        }
    }
    
    @FXML
    private void handleConnect(ActionEvent event) {
        if (userName.getText().isEmpty() == false) {
            userName.setDisable(true);
            chatMessage.setDisable(false);
            chatMessage.requestFocus();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        chatMessage.setDisable(true);
       
        // Move execution of requestFocus to UI thread.
        // This is needed because focus cannot be set
        // before the thing is visible.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                userName.requestFocus();
            }
        });
        
        backEnd = new ClientBackEnd(this); // creates connection
        backThread = new Thread(backEnd);
        // Tell JVM this is background thread, so JVM can kill when
        // backEnd is destroyed.
        backThread.setDaemon(true);
        backThread.start(); // this will eventually call backEnd's run()

    }    
    
    public void updateTextArea(ChatMessage msg) {
        chatMessageArea.appendText(msg.getUserName()+": "+
                msg.getChatMessage()+"\n");
        
        
        // HACK
        updateUsersArea(null);
    }

    // HACK
    ObservableList<String> arr = FXCollections.observableArrayList();
    
    public void updateUsersArea(ChatUserList msg) {
        
        // HACK
        arr.add("A");
        arr.add("B");
        arr.add("C");
        
        userList.setItems(arr);

    }
}
