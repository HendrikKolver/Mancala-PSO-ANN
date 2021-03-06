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
     
    public TreeComposite(NeuralNetwork n, int roundCounter, boolean aggresiveTactic, NeuralNetwork backup)
    {
        this.agressiveTactic = aggresiveTactic;
        evaluation = n;
        this.originalEval = evaluation;
        this.backup = backup;
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
        agressiveTactic = false;
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
        nnEval();
     }
     
     @Override
     public void evaluateMyself(boolean normal)
     {
        normalEval();
     }
     
     
     private void nnEval(){
        double input[] = getInputs();
         //node evaluation
        Neuron out[] = evaluation.calculate(input);
        this.nodeScore = out[0].fireOutput();
        out[0].clear(); 
     }
     
     private void normalEval(){
         double score = 0;
//        double score = this.playerController.getKillCount();
//        score += (this.playerController.getLives()*10);
//        score += (this.roundCount);
        if(this.playerController.hasAlienFactory()){
            score += 30;
        }
        if(this.playerController.hasBulletFactory()){
            score += 20;
        }

        if(isGameOver() && this.roundCount !=200){
            score-=100;
        }
        
//        this.nodeScore = this.boardFinalRating();
        this.nodeScore = score;
     }
    
    private double[] getInputs(){
        double[] inputs;
        if(evaluation.getInputCount() == 6){
            inputs = new double[6];
            inputs[0] = this.playerController.getAllShields().size();
            inputs[1] = this.playerController.getPlayer().getBulletLimit();
            inputs[2] = this.playerController.getLives();
            inputs[3] = this.playerController.getKillCount();
            inputs[4] = this.bulletController.getPlayerBulletCount();
            inputs[5] = this.roundCount;
        }else{
            inputs = new double[11];
            inputs[0] = this.alienController.getAlienCount();
            inputs[1] = this.playerController.getAllShields().size();
            inputs[2] = this.bulletController.getPlayerBulletCount();
            inputs[3] = this.bulletController.getAlienbullets().size();
            inputs[4] = this.playerController.getLives();
            inputs[5] = this.playerController.getKillCount();
            ArrayList<GameObject> buildings = this.playerController.getBuildings();

            int bulletFactoryCount = 0;
            for(GameObject building : buildings){
                if(building.getRepresentation().equals("B")){
                    bulletFactoryCount++;
                }
            }

            inputs[6] = bulletFactoryCount;

            int alienFactoryCount = 0;
            for(GameObject building : buildings){
                if(building.getRepresentation().equals("X")){
                    alienFactoryCount++;
                }
            }

            inputs[7] = alienFactoryCount;

            inputs[8] = this.alienController.getAlienDistanceFromWall();
            inputs[9] = this.roundCount;
            inputs[10] = this.alienController.getWaveSize();
        }
        
        
        return inputs;
    }
     

    @Override
    public boolean isGameOver() {
        this.finalState = (alienController.isGameOver() || playerController.isGameOver());
        return this.finalState;
    }

    @Override
    protected TreeInterface clone() {
        TreeInterface nodeCopy = new TreeComposite();
        AlienController tmpAlienController = this.alienController.clone();
        nodeCopy.alienController = tmpAlienController;
        
        PlayerController tmpPlayerController = this.playerController.clone();
        nodeCopy.playerController = tmpPlayerController;
        
        BulletController tmpBulletController = this.bulletController.clone();
        nodeCopy.bulletController = tmpBulletController;
        setupControllers(nodeCopy, tmpPlayerController, tmpAlienController, tmpBulletController);
        
        nodeCopy.children = null;
        nodeCopy.next =null;
        nodeCopy.nodeScore = this.nodeScore;
        nodeCopy.nodeDepth = this.nodeDepth;
        nodeCopy.finalState = this.finalState;
        nodeCopy.roundCount = this.roundCount;
        nodeCopy.evaluation = this.evaluation.clone();
        nodeCopy.backup = this.backup.clone();
        nodeCopy.originalEval = this.originalEval.clone();
        nodeCopy.agressiveTactic = this.agressiveTactic;
        
        return nodeCopy;
    }
    
    @Override
    void nextMove(String move, int roundCounter) {
        this.move = move;
        this.bulletController.update();
        this.alienController.update(roundCounter);
        this.playerController.update(move);
        //Check again for collisions to see if someone moved into a bullet
        this.bulletController.alienBulletColissionDetection();
        this.bulletController.playerBulletColissionDetection();
        this.bulletController.enemyBulletColissionDetection();
        
    }

    @Override
    ArrayList<String> getPossibleMoves() {
        return this.playerController.getPossibleMoves(this.roundCount);
    }
    
    private void setupInitialControllers(TreeInterface node, PlayerController playerController, AlienController alienController, BulletController bulletController){
        node.alienController.setAlienController(alienController);
        node.alienController.setBulletController(bulletController);
        node.alienController.setPlayerController(playerController);
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
    ArrayList<GameObject> enemyBulletList = bulletController.getEnemyBulletList();
    
    
    
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
        if(playerBullet.getyPosition() <13){
            board[playerBullet.getxPosition()][playerBullet.getyPosition()] = "1";
        }else{
            board[playerBullet.getxPosition()][13] = "1";
        }
    }
    
    for(GameObject enemyBullet : enemyBulletList){
        if(enemyBullet.getyPosition() <13){
            board[enemyBullet.getxPosition()][enemyBullet.getyPosition()] = ";";
        }
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
        int finalRating = 0;
        if(this.roundCount >= 200){
            finalRating+= 250;
            finalRating+=this.playerController.getKillCount();
        }else{
            finalRating+= this.roundCount;
            finalRating+= this.playerController.getKillCount();    
        }

        return finalRating;
    }
    
    @Override
    public int getKillCount(){
        return this.playerController.getKillCount();
    }

    public AlienController getAlienController() {
        return alienController;
    }

    public void setAlienController(AlienController alienController) {
        this.alienController = alienController;
    }

    @Override
    public PlayerController getPlayerController() {
        return playerController;
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    @Override
    public BulletController getBulletController() {
        return bulletController;
    }

    public void setBulletController(BulletController bulletController) {
        this.bulletController = bulletController;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }
    
    @Override
    public String getMove(){
        return this.move;
    }

    @Override
    public void setBackupTo(boolean val) {
        if(val){
            this.evaluation = backup;
        }else{
            this.evaluation = originalEval;
        }
    }
         
}
