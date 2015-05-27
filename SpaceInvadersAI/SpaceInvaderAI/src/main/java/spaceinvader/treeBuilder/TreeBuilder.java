package spaceinvader.treeBuilder;

import java.util.ArrayList;
import spaceinvader.neuralNetwork.NeuralNetwork;

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
        network = node.evaluation;
        TreeInterface root = node;
        root.nodeDepth = 0;
        
        ArrayList<String> possibleMoves = node.getPossibleMoves();  
        //System.out.println("Outside loop possibleMoves: "+ possibleMoves);
        
        
        TreeInterface tmpNode = null;
       //Increase the node depth
        double tmpNodeScore = 0;
        double tmpNodeScoreMax = 0;
        
        ArrayList<ThreadedBuilder> builders = new ArrayList();

        for(String possibleMove : possibleMoves){
            tmpNode = root.getCopy();
            root.addChild(tmpNode);
            ThreadedBuilder builderThread = new ThreadedBuilder(tmpNode,possibleMove,this.plyDepth);
            Thread t = new Thread(builderThread);
            t.start();
            builders.add(builderThread);
        }
        
        
        
        boolean isAllCompleted = true;
        while(true){
           
            for(ThreadedBuilder builder : builders){
                if(!builder.isCompleted){
                    isAllCompleted = false;
                    break;
                }
            }
            if(isAllCompleted){
                break;
            }
            isAllCompleted = true;
        }
        
       // buildTree(root,null);
       
        tmpNode = root.children;
        
        //Root has no children... 
        //Probably caused by it being the last round or something
        //very rare bug.. still consider looking into it
//        if(tmpNode == null){
//            return root;
//        }
        
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
