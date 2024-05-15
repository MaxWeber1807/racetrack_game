package gui;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.Duration;
import logic.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.util.Objects.isNull;

/**
 * Main class for the user interface.
 * This is where user input is handled
 *
 * @author Max Weber
 */
public class UserInterfaceController implements Initializable {

    private Stage stage;
    private Stage popupStage;

    private GameMode gameMode;
    private int currAnimationSpeed = Logic.HIGHEST_ANIM_SPEED;
    private MouseButton mouseButton;
    public static final double TARGET_RATIO = 16.0 / 9.0;


    @FXML
    private GridPane game_GP;
    @FXML
    private RadioMenuItem animSpeed_none;
    @FXML
    private RadioMenuItem animSpeed_low;
    @FXML
    private RadioMenuItem animSpeed_middle;
    @FXML
    private RadioMenuItem animSpeed_high;
    @FXML
    private MenuBar menu_bar;
    @FXML
    private TextField mapSaved_text;
    @FXML
    private Hyperlink startRace_link;
    @FXML
    private TextField mode_textfield;
    @FXML
    private HBox display_hbox;
    @FXML
    private RadioMenuItem editor_radioMenuItem;

    private GUIConnector guiCon;
    private Logic logic;

    //Getters and Setters
    public Stage getStage() {
        return stage;
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }



    /**
     * stops the race or the preparations depending on where in the progress the user is
     * @output true if there should be a confirmation alert, false if not
     * @fromEditor true if method is called from editor method, false if not
     * @return true if user confirmed the cancellation, false if not
     */
    public boolean stopRaceOrPreparations(boolean output, boolean fromEditor) {
        if (!logic.getInAnimation()) {
            if (output) {
                Alert stopPrepAlert = new Alert(Alert.AlertType.CONFIRMATION);
                stopPrepAlert.setTitle("Information");
                if (logic.getGameMode() == GameMode.Preparation_Mode) {
                    stopPrepAlert.setHeaderText("Stop Race Preparations");
                    stopPrepAlert.setContentText("Stopping the race preparations " +
                            "loses all progress from the current preparations.");
                } else {
                    this.gameMode = GameMode.Race_Mode;
                    stopPrepAlert.setHeaderText("Stop Race");
                    stopPrepAlert.setContentText("Stopping the race now will lose all progress made in the current race!");
                }
                ButtonType btnYes = new ButtonType("Yes");
                ButtonType btnNo = new ButtonType("No");
                ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                stopPrepAlert.getButtonTypes().setAll(btnYes, btnNo, btnCancel);
                Optional result = stopPrepAlert.showAndWait();

                if (result.get() == btnYes) {
                    this.mode_textfield.textProperty().set("");
                    this.startRace_link.setText("Start Race");
                    this.display_hbox.getChildren().clear();
                    logic.setGameMode(GameMode.Menu_Mode);
                    if (fromEditor) {
                        this.gameMode = GameMode.Editor_Mode;
                        activateEditorMode();
                    } else {
                        this.gameMode = GameMode.Menu_Mode;
                    }
                    return true;
                } else {
                    return false;
                }
            }
            this.mode_textfield.textProperty().set("");
            this.startRace_link.setText("Start Race");
            if (fromEditor) {
                this.gameMode = GameMode.Editor_Mode;
                activateEditorMode();
            } else {
                this.gameMode = GameMode.Menu_Mode;
            }
            return true;
        } else {
            guiCon.outputErrorMessage(ErrorCodes.GameInAnimation, "");
            return false;
        }
    }

