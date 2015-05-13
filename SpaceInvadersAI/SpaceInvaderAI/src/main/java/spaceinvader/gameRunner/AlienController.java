package spaceinvader.gameRunner;

import java.util.ArrayList;
import spaceinvader.entities.Alien;
import spaceinvader.entities.GameObject;
import spaceinvader.utilities.RandomGenerator;

/**
 *
 * @author Hendrik Kolver
 */
public class AlienController {
    ArrayList <ArrayList<Alien>> alienRow;
    ArrayList <Alien> latestRowAliens;
    boolean spawnRow = true;
    private int waveSize;
    
    public AlienController(){
        waveSize =3;
        alienRow = new ArrayList();
        latestRowAliens = new ArrayList();
        alienRow.add(latestRowAliens);
        addAlien();
    }
  
    public void update(int roundNumber){
        removeEmptyRows();
        updateAlienPosition();
        checkToFireBullet(roundNumber);
        checkToAddRow();
    }
    
    public void checkToFireBullet(int roundNumber){
        if(roundNumber % 6 == 0){
            ArrayList<Alien> firstRow = null;
            ArrayList<Alien> secondRow = null;
            if(alienRow.size() >=0){
                firstRow = alienRow.get(0);
            }
            if(alienRow.size() >1){
                secondRow = alienRow.get(1);
            }
            
            if(firstRow != null){
                int probability = RandomGenerator.randInt(1, 1000);
                if(probability <= 333){
                    int alienToShoot = RandomGenerator.randInt(0, firstRow.size()-1);
                    Alien firstRowAlienToShoot = firstRow.get(alienToShoot);
                    firstRowAlienToShoot.fireBullet();
                    //System.out.println("First row shooting alien xPos: "+ firstRowAlienToShoot.getxPosition());
                }
            }
            
            if(secondRow != null){
                int probability = RandomGenerator.randInt(1, 1000);
                if(probability <= 666){
                    int alienToShoot = RandomGenerator.randInt(0, secondRow.size()-1);
                    Alien secondRowAlienToShoot = secondRow.get(alienToShoot);
                    secondRowAlienToShoot.fireBullet();
                    //System.out.println("Second row shooting alien xPos: "+ firstRowAlienToShoot.getxPosition());
                }
            }

        }
    }
    
    public void checkToAddRow(){
        if(latestRowAliens.get(0).getyPosition() <10){
            addNewRow();
            addAlien();
        }
    }
    
    public void updateAlienPosition(){
         for (ArrayList<Alien> rowAliens : alienRow) { 
            boolean moveDown = false;
            for (Alien alien : rowAliens) {    
                if(alien.getMoveDirection().equals("LEFT")){
                    if(alien.getxPosition() == 1){
                       moveDown = true;
                       break;
                    }
                }else{
                    if(alien.getxPosition() == 17){
                       moveDown = true;
                       break;
                    }
                }
            }
            
            for (Alien alien : rowAliens) {
                 if(moveDown){
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
            latestRowAliens.add(new Alien(xStartLocation,11));
            xStartLocation -= 2;
        }
    }
    
    private void addNewRow(){
        latestRowAliens = new ArrayList();
        alienRow.add(latestRowAliens);
    }
    
    private void removeEmptyRows(){ 
        for (int i = 0; i < alienRow.size();) {
            if(alienRow.get(i).isEmpty()){
                alienRow.remove(i);
            }else{
                i++;
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

}
