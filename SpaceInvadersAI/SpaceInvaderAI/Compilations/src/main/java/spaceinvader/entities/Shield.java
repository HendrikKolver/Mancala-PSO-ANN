package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class Shield extends GameObject {

    public Shield(int xPos, int yPos, int xSize, int ySize) {
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.xSize = xSize;
        this.ySize = ySize; 
        this.representation = "-";
    }

    @Override
    public String stringContent() {
        return "Shield xPos: "+this.getxPosition();
    }
}
