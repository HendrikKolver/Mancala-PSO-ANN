package spaceinvader.pso;


import java.io.IOException;
import java.util.Random;
import javax.swing.JOptionPane;
import spaceinvader.neuralNetwork.NeuralNetwork;

/**
 *
 * @author Hendrik Kolver
 */
public class Trainer {
    int particleCount;
    
    public Trainer()
    {    
    }
    
    public NeuralNetwork train(int iterations, int plyDepth) throws IOException, InterruptedException
    {
        int counter = 0;
        double w= 0.72;
        double c1 = 1.4;
        double c2 = 1.4;
        particleCount =50;
        double maxVel =0.3; //Double.parseDouble(JOptionPane.showInputDialog("Max Velocity: "));
        int maxIter = iterations;//Integer.parseInt(JOptionPane.showInputDialog("Number of Iterations: "));
        double uB = 1; //upper bound
        double lB = -1; //lower bound
        int inputs = 6;
        int outputs = 1;
        int hidden = 4;//Integer.parseInt(JOptionPane.showInputDialog("Number of hidden units: "));
        int sigmoid = 1;
        int neighSize = 4;//Integer.parseInt(JOptionPane.showInputDialog("Neighborhood Size: "));
        int gamesPlayedPerPlayerPerRound = 5;
        
       PSO swarmOptimize = new PSO(plyDepth, w, c1,  c2,  particleCount,  maxVel,  maxIter,  uB,  lB,  inputs,  outputs,  hidden,  sigmoid,gamesPlayedPerPlayerPerRound, true);

       swarmOptimize.trainLocal(neighSize);

       
       Particle p =swarmOptimize.winningParticle();
       NeuralNetwork best = p.bestNetwork;
       
       //PrintFinalNN
       swarmOptimize.printTmpFile();
       
       return best;
       
    }
    
    public NeuralNetwork trainWithOpponent(int iterations, int plyDepth) throws IOException, InterruptedException
    {
        int counter = 0;
        double w= 0.72;
        double c1 = 1.4;
        double c2 = 1.4;
        particleCount =30;
        double maxVel =0.2; //Double.parseDouble(JOptionPane.showInputDialog("Max Velocity: "));
        int maxIter = iterations;//Integer.parseInt(JOptionPane.showInputDialog("Number of Iterations: "));
        double uB = 1; //upper bound
        double lB = -1; //lower bound
        int inputs = 11;
        int outputs = 1;
        int hidden = 4;//Integer.parseInt(JOptionPane.showInputDialog("Number of hidden units: "));
        int sigmoid = 0;
        int neighSize = 4;//Integer.parseInt(JOptionPane.showInputDialog("Neighborhood Size: "));
        int global= 0;//Integer.parseInt(JOptionPane.showInputDialog("Global = 1, Local = 0"));
         int gamesPlayedPerPlayerPerRound = 5;
        
       PSO swarmOptimize = new PSO(plyDepth, w, c1,  c2,  particleCount,  maxVel,  maxIter,  uB,  lB,  inputs,  outputs,  hidden,  sigmoid,gamesPlayedPerPlayerPerRound, true);

       swarmOptimize.trainLocal2Player(neighSize); 
       //PrintFinalNN
       swarmOptimize.printTmpFile();
       
       return null;
       
    }
    
    
            
    
}
