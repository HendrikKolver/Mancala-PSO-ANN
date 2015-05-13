package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class AlienBullet extends GameObject{
    
    public AlienBullet(int xPos, int yPos){
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.xSize = 1;
        this.ySize = 1;
    }

}