    /**
     * helping method to set all the speed values
     * @param animSpeed animation speed
     */
    public void animValueSetter(int animSpeed) {
        switch (animSpeed) {
            case Logic.HIGHEST_ANIM_SPEED:
                animSpeed_high.setSelected(true);
                animSpeed_middle.setSelected(false);
                animSpeed_low.setSelected(false);
                animSpeed_none.setSelected(false);
                this.currAnimationSpeed = Logic.HIGHEST_ANIM_SPEED;
                if (logic != null) {
                    logic.setAnimSpeed(Logic.HIGHEST_ANIM_SPEED);
                }
                break;
            case Logic.MIDDLE_ANIM_SPEED:
                animSpeed_high.setSelected(false);
                animSpeed_middle.setSelected(true);
                animSpeed_low.setSelected(false);
                animSpeed_none.setSelected(false);
                this.currAnimationSpeed = Logic.MIDDLE_ANIM_SPEED;
                if (logic != null) {
                    logic.setAnimSpeed(Logic.MIDDLE_ANIM_SPEED);
                }
                break;
            case Logic.LOWEST_ANIM_SPEED:
                animSpeed_high.setSelected(false);
                animSpeed_middle.setSelected(false);
                animSpeed_low.setSelected(true);
                animSpeed_none.setSelected(false);
                this.currAnimationSpeed = Logic.LOWEST_ANIM_SPEED;
                if (logic != null) {
                    logic.setAnimSpeed(Logic.LOWEST_ANIM_SPEED);
                }
                break;
            case Logic.NO_ANIM_SPEED:
                animSpeed_high.setSelected(false);
                animSpeed_middle.setSelected(false);
                animSpeed_low.setSelected(false);
                animSpeed_none.setSelected(true);
                this.currAnimationSpeed = Logic.NO_ANIM_SPEED;
                if (logic != null) {
                    logic.setAnimSpeed(Logic.NO_ANIM_SPEED);
                }
        }
    }

    /**
     * takes in an action event and changes the animation speed values based on the user input
     * @param e user input
     */
    public void handleAnimationSpeed(ActionEvent e) {
        if (e.getSource().equals(animSpeed_high)) {
            if (this.currAnimationSpeed != Logic.HIGHEST_ANIM_SPEED) {
                animValueSetter(Logic.HIGHEST_ANIM_SPEED);
            } else {
                animValueSetter(Logic.NO_ANIM_SPEED);
            }
        } else if (e.getSource().equals(animSpeed_middle)) {
            if (this.currAnimationSpeed != Logic.MIDDLE_ANIM_SPEED) {
                animValueSetter(Logic.MIDDLE_ANIM_SPEED);
            } else {
                animValueSetter(Logic.NO_ANIM_SPEED);
            }
        } else if (e.getSource().equals(animSpeed_low)) {
            if (this.currAnimationSpeed != Logic.LOWEST_ANIM_SPEED) {
                animValueSetter(Logic.LOWEST_ANIM_SPEED);
            } else {
                animValueSetter(Logic.NO_ANIM_SPEED);
            }
        } else if (e.getSource().equals(animSpeed_none)) {
                animValueSetter(Logic.NO_ANIM_SPEED);
        }
    }

