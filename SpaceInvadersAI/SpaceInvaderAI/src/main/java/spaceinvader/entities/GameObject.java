package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public abstract class GameObject {
    protected int xPosition;
    protected int yPosition;
    protected int xSize;
    protected int ySize;
    
    abstract void updatePosition();
}
