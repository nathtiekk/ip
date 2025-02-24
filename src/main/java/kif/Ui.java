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

    private static final String SEPARATOR = "____________________________________________________________";

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Ui.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Formats messages with a separator.
     *
     * @param lines The lines to format.
     * @return The formatted message.
     */
    public static String formatMessage(String... lines) {
        return SEPARATOR + System.lineSeparator() + String.join(System.lineSeparator(), lines) + System.lineSeparator() + SEPARATOR;
    }

    /**
     * Returns the introduction message for Kif.
     */
    public static String getIntroductionMessage() {
        return formatMessage("Hello! I'm Kif", "What can I do for you?");
    }

    /**
     * Closes the GUI application.
     */
    public static void closeGui() {
        Platform.exit();
    }

    public static String getUnknownCommandMessage() {
        return "I'm sorry, but I don't understand that command. Please try again!";
    }

    /**
     * Returns the goodbye message.
     */
    public static String getGoodbyeMessage() {
        return formatMessage("Kif: Bye. Hope to see you again soon!");
    }

    /**
     * Returns a message indicating that there is no command to undo.
     */
    public static String getCannotUndoMessage() {
        return formatMessage("Kif: No command to undo.");
    }

    /**
     * Represents a dialog box consisting of an ImageView to represent the speaker's face and a label containing text from the speaker.
     */
    public static class DialogBox extends HBox {

        @FXML
        private Label dialog;
        @FXML
        private ImageView displayPicture;

        private DialogBox(String text, Image img) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
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
            ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
            Collections.reverse(children);
            getChildren().setAll(children);
            setAlignment(Pos.TOP_LEFT);
        }

        /**
         * Creates a dialog box for user input.
         */
        public static DialogBox getUserDialog(String text, Image img) {
            return new DialogBox(text, img);
        }

        /**
         * Creates a dialog box for Kif's response and flips it.
         */
        public static DialogBox getKifDialog(String text, Image img) {
            DialogBox db = new DialogBox(text, img);
            db.flip();
            return db;
        }
    }
}
