package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class Shield extends Building {

    public Shield(int xPos, int yPos, int xSize, int ySize) {
        super(xPos, yPos, xSize, ySize);
    }

    @Override
    public String stringContent() {
        return "Shield xPos: "+this.getxPosition();
    }

    @Override
    public String getRepresentation() {
        return "-";
    }

}
