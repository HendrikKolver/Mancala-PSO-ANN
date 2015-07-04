package spaceinvader.utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import spaceinvader.entities.Alien;
import spaceinvader.entities.AlienBullet;
import spaceinvader.entities.AlienFactory;
import spaceinvader.entities.BulletFactory;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.Player;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.entities.Shield;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.gameRunner.PlayerController;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.pso.AIPlayer;

/**
 *
 * @author Hendrik Kolver
 */
public class InputParser {
    
    
    public static AIPlayer getState(AIPlayer aiPlayer){
        return setGameState(aiPlayer);
    }
    
    public static void getBoard() throws IOException{
        String board = getBoardFromFile();
        System.out.println(board);
    }
    
    private static String getBoardFromFile() throws IOException{
        List<String> jsonList = readSmallTextFile("output/state.json");
        String jsonString = jsonList.toString();
        return jsonString;
    }
    
    private static AIPlayer setGameState(AIPlayer aiPlayer){
        JSONParser parser = new JSONParser();
 
        try {
 
            Object obj = parser.parse(new FileReader("output/state.json"));
 
            JSONObject jsonObject = (JSONObject) obj;
            
            long RoundNumber = (long) jsonObject.get("RoundNumber");
            JSONArray players = (JSONArray) jsonObject.get("Players");
            JSONObject player = (JSONObject)players.get(0);
            JSONObject player2 = (JSONObject)players.get(1);
            long waveSize = (long) player2.get("AlienWaveSize");
            JSONArray bullets = (JSONArray) player.get("Missiles");
            JSONArray enemyBullets = (JSONArray) player2.get("Missiles");
            long realPlayerNumber = (long) player.get("PlayerNumberReal");
            
            
            aiPlayer.getCurrentPosition().roundCount = (int) RoundNumber;
            BulletController bulletFactory = aiPlayer.getCurrentPosition().getBulletController();
            AlienController alienController = aiPlayer.getCurrentPosition().getAlienController();
            alienController.setWaveSize((int) waveSize);
            
            ArrayList<GameObject> shields = setupPlayer(player, aiPlayer);
            setupPlayerBullets(bullets, bulletFactory);
            setupEnemyBullets(enemyBullets,bulletFactory);
            setupMapObjects(jsonObject, shields, bulletFactory, alienController, realPlayerNumber);
            
            
            
            
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return aiPlayer;
        
    }

    private static void setupPlayerBullets(JSONArray bullets, BulletController bulletFactory) {
        Iterator<JSONObject> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            JSONObject bullet = bulletIterator.next();
            if(bullet.get("Alive").equals(true)){
                PlayerBullet playerBullet = new PlayerBullet((int)(long)bullet.get("X"),24-(int)(long)bullet.get("Y"));
                bulletFactory.addPlayerBullet(playerBullet);
            }
        }
    }
    
