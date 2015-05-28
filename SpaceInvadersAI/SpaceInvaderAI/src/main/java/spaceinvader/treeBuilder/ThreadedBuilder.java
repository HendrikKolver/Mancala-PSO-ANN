package spaceinvader.treeBuilder;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Hendrik Kolver
 */
public class ThreadedBuilder implements Runnable {
    TreeInterface rootNode;
    String initialMove;
    boolean isCompleted;
    int plyDepth;
    
    public ThreadedBuilder(TreeInterface node, String move, int plyDepth){
        this.rootNode = node;
        this.initialMove = move;
        this.isCompleted = false;
        this.plyDepth = plyDepth;
    }

    @Override
    public synchronized void run() {
        try {
            //Quick fix for bug caused by the first node's node depth being increased twice (first time in addChild(), second time in buildTree()
            rootNode.nodeDepth--;
            buildTree(rootNode,initialMove);
            this.isCompleted = true;
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadedBuilder.class.getName()).log(Level.SEVERE, null, ex);
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
