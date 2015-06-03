package spaceinvader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
        int plyDepth = 6;
        int hiddenLayers = 16;
        
        NeuralNetwork nn = new NeuralNetwork(10,1,hiddenLayers,1);
        //setRandomWeights(nn);
        getWeightsFromFile(nn);
       
        AIPlayer player = new AIPlayer(plyDepth,nn);
        int totalRoundCount = 0;
        int totalKillCount = 0;
        
        double gamesToPlay = 200.0;
        
        double start = System.currentTimeMillis();
        
        for (int i = 0; i < gamesToPlay; i++) {
            
            while(!player.isGameOver() && player.getRoundCount() <200){

               // Thread.sleep(300);
                //player.getCurrentPosition().printBoard();
                player.playRound();
                
            }
            totalRoundCount += player.getRoundCount();
            totalKillCount += player.getKillCount();
            player = new AIPlayer(plyDepth,nn);
            System.out.println("Game: "+ i + "\r");
        }
        System.out.println("Average round count: "+ (totalRoundCount/gamesToPlay));
        System.out.println("Average kill count: "+ (totalKillCount/gamesToPlay));
        double end = System.currentTimeMillis();
        System.out.println("Total time: "+ (end-start));
            
            
            
            
   
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
    
    private static void getWeightsFromFile(NeuralNetwork nn){
        double weights[] = new double[nn.getConnections()];
        List<String> lines;
        
        //read from file
        try {
            String name = "tmpFile.txt";//JOptionPane.showInputDialog("Name of file");
            lines = readSmallTextFile(name);
            if(lines.size()<1)
            {
                System.out.println("Cannot, file is empty please train first");
            }
            else
            {
                String tmp[] =lines.get(0).split(";");
                System.out.println(tmp.length);
                System.out.println(weights.length);
                for(int x=0; x<weights.length;x++)
                {
                    weights[x] = Double.parseDouble(tmp[x]);
                }
                nn.updateWeights(weights);
            }

        } catch (IOException ex) {
            System.out.println("Some file error");
        }
    }
    
    private static List<String> readSmallTextFile(String aFileName) throws IOException {

        List<String> tmp = new ArrayList<String>();
        BufferedReader inFile = new BufferedReader(new FileReader(aFileName));    
        String line;
        while((line =inFile.readLine())!= null)
        {
            tmp.add(line);
        }
        inFile.close();

        return tmp;
      }
}
