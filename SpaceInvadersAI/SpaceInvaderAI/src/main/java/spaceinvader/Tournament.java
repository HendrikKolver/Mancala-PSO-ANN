package spaceinvader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.entities.TournamentPlayer;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.pso.AIPlayer;
import spaceinvader.utilities.InputParser;
import spaceinvader.utilities.RandomGenerator;
import spaceinvader.utilities.ThreadPool;

/**
 *
 * @author Hendrik Kolver
 */
public class Tournament {
/**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws InterruptedException, FileNotFoundException, IOException {           
        

        ArrayList<TournamentPlayer> players = new ArrayList();
       
        players.add(new TournamentPlayer("Agressive Non-aggresive trained solution",true,6,4,"potential_new_winner.txt"));
        players.add(new TournamentPlayer("Non-aggresive trained solution",false,6,4,"potential_new_winner.txt"));
        players.add(new TournamentPlayer("Aggresive trained solution",true,11,4,"11input35roundTrain.txt"));
        players.add(new TournamentPlayer("Non-aggresive Aggresive trained solution",false,11,4,"11input35roundTrain.txt"));
//        players.add(new TournamentPlayer("Non-aggresive random",false,6,4,false));
//        players.add(new TournamentPlayer("Non-aggresive normal eval",false,6,4,true));
        players.add(new TournamentPlayer("Aggresive random",true,6,4,false));
//        players.add(new TournamentPlayer("Aggresive normal eval",true,6,4,true));
        int plyDepth = 6;
        boolean playAgainstSelf = true;
        int tournamentsToPlay = 5;
        int counter = 0;
        double totalGames = ((players.size() * players.size())*2)*tournamentsToPlay;
        
        for (int i = 0; i < tournamentsToPlay; i++) {
            counter = runTournament(players, plyDepth, totalGames, counter, playAgainstSelf);
        }
        
        System.out.println("----------Tournament summary----------");
        System.out.println("Player Name                              \t Wins \t Loss \t Ties \t Score");
        for(TournamentPlayer tPlayer : players){
            System.out.println(tPlayer.getPlayerName() + " \t   " + tPlayer.getWins() + " \t " + tPlayer.getLosses() + " \t " + tPlayer.getTies() + " \t " + tPlayer.getScore());
        }
        System.out.println("--------------------------------------");
        ThreadPool.executor.shutdown();
    }

    private static int runTournament(ArrayList<TournamentPlayer> players, int plyDepth, double totalGames, int counter, boolean playAgainstSelf) throws InterruptedException {
        for(TournamentPlayer tPlayer : players){
            for(TournamentPlayer tOpponent : players){
                if(!playAgainstSelf && tPlayer == tOpponent){
                    break;
                }
                //Play as player 1
                AIPlayer player1 = new AIPlayer(plyDepth,tPlayer.getNeuralNetwork(), tPlayer.isAggresiveStrategy());
                AIPlayer player2 = new AIPlayer(plyDepth,tOpponent.getNeuralNetwork(), tOpponent.isAggresiveStrategy());
                
                while(true)
                { 
                    if(player1.isGameOver() || player2.isGameOver())
                    {
                        if(player1.getRoundCount() >=200 && player1.getKillCount() > player2.getKillCount()){
                            tPlayer.winGame();
                            tOpponent.loseGame();
                        }else if(player1.isGameOver() == player2.isGameOver()
                                && player1.getKillCount() > player2.getKillCount()){
                            tPlayer.winGame();
                            tOpponent.loseGame();
                        }else if(!player1.isGameOver()){
                            tPlayer.winGame();
                            tOpponent.loseGame();
                        }else if(player1.getKillCount() == player2.getKillCount() && player1.getRoundCount() >=200){
                            tPlayer.tieGame();
                            tOpponent.tieGame();
                        }else{
                            tPlayer.loseGame();
                            tOpponent.winGame();
                        }
                        break;
                    }
                    player1.playRound();
                    player2.playRound();
                    syncBoards(player1, player2);
                }
                
                counter++;
                System.out.println("PercentageComplete: "+ Math.round((counter/totalGames)*100.0) + "%");
                
                //Play as player 2
                player1 = new AIPlayer(plyDepth,tOpponent.getNeuralNetwork(), tOpponent.isAggresiveStrategy());
                player2 = new AIPlayer(plyDepth,tPlayer.getNeuralNetwork(), tPlayer.isAggresiveStrategy());
                
                while(true)
                { 
                    if(player1.isGameOver() || player2.isGameOver())
                    {
                        if(player1.getRoundCount() >=200 && player1.getKillCount() > player2.getKillCount()){
                            tOpponent.winGame();
                            tPlayer.loseGame();
                        }else if(player1.isGameOver() == player2.isGameOver()
                                && player1.getKillCount() > player2.getKillCount()){
                            tOpponent.winGame();
                            tPlayer.loseGame();
                        }else if(!player1.isGameOver()){
                            tOpponent.winGame();
                            tPlayer.loseGame();
                        }else if(player1.getKillCount() == player2.getKillCount() && player1.getRoundCount() >=200){
                            tOpponent.tieGame();
                            tPlayer.tieGame();
                        }else{
                            tOpponent.loseGame();
                            tPlayer.winGame();
                        }
                        break;
                    }
                    player1.playRound();
                    player2.playRound();
                    syncBoards(player1, player2);
                }
                counter++;
                System.out.println("PercentageComplete: "+ Math.round((counter/totalGames)*100.0) + "%");
            }
        }
        return counter;
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