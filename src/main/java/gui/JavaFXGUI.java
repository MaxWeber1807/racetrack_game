package gui;

import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * this class is responsible for outputting information the logic provides to the gui
 */
public class JavaFXGUI implements GUIConnector {


    private final GridPane gp;

    private final TextField modeTextfield;

    private final Hyperlink startRace_link;

    private final UserInterfaceController controller;


    protected JavaFXGUI(GridPane gp, TextField modeTextfield, Hyperlink hl,
                        UserInterfaceController controller) {
        this.gp = gp;
        this.modeTextfield = modeTextfield;
        this.startRace_link = hl;
        this.controller = controller;
    }


    /**
     * updates the text in the player info text field
     * @param s text to be displayed
     */
    public void updatePlayerInfoTextField(String s) {
        modeTextfield.setText(s);
    }

    /**
     * creates a circle and binds it to a pane
     * @param currActPl decides the color of the circle
     * @return the circle
     */
    private Circle createCircle(int currActPl) {
        Circle circle = new Circle();
        switch (currActPl) {
            case Logic.PLAYER_1 -> circle.setFill(Color.web(Logic.P1_COLOR));
            case Logic.PLAYER_2 -> circle.setFill(Color.web(Logic.P2_COLOR));
            case Logic.PLAYER_3 -> circle.setFill(Color.web(Logic.P3_COLOR));
            case Logic.PLAYER_4 -> circle.setFill(Color.web(Logic.P4_COLOR));
        }
        return circle;
    }

    /**
     * helping method to prepare the gui elements for the race or the choosing of the next player
     * @param logic reference to the logic
     * @param currActPl currently active player
     * @param players player array
     */
    private void prepareRaceGUI(Logic logic, int currActPl, Player[] players) {
        logic.setInAnimation(false);
        controller.getStage().setResizable(true);
        int nextPlayerNumber = logic.getNextActivePlayer(currActPl);
        logic.setCurrentPlayer(nextPlayerNumber);
        if (logic.findFirstActivePlayer() == nextPlayerNumber) {
            logic.setGameMode(GameMode.Race_Mode);
            if (!players[nextPlayerNumber].isAI()) {
                modeTextfield.setText(players[nextPlayerNumber].getName() + " make your move!");
                logic.calculatePossibleFields(players[logic.findFirstActivePlayer()]);
                logic.showPossibleFieldsToGUI(false);
            } else {
                modeTextfield.setText(players[nextPlayerNumber].getName()+"(AI) is choosing...");
                logic.updatePlayerPosition(new Position(0,0));
            }
            startRace_link.setText("Stop Race");
        } else {
            if (players[nextPlayerNumber].isAI()) {
                modeTextfield.setText(players[nextPlayerNumber].getName() + "(AI) is choosing...");
                logic.updatePlayerPosition(new Position(0, 0));
            } else {
                modeTextfield.setText(
                        players[logic.getNextActivePlayer(currActPl)].getName() + " choose starting position!");
            }
        }
    }

    /**
     * prepares the gui elements needed for the next move in the race
     * @param logic instance of logic
     * @param players player array
     * @param crashFound if the player crashed this turn
     * @param currActPl currently active player
     * @param circle player character
     * @param oldPos old position before move
     * @param newPos new position player moved to
     * @param tempPane pane to temporarily save circle to, to prevent overlapping
     * @param targetPane pane that is at new position
     */
    private void prepareNextMoveGUI(Logic logic, Player[] players, boolean crashFound, int currActPl, boolean isAI,
                                    Circle circle, Position oldPos, Position newPos, Pane tempPane, Pane targetPane){
        tempPane.getChildren().remove(0);
        targetPane.getChildren().add(circle);
        circle.setTranslateY(0);
        circle.setTranslateX(0);
        circle.toFront();
        logic.setInAnimation(false);
        controller.getStage().setResizable(true);
        if (logic.getIsGameWon()) {
            if (crashFound) {
                logic.getPlayers()[currActPl].setLastPos(newPos);
                logic.getPlayers()[currActPl].setCurrPos(newPos);
            } else {
                players[currActPl].setLastPos(oldPos);
                players[currActPl].setCurrPos(newPos);
            }
            modeTextfield.setText("Game is Won!");
            controller.handleVictoryScreen();
        } else {
            if (crashFound) {
                logic.prepareNextMove(newPos, newPos, false);
            } else {
                logic.prepareNextMove(oldPos, newPos, false);
                if (!logic.getPlayers()[logic.getNextActivePlayer(currActPl)].isAI()) {
                    modeTextfield.setText(logic.getPlayers()[logic.getNextActivePlayer(currActPl)].getName()
                            + " make your move!");
                } else {
                    modeTextfield.setText(logic.getPlayers()[logic.getNextActivePlayer(currActPl)].getName()
                            + "(AI) is choosing...");
                }
            }
        }
    }


