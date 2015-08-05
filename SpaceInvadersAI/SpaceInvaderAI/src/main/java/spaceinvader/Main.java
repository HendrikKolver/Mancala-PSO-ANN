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
//        folder = "../"+folder;
        
        NeuralNetwork nn = new NeuralNetwork(6,1,hiddenLayers,1);
//        getWeightsFromFile(nn);
        getWeightsFromString(nn);
       NeuralNetwork backup = new NeuralNetwork(11,1,4,1);
        
        AIPlayer player = new AIPlayer(plyDepth,nn, true, backup);
        
        
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
        String weightString = "1.3304245778335644;-0.5078058770702383;-0.9795415301026527;-1.9609475850375446;-1.8089672704050836;-1.6884567134894413;-1.6280202735158316;-1.5384790517062694;-1.2265360416394044;-1.340457313617934;-1.1503376285268163;-1.9484015298508437;-1.5740718437388737;-1.8996043268678333;-1.8673979751968555;-1.849999343275304;-1.8566975130269474;-1.9782847125715903;-0.7170867803480337;-1.41527846280784;-2.0;-1.9281209768747702;-1.9750124297154819;-1.9803635533462831;-1.9032218794045366;-1.8720616129043295;-1.8509303888833466;-1.8978713124247402;-1.9907976109649774;-1.8752706150359784;-1.5644600138716087;-0.39200195008634603;-1.9875634842804983";
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
