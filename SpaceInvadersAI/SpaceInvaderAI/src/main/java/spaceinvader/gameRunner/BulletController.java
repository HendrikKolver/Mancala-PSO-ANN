package spaceinvader.gameRunner;

import java.util.ArrayList;
import java.util.Iterator;
import spaceinvader.entities.Alien;
import spaceinvader.entities.AlienBullet;
import spaceinvader.entities.Building;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.entities.Shield;
import spaceinvader.utilities.ArrayListCopy;

/**
 *
 * @author Hendrik Kolver
 */
public class BulletController {
    private ArrayList<GameObject> alienBulletList;
    private ArrayList<GameObject> playerBulletList;
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
        for(GameObject alienBullet : alienBulletList){
            alienBullet.updatePosition("DOWN");
        }
    }
    
    public void updatePlayerBulletPosition(){
       for(GameObject playerBullet : playerBulletList){
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
        
        ArrayList<GameObject> buildings = playerController.getBuildings();
        ArrayList<GameObject> shields = playerController.getAllShields();
        int playerPos = playerController.getPlayerPosition();
        
        for (int i = 0; i < alienBulletList.size();) {
            boolean increaseCounter = true;
            
            for(GameObject building : buildings){
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
                for(GameObject shield : shields){
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
                for(GameObject playerBullet : playerBulletList){
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
            
            //TODO alien bullets should hit each other
            
            if(increaseCounter){
                i++;
            }
        }
        playerController.setBuildings(buildings);
        playerController.setShields(shields);
    }
    
    public void playerBulletColissionDetection(){
        
        ArrayList<ArrayList<Alien>> allAliens = alienController.getAllAliens();
        ArrayList<GameObject> shields = playerController.getAllShields();
        
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
                for(GameObject shield : shields){
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
            
           for(GameObject alienBullet : alienBulletList){
                System.out.println("Alien Bullet: ["+alienBullet.getxPosition()+"]["+alienBullet.getyPosition()+"],");
            }

            for(GameObject playerBullet : playerBulletList){
                System.out.println("Player Bullet: ["+playerBullet.getxPosition()+"]["+playerBullet.getyPosition()+"],");
            }
            System.out.println("\n"); 
        }
    }
    
    public BulletController getCopy(){
        BulletController bulletControllerCopy = new BulletController();
        bulletControllerCopy.setAlienbullets(ArrayListCopy.copyArray(alienBulletList));
        bulletControllerCopy.setPlayerbullets(ArrayListCopy.copyArray(playerBulletList));
        return bulletControllerCopy;
    }
    
    public int getPlayerBulletCount(){
        return playerBulletList.size();
    }
    
    public ArrayList<GameObject> getAlienbullets(){
        return alienBulletList;
    }
    
    public ArrayList<GameObject> getPlayerbullets(){
        return playerBulletList;
    }
    
    public void setAlienbullets(ArrayList<GameObject> alienBullets){
        this.alienBulletList = alienBullets;
    }
    
    public void setPlayerbullets(ArrayList<GameObject> playerBullets){
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

    public ArrayList<GameObject> getAlienBulletList() {
        return alienBulletList;
    }

    public ArrayList<GameObject> getPlayerBulletList() {
        return playerBulletList;
    }
}
