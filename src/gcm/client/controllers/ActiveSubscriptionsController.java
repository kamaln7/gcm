package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindActiveSubscriptionByUserIDCommand;
import gcm.commands.FindCityByIDCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ActiveSubscriptionsController {
    @FXML
    private TableView<subscriptionInfo> tableList;

    @FXML
    private TableColumn<subscriptionInfo, String> city_country_column;

    @FXML
    private TableColumn<subscriptionInfo, String> expiration_date_column;

    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) tableList.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    @FXML
    void showCityMapsAttractionsTours(ActionEvent event) {
        TablePosition pos = tableList.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        subscriptionInfo item = tableList.getItems().get(row);

        try {
            // show the attractions to the costumer
            ShowBoughtCityAttractionsController.loadView(new Stage(), Integer.parseInt(item.getCityID()));
            //show the maps to the costumer
            ShowBoughtCityMapsController.loadView(new Stage(), Integer.parseInt(item.getCityID()));
            //show the Tours to the costumer
            ShowBoughtCityToursController.loadView(new Stage(), Integer.parseInt(item.getCityID()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCities(User user) {
        //command
        Input input = new FindActiveSubscriptionByUserIDCommand.Input(user.getId());
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindActiveSubscriptionByUserIDCommand.Output output = response.getOutput(FindActiveSubscriptionByUserIDCommand.Output.class);

            city_country_column.setCellValueFactory(new PropertyValueFactory<>("city_country"));
            expiration_date_column.setCellValueFactory(new PropertyValueFactory<>("expiration_date"));


            ObservableList<subscriptionInfo> oblist = FXCollections.observableArrayList();
            for (int i = 0; i < output.subscriptions.size(); i++) {

                Input input2 = new FindCityByIDCommand.Input(output.subscriptions.get(i).getCityId());
                Response response2 = ClientGUI.getClient().sendInputAndWaitForResponse(input2);
                FindCityByIDCommand.Output output2 = response2.getOutput(FindCityByIDCommand.Output.class);

                String city_country = output2.city.getName() + ", " + output2.city.getCountry();
                oblist.add(new subscriptionInfo(String.valueOf(output.subscriptions.get(i).getCityId()), city_country, String.valueOf(output.subscriptions.get(i).getToDate())));
            }
            tableList.setItems(oblist);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void loadView(Stage primaryStage, User user) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ActiveSubscriptions.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        ActiveSubscriptionsController controller = loader.getController();
        controller.setCities(user);

        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public class subscriptionInfo {

        private String cityID, city_country, expiration_date;

        public subscriptionInfo(String cityID, String city_country, String expiration_date) {
            this.cityID = cityID;
            this.city_country = city_country;
            this.expiration_date = expiration_date;
        }

        public String getCityID() {
            return cityID;
        }

        public String getCity_country() {
            return city_country;
        }

        public String getExpiration_date() {
            return expiration_date;
        }
    }
}

