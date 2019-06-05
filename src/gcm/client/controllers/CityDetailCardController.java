package gcm.client.controllers;

import gcm.database.models.City;
import gcm.database.models.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CityDetailCardController extends AnchorPane {
    @FXML
    private Text titleText;

    @FXML
    private Text mapsText;
    @FXML
    private Text toursText;
    @FXML
    private Text attractionsText;

    @FXML
    private TilePane mapsTilePane;

    private City city;

    public CityDetailCardController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gcm/client/views/CityDetailCard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCity(City city) {
        if (city == null) return;
        this.city = city;

        titleText.setText(String.format("%s, %s", city.getName(), city.getCountry()));
        attractionsText.setText(String.format("%s attractions", city._extraInfo.getOrDefault("attractionCount", "-1")));
        toursText.setText(String.format("%s tours", city._extraInfo.getOrDefault("tourCount", "-1")));
        mapsText.setText(String.format("%s maps", city._extraInfo.getOrDefault("mapCount", "-1")));
    }

    public void setMaps(List<Map> maps) {
        mapsTilePane.getChildren().setAll(
                maps
                        .parallelStream()
                        .map(map -> {
                            MapDetailCardController md = new MapDetailCardController();
                            md.setMap(map);

                            return md;
                        })
                        .collect(Collectors.toList())
        );
    }

    public void buy(ActionEvent event) {
        new Alert(Alert.AlertType.WARNING, "Yalla waseem").show();
    }
}
