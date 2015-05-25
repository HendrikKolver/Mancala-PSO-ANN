package spaceinvader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import spaceinvader.entities.Alien;
import spaceinvader.entities.GameObject;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.gameRunner.PlayerController;
import spaceinvader.treeBuilder.TreeBuilder;
import spaceinvader.treeBuilder.TreeComposite;
import spaceinvader.treeBuilder.TreeInterface;

/**
 *
 * @author Hendrik Kolver
 */
public class SpaceInvader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {        
        long startTime = System.currentTimeMillis();
        
        TreeInterface rootNode = new TreeComposite(null, 0);
        TreeBuilder treeBuilder = new TreeBuilder(6);
            
            while(!rootNode.isGameOver() && rootNode.roundCount <200){

                Thread.sleep(200);
                rootNode.printBoard();
                long moveStartTime = System.currentTimeMillis();
                rootNode = treeBuilder.build(rootNode);
                long moveEndTime = System.currentTimeMillis();
                System.out.println("move duration: "+(moveEndTime-moveStartTime));
            }
            rootNode.printBoard();
               
        long endTime = System.currentTimeMillis();

        long duration = (endTime - startTime);
        System.out.println("Total time: "+duration);
        
        
    }
    
public static void setupControllers(PlayerController playerController, AlienController alienController, BulletController bulletController){
    alienController.setAlienController(alienController);
    alienController.setBulletController(bulletController);
    alienController.setPlayerController(playerController);
    alienController.addAlien();
    playerController.setAlienController(alienController);
    playerController.setBulletController(bulletController);
    bulletController.setAlienController(alienController);
    bulletController.setPlayerController(playerController);
}
    
 public static void printBoard(AlienController alienController, PlayerController playerController, BulletController bulletController, int roundCounter){
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
     
    ArrayList<GameObject> alienBulletList = bulletController.getAlienbullets();
    ArrayList<GameObject> playerBulletList = bulletController.getPlayerbullets();
    
    
    
    ArrayList<ArrayList<Alien>> aliens = alienController.getAllAliens();
    for (ArrayList<Alien> rowAliens : aliens) { 
        for (Alien alien : rowAliens) {   
            board[alien.getxPosition()][alien.getyPosition()] = "O";
        }
    }
    System.out.println("\n");
    
    if(playerController.isPlayerAlive()){
        int playerPosition = playerController.getPlayerPosition();
        board[playerPosition][2] = "A";
        board[playerPosition+1][2] = "A";
        board[playerPosition+2][2] = "A";
    }
    
    ArrayList<GameObject> buildings = playerController.getBuildings();
    for(GameObject building : buildings){
        board[building.getxPosition()][building.getyPosition()] = building.getRepresentation();
        board[building.getxPosition()+1][building.getyPosition()] = building.getRepresentation();
        board[building.getxPosition()+2][building.getyPosition()] = building.getRepresentation();
    }
    
    ArrayList<GameObject> shields = playerController.getAllShields();
    for(GameObject shield : shields){
        board[shield.getxPosition()][shield.getyPosition()] = shield.getRepresentation();
    } 
    
    for(GameObject alienBullet : alienBulletList){
        board[alienBullet.getxPosition()][alienBullet.getyPosition()] = "|";
    }
    
    for(GameObject playerBullet : playerBulletList){
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
    System.out.println(boardString + "\n" + "Round: "+ roundCounter + " \n Total kills: "+playerController.getKillCount());

    //System.out.println("Round: "+ roundCounter);
    //System.out.println("-------------------");
 }    
    
}
