package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class GameObject {
    protected int xPosition;
    protected int yPosition;
    protected int xSize;
    protected int ySize;
    
    public void updatePosition(String direction){
        switch(direction){
            case "LEFT" : {
                //System.out.println("Called Left");
                int newX = xPosition-1;
                if(isPositionInBounds(newX,yPosition)){
                    xPosition = newX;
                }
                break;
            }
            case "RIGHT" : {
               // System.out.println("Called Right");
                int newX = xPosition+1;
                if(isPositionInBounds(newX,yPosition)){
                    xPosition = newX;
                }
                break;
            }
            case "UP" : {
               // System.out.println("Called Up");
                int newY = yPosition+1;
                if(isPositionInBounds(xPosition,newY)){
                    yPosition = newY;
                }
                break;
            }
            case "DOWN" : {
               // System.out.println("Called Down");
                 int newY = yPosition-1;
                if(isPositionInBounds(xPosition,newY)){
                    yPosition = newY;
                }
                break;
            }
        }
    }
    
    protected boolean isPositionInBounds(int newX, int newY)
    {
        
        if((newX + xSize-1 >= 18) || (newX + xSize-1 <= 0)){
            
            return false;
        }
        
        if((newY + ySize-1 >= 12) || (newY + ySize-1 <= 0)){
            return false;
        }
        return true;
            
    };

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }
    
    public GameObject getCopy(){
        GameObject gameObject = new GameObject();
        gameObject.xPosition = this.xPosition;
        gameObject.yPosition = this.yPosition;
        gameObject.xSize = this.xSize;
        gameObject.ySize = this.ySize;
        return gameObject;
    };
    
    public String getRepresentation(){
        return "D";
    }
    
    public String stringContent(){
        return "R";
    }
    
    
    
    
}
