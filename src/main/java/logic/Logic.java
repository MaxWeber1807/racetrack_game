package logic;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * all calculation needed for the game is done here
 */
public class Logic {

    private Board board;

    private Player[] players;

    private final GUIConnector guiCon;

    protected Position[][] possiblePositions;

    private int[][] distanceValues;

    private int movesUsed;

    //needs to be protected to test start cross
    protected ArrayList<ArrayList<Position>> replayData;

    private StartCrossDirection direction;

    private GameMode gameMode;

    private int currAnimationSpeed;


    private boolean inAnimation;
    private boolean isGameWon;
    private int currentPlayer;
    private boolean inReplay;


    public static final byte MAX_PLAYER_NUMBER = 4;
    public static final byte PLAYER_1 = 0;
    public static final byte PLAYER_2 = 1;
    public static final byte PLAYER_3 = 2;
    public static final byte PLAYER_4 = 3;
    public static final byte TOP = 0;
    public static final byte RIGHT = 1;
    public static final byte DOWN = 2;
    public static final byte LEFT = 3;
    public static final String P1_COLOR = "yellow";
    public static final String P2_COLOR = "#35baf6";
    public static final String P3_COLOR = "red";
    public static final String P4_COLOR = "green";
    public static final String GreenRoad_Color = "#B4EF94";
    public static final String GreenGravel_Color = "#BDCEB4";


    //time in milliseconds it takes to finish animation
    public static final int HIGHEST_ANIM_SPEED = 50;
    public static final int MIDDLE_ANIM_SPEED = 100;
    public static final int LOWEST_ANIM_SPEED = 200;
    public static final int NO_ANIM_SPEED = 0;


    /**
     * initializer for testing purposes
     *
     * @param guiCon instance of the gui connector
     * @param board  board
     * @param x      x value
     * @param y      y value
     */
    public Logic(GUIConnector guiCon, String board, int x, int y,
                 StartCrossDirection direction, Player[] players, boolean inRace) {
        this.guiCon = guiCon;
        this.board = new Board(board, x, y);
        this.direction = direction;
        this.players = players;
        if (inRace) gameMode = GameMode.Race_Mode;
        else gameMode = GameMode.Preparation_Mode;
        initializeReplayData();
        guiCon.initializeGame(this.board, this.direction, inRace, players);
    }

    /**
     * Initializes logic.
     * Game always starts with default map.
     *
     * @param guiCon instance of the gui connector
     */
    public Logic(GUIConnector guiCon) {
        this.guiCon = guiCon;
        this.board = new Board();
        initializeReplayData();
    }

    //Getters and Setters
    public boolean isInReplay() {
        return inReplay;
    }

    public void setInReplay(boolean inReplay) {
        this.inReplay = inReplay;
    }

    /**
     * setter for in animation
     *
     * @param b true if animation is happening, false if not
     */
    public void setInAnimation(boolean b) {
        this.inAnimation = b;
    }

    /**
     * getter for in animation
     */
    public boolean getInAnimation() {
        return this.inAnimation;
    }

    /**
     * setter for current player
     *
     * @param i sets current player to i
     */
    public void setCurrentPlayer(int i) {
        this.currentPlayer = i;
    }

    /**
     * setter for animation speed
     *
     * @param animSpeed animation speed
     */
    public void setAnimSpeed(int animSpeed) {
        this.currAnimationSpeed = animSpeed;
    }

    /**
     * setter for gameMode
     *
     * @param gameMode gameMode
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * getter for gameMode
     *
     * @return gameMode
     */
    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean getIsGameWon() {
        return this.isGameWon;
    }

    public int getMovesUsed() {
        return movesUsed;
    }

    /**
     * getter for animation speed
     *
     * @return animation speed
     */
    public int getCurrAnimationSpeed() {
        return this.currAnimationSpeed;
    }

    /**
     * getter for board
     *
     * @return the board
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * returns current start cross directions
     *
     * @return current start cross directions
     */
    public StartCrossDirection getDir() {
        return this.direction;
    }

    /**
     * returns players
     *
     * @return player array or null if players is null
     */
    public Player[] getPlayers() {
        Player[] temp = new Player[players.length];
        for (int i = 0; i < players.length; i++) {
            temp[i] = new Player(players[i].isActive(), players[i].isAI(), players[i].getName(),
                    players[i].getLastPos(), players[i].getCurrentPos(), players[i].getLap());
        }
        return temp;
    }

    /**
     * sets all players to inactive
     */
    public void setAllPlayersInactive() {
        for (Player player : players) {
            player.setActive(false);
        }
    }

    /**
     * setter for players
     *
     * @param p value to set to
     */
    public void setPlayers(Player[] p) {
        this.players = p;
    }

