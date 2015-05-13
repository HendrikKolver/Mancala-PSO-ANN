package spaceinvader.entities;

import java.util.ArrayList;
import spaceinvader.gameRunner.BulletController;

/**
 *
 * @author Hendrik Kolver
 */
public class Player extends GameObject{
    private ArrayList buildings;
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
        
        //check if factories are behind player
        possibleMoves.add("BuildAlienFactory");
        possibleMoves.add("BuildMissileController");
        possibleMoves.add("BuildShield");
        
        return possibleMoves;
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
        //TODO
    }
    
    private void buildAlienFactory(){
        //TODO
    }
    
    private void buildShield(){
        //TODO
    }


}
