package logic;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * class for the map
 */
public class Board {

    /**
     * values for the map
     */
    private Surfaces[][] values;



    /**
     * returns reference to values
     * @return reference
     */
    public Surfaces[][] getValues() {
        Surfaces[][] temp = new Surfaces[values.length][];
        for (int i = 0; i < values.length; i++) {
            temp[i] = Arrays.copyOf(values[i], values[i].length);
        }
        return temp;
    }

    /**
     * standard constructor
     */
    protected Board (){
    }

    /**
     * creates board from string for testing purposes
     * @param s string board is created from
     * @param x x length of board
     * @param y y length of board
     */
    protected Board (String s, int x, int y){
        values = new Surfaces[x][y];
        String[] rows = s.split("\n");
        for (int i = 0; i < rows.length; i++) {
            for (int j = 0; j < rows[i].length(); j++) {
                switch (rows[i].charAt(j)) {
                    case '0':
                        setSurfaceAt(j, i , Surfaces.GRAVEL);
                        break;
                    case '1':
                        setSurfaceAt(j, i , Surfaces.ROAD);
                        break;
                    case '2':
                        setSurfaceAt(j, i , Surfaces.START);
                        break;
                }
            }
        }
    }

    /**
     * instantiates the values
     * @param values instantiation
     */
    protected void setValues(Surfaces[][] values) {
        this.values = values;
    }

    /**
     * changes surface at specific position
     * @param i x value
     * @param j y value
     * @param sur surface to be changed to
     */
    private void setValueAt(int i, int j, Surfaces sur) {
        this.values[i][j] = sur;
    }

    /**
     * returns an array with neighbours of given position
     * @param pos middle position
     * @return array
     */
    protected Position[] getNeighbours(Position pos) {
        Position[] neighbours = {pos.getTopNeighbour(), pos.getRightNeighbour(this),
                pos.getBottomNeighbour(this), pos.getLeftNeighbour()};
        return neighbours;
    }

    /**
     * converts board from int to states
     * @param track
     * @return the converted board
     */
    protected static Surfaces[][] convertBoardIntToStates(int[][] track) {
        Surfaces[][] b = new Surfaces[track.length][track[0].length];
        for (int i = 0; i < track.length; i++) {
            for (int j = 0; j < track[i].length; j++) {
                switch (track[i][j]) {
                    case 0:
                        b[i][j] = Surfaces.GRAVEL;
                        break;
                    case 1:
                        b[i][j] = Surfaces.ROAD;
                        break;
                    case 2:
                        b[i][j] = Surfaces.START;
                        break;
                }
            }
        }
        return b;
    }