    /**
     * adds a handler for each pane that handles mouseClicks in editor mode
     */
    public void setEditorEventHandlers() {
        for (int i = 0; i < game_GP.getColumnConstraints().size(); i++) {
            for (int j = 0; j < game_GP.getRowConstraints().size(); j++) {
                final int indexI = i;
                final int indexJ = j;
                Pane p = (Pane) game_GP.getChildren().get(i* game_GP.getRowConstraints().size()+j);

                EventHandler<MouseEvent> onMousePressedHandler = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(mouseEvent.getButton() == MouseButton.PRIMARY) {
                            if (logic.getBoard().getValues()[indexI][indexJ] != Surfaces.START) {
                                logic.getBoard().changeBoardCell(indexI, indexJ,
                                        Surfaces.ROAD, logic.getBoard(), guiCon, logic.getDir());
                            }
                        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            if (logic.getBoard().getValues()[indexI][indexJ] == Surfaces.START) {
                                logic.handleTheStartingLine(indexI, indexJ, false, true);
                            } else {
                                logic.getBoard().changeBoardCell(indexI, indexJ,
                                        Surfaces.GRAVEL, logic.getBoard(), guiCon, logic.getDir());
                            }
                        }  else if (mouseEvent.getButton() == MouseButton.MIDDLE) {
                            if (logic.getBoard().getValues()[indexI][indexJ] == Surfaces.START) {
                                logic.handleTheStartingLine(indexI, indexJ,true, false);
                            } else if (logic.getBoard().getValues()[indexI][indexJ] == Surfaces.ROAD) {
                                logic.handleTheStartingLine(indexI, indexJ, false, false);
                            }
                        }
                    }
                };
                p.setOnMousePressed(onMousePressedHandler);

                EventHandler<MouseEvent> onDragDetectedHandler = (MouseEvent event) -> {
                    mouseButton = event.getButton();
                    Dragboard db = game_GP.getChildren().get(
                            indexI* game_GP.getRowConstraints().size()+indexJ).startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString("has key");
                    db.setContent(content);

                    event.consume();
                };
                p.setOnDragDetected(onDragDetectedHandler);

                EventHandler<DragEvent> onDragOverHandler = (DragEvent event) -> {
                    if (event.getGestureSource() != game_GP.getChildren().get(
                            indexI* game_GP.getRowConstraints().size()+indexJ) &&
                            event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    }
                    if(mouseButton == MouseButton.PRIMARY) {
                        if (logic.getBoard().getValues()[indexI][indexJ] != Surfaces.START) {
                            logic.getBoard().changeBoardCell(indexI, indexJ,
                                    Surfaces.ROAD, logic.getBoard(), guiCon, logic.getDir());
                        }
                    } else if(mouseButton == MouseButton.SECONDARY) {
                        if (logic.getBoard().getValues()[indexI][indexJ] != Surfaces.START) {
                            logic.getBoard().changeBoardCell(indexI, indexJ,
                                    Surfaces.GRAVEL, logic.getBoard(), guiCon, logic.getDir());
                        }
                    }
                    event.consume();
                };
                p.setOnDragOver(onDragOverHandler);
            }
        }
    }

    /**
     * checks if the current map is valid
     */
    public void checkMapValidity() {
        if(gameMode != GameMode.Race_Mode && gameMode != GameMode.Preparation_Mode) {
            this.logic.checkBoardValidity();
       }
    }


    /**
     * loads map from a file the user can choose
     */
    public void loadMap() {
        if (gameMode != GameMode.Race_Mode && gameMode != GameMode.Preparation_Mode) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("java.io.tmpdir")));

            fileChooser.setTitle("Open JSON File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON files", "*.json"),
                    new FileChooser.ExtensionFilter("All files", "*.*")
            );
            //Step 3: Open the Dialog (set window owner, so nothing in the original window
            //can be changed)
            File selectedFile = fileChooser.showOpenDialog(game_GP.getScene().getWindow());
            if (gameMode == GameMode.Editor_Mode) {
                activateEditorMode();
                editor_radioMenuItem.setSelected(false);
            }

            if (!isNull(selectedFile)) {
                String customF = System.getProperty("java.io.tmpdir") + selectedFile.getName();
                if (gameMode == GameMode.Editor_Mode) {
                    activateEditorMode();
                    editor_radioMenuItem.setSelected(false);
                }
                loadGame(customF);
            }
        } else {

            if (stopRaceOrPreparations(true, false)) {
                loadMap();
            }
        }
    }

    /**
     * loads a new map with only gravel blocks
     */
    public void newMap() {
        if(gameMode != GameMode.Race_Mode && gameMode != GameMode.Preparation_Mode) {
            this.guiCon = new JavaFXGUI(game_GP, mode_textfield, startRace_link, this);
            this.logic = new Logic(guiCon);
            logic.getGameFromJson("maps/newMap.json");
            if (gameMode == GameMode.Editor_Mode) {
                activateEditorMode();
                activateEditorMode();
            }
        } else if (stopRaceOrPreparations(true, false)) {
            newMap();
        }
    }

    /**
     * saves the current map in a file the user can name
     */
    public void saveMap() {
        if (!logic.getInAnimation()) {
            if ((gameMode == GameMode.Menu_Mode || gameMode == GameMode.Editor_Mode)
                    || (logic.getGameMode() == GameMode.Race_Mode)) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setContentText("Please enter map name:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    //checks if map name only consists of letters
                    if (result.toString().matches("^[a-zA-z]+$")) {
                        // removes optional[] from the string
                        this.logic.saveGameToJson(result.toString().
                                substring(9, result.toString().length() - 1) + ".json");
                        // user info
                        mapSaved_text.setVisible(true);
                        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), mapSaved_text);
                        fadeTransition.setFromValue(1);
                        fadeTransition.setToValue(0);
                        fadeTransition.play();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Wrong map name");
                        alert.setContentText("Please write a map name that only consists of letters!");
                        alert.showAndWait();
                    }
                }
            } else {
                stopRaceOrPreparations(true, false);
            }
        } else {
            guiCon.outputErrorMessage(ErrorCodes.GameInAnimation, "");
        }
    }

    /**
     * creates buttons to add or delete rows and columns
     */
    public void createEditorButtons() {
        Button b1 = new Button("Top +");
        Button b2 = new Button("Top -");
        Button b3 = new Button("Right +");
        Button b4 = new Button("Right -");
        Button b5 = new Button("Left +");
        Button b6 = new Button("Left -");
        Button b7 = new Button("Bottom +");
        Button b8 = new Button("Bottom -");
        ButtonBar bar = new ButtonBar();
        bar.getButtons().addAll(b1, b2, b3, b4, b5, b6, b7, b8);

        for (int i = 0; i < bar.getButtons().size(); i++) {
            Button temp = (Button) bar.getButtons().get(i);
            final int index = i;
            temp.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    switch (index) {
                        case 0 -> logic.addOrDeleteLine(StartCrossDirection.UP, true);
                        case 1 -> logic.addOrDeleteLine(StartCrossDirection.UP, false);
                        case 2 -> logic.addOrDeleteLine(StartCrossDirection.RIGHT, true);
                        case 3 -> logic.addOrDeleteLine(StartCrossDirection.RIGHT, false);
                        case 4 -> logic.addOrDeleteLine(StartCrossDirection.LEFT, true);
                        case 5 -> logic.addOrDeleteLine(StartCrossDirection.LEFT, false);
                        case 6 -> logic.addOrDeleteLine(StartCrossDirection.DOWN, true);
                        case 7 -> logic.addOrDeleteLine(StartCrossDirection.DOWN, false);
                    }
                    setEditorEventHandlers();
                }
            });
        }
        this.display_hbox.getChildren().addAll(b1, b2, b3, b4, b5, b6, b7, b8);
        for (int i = 0; i < display_hbox.getChildren().size(); i++) {
            Button temp = (Button) display_hbox.getChildren().get(i);
            HBox.setHgrow(temp, Priority.ALWAYS);
            temp.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
    }

    /**
     * loads a new game and the re-initializes the GUI
     * @param s name of the map to be loaded
     */
    private void loadGame(String s) {
        if (logic == null)  {
            guiCon = new JavaFXGUI(game_GP, mode_textfield, startRace_link, this);
            this.logic = new Logic(guiCon);
            this.logic.getGameFromJson(s);
            this.gameMode = logic.getGameMode();
            logic.setAnimSpeed(this.currAnimationSpeed);
            if (gameMode == GameMode.Race_Mode) {
                setGameEventHandlers();
                initializePlayerInfo();
            }
        } else if (ErrorCodes.NoError == this.logic.getGameFromJson(s)) {
            guiCon = new JavaFXGUI(game_GP, mode_textfield, startRace_link, this);
            this.logic = new Logic(guiCon);
            this.logic.getGameFromJson(s);
            this.gameMode = logic.getGameMode();
            logic.setAnimSpeed(this.currAnimationSpeed);
            if (gameMode == GameMode.Race_Mode) {
                setGameEventHandlers();
                initializePlayerInfo();
            }
        } else {
            guiCon = new JavaFXGUI(game_GP, mode_textfield, startRace_link, this);
            this.logic = new Logic(guiCon);
            this.logic.getGameFromJson("maps/SimpleOval.json");
            logic.setAnimSpeed(this.currAnimationSpeed);
        }
    }

    /**
     * reloads all game data without reinitializing the GUI
     */
    private void reloadGameData() {
            logic.reInitializeGame();
            logic.setAnimSpeed(this.currAnimationSpeed);
            logic.resetGame();
            this.gameMode = GameMode.Menu_Mode;
    }

    /**
     * activates the editor mode
     */
    public void activateEditorMode() {
        if (gameMode != GameMode.Race_Mode && gameMode != GameMode.Preparation_Mode) {
            if (gameMode != GameMode.Editor_Mode) {
                this.gameMode = GameMode.Editor_Mode;
                this.mode_textfield.textProperty().set("Editor Mode");

                createEditorButtons();

                setEditorEventHandlers();
            } else {
                this.gameMode = GameMode.Menu_Mode;
                this.mode_textfield.textProperty().set("");
                this.display_hbox.getChildren().clear();
                guiCon.initializeGame(logic.getBoard(), logic.getDir(), false, logic.getPlayers());
            }
        } else if (stopRaceOrPreparations(true, true)) {
            reloadGameData();
            editor_radioMenuItem.setSelected(true);
            activateEditorMode();
        } else {
            editor_radioMenuItem.setSelected(false);
        }
    }

    /**
     * closes popUpWindow if main window is closed
     * @param event close button
     */
    private void handleCloseRequest(WindowEvent event) {
        if (popupStage != null) {
            popupStage.close();
        }
    }

    /**
     * exits the game with error code 0
     */
    public void exitGame() {
        System.exit(0);
    }

    /**
     * shows a popup controller that shows and collects player info
     * @param e user click
     */
    private void handlePopupController(ActionEvent e) {
        Player[] players = logic.getPlayers();
        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Popup.fxml"));
        PopupController popupController;
        Parent layout;
        try {
            layout = loader.load();
            popupController = loader.getController();
            Scene scene = new Scene(layout);
            scene.getStylesheets().add("style.css");
            popupStage = new Stage();
            popupController.setStage(popupStage);
            popupController.initialize(players, this.logic, this, this.guiCon);
            popupStage.initOwner(this.stage);
            popupStage.initModality(Modality.WINDOW_MODAL);
            stage.setOnCloseRequest(this::handleCloseRequest);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * handles alert that shows user who won. user can choose to watch a replay of the game if he wishes
     */
    public void handleVictoryScreen() {
        Player[] winners = logic.returnWinners();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setHeaderText("Game won in " + logic.getMovesUsed() + " moves, by:");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        for (int i = 0; i < winners.length; i++) {
            if (winners[i] != null) {
                Player player = winners[i];
                Label label = new Label(player.getName());
                switch (i) {
                    case 0 -> label.setStyle("-fx-background-color: " + Logic.P1_COLOR + ";");
                    case 1 -> label.setStyle("-fx-background-color: " + Logic.P2_COLOR + ";");
                    case 2 -> label.setStyle("-fx-background-color: " + Logic.P3_COLOR + ";");
                    case 3 -> label.setStyle("-fx-background-color: " + Logic.P4_COLOR + ";");
                }

                gridPane.add(label, 0, i);
            }
        }

        alert.getDialogPane().setContent(gridPane);

        ButtonType btnReplay = new ButtonType("Play Replay");
        ButtonType btnMM = new ButtonType("Go back to Main Menu");
        alert.getButtonTypes().setAll(btnReplay, btnMM);

        Button replayButton = (Button) alert.getDialogPane().lookupButton(btnReplay);
        replayButton.setOnAction(event -> {
            animValueSetter(Logic.MIDDLE_ANIM_SPEED);
            logic.setInReplay(true);
            this.startRace_link.setText("Stop Replay");
            logic.playReplay(0);
            alert.close();
        });

        Button mmButton = (Button) alert.getDialogPane().lookupButton(btnMM);
        mmButton.setOnAction(event -> {
            startRace_link.setText("Start Race");
            mode_textfield.setText("");
            display_hbox.getChildren().clear();
            reloadGameData();
            alert.close();
        });
        alert.show();
    }

    /**
     * changes the background color of a given textField depending on the given int value
     * @param i int value
     * @param tf text field
     */
    private void chooseTextFieldColor(int i, TextField tf) {
        switch (i) {
            case Logic.PLAYER_1 -> tf.setStyle("-fx-background-color: " + Logic.P1_COLOR+ ";");
            case Logic.PLAYER_2 -> tf.setStyle("-fx-background-color: " + Logic.P2_COLOR+ ";");
            case Logic.PLAYER_3 -> tf.setStyle("-fx-background-color: " + Logic.P3_COLOR+ ";");
            case Logic.PLAYER_4 -> tf.setStyle("-fx-background-color: " + Logic.P4_COLOR+ ";");
        }
    }

    /**
     * activates the event handlers needed for the game. shows user fields he can go to and where he crashes.
     */
    private void setGameEventHandlers() {
        for (int i = 0; i < game_GP.getColumnCount(); i++) {
            for (int j = 0; j < game_GP.getRowCount(); j++) {
                final int indexI = i;
                final int indexJ = j;
                Pane p = (Pane) game_GP.getChildren().get(i * game_GP.getRowConstraints().size() + j);

                EventHandler<MouseEvent> onMousePressedHandler = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                            logic.updatePlayerPosition(new Position(indexI, indexJ));
                        }
                    }
                };
                EventHandler<MouseEvent> onMouseEnteredHandler = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (logic.getGameMode() == GameMode.Race_Mode) {
                            if (!logic.getPlayers()[logic.getCurrentPlayer()].isAI()) {
                                logic.showWallCrashPoint(new Position(indexI, indexJ), false);
                            }
                        }
                    }
                };
                EventHandler<MouseEvent> onMouseExitedHandler = new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (logic.getGameMode() == GameMode.Race_Mode) {
                            if (!logic.getPlayers()[logic.getCurrentPlayer()].isAI()) {
                                logic.showWallCrashPoint(new Position(indexI, indexJ), true);
                            }
                        }
                    }
                };
                p.setOnMouseEntered(onMouseEnteredHandler);
                p.setOnMouseExited(onMouseExitedHandler);
                p.setOnMousePressed(onMousePressedHandler);
            }
        }
    }

    /**
     * helping method to output the player information to the screen.
     */
    public void initializePlayerInfo() {
        Player[] players = logic.getPlayers();
        for (int i = 0; i < Logic.MAX_PLAYER_NUMBER; i++) {
            TextField tf = new TextField();
            if (players[i].isActive()) {
                tf.setText(players[i].getName());
                if (players[i].isAI()) {
                    tf.setText(players[i].getName() + " - AI");
                }
                tf.setEditable(false);
                tf.setFocusTraversable(false);
                tf.setMouseTransparent(true);
                chooseTextFieldColor(i, tf);
                this.display_hbox.getChildren().add(tf);
                HBox.setHgrow(tf, Priority.ALWAYS);
                tf.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
        }
    }

    /**
     * starts the race preparations -> collects player info through popup, show it on screen, lets players choose
     * positions
     */
    public void startRacePreparations(ActionEvent e) {
        if (gameMode != GameMode.Race_Mode && gameMode != GameMode.Preparation_Mode) {
            if(logic.checkBoardValidity()) {
                if (gameMode == GameMode.Editor_Mode) {
                    activateEditorMode();
                }
                logic.setGameMode(GameMode.Preparation_Mode);
                this.gameMode = GameMode.Preparation_Mode;
                logic.resetGame();
                editor_radioMenuItem.setSelected(false);
                this.startRace_link.setText("Stop Race Preparation");
                handlePopupController(e);
                if (gameMode == GameMode.Preparation_Mode) {
                    initializePlayerInfo();
                    int firstActive = logic.findFirstActivePlayer();
                    Player temp = logic.getPlayers()[firstActive];
                    logic.setCurrentPlayer(firstActive);
                    if (temp.isAI()) {
                        logic.updatePlayerPosition(new Position(0,0));
                    } else {
                        mode_textfield.setText(temp.getName() + " choose starting position!");
                    }
                    setGameEventHandlers();
                }
            }
        } else {
            if (!logic.getInAnimation() && stopRaceOrPreparations(true, false) ) {
                logic.setAllPlayersInactive();
                reloadGameData();
            } else if (logic.isInReplay()) {
                logic.setInReplay(false);
            }
        }
    }

    /**
     * initializes this controller and the basic game menu
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initialize Play.fxml
        if (!isNull(menu_bar)) {
            VBox.setVgrow(game_GP, Priority.ALWAYS);
            game_GP.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            HBox.setHgrow(menu_bar, Priority.ALWAYS);
            menu_bar.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            HBox.setHgrow(mapSaved_text, Priority.ALWAYS);
            mapSaved_text.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            HBox.setHgrow(startRace_link, Priority.ALWAYS);
            startRace_link.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            animSpeed_middle.setSelected(true);
            currAnimationSpeed = Logic.MIDDLE_ANIM_SPEED;
            loadGame("maps/SimpleOval.json");
        }
    }
}