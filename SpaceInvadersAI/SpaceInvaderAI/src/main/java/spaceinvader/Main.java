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
        int plyDepth = 8;
        int hiddenLayers = 16;
        
        NeuralNetwork nn = new NeuralNetwork(10,1,hiddenLayers,1);
        getWeightsFromFile(nn);
       
        AIPlayer player = new AIPlayer(plyDepth,nn);
        
        player = InputParser.getState(player);
        
//        player.getCurrentPosition().printBoard();
        player.playRound();
//         player.getCurrentPosition().printBoard();
        

        
        String move = player.getMove();
        writeFile("output","move.txt",move);
        //System.out.println("Move: "+ move);
        
        

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
    
        public static void writeFile(String folder, String aFileName, String aItem) throws IOException {
        Path path = Paths.get(folder, aFileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(aItem);
            writer.newLine();
        }
    }
}
