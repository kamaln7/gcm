package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ShowPurchaseHistoryController {
    @FXML
    private TableView<PurchaseHistory> tableView;

    @FXML
    private TableColumn<PurchaseHistory, Integer> idCol;

    @FXML
    private TableColumn<PurchaseHistory, String> dateCol;

    @FXML
    private TableColumn<PurchaseHistory, Double> priceCol;

    @FXML
    private TableColumn<PurchaseHistory, String> cityCol;

    public int myUserID;

    public class PurchaseHistory{
        private SimpleIntegerProperty id;
        private  SimpleStringProperty city_country;
        private SimpleDoubleProperty purchase_price;
        private SimpleStringProperty date;


        public PurchaseHistory(int id, String city, double purchase_price, String date)
        {
            this.id=new SimpleIntegerProperty(id);
            this.city_country = new SimpleStringProperty(city);
            this.purchase_price = new SimpleDoubleProperty(purchase_price);
            this.date =  new SimpleStringProperty(date);

        }

        public int getId() {return id.get();}
        public SimpleIntegerProperty idProperty() {return id;}
        public String getCity_country() {return city_country.get();}
        public SimpleStringProperty city_countryProperty() {return city_country; }
        public double getPurchase_price() {return purchase_price.get();}
        public SimpleDoubleProperty purchase_priceProperty() {return purchase_price;}
        public String getdate() {return date.get();}
        public SimpleStringProperty dateProperty() {return date; }

    }


    public void show(int currentUserID) {


        Input input = new FindPurchaseByUserIDCommand.Input(currentUserID);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindPurchaseByUserIDCommand.Output output = response.getOutput(FindPurchaseByUserIDCommand.Output.class);

            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            priceCol.setCellValueFactory(new PropertyValueFactory<>("purchase_price"));
            cityCol.setCellValueFactory(new PropertyValueFactory<>("city_country"));

            ObservableList<PurchaseHistory> oblist = FXCollections.observableArrayList();
            for(int i=0;i<output.purchases.size(); i ++) {
                String cityname;
                String countryname;

                Input input2 = new FindCityByIDCommand.Input(output.purchases.get(i).getCityId());
                try{
                    Response response1 = ClientGUI.getClient().sendInputAndWaitForResponse(input2);
                    FindCityByIDCommand.Output output1 = response1.getOutput(FindCityByIDCommand.Output.class);

                    cityname = output1.city.getName();
                    countryname = output1.city.getCountry();


                    oblist.add(new PurchaseHistory(output.purchases.get(i).getId(),
                            cityname + ", " + countryname,
                            output.purchases.get(i).getPrice(),
                            output.purchases.get(i).getCreatedAt().toString()));

                }
                catch (City.NotFound e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
                    alert.show();
                } catch (Exception e) {
                    ClientGUI.showErrorTryAgain();
                    e.printStackTrace();
                }

            }

            tableView.setItems(oblist);

        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }

    }

    public static void loadView(Stage stage,int currentUserID) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ShowPurchaseHistory.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);
        ShowPurchaseHistoryController controller = loader.getController();

        controller.show(currentUserID);

        // setting the stage
        stage.setScene(scene);

        stage.setResizable(true);

        stage.showAndWait();

    }

}
