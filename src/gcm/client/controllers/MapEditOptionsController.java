package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.ReadMapImageById;
import gcm.commands.Response;
import gcm.commands.UpdateMapTitleAndDescriptionCommand;
import gcm.database.models.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    /**
     * initialize the controller
     */
    public void initialize() {
        try {
            BufferedImage bufferedImage = ImageIO.read(this.getClass().getResourceAsStream("/gcm/client/staticFiles/thumb-1920-44975.jpg"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            mapImg.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * load the viewer
     * @param primaryStage
     * @throws IOException
     */
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

    /**
     * open window to choose a new map
     * @param actionEvent
     */
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

    /**
     * set the map info in the viewer
     * @param map
     */
    public void setMap(Map map) {
        this.map = map;
        title_field.setText(map.getTitle());
        description_field.setText(map.getDescription());
        this.mapTF.setText(String.format("%s (%s)", this.map.getTitle(), this.map._extraInfo.get("cityTitle")));
        Input input = new ReadMapImageById.Input(this.map.getId());
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ReadMapImageById.Output output = response.getOutput(ReadMapImageById.Output.class);
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(output.imgBytes));
            Image image = SwingFXUtils.toFXImage(bImage, null);
            mapImg.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    /**
     * save the changes
     * @param event
     */
    @FXML
    void saveChanges(ActionEvent event) {
        if (this.map==null){
            (new Alert(Alert.AlertType.WARNING, "Please Choose A Map!")).show();
        }
        this.map.setTitle(title_field.getText());
        this.map.setDescription(description_field.getText());

        Input input = new UpdateMapTitleAndDescriptionCommand.Input(this.map);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            response.getOutput(UpdateMapTitleAndDescriptionCommand.Output.class);

            (new Alert(Alert.AlertType.INFORMATION, "Map successfully updated!")).show();
            ((Stage) mapTF.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }
}

