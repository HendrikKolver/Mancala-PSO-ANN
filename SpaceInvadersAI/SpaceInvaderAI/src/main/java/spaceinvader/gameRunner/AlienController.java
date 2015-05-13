package spaceinvader.gameRunner;

import java.util.ArrayList;
import spaceinvader.entities.Alien;
import spaceinvader.entities.GameObject;

/**
 *
 * @author Hendrik Kolver
 */
public class AlienController {
    ArrayList <Alien> aliens;
    
    public AlienController(){
        aliens = new ArrayList();
        aliens.add(new Alien());
    }
  
    public void update(){
        for (Alien alien : aliens) {
            System.out.println("X Pos: "+ alien.getxPosition() + " Y Pos: "+ alien.getyPosition());
            alien.updatePosition(alien.getMoveDirection());
        }
    }
}
