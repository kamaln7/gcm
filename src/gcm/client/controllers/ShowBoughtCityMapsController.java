package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ShowBoughtCityMapsController {


    @FXML
    private ImageView mapImg;

    @FXML
    private TextArea description_field;

    @FXML
    private Text mapTitle;

    @FXML
    private Button next;

    @FXML
    private Button previous;


    @FXML
    void next(ActionEvent event) {
        current++;
        showMap(maps.get(current));
        if (current==size-1){
            next.setVisible(false);
        }

        previous.setVisible(true);
    }

    @FXML
    void previous(ActionEvent event) {
        current--;
        showMap(maps.get(current));
        if (current==0){
            previous.setVisible(false);
        }

        next.setVisible(true);
    }

    private  int myCityID;
    private List<Map> maps;
    private int current=0;
    private int size;
    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) mapImg.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    private void setCity(int cityID){
        this.myCityID=cityID;
    }

    private void setMaps(){
        Input input = new FindMapsByCityIdCommand.Input(myCityID);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindMapsByCityIdCommand.Output output = response.getOutput(FindMapsByCityIdCommand.Output.class);
            this.maps = output.maps;
            this.size= maps.size();

            //show the first map
            showMap(this.maps.get(current));
            previous.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void download(ActionEvent event) {
        Input input = new AddDownloadCommand.Input(ClientGUI.getCurrentUser().getId(),maps.get(current).getId(),"map");
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddDownloadCommand.Output output = response.getOutput(AddDownloadCommand.Output.class);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Map Downloaded!");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMap(Map map){
        //add view to the map
        Input viewInput = new AddViewCommand.Input(ClientGUI.getCurrentUser().getId(),map.getId(),"map");
        try {
            Response viewResponse = ClientGUI.getClient().sendInputAndWaitForResponse(viewInput);
            AddViewCommand.Output viewOutput = viewResponse.getOutput(AddViewCommand.Output.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //set fields
        description_field.setText(map.getDescription());
        mapTitle.setText(map.getTitle());
        //show the image
        //get the image bytes[]
        Input input = new ReadMapImageById.Input(map.getId());
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ReadMapImageById.Output output = response.getOutput(ReadMapImageById.Output.class);
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(output.imgBytes));
            Image image = SwingFXUtils.toFXImage(bImage, null);
            mapImg.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadView(Stage primaryStage, int cityID) throws IOException {
        URL url = ShowBoughtCityMapsController.class.getResource("/gcm/client/views/ShowBoughtCityMaps.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        ShowBoughtCityMapsController controller = loader.getController();
        controller.setCity(cityID);
        controller.setMaps();

        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }






}

