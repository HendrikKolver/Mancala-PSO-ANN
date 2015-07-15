package spaceinvader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.pso.AIPlayer;
import spaceinvader.utilities.InputParser;

/**
 *
 * @author Hendrik Kolver
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {           
        int plyDepth = 6;
        int hiddenLayers = 4;
        String folder = args[0];
        
        NeuralNetwork nn = new NeuralNetwork(6,1,hiddenLayers,1);
//        getWeightsFromFile(nn);
        getWeightsFromString(nn);
       
        
        AIPlayer player = new AIPlayer(plyDepth,nn);
        
        player = InputParser.getState(player, folder);
//        System.out.println("before ----");
//        player.getCurrentPosition().printBoard();
        player.playRound();
//        System.out.println("after (did I screw up? ----");
//        player.getCurrentPosition().printBoard();
//        while(true){
//            player.playRound();
//            player.getCurrentPosition().printBoard();
//        }

        
        String move = player.getMove();
        writeFile(folder,"move.txt",move);
        writeFile(folder,"moveDownRound.txt",String.valueOf(player.getCurrentPosition().getAlienController().moveDownRound));
        System.out.println("Move: "+ move);
        
        

    }
    
    private static void getWeightsFromString(NeuralNetwork nn){
        String weightString = "1.0835870090141984;1.100721920417205;-0.34632701332158905;-0.016156456406784432;0.593774398144852;-1.2236624197712027;-1.418340032440387;-0.9927699946840591;-1.6559847229462572;-1.5791007987248817;-0.17355074291970907;-0.4272730761620364;-1.3263478709106693;0.313501047406877;-1.6620981612332362;-1.3461128661890354;1.186129967701129;-0.7640343823848508;-0.9990123784257794;-1.3650557773057683;-1.3129511427604523;-1.3980498971178041;-0.5168648677115766;-1.2464744633773366;-1.5387798942116515;0.11490054753825499;-0.2982963820170826;-0.28862938004795047;-0.7223584250568469;0.34760724102085083;-0.9842956857292396;-0.20896398585222828;-0.9073410516063893";
        double weights[] = new double[nn.getConnections()];
        String tmp[] = weightString.split(";");
        for(int x=0; x<weights.length;x++)
        {
            weights[x] = Double.parseDouble(tmp[x]);
        }
        nn.updateWeights(weights);
    }
    
    private static void getWeightsFromFile(NeuralNetwork nn){
        double weights[] = new double[nn.getConnections()];
        List<String> lines;
        
        //read from file
        try {
            String name = "6input_beats1stPlaceOn6Ply.txt";//JOptionPane.showInputDialog("Name of file");
            lines = readSmallTextFile(name, ".");
            if(lines.size()<1)
            {
                System.out.println("Cannot, file is empty please train first");
            }
            else
            {
                String tmp[] =lines.get(0).split(";");
                //System.out.println(tmp.length);
                //System.out.println(weights.length);
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
    
    private static List<String> readSmallTextFile(String aFileName, String folder) throws IOException {

        List<String> tmp = new ArrayList<String>();
        BufferedReader inFile = new BufferedReader(new FileReader(folder+"/"+aFileName));    
        String line;
        while((line =inFile.readLine())!= null)
        {
            tmp.add(line);
        }
        inFile.close();

        return tmp;
      }
    
        public static void writeFile(String folder, String aFileName, String aItem) throws IOException {
        Path path = Paths.get(folder, aFileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(aItem);
            writer.newLine();
        }
    }
}
