package spaceinvader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import spaceinvader.entities.AlienFactory;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.pso.AIPlayer;

/**
 *
 * @author Hendrik Kolver
 */
public class SpaceInvader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {           
        int plyDepth = 2;
        int hiddenLayers = 16;
        
        NeuralNetwork nnp1 = new NeuralNetwork(10,1,hiddenLayers,1);
        NeuralNetwork nnp2 = new NeuralNetwork(10,1,hiddenLayers,1);
//        setRandomWeights(nnp2);
//        setRandomWeights(nnp1);
        getWeightsFromFile(nnp2,"tmpFile.txt");
        getWeightsFromFile(nnp1,"p2.txt");
       
        
        int totalRoundCountp1 = 0;
        int totalKillCountp1 = 0;
        int totalRoundCountp2 = 0;
        int totalKillCountp2 = 0;
        int winsp1 = 0;
        int winsp2 = 0;
        int ties = 0;
        
        double gamesToPlay = 100.0;
        
        double start = System.currentTimeMillis();
        
        for (int i = 0; i < gamesToPlay; i++) {
            AIPlayer player1 = new AIPlayer(plyDepth,nnp1);
            AIPlayer player2 = new AIPlayer(plyDepth,nnp2);
            while(true)
            { 
                //sleep(200);
                //System.in.read();
                if(player1.isGameOver() || player2.isGameOver())
                {
                    if(!player2.isGameOver()){
                        winsp2++;
                    }
                    else if(!player1.isGameOver()){
                        winsp1++;
                    }else{
                        ties++;
                    }
                    break;
                }
//                System.out.println("P1 board---------------");
//                player1.getCurrentPosition().printBoard();
//                System.out.println("P2 board---------------");
//                player2.getCurrentPosition().printBoard();
                player1.playRound();
                player2.playRound();
                syncBoards(player1, player2);
                
            }
            totalRoundCountp1 += player1.getRoundCount();
            totalKillCountp1 += player1.getKillCount();
            totalRoundCountp2 += player2.getRoundCount();
            totalKillCountp2 += player2.getKillCount();
            System.out.println("Game: "+ i + "\r");
        }
        System.out.println("Average round count p1: "+ (totalRoundCountp1/gamesToPlay));
        System.out.println("Average kill count p1: "+ (totalKillCountp1/gamesToPlay));
        System.out.println("Average round count p2: "+ (totalRoundCountp2/gamesToPlay));
        System.out.println("Average kill count p2: "+ (totalKillCountp2/gamesToPlay));
        System.out.println("Wins p1: "+ (winsp1));
        System.out.println("Wins p2: "+ (winsp2));
        System.out.println("ties: "+ (ties));
        double end = System.currentTimeMillis();
        System.out.println("Total time: "+ (end-start));
    }
    
    
    //TODO consolidate this and the copy of this function in PSO.java
    private static void syncBoards(AIPlayer player1, AIPlayer player2){
        BulletController p1Controller = player1.getCurrentPosition().getBulletController();
        BulletController p2Controller = player2.getCurrentPosition().getBulletController();
        
        ArrayList<GameObject> p1Bullets = p1Controller.getPlayerBulletList();
        ArrayList<GameObject> p2Bullets = p2Controller.getPlayerBulletList();
        
        Iterator p1Bullet = p1Bullets.iterator();
        while(p1Bullet.hasNext()){
            GameObject bullet = (GameObject) p1Bullet.next();
            if (bullet.getyPosition() == 12 && bullet.getPlayer() == 1){
                bullet.generateObjectID();
               PlayerBullet newBullet = new PlayerBullet(18-bullet.getxPosition(),12,2);
               newBullet.setObjectID(bullet.getObjectID());
               p2Controller.addEnemyBullet(newBullet);
            }
        }

        Iterator p2Bullet = p2Bullets.iterator();
        while(p2Bullet.hasNext()){
            GameObject bullet = (GameObject) p2Bullet.next();
            if (bullet.getyPosition() == 12 && bullet.getPlayer() == 1){
                bullet.generateObjectID();
                PlayerBullet newBullet = new PlayerBullet(18-bullet.getxPosition(),12,2);
                newBullet.setObjectID(bullet.getObjectID());
                p1Controller.addEnemyBullet(newBullet);
            }
        }

        AlienController p1AlienController = player1.getCurrentPosition().getAlienController();
        AlienController p2AlienController = player2.getCurrentPosition().getAlienController();
        ArrayList<GameObject> p1Buildings = player1.getCurrentPosition().getPlayerController().getBuildings();
        ArrayList<GameObject> p2Buildings = player2.getCurrentPosition().getPlayerController().getBuildings();
        int counter = 0;
         
        //Checking the representation because I stuffed up the polymorphism. Should have created seperate lists for the objects or I
        //Should rectify the deep copy function. Because the arrayCopy function is templatized it casts the objects to instances of GameObject and kills their type 
        for(GameObject building : p1Buildings){
            if(building.getRepresentation().equals("X")){
                counter++;
            }
        }
         
        if (player1.getRoundCount() >=40){
            p2AlienController.setWaveSize(4+counter);
        }else {
            p2AlienController.setWaveSize(3+counter);
        }
        
        counter = 0;
        
        for(GameObject building : p2Buildings){
            if(building.getRepresentation().equals("X")){
                counter++;
            }
        }
         
        if (player2.getRoundCount() >=40){
            p1AlienController.setWaveSize(4+counter);
        }else {
            p1AlienController.setWaveSize(3+counter);
        }
        
        //Remove the enemy bullet once it is destroyed so that the player that fired it can fire again
        ArrayList p1IdsToRemove = p1Controller.getBulletIdsToRemove();
        ArrayList p2IdsToRemove = p2Controller.getBulletIdsToRemove();
        
        for(Object id : p1IdsToRemove){
            Iterator p1Iterator = p2Bullets.iterator();
            while(p1Iterator.hasNext()){
                
                GameObject bullet = (GameObject) p1Iterator.next();
                if((int)id == bullet.getObjectID()){
                   p1Iterator.remove();    
                } 
            }
        }
        
        for(Object id : p2IdsToRemove){
            Iterator p2Iterator = p1Bullets.iterator();
            while(p2Iterator.hasNext()){
                GameObject bullet = (GameObject) p2Iterator.next();
                if((int)id == bullet.getObjectID()){
                   p2Iterator.remove();    
                } 
            }
        }
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
    
    private static void getWeightsFromFile(NeuralNetwork nn, String filename) throws IOException{
        double weights[] = new double[nn.getConnections()];
        List<String> lines;
        
        //read from file
        try {
            String name = filename;//JOptionPane.showInputDialog("Name of file");
            lines = readSmallTextFile(name);
            if(lines.size()<1)
            {
                throw(new RuntimeException("Error file missing"));
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
            throw(ex);
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
