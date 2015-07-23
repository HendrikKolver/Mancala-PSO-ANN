package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class BulletFactory extends GameObject {

    public BulletFactory(int xPos, int yPos, int xSize, int ySize) {
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.xSize = xSize;
        this.ySize = ySize;      
        this.representation = "B";
    }

    @Override
    public String stringContent() {
        return "Bullet Factory xPos: "+this.getxPosition();
    }
}
