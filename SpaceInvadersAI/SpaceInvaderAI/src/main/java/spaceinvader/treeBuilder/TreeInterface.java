package spaceinvader.treeBuilder;

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
    public NeuralNetwork evaluation;
    
    abstract TreeInterface getNext();
    abstract void setNext(TreeInterface newNode);
    abstract void addChild(TreeInterface node);
    abstract void evaluateMyself(int gPlayer);
    abstract boolean isGameOver();
    
     
}

