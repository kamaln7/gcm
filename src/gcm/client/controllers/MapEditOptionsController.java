package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import gcm.database.models.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MapEditOptionsController {
    Map map;

    @FXML
    private TextField mapTF;

    @FXML
    private TextField title_field;

    @FXML
    private TextArea description_field;

    @FXML
    private ImageView mapImg;

    public void initialize() {

        try {
            BufferedImage bufferedImage = ImageIO.read(new File("thumb-1920-44975.jpg"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            mapImg.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/MapEditOptions.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void addMap(ActionEvent event) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gcm/client/views/AddMap.fxml"));
        Parent root1 = null;
        try {
            root1 = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();
    }

    @FXML
    void addAttractionToMap(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gcm/client/views/AddAttraction.fxml"));
        Parent root1 = null;
        try {
            root1 = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();

    }
    @FXML
    public void openMapPicker(ActionEvent actionEvent) {
        try {
            Map map = AdminTablePickerMapController.loadViewAndWait(new Stage());
            if (map != null) {
                setMap(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    public void setMap(Map map) {
        this.map = map;
        title_field.setText(map.getTitle());
        description_field.setText(map.getDescription());
        this.mapTF.setText(String.format("%s - %s", this.map.getTitle(), this.map._extraInfo.get("cityTitle")));
        Input input = new ReadMapImageById.Input(this.map.getId());
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ReadMapImageById.Output output = response.getOutput(ReadMapImageById.Output.class);
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(output.imgBytes));
            Image image = SwingFXUtils.toFXImage(bImage, null);
            mapImg.setImage(image);
        }catch (Exception e){e.printStackTrace();}
    }
    @FXML
    void saveChanges(ActionEvent event) {
        this.map.setTitle(title_field.getText());
        this.map.setDescription(description_field.getText());

        Input input = new UpdateMapTitleAndDescriptionCommand.Input(this.map);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            response.getOutput(UpdateMapTitleAndDescriptionCommand.Output.class);
            ((Stage) mapTF.getScene().getWindow()).close();
        }catch (Exception e){
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }
}

