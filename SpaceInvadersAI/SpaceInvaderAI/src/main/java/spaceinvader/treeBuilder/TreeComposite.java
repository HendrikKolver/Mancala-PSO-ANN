package spaceinvader.treeBuilder;

import java.util.ArrayList;
import spaceinvader.entities.Alien;
import spaceinvader.entities.AlienFactory;
import spaceinvader.entities.BulletFactory;
import spaceinvader.entities.GameObject;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.gameRunner.PlayerController;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.neuralNetwork.Neuron;

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
        setupInitialControllers(this, playerController, alienController, bulletController);
    }
    
    public TreeComposite()
    {
       
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
//        double score = this.playerController.getKillCount();
//        score += this.playerController.getLives();
//
//        if(isGameOver()){
//            score-=50;
//        }
//        this.nodeScore = score;
         //TODO Simple evaluation function  
        
        double input[] = getInputs();
         //node evaluation
        Neuron out[] = evaluation.calculate(input);
        this.nodeScore = out[0].fireOutput();
        out[0].clear(); 
     }
    
    private double[] getInputs(){
        double[] inputs = new double[10];
        inputs[0] = this.alienController.getAlienCount();
        inputs[1] = this.playerController.getAllShields().size();
        inputs[2] = this.bulletController.getPlayerBulletCount();
        inputs[3] = this.bulletController.getAlienbullets().size();
        inputs[4] = this.playerController.getLives();
        inputs[5] = this.playerController.getKillCount();
        ArrayList<GameObject> buildings = this.playerController.getBuildings();
        
        int bulletFactoryCount = 0;
        for(GameObject building : buildings){
            if(building instanceof BulletFactory){
                bulletFactoryCount++;
            }
        }
        
        inputs[6] = bulletFactoryCount;
        
        int alienFactoryCount = 0;
        for(GameObject building : buildings){
            if(building instanceof AlienFactory){
                alienFactoryCount++;
            }
        }
       
        inputs[7] = alienFactoryCount;
        
        inputs[8] = this.alienController.getAlienDistanceFromWall();
        inputs[9] = this.roundCount;
        
        return inputs;
    }
     

    @Override
    public boolean isGameOver() {
        this.finalState = (alienController.isGameOver() || playerController.isGameOver());
        return this.finalState;
    }

    @Override
    TreeInterface getCopy() {
        TreeInterface nodeCopy = new TreeComposite();
        AlienController tmpAlienController = this.alienController.getCopy();
        nodeCopy.alienController = tmpAlienController;
        
        PlayerController tmpPlayerController = this.playerController.getCopy();
        nodeCopy.playerController = tmpPlayerController;
        
        BulletController tmpBulletController = this.bulletController.getCopy();
        nodeCopy.bulletController = tmpBulletController;
        setupControllers(nodeCopy, tmpPlayerController, tmpAlienController, tmpBulletController);
        
        nodeCopy.children = null;
        nodeCopy.next =null;
        nodeCopy.nodeScore = this.nodeScore;
        nodeCopy.nodeDepth = this.nodeDepth;
        nodeCopy.finalState = this.finalState;
        nodeCopy.roundCount = this.roundCount;
        nodeCopy.evaluation = this.evaluation;
        
        return nodeCopy;
    }
    
    @Override
    void nextMove(String move, int roundCounter) {
        this.bulletController.update();
        this.alienController.update(roundCounter);
        this.playerController.update(move);
        //Check again for collisions to see if someone moved into a bullet
        this.bulletController.alienBulletColissionDetection();
        this.bulletController.playerBulletColissionDetection();
    }

    @Override
    ArrayList<String> getPossibleMoves() {
        return this.playerController.getPossibleMoves();
    }
    
    private void setupInitialControllers(TreeInterface node, PlayerController playerController, AlienController alienController, BulletController bulletController){
        node.alienController.setAlienController(alienController);
        node.alienController.setBulletController(bulletController);
        node.alienController.setPlayerController(playerController);
        node.alienController.addAlien();
        node.playerController.setAlienController(alienController);
        node.playerController.setBulletController(bulletController);
        node.bulletController.setAlienController(alienController);
        node.bulletController.setPlayerController(playerController);
    }
    
     private void setupControllers(TreeInterface node, PlayerController playerController, AlienController alienController, BulletController bulletController){
        node.alienController.setAlienController(alienController);
        node.alienController.setBulletController(bulletController);
        node.alienController.setPlayerController(playerController);
        node.playerController.setAlienController(alienController);
        node.playerController.setBulletController(bulletController);
        node.bulletController.setAlienController(alienController);
        node.bulletController.setPlayerController(playerController);
    }
    
    @Override
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
        board[shield.getxPosition()][shield.getyPosition()] = "-";
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

    @Override
    public int boardFinalRating() {
        int finalRating = 
                this.roundCount
                +this.playerController.getKillCount()
                +this.playerController.getLives();
        return finalRating;
    }
    
    @Override
    public int getKillCount(){
        return this.playerController.getKillCount();
    }
         
}
