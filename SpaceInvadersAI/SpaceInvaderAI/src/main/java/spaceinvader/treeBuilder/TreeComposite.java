package spaceinvader.treeBuilder;

import java.util.ArrayList;
import java.util.Random;
import spaceinvader.entities.Alien;
import spaceinvader.entities.AlienBullet;
import spaceinvader.entities.Building;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.entities.Shield;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.gameRunner.PlayerController;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.utilities.RandomGenerator;

/**
 *
 * @author Hendrik Kolver
 */
public class TreeComposite extends TreeInterface {       
     
    public TreeComposite(NeuralNetwork n, int roundCounter)
    {
        evaluation = n;
        children = null;
        next = null;
        nodeScore = 0;
        
        this.roundCount = roundCounter;
       
        alienController = new AlienController();
        playerController = new PlayerController();
        bulletController = new BulletController();
        setupControllers(this, playerController, alienController, bulletController);
    }
    
    
    
     
    @Override
    public TreeInterface getNext()
    {
        return next;
    }
     
    @Override
    public void setNext(TreeInterface newNode)
    {
        next = newNode;
    }
     
    @Override
    public void addChild(TreeInterface node)
    {

        if(children == null)
       {
           children = node;
           children.nodeDepth = nodeDepth+1;
       }
       else
       {
           TreeInterface tmpNode = children;

           while(tmpNode.next != null)
           {
               tmpNode = tmpNode.next;
           }
           tmpNode.next = node; 
           node.nodeDepth = nodeDepth+1;
       }
    }

    @Override
     public void evaluateMyself()
     {
        this.nodeScore = RandomGenerator.randInt(0, 20);
         //TODO Simple evaluation function  
     }

    @Override
    public boolean isGameOver() {
        return (alienController.isGameOver() || playerController.isGameOver());
    }

    @Override
    TreeInterface getCopy() {
        TreeInterface nodeCopy = new TreeComposite(null, this.roundCount);
        AlienController tmpAlienController = this.alienController.getCopy();
        nodeCopy.alienController = tmpAlienController;
        
        PlayerController tmpPlayerController = this.playerController.getCopy();
        nodeCopy.playerController = tmpPlayerController;
        
        BulletController tmpBulletController = this.bulletController.getCopy();
        nodeCopy.bulletController = tmpBulletController;
        setupControllers(nodeCopy, tmpPlayerController, tmpAlienController, tmpBulletController);
        
        return nodeCopy;
    }
    
    @Override
    void nextMove(String move, int roundCounter) {
        this.bulletController.update();
        this.alienController.update(roundCounter);
        this.playerController.update(move);
    }

    @Override
    ArrayList<String> getPossibleMoves() {
        return this.playerController.getPossibleMoves();
    }
    
    private void setupControllers(TreeInterface node, PlayerController playerController, AlienController alienController, BulletController bulletController){
        node.alienController.setAlienController(alienController);
        node.alienController.setBulletController(bulletController);
        node.alienController.setPlayerController(playerController);
        node.alienController.addAlien();
        node.playerController.setAlienController(alienController);
        node.playerController.setBulletController(bulletController);
        node.bulletController.setAlienController(alienController);
        node.bulletController.setPlayerController(playerController);
    }
    
    public void printBoard(){
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
    System.out.println(boardString + "\n" + "Round: "+ roundCount + " \n Total kills: "+playerController.getKillCount());

    //System.out.println("Round: "+ roundCounter);
    //System.out.println("-------------------");
 }   
         
}
