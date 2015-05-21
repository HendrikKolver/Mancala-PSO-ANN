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
    private ArrayList<Building> buildings;
    private ArrayList<Shield> shields;
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
        buildings = new ArrayList();
        this.isAlive = true;
        this.kills = 0;
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
        
        if(bulletController.getPlayerBulletCount()<bulletLimit && !isShieldBlockingPlayerBullet()){
            possibleMoves.add("Shoot");
        }
        
        if(canLifeBeUsed() && !isBuildingBehindPlayer()){
            possibleMoves.add("BuildAlienFactory");
            possibleMoves.add("BuildMissileController");
        }
        
        if(canLifeBeUsed() && !isShieldInfrontOfPlayer()){
            possibleMoves.add("BuildShield");
        }
        
        
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
    
    private boolean isShieldBlockingPlayerBullet(){
        for(Shield shield : shields){
            if(shield.getxPosition() == this.getxPosition()+1 ){
                return true;
            }
        }
        return false;
    }
    
    private boolean isShieldInfrontOfPlayer(){
         for(Shield shield : shields){

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
    
    public ArrayList<Building> getAllBuildings(){
        return this.buildings;
    }
    
    public void setShields(ArrayList<Shield> shields){
        this.shields = shields;
    }
    
    public ArrayList<Shield> getAllShields(){
        return shields;
    }
    
    public ArrayList<Shield> createShieldBlock(int startPos){
        ArrayList<Shield> shields = new ArrayList();
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
    
    public void setBuildings(ArrayList<Building> buildings){
        this.buildings = buildings;
        int extraBullets = 0;
        for(Building building : buildings){
            if(building.getRepresentation().equals("B")){
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
        ArrayList<PlayerBullet> playerBullets = bulletController.getPlayerbullets();
        ArrayList<AlienBullet> alienBullets = bulletController.getAlienbullets();
        
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
    public GameObject getCopy(){
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
    
    
}
