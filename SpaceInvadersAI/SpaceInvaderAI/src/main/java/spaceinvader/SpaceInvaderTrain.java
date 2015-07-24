package spaceinvader;

import java.io.FileNotFoundException;
import java.io.IOException;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.pso.Trainer;
import spaceinvader.utilities.ThreadPool;

/**
 *
 * @author Hendrik Kolver
 */
public class SpaceInvaderTrain {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException { 
        Trainer trainer = new Trainer();
//        NeuralNetwork nn = trainer.train(50, 3);


        NeuralNetwork nn = trainer.trainWithOpponent(200, 5);

        System.out.println("Done");
        ThreadPool.executor.shutdown();

//        AIPlayer player = new AIPlayer(6,nn);
//            
//        while(!player.isGameOver() && player.getRoundCount() <200){
//            Thread.sleep(200);
//            player.getCurrentPosition().printBoard();
//            player.playRound();
//        }
   
    }
    
    
}
