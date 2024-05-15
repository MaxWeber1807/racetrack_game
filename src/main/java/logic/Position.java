package logic;

/**
 * class for a position in the board
 */
public class Position {

    private final int posX, posY;

    public Position (int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    //Getters and Setters
    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public Position getTopNeighbour() {
        if (this.getPosY() == 0) {
            return null;
        }
            return new Position(this.getPosX(), this.getPosY() - 1);
    }
    public Position getBottomNeighbour(Board b) {
        if (this.getPosY() >= b.getLength(0) - 1) {
            return null;
        }
            return new Position(this.getPosX(), this.getPosY() + 1);
    }

    public Position getRightNeighbour(Board b) {
        if (this.getPosX() >= b.getLength() - 1) {
            return null;
        }
            return new Position(this.getPosX() + 1, this.getPosY());
    }
    public Position getLeftNeighbour() {
        if (this.getPosX() == 0) {
            return null;
        }
            return new Position(this.getPosX() - 1, this.getPosY());
    }



    /**
     * compares two positions
     * @param pos2 second position
     * @return true if they are equal, false if not
     */
    public boolean isEqualTo(Position pos2) {
        return (this.getPosX() == pos2.getPosX() && this.getPosY() == pos2.getPosY());
    }
}
