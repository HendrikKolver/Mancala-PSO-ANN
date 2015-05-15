package spaceinvader;

import java.util.ArrayList;
import spaceinvader.entities.Alien;
import spaceinvader.entities.AlienBullet;
import spaceinvader.entities.Building;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.entities.Shield;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.gameRunner.PlayerController;

/**
 *
 * @author Hendrik Kolver
 */
public class SpaceInvader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {        
        AlienController alienController = AlienController.getInstance();
        int roundCounter = 0;
        int waveRoundCounter = 0;
        long startTime = System.currentTimeMillis();

        while(roundCounter<200){
            Thread.sleep(500);
            roundCounter++;
            waveRoundCounter++;
            
            if(waveRoundCounter ==40){
                alienController.increaseWaveSize();
                //waveRoundCounter=0;
            }
            
            printBoard(alienController, roundCounter);
            
            if(PlayerController.getInstance().isGameOver()){
                System.out.println("Game Over");
                break;
            }
            
            BulletController.getInstance().update();
            alienController.update(roundCounter);
            PlayerController.getInstance().update();
            
            //Check again for collisions to see if someone moved into a bullet
            BulletController.getInstance().alienBulletColissionDetection();
            BulletController.getInstance().playerBulletColissionDetection();
        }
        
        long endTime = System.currentTimeMillis();

        long duration = (endTime - startTime);
        System.out.println("Total rounds played: "+roundCounter);
        System.out.println("Total time: "+duration);
        
        
    }
    
 public static void printBoard(AlienController alienController, int roundCounter){
    String board[][] = new String[19][14];
    
    for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[0].length; j++) {
            if(j == 13 || j == 0){
                board[i][j] = "#";
            }else if(i == 0 || i == 18){
                board[i][j] = "#";
            }else{
                board[i][j] = " ";
            }
        }
    }
     
    ArrayList<AlienBullet> alienBulletList = BulletController.getInstance().getAlienbullets();
    ArrayList<PlayerBullet> playerBulletList = BulletController.getInstance().getPlayerbullets();
    
    
    
    ArrayList<ArrayList<Alien>> aliens = alienController.getAllAliens();
    for (ArrayList<Alien> rowAliens : aliens) { 
        for (Alien alien : rowAliens) {   
            board[alien.getxPosition()][alien.getyPosition()] = "O";
        }
    }
    System.out.println("\n");
    
    if(PlayerController.getInstance().isPlayerAlive()){
        int playerPosition = PlayerController.getInstance().getPlayerPosition();
        board[playerPosition][2] = "A";
        board[playerPosition+1][2] = "A";
        board[playerPosition+2][2] = "A";
    }
    
    ArrayList<Building> buildings = PlayerController.getInstance().getBuildings();
    for(Building building : buildings){
        board[building.getxPosition()][building.getyPosition()] = building.getRepresentation();
        board[building.getxPosition()+1][building.getyPosition()] = building.getRepresentation();
        board[building.getxPosition()+2][building.getyPosition()] = building.getRepresentation();
    }
    
    ArrayList<Shield> shields = PlayerController.getInstance().getAllShields();
    for(Shield shield : shields){
        board[shield.getxPosition()][shield.getyPosition()] = shield.getRepresentation();
    } 
    
    for(AlienBullet alienBullet : alienBulletList){
        board[alienBullet.getxPosition()][alienBullet.getyPosition()] = "|";
    }
    
    for(PlayerBullet playerBullet : playerBulletList){
        board[playerBullet.getxPosition()][playerBullet.getyPosition()] = "1";

    }
    
    String boardString = "";
    for (int i = board[0].length-1; i >= 0; i--) {
        for (int j = 0; j < board.length; j++) {
            boardString += board[j][i];
        }
        boardString+="\n";
    }
    //System.out.println("-------------------");
    System.out.print('\r');
    System.out.println(boardString + "\n" + "Round: "+ roundCounter);
    //System.out.println("Round: "+ roundCounter);
    //System.out.println("-------------------");
 }    
    
}
