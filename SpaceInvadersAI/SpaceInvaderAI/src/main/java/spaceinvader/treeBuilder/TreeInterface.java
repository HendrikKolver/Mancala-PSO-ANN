package spaceinvader.treeBuilder;

import spaceinvader.neuralNetwork.NeuralNetwork;

/**
 *
 * @author Hendrik Kolver
 */
public abstract class TreeInterface {
    protected TreeInterface children;
    protected int boardPosition[][];
    protected TreeInterface next;
    protected double nodeScore;
    protected int player;
    protected int nodeDepth;
    protected int minMaxScore;
    protected boolean finalState;
    protected boolean repeat = false;
    public int alpha, beta;
    public NeuralNetwork evaluation;
    public double fitness;
    
    abstract void setBoard(int[][] newPosition);
    abstract int[][] getBoard();
    abstract TreeInterface getNext();
    abstract void setNext(TreeInterface newNode);
    abstract void setPlayer(int num);
    abstract int getPlayer();
    abstract void addChild(TreeInterface node);
    abstract void evaluateMyself(int gPlayer);
    abstract boolean win(TreeInterface node);
    
     
}

