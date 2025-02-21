package kif;

import java.io.IOException;
import java.util.Collections;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * A GUI for Kif using FXML.
 */
public class Ui extends Application {

    private Kif kif = new Kif();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Ui.class.getResource(
                    "/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setKif(kif);  // inject the Kif instance
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String introduction() {
        return """
               ____________________________________________________________
               Hello! I'm Kif
               What can I do for you?
               ____________________________________________________________
               """;
    }

    public static void closeGui() {
        Platform.exit();
    }

    public static String goodbye() {
        return """
               ____________________________________________________________
               Kif: Bye. Hope to see you again soon!
               ____________________________________________________________""";
    }

    /**
     * Represents a dialog box consisting of an ImageView to represent the speaker's face
     * and a label containing text from the speaker.
     */
    public static class DialogBox extends HBox {

        @FXML
        private Label dialog;
        @FXML
        private ImageView displayPicture;

        private DialogBox(String text, Image img) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource(
                        "/view/DialogBox.fxml"));
                fxmlLoader.setController(this);
                fxmlLoader.setRoot(this);
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            dialog.setText(text);
            displayPicture.setImage(img);
        }

        /**
         * Flips the dialog box such that the ImageView is on the left and text on the right.
         */
        private void flip() {
            ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
            Collections.reverse(tmp);
            getChildren().setAll(tmp);
            setAlignment(Pos.TOP_LEFT);
        }

        public static DialogBox getUserDialog(String text, Image img) {
            return new DialogBox(text, img);
        }

        public static DialogBox getKifDialog(String text, Image img) {
            var db = new DialogBox(text, img);
            db.flip();
            return db;
        }
    }
}
