package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class AddTourController {
    @FXML
    private TableView<attractionInfo> tableList;

    @FXML
    private TableColumn<attractionInfo, String> id_column;

    @FXML
    private TableColumn<attractionInfo, String> name_column;

    @FXML
    private TextField CityTF;

    @FXML
    private TableView<attractionInfo> tableList2;

    @FXML
    private TableColumn<attractionInfo, String> Index_column;

    @FXML
    private TableColumn<attractionInfo, String> added_id_column;

    @FXML
    private TableColumn<attractionInfo, String> added_name_column;

    @FXML
    private TableColumn<attractionInfo, String> time_column;

    @FXML
    private TextField time_field;

    @FXML
    private TextArea tour_description_field;

    private City city;

    private Integer index = 1;

    @FXML
    void ChooseCity(ActionEvent event) {
        try {
            this.city = AdminTablePickerCityController.loadViewAndWait(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        CityTF.setText(city.toString());

        getAttractions(event);
    }

    /**
     * add the tour to the database
     * @param event
     */
    @FXML
    void addToTour(ActionEvent event) {
        if (this.city==null || time_field.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please choose a city or fill time field");
            alert.show();
            return;
        }
        //Get ID of selected attraction from the table
        TablePosition pos = tableList.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        attractionInfo item = tableList.getItems().get(row);

        //adding the attraction to the other table
        added_id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
        added_name_column.setCellValueFactory(new PropertyValueFactory<>("attraction_name"));
        time_column.setCellValueFactory(new PropertyValueFactory<>("time"));
        Index_column.setCellValueFactory(new PropertyValueFactory<>("myIndex"));
        attractionInfo itemToAdd = new attractionInfo(item.getId(), item.getAttraction_name(), time_field.getText());
        index++;
        tableList2.getItems().add(itemToAdd);


        //removing the attraction from the first table
        tableList.getItems().remove(item);
    }

    /**
     * create a new tour
     * @param event
     */
    @FXML
    void createTour(ActionEvent event) {
        if (tour_description_field.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill the description");
            alert.show();
            return;
        }
        int tour_id = -1;
        //add the tour to the Tour model and get the ID back
        Input input = new AddTourCommand.Input(city.getId(), tour_description_field.getText());

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddTourCommand.Output output = response.getOutput(AddTourCommand.Output.class);
            tour_id = output.tour.getId();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //add tour-attraction relation to the TourAttraction model
        ObservableList<attractionInfo> oblist = tableList2.getItems();
        for (int i = 0; i < oblist.size(); i++) {
            System.out.println("attraction id:" + oblist.get(i).getId());
            Input input2 = new AddTourAttractionCommand.Input(tour_id, oblist.get(i).getId(), oblist.get(i).getMyIndex(), oblist.get(i).getTime());
            try {
                Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input2);
                response.getOutput(AddTourAttractionCommand.Output.class);
                (new Alert(Alert.AlertType.INFORMATION, "Tour was added!")).showAndWait();
                ((Stage) tableList.getScene().getWindow()).close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * returns the attractions inside a city
     * @param event
     */
    void getAttractions(ActionEvent event) {
        Input input = new GetCityAttractionsCommand.Input(city.getId());
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            GetCityAttractionsCommand.Output output = response.getOutput(GetCityAttractionsCommand.Output.class);

            id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
            name_column.setCellValueFactory(new PropertyValueFactory<>("attraction_name"));


            ObservableList<attractionInfo> oblist = FXCollections.observableArrayList();
            for (int i = 0; i < output.result.size(); i++)
                oblist.add(new attractionInfo(output.result.get(i).getId(), output.result.get(i).getName()));

            tableList.setItems(oblist);

        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    /**
     * class to help with tha table list
     */
    public class attractionInfo {
        private Integer id, myIndex;
        private String attraction_name;
        private String time;

        public attractionInfo(Integer id, String attraction_name) {
            this.id = id;
            this.attraction_name = attraction_name;
        }

        public attractionInfo(Integer id, String attraction_name, String time) {
            this.id = id;
            this.attraction_name = attraction_name;
            this.time = time;
            this.myIndex = index;
        }


        public Integer getId() {
            return id;
        }

        public String getAttraction_name() {
            return attraction_name;
        }

        public String getTime() {
            return time;
        }

        public Integer getMyIndex() {
            return myIndex;
        }
    }
/**
 * loads the viewer
 */
    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddTour.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }


}

