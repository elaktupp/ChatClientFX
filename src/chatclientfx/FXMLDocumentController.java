/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientfx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import message.ChatConnect;
import message.ChatConnectResponse;
import message.ChatDisconnect;
import message.ChatMessage;
import message.ChatUserListUpdate;

/**
 *
 * @author Ohjelmistokehitys
 */
public class FXMLDocumentController implements Initializable {
    
    private ClientBackEnd backEnd;
    private Thread backThread;
    private boolean connected;
    private String connectedUser;
    
    @FXML
    private TextField chatMessage;
    
    @FXML
    private TextArea chatMessageArea;
    
    @FXML
    private TextField userName;
    
    @FXML
    private Button buttonConnect;
    
    @FXML
    private ListView userListArea;
    
    private ChatMessage createMessage() {
        ChatMessage msg = new ChatMessage();
        msg.setChatMessage(chatMessage.getText());
        msg.setUserName(connectedUser);
        chatMessage.clear();
        return msg;
    }
    
    @FXML
    private void sendMessageOnKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (chatMessage.getText().isEmpty() == false) {
                backEnd.sendMessage(createMessage());
            }
        }
    }
    
    @FXML
    private void handleConnectButton(ActionEvent event) {

        if (connected) {
            setUiToDisconnectedState();
            disconnectFromServer();
        } else {
            connectToServer();
        }

    }

    @FXML
    private void connectOnKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            connectToServer();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        connected = false;
        
        setUiToDisconnectedState();
       
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
    }
    
    public void updateUserListArea(ChatUserListUpdate msg) {
        
        userListArea.setItems(FXCollections.observableArrayList(msg.getUserList()));

    }
    
    public void updateConnection(ChatConnectResponse msg) {

        setUiToConnectedState();
        
        if (msg.isNameChanged()) {
            showSystemMessage("Your user name "+userName.getText()+
                    " changed to "+msg.getUserName());
        }
        connectedUser = msg.getUserName();

    }
    
    public void showSystemMessage(String msg) {
        chatMessageArea.appendText("[SYSTEM]: "+msg+"\n");
    }

    private void setUiToConnectedState() {
        connected = true;
        buttonConnect.setText("DISCONNECT");
        userName.setDisable(true);
        chatMessage.setDisable(false);
    }
    
    private void setUiToDisconnectedState() {
        connected = false;
        buttonConnect.setText("CONNECT");
        userName.clear();
        userName.setDisable(false);
        chatMessage.setDisable(true);
    }
    
    private void connectToServer() {
        if (connected == false) {
            if (userName.getText().isEmpty() == false) {
                userName.setDisable(true);
                chatMessage.setDisable(false);
                chatMessage.requestFocus();

            ChatConnect msg = new ChatConnect();
            msg.setUserName(userName.getText());

            backEnd.sendMessage(msg); // CONNECT
            }
        }
    }
    
    private void disconnectFromServer() {
        if (connected == true) {
            ChatDisconnect msg = new ChatDisconnect();
            backEnd.sendMessage(msg); // DISCONNECT
        }
    }
}
