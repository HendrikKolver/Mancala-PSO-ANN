package spaceinvader.treeBuilder;

import java.util.ArrayList;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.gameRunner.PlayerController;
import spaceinvader.neuralNetwork.NeuralNetwork;

/**
 *
 * @author Hendrik Kolver
 */
public abstract class TreeInterface {
    protected TreeInterface children;
    
    protected TreeInterface next;
    protected double nodeScore;
    protected int nodeDepth;
    protected boolean finalState;
    public int roundCount;
    protected String move;
    
    protected AlienController alienController;
    protected PlayerController playerController;
    protected BulletController bulletController;
    
    public NeuralNetwork evaluation;
    
    protected abstract TreeInterface clone();
    abstract void nextMove(String move, int roundCounter);
    abstract ArrayList<String> getPossibleMoves();
    abstract TreeInterface getNext();
    abstract void setNext(TreeInterface newNode);
    abstract void addChild(TreeInterface node);
    abstract void evaluateMyself();
    abstract void evaluateMyself(boolean normal);
    public abstract boolean isGameOver();
    public abstract void printBoard();
    public abstract int boardFinalRating();
    public abstract int getKillCount();
    public abstract String getMove();
    public abstract PlayerController getPlayerController();
    public abstract BulletController getBulletController();
    public abstract AlienController getAlienController();
    
    
     
}

