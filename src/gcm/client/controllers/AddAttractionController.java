package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.AddAttractionAndUpdateMapImageCommand;
import gcm.commands.Input;
import gcm.commands.ReadMapImageById;
import gcm.commands.Response;
import gcm.database.models.Attraction;
import gcm.database.models.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import static java.awt.Color.BLACK;

public class AddAttractionController {

    @FXML
    private AnchorPane pane;

    @FXML
    private ImageView mapImg;

    @FXML
    private TextField Xcord;

    @FXML
    private TextField Ycord;

    @FXML
    public ChoiceBox<String> attraction_choiceBox;

    @FXML
    private ChoiceBox<String> accessible_choiceBox;

    @FXML
    private TextField attraction_name_field;

    @FXML
    private TextField attraction_location_field;

    @FXML
    private TextArea description_field;

    private Map map;

    @FXML
    private TextField mapTF;

    private double X, Y;

    private byte[] originalImageBytes, newImageBytes;
    private Image originalImage;

    private Attraction attraction;

    /**
     * initialize the viewer
     */
    public void initialize() {
        attraction_choiceBox.getItems().setAll(Attraction.types);
        attraction_choiceBox.getSelectionModel().selectFirst();

        accessible_choiceBox.getItems().add("Yes");
        accessible_choiceBox.getItems().add("No");
        accessible_choiceBox.setValue("No");
        try {
            BufferedImage bufferedImage = ImageIO.read(this.getClass().getResourceAsStream("/gcm/client/staticFiles/thumb-1920-44975.jpg"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            mapImg.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * load the viewer
     */
    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddAttraction.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Attraction - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * load the viewer
     */
    public static Attraction loadViewAndWait(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddAttraction.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        AddAttractionController controller = loader.getController();
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Attraction - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.showAndWait();
        return controller.getAttraction();
    }

    /**
     * choose map and add attraction to the map image
     *
     * @param event
     */
    @FXML
    void chooseMap(ActionEvent event) {
        try {
            this.map = AdminTablePickerMapController.loadViewAndWait(new Stage());
            this.mapTF.setText(String.format("%s - %s", this.map.getTitle(), this.map._extraInfo.get("cityTitle")));

            Input input = new ReadMapImageById.Input(this.map.getId());
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ReadMapImageById.Output output = response.getOutput(ReadMapImageById.Output.class);

            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(output.imgBytes));
            Image image = SwingFXUtils.toFXImage(bImage, null);
            originalImage = image;
            mapImg.setImage(image);
            (new Alert(Alert.AlertType.INFORMATION, "please fill all the fields before you click on the map")).show();
            mapImg.setOnMouseClicked(e -> {
                Xcord.setText(String.valueOf(e.getX()));
                Ycord.setText(String.valueOf(e.getY()));
                X = e.getX();
                Y = e.getY();

                try {
                    ByteArrayOutputStream s = new ByteArrayOutputStream();
                    ImageIO.write(createImageWithText(), "jpg", s);
                    newImageBytes = s.toByteArray();

                    BufferedImage bImage2 = ImageIO.read(new ByteArrayInputStream(newImageBytes));
                    Image image2 = SwingFXUtils.toFXImage(bImage2, null);
                    mapImg.setImage(image2);
                    s.close();
                } catch (Exception e1) {
                    ClientGUI.showErrorTryAgain();
                    e1.printStackTrace();
                }
            });

        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }

    }

    /**
     * close the viewer
     *
     * @param event
     */
    @FXML
    private void finishJop(ActionEvent event) {
        if (this.map == null || this.attraction_name_field.getText().equals("") || this.attraction_location_field.getText().equals("") || this.description_field.getText().equals("") || this.Xcord.getText().equals("")) {
            (new Alert(Alert.AlertType.INFORMATION, "please fill all the fields and then click on the map")).show();
            return;
        }
        try {
            boolean accessibility = getAccessibility();

            Input input2 = new AddAttractionAndUpdateMapImageCommand.Input(this.map.getId(), attraction_choiceBox.getValue(), attraction_name_field.getText(), attraction_location_field.getText(), newImageBytes, accessibility, description_field.getText());

            Response response2 = ClientGUI.getClient().sendInputAndWaitForResponse(input2);
            AddAttractionAndUpdateMapImageCommand.Output output2 = response2.getOutput(AddAttractionAndUpdateMapImageCommand.Output.class);
            (new Alert(Alert.AlertType.INFORMATION, "Attraction successfully added!")).show();
            setAttraction(output2.attraction);
        } catch (Attraction.AlreadyExists x) {
            (new Alert(Alert.AlertType.ERROR, "Attraction already exists.")).show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    /**
     * draw the new attraction on th image
     *
     * @return
     * @throws IOException
     */
    private BufferedImage createImageWithText() throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(BLACK);

        double aspectRatio = bufferedImage.getWidth() / bufferedImage.getHeight();
        double realWidth = Math.min(mapImg.getFitWidth(), mapImg.getFitHeight() * aspectRatio);
        double realHeight = Math.min(mapImg.getFitHeight(), mapImg.getFitWidth() / aspectRatio);

        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2d.drawString("•" + attraction_name_field.getText(), Math.round(bufferedImage.getWidth() * X / realWidth), Math.round(bufferedImage.getHeight() * Y / realHeight));
        g2d.dispose();

        return bufferedImage;
    }

    private boolean getAccessibility() {
        if (accessible_choiceBox.getValue().equals("YES")) {
            return true;
        }
        return false;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
        ((Stage) description_field.getScene().getWindow()).close();
    }
}

