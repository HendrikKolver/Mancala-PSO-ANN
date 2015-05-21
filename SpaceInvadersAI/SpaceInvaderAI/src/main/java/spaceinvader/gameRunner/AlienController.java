package spaceinvader.gameRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import spaceinvader.entities.Alien;
import spaceinvader.entities.AlienBullet;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.entities.Shield;
import spaceinvader.utilities.RandomGenerator;

/**
 *
 * @author Hendrik Kolver
 */
public class AlienController {
    ArrayList <ArrayList<Alien>> alienRow;
    ArrayList <Alien> latestRowAliens;
    int fakeOpponentAlienFactory = 0;
    private boolean gameOver;
    private int waveSize;
    private PlayerController playerController;
    private BulletController bulletController;
    private AlienController alienController;
    
    public AlienController(){
        waveSize =3;
        alienRow = new ArrayList();
        latestRowAliens = new ArrayList();
        alienRow.add(latestRowAliens);
        gameOver = false;      
    }
  
    public void update(int roundNumber){
        if(fakeOpponentAlienFactory == 0){
            int probability = RandomGenerator.randInt(1, 100);
            if(probability <=5){
                increaseWaveSize();
                fakeOpponentAlienFactory++;
            }
        }
        
        if(fakeOpponentAlienFactory == 1){
            int probability = RandomGenerator.randInt(1, 1000);
            if(probability <=5){
                increaseWaveSize();
                fakeOpponentAlienFactory++;
            }
        }
        
        removeEmptyRows();
        updateAlienPosition();
        checkForShieldColission();
        checkToFireBullet(roundNumber);
        removeEmptyRows();
        checkToAddRow();
    }
    
    public void checkToFireBullet(int roundNumber){
        removeEmptyRows();
        if(roundNumber % 6 == 0){
            ArrayList<Alien> firstRow = null;
            ArrayList<Alien> secondRow = null;
            
            if(alienRow.size() >0){
                firstRow = alienRow.get(0);
            }
            if(alienRow.size() >1){
                secondRow = alienRow.get(1);
            }
            
            if(firstRow != null){
                int probability = RandomGenerator.randInt(1, 1000);
                if(probability <= 333){
                    //TODO Alien infront of player should shoot
                }else{
                    int rowChoice = 0;
                    if(secondRow != null){
                        rowChoice = RandomGenerator.randInt(0, 1);
                    }
                    
                    if(rowChoice == 0){
                        int alienToShoot = RandomGenerator.randInt(0, firstRow.size()-1);
                        Alien firstRowAlienToShoot = firstRow.get(alienToShoot);
                        firstRowAlienToShoot.fireBullet();
                    }else{
                        int alienToShoot = RandomGenerator.randInt(0, secondRow.size()-1);
                        Alien secondRowAlienToShoot = secondRow.get(alienToShoot);
                        secondRowAlienToShoot.fireBullet();
                    }
                    
                }
            }
        }
    }
    
    public void checkToAddRow(){
        if(alienRow.isEmpty() || alienRow.get(alienRow.size()-1).get(0).getyPosition() <10){
            addNewRow();
            addAlien();
        }
    }
    
    public void updateAlienPosition(){
        boolean moveDown = false;
         for (ArrayList<Alien> rowAliens : alienRow) { 
            if(rowAliens.get(0).getMoveDirection().equals("LEFT")){
                if(rowAliens.get(0).getxPosition() == 1){
                   moveDown = true;
                   break;
                }
            }else{
                if(rowAliens.get(rowAliens.size()-1).getxPosition() == 17){
                   moveDown = true;
                   break;
                }
            }
        }
         
         for (ArrayList<Alien> rowAliens : alienRow) { 
            for (Alien alien : rowAliens) {
                if(moveDown){
                    if(alien.getyPosition()==1){
                        gameOver = true;
                    }
                    alien.updatePosition("DOWN");
                    alien.invertMoveDirection();
                }else{
                    alien.updatePosition(alien.getMoveDirection());
                }
             } 
        }
    }
    
    public void addAlien(){
        int xStartLocation = 17;
        for (int i = 0; i < waveSize; i++) {
            Alien alien = new Alien(xStartLocation,11);
            alien.setBulletController(bulletController);
            latestRowAliens.add(alien);
            xStartLocation -= 3;
        }
        Collections.reverse(latestRowAliens);
    }
    
    private void addNewRow(){
        latestRowAliens = new ArrayList();
        alienRow.add(latestRowAliens);
    }
    
    private void removeEmptyRows(){ 
        Iterator<ArrayList<Alien>> i = alienRow.iterator();
            while (i.hasNext()) {
                ArrayList<Alien> aliens = i.next();
                 if(aliens.isEmpty()){
                       i.remove(); 
                }
            }
    }
    
    public void increaseWaveSize(){
        this.waveSize++;
    }
    
    public void printAllAliens(){
        
        for (ArrayList<Alien> rowAliens : alienRow) { 
            String alienString = "";
            for (Alien alien : rowAliens) {   
                alienString += "["+alien.getxPosition()+"][" + alien.getyPosition()+"],";
            }
            System.out.println(alienString);  
        }
        System.out.println("\n");
    }
    
    public ArrayList<ArrayList<Alien>> getAllAliens(){
        return alienRow;
    }
    
    public void setAliens(ArrayList<ArrayList<Alien>> allAliens){
        this.alienRow = allAliens;
    }

    private void checkForShieldColission() {
         ArrayList<Shield> shields = playerController.getAllShields();

        for(ArrayList<Alien> aliens : alienRow){
            Iterator<Alien> i = aliens.iterator();
            while (i.hasNext()) {
               Alien alien = i.next(); 
                   
                for(Shield shield : shields){
                    if(shield.getyPosition() == alien.getyPosition() 
                        && (shield.getxPosition() == alien.getxPosition()))
                    {
                        int xPos = alien.getxPosition();
                        i.remove();
                        removeAllObjectsInBlock(alien.getxPosition()-1,alien.getyPosition()-1);
                        break;
                    }
                } 
            }     
        } 
    }
    
    //Similiar to player version but removes shields as well
    private void removeAllObjectsInBlock(int xPosition, int yPosition) {
        ArrayList<PlayerBullet> playerBullets = bulletController.getPlayerbullets();
        ArrayList<AlienBullet> alienBullets = bulletController.getAlienbullets();
        ArrayList<Shield> shields = playerController.getAllShields();
        
        int xMax = xPosition +3;
        int y = yPosition;
        int yMax = y +3;
        for (int i = xPosition; i < xMax; i++) {
            for (int j = y; j < yMax; j++) {
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
                
                for(Shield shield : shields){
                    if(shield.getyPosition() == j 
                            && (shield.getxPosition() == i))
                    {
                      shields.remove(shield);
                      break;
                    }
                }
            }
        }
        bulletController.setAlienbullets(alienBullets);
        bulletController.setPlayerbullets(playerBullets);
        playerController.setShields(shields); 
    }
    
    
    public AlienController getCopy(){
        return this;
    }
    
    public String dumpGameState(){
        return null;
    }
    
    public void importGameState(String gameState){
        //TODO import from game state
        JSONObject jsonObj = new JSONObject(gameState);
    }
    
    public boolean isGameOver(){
        return gameOver;
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
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
