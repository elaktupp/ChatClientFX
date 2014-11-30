/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientfx;

import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
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
    private ChoiceBox fontSizeChoiceBox;
    
    @FXML
    private ColorPicker fontColorPicker;
    
    @FXML
    private TextField chatMessageField;
    
    @FXML
    private ListView chatMessageArea;
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private TextField sendToNameField;
    
    @FXML
    private Button buttonConnect;
    
    @FXML
    private ListView userListArea;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        backEnd = null;
        backThread = null;
        connected = false;
        connectedUser = "";
        privateMessage = false;
       
        fontSizeChoiceBox.setItems(
                FXCollections.observableArrayList(12,14,16,20,25));
        fontSizeChoiceBox.setValue(12);
        
        fontColorPicker.setValue(Color.BLACK);
        
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
    public void exitButtonClick(ActionEvent event) {
        backEnd.shutdown();
        Platform.exit();
    }
    
    @FXML
    private void toggleConnectionOnButtonClick(ActionEvent event) {

        if (connected) {
            disconnectFromServer(); // first disconnect
            setUiToDisconnectedState(); // then set UI
            showSystemMessage("Disconnected from the server.");
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
                msg.setPrivateName(sendToNameField.getText());

//                String selected = (String)fontSizeChoiceBox.getSelectionModel().getSelectedItem();
                
                Integer selected = (Integer)fontSizeChoiceBox.getSelectionModel().getSelectedItem();
                msg.setFontSize(selected.intValue());
                
//                msg.setFontSize(Integer.parseInt(selected));
                msg.setMessageColor(fontColorPicker.getValue().toString());
                
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
        
        boolean onLeft = connectedUser.equals(msg.getUserName());
        
        Image image = parseImageForSmiley(msg.getChatMessage());
        
        ChatRow newRow = new ChatRow(image,
                msg.getChatMessage(),
                msg.getFontFamily(),
                msg.getFontSize(),
                msg.getMessageColor(),
                onLeft);
        
        chatMessageArea.getItems().add(newRow);
        chatMessageArea.scrollTo(newRow);

    }

    /* 
     * NOTE! smileyAsChars must match exactly with smileyAsImage
     */
    private String[] smileyAsChars = {
        "[SYSTEM]",
        ":{",
        ":}",
        "#(","#-(",
        ":o)",":O)",
        "P)","P-)",
        ":|",":-|",
        ":)",":-)",
        ":(",":-(",
        ":D",":-D",
        ":P",":p",":-P",":-p",
        ";)",";-)",
        "8)","8-)",
        "8(","8-(",
        "8P","8p","8-P","8-p",
        "8o","8-o",
        "8O","8-O",
        ":X",":x",":-X",":-x",
        ":o",":-o",
        ":O",":-O",
        ":S",":s",":-S",":-s",
        ":/",":-/",":\\",":-\\",
        "x)","X)","x-)","X-)",
        "x(","X(","x-(","X-(",
        "xo","Xo","x-o","X-o","xO","XO","x-O","X-O",
        ":*",":-*",
        "|O","|o","|-O","|-o",
        "B)","B-)",
        ":.(",":,("
    };
    private String[] smileyAsImage = {
        "system.png",
        "moustach_down.png",
        "moustach_up.png",
        "sick.png","sick.png",
        "clown_nose.png","clown_nose.png",
        "pirate.png","pirate.png",
        "neutral.png","neutral.png",
        "happy.png","happy.png",
        "unhappy.png","unhappy.png",
        "laugh.png","laugh.png",
        "tongue_out.png","tongue_out.png","tongue_out.png","tongue_out.png",
        "wink.png","wink.png",
        "eyes_wide_happy.png","eyes_wide_happy.png",
        "eyes_wide_unhappy.png","eyes_wide_unhappy.png",
        "looney.png","looney.png","looney.png","looney.png",
        "eyes_wide_amazed.png","eyes_wide_amazed.png",
        "eyes_wide_alarmed.png","eyes_wide_alarmed.png",
        "mouth_shut.png","mouth_shut.png","mouth_shut.png","mouth_shut.png",
        "amazed.png","amazed.png",
        "alarmed.png","alarmed.png",
        "uncertain.png","uncertain.png","uncertain.png","uncertain.png",
        "confused.png","confused.png","confused.png","confused.png",
        "eyes_closed_happy.png","eyes_closed_happy.png","eyes_closed_happy.png","eyes_closed_happy.png",
        "eyes_closed_unhappy.png","eyes_closed_unhappy.png","eyes_closed_unhappy.png","eyes_closed_unhappy.png",
        "angry_shout.png","angry_shout.png","angry_shout.png","angry_shout.png","angry_shout.png","angry_shout.png","angry_shout.png","angry_shout.png",
        "kiss.png","kiss.png",
        "yawn.png","yawn.png","yawn.png","yawn.png",
        "cool.png","cool.png",
        "sad.png","sad.png"
    };
    
  
    private Image parseImageForSmiley(String text) {
        
        Image image = null;
        
        for (int i = 0; i < smileyAsChars.length; i++) {
            if (text.contains(smileyAsChars[i])) {
                image = new Image("file:"+smileyAsImage[i]);
                break; // We take the first one we found
            }
        }
        
        if (image == null) {
            image = new Image("file:neutral.png");
        }
        
        return image;
    }
    
    /**
     * Handles selection from the connected users list. Selected user is
     * set as a sole target of next private message(s).
     * 
     * @param event list was mouse clicked
     */
    @FXML
    public void handleSelectionFromUserList(MouseEvent event) {
      
        String name = (String)userListArea.getSelectionModel().getSelectedItem();
        // Do not allow private messages to self
        if (name.equals(connectedUser) == false) {
            sendToNameField.setText(name);
            privateMessage = true;
        }
        userListArea.getSelectionModel().clearSelection();
        chatMessageField.requestFocus();
    }
    
    /**
     * Mouse click on message receiver filed clears it back to default
     * i.e. messages are sent to all connected users.
     * 
     * @param event filed was mouse clicked
     */
    @FXML
    public void clearSendToNameField(MouseEvent event) {
        sendToNameField.setText("");
        privateMessage = false;
        chatMessageField.requestFocus();
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
            showSystemMessage("Connected to the server. Your user name "+
                    userNameField.getText()+" changed to "+msg.getUserName());
        } else {
            showSystemMessage("Connected to the server.");
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
        
        ChatMessage sysMsg = new ChatMessage();
        
        sysMsg.setChatMessage("[SYSTEM]: "+msg+"\n");
        sysMsg.setFontFamily("Helvetica");
        sysMsg.setFontSize(12);
        sysMsg.setMessageColor("#FF0000");
        
        updateMessageArea(sysMsg);
    }
    
    /**
     * This prepares the connection to the server but actual communication
     * requires user name to be sent to the server i.e. ChatConnect.
     */
    private void setUpClientBackEnd() {
        
        try {
            backEnd = new ClientBackEnd(this); // tries to create connection
            backThread = new Thread(backEnd);
            backThread.setName("Client BET");
            // Tell JVM this is background thread, so JVM can kill when
            // backEnd is destroyed.
            backThread.setDaemon(true);
            backThread.start(); // this will eventually call backEnd's run()
        } catch (SocketException e) {
            
// PROBLEM: If this fails, currently the only way to get this working properly
// is to restart the client and hope the server is there. So this means it
// is not possible to start the server afterwards and just hit the connect
// button.
            
            showSystemMessage("Cannot find the server, please restart!");
            backEnd = null;
            backThread = null;
        }
    }
    
    /**
     * Sets buttons and text fields after user has connected.
     */
    private void setUiToConnectedState() {
        
        connected = true;
        buttonConnect.setText("DISCONNECT");
        userNameField.setDisable(true);
        chatMessageField.setDisable(false);
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
    }
    
    /**
     * ChatConnect message is created and sent to the server.
     * 
     * If the user name is empty or just whitespace characters
     * the message is not sent.
     */
    private void connectToServer() {
        
// PROBLEM: The need of doing the preliminary connection to
// avoid the null exception when connectToServer is called and
// ChatConnect message is sent...
        
        if (backEnd != null && connected == false) {
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
        
// PROBLEM: The need of doing the preliminary connection to
// avoid the null exception when connectToServer is called and
// ChatConnect message is sent...
        
        // Preliminary connection, ChatConnect still needed
        setUpClientBackEnd();
    }
}
