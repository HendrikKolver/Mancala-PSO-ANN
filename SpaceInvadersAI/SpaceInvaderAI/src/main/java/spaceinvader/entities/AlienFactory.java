package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class AlienFactory extends Building {

    public AlienFactory(int xPos, int yPos, int xSize, int ySize) {
        super(xPos, yPos, xSize, ySize);
    }

    @Override
    public String stringContent() {
        return "Alien Factory xPos: "+this.getxPosition();
    }

    @Override
    public String getRepresentation() {
        return "X";
    }

}