    /**
     * handles the creation of player cars and gui after picking a spot
     * @param logic instance of logic, because next player may only move when AI animation is done
     * @param isAI true if current player is AI, false if not
     * @param newPos chosen position of the choosing player
     * @param currActPl currently active player
     */
    private void handlePreparationGUI(Logic logic, boolean isAI, Position newPos, int currActPl) {
        Pane paneWithCircle =
                (Pane)gp.getChildren().get(newPos.getPosX() * gp.getRowConstraints().size() + newPos.getPosY());
        Circle circle = createCircle(currActPl);
        Player[] players = logic.getPlayers();
        if (isAI) {
            circle.setTranslateX(-1000);
            circle.setTranslateY(-1000);
            logic.setInAnimation(true);

            TranslateTransition tt =
                    new TranslateTransition(Duration.millis(5*logic.getCurrAnimationSpeed() + 1), circle);
            SequentialTransition seqTransition = new SequentialTransition(
                    new PauseTransition(Duration.millis(5*logic.getCurrAnimationSpeed() + 1)),
                    tt
            );

            tt.setToX(0);
            tt.setToY(0);
            seqTransition.setOnFinished(event -> {
                prepareRaceGUI(logic, currActPl, players);
            });
            seqTransition.play();
        } else {
            prepareRaceGUI(logic, currActPl, players);
        }
        paneWithCircle.getChildren().add(circle);
        circle.centerXProperty().bind(paneWithCircle.widthProperty().divide(2));
        circle.centerYProperty().bind(paneWithCircle.heightProperty().divide(2));
        circle.radiusProperty().bind(paneWithCircle.widthProperty().divide(2.5));
    }

    /**
     * handles the movement of player character GUI-wise
     * @param logic instance of logic
     * @param players player array
     * @param isAI true if moving player is AI player false if not
     * @param crashFound if the player crashed this turn
     * @param currActPl currently active player
     * @param oldPos old position before move
     * @param newPos new position player moved to
     */
    private void handleRaceGUI(Logic logic, Player[] players, boolean isAI, Position oldPos,
                               Position newPos, int currActPl, boolean crashFound) {

        int startPanePosition = oldPos.getPosX() * gp.getRowConstraints().size() + oldPos.getPosY();
        int targetPanePosition = newPos.getPosX() * gp.getRowConstraints().size() + newPos.getPosY();
        logic.setInAnimation(true);

        Pane paneWithCircle = (Pane)gp.getChildren().get(startPanePosition);
        Pane targetPane = (Pane) gp.getChildren().get(targetPanePosition);
        Pane tempPane = (Pane) gp.getChildren().get(gp.getChildren().size() - 1);

        Circle circle = (Circle) paneWithCircle.getChildren().get(paneWithCircle.getChildren().size() - 1);
        paneWithCircle.getChildren().remove(paneWithCircle.getChildren().size() - 1);
        tempPane.getChildren().add(circle);

        circle.setTranslateX(-(tempPane.getLayoutX() - paneWithCircle.getLayoutX()));
        circle.setTranslateY(-(tempPane.getLayoutY() - paneWithCircle.getLayoutY()));

        TranslateTransition tt =
                new TranslateTransition(Duration.millis(3*logic.getCurrAnimationSpeed() + 1), circle);
        SequentialTransition seqTransition = new SequentialTransition(
                new PauseTransition(Duration.millis(3*logic.getCurrAnimationSpeed() + 1)),
                tt
        );
        tt.setToX(-(tempPane.getLayoutX() - targetPane.getLayoutX()));
        tt.setToY(-(tempPane.getLayoutY() - targetPane.getLayoutY()));

        seqTransition.setOnFinished(event -> {
            prepareNextMoveGUI(logic, players, crashFound, currActPl, isAI,
                    circle, oldPos, newPos, tempPane, targetPane);
        });

        seqTransition.play();
    }

