package spaceinvader.treeBuilder;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    public TreeBuilder(int depth)
    {
        plyDepth = depth;    
    }
    
    public TreeInterface build(TreeInterface node) throws InterruptedException
    {
//        double start = System.currentTimeMillis();
        network = node.evaluation;
        TreeInterface root = node;
        root.nodeDepth = 0;
        
        ArrayList<String> possibleMoves = node.getPossibleMoves();  
        //System.out.println("Outside loop possibleMoves: "+ possibleMoves);
        
        
        TreeInterface tmpNode = null;
        
        ExecutorService executor = ThreadPool.executor;
//        ExecutorService executor = Executors.newFixedThreadPool(2);
        ArrayList<ThreadedBuilder> builders = new ArrayList();

        for(String possibleMove : possibleMoves){
            tmpNode = root.getCopy();
            root.addChild(tmpNode);
            ThreadedBuilder builderThread = new ThreadedBuilder(tmpNode,possibleMove,this.plyDepth);
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
//        executor.shutdown();
//        
//        while (!executor.isTerminated()) {
//        }
       
//        buildTree(root,null);
       
        tmpNode = root.children;
        
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
//        double end = System.currentTimeMillis();
//        System.out.println("Total time in method: "+ (end-start));
        
        return finalNode;
    }
    
    private double buildTree(TreeInterface node, String move) throws InterruptedException
    {
        //Run a game update cycle, updating the board to a new state if not root node
        if(move != null){
            node.nextMove(move, node.roundCount); 
            node.nodeDepth++;
            node.roundCount++;
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
        //System.out.println("Outside loop possibleMoves: "+ possibleMoves);
        
        
        TreeInterface tmpNode = null;
       //Increase the node depth
        double tmpNodeScore = 0;
        double tmpNodeScoreMax = 0;
        

        for(String possibleMove : possibleMoves){
            tmpNode = node.getCopy();

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
