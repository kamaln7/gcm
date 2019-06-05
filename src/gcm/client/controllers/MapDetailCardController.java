package gcm.client.controllers;

import gcm.database.models.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class MapDetailCardController extends AnchorPane {
    @FXML
    private Text titleText;
    @FXML
    private Text descriptionText;

    private Map map;

    public MapDetailCardController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gcm/client/views/MapDetailCard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMap(Map map) {
        if (map == null) {
            return;
        }

        this.map = map;
        titleText.setText(map.getTitle());
        descriptionText.setText(map.getDescription());
    }
}
