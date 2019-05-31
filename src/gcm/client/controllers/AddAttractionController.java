package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindMapByTitleAndVersionCommand;
import gcm.commands.FindMapByTitleCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.Map;
import gcm.database.models.User;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

public class AddAttractionController {

    @FXML
    private ImageView mapImg;

    @FXML
    private TextField title_field;

    @FXML
    private TextField version_field;

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
        Input input = new FindMapByTitleCommand.Input(title_field.getText());

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindMapByTitleAndVersionCommand.Output output = response.getOutput(FindMapByTitleAndVersionCommand.Output.class);
            System.out.println("map title:"+ output.map.getVersion());
            System.out.println(output.imgBytes);
//            System.out.println(output.getImageBytes().length);
//            for(int i=0; i< output.getImageBytes().length; i++) {
//                System.out.print(output.getImageBytes()[i] +" ");
//            }

//            ByteArrayInputStream bis = new ByteArrayInputStream(output.img);
//            BufferedImage bImage2 = ImageIO.read(bis);
//            Image image = SwingFXUtils.toFXImage(bImage2, null);
//            mapImg.setImage(image);




        } catch (User.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect login details.");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

}

