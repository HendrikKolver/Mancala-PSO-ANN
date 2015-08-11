package spaceinvader.gameRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.json.JSONObject;
import spaceinvader.entities.Alien;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.Player;
import spaceinvader.utilities.ArrayListCopy;
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
    public long playerNumber;
    public long moveDownRound;
    
    public AlienController(){
        waveSize =3;
        playerNumber = 1;
        alienRow = new ArrayList();
        latestRowAliens = new ArrayList();
        alienRow.add(latestRowAliens);
        gameOver = false; 
        moveDownRound = -1;
    }
  
    public void update(int roundNumber){
        if(roundNumber == 40){
            this.increaseWaveSize();
        }
        
        removeEmptyRows();
        updateAlienPosition(roundNumber);
        checkForShieldColission();
        checkForPlayerColission();
        checkToFireBullet(roundNumber);
        removeEmptyRows();
        checkToAddRow(roundNumber);
    }
    
    public void updateForBulletCheck(int roundNumber){
        if(roundNumber == 40){
            this.increaseWaveSize();
        }
        
        removeEmptyRows();
        updateAlienPosition(roundNumber);
        checkForShieldColission();
//        checkForPlayerColission();
        checkToFireBullet(roundNumber);
        removeEmptyRows();
        checkToAddRow(roundNumber);
    }
    
    public void checkToFireBullet(int roundNumber){
        removeEmptyRows();
        if((roundNumber+1) % 6 == 0 && roundNumber != 0){
            ArrayList<Alien> firstRow = null;
            ArrayList<Alien> secondRow = null;
            if(alienRow.size() >0){
                firstRow = alienRow.get(0);
            }
            if(alienRow.size() >1){
                secondRow = alienRow.get(1);
            }
            
            if(firstRow != null){
//                int probability = RandomGenerator.randInt(1, 1000);//Remove to remove randomness
//                if(probability <= 333){//Remove to remove randomness
                    if(!firstRow.isEmpty()){
                        Alien alienToShoot = getClosestAlienToPlayer(firstRow);
                        alienToShoot.fireBullet();
//                    } //Remove to remove randomness
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
                        secondRowAlienToShoot = getAlienInfront(firstRow, secondRowAlienToShoot);
                        secondRowAlienToShoot.fireBullet();
                    }
                    
                }
            }
        }
    }
    
    private Alien getClosestAlienToPlayer(ArrayList<Alien> aliens){
        Alien tmpClosest = aliens.get(0);
        int closestDistance = 1000;
        
        for(Alien alien : aliens){
            int tmpDistance = alien.getxPosition() - playerController.getPlayerPosition();
            
            if(tmpDistance <0){
                tmpDistance *= (-1);
            }
            
            if(tmpDistance < closestDistance){
                closestDistance = tmpDistance;
                tmpClosest = alien;
            }
        }
        return tmpClosest;
    }
    
    private Alien getAlienInfront(ArrayList<Alien> aliens, Alien alien){
        for(Alien firstRowAlien : aliens){
            if(firstRowAlien.getxPosition() == alien.getxPosition()){
                return firstRowAlien;
            }
        }
        return alien;
    }
    
    
    
    public void checkToAddRow(int roundNumber){
        if(this.getAlienCount() == 0 && roundNumber >5){
            addNewRow();
            addAlien();
        }else if(moveDownRound+1 == roundNumber){
           if(alienRow.isEmpty() || alienRow.get(alienRow.size()-1).get(0).getyPosition() <10){
                addNewRow();
                 addAlien(); 
             } 
        }
        
    }
    
    public void updateAlienPosition(int roundNumber){
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
                    moveDownRound = roundNumber;
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
        if(playerNumber == 2){
            int xStartLocation = 2;
            for (int i = 0; i < waveSize; i++) {
                Alien alien = new Alien(xStartLocation,11);
                alien.setBulletController(bulletController);
                latestRowAliens.add(alien);
                xStartLocation += 3;
                
            }
            Collections.reverse(latestRowAliens);
        }else{
            int xStartLocation = 16;
            for (int i = 0; i < waveSize; i++) {
                Alien alien = new Alien(xStartLocation,11);
                alien.setBulletController(bulletController);
                latestRowAliens.add(alien);
                xStartLocation -= 3;
            }
            Collections.reverse(latestRowAliens);
        }
        
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
    
    public ArrayList<ArrayList<Alien>> getAllAliens(){
        return alienRow;
    }
    
    public void setAliens(ArrayList<ArrayList<Alien>> allAliens){
        this.alienRow = allAliens;
    }

    private void checkForShieldColission() {
         ArrayList<GameObject> shields = playerController.getAllShields();

        for(ArrayList<Alien> aliens : alienRow){
            Iterator<Alien> i = aliens.iterator();
            while (i.hasNext()) {
               Alien alien = i.next(); 
                   
                for(GameObject shield : shields){
                    if(alien.getyPosition() > 5){
                        //No shields can be build here thus there is no need to check
                        break;
                    }
                    
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
    
    private void checkForPlayerColission() {
          Player player = playerController.getPlayer();

        for(ArrayList<Alien> aliens : alienRow){
            Iterator<Alien> i = aliens.iterator();
            while (i.hasNext()) {
               Alien alien = i.next(); 

                if(player.getyPosition() == alien.getyPosition() 
                    && (player.getxPosition() == alien.getxPosition()
                        || player.getxPosition()+1 == alien.getxPosition()
                        || player.getxPosition()+2 == alien.getxPosition()))
                {
                    int xPos = alien.getxPosition();
//                    Don't remove alien if it comes into contact with player (This is as per the game harness)
//                    i.remove();
                    playerController.killPlayer();
                    break;
                }
                
            }     
        } 
    }
    
    //Similiar to player version but removes shields and players as well
    private void removeAllObjectsInBlock(int xPosition, int yPosition) {
        ArrayList<GameObject> playerBullets = bulletController.getPlayerbullets();
        ArrayList<GameObject> alienBullets = bulletController.getAlienbullets();
        ArrayList<GameObject> shields = playerController.getAllShields();
        Player player = playerController.getPlayer();
        
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
                
                for(GameObject shield : shields){
                    if(shield.getyPosition() == j 
                            && (shield.getxPosition() == i))
                    {
                      shields.remove(shield);
                      break;
                    }
                }
                
                //remove the player is they are in range of a shield collision
                if(player.getyPosition()== j &&
                        (player.getxPosition() == i ||
                        player.getxPosition()+1 == i ||
                        player.getxPosition()+2 == i)){
                    playerController.killPlayer();
                }
            }
        }
        bulletController.setAlienbullets(alienBullets);
        bulletController.setPlayerbullets(playerBullets);
        playerController.setShields(shields); 
    }
    
    
    public AlienController clone(){
        AlienController alienControllerCopy = new AlienController();
//        ArrayList <ArrayList<Alien>> alienRow;

        alienControllerCopy.setFakeOpponentAlienFactory(fakeOpponentAlienFactory);
        alienControllerCopy.setGameOver(gameOver);
        alienControllerCopy.setWaveSize(waveSize);
        alienControllerCopy.playerNumber = this.playerNumber;
        alienControllerCopy.moveDownRound = this.moveDownRound;
        
        ArrayList <ArrayList<Alien>> tmpAlienRow = new ArrayList();
        for(ArrayList<Alien> tmpAliens : alienRow){
            tmpAlienRow.add(ArrayListCopy.copyArray(tmpAliens));
        }
        
        alienControllerCopy.setAliens(tmpAlienRow);
        if(!tmpAlienRow.isEmpty()){
            alienControllerCopy.setLatestRowAliens(tmpAlienRow.get(tmpAlienRow.size()-1));
        }else{
            ArrayList<Alien> tmpLatestRowAliens = new ArrayList();
            tmpAlienRow.add(latestRowAliens);
            alienControllerCopy.setLatestRowAliens(tmpLatestRowAliens);
        }


        return alienControllerCopy;
    }
    
    public int getAlienCount(){
        int count = 0;
        for(ArrayList<Alien> aliens : alienRow){
            count+= aliens.size();    
        }
        return count;
    }
    
    public int getAlienDistanceFromWall(){
        int minDistance = 13;
        if(!alienRow.isEmpty()){
            for(Alien alien : alienRow.get(0)){
               minDistance = alien.getyPosition();
               break;
            }
        }
        return minDistance;
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
        for(ArrayList<Alien> aliens : alienRow){
            Iterator<Alien> i = aliens.iterator();
            while (i.hasNext()) {
               Alien alien = i.next(); 
               alien.setBulletController(bulletController);
            }     
        } 
    }

    public AlienController getAlienController() {
        return alienController;
    }

    public void setAlienController(AlienController alienController) {
        this.alienController = alienController;
    }

    public void setLatestRowAliens(ArrayList<Alien> latestRowAliens) {
        this.latestRowAliens = latestRowAliens;
    }

    public void setFakeOpponentAlienFactory(int fakeOpponentAlienFactory) {
        this.fakeOpponentAlienFactory = fakeOpponentAlienFactory;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void setWaveSize(int waveSize) {
        this.waveSize = waveSize;
    } 
    
    public int getWaveSize(){
        return waveSize;
    }

}
