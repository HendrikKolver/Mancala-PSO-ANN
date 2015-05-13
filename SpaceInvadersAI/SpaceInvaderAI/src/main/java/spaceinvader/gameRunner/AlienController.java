package spaceinvader.gameRunner;

import java.util.ArrayList;
import spaceinvader.entities.Alien;
import spaceinvader.entities.GameObject;

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
  
    public void update(){
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
        if(latestRowAliens.get(0).getyPosition() <10){
            addNewRow();
            addAlien();
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
