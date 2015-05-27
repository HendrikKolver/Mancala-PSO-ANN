package spaceinvader.pso;

import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.treeBuilder.TreeBuilder;
import spaceinvader.treeBuilder.TreeComposite;
import spaceinvader.treeBuilder.TreeInterface;

/**
 *
 * @author Hendrik Kolver
 */
public class AIPlayer {
    private int plyDepth;
    private NeuralNetwork neuralNetwork;
    private int roundCount;
    private TreeInterface currentPosition;
    
    public AIPlayer(int plyDepth, NeuralNetwork neuralNetwork){
        this.plyDepth = plyDepth;
        this.neuralNetwork = neuralNetwork;
        this.roundCount = 1;
        currentPosition = new TreeComposite(neuralNetwork, roundCount);
    }
    
    public void playRound() throws InterruptedException{
        TreeBuilder treeBuilder = new TreeBuilder(plyDepth);
        currentPosition = treeBuilder.build(currentPosition);
        this.roundCount = currentPosition.roundCount;
    }
    
    public boolean isGameOver(){
        return (currentPosition.isGameOver() || this.roundCount >=200);
    }

    public TreeInterface getCurrentPosition() {
        return currentPosition;
    }
    
    public int getRoundCount(){
        return roundCount;
    }
    
    public int getKillCount(){
        return this.currentPosition.getKillCount();
    }
}
