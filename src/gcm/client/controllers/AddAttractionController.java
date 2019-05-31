package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.Map;
import gcm.database.models.User;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.AttributedCharacterIterator;

public class AddAttractionController {

    @FXML
    private AnchorPane pane;

    @FXML
    private ImageView mapImg;

    @FXML
    private TextField title_field;

    @FXML
    private TextField version_field;

    @FXML
    private TextField Xcord;

    @FXML
    private TextField Ycord;

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddAttraction.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void showMap(ActionEvent event) {
        Input input = new FindMapByTitleAndVersionCommand.Input(title_field.getText(), version_field.getText());

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindMapByTitleAndVersionCommand.Output output = response.getOutput(FindMapByTitleAndVersionCommand.Output.class);

            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(output.imgBytes));
            Image image = SwingFXUtils.toFXImage(bImage, null);
            mapImg.setImage(image);
            mapImg.setOnMouseClicked(e -> {
                System.out.println("["+e.getX()+", "+e.getY()+"]");
                Xcord.setText(String.valueOf(e.getX()));
                Ycord.setText(String.valueOf(e.getY()));
                Circle c = new Circle(e.getX(), e.getY(), 5, Color.RED);
                pane.getChildren().add(c);
                mapImg.setOnMouseClicked(null);
            });
            BufferedImage bImage2 = SwingFXUtils.fromFXImage(mapImg.getImage(), null);
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            ImageIO.write(bImage, "jpg", s);
            ImageIO.write(bImage2, "jpg", new File("outputtest.jpg") );
            byte[] res  = s.toByteArray();
            s.close();

        } catch (Map.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Map Not Found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

}

