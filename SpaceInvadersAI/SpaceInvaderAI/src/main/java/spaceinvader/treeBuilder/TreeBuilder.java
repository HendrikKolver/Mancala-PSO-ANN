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
    
    public TreeInterface build(TreeInterface node)
    {
        network = node.evaluation;
        TreeInterface root = node;
        root.nodeDepth = 0;
        buildTree(root);
       
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
    
    private double buildTree(TreeInterface node)
    {
       //Todo recursive tree builder.
       //Takes any node of the tree and calculates all possible children for that node
       
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
       for(String possibleMove : possibleMoves){
           if(node.children == null){
               node.children = node.getCopy();
               tmpNode = node.children;
           }else{
               tmpNode.next = node.getCopy();
               tmpNode = tmpNode.next;
           }
           
           //Increase the node depth
           tmpNode.nodeDepth = node.nodeDepth+1;
          
           //Run a game update cycle, updating the board to a new state
           tmpNode.nextMove(possibleMove, node.roundCount);         
           tmpNode.nodeScore = buildTree(tmpNode);
           
       }
       
       node.evaluateMyself();
       return node.nodeScore;
    }
    
    public TreeInterface makeMove(TreeInterface node, String move)
    {
         
       
        return null;   
    }
    
    
}
