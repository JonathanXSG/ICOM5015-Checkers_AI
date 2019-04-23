/**
 * Class for holding the coordinates
 * @author Victor, Jonathan, Eduardo
 *
 */
public class Pair<X,Y> {

    public final X posX;
    public final Y posY;

    public Pair(X posX, Y posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public int hashCode() { return posX.hashCode() ^ posY.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) return false;
        Pair pairObject = (Pair) o;
        return this.posX.equals(pairObject.posX) &&
                this.posY.equals(pairObject.posY);
    }

    @Override
    public String toString() {
        return "[X=" + posX +
                ", Y=" + posY +
                ']';
    }
}