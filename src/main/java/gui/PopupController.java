package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logic.ErrorCodes;
import logic.GUIConnector;
import logic.Logic;
import logic.Player;

import java.util.ArrayList;
import java.util.Optional;

/**
 * class to collect player info for race preparation
 */
public class PopupController {

    //max characters for player names
    private static final int MAX_CHARACTERS = 20;

    private Stage stage;
    private Logic logic;
    private UserInterfaceController userInterfaceController;
    private static final String LIGHT_BLUE = "#35baf6";
    private ArrayList<TextField> textFields;
    private ButtonBar activeButtons;
    private ButtonBar AIButtons;
    private GUIConnector guiCon;

    @FXML
    private RadioButton p1Active_radioButton;
    @FXML
    private RadioButton p2Active_radioButton;
    @FXML
    private RadioButton p3Active_radioButton;
    @FXML
    private RadioButton p4Active_radioButton;
    @FXML
    private RadioButton p1AI_radioButton;
    @FXML
    private RadioButton p2AI_radioButton;
    @FXML
    private RadioButton p3AI_radioButton;
    @FXML
    private RadioButton p4AI_radioButton;
    @FXML
    private TextField p1Name_textField;
    @FXML
    private TextField p2Name_textField;
    @FXML
    private TextField p3Name_textField;
    @FXML
    private TextField p4Name_textField;

    //Getters and Setters
    public void setStage(Stage stage) {
        this.stage = stage;
    }



    /**
     * limits the characters of the player names to a certain number
     * @param tf textField to be limited
     */
    private void limitTextFieldCharacters(TextField tf) {
        tf.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (tf.getText().length() >= MAX_CHARACTERS) {
                event.consume();
            }
        });
    }

    /**
     * initializes popup controller
     * @param p player information
     * @param logic instance of logic
     * @param userInterfaceController instance of controller for Play.fxml
     * @param guiCon connector to gui output
     */
    public void initialize(Player[] p, Logic logic, UserInterfaceController userInterfaceController,
                           GUIConnector guiCon) {
        stage.setWidth(600);
        stage.setHeight(430);
        stage.setMaxWidth(600);
        stage.setMaxHeight(430);
        stage.setMinWidth(600);
        stage.setMinHeight(430);
        stage.setTitle("Player Settings");
        stage.initStyle(StageStyle.UNDECORATED);

        textFields = new ArrayList<>();
        textFields.add(p1Name_textField);
        textFields.add(p2Name_textField);
        textFields.add(p3Name_textField);
        textFields.add(p4Name_textField);
        activeButtons = new ButtonBar();
        activeButtons.getButtons().addAll(p1Active_radioButton,p2Active_radioButton,
                p3Active_radioButton,p4Active_radioButton);
        AIButtons = new ButtonBar();
        AIButtons.getButtons().addAll(p1AI_radioButton,p2AI_radioButton,p3AI_radioButton,p4AI_radioButton);
        for (int i = 0; i < Logic.MAX_PLAYER_NUMBER; i++) {
            ((RadioButton)activeButtons.getButtons().get(i)).setSelected(p[i].isActive());
            ((RadioButton)AIButtons.getButtons().get(i)).setSelected(p[i].isAI());
            textFields.get(i).setText(p[i].getName());
            limitTextFieldCharacters(textFields.get(i));
        }
        p1Name_textField.setStyle("-fx-background-color: " + Logic.P1_COLOR+";");
        p2Name_textField.setStyle("-fx-background-color: " + Logic.P2_COLOR+";");
        p3Name_textField.setStyle("-fx-background-color: " + Logic.P3_COLOR+";");
        p4Name_textField.setStyle("-fx-background-color: " + Logic.P4_COLOR+";");

        this.userInterfaceController = userInterfaceController;
        this.logic = logic;
        this.guiCon = guiCon;
    }

    /**
     * handles user wanting to exit player preparation
     */
    public void handleGoBackButton() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Interrupt Race Preparations?");
        alert.setContentText("Going Back now will stop the Race Preparations");
        ButtonType btnYes = new ButtonType("Yes");
        ButtonType btnNo = new ButtonType("No");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnYes, btnNo, btnCancel);
        Optional result = alert.showAndWait();
        if (result.get() == btnYes) {
            userInterfaceController.stopRaceOrPreparations(false, false);
            stage.close();
        }
    }

    /**
     * handles user wanting to confirm race start
     */
    public void handleConfirmButton() {
        byte activePlayers = 0;
        Player[] newPlayerInfo = new Player[Logic.MAX_PLAYER_NUMBER];
        Player[] oldPlayerInfo = logic.getPlayers();
        for (int i = 0; i < Logic.MAX_PLAYER_NUMBER; i++) {
            newPlayerInfo[i] = new Player(((RadioButton) activeButtons.getButtons().get(i)).isSelected(),
                    ((RadioButton) AIButtons.getButtons().get(i)).isSelected(), textFields.get(i).getText(),
                    oldPlayerInfo[0].getLastPos(), oldPlayerInfo[0].getCurrentPos(), oldPlayerInfo[0].getLap());
            if (newPlayerInfo[i].isActive()) {
                activePlayers++;
            }
        }
        if (activePlayers > 0) {
            logic.setPlayers(newPlayerInfo);
            stage.close();
        } else {
            guiCon.outputErrorMessage(ErrorCodes.NoActivePlayers, "");
        }
    }
}
