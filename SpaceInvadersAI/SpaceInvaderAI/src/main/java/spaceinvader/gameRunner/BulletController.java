package spaceinvader.gameRunner;

import java.util.ArrayList;
import spaceinvader.entities.AlienBullet;
import spaceinvader.entities.PlayerBullet;

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
                if(playerBulletList.get(i).getyPosition() == 1){
                    playerBulletList.remove(i);
                }else{
                    i++;
                }
            }
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

}