    private static void setupEnemyBullets(JSONArray bullets, BulletController bulletFactory) {
        Iterator<JSONObject> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            JSONObject bullet = bulletIterator.next();
            if(bullet.get("Alive").equals(true)){
                PlayerBullet playerBullet = new PlayerBullet((int)(long)bullet.get("X"),24-(int)(long)bullet.get("Y"),2);
                bulletFactory.addEnemyBullet(playerBullet);
            }
        }
    }

    private static void setupMapObjects(JSONObject jsonObject, ArrayList<GameObject> shields, BulletController bulletFactory, AlienController alienController, long realPlayerNumber) {
        alienController.getAllAliens().removeAll(alienController.getAllAliens());
        JSONObject map = (JSONObject) jsonObject.get("Map");
        JSONArray rows = (JSONArray) map.get("Rows");
        Iterator<JSONArray> rowsIterator = rows.iterator();
        while (rowsIterator.hasNext()) {
            Iterator<JSONObject> rowIterator = rowsIterator.next().iterator();
            while (rowIterator.hasNext()) {
                JSONObject gameObject = rowIterator.next();
                if(gameObject != null){
                    if(gameObject.get("Type").equals("Shield") && gameObject.get("PlayerNumber").equals((long)1)){
                        Shield shield = new Shield((int)(long)gameObject.get("X"),24-(int)(long)gameObject.get("Y"),1,1);
                        shields.add(shield);
                    }
                    
                    if(gameObject.get("Type").equals("Bullet") && gameObject.get("PlayerNumber").equals((long)2)){
                        AlienBullet alienBullet = new AlienBullet((int)(long)gameObject.get("X"),24-(int)(long)gameObject.get("Y"));
                        bulletFactory.addAlienBullet(alienBullet);
                    }
                    
                    if(gameObject.get("Type").equals("Alien") && gameObject.get("PlayerNumber").equals((long)2)){
                        Alien alien = new Alien((int)(long)gameObject.get("X"),24-(int)(long)gameObject.get("Y"));
                        ArrayList<ArrayList<Alien>> alienRows = alienController.getAllAliens();
                        ArrayList<Alien> latestRow;
                        if(alienRows.isEmpty()){
                            latestRow = new ArrayList();
                            alienRows.add(latestRow);       
                        }else{
                            latestRow = alienRows.get(alienRows.size()-1);
                        }
                        if(realPlayerNumber == 1){
                           if(alien.getyPosition() % 2 == 0){
                                alien.invertMoveDirection();
                            } 
                        }else{
                          if(alien.getyPosition() % 2 != 0){
                                alien.invertMoveDirection();
                            }   
                        }
                        
                        
                        if(!latestRow.isEmpty() && (alien.getyPosition() == latestRow.get(0).getyPosition())){
                            latestRow.add(alien);
                        }else if(latestRow.isEmpty()){
                            latestRow.add(alien); 
                        }else{
                            latestRow = new ArrayList();
                            alienRows.add(latestRow);
                            latestRow.add(alien);
                        }
                        Collections.reverse(alienRows);
                        alienController.setLatestRowAliens(alienRows.get(alienRows.size()-1));
                    }
                }
            }
        }
    }

    private static ArrayList<GameObject> setupPlayer(JSONObject player, AIPlayer aiPlayer) {
        JSONObject playerShip = (JSONObject) player.get("Ship");
        long kills = (long) player.get("Kills");
        long lives = (long) player.get("Lives");
        long respawnTimer = (long) player.get("RespawnTimer");
        long bulletLimit = (long) player.get("MissileLimit");
        JSONObject alienFactory = (JSONObject) player.get("AlienFactory");
        JSONObject bulletFactory = (JSONObject) player.get("MissileController");
        Player playerObj = aiPlayer.getCurrentPosition().getPlayerController().getPlayer();
        PlayerController playerController = aiPlayer.getCurrentPosition().getPlayerController();
        playerObj.setBulletLimit((int) bulletLimit);
        playerObj.setIsAlive((respawnTimer<=0));
        playerController.setRespawnCounter((int) respawnTimer);
        playerObj.setKills((int) kills);
        playerObj.setLives((int) lives);
        playerObj.setxPosition((int) (long) playerShip.get("X"));
        playerObj.setyPosition((int) (24- (long) playerShip.get("Y")));
        ArrayList<GameObject> shields = playerObj.getAllShields();
        ArrayList<GameObject> buildings = playerObj.getAllBuildings();
        buildings.removeAll(buildings);
        shields.removeAll(shields);
        if(alienFactory != null && (boolean)alienFactory.get("Alive")){
            AlienFactory factory = new AlienFactory((int)(long)alienFactory.get("X"),24-(int)(long)alienFactory.get("Y"),3,1);
            buildings.add(factory);
        }
        if(bulletFactory != null && (boolean)bulletFactory.get("Alive")){
            BulletFactory factory = new BulletFactory((int)(long)bulletFactory.get("X"),24-(int)(long)bulletFactory.get("Y"),3,1);
            buildings.add(factory);
        }
        return shields;
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
    
     public static void getWeightsFromString(NeuralNetwork nn){
        String weightString = "1.0512049441314197;0.5664481645730253;-0.6886893547457091;0.6519060457448363;-0.6876862913637966;-1.9151817534781377;-1.4686963428553679;-1.9711547031074619;-0.533682209588541;-0.6246842824018869;-0.5068320592982384;-1.1135138192144967;-1.8480627070038853;-1.4031307172700507;-0.6303307566979858;-1.33474113203369;-0.9932558603000936;-1.456051370672671;0.41828735325896654;0.0152195373252979;-0.6632907098822571;-1.8838642539609576;-1.0012679709214027;-0.7430746870327497;-0.9050837159024673;-0.6095380373200011;-0.780769986186697;-0.25075300256258337;-0.38868015222749064;-0.39254203868982424;-0.45380683786074955;-0.4872547749197802;-1.3278193131702845";
        double weights[] = new double[nn.getConnections()];
        String tmp[] = weightString.split(";");
        for(int x=0; x<weights.length;x++)
        {
            weights[x] = Double.parseDouble(tmp[x]);
        }
        nn.updateWeights(weights);
    }
    
    
    public static void getWeightsFromFile(NeuralNetwork nn, String filename) throws IOException{
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
}
