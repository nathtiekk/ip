package kif;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Kif kif;

    private Image kifImage = new Image(this.getClass().getResourceAsStream(
            "/images/WhatsApp Image 2025-02-19 at 11.27.22.jpeg"));
    private Image userImage = new Image(this.getClass().getResourceAsStream(
            "/images/WhatsApp Image 2025-02-19 at 11.27.24.jpeg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        showWelcomeMsg();
        showExistingTasks();
    }

    private void showExistingTasks() {
        Storage.initialiseUserTasks();
        dialogContainer.getChildren().addAll(
                Ui.DialogBox.getKifDialog(Task.listUserTask(), kifImage)
        );
    }

    public void showWelcomeMsg() {
        dialogContainer.getChildren().addAll(
                Ui.DialogBox.getKifDialog(Ui.introduction(), kifImage)
        );
    }

    /** Injects the Kif instance */
    public void setKif(Kif k) {
        kif = k;
    }

    /**
     * Creates two dialog boxes, one for user input and the other containing Kif's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        String kifText = Kif.getResponse(userText);
        dialogContainer.getChildren().addAll(
                Ui.DialogBox.getUserDialog(userText, userImage),
                Ui.DialogBox.getKifDialog(kifText, kifImage)
        );
        userInput.clear();
    }
}