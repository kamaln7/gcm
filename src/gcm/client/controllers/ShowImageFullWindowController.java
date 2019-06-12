package gcm.client.controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ShowImageFullWindowController implements Initializable {
    public ImageView imageView;
    public AnchorPane anchorPane;

    public static void loadViewAndWait(String title, byte[] imageBytes) throws IOException {
        URL url = LoginController.class.getResource("/gcm/client/views/ShowImageFullWindow.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        // set image
        ShowImageFullWindowController controller = loader.getController();
        controller.setImage(imageBytes);

        // setting the stage
        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.setResizable(true);
        primaryStage.showAndWait();
    }

    public void setImage(byte[] imageBytes) throws IOException {
        BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        Image image = SwingFXUtils.toFXImage(bImage, null);
        imageView.setImage(image);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(anchorPane.widthProperty());
        imageView.fitHeightProperty().bind(anchorPane.heightProperty());
    }
}
