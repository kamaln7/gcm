package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

public class ApprovePriceController{

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ApprovePrice.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public class CityPrices{
        private  SimpleIntegerProperty id;
        private  SimpleStringProperty city_country;
        private  SimpleStringProperty purchase_price;
        private  SimpleStringProperty sub_price;

        public CityPrices(int id, String city, String purchase, String sub)
        {
            this.id=new SimpleIntegerProperty(id);
            this.city_country = new SimpleStringProperty(city);
            this.purchase_price = new SimpleStringProperty(purchase);
            this.sub_price = new SimpleStringProperty(sub);
        }

        public int getId() {return id.get();}
        public SimpleIntegerProperty idProperty() {return id;}
        public String getCity_country() {return city_country.get();}
        public SimpleStringProperty city_countryProperty() {return city_country; }
        public String getPurchase_price() {return purchase_price.get();}
        public SimpleStringProperty purchase_priceProperty() {return purchase_price;}
        public String getSub_price() {return sub_price.get();}
        public SimpleStringProperty sub_priceProperty() {return sub_price;}
    }

    @FXML
    void getPrice(ActionEvent event) {

        Input input = new ReviewPendingPriceChangesCommand.Input();
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ReviewPendingPriceChangesCommand.Output output = response.getOutput(ReviewPendingPriceChangesCommand.Output.class);

            id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
            city_column.setCellValueFactory(new PropertyValueFactory<>("city_country"));
            purchase_column.setCellValueFactory(new PropertyValueFactory<>("purchase_price"));
            sub_column.setCellValueFactory(new PropertyValueFactory<>("sub_price"));

            ObservableList<CityPrices> oblist = FXCollections.observableArrayList();
            for(int i=0;i<output.result.size(); i ++)
                oblist.add(new CityPrices(output.result.get(i).getId(),
                        output.result.get(i).getName()+", " + output.result.get(i).getCountry(),
                        output.result.get(i).getPurchasePrice() + " -> " + output.result.get(i).getNewPurchasePrice() ,
                        output.result.get(i).getSubscriptionPrice()+ " -> " + output.result.get(i).getNewSubscriptionPrice()));

            tableList.setItems(oblist);

        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void ApprovePrice(ActionEvent event) {
        //Get ID of selected city from the table

        TablePosition pos = tableList.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        CityPrices item = tableList.getItems().get(row);
        int id = item.getId();

        //Send this ID to DATA BASE for updateing price

        Input input = new ApprovePriceCommand.Input(id);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ApprovePriceCommand.Output output = response.getOutput(ApprovePriceCommand.Output.class);

            // delete the selcted row
            tableList.getItems().remove(item);

        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void DeclinePrice(ActionEvent event) {
        //Get ID of selected city from the table

        TablePosition pos = tableList.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        CityPrices item = tableList.getItems().get(row);
        int id = item.getId();

        //Send this ID to DATA BASE

        Input input = new DeclinePriceCommand.Input(id);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            DeclinePriceCommand.Output output = response.getOutput(DeclinePriceCommand.Output.class);

            // delete the selcted row
            tableList.getItems().remove(item);

        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    private TableView<CityPrices> tableList;

    @FXML
    private TableColumn<CityPrices, String> city_column;

    @FXML
    private TableColumn<CityPrices, String> purchase_column;

    @FXML
    private TableColumn<CityPrices, String> sub_column;

    @FXML
    private TableColumn<CityPrices, String> id_column;


}

