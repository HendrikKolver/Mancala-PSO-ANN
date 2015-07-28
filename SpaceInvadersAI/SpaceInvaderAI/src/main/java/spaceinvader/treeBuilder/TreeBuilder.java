package spaceinvader.treeBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import spaceinvader.entities.GameObject;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.utilities.ThreadPool;

/**
 *
 * @author Hendrik Kolver
 */
public class TreeBuilder {
    public boolean gameOver = false;
    public int nodeCount =0;
    public int minMaxSwitcher = 0;
    public int plyDepth;
    public double mainA, mainB;
    public int globalPlayer = 2;
    public NeuralNetwork network;
    public boolean normalEval;
    public boolean aggresiveTactic;

    public TreeBuilder(int depth)
    {
        plyDepth = depth;  
        normalEval = false;
    }
    
    public TreeInterface build(TreeInterface node, boolean aggresiveTactic) throws InterruptedException
    {
        this.aggresiveTactic = aggresiveTactic;
//        double start = System.currentTimeMillis();
        network = node.evaluation;
        TreeInterface root = node;
        root.nodeDepth = 0;
        
//        node.setBackupTo(false); 
//        if(aggresiveTactic){
//            if(node.getAlienController().getWaveSize() > 4 && !node.getPlayerController().hasAlienFactory()){
//                this.normalEval = true;
//            }
//        }
        
        buildTreeMultiThreaded(node, root);  
//        buildTreeSingleThreaded(root);
       
        TreeInterface tmpNode = root.children;
        
        TreeInterface finalNode = null;
        double tmpCount = tmpNode.nodeScore;
        finalNode = tmpNode;
        tmpNode = tmpNode.next;      
        
        while(tmpNode != null)
        {
            if(aggresiveTactic){
                if(node.roundCount ==4){
                    if(tmpNode.move.equals("BuildAlienFactory")){
                        finalNode = tmpNode;
                        break;
                    }
                }
                if(node.roundCount ==5){
                    if(tmpNode.move.equals("BuildShield")){
                        finalNode = tmpNode;
                        break;
                    }
                }
            }
            if(tmpNode.nodeScore > tmpCount)
            {
                tmpCount = tmpNode.nodeScore;
                finalNode = tmpNode;
            }
            tmpNode = tmpNode.next; 
        }
        
        return finalNode;
    }
    
    private void buildTreeSingleThreaded(TreeInterface root) throws InterruptedException{
        buildTree(root,null);
    }

    private void buildTreeMultiThreaded(TreeInterface node, TreeInterface root) {
        ArrayList<String> possibleMoves = node.getPossibleMoves();
        //System.out.println("Outside loop possibleMoves: "+ possibleMoves);
        
        
        TreeInterface tmpNode = null;
        
        ExecutorService executor = ThreadPool.executor;
        ArrayList<ThreadedBuilder> builders = new ArrayList();

        for(String possibleMove : possibleMoves){
            tmpNode = root.clone();
            root.addChild(tmpNode);
            ThreadedBuilder builderThread = new ThreadedBuilder(tmpNode,possibleMove,this.plyDepth, this.aggresiveTactic);
            builderThread.normalEval = this.normalEval;
            builders.add(builderThread);
            executor.execute(builderThread);
        }
        

        while(true){
            boolean isCompleted = true;
            for(ThreadedBuilder builder : builders){
                if(!builder.isCompleted){
                    isCompleted = false;
                    break;
                }
            }
            if(isCompleted){
                break;
            }
        }
    }
    
    private double buildTree(TreeInterface node, String move) throws InterruptedException
    {
        boolean hasFactoryBefore = node.playerController.hasAlienFactory();
        boolean hasBulletFactoryBefore = node.playerController.hasBulletFactory();
         
        //Run a game update cycle, updating the board to a new state if not root node
        if(move != null){
            node.nextMove(move, node.roundCount); 
            node.nodeDepth++;
            node.roundCount++;
        }
        
        //Although I achieved better results by checking the distance from wall first the death needs to be checked first
        //Eg all but one position causes aliens to be in the bottom row. The one that doesn't causes the player to die. 
        //This results in the game choosing death for the player
        //Negative points for dying
        if(node.getPlayerController().getDeathOccured() && node.nodeDepth != 0){
            node.evaluateMyself();
            node.nodeScore-= 1000;
            return node.nodeScore;
        }

        //negative points for letting aliens come too close   
        if(node.getAlienController().getAlienDistanceFromWall() <=2){
            if(node.nodeDepth != 0){
                node.evaluateMyself();
                node.nodeScore-= (500/node.getAlienController().getAlienDistanceFromWall());
                return node.nodeScore;
            }else{
                node.evaluateMyself();
                node.nodeScore-= (500/node.getAlienController().getAlienDistanceFromWall());
            } 
        }      

        if(node.isGameOver()){
            node.evaluateMyself();
            return node.nodeScore;
        }

        if(node.roundCount >=200){
            node.evaluateMyself();
            return node.nodeScore;
        }

        if(node.nodeDepth >= plyDepth){
            
            node.evaluateMyself();
            return node.nodeScore;
        }
        
        if(this.aggresiveTactic){
           if(node.alienController.getWaveSize() >4){
                if(!hasBulletFactoryBefore && node.playerController.hasBulletFactory()){
                    node.evaluateMyself();
                    node.nodeScore += 100;
                    return node.nodeScore;
                }
                if(!hasFactoryBefore && node.playerController.hasAlienFactory()){
                    node.evaluateMyself();
                    node.nodeScore += 100;
                    return node.nodeScore;
                }
            } 
        }
        
       
        ArrayList<String> possibleMoves = node.getPossibleMoves();         
        
        TreeInterface tmpNode = null;
       //Increase the node depth
        double tmpNodeScore = 0;
        double tmpNodeScoreMax = 0;
        

        for(String possibleMove : possibleMoves){
            tmpNode = node.clone();

            tmpNodeScore = buildTree(tmpNode,possibleMove);
            node.addChild(tmpNode);
            if(tmpNodeScore>tmpNodeScoreMax){
                tmpNodeScoreMax = tmpNodeScore;
            }

        }
        node.nodeScore = tmpNodeScoreMax;
        
        return node.nodeScore;
    }  
}
