package spaceinvader;

import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;

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
        long startTime = System.currentTimeMillis();

        while(roundCounter<200){
            Thread.sleep(400);
            roundCounter++;
            waveRoundCounter++;
            
            if(waveRoundCounter ==40){
                alienController.increaseWaveSize();
                waveRoundCounter=0;
            }
            
           // alienController.printAllAliens();
            BulletController.getInstance().printAllBullets();
            BulletController.getInstance().update();
            alienController.update(roundCounter);  
        }
        
        long endTime = System.currentTimeMillis();

        long duration = (endTime - startTime);
        System.out.println("Total time: "+duration);
        
    }
    
}
