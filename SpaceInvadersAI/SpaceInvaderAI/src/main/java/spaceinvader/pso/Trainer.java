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
        particleCount =60;
        double maxVel =0.3; //Double.parseDouble(JOptionPane.showInputDialog("Max Velocity: "));
        int maxIter = Integer.parseInt(JOptionPane.showInputDialog("Number of Iterations: "));
        double uB = 1;
        double lB = -1;
        int inputs = 8;
        int outputs = 1;
        int hidden = 4;//Integer.parseInt(JOptionPane.showInputDialog("Number of hidden units: "));
        int sigmoid = 1;
        int neighSize = 4;//Integer.parseInt(JOptionPane.showInputDialog("Neighborhood Size: "));
        int global= 0;//Integer.parseInt(JOptionPane.showInputDialog("Global = 1, Local = 0"));
        
       PSO swarmOptimize = new PSO(plyDepth, w, c1,  c2,  particleCount,  maxVel,  maxIter,  uB,  lB,  inputs,  outputs,  hidden,  sigmoid);
       if(global == 1)
       {
           swarmOptimize.train();
       }
       else
       {
           swarmOptimize.trainLocal(neighSize);
       }
       
       Particle p =swarmOptimize.winningParticle();
       NeuralNetwork best = p.bestNetwork;
       
       return best;
       
    }
            
    
}
