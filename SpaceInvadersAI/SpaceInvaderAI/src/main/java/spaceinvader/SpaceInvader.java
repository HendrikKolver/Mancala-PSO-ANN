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
        int roundCounter = 0;
        int waveRoundCounter = 0;
        while(true){
            Thread.sleep(400);
            roundCounter++;
            waveRoundCounter++;
            
            if(waveRoundCounter ==40){
                alienController.increaseWaveSize();
                waveRoundCounter=0;
            }
            
            alienController.printAllAliens();
            alienController.update();
            
            
        }
    }
    
}
