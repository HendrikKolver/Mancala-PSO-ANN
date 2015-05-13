package spaceinvader.gameRunner;

import java.util.ArrayList;
import spaceinvader.entities.Player;
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
    
    
}
