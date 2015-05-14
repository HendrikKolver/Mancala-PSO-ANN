package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public abstract class Building extends GameObject{
    public Building(int xPos, int yPos, int xSize, int ySize){
        this.xPosition = xPos;
        this.yPosition = yPos;
        this.xSize = xSize;
        this.ySize = ySize;
        
        
    }
    
    public abstract String stringContent();
}
