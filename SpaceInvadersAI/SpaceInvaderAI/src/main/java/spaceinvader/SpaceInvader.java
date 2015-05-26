package spaceinvader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import spaceinvader.entities.Alien;
import spaceinvader.entities.GameObject;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.gameRunner.PlayerController;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.pso.AIPlayer;
import spaceinvader.treeBuilder.TreeBuilder;
import spaceinvader.treeBuilder.TreeComposite;
import spaceinvader.treeBuilder.TreeInterface;

/**
 *
 * @author Hendrik Kolver
 */
public class SpaceInvader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {           
        NeuralNetwork nn = new NeuralNetwork(10,1,4,1);
        setRandomWeights(nn);
        AIPlayer player = new AIPlayer(6,nn);
            
            while(!player.isGameOver() && player.getRoundCount() <200){

                Thread.sleep(200);
                player.getCurrentPosition().printBoard();
                player.playRound();
                
            }
   
    }
    
    public static void setRandomWeights(NeuralNetwork nn){
        double weights[] = new double[nn.getConnections()];
        for(int x=0;x<nn.getConnections();x++)
        {
            weights[x] = getRandom(1,-1);
        } 
        nn.updateWeights(weights);
    }
    
    public static double getRandom(int upper, int lower){
         Random r = new Random();

        return (lower + (upper - lower) * r.nextDouble());
    }
}
