package spaceinvader.entities;

import java.util.ArrayList;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.utilities.ArrayListCopy;

/**
 *
 * @author Hendrik Kolver
 */
public class Player extends GameObject{
    private ArrayList<GameObject> buildings;
    private ArrayList<GameObject> shields;
    private int bulletLimit;
    private int lives;
    private int kills;
    private boolean isAlive;
    private BulletController bulletController;
    private AlienController alienController;
    
    public Player(){
        //start location
        this.xPosition = 8;
        this.yPosition = 2;
        this.xSize =3;
        this.ySize =1;
        bulletLimit = 1;
        this.lives = 2;
        this.isAlive = true;
        this.kills = 0;
        buildings = new ArrayList();
    }
    
    public ArrayList<String> getPossibleMoves(){
        ArrayList<String> possibleMoves = new ArrayList();
          
        if(bulletController.getPlayerBulletCount()<bulletLimit && !isShieldBlockingPlayerBullet()){
            possibleMoves.add("Shoot");
        }
        
        possibleMoves.add("Nothing");
        
        if(canLifeBeUsed() && !isBuildingBehindPlayer()){
            possibleMoves.add("BuildAlienFactory");
            possibleMoves.add("BuildMissileController");
        }
        
        if(canLifeBeUsed() && !isShieldInfrontOfPlayer()){
            possibleMoves.add("BuildShield");
        }
        
        if(this.xPosition >1){
            possibleMoves.add("MoveLeft");
        }
        if(this.xPosition+this.xSize <17){
            possibleMoves.add("MoveRight");
        }

        return possibleMoves;
    }
    
    private boolean canLifeBeUsed(){
        return (this.lives >0);
    }
    
    private boolean isBuildingBehindPlayer(){
        for(GameObject building : buildings){
            if(building.getxPosition() >= this.getxPosition()-2 && building.getxPosition() <= this.getxPosition()+2){
                return true;
            }
        }
        return false;
    }
    
    private boolean isShieldBlockingPlayerBullet(){
        for(GameObject shield : shields){
            if(shield.getxPosition() == this.getxPosition()+1 ){
                return true;
            }
        }
        return false;
    }
    
    private boolean isShieldInfrontOfPlayer(){
         for(GameObject shield : shields){

            if(shield.getxPosition() == this.getxPosition() 
                || shield.getxPosition() == this.getxPosition()+1 
                || shield.getxPosition() == this.getxPosition()+2){
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
        PlayerBullet playerBullet = new PlayerBullet(this.getxPosition()+1,this.getyPosition()+1);
        bulletController.addPlayerBullet(playerBullet);
    }
    
    private void buildBulletFactory(){
        //xPos, yPos, xSize, ySize
        BulletFactory bulletFactory = new BulletFactory(this.getxPosition(),1,3,1);
        this.buildings.add(bulletFactory);
        //+1 for the initial limit of 1
        this.bulletLimit++;
        this.lives--;
    }
    
    private void buildAlienFactory(){
        //xPos, yPos, xSize, ySize
        AlienFactory alienFactory = new AlienFactory(this.getxPosition(),1,3,1);
        this.buildings.add(alienFactory);
        this.lives--;
    }
    
    private void buildShield(){
        removeAllObjectsInBlock(this.getxPosition());
        shields.addAll(createShieldBlock(this.getxPosition()));
        this.lives--;
    }
    
    public ArrayList<GameObject> getAllBuildings(){
        return this.buildings;
    }
    
    public void setShields(ArrayList<GameObject> shields){
        this.shields = shields;
    }
    
    public ArrayList<GameObject> getAllShields(){
        return shields;
    }
    
    public ArrayList<GameObject> createShieldBlock(int startPos){
        ArrayList<GameObject> shields = new ArrayList();
        int xPosCounter = startPos;
        int yPosCounter = 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Shield shield = new Shield(xPosCounter,yPosCounter,1,1);
                shields.add(shield);
                xPosCounter++;
            }
            xPosCounter = startPos;
            yPosCounter++;
        }
        return shields;
    }
    
    public void setBuildings(ArrayList<GameObject> buildings){
        this.buildings = buildings;
        int extraBullets = 0;
        for(GameObject building : buildings){
            if(building instanceof BulletFactory){
              extraBullets++; 
            }
        }
        
        //+1 for the initial limit of 1
        this.bulletLimit = extraBullets + 1;
    }
    
    public boolean isAlive(){
        return this.isAlive;
    }
    
    public void die(){
        this.isAlive = false;
        this.lives--;
    }
    
    public void respawn(){
        this.xPosition = 8;
        this.yPosition = 2;
        this.isAlive = true;
    }
    
    public int getLives(){
        return this.lives;
    }
    
    public void increaseKillCount(){
        this.kills++;
    }
    
    public int getKills(){
        return this.kills;
    }

    public void removeAllObjectsInBlock(int xPosition) {
        ArrayList<ArrayList<Alien>> allAliens = alienController.getAllAliens();
        ArrayList<GameObject> playerBullets = bulletController.getPlayerbullets();
        ArrayList<GameObject> alienBullets = bulletController.getAlienbullets();
        
        int xMax = xPosition +3;
        int y = 3;
        int yMax = y +3;
        for (int i = xPosition; i < xMax; i++) {
            for (int j = y; j < yMax; j++) {
                for(ArrayList<Alien> aliens : allAliens){
                    for(Alien alien : aliens){
                        if(alien.getyPosition() == j 
                                && (alien.getxPosition() == i))
                        {
                          aliens.remove(alien);
                          break;
                        }
                    } 
                }
                
                for (int k = 0; k < playerBullets.size();k++) {
                    if(playerBullets.get(k).getxPosition() == i
                        && playerBullets.get(k).getyPosition() == j){
                        playerBullets.remove(k);
                        break;
                    }
                }
                
                for (int k = 0; k < alienBullets.size();k++) {
                    if(alienBullets.get(k).getxPosition() == i
                        && alienBullets.get(k).getyPosition() == j){
                        alienBullets.remove(k);
                        break;
                    }
                }
            }
        }
        alienController.setAliens(allAliens);
        bulletController.setAlienbullets(alienBullets);
        bulletController.setPlayerbullets(playerBullets);
    }
    
    @Override
    public GameObject clone(){
        Player player = new Player();
        
        player.shields = ArrayListCopy.copyArray(shields);
        player.buildings = ArrayListCopy.copyArray(buildings);
        
        //start location
        player.xPosition = this.xPosition;
        player.yPosition = this.yPosition;
        player.bulletLimit = this.bulletLimit;
        player.lives = this.lives;
        player.isAlive = this.isAlive;
        
        player.kills = this.kills;
        return player;
    }

    public BulletController getBulletController() {
        return bulletController;
    }

    public void setBulletController(BulletController bulletController) {
        this.bulletController = bulletController;
    }

    public AlienController getAlienController() {
        return alienController;
    }

    public void setAlienController(AlienController alienController) {
        this.alienController = alienController;
    }

    public void setBulletLimit(int bulletLimit) {
        this.bulletLimit = bulletLimit;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    public void setySize(int ySize) {
        this.ySize = ySize;
    }

    public ArrayList<GameObject> getBuildings() {
        return buildings;
    }

    public ArrayList<GameObject> getShields() {
        return shields;
    }
    
}
