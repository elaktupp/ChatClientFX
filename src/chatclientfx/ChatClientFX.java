/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Ohjelmistokehitys
 */
public class ChatClientFX extends Application  {
    
    public static final boolean TESTING = true;
    
    public FXMLDocumentController controller;
    
    @Override
    public void start(Stage stage) throws Exception {

        /* NOTE!
         *
         * If you do it like this:
         *
         * FXMLLoader loader = new FXMLLoader();
         * Parent root = loader.load(getClass().getResource("FXMLDocument.fxml"));
         * controller = (FXMLDocumentController)loader.getController();
         *
         * the controller will be NULL!
         *
         * So it must be done like this:
         */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
        Parent root = loader.load();
        controller = (FXMLDocumentController)loader.getController();
       
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                controller.clientCloseRequested();
                Platform.exit();
            }
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
