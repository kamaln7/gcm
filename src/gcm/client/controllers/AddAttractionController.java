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


import java.awt.Font;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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

    private int X;
    private int Y;

    public void initialize() {
        attraction_choiceBox.getItems().add("Museum");
        attraction_choiceBox.getItems().add("Historical Place");
        attraction_choiceBox.getItems().add("Hotel");
        attraction_choiceBox.getItems().add("Restaurant");
        attraction_choiceBox.getItems().add("Public Institution");
        attraction_choiceBox.getItems().add("Park");
        attraction_choiceBox.getItems().add("Parking Lot");
        attraction_choiceBox.getItems().add("Cinema");
        attraction_choiceBox.setValue("Museum");

        accessible_choiceBox.getItems().add("YES");
        accessible_choiceBox.getItems().add("NO");
        accessible_choiceBox.setValue("NO");
        try {
            BufferedImage bufferedImage = ImageIO.read(new File("thumb-1920-44975.jpg"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            mapImg.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        chooseMap(null);
    }

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddAttraction.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void finishJop(ActionEvent event) {
        Stage stage = (Stage) pane.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

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
            mapImg.setImage(image);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please fill the fields down below, \nbefore you click on the map image!");
            alert.show();
            mapImg.setOnMouseClicked(e -> {

                Xcord.setText(String.valueOf(e.getX()));
                Ycord.setText(String.valueOf(e.getY()));
                X = (int) Math.round(e.getX());
                Y = (int) Math.round(e.getY());
                mapImg.setOnMouseClicked(null);

                try {
                    ByteArrayOutputStream s = new ByteArrayOutputStream();
                    ImageIO.write(createImageWithText(), "jpg", s);
                    byte[] res = s.toByteArray();

                    BufferedImage bImage2 = ImageIO.read(new ByteArrayInputStream(res));
                    Image image2 = SwingFXUtils.toFXImage(bImage2, null);
                    mapImg.setImage(image2);
                    s.close();

                    boolean accessibility = getAccessibility();

                    Input input2 = new AddAttractionAndUpdateMapImageCommand.Input(this.map.getId(), attraction_choiceBox.getValue(), attraction_name_field.getText(), attraction_location_field.getText(), res, accessibility, description_field.getText() );

                    Response response2 = ClientGUI.getClient().sendInputAndWaitForResponse(input2);
                    response2.getOutput(AddAttractionAndUpdateMapImageCommand.Output.class);
                    
                } catch (Attraction.AlreadyExists x) {
                    Alert alert2 = new Alert(Alert.AlertType.ERROR, "attraction already exist");
                    alert2.show();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }

    }

    private BufferedImage createImageWithText() throws IOException {

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(mapImg.getImage(), null);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(BLACK);


        g2d.setFont(new Font("SansSerif", Font.BOLD, 60));
        g2d.drawString("•" + attraction_name_field.getText(), bufferedImage.getWidth() * X / 672, bufferedImage.getHeight() * Y / 376);

        //ImageIO.write(bufferedImage, "png", new File("afterAdding.png") );
        g2d.dispose();

        return bufferedImage;
    }
    private boolean getAccessibility(){
        if (accessible_choiceBox.getValue().equals("YES")){
            return true;
        }
        return false;
    }

}

