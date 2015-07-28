package spaceinvader.entities;

import java.io.IOException;
import java.util.Random;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.utilities.InputParser;

/**
 *
 * @author Hendrik Kolver
 */
public class TournamentPlayer {
    private NeuralNetwork nn;
    private boolean aggresiveStrategy;
    private boolean normalEval;
    private String playerName;
    private int wins;
    private int losses;
    private int ties;
    
    public TournamentPlayer(String playerName, boolean aggresiveStrategy, int inputs, int hiddenLayers, String fileName) throws IOException{
        init(playerName, aggresiveStrategy, inputs, hiddenLayers);
        InputParser.getWeightsFromFile(nn,fileName, ".");
    }
    
    public TournamentPlayer(String playerName, boolean aggresiveStrategy, int inputs, int hiddenLayers, boolean normalEval) throws IOException{
        init(playerName, aggresiveStrategy, inputs, hiddenLayers);
        setRandomWeights(nn);
        this.normalEval = normalEval;
    }
    
    private void init(String playerName, boolean aggresiveStrategy, int inputs, int hiddenLayers) {
        this.playerName = playerName;
        int nameLength = this.playerName.length();
        int lengthDifference = 40 - nameLength;
        for (int i = 0; i < lengthDifference; i++) {
           this.playerName+=" ";
        }
        this.aggresiveStrategy = aggresiveStrategy;
        normalEval = false;
        wins = 0;
        losses = 0;
        ties = 0;
        nn = new NeuralNetwork(inputs,1,hiddenLayers,1);
    }

    public NeuralNetwork getNeuralNetwork() {
        return nn;
    }

    public boolean isAggresiveStrategy() {
        return aggresiveStrategy;
    } 

    public boolean isNormalEval() {
        return normalEval;
    }

    public String getPlayerName() {
        return playerName;
    }
    
    public void winGame(){
        this.wins++;
    }
    
    public void loseGame(){
       this.losses++; 
    }
    
    public void tieGame(){
       this.ties++; 
    }
    
    public int getScore(){
        return this.wins-this.losses;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getTies() {
        return ties;
    }
    
    private void setRandomWeights(NeuralNetwork nn){
        double weights[] = new double[nn.getConnections()];
        for(int x=0;x<nn.getConnections();x++)
        {
            weights[x] = getRandom(1,-1);
        } 
        nn.updateWeights(weights);
    }
    
    private static double getRandom(int upper, int lower){
         Random r = new Random();

        return (lower + (upper - lower) * r.nextDouble());
    }
}
