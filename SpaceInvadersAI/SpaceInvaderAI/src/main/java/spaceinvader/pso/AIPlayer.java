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
    public boolean normalEval;
    private boolean aggresiveTactic;
    
    public AIPlayer(int plyDepth, NeuralNetwork neuralNetwork, boolean aggresiveTactic, NeuralNetwork backup){
        this.aggresiveTactic = aggresiveTactic;
        this.plyDepth = plyDepth;
        this.neuralNetwork = neuralNetwork;
        this.roundCount = 0;
        currentPosition = new TreeComposite(neuralNetwork, roundCount, aggresiveTactic, backup);
        normalEval = false;
    }
    
    public void playRound() throws InterruptedException{
        TreeBuilder treeBuilder = new TreeBuilder(plyDepth);
        treeBuilder.normalEval = this.normalEval;
        currentPosition = treeBuilder.build(currentPosition, this.aggresiveTactic);
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
    
    public String getMove(){
        return this.currentPosition.getMove();
    }
}
