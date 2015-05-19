package spaceinvader.treeBuilder;

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
        mainA =-100000.0;
        mainB = 100000.0;
        
        
    }
    
    public TreeInterface build(TreeInterface node)
    {
     
        network = node.evaluation;
        TreeInterface root = node;
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
    
    public void buildTree(TreeInterface node)
    {
       //Todo recursive tree builder.
       //Takes any node of the tree and calculates all possible children for that node
    }
    
    public TreeInterface makeMove(TreeInterface node, String move)
    {
         
       
        return null;   
    }
    
    
}
