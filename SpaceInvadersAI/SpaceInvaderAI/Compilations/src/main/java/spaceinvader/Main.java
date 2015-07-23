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
        int plyDepth = 7;
        int hiddenLayers = 4;
        String folder = args[0];
//        folder = "../"+folder;
        
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
        String weightString = "-1.206295264583534;-0.09731958109925025;-0.4513497395296258;0.5372713521701551;-1.1820585586127879;-1.7158446333491442;-1.5912161687396926;-1.7188118879611307;-1.0148602537137048;-1.5557082803713245;-1.1818104545742671;-1.698437523774525;-1.5337929994991053;-1.7297046781273269;-1.922290954740561;-1.764772071403943;-1.0178144942986416;-1.68925549204179;-1.9128511745868684;-1.0693028108837608;-1.8724053553422464;-1.7610564366256778;-0.8405408104173866;-1.2873809168377042;-1.7287257002618428;-1.3491464703271854;-1.1544620524593827;-1.4382231731436672;-0.10566603968718757;-1.5505342689529076;0.25384959704590876;-1.8295522940964355;-0.9290507897729192";
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
