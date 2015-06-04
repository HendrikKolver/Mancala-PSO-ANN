package spaceinvader.gameRunner;

import java.util.ArrayList;
import spaceinvader.entities.Alien;
import spaceinvader.entities.AlienBullet;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.Player;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.utilities.ArrayListCopy;

/**
 *
 * @author Hendrik Kolver
 */
public class BulletController {
    private ArrayList<GameObject> alienBulletList;
    private ArrayList<GameObject> playerBulletList;
    private ArrayList<GameObject> enemyBulletList;
    private PlayerController playerController;
    private AlienController alienController; 
    
    public BulletController(){
        alienBulletList = new ArrayList();
        playerBulletList = new ArrayList();
        enemyBulletList = new ArrayList();
    }
    
    public void update(){
        removeOutOfBoundsBullets();
        updateAlienBulletPosition();
        alienBulletColissionDetection();
        updatePlayerBulletPosition();
        playerBulletColissionDetection();
        updateEnemyBulletPosition();
        enemyBulletColissionDetection();
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
    
    public void updateEnemyBulletPosition(){
       for(GameObject playerBullet : enemyBulletList){
            playerBullet.updatePosition("DOWN");
        } 
    }
    
    public void addAlienBullet(AlienBullet alienBullet){
        alienBulletList.add(alienBullet);
    }
    
    public void addPlayerBullet(PlayerBullet playerBullet){
        playerBulletList.add(playerBullet);
    }
    
    public void addEnemyBullet(GameObject enemyBullet){
        enemyBulletList.add(enemyBullet);
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
                if(playerBulletList.get(i).getyPosition() > 22){
                    playerBulletList.remove(i);
                }else{
                    i++;
                }
            }
             
            for (int i = 0; i < enemyBulletList.size();) {
                if(enemyBulletList.get(i).getyPosition() == 1){
                    enemyBulletList.remove(i);
                }else{
                    i++;
                }
            }
             
             
    }
    
    private boolean bulletColissionDetection(GameObject bullet, GameObject objectToCollide, int size){
        if(size == 3){
            return (objectToCollide.getyPosition() == bullet.getyPosition() 
                        && (objectToCollide.getxPosition() == bullet.getxPosition() 
                        || objectToCollide.getxPosition()+1 == bullet.getxPosition()
                        || objectToCollide.getxPosition()+2 == bullet.getxPosition()));
        }else{
            return (objectToCollide.getyPosition() == bullet.getyPosition() 
                            && (objectToCollide.getxPosition() == bullet.getxPosition()));
        }
    }
    
    public void alienBulletColissionDetection(){
        
        ArrayList<GameObject> buildings = playerController.getBuildings();
        ArrayList<GameObject> shields = playerController.getAllShields();
        Player player = playerController.getPlayer();
        
        for (int i = 0; i < alienBulletList.size();) {
            boolean increaseCounter = true;
            
            for(GameObject building : buildings){
                if(bulletColissionDetection(alienBulletList.get(i),building,3))
                {
                  buildings.remove(building);
                  alienBulletList.remove(alienBulletList.get(i));
                  break;
                }
            }
            
            if(i < alienBulletList.size()){
                for(GameObject shield : shields){
                    if(bulletColissionDetection(alienBulletList.get(i),shield,1))
                    {
                      shields.remove(shield);
                      alienBulletList.remove(alienBulletList.get(i));
                      break;
                    }
                } 
            }
            
            if(i < alienBulletList.size()){
                for(GameObject playerBullet : playerBulletList){
                    if(bulletColissionDetection(alienBulletList.get(i),playerBullet,1)
                            && playerBullet.getPlayer() == 1)
                    {
                        alienBulletList.remove(alienBulletList.get(i));
                        playerBulletList.remove(playerBullet);
                        increaseCounter = false;
                        break;
                    }
                } 
            }
            
            if(i < alienBulletList.size() && playerController.isPlayerAlive()){
                 if(bulletColissionDetection(alienBulletList.get(i),player,3))
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
        ArrayList<GameObject> shields = playerController.getAllShields();
        
        for (int i = 0; i < playerBulletList.size();) {
            boolean increaseCounter = true;
            
            if(playerBulletList.get(i).getPlayer() == 1){
                for(ArrayList<Alien> aliens : allAliens){
                    for(Alien alien : aliens){
                        if(bulletColissionDetection(playerBulletList.get(i),alien,1))
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
                        if(bulletColissionDetection(playerBulletList.get(i),shield,1))
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
            }else{
                i++;
            }
        }
        alienController.setAliens(allAliens);
    }
    
    public void enemyBulletColissionDetection(){
        
        ArrayList<ArrayList<Alien>> allAliens = alienController.getAllAliens();
        ArrayList<GameObject> shields = playerController.getAllShields();
        ArrayList<GameObject> buildings = playerController.getBuildings();
        Player player = playerController.getPlayer();

        
        for (int i = 0; i < enemyBulletList.size();) {
            boolean increaseCounter = true;
            
            for(ArrayList<Alien> aliens : allAliens){
                for(Alien alien : aliens){
                    if(bulletColissionDetection(enemyBulletList.get(i),alien,1))
                    {
                      aliens.remove(alien);
                      enemyBulletList.remove(enemyBulletList.get(i));
                      increaseCounter = false;
                      break;
                    }
                } 
                if(!increaseCounter){
                    break;
                }
                    
            }
            if(i < enemyBulletList.size()){
                for(GameObject building : buildings){
                    if(bulletColissionDetection(enemyBulletList.get(i),building,3))
                    {
                      buildings.remove(building);
                      enemyBulletList.remove(enemyBulletList.get(i));
                      break;
                    }
                }
            }
            
            if(i < enemyBulletList.size()){
                for(GameObject shield : shields){
                    if(bulletColissionDetection(enemyBulletList.get(i),shield,1))
                    {
                      shields.remove(shield);
                      enemyBulletList.remove(enemyBulletList.get(i));
                      break;
                    }
                } 
            }
            
            if(i < enemyBulletList.size()){
                for(GameObject playerBullet : enemyBulletList){
                    if(bulletColissionDetection(enemyBulletList.get(i),playerBullet,1) && enemyBulletList.get(i).getObjectID() != playerBullet.getObjectID())
                    {
                        enemyBulletList.remove(enemyBulletList.get(i));
                        playerBulletList.remove(playerBullet);
                        increaseCounter = false;
                        break;
                    }
                } 
            }
            
            if(i < enemyBulletList.size() && playerController.isPlayerAlive()){
                 if(bulletColissionDetection(enemyBulletList.get(i),player,1))
                    {
                        enemyBulletList.remove(enemyBulletList.get(i));
                        playerController.killPlayer();
                        increaseCounter = false;
                    }
            }

            if(increaseCounter){
                i++;
            }
        }
        alienController.setAliens(allAliens);
        playerController.setBuildings(buildings);
        playerController.setShields(shields);
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
        bulletControllerCopy.setEnemyBulletList(ArrayListCopy.copyArray(enemyBulletList));
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

    public void setEnemyBulletList(ArrayList<GameObject> enemyBulletList) {
        this.enemyBulletList = enemyBulletList;
    }

    public ArrayList<GameObject> getEnemyBulletList() {
        return enemyBulletList;
    }
    
    
    
    
}
