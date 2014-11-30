/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientfx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 * @author Kimmo
 */
public class ChatRow extends HBox {
    
    private ImageView imageView = null;
    private TextFlow messageArea = null;
    private final ImageView emptyView = new ImageView();      
       
    public ChatRow(Image image, String message, String family, int size,
            String color, boolean imageOnLeft) {

        System.out.println("imageOnLeft "+imageOnLeft);
        
        this.setPrefSize(400,70);
        this.setMaxWidth(USE_PREF_SIZE);
        this.setMaxHeight(USE_COMPUTED_SIZE);
        this.setAlignment(Pos.CENTER);
        
        imageView = new ImageView(image);
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        
        messageArea = new TextFlow();
        messageArea.setMinSize(310,70);
        messageArea.setPrefSize(310,70);
        messageArea.setMaxWidth(310);
        messageArea.setPadding(new Insets(5));

        messageArea.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(20), BorderWidths.DEFAULT)));
        
       
        Text text = new Text(message);
        text.setFont(Font.font(family, size));
        text.setFill(Color.web(color));
        
        messageArea.getChildren().add(text);

        if (imageOnLeft) {
            this.getChildren().add(imageView);
            this.getChildren().add(messageArea);
        } else {
            this.getChildren().add(messageArea);
            this.getChildren().add(imageView);
        }

    }
    
}
