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
        buildTree(root,null);
       
        TreeInterface tmpNode = root.children;
        TreeInterface finalNode = null;
        double tmpCount = -1000.0;
        
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
        
        //Todo recursive tree builder.
        //Takes any node of the tree and calculates all possible children for that node
        
        
        //Run a game update cycle, updating the board to a new state if not root node
        if(move != null){
            node.nextMove(move, node.roundCount); 
            node.nodeDepth++;
            node.roundCount++;
        }

        if(node.isGameOver()){
            //System.out.println("gameOver");
            node.evaluateMyself();
            return node.nodeScore;
        }

        if(node.roundCount >=200){
            //System.out.println("round limit reached");
            node.evaluateMyself();
            return node.nodeScore;
        }

        //System.out.println("plyDepth: "+node.nodeDepth);
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
    
    public TreeInterface makeMove(TreeInterface node, String move)
    {
         
       
        return null;   
    }
    
    
}
