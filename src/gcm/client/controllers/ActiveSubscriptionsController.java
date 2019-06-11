package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindActiveSubscriptionByUserIDCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.Subscription;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ActiveSubscriptionsController implements Initializable {
    @FXML
    private TableView<Subscription> tableList;

    @FXML
    private TableColumn<Subscription, String> city_country_column;

    @FXML
    private TableColumn<Subscription, String> expiration_date_column;
    @FXML
    private TableColumn<Subscription, Void> buttonCol;

    private ObservableList oblist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableList.setItems(oblist);
        city_country_column.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("cityTitle")));
        expiration_date_column.setCellValueFactory(new PropertyValueFactory<>("toDate"));
        buttonCol.setCellFactory(new Callback<TableColumn<Subscription, Void>, TableCell<Subscription, Void>>() {
            @Override
            public TableCell<Subscription, Void> call(TableColumn<Subscription, Void> param) {
                final TableCell<Subscription, Void> cell = new TableCell<Subscription, Void>() {
                    private final HBox btns = new HBox();

                    private final Button attractionsBtn = new Button("Attractions");
                    private final Button mapsBtn = new Button("Maps");
                    private final Button toursBtn = new Button("Tours");

                    {
                        attractionsBtn.setOnAction((ActionEvent event) -> {
                            try {
                                ShowBoughtCityAttractionsController.loadView(new Stage(), getSubscription().getCityId());
                            } catch (IOException e) {
                                ClientGUI.showErrorTryAgain();
                                e.printStackTrace();
                            }
                        });
                        mapsBtn.setOnAction((ActionEvent event) -> {
                            try {
                                ShowBoughtCityMapsController.loadView(new Stage(), getSubscription().getCityId());
                            } catch (IOException e) {
                                ClientGUI.showErrorTryAgain();
                                e.printStackTrace();
                            }
                        });
                        toursBtn.setOnAction((ActionEvent event) -> {
                            try {
                                ShowBoughtCityToursController.loadView(new Stage(), getSubscription().getCityId());
                            } catch (IOException e) {
                                ClientGUI.showErrorTryAgain();
                                e.printStackTrace();
                            }
                        });

                        btns.setSpacing(5);
                        btns.getChildren().addAll(attractionsBtn, mapsBtn, toursBtn);
                    }

                    public Subscription getSubscription() {
                        return getTableView().getItems().get(getIndex());
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btns);
                        }
                    }
                };

                return cell;
            }
        });
    }

    private void setUser(Integer userId) {
        //command
        Input input = new FindActiveSubscriptionByUserIDCommand.Input(userId);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindActiveSubscriptionByUserIDCommand.Output output = response.getOutput(FindActiveSubscriptionByUserIDCommand.Output.class);

            oblist.setAll(output.subscriptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadView(Stage primaryStage, Integer userId) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ActiveSubscriptions.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        ActiveSubscriptionsController controller = loader.getController();
        controller.setUser(userId);

        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Active Subscriptions - GCM 2019");
        primaryStage.show();
    }
}

