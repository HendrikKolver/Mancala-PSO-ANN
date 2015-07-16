package spaceinvader.gameRunner;

import java.util.ArrayList;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.Player;

/**
 *
 * @author Hendrik Kolver
 */
public class PlayerController {
        private Player player;
        private int respawnCounter;
        private BulletController bulletController;
        private AlienController alienController;
    
    
    public PlayerController(){
        player = new Player();
        ArrayList<GameObject> shieldBlock1 = player.createShieldBlock(2);
        ArrayList<GameObject> shieldBlock2 = player.createShieldBlock(14);
        shieldBlock1.addAll(shieldBlock2);
        player.setShields(shieldBlock1);
        respawnCounter = 0;
    }
    
    public PlayerController(boolean generatePlayer){
        //This constructor is used to reduce perfomance load by not unnecessarily creating player and shield blocks
    }

    
    public void update(String move){
        if(!player.isAlive() && respawnCounter < 0){
            player.respawn();
        }else if(!player.isAlive()){
            respawnCounter--;
            return;
        }

        player.makeMove(move);
    }
    
    public ArrayList<String> getPossibleMoves(){
       return player.getPossibleMoves(); 
    }
    
    public void printPlayerPosition(){
        System.out.println("Player Pos: "+ player.getxPosition());
    }
    
    public int getPlayerPosition(){
        return player.getxPosition();
    }
    
    public ArrayList<GameObject> getBuildings(){
        return player.getAllBuildings();
    }
    
    public void printBuildingPositions(){
        for(GameObject building : player.getAllBuildings()){
            System.out.println(building.stringContent());
        }
    }
    
    public PlayerController clone(){
        PlayerController playerControllerCopy = new PlayerController(false);
        playerControllerCopy.setPlayer((Player) this.player.clone());
        playerControllerCopy.setRespawnCounter(this.respawnCounter);
        return playerControllerCopy;
    }
    
    public ArrayList<GameObject> getAllShields(){
        return player.getAllShields();
    }
    
    public void setBuildings(ArrayList<GameObject> buildings){
        player.setBuildings(buildings);
    }
    
    public void setShields(ArrayList<GameObject> shields){
        player.setShields(shields);
    }
    
    public boolean isPlayerAlive(){
        return player.isAlive();
    }
    
    public void killPlayer(){
        respawnCounter =2;
        player.die();
    }
    
    public boolean isGameOver(){
        return (player.getLives()<0);
    }
    
    public void increaseKillCount(){
        player.increaseKillCount();
    }
    
    public int getKillCount(){
        return player.getKills();
    }
    
    public void removeAllObjectsInBlock(int xPos){
        player.removeAllObjectsInBlock(xPos);
    }

    public BulletController getBulletController() {
        return bulletController;
    }

    public void setBulletController(BulletController bulletController) {
        this.bulletController = bulletController;
        player.setBulletController(bulletController);
    }

    public AlienController getAlienController() {
        return alienController;
    }

    public void setAlienController(AlienController alienController) {
        this.alienController = alienController;
        player.setAlienController(alienController);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setRespawnCounter(int respawnCounter) {
        this.respawnCounter = respawnCounter;
    }
    
    public int getLives(){
        return this.player.getLives();
    }

    public Player getPlayer() {
        return player;
    }
    
    public boolean getDeathOccured(){
        return player.isDeathOccured();
    }
     
}

