package logic;

/**
 * saves game data to write to json or that is read from json
 */
public class GameFromAndToJSON {

    private final int[][] track;

    private final int direction;
    private final int currentPlayer;

    private final Player[] player;

    public GameFromAndToJSON(int[][] board, int dir, int currentPlayer, Player[] players) {
        this.track = board;
        this.direction = dir;
        this.currentPlayer = currentPlayer;
        this.player = players;
    }

    //Getters and Setters
    protected int[][] getTrack() {
        return this.track;
    }

    protected Player[] getPlayers() {
        return this.player;
    }

    protected int getDirection() {
        return this.direction;
    }

    protected int getCurrentPlayer() {
        return this.currentPlayer;
    }
}
