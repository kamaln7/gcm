package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.Map;
import gcm.database.models.Tour;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class ShowBoughtCityToursController {




    @FXML
    private TextArea description_field;

    @FXML
    private Button next;

    @FXML
    private Button previous;

    @FXML
    private TableView<attractionInfo> table;

    @FXML
    private TableColumn<attractionInfo, String> index_column;

    @FXML
    private TableColumn<attractionInfo, String> attraction_name_column;

    @FXML
    private TableColumn<attractionInfo, String> time_column;



    @FXML
    void next(ActionEvent event) {
        current++;
        showTour(tours.get(current));
        if (current==size-1){
            next.setVisible(false);
        }

        previous.setVisible(true);
    }

    @FXML
    void previous(ActionEvent event) {
        current--;
        showTour(tours.get(current));
        if (current==0){
            previous.setVisible(false);
        }

        next.setVisible(true);
    }

    private  int myCityID;
    private List<Tour> tours;
    private int current=0;
    private int size;
    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) description_field.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    private void setCity(int cityID){
        this.myCityID=cityID;
    }

    private void setTours(){
        Input input = new FindToursByCityIdCommand.Input(myCityID);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindToursByCityIdCommand.Output output = response.getOutput(FindToursByCityIdCommand.Output.class);
            this.tours = output.tours;
            this.size= tours.size();

            //show the first map
            showTour(this.tours.get(current));
            previous.setVisible(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showTour(Tour tour){
        description_field.setText(tour.getDescription());


        //get the attractions in the tour
        Input input = new FindAttractionsByTourIdCommand.Input(tour.getId());
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindAttractionsByTourIdCommand.Output output = response.getOutput(FindAttractionsByTourIdCommand.Output.class);

            index_column.setCellValueFactory(new PropertyValueFactory<>("index"));
            attraction_name_column.setCellValueFactory(new PropertyValueFactory<>("name"));
            time_column.setCellValueFactory(new PropertyValueFactory<>("time"));

            ObservableList<attractionInfo> oblist = FXCollections.observableArrayList();
            for(int i=0;i<output.attractions.size(); i ++)
                oblist.add(new attractionInfo(String.valueOf(output.tourAttractionList.get(i).getOrderIndex()), output.attractions.get(i).getName(),String.valueOf(output.tourAttractionList.get(i).getTime())));

            table.setItems(oblist);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadView(Stage primaryStage, int cityID) throws IOException {
        URL url = ShowBoughtCityToursController.class.getResource("/gcm/client/views/ShowBoughtCityTours.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        ShowBoughtCityToursController controller = loader.getController();
        controller.setCity(cityID);
        controller.setTours();

        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    public class attractionInfo{

        private String index, name, time;

        public attractionInfo(String index, String name, String time) {
            this.index = index;
            this.name = name;
            this.time = time;
        }

        public String getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public String getTime() {
            return time;
        }
    }




}

