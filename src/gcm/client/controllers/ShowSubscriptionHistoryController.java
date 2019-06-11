package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindSubscriptionByUserIDCommand;
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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class ShowSubscriptionHistoryController implements Initializable {
    @FXML
    private TableView tableView;

    @FXML
    private TableColumn<Subscription, String> dateCol;

    @FXML
    private TableColumn<Subscription, Double> priceCol;

    @FXML
    private TableColumn<Subscription, String> cityCol;

    @FXML
    private TableColumn<Subscription, String> newCol;

    @FXML
    private TableColumn<Subscription, String> fromCol;

    @FXML
    private TableColumn<Subscription, String> toCol;
    @FXML
    private TableColumn<Subscription, String> activeCol;
    @FXML
    private TableColumn<Subscription, Void> buttonCol;

    private ObservableList oblist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setItems(oblist);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        cityCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("cityTitle")));
        newCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRenew() ? "Renew" : "New"));
        fromCol.setCellValueFactory(new PropertyValueFactory<>("fromDate"));
        toCol.setCellValueFactory(new PropertyValueFactory<>("toDate"));
        activeCol.setCellValueFactory(cell -> {
            Subscription s = cell.getValue();
            Date now = new Date();
            Boolean active = s.getFromDate().before(now) && s.getToDate().after(now);
            return new SimpleStringProperty(active ? "Yes" : "No");
        });
        buttonCol.setCellFactory(new Callback<TableColumn<Subscription, Void>, TableCell<Subscription, Void>>() {
            @Override
            public TableCell<Subscription, Void> call(TableColumn<Subscription, Void> param) {
                final TableCell<Subscription, Void> cell = new TableCell<Subscription, Void>() {
                    private final Button btn = new Button("Renew");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Subscription subscription = getTableView().getItems().get(getIndex());
                            try {
                                Subscription newSubscription = RenewSubscriptionController.loadView(new Stage(), subscription, subscription.getPrice(), true);
                                if (newSubscription != null) {
                                    oblist.add(0, newSubscription);
                                }
                            } catch (Exception e) {
                                ClientGUI.showErrorTryAgain();
                                e.printStackTrace();
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });
    }

    public void show(int currentUserID) {
        Input input = new FindSubscriptionByUserIDCommand.Input(currentUserID);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindSubscriptionByUserIDCommand.Output output = response.getOutput(FindSubscriptionByUserIDCommand.Output.class);
            oblist.setAll(output.subscriptions);
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }

    }

    public static void loadView(Stage stage, int currentUserID) throws IOException {
        loadView(stage, currentUserID, true);
    }

    public static void loadView(Stage stage, int currentUserID, Boolean withButtonCol) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ShowSubscriptionHistory.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        ShowSubscriptionHistoryController controller = loader.getController();
        controller.show(currentUserID);
        controller.showButtonCol(withButtonCol);
        // setting the stage
        stage.setScene(scene);
        stage.setTitle("Subscription History - GCM 2019");
        stage.setResizable(true);
        stage.showAndWait();
    }

    public void showButtonCol(Boolean shown) {
        buttonCol.setVisible(shown);
    }
}
