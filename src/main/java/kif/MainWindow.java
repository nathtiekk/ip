package kif;

import javafx.fxml.FXML;

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

    private final Image kifImage;
    private final Image userImage;

    /**
     * Initializes the MainWindow with necessary UI components.
     */
    public MainWindow() {
        this.kifImage = loadImage("/images/WhatsApp Image 2025-02-19 at 11.27.22.jpeg");
        this.userImage = loadImage("/images/WhatsApp Image 2025-02-19 at 11.27.24.jpeg");
    }

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        showWelcomeMsg();
        showExistingTasks();
    }

    /**
     * Loads an image resource.
     *
     * @param path The path to the image resource.
     * @return The loaded Image.
     */
    private Image loadImage(String path) {
        return new Image(this.getClass().getResourceAsStream(path));
    }

    /**
     * Loads and displays existing user tasks.
     */
    private void showExistingTasks() {
        Storage.initialiseUserTasks();
        dialogContainer.getChildren().add(Ui.DialogBox.getKifDialog(Task.listUserTask(), kifImage));
    }

    /**
     * Displays the welcome message.
     */
    public void showWelcomeMsg() {
        dialogContainer.getChildren().add(Ui.DialogBox.getKifDialog(Ui.getIntroductionMessage(), kifImage));
    }

    /**
     * Handles user input by displaying the user's message and generating Kif's response.
     * Clears the input field after processing.
     */
    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        if (userText.isBlank()) {
            return;
        }

        String kifText = Kif.getResponse(userText);
        dialogContainer.getChildren().addAll(
                Ui.DialogBox.getUserDialog(userText, userImage),
                Ui.DialogBox.getKifDialog(kifText, kifImage)
        );
        userInput.clear();
    }
}