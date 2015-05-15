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
    private static BulletController instance = null;
    private ArrayList<AlienBullet> alienBulletList;
    private ArrayList<PlayerBullet> playerBulletList;
    
    
    private BulletController(){
        instance = this;
        alienBulletList = new ArrayList();
        playerBulletList = new ArrayList();
    }
    
    public static BulletController getInstance(){
        if(instance == null){
            instance = new BulletController();
        }
        
        return instance;
    }
    
    public void update(){
        removeOutOfBoundsBullets();
        updateAlienBulletPosition();
        updatePlayerBulletPosition();
        alienBulletColissionDetection();
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
        
        ArrayList<Building> buildings = PlayerController.getInstance().getBuildings();
        ArrayList<Shield> shields = PlayerController.getInstance().getAllShields();
        int playerPos = PlayerController.getInstance().getPlayerPosition();
        
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
            
            if(i < alienBulletList.size() && PlayerController.getInstance().isPlayerAlive()){
                 if(alienBulletList.get(i).getyPosition() == 2 
                        && (playerPos == alienBulletList.get(i).getxPosition() 
                        || playerPos+1 == alienBulletList.get(i).getxPosition()
                        || playerPos+2 == alienBulletList.get(i).getxPosition()))
                    {
                        alienBulletList.remove(alienBulletList.get(i));
                        PlayerController.getInstance().killPlayer();
                        increaseCounter = false;
                    }
            }
            
            if(increaseCounter){
                i++;
            }
        }
        PlayerController.getInstance().setBuildings(buildings);
        PlayerController.getInstance().setShields(shields);
    }
    
    public void playerBulletColissionDetection(){
        
        ArrayList<ArrayList<Alien>> allAliens = AlienController.getInstance().getAllAliens();
        ArrayList<Shield> shields = PlayerController.getInstance().getAllShields();
        
        for (int i = 0; i < playerBulletList.size();) {
            boolean increaseCounter = true;
            
            for(ArrayList<Alien> aliens : allAliens){
                for(Alien alien : aliens){
                    if(alien.getyPosition() == playerBulletList.get(i).getyPosition() 
                            && (alien.getxPosition() == playerBulletList.get(i).getxPosition()))
                    {
                      aliens.remove(alien);
                      playerBulletList.remove(playerBulletList.get(i));
                      PlayerController.getInstance().increaseKillCount();
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
        AlienController.getInstance().setAliens(allAliens);
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
    
    

}
