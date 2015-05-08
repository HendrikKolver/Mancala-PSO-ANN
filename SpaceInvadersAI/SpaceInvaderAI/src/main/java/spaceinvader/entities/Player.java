package spaceinvader.entities;

import java.util.ArrayList;

/**
 *
 * @author Hendrik Kolver
 */
public class Player extends GameObject{
    private ArrayList buildings;
    private ArrayList bullets;
    private int lives;
    private int kills;
    private boolean isAlive;

    @Override
    void updatePosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
