package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class BulletFactory extends Building {

    public BulletFactory(int xPos, int yPos, int xSize, int ySize) {
        super(xPos, yPos, xSize, ySize);
    }

    @Override
    public String stringContent() {
        return "Bullet Factory xPos: "+this.getxPosition();
    }

}
