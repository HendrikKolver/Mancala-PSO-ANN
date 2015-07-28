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
    public boolean isCompleted;
    int plyDepth;
    public boolean normalEval;
    private boolean aggresiveTactic;
    
    public ThreadedBuilder(TreeInterface node, String move, int plyDepth, boolean aggresiveTactic){
        this.aggresiveTactic = aggresiveTactic;
        this.rootNode = node;
        this.initialMove = move;
        this.isCompleted = false;
        this.plyDepth = plyDepth;
        this.normalEval = false;
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
            if(this.normalEval){
                node.evaluateMyself(true);
            }else{
                node.evaluateMyself();
            }
            node.nodeScore-= 1000;
            return node.nodeScore;
        }

        //negative points for letting aliens come too close   
        if(node.getAlienController().getAlienDistanceFromWall() <=3){
            if(node.nodeDepth != 0){
                if(this.normalEval){
                node.evaluateMyself(true);
            }else{
                node.evaluateMyself();
            }
                node.nodeScore-= (500/node.getAlienController().getAlienDistanceFromWall());
                return node.nodeScore;
            }else{
                if(this.normalEval){
                node.evaluateMyself(true);
            }else{
                node.evaluateMyself();
            }
                node.nodeScore-= (500/node.getAlienController().getAlienDistanceFromWall());
            } 
        }       

        if(node.isGameOver()){
            if(this.normalEval){
                node.evaluateMyself(true);
            }else{
                node.evaluateMyself();
            }
            
            return node.nodeScore;
        }

        if(node.roundCount >=200){
            if(this.normalEval){
                node.evaluateMyself(true);
            }else{
                node.evaluateMyself();
            }
            return node.nodeScore;
        }

        if(node.nodeDepth >= plyDepth){
            
            if(this.normalEval){
                node.evaluateMyself(true);
            }else{
                node.evaluateMyself();
            }
            return node.nodeScore;
        }
       
        ArrayList<String> possibleMoves = node.getPossibleMoves();  
        
        TreeInterface tmpNode = null;
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
