package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class AlienFactory extends GameObject {

    public AlienFactory(int xPos, int yPos, int xSize, int ySize) {
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.xSize = xSize;
        this.ySize = ySize; 
        this.representation = "X";
    }

    @Override
    public String stringContent() {
        return "Alien Factory xPos: "+this.getxPosition();
    }
}
