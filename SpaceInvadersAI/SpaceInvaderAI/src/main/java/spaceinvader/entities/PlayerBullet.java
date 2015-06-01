package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class PlayerBullet extends GameObject{
    public PlayerBullet(int xPos, int yPos){
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.xSize = 1;
        this.ySize = 1;
    }
    
    public PlayerBullet(int xPos, int yPos, int player){
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.xSize = 1;
        this.ySize = 1;
        this.player = player;
    }

}
