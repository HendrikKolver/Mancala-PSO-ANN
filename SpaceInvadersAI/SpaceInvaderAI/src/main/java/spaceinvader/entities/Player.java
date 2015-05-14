package spaceinvader.entities;

import java.util.ArrayList;
import spaceinvader.gameRunner.BulletController;

/**
 *
 * @author Hendrik Kolver
 */
public class Player extends GameObject{
    private ArrayList<Building> buildings;
    private ArrayList<Shield> shields;
    private int bulletLimit;
    private int lives;
    private int kills;
    private boolean isAlive;
    
    public Player(){
        //start location
        this.xPosition = 7;
        this.yPosition = 2;
        this.xSize =3;
        this.ySize =1;
        bulletLimit = 1;
        this.lives = 2;
        buildings = new ArrayList();
    }
    
    public ArrayList<String> getPossibleMoves(){
        ArrayList<String> possibleMoves = new ArrayList();

        possibleMoves.add("Nothing");
        if(this.xPosition >1){
            possibleMoves.add("MoveLeft");
        }
        if(this.xPosition+this.xSize <17){
            possibleMoves.add("MoveRight");
        }
        
        if(BulletController.getInstance().getPlayerBulletCount()<bulletLimit){
            //check if shield infront of player
            possibleMoves.add("Shoot");
        }
        
        if(canLifeBeUsed() && !isBuildingBehindPlayer()){
            possibleMoves.add("BuildAlienFactory");
            possibleMoves.add("BuildMissileController");
        }
        
        possibleMoves.add("BuildShield");
        
        return possibleMoves;
    }
    
    private boolean canLifeBeUsed(){
        return (this.lives >0);
    }
    
    private boolean isBuildingBehindPlayer(){
        
        for(Building building : buildings){
            if(building.getxPosition() >= this.getxPosition()-2 && building.getxPosition() <= this.getxPosition()+2){
                
                return true;
            }
        }
        return false;
    }
    
    public void makeMove(String move){
        switch(move){
            case "MoveLeft" : {
                moveLeft();
                break;
            }
            case "MoveRight" : {
                moveRight();
                break;
            }
            case "Shoot" : {
                fireBullet();
                break;
            }
            case "BuildAlienFactory" : {
                buildAlienFactory();
                break;
            }
            case "BuildMissileController" : {
                buildBulletFactory();
                break;
            }
            case "BuildShield" : {
                buildShield();
                break;
            }
        }
    }
    
    private void moveLeft(){
        this.updatePosition("LEFT");
    }
    
    private void moveRight(){
        this.updatePosition("RIGHT");
    }
    
    private void fireBullet(){
        PlayerBullet playerBullet = new PlayerBullet(xPosition,yPosition+1);
        BulletController.getInstance().addPlayerBullet(playerBullet);
    }
    
    private void buildBulletFactory(){
        //xPos, yPos, xSize, ySize
        BulletFactory bulletFactory = new BulletFactory(this.getxPosition(),1,3,1);
        this.buildings.add(bulletFactory);
        this.lives--;
    }
    
    private void buildAlienFactory(){
        //xPos, yPos, xSize, ySize
        AlienFactory alienFactory = new AlienFactory(this.getxPosition(),1,3,1);
        this.buildings.add(alienFactory);
        this.lives--;
    }
    
    private void buildShield(){
        //TODO
    }
    
    public ArrayList<Building> getAllBuildings(){
        return this.buildings;
    }


}
