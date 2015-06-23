package spaceinvader.pso;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.utilities.InputParser;


/**
 *
 * @author Hendrik Kolver
 */
public class Particle {
    public NeuralNetwork neuralNetwork;
    public NeuralNetwork bestNetwork;
    public int dimensions;
    public double position[];
    public double inputs;
    public double outputs;
    public double personalBest[];
    public double momentum[];
    public double personalBestFitness;
    public ArrayList pos;
    public ArrayList pBest;
    public int sigmoid;

    
    public double wins;
    public double losses;
    public double ties;
    public double bestWins;
    public double bestLosses;
    public double bestTies;
  
    
    public Particle(int inputs, int outputs, int hidden, int sigmoid)
    {
        

        this.inputs = inputs;
        this.outputs = outputs;

        wins = 0;
        losses = 0;
        ties = 0;
        bestWins = 0;
        bestLosses = 0;
        bestTies = 0;

        neuralNetwork = new NeuralNetwork(inputs,outputs,hidden,sigmoid);
        bestNetwork = new NeuralNetwork(inputs,outputs,hidden,sigmoid);
        dimensions = neuralNetwork.getConnections();
        //dimensions = inputs+hidden;
        position = new double[dimensions];
        personalBest = new double[dimensions];
        personalBestFitness=10000000000000000.0;
        momentum = new double[dimensions];
        pos = new ArrayList();
        this.sigmoid = sigmoid;
   
    }
    
        public double avgMomentum()
    {
        double total = 0;
       for(int x=0; x<dimensions;x++)
        {
            total+= momentum[x];
        }
       
       total /=dimensions;
       return total;
    }
    
    public void initParticle(double lowerBound, double upperBound)
    {
        Random r = new Random();
        //System.out.println("init weights!--------------------\n");
        for(int x=0; x<dimensions;x++)
        {
            double randomValue = lowerBound + (upperBound - lowerBound) * r.nextDouble();
            position[x] = randomValue;
            personalBest[x] = randomValue;
            momentum[x] = 0;
        }
        
        updateWeights();
       particleBestUpdate();
    }
    
    public void initParticle(String fileName) throws IOException
    {
        InputParser.getWeightsFromFile(neuralNetwork, fileName);
        InputParser.getWeightsFromFile(bestNetwork, fileName);
    }

    
    public void updateWeights()
    {
       neuralNetwork.updateWeights(position); 
    }
    
    public void externalWeightUpdate(double[] w)
    {
        neuralNetwork.updateWeights(w); 
    }
    
    public void particleBestUpdate()
    {
        
        bestNetwork.updateWeights(position); 
    }

    public double fitness()
    {
        
       double finalAnswer = 0;
       finalAnswer = ((wins+ties)-losses);
      //System.out.println("finalAnswer: " +finalAnswer);
        return finalAnswer;
    }
    
    public double bestFitness()
    {  
       double finalAnswer = 0;
       finalAnswer = ((bestWins+bestTies)-bestLosses);
       // System.out.println("finalAnswerBest: " +finalAnswer);
        return finalAnswer;
    }
 
}
