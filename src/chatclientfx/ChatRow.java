/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclientfx;

import java.util.HashMap;
import java.util.Vector;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
    
    public ChatRow(Image image, String message, String family, int size,
            String color, boolean imageOnLeft) {
        
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
        messageArea.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, new CornerRadii(20),
                BorderWidths.DEFAULT)));
        
       
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

    // TODO: Create ChatRow with Smileys embedded into the text.
    //
    // - Text with smileys is not shown properly
    // - Current smiley images are not suitable for scaling
    // - Text view is not scaled if the text is very long
    
    public ChatRow(String message, String family, int size,
            String color) {
        
        this.setPrefSize(400,70);
        this.setMaxWidth(USE_PREF_SIZE);
        this.setMaxHeight(USE_COMPUTED_SIZE);
        this.setAlignment(Pos.CENTER);
        
        messageArea = new TextFlow();
        messageArea.setMinSize(400,70);
        messageArea.setPrefSize(400,70);
        messageArea.setMaxWidth(400);
        messageArea.setPadding(new Insets(5));
        messageArea.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, new CornerRadii(20),
                BorderWidths.DEFAULT)));
        
        // Hunting smileys
        
        // TODO: NO int[] THEY WILL CRASH WHEN THERE IS SEVERAL SMILEYS!!!
        // -> switch to some collection...
        int[] smileys = new int[10];
        int[] positions = new int[10];
        int finds = 0;
        int found;
        for (int i = 0; i < smileyAsChars.length; i++) {
            found = message.indexOf(smileyAsChars[i]);
            if (found < 0) {
                // Nothing found
            } else {
                // Smiley i at this found index
                smileys[finds] = i;
                positions[finds] = found;
                finds++;
            }
        }

        // Now we got the positions, chop up the message
        
        Vector<Object> parts = new Vector();
       
        if (finds > 0) {
            // Initial values
            int begin = 0;
            int length = smileyAsChars[smileys[0]].length();
            int end = positions[0];
            String cut = "";

// TODO: This here looses text if a smiley is first thing in the message!
            
            for (int i = 0; i < finds; ) {
//                System.out.println("ROUND: "+i+") "+begin+" - "+end+" "+length);
                // Get text until the next smiley
                cut = message.substring(begin, end);
//                System.out.println("CUT: ["+begin+"-"+end+"] "+cut);
                parts.add(cut);
                // Then handle the smiley
                begin = end;
                end += length;
                Image img = parseImageForSmiley(message.substring(begin,end));
//                System.out.println("IMG: ["+begin+"-"+end+"] "+
//                        message.substring(begin,end)+" "+img);
                if (img != null) {
                    ImageView imgV = new ImageView(img);
                    imgV.setFitWidth(40);
                    imgV.setFitHeight(40);
                    parts.add(imgV);
                }
                // Ready for next round
                i++; // INDEX INCREMENTED HERE to get correct table values
                begin = end;
                length = smileyAsChars[smileys[i]].length();
                end = positions[i];
            }

            // Put it all to TextFlow
            int i = 0;
            for (Object it : parts) {
                if (it instanceof String) {
                    Text text = new Text((String)it);
                    text.setFont(Font.font(family, size));
                    text.setFill(Color.web(color));
                    messageArea.getChildren().add(i,text);
                } else {
                    // Must be ImageView
                    messageArea.getChildren().add(i,(ImageView)it);
                }
                i++;
            }
        } else {
            // No smileys detected
            Text text = new Text(message);
            text.setFont(Font.font(family, size));
            text.setFill(Color.web(color));

            messageArea.getChildren().add(text);
        }
        
        this.getChildren().add(messageArea);
    }
    
    /* 
     * NOTE! smileyAsChars must match exactly with smileyAsImage
     */
    private String[] smileyAsChars = {
//        "[SYSTEM]",
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
//        "system.png",
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
                break; // we want only the first one
            }
        }
        
        return image;
    }
}
