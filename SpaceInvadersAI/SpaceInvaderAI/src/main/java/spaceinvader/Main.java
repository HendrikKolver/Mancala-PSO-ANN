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
        
        NeuralNetwork nn = new NeuralNetwork(6,1,hiddenLayers,1);
//        getWeightsFromFile(nn);
        getWeightsFromString(nn);
       
        
        AIPlayer player = new AIPlayer(plyDepth,nn);
        
        player = InputParser.getState(player);
        System.out.println("before ----");
        player.getCurrentPosition().printBoard();
        player.playRound();
        System.out.println("asfter (did I screw up? ----");
        player.getCurrentPosition().printBoard();
//        while(true){
//            player.playRound();
//            player.getCurrentPosition().printBoard();
//        }

        
        String move = player.getMove();
        writeFile("output","move.txt",move);
        //System.out.println("Move: "+ move);
        
        

    }
    
    private static void getWeightsFromString(NeuralNetwork nn){
        String weightString = "1.0512049441314197;0.5664481645730253;-0.6886893547457091;0.6519060457448363;-0.6876862913637966;-1.9151817534781377;-1.4686963428553679;-1.9711547031074619;-0.533682209588541;-0.6246842824018869;-0.5068320592982384;-1.1135138192144967;-1.8480627070038853;-1.4031307172700507;-0.6303307566979858;-1.33474113203369;-0.9932558603000936;-1.456051370672671;0.41828735325896654;0.0152195373252979;-0.6632907098822571;-1.8838642539609576;-1.0012679709214027;-0.7430746870327497;-0.9050837159024673;-0.6095380373200011;-0.780769986186697;-0.25075300256258337;-0.38868015222749064;-0.39254203868982424;-0.45380683786074955;-0.4872547749197802;-1.3278193131702845";
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