    /**
     * returns current player
     *
     * @return current player
     */
    public int getCurrentPlayer() {
        return currentPlayer;

    }


    //HELPERS

    /**
     * resets all the games values, usually called in preparation
     */
    public void resetGame() {
        this.currentPlayer = 0;
        for (int i = 0; i < MAX_PLAYER_NUMBER; i++) {
            players[i].setLastPos(new Position(0, 0));
            players[i].setCurrPos(new Position(0, 0));
            players[i].setLap(0);
        }
        isGameWon = false;
        inReplay = false;
        initializeReplayData();
        movesUsed = 0;
    }

    /**
     * calculates how many active players are in the race
     *
     * @return number of active players
     */
    public byte howManyActivePlayers() {
        byte active = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i].isActive()) {
                active++;
            }
        }
        return active;
    }

    /**
     * initializes replay array list
     */
    private void initializeReplayData() {
        this.replayData = new ArrayList<>();
        for (int i = 0; i < MAX_PLAYER_NUMBER; i++) {
            replayData.add(new ArrayList<Position>());
        }
    }

    /**
     * searches for the first player that is active
     *
     * @return first player found, -1 if there is none
     */
    public byte findFirstActivePlayer() {
        byte i = 0;
        while (i < MAX_PLAYER_NUMBER) {
            if (players[i].isActive()) {
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }

    /**
     * searches for the last player that is active
     *
     * @return last player found, -1 if there is none
     */
    private byte findLastActivePlayer() {
        byte i = MAX_PLAYER_NUMBER - 1;
        while (i >= 0) {
            if (players[i].isActive()) {
                return i;
            } else {
                i--;
            }
        }
        return -1;
    }

    /**
     * compares a position to the possible move options and states if it is among them
     *
     * @param pos position to be compared
     * @return true if in options, false if not
     */
    private boolean isInOptions(Position pos) {
        for (int i = 0; i < possiblePositions.length; i++) {
            for (int j = 0; j < possiblePositions[0].length; j++) {
                if (possiblePositions[i][j].isEqualTo(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * tells the player which fields are usable using green highlighting
     *
     * @param oldColor returns the fields to their old color
     */
    public void showPossibleFieldsToGUI(boolean oldColor) {
        for (int i = 0; i < possiblePositions.length; i++) {
            for (int j = 0; j < possiblePositions[0].length; j++) {
                if (board.isValidPositionInGameMode(players, possiblePositions[i][j], this.currentPlayer, gameMode)) {
                    if (!oldColor) {
                        guiCon.showPossibleField(possiblePositions[i][j],
                                board.getSurfaceAt(possiblePositions[i][j]), direction);
                    } else {
                        guiCon.changeGridPaneCell(possiblePositions[i][j].getPosX(),
                                possiblePositions[i][j].getPosY(), board.getSurfaceAt(possiblePositions[i][j]),
                                direction, board.getSurfaceAt(possiblePositions[i][j]) == Surfaces.START);
                    }
                }
            }
        }
    }

    /**
     * calculates which fields are available for the player
     *
     * @param p player
     */
    public void calculatePossibleFields(Player p) {
        int newX = (p.getCurrentPos().getPosX() - p.getLastPos().getPosX()) + p.getCurrentPos().getPosX();
        int newY = (p.getCurrentPos().getPosY() - p.getLastPos().getPosY()) + p.getCurrentPos().getPosY();
        Position newMid = new Position(newX, newY);
        this.possiblePositions = new Position[][]{
                {
                        new Position(newX - 1, newY - 1), new Position(newX, newY - 1), new Position(newX + 1, newY - 1)
                },
                new Position[]{
                        new Position(newX - 1, newY), newMid, new Position(newX + 1, newY)
                },
                {
                        new Position(newX - 1, newY + 1), new Position(newX, newY + 1), new Position(newX + 1, newY + 1)
                }
        };
    }

    /**
     * colors the point where the player first leaves the road red if there is any
     *
     * @param target end of route
     * @param exit   true if the user is exiting the field with his mouse, false if entering it
     */
    public void showWallCrashPoint(Position target, boolean exit) {
        if (isInOptions(target) && !inAnimation) {
            Position[] route = calcRoute(players[currentPlayer].getCurrentPos(), target);
            for (int i = 0; i < route.length; i++) {
                if (board.getSurfaceAt(route[i]) == Surfaces.GRAVEL) {
                    int posX = route[i].getPosX();
                    int posY = route[i].getPosY();
                    if (!exit) {
                        guiCon.changeGridPaneCell(posX, posY, Surfaces.RED_GRAVEL, direction, false);
                    } else {
                        guiCon.showPossibleField(route[i], Surfaces.GRAVEL, direction);
                        if (!isInOptions(new Position(posX, posY))) {
                            switch (board.getSurfaceAt(new Position(posX, posY))) {
                                case GRAVEL -> guiCon.changeGridPaneCell(posX, posY, Surfaces.GRAVEL,
                                        direction, false);
                                case ROAD -> guiCon.changeGridPaneCell(posX, posY, Surfaces.ROAD,
                                        direction, false);
                                case START -> guiCon.changeGridPaneCell(posX, posY, Surfaces.START,
                                        direction, true);
                            }
                        }
                    }
                    i = route.length;
                }
            }
        }
    }

    /**
     * returns the number of the next active player
     *
     * @param currPl number from which next player is asked for
     * @return number of next player or -1 if there is none
     */
    public int getNextActivePlayer(int currPl) {
        if (findLastActivePlayer() == currPl) {
            return findFirstActivePlayer();
        } else {
            for (int i = (currPl + 1); i < MAX_PLAYER_NUMBER; i++) {
                if (players[i].isActive()) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * calculates the route the player takes via monochrome screening algorithm
     *
     * @param start  start position
     * @param target end position
     * @return Position array with all fields the player visits on the way
     */
    protected Position[] calcRoute(Position start, Position target) {
        ArrayList<Position> route = new ArrayList<>();

        int dx = target.getPosX() - start.getPosX();
        int dy = target.getPosY() - start.getPosY();
        int dxAbs = Math.abs(dx);
        int dyAbs = Math.abs(dy);
        int signX;
        int signY;

        if (dx > 0) {
            signX = 1;
        } else {
            signX = -1;
        }
        if (dy > 0) {
            signY = 1;
        } else {
            signY = -1;
        }

        double m = (double) dy / dx;

        if (dxAbs >= dyAbs) {
            for (int x = start.getPosX(); x != target.getPosX() + signX; x = x + signX) {
                int y = start.getPosY() + (int) Math.round(m * (x - start.getPosX()));
                route.add(new Position(x, y));
            }
        } else {
            for (int y = start.getPosY(); y != target.getPosY() + signY; y = y + signY) {
                int x = start.getPosX() + (int) Math.round((1 / m) * (y - start.getPosY()));
                route.add(new Position(x, y));
            }
        }

        Position[] arrayRoute = new Position[route.size()];
        for (int i = 0; i < route.size(); i++) {
            arrayRoute[i] = route.get(i);
        }

        return arrayRoute;
    }

    /**
     * calculates if the route is valid or there is a crash or another player in the route
     *
     * @param route  route to be checked
     * @param newPos last position on route
     * @param isAI   if route is calculated for AI
     * @return an instance of RouteResult containing if there is a crash(for GUI) and the final point on the route
     */
    protected RouteResult isRouteValid(Position[] route, Position newPos, boolean isAI) {
        Position lastPointOnRoute = newPos;
        boolean crashFound = false;
        for (int i = 0; i < route.length; i++) {
            if (board.getSurfaceAt(route[i]) == Surfaces.GRAVEL
                    || (isAI && !board.isValidPositionInGameMode(players, route[i], currentPlayer, gameMode))) {
                while (!board.isValidPositionInGameMode(players, route[i - 1], currentPlayer, gameMode)) {
                    i--;
                }
                lastPointOnRoute = route[i - 1];
                i = route.length;
                crashFound = true;
            } else if ((route[i].getPosX() == 0 || route[i].getPosY() == 0) && route.length > 2) {
                while (!board.isValidPositionInGameMode(players, route[i - 1], currentPlayer, gameMode)) {
                    i--;
                }
                lastPointOnRoute = route[i];
                i = route.length;
                crashFound = true;
            }
        }
        return new RouteResult(crashFound, lastPointOnRoute);
    }


    //PLAYER

    /**
     * handles the picking of starting positions
     *
     * @param newPos chosen position of the player
     */
    private void handlePlayerPreparation(Position newPos) {
        ErrorCodes errorCode;
        if (players[currentPlayer].isActive()) {
            // checks if current player is bot
            if (players[currentPlayer].isAI()) {
                if (findFirstActivePlayer() == currentPlayer) {
                    guiCon.updatePlayerInfoTextField(players[currentPlayer].getName() + "(AI) is choosing...");
                }
                updateAIPosition();
                //checks if chosen position is valid
            } else if (board.getValues()[newPos.getPosX()][newPos.getPosY()] != Surfaces.ROAD
                    || !board.isValidPositionInGameMode(players, newPos, currentPlayer, gameMode)) {
                errorCode = ErrorCodes.ChosenPositionInvalid;
                guiCon.outputErrorMessage(errorCode, "");
            } else {
                players[currentPlayer].setCurrPos(newPos);
                players[currentPlayer].setLastPos(newPos);
                guiCon.updatePlayerPosGUI(this, players, false, new Position(0, 0),
                        newPos, currentPlayer, false);
            }
        }
    }

    /**
     * handles movement of players race
     *
     * @param newPos   chosen position
     * @param noFields if all possible fields are outside the screen
     */
    private void handlePlayerInRace(Position newPos, boolean noFields) {
        if (!players[currentPlayer].isAI() && (isInOptions(newPos)
                && board.isValidPositionInGameMode(players, newPos, currentPlayer, gameMode)) || noFields) {
            Position[] route = calcRoute(players[currentPlayer].getCurrentPos(), newPos);
            RouteResult rr = isRouteValid(route, newPos, false);
            replayData.get(currentPlayer).add(rr.getLastPointOnRoute());
            handleStartCross(currentPlayer, rr.getLastPointOnRoute());
            if (currentPlayer == findLastActivePlayer()) {
                movesUsed++;
                checkWon();
            }
            guiCon.updatePlayerPosGUI(this, players, false, players[currentPlayer].getCurrentPos(),
                    rr.getLastPointOnRoute(), currentPlayer, rr.isCrashFound());
            if (!noFields) {
                showPossibleFieldsToGUI(true);
            }
        } else if (players[currentPlayer].isAI()) {
            updateAIPosition();
        }
    }

    /**
     * sets all logic values to be set after a move is done
     *
     * @param oldPos last position player had
     * @param newPos new position player has chosen
     */
    public void prepareNextMove(Position oldPos, Position newPos, boolean startOfGame) {
        if (!isGameWon) {
            if (!startOfGame) {
                players[currentPlayer].setLastPos(oldPos);
                players[currentPlayer].setCurrPos(newPos);
                currentPlayer = getNextActivePlayer(currentPlayer);
            }
            if (!players[currentPlayer].isAI()) {
                calculatePossibleFields(players[currentPlayer]);
                boolean allFieldsOutside = true;
                for (int i = 0; i < possiblePositions.length; i++) {
                    for (int j = 0; j < possiblePositions[0].length; j++) {
                        if (board.isValidPositionInGameMode(players, possiblePositions[i][j], currentPlayer, gameMode)) {
                            allFieldsOutside = false;
                        }
                    }
                }
                if (allFieldsOutside) {
                    guiCon.outputErrorMessage(ErrorCodes.NoAvailableFields, players[currentPlayer].getName());
                    handlePlayerInRace(possiblePositions[1][1], true);
                } else {
                    showPossibleFieldsToGUI(false);
                }
            } else {
                updateAIPosition();
            }
        } else {
            players[currentPlayer].setLastPos(oldPos);
            players[currentPlayer].setCurrPos(newPos);
        }
    }

    /**
     * handles any update to player position depending on if race is in preparation or started
     *
     * @param newPos new position to move player to
     */
    public void updatePlayerPosition(Position newPos) {
        if (!inAnimation) {
            if (gameMode == GameMode.Race_Mode) {
                handlePlayerInRace(newPos, false);
            } else if (gameMode == GameMode.Preparation_Mode) {
                handlePlayerPreparation(newPos);
            }
        }
    }

    //AI

    /**
     * updates the AI depending on if race is in preparation or started
     */
    private void updateAIPosition() {
        if (gameMode == GameMode.Preparation_Mode) {
            Position newAIPos = board.findFreePositionFromSL(players, direction, currentPlayer, gameMode);
            players[currentPlayer].setCurrPos(newAIPos);
            players[currentPlayer].setLastPos(newAIPos);
            guiCon.updatePlayerPosGUI(this, players, true,
                    new Position(0, 0), newAIPos, currentPlayer, false);
        } else if (gameMode == GameMode.Race_Mode) {
            RouteResult rr = calculateNextAIMove(players[currentPlayer].getCurrentPos());
            replayData.get(currentPlayer).add(rr.getLastPointOnRoute());
            handleStartCross(currentPlayer, rr.getLastPointOnRoute());
            if (currentPlayer == findLastActivePlayer()) {
                movesUsed++;
                checkWon();
            }
            guiCon.updatePlayerPosGUI(this, players, true, players[currentPlayer].getCurrentPos(),
                    rr.getLastPointOnRoute(), currentPlayer, rr.isCrashFound());
        }
    }

    /**
     * calculates the next move for the AI
     *
     * @param start current position of the AI
     * @return RouteResult containing the final field of the calculated route and if there was a crash
     */
    private RouteResult calculateNextAIMove(Position start) {
        boolean lookForHighest = false;
        Position bestPossible = new Position(start.getPosX(), start.getPosY());

        if (replayData.get(currentPlayer).size() <= 1) {
            instantiateMapDistanceValues();
        }
        calculatePossibleFields(players[currentPlayer]);

        for (int i = 0; i < possiblePositions.length; i++) {
            for (int j = 0; j < possiblePositions[0].length; j++) {
                final int posX = possiblePositions[i][j].getPosX();
                final int posY = possiblePositions[i][j].getPosY();
                Position[] route = calcRoute(start, possiblePositions[i][j]);
                for (int k = 0; k < route.length; k++) {
                    //start crossed
                    if (!board.isOutOfBounds(route[k])
                            && board.getValues()[route[k].getPosX()][route[k].getPosY()] == Surfaces.START
                            && k < route.length - 1) {
                        lookForHighest = true;
                        bestPossible = start;
                    }
                    if (!board.isOutOfBounds(possiblePositions[i][j])
                            && board.getSurfaceAt(possiblePositions[i][j]) != Surfaces.GRAVEL) {
                        if ((lookForHighest && distanceValues[posX][posY] >
                                distanceValues[bestPossible.getPosX()][bestPossible.getPosY()])
                                || (!lookForHighest && distanceValues[posX][posY] <
                                distanceValues[bestPossible.getPosX()][bestPossible.getPosY()])) {
                            bestPossible = new Position(posX, posY);
                        }
                    }
                }
            }
        }
        Position[] routeToBestPossible = calcRoute(start, bestPossible);
        return isRouteValid(routeToBestPossible, routeToBestPossible[routeToBestPossible.length - 1],
                true);
    }

    /**
     * gives each field of the map a value how long it takes from it to the starting line
     */
    public void instantiateMapDistanceValues() {
        distanceValues = new int[board.getLength()][board.getLength(0)];
        int stepsFromStart = 0;
        boolean[][] visited = new boolean[board.getLength()][board.getLength(0)];
        ArrayList<Position> temp = new ArrayList<Position>();
        ArrayList<Position> positionsToCheck = board.getCurrStartingLine();
        for (int i = 0; i < positionsToCheck.size(); i++) {
            final int posX = positionsToCheck.get(i).getPosX();
            final int posY = positionsToCheck.get(i).getPosY();
            visited[posX][posY] = true;
            distanceValues[posX][posY] = stepsFromStart;
        }
        stepsFromStart++;

        while (!positionsToCheck.isEmpty()) {
            for (int i = 0; i < positionsToCheck.size(); i++) {
                Position[] neighbours = board.getNeighbours(positionsToCheck.get(i));
                for (int j = 0; j < neighbours.length; j++) {
                    final int neighX = neighbours[j].getPosX();
                    final int neighY = neighbours[j].getPosY();

                    //traverses the board only in one direction
                    if (!(stepsFromStart == 1 && j == TOP && board.getValues()[neighX][neighY] == Surfaces.ROAD
                            && direction == StartCrossDirection.UP) &&
                            !(stepsFromStart == 1 && j == LEFT && board.getValues()[neighX][neighY] == Surfaces.ROAD
                                    && direction == StartCrossDirection.LEFT) &&
                            !(stepsFromStart == 1 && j == DOWN && board.getValues()[neighX][neighY] == Surfaces.ROAD
                                    && direction == StartCrossDirection.DOWN)
                            && !(stepsFromStart == 1 && j == RIGHT && board.getValues()[neighX][neighY] == Surfaces.ROAD
                            && direction == StartCrossDirection.RIGHT)) {
                        if (!board.isOutOfBounds(neighbours[j]) && !visited[neighX][neighY]
                                && board.getValues()[neighX][neighY] == Surfaces.ROAD) {
                            visited[neighX][neighY] = true;
                            distanceValues[neighX][neighY] = stepsFromStart;
                            temp.add(neighbours[j]);
                        }
                    }
                }
            }
            positionsToCheck = temp;
            temp = new ArrayList<>();
            stepsFromStart++;
        }
    }


    /**
     * calculates from which side of the starting line the player came from
     *
     * @param player number of player
     * @return last position before starting line was entered
     */
    private Position findPositionBeforeCross(int player) {
        Position temp = new Position(0, 0);
        for (int i = replayData.get(player).size() - 1; i > 0; i--) {
            if (board.getSurfaceAt(replayData.get(player).get(i - 1)) == Surfaces.ROAD) {
                temp = replayData.get(player).get(i - 1);
                i = 0;
            }
        }
        return temp;
    }

    /**
     * adds lap to player count or removes depending on if and in which direction the starting line is crossed
     *
     * @param player number of player
     * @param target end of route player took
     */
    protected void handleStartCross(int player, Position target) {
        Position[] route = calcRoute(players[player].getCurrentPos(), target);
        for (int i = 0; i < route.length - 1; i++) {
            if (board.getSurfaceAt(route[i]) == Surfaces.START) {
                final int startY = route[i].getPosY();
                final int startX = route[i].getPosX();
                while (i < (route.length - 1)) {
                    i++;
                    Position posBeforeCross = findPositionBeforeCross(player);
                    if (board.getSurfaceAt(route[i]) == Surfaces.ROAD) {
                        switch (direction) {
                            case UP -> {
                                if (posBeforeCross.getPosY() > startY && route[i].getPosY() < startY) {
                                    players[player].addLap();
                                } else if (posBeforeCross.getPosY() < startY) {
                                    players[player].removeLap();
                                }
                            }
                            case DOWN -> {
                                if (posBeforeCross.getPosY() < startY && route[i].getPosY() > startY) {
                                    players[player].addLap();
                                } else if (posBeforeCross.getPosY() > startY) {
                                    players[player].removeLap();
                                }
                            }
                            case RIGHT -> {
                                if (posBeforeCross.getPosX() < startX && route[i].getPosX() > startX) {
                                    players[player].addLap();
                                } else if (posBeforeCross.getPosX() > startX) {
                                    players[player].removeLap();
                                }
                            }
                            case LEFT -> {
                                if (posBeforeCross.getPosX() > startX && route[i].getPosX() < startX) {
                                    players[player].addLap();
                                } else if (posBeforeCross.getPosX() < startX) {
                                    players[player].removeLap();
                                }
                            }
                        }
                        i = route.length;
                    }
                }
            }
        }
    }

    /**
     * return an array with the winning player-/s in it
     *
     * @return winning player array
     */
    public Player[] returnWinners() {
        Player[] winners = new Player[MAX_PLAYER_NUMBER];
        int maxWinningDistance = 0;
        for (int i = 0; i < players.length; i++) {
            if (players[i].getLap() == 2) {
                Position lastPos = replayData.get(i).get(replayData.get(i).size() - 1);
                Position secondToLastPos = replayData.get(i).get(replayData.get(i).size() - 2);
                Position[] route = calcRoute(secondToLastPos, lastPos);
                for (int j = 0; j < route.length; j++) {
                    if (board.getSurfaceAt(route[j]) == Surfaces.START) {
                        int routeFromStart = calcRoute(route[j], lastPos).length;
                        if (routeFromStart > maxWinningDistance) {
                            maxWinningDistance = routeFromStart;
                            winners = new Player[MAX_PLAYER_NUMBER];
                            winners[i] = players[i];
                        } else if (routeFromStart == maxWinningDistance) {
                            winners[i] = players[i];
                        }
                    }
                }

            }
        }
        return winners;
    }

    /**
     * changes status of won if one or more players have won
     */
    public void checkWon() {
        for (Player player : players) {
            if (player.getLap() == 2) {
                this.isGameWon = true;
                break;
            }
        }
    }

    /**
     * starts the replay
     *
     * @param move on which move the replay should be started
     */
    public void playReplay(int move) {
        inAnimation = true;
        guiCon.playReplayMove(this, replayData, move, currAnimationSpeed);
    }

    /**
     * Parses JSON File
     *
     * @param filename filename
     * @return the read data as a class
     * @throws FileNotFoundException if file not found
     */
    private GameFromAndToJSON parseJSON(String filename) throws FileNotFoundException {
        Gson gson = new Gson();
        GameFromAndToJSON game = null;
        if (filename.contains(System.getProperty("java.io.tmpdir"))) {
            try {
                game = gson.fromJson(new FileReader(filename), GameFromAndToJSON.class);
            } catch (JsonSyntaxException jsonSyntaxException) {
                guiCon.outputErrorMessage(ErrorCodes.WrongMapSyntax, "");
            }
        } else {
            Reader r = new InputStreamReader(Objects.
                    requireNonNull(this.getClass().getResourceAsStream("/" + filename)));

            try {
                game = gson.fromJson(r, GameFromAndToJSON.class);
            } catch (JsonSyntaxException jsonSyntaxException) {
                guiCon.outputErrorMessage(ErrorCodes.WrongMapSyntax, "");
            }
        }
        return game;
    }


    /**
     * Converts direction from StartCrossDirection to int
     *
     * @param s StartCrossDirection value
     * @return int value
     */
    private int convertDirFromEnumToInt(StartCrossDirection s) {
        int i = 0;
        switch (s) {
            case RIGHT:
                i = 1;
                break;
            case DOWN:
                i = 2;
                break;
            case LEFT:
                i = 3;
        }
        return i;
    }

    /**
     * Converts direction from int to StartCrossDirection
     *
     * @param i int value
     */
    private void convertDirFromIntToEnum(int i) {
        switch (i) {
            case 0 -> this.direction = StartCrossDirection.UP;
            case 1 -> this.direction = StartCrossDirection.RIGHT;
            case 2 -> this.direction = StartCrossDirection.DOWN;
            case 3 -> this.direction = StartCrossDirection.LEFT;
        }
    }

    /**
     * initializes the map again
     */
    public void reInitializeGame() {
        guiCon.initializeGame(this.board, this.direction, false, players);
    }

    /**
     * checks if provided game data is valid
     *
     * @param g class of game data
     * @return error code that states if data is valid
     */
    private ErrorCodes validateGameData(GameFromAndToJSON g) {
        Board tempBoard = new Board();
        ErrorCodes errorCode = tempBoard.isBoardValid(g.getTrack());
        if (errorCode == ErrorCodes.NoError) {
            this.board.setValues(Board.convertBoardIntToStates(g.getTrack()));
        } else {
            guiCon.outputErrorMessage(errorCode, "");
        }

        if (errorCode == ErrorCodes.NoError) {
            if (g.getDirection() < 0 || g.getDirection() > 3 || g.getCurrentPlayer() < 0
                    || g.getCurrentPlayer() > (MAX_PLAYER_NUMBER - 1)) {
                guiCon.outputErrorMessage(ErrorCodes.WrongCurrentPlayerOrDirection, "");
                errorCode = ErrorCodes.WrongCurrentPlayerOrDirection;
            } else {
                convertDirFromIntToEnum(g.getDirection());
                this.currentPlayer = g.getCurrentPlayer();
            }
        }

        if (errorCode == ErrorCodes.NoError) {
            if (g.getPlayers() == null || g.getPlayers().length != MAX_PLAYER_NUMBER) {
                errorCode = ErrorCodes.InvalidPLayerData;
                guiCon.outputErrorMessage(ErrorCodes.InvalidPLayerData, "");
            } else {
                Player[] tempPlayers = new Player[MAX_PLAYER_NUMBER];
                for (int i = 0; i < MAX_PLAYER_NUMBER; i++) {
                    tempPlayers[i] = g.getPlayers()[i];
                }
                for (int i = 0; i < MAX_PLAYER_NUMBER; i++) {
                    if (!tempPlayers[i].isValidPlayer(this.board, tempPlayers)) {
                        guiCon.outputErrorMessage(ErrorCodes.InvalidPLayerData, "");
                        errorCode = ErrorCodes.InvalidPLayerData;
                        i = MAX_PLAYER_NUMBER;
                    }
                }
                if (errorCode == ErrorCodes.NoError) {
                    this.players = new Player[MAX_PLAYER_NUMBER];
                    System.arraycopy(tempPlayers, 0, this.players, 0, MAX_PLAYER_NUMBER);
                }
            }
        }
        return errorCode;
    }

    /**
     * Reads out the needed Data to create a Game from a JSON File. If game is in race, prepares first player move.
     *
     * @param filename name of file to read data from
     */
    public ErrorCodes getGameFromJson(String filename) {
        GameFromAndToJSON g;
        ErrorCodes errorCode = ErrorCodes.NoError;
        try {
            g = parseJSON(filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (g == null) {
            errorCode = ErrorCodes.WrongMapSyntax;
        }
        if (errorCode == ErrorCodes.NoError) {

            errorCode = validateGameData(g);

            if (errorCode == ErrorCodes.NoError) {
                byte firstActive = findFirstActivePlayer();
                if (firstActive == -1) {
                    gameMode = GameMode.Menu_Mode;
                    guiCon.initializeGame(this.board, this.direction, false, players);
                } else {
                    gameMode = GameMode.Race_Mode;
                    guiCon.initializeGame(this.board, this.direction, true, players);
                    guiCon.updatePlayerInfoTextField(players[currentPlayer].getName() + " make your move!");
                    prepareNextMove(players[firstActive].getLastPos(), players[firstActive].getCurrentPos(), true);
                    for (int i = 0; i < MAX_PLAYER_NUMBER; i++) {
                        Position temp = players[i].getCurrentPos();
                        replayData.get(i).add(new Position(temp.getPosX(), temp.getPosY()));
                    }
                }
            }
        }
        return errorCode;
    }

    /**
     * writes the current gameState to a file
     *
     * @param fileName name of the file
     */
    public void saveGameToJson(String fileName) {
        GameFromAndToJSON g = new GameFromAndToJSON(Board.convertBoardStatesToInt(this.board.getValues()),
                convertDirFromEnumToInt(this.direction), this.currentPlayer, this.players);
        Gson gson = new Gson();
        String jsonData = gson.toJson(g);

        String filePath = System.getProperty("java.io.tmpdir") + fileName;

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * reverses the start cross direction
     */
    private StartCrossDirection reverseStartCrossDir(StartCrossDirection dir) {
        return switch (dir) {
            case UP -> StartCrossDirection.DOWN;
            case DOWN -> StartCrossDirection.UP;
            case RIGHT -> StartCrossDirection.LEFT;
            case LEFT -> StartCrossDirection.RIGHT;
        };
    }

    /**
     * adds or subtracts a line from the board
     *
     * @param dir position where line shall be deleted or added
     * @param add true if line shall be added false if line shall be deleted
     */
    public void addOrDeleteLine(StartCrossDirection dir, boolean add) {
        if (add) {
            this.board = board.addLine(dir);
        } else {
            this.board = board.deleteLine(dir);
        }
        guiCon.initializeGame(this.board, this.direction, false, players);
    }

    /**
     * replaces cells with starting line cells in given axis
     *
     * @param vertical axis
     * @param start    starting position
     */
    private void setStartingLine(boolean vertical, Position start) {
        if (!vertical) {
            Position left = start.getLeftNeighbour();
            Position right = start.getRightNeighbour(this.board);
            this.board.changeBoardCell(start.getPosX(), start.getPosY(), Surfaces.START, this.board, this.guiCon, StartCrossDirection.DOWN);
            this.direction = StartCrossDirection.DOWN;
            while (left != null && board.getValues()[left.getPosX()][left.getPosY()] == Surfaces.ROAD) {
                this.board.changeBoardCell(left.getPosX(), left.getPosY(), Surfaces.START, this.board, this.guiCon, StartCrossDirection.DOWN);
                left = left.getLeftNeighbour();
            }
            while (right != null && board.getValues()[right.getPosX()][right.getPosY()] == Surfaces.ROAD) {
                this.board.changeBoardCell(right.getPosX(), right.getPosY(), Surfaces.START, this.board, this.guiCon, StartCrossDirection.DOWN);
                right = right.getRightNeighbour(this.board);
            }
        } else {
            Position top = start.getTopNeighbour();
            Position bottom = start.getBottomNeighbour(this.board);
            this.board.changeBoardCell(start.getPosX(), start.getPosY(), Surfaces.START, this.board, this.guiCon, StartCrossDirection.RIGHT);
            this.direction = StartCrossDirection.RIGHT;
            while (top != null && board.getValues()[top.getPosX()][top.getPosY()] == Surfaces.ROAD) {
                this.board.changeBoardCell(top.getPosX(), top.getPosY(), Surfaces.START, this.board, this.guiCon, StartCrossDirection.RIGHT);
                top = top.getTopNeighbour();
            }
            while (bottom != null && board.getValues()[bottom.getPosX()][bottom.getPosY()] == Surfaces.ROAD) {
                this.board.changeBoardCell(bottom.getPosX(), bottom.getPosY(), Surfaces.START, this.board, this.guiCon, StartCrossDirection.RIGHT);
                bottom = bottom.getBottomNeighbour(this.board);
            }
        }
    }

    /**
     * reacts to user input corresponding to the starting line
     *
     * @param indexI I index of user click
     * @param indexJ J index of user click
     * @param rotate true if user used middle mouse click on starting line tile
     * @param swDir  true if user used right mouse click on starting line tile
     */
    public void handleTheStartingLine(int indexI, int indexJ, boolean rotate, boolean swDir) {
        ArrayList<Position> oldSL = this.board.getCurrStartingLine();
        Position start = new Position(indexI, indexJ);
        if (this.board.getValues()[indexI][indexJ] == Surfaces.START) {
            if (swDir) {
                this.direction = reverseStartCrossDir(this.direction);
                this.board.changeStartingLineCrossDirection(oldSL, this.guiCon, this.direction);
            } else if (rotate) {
                Position[] neighbours = {start.getTopNeighbour(), start.getRightNeighbour(this.board), start.getBottomNeighbour(this.board), start.getLeftNeighbour()};
                boolean startLineDir = this.board.getStartingLineDir(neighbours);

                this.board.deleteCurrStartingLine(oldSL, this.guiCon, this.direction);
                setStartingLine(startLineDir, start);
            }
        } else {
            this.board.deleteCurrStartingLine(oldSL, this.guiCon, this.direction);
            setStartingLine(board.findShortestPath(start), start);
        }
    }

    /**
     * gives a way to check if board is valid from the gui without giving the gui access to the board
     *
     * @return true if board is valid, false if not
     */
    public boolean checkBoardValidity() {
        ErrorCodes error = this.board.isBoardValidForRace(this.board);
        this.guiCon.outputErrorMessage(error, "");
        return error == ErrorCodes.NoError;
    }
}
