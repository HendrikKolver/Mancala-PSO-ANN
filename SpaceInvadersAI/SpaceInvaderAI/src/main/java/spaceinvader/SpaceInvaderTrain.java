package spaceinvader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.pso.AIPlayer;
import spaceinvader.pso.Trainer;

/**
 *
 * @author Hendrik Kolver
 */
public class SpaceInvaderTrain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException { 
        Trainer trainer = new Trainer();
        NeuralNetwork nn = trainer.train(200, 5);
//        AIPlayer player = new AIPlayer(6,nn);
//            
//        while(!player.isGameOver() && player.getRoundCount() <200){
//            Thread.sleep(200);
//            player.getCurrentPosition().printBoard();
//            player.playRound();
//        }
   
    }
    
    
}
