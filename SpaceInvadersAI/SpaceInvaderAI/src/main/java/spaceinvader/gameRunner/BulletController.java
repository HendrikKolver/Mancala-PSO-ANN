package spaceinvader.gameRunner;

import java.util.ArrayList;
import spaceinvader.entities.Alien;
import spaceinvader.entities.AlienBullet;
import spaceinvader.entities.Building;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.entities.Shield;

/**
 *
 * @author Hendrik Kolver
 */
public class BulletController {
    private ArrayList<AlienBullet> alienBulletList;
    private ArrayList<PlayerBullet> playerBulletList;
    private PlayerController playerController;
    private AlienController alienController; 
    
    public BulletController(){
        alienBulletList = new ArrayList();
        playerBulletList = new ArrayList();
    }
    
    public void update(){
        removeOutOfBoundsBullets();
        updateAlienBulletPosition();
        alienBulletColissionDetection();
        updatePlayerBulletPosition();
        playerBulletColissionDetection();
    }
    
    public void updateAlienBulletPosition(){
        for(AlienBullet alienBullet : alienBulletList){
            alienBullet.updatePosition("DOWN");
        }
    }
    
    public void updatePlayerBulletPosition(){
       for(PlayerBullet playerBullet : playerBulletList){
            playerBullet.updatePosition("UP");
        } 
    }
    
    public void addAlienBullet(AlienBullet alienBullet){
        alienBulletList.add(alienBullet);
    }
    
    public void addPlayerBullet(PlayerBullet playerBullet){
        playerBulletList.add(playerBullet);
    }
    
    private void removeOutOfBoundsBullets(){
            for (int i = 0; i < alienBulletList.size();) {
                if(alienBulletList.get(i).getyPosition() == 1){
                    alienBulletList.remove(i);
                }else{
                    i++;
                }
            }
            
             for (int i = 0; i < playerBulletList.size();) {
                if(playerBulletList.get(i).getyPosition() == 11){
                    playerBulletList.remove(i);
                }else{
                    i++;
                }
            }
    }
    
    public void alienBulletColissionDetection(){
        
        ArrayList<Building> buildings = playerController.getBuildings();
        ArrayList<Shield> shields = playerController.getAllShields();
        int playerPos = playerController.getPlayerPosition();
        
        for (int i = 0; i < alienBulletList.size();) {
            boolean increaseCounter = true;
            
            for(Building building : buildings){
                if(building.getyPosition() == alienBulletList.get(i).getyPosition() 
                        && (building.getxPosition() == alienBulletList.get(i).getxPosition() 
                        || building.getxPosition()+1 == alienBulletList.get(i).getxPosition()
                        || building.getxPosition()+2 == alienBulletList.get(i).getxPosition()))
                {
                  buildings.remove(building);
                  alienBulletList.remove(alienBulletList.get(i));
                  break;
                }
            }
            
            if(i < alienBulletList.size()){
                for(Shield shield : shields){
                    if(shield.getyPosition() == alienBulletList.get(i).getyPosition() 
                            && (shield.getxPosition() == alienBulletList.get(i).getxPosition()))
                    {
                      shields.remove(shield);
                      alienBulletList.remove(alienBulletList.get(i));
                      break;
                    }
                } 
            }
            
            if(i < alienBulletList.size()){
                for(PlayerBullet playerBullet : playerBulletList){
                    if(playerBullet.getxPosition() == alienBulletList.get(i).getxPosition()
                        && playerBullet.getyPosition() == alienBulletList.get(i).getyPosition())
                    {
                        alienBulletList.remove(alienBulletList.get(i));
                        playerBulletList.remove(playerBullet);
                        increaseCounter = false;
                        break;
                    }
                } 
            }
            
            if(i < alienBulletList.size() && playerController.isPlayerAlive()){
                 if(alienBulletList.get(i).getyPosition() == 2 
                        && (playerPos == alienBulletList.get(i).getxPosition() 
                        || playerPos+1 == alienBulletList.get(i).getxPosition()
                        || playerPos+2 == alienBulletList.get(i).getxPosition()))
                    {
                        alienBulletList.remove(alienBulletList.get(i));
                        playerController.killPlayer();
                        increaseCounter = false;
                    }
            }
            
            if(increaseCounter){
                i++;
            }
        }
        playerController.setBuildings(buildings);
        playerController.setShields(shields);
    }
    
    public void playerBulletColissionDetection(){
        
        ArrayList<ArrayList<Alien>> allAliens = alienController.getAllAliens();
        ArrayList<Shield> shields = playerController.getAllShields();
        
        for (int i = 0; i < playerBulletList.size();) {
            boolean increaseCounter = true;
            
            for(ArrayList<Alien> aliens : allAliens){
                for(Alien alien : aliens){
                    if(alien.getyPosition() == playerBulletList.get(i).getyPosition() 
                            && (alien.getxPosition() == playerBulletList.get(i).getxPosition()))
                    {
                      aliens.remove(alien);
                      playerBulletList.remove(playerBulletList.get(i));
                      playerController.increaseKillCount();
                      increaseCounter = false;
                      break;
                    }
                } 
                if(!increaseCounter){
                    break;
                }
                    
            }
            
            //This is incase a player fires a bullet and then builds a shield on top of that bullet.
            //This is allowed since there may be enemy bullets in that area that the player wants to mitigate as well
            if(i < playerBulletList.size()){
                for(Shield shield : shields){
                    if(shield.getyPosition() == playerBulletList.get(i).getyPosition() 
                            && (shield.getxPosition() == playerBulletList.get(i).getxPosition()))
                    {
                      shields.remove(shield);
                      playerBulletList.remove(playerBulletList.get(i));
                      break;
                    }
                } 
            }

            if(increaseCounter){
                i++;
            }
        }
        alienController.setAliens(allAliens);
    }
    
    public void printAllBullets(){
        if(!alienBulletList.isEmpty() || !playerBulletList.isEmpty()){
            
           for(AlienBullet alienBullet : alienBulletList){
                System.out.println("Alien Bullet: ["+alienBullet.getxPosition()+"]["+alienBullet.getyPosition()+"],");
            }

            for(PlayerBullet playerBullet : playerBulletList){
                System.out.println("Player Bullet: ["+playerBullet.getxPosition()+"]["+playerBullet.getyPosition()+"],");
            }
            System.out.println("\n"); 
        }
    }
    
    public int getPlayerBulletCount(){
        return playerBulletList.size();
    }
    
    public ArrayList<AlienBullet> getAlienbullets(){
        return alienBulletList;
    }
    
    public ArrayList<PlayerBullet> getPlayerbullets(){
        return playerBulletList;
    }
    
    public void setAlienbullets(ArrayList<AlienBullet> alienBullets){
        this.alienBulletList = alienBullets;
    }
    
    public void setPlayerbullets(ArrayList<PlayerBullet> playerBullets){
        this.playerBulletList = playerBullets;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    public AlienController getAlienController() {
        return alienController;
    }

    public void setAlienController(AlienController alienController) {
        this.alienController = alienController;
    }
}