    /**
     * handles any update in player position deciding between in race and in preparation
     * @param logic instance of logic
     * @param players player array
     * @param isAI true if moving player is AI player false if not
     * @param crashFound if the player crashed this turn
     * @param currActPl currently active player
     * @param oldPos old position before move
     * @param newPos new position player moved to
     */
    public void updatePlayerPosGUI(Logic logic, Player[] players, boolean isAI, Position oldPos,
                                   Position newPos, int currActPl, boolean crashFound) {
        GameMode gameMode = logic.getGameMode();
        controller.getStage().setResizable(false);
        if (gameMode == GameMode.Race_Mode) {
            handleRaceGUI(logic, players, isAI, oldPos, newPos, currActPl, crashFound);
        } else if (gameMode == GameMode.Preparation_Mode) {
            handlePreparationGUI(logic, isAI, newPos, currActPl);
        }
    }

    /**
     * plays out one replay move to the gui, also handles replay interruption
     * @param logic instance of logic
     * @param replayData player replay data to play move from
     * @param moveCount current move the replay is at
     * @param animationSpeed speed of replay animation
     */
    public void playReplayMove(Logic logic, ArrayList<ArrayList<Position>> replayData,
                               int moveCount, int animationSpeed) {
        TranslateTransition[] animations = new TranslateTransition[logic.howManyActivePlayers()];
        controller.getStage().setResizable(false);

        for (int i = 0; i < Logic.MAX_PLAYER_NUMBER; i++) {
            if (logic.getPlayers()[i].isActive()) {
                Circle circle;
                int startPanePosition = (replayData.get(i).get(moveCount)).getPosX()
                        * gp.getRowConstraints().size() + (replayData.get(i).get(moveCount)).getPosY();
                int targetPanePosition = (replayData.get(i).get(moveCount + 1)).getPosX()
                        * gp.getRowConstraints().size() + (replayData.get(i).get(moveCount + 1)).getPosY();
                Pane paneWithCircle = (Pane) gp.getChildren().get(startPanePosition);
                Pane targetPane = (Pane) gp.getChildren().get(targetPanePosition);
                Pane tempPane = (Pane) gp.getChildren().get(gp.getChildren().size() - 1);


                if (moveCount != 0) {
                    circle = (Circle) paneWithCircle.getChildren().get(paneWithCircle.getChildren().size() - 1);
                    paneWithCircle.getChildren().remove(paneWithCircle.getChildren().size() - 1);
                } else {
                    Pane oldPos = (Pane)gp.getChildren().get(logic.getPlayers()[i].getCurrentPos().getPosX()
                            * gp.getRowConstraints().size() + logic.getPlayers()[i].getCurrentPos().getPosY());
                    circle = (Circle)oldPos.getChildren().get(oldPos.getChildren().size() - 1);
                    oldPos.getChildren().remove(oldPos.getChildren().size() - 1);
                }
                circle.setVisible(true);
                circle.centerXProperty().bind(tempPane.widthProperty().divide(2));
                circle.centerYProperty().bind(tempPane.heightProperty().divide(2));
                circle.radiusProperty().bind(tempPane.widthProperty().divide(2.5));
                circle.setTranslateX(-(tempPane.getLayoutX() - paneWithCircle.getLayoutX()));
                circle.setTranslateY(-(tempPane.getLayoutY() - paneWithCircle.getLayoutY()));

                tempPane.getChildren().add(circle);

                TranslateTransition temp =
                        new TranslateTransition(Duration.millis(3*logic.getCurrAnimationSpeed() + 1), circle);
                temp.setToX(-(tempPane.getLayoutX() - targetPane.getLayoutX()));
                temp.setToY(-(tempPane.getLayoutY() - targetPane.getLayoutY()));
                animations[i] = temp;
            }
        }

        ParallelTransition parallelTransition = new ParallelTransition(
                new PauseTransition(Duration.millis(3*logic.getCurrAnimationSpeed() + 1)));
        parallelTransition.getChildren().addAll(animations);

        parallelTransition.setOnFinished(event -> {
            controller.getStage().setResizable(true);
            if (moveCount < replayData.get(logic.findFirstActivePlayer()).size() - 1) {
                int setToMove = moveCount + 1;
                if (!logic.isInReplay()) {
                    setToMove = replayData.get(logic.findFirstActivePlayer()).size() - 1;
                }
                for (int i = 0; i < Logic.MAX_PLAYER_NUMBER; i++) {
                    if (logic.getPlayers()[i].isActive()) {
                        int targetPanePosition = (replayData.get(i).get(setToMove)).getPosX()
                                * gp.getRowConstraints().size()
                                + (replayData.get(i).get(setToMove)).getPosY();
                        Pane tempPane = ((Pane)gp.getChildren().get(gp.getChildren().size() - 1));
                        Circle circle = (Circle)tempPane.getChildren().get(tempPane.getChildren().size() - 1);
                        tempPane.getChildren().remove(tempPane.getChildren().size() - 1);
                        switch (i) {
                            case Logic.PLAYER_1 -> circle.setStyle("-fx-fill: " + Logic.P1_COLOR+ ";");
                            case Logic.PLAYER_2 -> circle.setStyle("-fx-fill: " + Logic.P2_COLOR+ ";");
                            case Logic.PLAYER_3 -> circle.setStyle("-fx-fill: " + Logic.P3_COLOR+ ";");
                            case Logic.PLAYER_4 -> circle.setStyle("-fx-fill: " + Logic.P4_COLOR+ ";");
                        }
                        ((Pane)gp.getChildren().get(targetPanePosition)).getChildren().add(circle);
                        circle.setVisible(false);
                    }
                }
                if (!logic.isInReplay()) {
                    controller.handleVictoryScreen();
                    logic.setInAnimation(false);
                } else if (moveCount == replayData.get(logic.findFirstActivePlayer()).size() - 2 || !logic.isInReplay()) {
                    controller.handleVictoryScreen();
                } else {
                    logic.playReplay(moveCount + 1);
                }
            }
        });

        parallelTransition.play();
    }

