package spaceinvader.treeBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
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

    public TreeBuilder(int depth)
    {
        plyDepth = depth;  
        normalEval = false;
    }
    
    public TreeInterface build(TreeInterface node) throws InterruptedException
    {
//        double start = System.currentTimeMillis();
        network = node.evaluation;
        TreeInterface root = node;
        root.nodeDepth = 0;
        
        
          buildTreeMultiThreaded(node, root);  
//        buildTreeSingleThreaded(root);
       
        TreeInterface tmpNode = root.children;
        
        TreeInterface finalNode = null;
        double tmpCount = tmpNode.nodeScore;
        finalNode = tmpNode;
        tmpNode = tmpNode.next;      
        
        while(tmpNode != null)
        {
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
            ThreadedBuilder builderThread = new ThreadedBuilder(tmpNode,possibleMove,this.plyDepth);
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
        //Run a game update cycle, updating the board to a new state if not root node
        if(move != null){
            node.nextMove(move, node.roundCount); 
            node.nodeDepth++;
            node.roundCount++;
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
        
        //Negative points for dying
        if(node.getPlayerController().getDeathOccured() && node.nodeDepth != 0){
            node.evaluateMyself();
            node.nodeScore-= 1000;
            return node.nodeScore;
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
