package spaceinvader.gameRunner;

import java.util.ArrayList;
import spaceinvader.entities.Building;
import spaceinvader.entities.Player;
import spaceinvader.entities.Shield;
import spaceinvader.utilities.RandomGenerator;

/**
 *
 * @author Hendrik Kolver
 */
public class PlayerController {
  private static PlayerController instance = null;
        private Player player;
    
    
    private PlayerController(){
        instance = this;
        player = new Player();
        ArrayList<Shield> shieldBlock1 = player.createShieldBlock(2);
        ArrayList<Shield> shieldBlock2 = player.createShieldBlock(14);
        shieldBlock1.addAll(shieldBlock2);
        player.setShields(shieldBlock1);
    }
    
    public static PlayerController getInstance(){
        if(instance == null){
            instance = new PlayerController();
        }
        
        return instance;
    }
    
    public void update(){
        ArrayList<String> possibleMoves = player.getPossibleMoves();
        
        int moveIndex = RandomGenerator.randInt(0, possibleMoves.size()-1);
        
        //Some AI witchcraft will happen here
        player.makeMove(possibleMoves.get(moveIndex));
    }
    
    public void printPlayerPosition(){
        System.out.println("Player Pos: "+ player.getxPosition());
    }
    
    public int getPlayerPosition(){
        return player.getxPosition();
    }
    
    public ArrayList<Building> getBuildings(){
        return player.getAllBuildings();
    }
    
    public void printBuildingPositions(){
        for(Building building : player.getAllBuildings()){
            System.out.println(building.stringContent());
        }
    }
    
    public ArrayList<Shield> getAllShields(){
        return player.getAllShields();
    }
    
    public void setBuildings(ArrayList<Building> buildings){
        
    }
    
    public void setShields(ArrayList<Shield> shields){
        player.setShields(shields);
    }
    
    
}