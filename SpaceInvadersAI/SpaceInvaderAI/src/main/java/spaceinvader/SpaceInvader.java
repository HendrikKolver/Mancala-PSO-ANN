package spaceinvader;

import spaceinvader.gameRunner.AlienController;

/**
 *
 * @author Hendrik Kolver
 */
public class SpaceInvader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        AlienController alienController = new AlienController();
        while(true){
            Thread.sleep(500);
            alienController.update();
        }
    }
    
}
