package spaceinvader.entities;

import spaceinvader.gameRunner.BulletController;

/**
 *
 * @author Hendrik Kolver
 */
public class Alien extends GameObject{
    private String moveDirection = "LEFT";
    private BulletController bulletController;
    
    public Alien(int xPosition, int yPosition){
        this.xPosition = xPosition;
        this.yPosition = yPosition;
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
    

    public void updatePosition(String direction){
        switch(direction){
            case "LEFT" : {
                int newX = xPosition-1;
                if(isPositionInBounds(newX,yPosition)){
                    xPosition = newX;
                }
                break;
            }
            case "RIGHT" : {
                int newX = xPosition+1;
                if(isPositionInBounds(newX,yPosition)){
                    xPosition = newX;
                }
                break;
            }
            case "DOWN" : {
                 int newY = yPosition-1;
                if(isPositionInBounds(xPosition,newY)){
                    yPosition = newY;
                }else{
                    //System.out.println("Game Over");
                }
            }
        }
    }
    
    public void fireBullet(){
        //yPos +1 so that the bullet is spawned below the alien
        AlienBullet alienBullet = new AlienBullet(xPosition,yPosition-1); // check if Alien infront and if so move bullet y ahead with 2
        bulletController.addAlienBullet(alienBullet);
    }

    public BulletController getBulletController() {
        return bulletController;
    }

    public void setBulletController(BulletController bulletController) {
        this.bulletController = bulletController;
    }
    
    @Override
    public GameObject getCopy(){
        Alien alien = new Alien(this.xPosition,this.yPosition);
        alien.moveDirection = this.moveDirection;
        return alien;
    }
    
    

}
