package logic;

/**
 * saves all information for a single player
 */
public class Player {

    private boolean active;
    private final boolean ai;

    private final String name;

    private int[] current, last;

    private byte lap;

    /**
     * initializes default inactive player for testing purposes
     */
    public Player() {
        this.active = false;
        this.ai = false;
        this.name = "";
        setLastPos(new Position(0,0));
        setCurrPos(new Position(0,0));
        this.lap = 0;
    }

    /**
     * initializes player
     */
    public Player(boolean active, boolean ai, String name, Position last, Position current, byte lap) {
        this.active = active;
        this.ai = ai;
        this.name = name;
        setLastPos(last);
        setCurrPos(current);
        this.lap = lap;
    }

    //Getters and Setters
    public boolean isActive() {
        return active;
    }

    public boolean isAI() {
        return ai;
    }

    public Position getLastPos() {
        Position temp = new Position(last[0],last[1]);
        return temp;
    }

    public Position getCurrentPos() {
        Position temp = new Position(current[0],current[1]);
        return temp;
    }
    protected void setActive(boolean active) {
        this.active = active;
    }

    public byte getLap() {
        return lap;
    }

    public void setCurrPos(Position pos) {
        this.current = new int[2];
        if (pos != null) {
            this.current[0] = pos.getPosX();
            this.current[1] = pos.getPosY();
        }
    }

    public void setLastPos(Position pos) {
        this.last = new int[2];
        if (pos != null) {
            this.last[0] = pos.getPosX();
            this.last[1] = pos.getPosY();
        }
    }

    public String getName() {
        return this.name;
    }

    protected void setLap(int i) {
        this.lap = (byte) i;
    }


    /**
     * adds a lap
     */
    protected void addLap() {
        lap++;
    }

    /**
     * removes a lap
     */
    protected void removeLap() {
        if (lap > 0) {
            lap--;
        }
    }

    /**
     * checks if the data in player is correct
     * @param b board
     * @param players other players
     * @return true if data is correct, false if not
     */
    protected boolean isValidPlayer(Board b, Player[] players) {
        if (lap < 0 || lap > 2 || current.length < 0 || current.length > 2 || last.length < 0 || last.length > 2) {
            return false;
        }
        if (!b.isValidPosition(new Position(current[0], current[1]), players)) {
            return false;
        }
        return b.isValidPosition(new Position(last[0], last[1]), players);
    }

}
