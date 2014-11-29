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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
    private boolean privateMessage;
    
    @FXML
    private TextField chatMessageField;
    
    @FXML
    private TextArea chatMessageArea;
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private Label sendToLabel;
    
    @FXML
    private Label sendToNameLabel;
    
    @FXML
    private Button buttonConnect;
    
    @FXML
    private ListView userListArea;
    
    @FXML
    public void handleSelectionFromUserList(MouseEvent event) {
      
        sendToLabel.setText("TO");
        
        String name = (String)userListArea.getSelectionModel().getSelectedItem();
        
        sendToNameLabel.setText(name);
        
        userListArea.getSelectionModel().clearSelection();
        privateMessage = true;
    }
    
    @FXML
    public void clearSendToNameField(MouseEvent event) {
        
        sendToLabel.setText("TO ALL");
        sendToNameLabel.setText("");
        privateMessage = false;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
        backEnd = null;
        backThread = null;
        connected = false;
        connectedUser = "";
        privateMessage = false;
        
        userListArea.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        setUiToDisconnectedState();
       
        // Move execution of requestFocus to UI thread.
        // This is needed because focus cannot be set
        // before the thing is visible.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                userNameField.requestFocus();
            }
        });
        
        setUpClientBackEnd();

    }    
   
    @FXML
    private void toggleConnectionOnButtonClick(ActionEvent event) {

        if (connected) {
            disconnectFromServer(); // first disconnect
            setUiToDisconnectedState(); // then set UI
        } else {
            connectToServer();
            // setUiToConnectedState gets called after response
        }
    }

    @FXML
    private void connectToServerOnEnterKeyReleased(KeyEvent event) {
        
        if (event.getCode() == KeyCode.ENTER) {
            // No need to do extra checks since the userNameField
            // gets disabled when connected.
            connectToServer();
        }
    }

    @FXML
    private void sendMessageOnEnterKeyReleased(KeyEvent event) {
        
        if (event.getCode() == KeyCode.ENTER) {
            String messageText = chatMessageField.getText();
            if (messageText.isEmpty() == false) {
                ChatMessage msg = new ChatMessage();
                msg.setChatMessage(messageText);
                msg.setUserName(connectedUser);

                msg.setIsPrivate(privateMessage);
                msg.setPrivateName(sendToNameLabel.getText());
                
                // TODO
                msg.setFontSize(12);
                msg.setMessageColor("#000000");
                
                chatMessageField.clear();
                
                backEnd.sendMessage(msg);
            }
        }
    }
    
    /**
     * Method for the client back end to update the UI view when the
     * client gets new chat message.
     * 
     * @param msg ChatMessage contains the message text and sender name.
     */
    public void updateMessageArea(ChatMessage msg) {
        
        chatMessageArea.appendText(msg.getUserName()+": "+
                msg.getChatMessage()+"\n");
    }
    
    /**
     * Method for the client back end to update the UI view when the
     * client gets updated list of connected clients from the server.
     *  
     * @param msg ChatUserListUpdate contains list of names
     */
    public void updateUserListArea(ChatUserListUpdate msg) {
        
        userListArea.setItems(FXCollections.
                observableArrayList(msg.getUserList()));

    }
    
    /**
     * Method for the client back end to update the UI view when the
     * client gets connected to the server.
     * 
     * @param msg ChatConnectResponse contains the validated user name.
     */
    public void updateToConnected(ChatConnectResponse msg) {

        setUiToConnectedState();
        
        if (msg.isNameChanged()) {
            showSystemMessage("Your user name "+userNameField.getText()+
                    " changed to "+msg.getUserName());
        }
        connectedUser = msg.getUserName();
    }
    
    /**
     * Method for the client back end to update the UI view when the
     * client gets disconnected from the server.
     */
    public void updateToDisconnected() {
        
        setUiToDisconnectedState();
    }
    
    /**
     * System message text is appended to the message area.
     * 
     * @param msg String containing the text to be shown.
     */
    public void showSystemMessage(String msg) {
        chatMessageArea.appendText("[SYSTEM]: "+msg+"\n");
    }
    
    /**
     * This prepares the connection to the server but actual communication
     * requires user name to be sent to the server i.e. ChatConnect.
     */
    private void setUpClientBackEnd() {
        
        backEnd = new ClientBackEnd(this); // creates connection
        backThread = new Thread(backEnd);
        backThread.setName("Client BET");
        // Tell JVM this is background thread, so JVM can kill when
        // backEnd is destroyed.
        backThread.setDaemon(true);
        backThread.start(); // this will eventually call backEnd's run()
    }
    
    /**
     * Sets buttons and text fields after user has connected.
     */
    private void setUiToConnectedState() {
        
        connected = true;
        buttonConnect.setText("DISCONNECT");
        userNameField.setDisable(true);
        chatMessageField.setDisable(false);
        showSystemMessage("You are now connected to the server.");
    }
    
    /**
     * Sets buttons and text fields after user has disconnected.
     */
    private void setUiToDisconnectedState() {
        
        connected = false;
        buttonConnect.setText("CONNECT");
        userNameField.clear();
        userNameField.setDisable(false);
        chatMessageField.setDisable(true);
        showSystemMessage("You are now disconnected from the server.");
    }
    
    /**
     * ChatConnect message is created and sent to the server.
     * 
     * If the user name is empty or just whitespace characters
     * the message is not sent.
     */
    private void connectToServer() {
        
        if (connected == false) {
           if (userNameField.getText().isEmpty() == false &&
               userNameField.getText().trim().isEmpty() == false &&
               userNameField.getText().length() <= 20) {
                // Acceptable user name
                userNameField.setDisable(true);
                chatMessageField.setDisable(false);
                chatMessageField.requestFocus();

                ChatConnect msg = new ChatConnect();
                msg.setUserName(userNameField.getText());

                backEnd.sendMessage(msg); // CONNECT
            } else {
               showSystemMessage("Check your user name! It is either empty"+
                       " or longer than 20 characters.");
               userNameField.requestFocus();
           }
        }
    }
    
    /**
     * ChatDisconnect message is created and sent to the server.
     * 
     * Old client back end is shutdown and new perliminary connection
     * is created, ready for new ChatConnect message.
     */
    private void disconnectFromServer() {
        
        if (connected == true) {
            ChatDisconnect msg = new ChatDisconnect();
            backEnd.sendMessage(msg); // DISCONNECT
        }
        backEnd.shutdown();
        
        // Preliminary connection, ChatConnect still needed
        setUpClientBackEnd();
    }
}