    /**
     * transforms a board to string values for testing
     * @return board as string
     */
    protected String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values[0].length; i++) {
            for (int j = 0; j < values.length; j++) {
                Surfaces surface = this.getSurfaceAt(new Position(j,i));
                char surfaceChar;
                switch (surface) {
                    case GRAVEL:
                        surfaceChar = '0';
                        break;
                    case ROAD:
                        surfaceChar = '1';
                        break;
                    case START:
                        surfaceChar = '2';
                        break;
                    default:
                        surfaceChar = ' ';
                }
                sb.append(surfaceChar);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * converts board from states to int
     * @param board
     * @return the converted board
     */
    protected static int[][] convertBoardStatesToInt(Surfaces[][] board) {
        int[][] b = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                switch (board[i][j]) {
                    case GRAVEL:
                        b[i][j] = 0;
                        break;
                    case ROAD:
                        b[i][j] = 1;
                        break;
                    case START:
                        b[i][j] = 2;
                        break;
                }
            }
        }
        return b;
    }

    /**
     * return the length of the array at a specific position
     * @param i position
     * @return length
     */
    public int getLength(int i) {
        return this.values[i].length;
    }

    /**
     * return the length of the array
     */
    public int getLength() {
        return this.values.length;
    }

    /**
     * replaces a board cell
     * @param i index of cell to be changed
     * @param j index of cell to be changed
     * @param sur surface of the cell to be changed
     */
    private void setSurfaceAt(int i, int j, Surfaces sur) {
        this.values[i][j] = sur;
    }

    /**
     * return the surface at given position
     * @param pos position
     * @return surface
     */
    protected Surfaces getSurfaceAt(Position pos) {
        return this.values[pos.getPosX()][pos.getPosY()];
    }

    /**
     * Changes a board cell and gives that info to the gui
     * @param x coordinate
     * @param y coordinate
     * @param sur new surface
     * @param b board
     * @param gui instance of the gui
     * @param dir direction of the finish line
     */
    public void changeBoardCell(int x, int y, Surfaces sur, Board b,
                                GUIConnector gui, StartCrossDirection dir) {
        boolean isStart;
        isStart = b.getValues()[x][y] == Surfaces.START;
        b.setValueAt(x, y, sur);
        gui.changeGridPaneCell(x, y, sur, dir, isStart);
    }

    /**
     * finds the first occurrence of a start surface
     * @param b board
     * @return the position of the surface, if none is found null
     */
    private Position findStart(Board b) {
        for (int i = 0; i < b.getLength(); i++) {
            for (int j = 0; j < b.getLength(0); j++) {
                if (b.getSurfaceAt(new Position(i, j)) == Surfaces.START) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    /**
     * gets the position of the current starting line tiles
     * @return arraylist of the positions
     */
    protected ArrayList<Position> getCurrStartingLine() {
        ArrayList<Position> oldStartPositions = new ArrayList<>();
            for (int i = 0; i < this.getLength(); i++) {
                for (int j = 0; j < this.getLength(0); j++) {
                    if (this.getValues()[i][j] == Surfaces.START) {
                        oldStartPositions.add(new Position(i, j));
                    }
                }
            }
        return oldStartPositions;
    }

    /**
     * deletes the current starting line from the board
     * @param oldStartPositions old position of the starting line
     */
    protected void deleteCurrStartingLine(ArrayList<Position> oldStartPositions, GUIConnector gui,
                                       StartCrossDirection dir) {
        for (int i = 0; i < oldStartPositions.size(); i++) {
            this.setValueAt(oldStartPositions.get(i).getPosX(),oldStartPositions.get(i).getPosY(), Surfaces.ROAD);
            gui.changeGridPaneCell(oldStartPositions.get(i).getPosX(), oldStartPositions.get(i).getPosY(),
                    Surfaces.ROAD, dir, true);
        }
    }

    /**
     * resets each start tile in the gui to update the starting line
     * @param oldStartPositions positions to be updated
     */
    protected void changeStartingLineCrossDirection(ArrayList<Position> oldStartPositions, GUIConnector gui,
                                                 StartCrossDirection dir) {
        for (int i = 0; i < oldStartPositions.size(); i++) {
            gui.changeGridPaneCell(oldStartPositions.get(i).getPosX(), oldStartPositions.get(i).getPosY(),
                    Surfaces.START, dir, true);
        }
    }

    /**
     * return which way the starting line is aligned currently
     * @param neighbours neighbours of clicked position
     * @return true if starting line is aligned vertical false if horizontal
     */
    protected boolean getStartingLineDir(Position[] neighbours) {
        boolean horizontal = false;
        for (int i = 0; i < neighbours.length; i++) {
            if(neighbours[i] != null) {
                if (i % 2 == 0 && values[neighbours[i].getPosX()][neighbours[i].getPosY()] == Surfaces.START) {
                    horizontal = false;
                } else if (values[neighbours[i].getPosX()][neighbours[i].getPosY()] == Surfaces.START) {
                    horizontal = true;
                }
            }
        }
        return horizontal;
    }

    /**
     * traverses the board until round course is found or every accessible field has been visited
     * @param visited boolean representation of the real field with already visited squares marked as true
     * @param neighbours neighbours of the start
     * @param b board
     * @param isHorizontal true if starting line is horizontal false if not
     * @param isSlOne true if starting line is one block long, false if not
     * @return true if map hs round course false if not
     */
    private boolean traverseBoard(boolean[][] visited, Position[] neighbours, Board b, boolean isHorizontal,
                                 boolean isSlOne) {
        for (int i = 0; i < neighbours.length; i++) {
            //checks edge case where starting line is vertical but only one long
            if (isSlOne) {
                if (!isHorizontal && i == 0 && neighbours[i] != null
                        && b.getSurfaceAt(neighbours[i]) == Surfaces.START) {
                    return true;
                }
            }
            //returns true if top neighbour is start(round course found)
            if (isHorizontal && i == Logic.TOP && neighbours[i] != null && b.getSurfaceAt(neighbours[i]) == Surfaces.START) {
                return true;
                //returns true if right neighbour is start(round course found)
            } else if (!isHorizontal && i == Logic.RIGHT && neighbours[i] != null
                    && b.getSurfaceAt(neighbours[i]) == Surfaces.START) {
                return true;
            } else if (neighbours[i] != null && !visited[neighbours[i].getPosY()][neighbours[i].getPosX()]
                    && b.getSurfaceAt(neighbours[i]) != Surfaces.GRAVEL) {
                visited[neighbours[i].getPosY()][neighbours[i].getPosX()] = true;
                Position[] newNeighbours = {neighbours[i].getTopNeighbour(), neighbours[i].getRightNeighbour(b),
                        neighbours[i].getBottomNeighbour(b), neighbours[i].getLeftNeighbour()};
                if (!(isHorizontal && i == Logic.DOWN && b.getSurfaceAt(neighbours[i].getTopNeighbour()) == Surfaces.START)
                && !(!isHorizontal && i == Logic.LEFT && b.getSurfaceAt(neighbours[i].getRightNeighbour(b)) == Surfaces.START)
                && traverseBoard(visited, newNeighbours, b, isHorizontal, isSlOne)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * tries to find a round course by calling traverseBoard
     * @param b the board
     * @return true if map has round course, false if not
     */
    private boolean checkForRoundCourse(Board b, ArrayList<Position> currSL) {
        Position start = findStart(b);
        //because the b is mirrored here Right = Bottom, Top = Left
        Position[] neighbours = {start.getTopNeighbour(), start.getRightNeighbour(b),
                start.getBottomNeighbour(b), start.getLeftNeighbour()};
        boolean isHorizontal = getStartingLineDir(neighbours);
        boolean[][] visited = new boolean[b.getLength(0)][b.getLength()];
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[0].length; j++) {
                visited[i][j] = false;
            }
        }
        visited[start.getPosY()][start.getPosX()] = true;
        if (start != null) {
            return traverseBoard(visited, neighbours, b, isHorizontal, currSL.size() == 1);
        }
        return true;
    }

    /**
     * checks if board is valid for starting a race
     * @param b board
     * @return true if board is valid false if not
     */
    protected ErrorCodes isBoardValidForRace(Board b) {
        ArrayList<Position> currSL = b.getCurrStartingLine();
        ErrorCodes error = ErrorCodes.NoError;
        // checks if there is a starting line to begin with
        if (currSL.size() == 0) {
            error = ErrorCodes.StartMissing;
        } else {
            Position temp = currSL.get(0);
            Position[] neighbours = {temp.getTopNeighbour(), temp.getRightNeighbour(b) ,
                    temp.getBottomNeighbour(b), temp.getLeftNeighbour()};
            // checks if starting line has road at the ends
            if (((neighbours[0] != null && b.getSurfaceAt(neighbours[0]) == Surfaces.ROAD) ||
                    (currSL.get(currSL.size()- 1).getBottomNeighbour(b) != null
                            && b.getSurfaceAt(currSL.get(currSL.size()- 1).getBottomNeighbour(b)) == Surfaces.ROAD))
            && (((neighbours[3] != null && b.getSurfaceAt(neighbours[3]) == Surfaces.ROAD) ||
                    (currSL.get(currSL.size()- 1).getRightNeighbour(b) != null
                            && b.getSurfaceAt(currSL.get(currSL.size()- 1).getRightNeighbour(b)) == Surfaces.ROAD)))) {
                error = ErrorCodes.StartNotComplete;
            }
        }
        if (error == ErrorCodes.NoError) {
            boolean hasRoundCourse = checkForRoundCourse(b, currSL);
            if (!hasRoundCourse) {
                error = ErrorCodes.NoRoundCourse;
            }
        }
        return error;
    }

    /**
     * checks if board is valid, for reading in data
     * @param values board values to be checked
     * @return error code that states found error
     */
    protected ErrorCodes isBoardValid(int[][] values) {
        if (values == null || values[0].length == 0 || values.length == 0 || values[0].length == 0 || values.length < 10
                || values.length > 40 || values[0].length < 10 || values[0].length > 40) {
            return ErrorCodes.WrongBoardSize;
        } else {
            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[0].length; j++) {
                    if (values[i][j] < 0  || values[i][j] > 2) {
                        return ErrorCodes.WrongBoardContents;
                    }
                }
            }
        }
        return ErrorCodes.NoError;
    }

    /**
     * checks if the given position is a valid position in the current gameMode. What a valid position is changes
     * depending on the gameMode active.
     * @param players array of the players
     * @param pos position to be checked
     * @param currentPlayer current Player
     * @param gameMode current GameMode
     * @return true if valid position, false if not
     */
    protected boolean isValidPositionInGameMode(Player[] players, Position pos, int currentPlayer, GameMode gameMode) {

        if (gameMode == GameMode.Preparation_Mode) {
            while (currentPlayer > -1) {
                if (pos.isEqualTo(players[currentPlayer].getCurrentPos())) {
                    return false;
                } else {
                    currentPlayer--;
                }
            }
            return this.getSurfaceAt(pos) == Surfaces.ROAD;

        } else if (gameMode == GameMode.Race_Mode) {
            for (int i = 0; i < players.length; i++) {
                if (i != currentPlayer && players[i].getCurrentPos().isEqualTo(pos)) {
                    return false;
                }
            }
            int posX = pos.getPosX();
            int posY = pos.getPosY();
            return posX >= 0 && posX < values.length && posY >= 0 && posY < values[0].length;
        }
        return true;
    }

    /**
     * checks if json data has faulty information in the player position array
     * @param pos position to be checked
     * @param players player array
     * @return true of if position is valid, false if not
     */
    protected boolean isValidPosition(Position pos, Player[] players) {
        int posX = pos.getPosX();
        int posY = pos.getPosY();
        if (posX < 0 || posX >= values.length || posY < 0 || posY >= values[0].length) {
            return false;
        }
        for (int i = 0; i < players.length; i++) {
            if (players[i].isActive()) {
                for (int j = 0; j < players.length; j++) {
                    if (i != j && (players[i].getCurrentPos().isEqualTo(players[j].getCurrentPos())
                    || players[i].getLastPos().isEqualTo(players[j].getLastPos()))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * checks if position is inside the map
     * @param pos position to be checked
     * @return true if inside board false if not
     */
    protected boolean isOutOfBounds(Position pos) {
        int posX = pos.getPosX();
        int posY = pos.getPosY();
        return posX < 0 || posX >= values.length || posY < 0 || posY >= values[0].length;
    }

    /**
     * helping method to find free position starting from given position
     * @param start position to start searching from
     * @return found free position
     */
    private Position findFreePositionFromPos(Position start, Player[] players, int currentPlayer, GameMode gameMode) {
        boolean[][] visited = new boolean[this.getLength()][this.getLength(0)];
        Position[] neighbours = getNeighbours(start);
        ArrayList<Position> currSL = getCurrStartingLine();
        boolean found = false;

        for (int i = 0; i < currSL.size(); i++) {
            visited[currSL.get(i).getPosX()][currSL.get(i).getPosY()] = true;
        }

        while (!found) {
            for (int i = 0; i < neighbours.length; i++) {
                if (isValidPositionInGameMode(players, neighbours[i], currentPlayer, gameMode)
                        && this.getSurfaceAt(neighbours[i]) == Surfaces.ROAD) {
                    return neighbours[i];
                } else if (!visited[neighbours[i].getPosX()][neighbours[i].getPosY()]){
                    visited[neighbours[i].getPosX()][neighbours[i].getPosY()] = true;
                    neighbours = getNeighbours(neighbours[i]);
                }
            }
        }
        return start;
    }

    /**
     * finds the shortest path for starting line from given position
     * @param start starting position
     * @return true if vertical is the shortest patch false if horizontal is the shortest path
     */
    protected boolean findShortestPath(Position start) {
        Position left = start.getLeftNeighbour();
        Position right = start.getRightNeighbour(this);
        Position top = start.getTopNeighbour();
        Position bottom = start.getBottomNeighbour(this);
        int horCounter = 0;
        int verCounter = 0;

        while (left != null && this.getValues()[left.getPosX()][left.getPosY()] == Surfaces.ROAD) {
            horCounter++;
            left = left.getLeftNeighbour();
        }
        while (right != null && this.getValues()[right.getPosX()][right.getPosY()] == Surfaces.ROAD) {
            horCounter++;
            right = right.getRightNeighbour(this);
        }
        while (top != null && this.getValues()[top.getPosX()][top.getPosY()] == Surfaces.ROAD) {
            verCounter++;
            top = top.getTopNeighbour();
        }
        while (bottom != null && this.getValues()[bottom.getPosX()][bottom.getPosY()] == Surfaces.ROAD) {
            verCounter++;
            bottom = bottom.getBottomNeighbour(this);
        }
        return verCounter <= horCounter;
    }

    /**
     * deletes a line at the top
     * @param temp board where line should be deleted
     */
    private void deleteLineUp(Board temp) {
        temp.setValues(new Surfaces[this.getLength()][this.getLength(0) - 1]);
        for (int i = 0; i < this.getLength(); i++) {
            for (int j = 0; j < this.getLength(0) - 1; j++) {
                temp.setValueAt(i, j, this.getValues()[i][j + 1]);
            }
        }
    }

    /**
     * deletes a line at the bottom
     * @param temp board where line should be deleted
     */
    private void deleteLineDown(Board temp) {
        temp.setValues(new Surfaces[this.getLength()][this.getLength(0) - 1]);
        for (int i = 0; i < this.getLength(); i++) {
            for (int j = 0; j < this.getLength(0) - 1; j++) {
                temp.setValueAt(i, j, this.getValues()[i][j]);
            }
        }
    }

    /**
     * deletes a line at the right
     * @param temp board where line should be deleted
     */
    private void deleteLineRight(Board temp) {
        temp.setValues(new Surfaces[this.getLength() - 1][this.getLength(0)]);
        for (int i = 0; i < this.getLength() - 1; i++) {
            for (int j = 0; j < this.getLength(0); j++) {
                temp.setValueAt(i, j, this.getValues()[i][j]);
            }
        }
    }

    /**
     * adds a line at the top
     * @param temp board where line should be deleted
     */
    private void addLineUp(Board temp) {
        temp.setValues(new Surfaces[this.getLength()][this.getLength(0) + 1]);
        for (int i = 0; i < temp.getLength(); i++) {
            for (int j = 0; j < temp.getLength(0); j++) {
                if (j == 0) {
                    temp.setValueAt(i, j, Surfaces.GRAVEL);
                } else {
                    temp.setValueAt(i, j, this.getValues()[i][j - 1]);
                }
            }
        }
    }

    /**
     * adds a line at the bottom
     * @param temp board where line should be deleted
     */
    private void addLineDown(Board temp) {
        temp.setValues(new Surfaces[this.getLength()][this.getLength(0) + 1]);
        for (int i = 0; i < temp.getLength(); i++) {
            for (int j = 0; j < temp.getLength(0); j++) {
                if (j == temp.getLength(0) - 1) {
                    temp.setValueAt(i, j, Surfaces.GRAVEL);
                } else {
                    temp.setValueAt(i, j, this.getValues()[i][j]);
                }
            }
        }
    }

    /**
     * adds a line at the right
     * @param temp board where line should be deleted
     */
    private void addLineRight(Board temp) {
        temp.setValues(new Surfaces[this.getLength() + 1][this.getLength(0)]);
        for (int i = 0; i < temp.getLength(); i++) {
            for (int j = 0; j < temp.getLength(0); j++) {
                if (i == temp.getLength() - 1) {
                    temp.setValueAt(i, j, Surfaces.GRAVEL);
                } else {
                    temp.setValueAt(i, j, this.getValues()[i][j]);
                }
            }
        }
    }

    /**
     * adds a line at the left
     * @param temp board where line should be deleted
     */
    private void addLineLeft(Board temp) {
        temp.setValues(new Surfaces[this.getLength() + 1][this.getLength(0)]);
        for (int i = 0; i < temp.getLength(); i++) {
            for (int j = 0; j < temp.getLength(0); j++) {
                if (i == 0) {
                    temp.setValueAt(i, j, Surfaces.GRAVEL);
                } else {
                    temp.setValueAt(i, j, this.getValues()[i - 1][j]);
                }
            }
        }
    }

    /**
     * deletes a line at the left
     * @param temp board where line should be deleted
     */
    private void deleteLineLeft(Board temp) {
        temp.setValues(new Surfaces[this.getLength() - 1][this.getLength(0)]);
        for (int i = 0; i < this.getLength() - 1; i++) {
            for (int j = 0; j < this.getLength(0); j++) {
                temp.setValueAt(i, j, this.getValues()[i + 1][j]);
            }
        }
    }

    /**
     * deletes a line from the board
     * @param dir direction of starting line
     */
    protected Board deleteLine(StartCrossDirection dir) {
        Board temp = new Board();
        temp.setValues(this.getValues());

        if (values[0].length > 10 && (dir == StartCrossDirection.UP || dir == StartCrossDirection.DOWN)) {
            switch (dir) {
                case UP:
                    deleteLineUp(temp);
                    break;
                case DOWN:
                    deleteLineDown(temp);
                    break;
            }
        } else if (values.length > 10) {
            switch (dir) {
                case RIGHT:
                    deleteLineRight(temp);
                    break;
                case LEFT:
                    deleteLineLeft(temp);
                    break;
            }
        }

        return temp;
    }

    /**
     * adds a line to the board
     * @param dir direction of starting line
     */
    protected Board addLine(StartCrossDirection dir) {
        Board temp = new Board();
        temp.setValues(this.getValues());

        if (values[0].length < 20 && (dir == StartCrossDirection.UP || dir == StartCrossDirection.DOWN)) {
            switch (dir) {
                case UP:
                    addLineUp(temp);
                    break;
                case DOWN:
                    addLineDown(temp);
                    break;
            }
        } else if (values.length < 40) {
            switch (dir) {
                case RIGHT:
                    addLineRight(temp);
                    break;
                case LEFT:
                    addLineLeft(temp);
                    break;
            }
        }

        return temp;
    }



    /**
     * finds free position in front of starting line for the AI
     * @param players other players
     * @param dir direction of start
     * @param currentPlayer currently active player
     * @param gameMode game mode
     * @return free position
     */
    protected Position findFreePositionFromSL(Player[] players, StartCrossDirection dir,
                                           int currentPlayer, GameMode gameMode) {
        Position freePos = new Position(0,0);
        ArrayList<Position> sl = getCurrStartingLine();

        for (int i = 0; i < sl.size(); i++) {
            switch (dir) {
                case UP ->freePos = sl.get(i).getBottomNeighbour(this);
                case DOWN ->freePos = sl.get(i).getTopNeighbour();
                case RIGHT ->freePos = sl.get(i).getLeftNeighbour();
                case LEFT ->freePos = sl.get(i).getRightNeighbour(this);
            }
            if (isValidPositionInGameMode(players, freePos, currentPlayer, gameMode)) {
                return freePos;
            }
        }
        return findFreePositionFromPos(freePos, players, currentPlayer, gameMode);
    }
}