    /**
     * creates the start image and adds it to a pane
     * @param dir direction the starting line should be crossed
     * @param lb pane
     * @param green true if image needs green background, false if not
     */
    private void setStartImage(StartCrossDirection dir, Pane lb, boolean green) {
        try {
            String s = "";
            /* couldn't get the JavaFX rotation-bug fixed in time*/
            if (green) {
                switch (dir) {
                    case LEFT     ->  s = "/img/finishLeft_green.png";
                    case RIGHT    ->  s = "/img/finishRight_green.png";
                    case UP, DOWN ->  s = "/img/finishUp_green.png";
                }
            } else {
                switch (dir) {
                    case LEFT     ->  s = "/img/finishLeft.png";
                    case RIGHT    ->  s = "/img/finishRight.png";
                    case UP, DOWN ->  s = "/img/finishUp.png";
                }
            }
            InputStream inputStream = getClass().getResourceAsStream(s);
            if (inputStream != null) {
                Image img = new Image(inputStream);
                ImageView imgV = new ImageView(img);
                lb.getChildren().add(0, imgV);

                if (dir == StartCrossDirection.DOWN) {
                    imgV.setRotate(180);
                }
                imgV.fitWidthProperty().bind(lb.widthProperty());
                imgV.fitHeightProperty().bind(lb.heightProperty());
            } else {
                throw new FileNotFoundException("Image not found: " + s);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * changes the style of a pane
     * @param b board
     * @param dir direction the starting line should be crossed
     */
    public void initializePaneStyles(Board b, StartCrossDirection dir) {
        Surfaces[][] values = b.getValues();
        for (int i = 0; i < b.getLength(); i++) {
            for (int j = 0; j < b.getLength(0); j++) {
                Pane lb = new Pane();
                this.gp.add(lb, i, j);
                switch (values[i][j]) {
                    case GRAVEL -> lb.setStyle("-fx-background-color : grey");
                    case ROAD -> lb.setStyle("-fx-background-color : white");
                    case START -> setStartImage(dir, lb, false);
                }
            }
        }
    }

    /**
     * updates one of the possible fields that the player can choose
     * @param pos position of field to be updated
     * @param sur surface at the given field
     * @param dir direction of starting line
     */
    public void showPossibleField(Position pos, Surfaces sur, StartCrossDirection dir) {
        int posX = pos.getPosX();
        int posY = pos.getPosY();

        if (sur == Surfaces.GRAVEL) {
            gp.getChildren().get(posX * gp.getRowConstraints().size() + posY).setStyle("-fx-background-color : "
                    + Logic.GreenGravel_Color);
        } else if (sur == Surfaces.ROAD) {
            gp.getChildren().get(posX * gp.getRowConstraints().size() + posY).setStyle("-fx-background-color : "
                    + Logic.GreenRoad_Color);
        } else if (sur == Surfaces.START) {
            ((Pane) gp.getChildren().get(posX * gp.getRowConstraints().size() + posY)).getChildren().remove(0);
            setStartImage(dir, (Pane)gp.getChildren().
                    get(posX * gp.getRowConstraints().size() + posY), true);
        }

    }

    /**
     * outputs diverse error messages
     * @param errorCode which error message should be put
     */
    public void outputErrorMessage(ErrorCodes errorCode, String currActPl) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        switch (errorCode) {
            case NoError -> {
                Alert allGoodInfo = new Alert(Alert.AlertType.INFORMATION);
                allGoodInfo.setTitle("Information");
                allGoodInfo.setContentText("Map is valid!");
                allGoodInfo.showAndWait();
            }
            case StartMissing -> {
                alert.setHeaderText("No Starting Line Error");
                alert.setContentText("Please add a starting line to the map!");
            }
            case StartNotComplete -> {
                alert.setHeaderText("Incomplete Starting Line Error");
                alert.setContentText("The starting line can't have road at the sides!");
            }
            case NoRoundCourse -> {
                alert.setHeaderText("Round Coarse Error");
                alert.setContentText("The Map must have a complete round course!");
            }
            case ChosenPositionInvalid -> {
                alert.setHeaderText("Placement Error");
                alert.setContentText("Please choose a valid Position!");
            }
            case NoActivePlayers -> {
                alert.setHeaderText("Active Player Error");
                alert.setContentText("Game needs at least 1 active player!");
            }
            case NoAvailableFields -> {
                alert.setHeaderText("Player " + currActPl + " has no available fields to choose from");
                alert.setContentText("The move was done without player input!");
            }
            case GameInAnimation -> {
                alert.setHeaderText("Game is in Animation");
                alert.setContentText("Please wait till the game is done animating!");
            }
            case WrongMapSyntax -> {
                alert.setHeaderText("Error with Map Syntax");
                alert.setContentText("The map you provides has a faulty syntax. Game is reset!");
            }
            case WrongBoardSize -> {
                alert.setHeaderText("Error with board size");
                alert.setContentText("Make sure the board is not smaller in length and width than 10" +
                        " and not longer than 40 in width and 20 in length. Game is reset!");
            }
            case WrongBoardContents -> {
                alert.setHeaderText("Error with board content");
                alert.setContentText("There is a number in the board which is not 0, 1 or 2. Game is reset!");
            }
            case WrongCurrentPlayerOrDirection -> {
                alert.setHeaderText("Error with current player or direction");
                alert.setContentText("Current player needs to be between 0 and "+ (Logic.MAX_PLAYER_NUMBER - 1) +
                        " direction needs to be between 0 and 3. Game is reset!");
            }
            case InvalidPLayerData -> {
                alert.setHeaderText("Error with player data");
                alert.setContentText("Player array has invalid data!. Game is reset!");
            }
        }
        if (errorCode == ErrorCodes.NoAvailableFields) {
            alert.setOnHidden(evt -> alert.close());
            alert.show();
        } else if (errorCode != ErrorCodes.NoError) {
            alert.showAndWait();
        }

    }

    /**
     * Gives the GUI all information to set up a game
     *
     * @param b   board
     * @param dir direction the starting line should be crossed
     */
    @Override
    public void initializeGame(Board b, StartCrossDirection dir, boolean inRace, Player[] players) {

        // Resets the gridPanes values
        this.gp.getChildren().removeAll(gp.getChildren());
        this.gp.setHgap(2);
        this.gp.setVgap(2);
        this.gp.getColumnConstraints().removeAll(this.gp.getColumnConstraints());
        this.gp.getRowConstraints().removeAll(this.gp.getRowConstraints());

        // Fills the gridPane
        for (int i = 0; i < b.getLength(); i++) {
            ColumnConstraints column = new ColumnConstraints(10);
            this.gp.getColumnConstraints().add(column);
            column.setPercentWidth(10);
        }
        for (int i = 0; i < b.getLength(0); i++) {
            RowConstraints row = new RowConstraints(10);
            this.gp.getRowConstraints().add(row);
            row.setPercentHeight(10);
        }
        initializePaneStyles(b, dir);
        if (inRace) {
            for (int i = 0; i < players.length; i++) {
                if (players[i].isActive()) {
                    Circle circle = createCircle(i);
                    Pane paneWithCircle = (Pane)gp.getChildren().get(players[i].getCurrentPos().getPosX()
                            * gp.getRowConstraints().size() + players[i].getCurrentPos().getPosY());
                    paneWithCircle.getChildren().add(circle);
                    circle.centerXProperty().bind(paneWithCircle.widthProperty().divide(2));
                    circle.centerYProperty().bind(paneWithCircle.heightProperty().divide(2));
                    circle.radiusProperty().bind(paneWithCircle.widthProperty().divide(2.5));
                }
            }
            startRace_link.setText("Stop Race");
        }
    }

    /**
     * changes the graphic of a gridPane cell
     * @param i x coordinate
     * @param j y coordinate
     * @param sur surface to be changed to
     * @param dir direction the starting line should be crossed
     * @param isStart true if the surface currently on the pane is the start surface
     */
    public void changeGridPaneCell(int i, int j, Surfaces sur, StartCrossDirection dir, boolean isStart) {
        if(isStart) {
            ((Pane) gp.getChildren().get(i * gp.getRowConstraints().size() + j)).getChildren().remove(0);
        }
        switch (sur) {
            case ROAD -> gp.getChildren().get(i * gp.getRowConstraints().size() + j).
                    setStyle("-fx-background-color : white");
            case GRAVEL -> gp.getChildren().get(i * gp.getRowConstraints().size() + j).
                    setStyle("-fx-background-color : grey");
            case START -> setStartImage(dir, (Pane) gp.getChildren().
                    get(i * gp.getRowConstraints().size() + j), false);
            case RED_GRAVEL -> gp.getChildren().get(i * gp.getRowConstraints().size() + j).
                    setStyle("-fx-background-color : red");
        }
    }
}
