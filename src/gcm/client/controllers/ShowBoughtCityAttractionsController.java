package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.GetCityAttractionsCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ShowBoughtCityAttractionsController {

    @FXML
    private TableView<attractionInfo> tableList;

    @FXML
    private TableColumn<attractionInfo, String> name_column;

    @FXML
    private TableColumn<attractionInfo, String> description_column;

    @FXML
    private TableColumn<attractionInfo, String> location_column;

    @FXML
    private TableColumn<attractionInfo, String> type_column;

    @FXML
    private TableColumn<attractionInfo, String> accessibility_column;

    private  City myCity;
    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) tableList.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    private void setCity(City city){
        this.myCity=city;
    }
    private void setAttractions(){
        Input input = new GetCityAttractionsCommand.Input(myCity.getId());

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            GetCityAttractionsCommand.Output output = response.getOutput(GetCityAttractionsCommand.Output.class);

            name_column.setCellValueFactory(new PropertyValueFactory<>("name"));
            description_column.setCellValueFactory(new PropertyValueFactory<>("description"));
            location_column.setCellValueFactory(new PropertyValueFactory<>("location"));
            type_column.setCellValueFactory(new PropertyValueFactory<>("type"));
            accessibility_column.setCellValueFactory(new PropertyValueFactory<>("accessibility"));

            ObservableList<attractionInfo> oblist = FXCollections.observableArrayList();
            for(int i=0;i<output.result.size(); i ++)
                oblist.add(new attractionInfo(output.result.get(i).getName(),output.result.get(i).getDescription(),output.result.get(i).getLocation(),output.result.get(i).getType(),output.result.get(i).getAccessibleSpecial()));

            tableList.setItems(oblist);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadView(Stage primaryStage, City city) throws IOException {
        URL url = ShowBoughtCityAttractionsController.class.getResource("/gcm/client/views/ShowBoughtCityAttractions.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        ShowBoughtCityAttractionsController controller = loader.getController();
        controller.setCity(city);
        controller.setAttractions();

        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public class attractionInfo{

        private String name, description, location, type, accessibility;

        public attractionInfo(String name, String description, String location, String type, boolean accessibility) {
            this.name = name;
            this.description = description;
            this.location = location;
            this.type = type;
            this.accessibility = accessibility?"YES":"NO";
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getLocation() {
            return location;
        }

        public String getType() {
            return type;
        }

        public String getAccessibility() {
            return accessibility;
        }
    }





}
