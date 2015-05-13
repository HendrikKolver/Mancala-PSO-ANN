package spaceinvader.entities;

/**
 *
 * @author Hendrik Kolver
 */
public class Alien extends GameObject{
    private String moveDirection = "LEFT";
    
    public Alien(){
        this.xPosition = 17;
        this.yPosition = 11;
        this.xSize = 1;
        this.ySize = 1;
    }
    
    public String getMoveDirection(){
        return this.moveDirection;
    }
    
    public void invertMoveDirection(){
        if(this.moveDirection.equals("LEFT")){
            this.moveDirection = "RIGHT";
        }else{
           this.moveDirection = "LEFT"; 
        }   
    }
    
    @Override
    public void updatePosition(String direction){
        switch(direction){
            case "LEFT" : {
                int newX = xPosition-2;
                if(isPositionInBounds(newX,yPosition)){
                    xPosition = newX;
                }else{
                    moveDirection = "RIGHT";
                    updatePosition("DOWN");
                }
                break;
            }
            case "RIGHT" : {
                int newX = xPosition+2;
                if(isPositionInBounds(newX,yPosition)){
                    xPosition = newX;
                }else{
                    moveDirection = "LEFT";
                    updatePosition("DOWN");
                }
                break;
            }
            case "UP" : {
                int newY = yPosition+1;
                if(isPositionInBounds(xPosition,newY)){
                    yPosition = newY;
                }
                break;
            }
            case "DOWN" : {
                 int newY = yPosition-1;
                if(isPositionInBounds(xPosition,newY)){
                    yPosition = newY;
                }
                break;
            }
        }
    }

}
