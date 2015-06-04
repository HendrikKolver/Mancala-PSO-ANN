package spaceinvader.pso;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import spaceinvader.entities.AlienFactory;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.neuralNetwork.NeuralNetwork;

/**
 *
 * @author Hendrik Kolver
 */
public class PSO {
    
    public double w;
    public double c1;
    public double c2;
    public int particleCount;
    public double maxVelocity;
    public int numIterations;
    public double upperBound;
    public double lowerBound;
    public int inputs;
    public int outputs;
    public Particle particles[];
    public Particle localBestParticles[];
    public int hidden;
    public double globalBest[];
    public double localBest[][];
    public double globalBestFitness;
    public double localBestFitness[];
    public Particle globalBestParticle;
    public int sigmoid;
    public int tournamentSize ;
    public int plyDepth;
    public double totalProgress;
    private int gamesPlayed;

    public PSO(int p, double w, double c1, double c2, int particalCount, double maxVel, int nIter, double uB, double lB, int inputs, int outputs, int hidden, int sigmoid)
    {

       this.c1 = c1;
       this.c2 = c2;
       this.w = w;
       this.particleCount = particalCount;
       maxVelocity = maxVel;
       numIterations = nIter;
       upperBound = uB;
       lowerBound = lB;
       this.inputs = inputs;
       this.outputs = outputs;
       this.hidden = hidden;
       globalBestFitness = 0.0;
       this.sigmoid = sigmoid;
       tournamentSize = 10;
       plyDepth = p;
       gamesPlayed = 5;
       
  
    }
    
    public void trainLocal2Player(int size) throws InterruptedException, IOException
    {
       //Particle init
       particles = new Particle[particleCount];
       localBestParticles = new Particle[particleCount];
         
       for(int x=0; x<particleCount;x++)
        {
           particles[x] = new Particle(inputs,outputs,hidden,sigmoid);
           particles[x].initParticle(lowerBound,upperBound);
           localBestParticles[x] = new Particle(inputs,outputs,hidden,sigmoid);
           localBestParticles[x].initParticle(lowerBound,upperBound);
        }
        int currentIteration = 0;
        
        //local best values init
        localBest = new double[particleCount][particles[0].dimensions];
        
        for(int x=0;x<localBest.length;x++)
            for(int k =0; k< localBest[x].length;k++)
                localBest[x][k] =1;
        
        //training loop init
        while(currentIteration<numIterations)
        {
            
            System.out.println("Iteration---"+currentIteration);
            
            //initial partical value init for each iteration
            for(int x=0; x< particles.length;x++)
            {
                particles[x].bestLosses =0;
                particles[x].bestWins =0;
                particles[x].bestTies =0;
                particles[x].losses =0;
                particles[x].wins =0;
                particles[x].ties =0;
            }
            
            //Noise value
            Random r = new Random();
            double randomValue = 0 + (1 - 0) * r.nextDouble();
            
            for(int x=0; x<particles.length;x++)
            {  
                for(int i = 0; i<particles.length;i++){
                    if(x != i){                       
                        //creating new "you" players after every round to reset the board
                        AIPlayer you = new AIPlayer(plyDepth,particles[x].neuralNetwork);
                        AIPlayer yourBest = new AIPlayer(plyDepth,particles[x].bestNetwork);

                        AIPlayer opponent = new AIPlayer(plyDepth,particles[i].neuralNetwork);

                        while(true)
                        { 
                            if(you.isGameOver() || opponent.isGameOver())
                            {
                                break;
                            }
                            you.playRound();
                            opponent.playRound();
                            syncBoards(you, opponent);
                        }
                        //updateWins
                        setOpponentWinsNormal(particles[x],you,opponent);

                        //Reset the opponent board for another game
                        opponent = new AIPlayer(plyDepth,particles[i].neuralNetwork);

                        while(true)
                        { 
                            if(yourBest.isGameOver() || opponent.isGameOver())
                            {
                                break;
                            }
                            yourBest.playRound();
                            opponent.playRound();
                            syncBoards(you, opponent);
                        }
                        //updateWins
                        setOpponentWinsBest(particles[x],yourBest, opponent);
                    }

                }

                //updating the personal best
                if(particles[x].bestFitness() < particles[x].fitness())
                {
                    particles[x].personalBestFitness = particles[x].fitness();
                    for(int y=0; y<particles[x].dimensions;y++)
                    {
                        particles[x].personalBest[y] = particles[x].position[y];
                        particles[x].particleBestUpdate();
                    }
                }
            }
            //updating the local best
            localBestCalculate(size);
            
            //calculate new particle velocities
            double particleVelocity[] = new double[particles[0].dimensions];
            for(int x=0; x<particles.length;x++)
            {
                for(int y=0; y<particles[x].dimensions;y++)
                {

                    particleVelocity[y] = ((w*particles[x].avgMomentum())+
                            cognitiveCalculate(randomValue,particles[x].personalBest[y],particles[x].position[y])+ //(c1*randomValue*(particles[x].personalBest[y]-particles[x].position[y]))+
                            //cognitive
                            socialCalculate(randomValue,localBest[x][y],particles[x].position[y])//(c2*randomValue*(globalBest[y]-particles[x].position[y]))
                            );
                            //social

                    if(particleVelocity[y] > maxVelocity)
                    {
                        particleVelocity[y] = maxVelocity;

                    }
                }

                //calculate new positions
                for(int y=0; y<particles[x].dimensions;y++)
                {
                    Random n = new Random();
                    double noise = 0 + ((localBest[x][y]-particles[x].personalBest[y]) - 0) * n.nextDouble();
                    double tmpVal = particles[x].position[y]+(particleVelocity[y]+noise);

                    particles[x].position[y] = tmpVal; 
                    if(particles[x].position[y]>upperBound)
                    {
                        particles[x].position[y] = upperBound;
                        particles[x].momentum[y] =0;
                    }
                    else if(particles[x].position[y]<lowerBound)
                    {
                        particles[x].position[y] = lowerBound;
                        particles[x].momentum[y] =0;  
                    }
                    else
                    {
                        particles[x].momentum[y] = particleVelocity[y];
                    }

                }
                particles[x].updateWeights();
            }
            
            currentIteration++;
            printTmpFile();
        }
    }
    
      public void trainLocal(int size) throws InterruptedException, IOException
    {
       //Particle init
       particles = new Particle[particleCount];
       localBestParticles = new Particle[particleCount];
         
       for(int x=0; x<particleCount;x++)
        {
           particles[x] = new Particle(inputs,outputs,hidden,sigmoid);
           particles[x].initParticle(lowerBound,upperBound);
           localBestParticles[x] = new Particle(inputs,outputs,hidden,sigmoid);
           localBestParticles[x].initParticle(lowerBound,upperBound);
        }
        int currentIteration = 0;
        
        //local best values init
        localBest = new double[particleCount][particles[0].dimensions];
        
        for(int x=0;x<localBest.length;x++)
            for(int k =0; k< localBest[x].length;k++)
                localBest[x][k] =1;
        
        //training loop init
        while(currentIteration<numIterations)
        {
            
            System.out.println("Iteration---"+currentIteration);
            
            //initial partical value init for each iteration
            for(int x=0; x< particles.length;x++)
            {
                particles[x].bestLosses =0;
                particles[x].bestWins =0;
                particles[x].bestTies =0;
                particles[x].losses =0;
                particles[x].wins =0;
                particles[x].ties =0;
            }
            
            
            Random r = new Random();
            double randomValue = 0 + (1 - 0) * r.nextDouble();
            
            for(int x=0; x<particles.length;x++)
            { 
                for(int i = 0; i<this.gamesPlayed;i++){
                    //this will be your players
                    AIPlayer you = new AIPlayer(plyDepth,particles[x].neuralNetwork);
                    AIPlayer yourBest = new AIPlayer(plyDepth,particles[x].bestNetwork);


                    while(true)
                    { 
                        if(you.isGameOver())
                        {
                            break;
                        }
                        you.playRound();
                    }
                    //updateWins
                    setWinsNormal(particles[x],you);

                    //play 5

                    while(true)
                    { 
                        if(yourBest.isGameOver())
                        {
                            break;
                        }
                        yourBest.playRound();
                    }
                    //updateWins
                    setWinsBest(particles[x],yourBest);
                }

                //updating the personal best
                if(particles[x].bestFitness() < particles[x].fitness())
                {
                    particles[x].personalBestFitness = particles[x].fitness();
                    for(int y=0; y<particles[x].dimensions;y++)
                    {
                        particles[x].personalBest[y] = particles[x].position[y];
                        particles[x].particleBestUpdate();
                    }
                }
            }
            //updating the local best
            localBestCalculate(size);
            
            //calculate new particle velocities
            double particleVelocity[] = new double[particles[0].dimensions];
            for(int x=0; x<particles.length;x++)
            {
                for(int y=0; y<particles[x].dimensions;y++)
                {

                    particleVelocity[y] = ((w*particles[x].avgMomentum())+
                            cognitiveCalculate(randomValue,particles[x].personalBest[y],particles[x].position[y])+ //(c1*randomValue*(particles[x].personalBest[y]-particles[x].position[y]))+
                            //cognitive
                            socialCalculate(randomValue,localBest[x][y],particles[x].position[y])//(c2*randomValue*(globalBest[y]-particles[x].position[y]))
                            );
                            //social

                    if(particleVelocity[y] > maxVelocity)
                    {
                        particleVelocity[y] = maxVelocity;

                    }
                }

                //calculate new positions
                for(int y=0; y<particles[x].dimensions;y++)
                {
                    Random n = new Random();
                    double noise = 0 + ((localBest[x][y]-particles[x].personalBest[y]) - 0) * n.nextDouble();
                    double tmpVal = particles[x].position[y]+(particleVelocity[y]+noise);

                    particles[x].position[y] = tmpVal; 
                    if(particles[x].position[y]>upperBound)
                    {
                        particles[x].position[y] = upperBound;
                        particles[x].momentum[y] =0;
                    }
                    else if(particles[x].position[y]<lowerBound)
                    {
                        particles[x].position[y] = lowerBound;
                        particles[x].momentum[y] =0;  
                    }
                    else
                    {
                        particles[x].momentum[y] = particleVelocity[y];
                    }

                }
                particles[x].updateWeights();
            }
            
            currentIteration++;
            printTmpFile();
        }
    }
    
    private void syncBoards(AIPlayer player1, AIPlayer player2){
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
               p1Bullet.remove();
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
                p2Bullet.remove();
            }
        }

        AlienController p1AlienController = player1.getCurrentPosition().getAlienController();
        AlienController p2AlienController = player2.getCurrentPosition().getAlienController();
        ArrayList<GameObject> p1Buildings = player1.getCurrentPosition().getPlayerController().getBuildings();
        ArrayList<GameObject> p2Buildings = player2.getCurrentPosition().getPlayerController().getBuildings();
        int counter = 0;
         
        for(GameObject building : p1Buildings){
            if(building instanceof AlienFactory){
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
            if(building instanceof AlienFactory){
                counter++;
            }
        }
         
        if (player2.getRoundCount() >=40){
            p1AlienController.setWaveSize(4+counter);
        }else {
            p1AlienController.setWaveSize(3+counter);
        }
     }
    
    public void setWinsNormal(Particle you, AIPlayer player)
    {
            you.wins += player.getCurrentPosition().boardFinalRating();
    }
    
    public void setOpponentWinsNormal(Particle you, AIPlayer player, AIPlayer opponent)
    {
        if(player.getRoundCount() >=200 && player.getKillCount() > opponent.getKillCount()){
            you.wins += 1;
        }else if(player.isGameOver() == opponent.isGameOver()
                && player.getKillCount() > opponent.getKillCount()){
            you.wins += 1;
        }else if(!player.isGameOver()){
            you.wins += 1;
        }else{
            you.losses +=1;
        }
    }
    
    public void setWinsBest(Particle you, AIPlayer player)
    {
        you.bestWins += player.getCurrentPosition().boardFinalRating();
    }
    
        public void setOpponentWinsBest(Particle you, AIPlayer player, AIPlayer opponent)
    {
        if(player.getRoundCount() >=200 && player.getKillCount() > opponent.getKillCount()){
            you.bestWins += 1;
        }else if(player.isGameOver() == opponent.isGameOver()
                && player.getKillCount() > opponent.getKillCount()){
            you.bestWins += 1;
        }else if(!player.isGameOver()){
            you.bestWins += 1;
        }else{
            you.bestLosses +=1;
        }
    }
    
    public void localBestCalculate(int size)
    {
        for(int x=0; x< particles.length;x++)
        {
            double tmpMax=0;
            for(int k= x-size;k<x+size;k++)
            {
                if(k>=0 && k<particles.length)
                {
                    if(particles[k].fitness()>tmpMax)
                    {
                        tmpMax =particles[k].fitness(); 
                        for(int y=0; y< particles[x].dimensions;y++)
                        {
                            localBest[x][y] =particles[k].position[y];
                            localBestParticles[x].position[y]= localBest[x][y];
                            localBestParticles[x].updateWeights();
                            localBestParticles[x].particleBestUpdate();
                        }
                    } 
                }
            }
        } 
    }
  
    public void bestParticleClaculate()
    {
        
        for(int x=0; x< particles.length;x++)
        {
           
            if(particles[x].fitness()>globalBestFitness)
            {
                globalBestFitness =particles[x].fitness(); 
                for(int y=0; y< particles[x].dimensions;y++)
                {
                    globalBest[y] =particles[x].position[y];
                    globalBestParticle.position[y]= globalBest[y];
                    globalBestParticle.updateWeights();
                    globalBestParticle.particleBestUpdate();
                }
            }
        }
    }
 
    public Particle winningParticle() throws InterruptedException, IOException
    {
        //one last tournament
        int totalParticlesPassed=0;
        System.out.println("Calculating winning Particle");
        for(int x=0; x< particles.length;x++)
            {
                particles[x].bestLosses =0;
                particles[x].bestWins =0;
                particles[x].bestTies =0;
                particles[x].losses =0;
                particles[x].wins =0;
                particles[x].ties =0;
            }
            
            
        Random r = new Random();
        double randomValue = 0 + (1 - 0) * r.nextDouble();
        //competitionStart
        Particle competitionPool[] = new Particle[particleCount];

        for(int x=0; x<competitionPool.length;x++)
        {
            competitionPool[x] = particles[x];
        }

        for(int x=0; x<particles.length;x++)
        {
            //this will be your players
            AIPlayer you = new AIPlayer(plyDepth,particles[x].neuralNetwork);
            AIPlayer yourBest = new AIPlayer(plyDepth,particles[x].bestNetwork);

            while(true)
            { 
                if(you.isGameOver())
                {
                    break;
                }
                you.playRound();
            }
            //updateWins
            setWinsNormal(particles[x],you);

            //play 5

            while(true)
            { 
                if(yourBest.isGameOver())
                {
                    break;
                }
                yourBest.playRound();
            }
            //updateWins
            setWinsBest(particles[x],yourBest);

            //updating the personal best
            if(particles[x].bestFitness() < particles[x].fitness())
            {
                particles[x].personalBestFitness = particles[x].fitness();
                for(int y=0; y<particles[x].dimensions;y++)
                {
                    particles[x].personalBest[y] = particles[x].position[y];
                    particles[x].particleBestUpdate();
                }
            }
        }
        
       Particle winner = null;
       
       double tmpFitness = 0.0;
       for(int x=0; x<particles.length;x++)
       {
           if(particles[x].bestFitness()>tmpFitness)
           {
               winner = particles[x];
               tmpFitness = particles[x].bestFitness();
           }
           
       }
       return winner;
    }
    
     public Particle winningOpponentParticle() throws InterruptedException, IOException
    {
        //one last tournament
        int totalParticlesPassed=0;
        System.out.println("Calculating winning Particle");
        for(int x=0; x< particles.length;x++)
        {
            particles[x].bestLosses =0;
            particles[x].bestWins =0;
            particles[x].bestTies =0;
            particles[x].losses =0;
            particles[x].wins =0;
            particles[x].ties =0;
        }
            
            
        Random r = new Random();
        double randomValue = 0 + (1 - 0) * r.nextDouble();
        //competitionStart
        Particle competitionPool[] = new Particle[particleCount];

        for(int x=0; x<competitionPool.length;x++)
        {
            competitionPool[x] = particles[x];
        }

        for(int x=0; x<particles.length;x++)
        {
            for(int i = 0; i<particles.length-1;i++){
                if(x == i){
                    break;
                }

                //creating new "you" players after every round to reset the board
                AIPlayer you = new AIPlayer(plyDepth,particles[x].neuralNetwork);
                AIPlayer yourBest = new AIPlayer(plyDepth,particles[x].bestNetwork);

                AIPlayer opponent = new AIPlayer(plyDepth,particles[i].neuralNetwork);

                while(true)
                { 
                    if(you.isGameOver() || opponent.isGameOver())
                    {
                        break;
                    }
                    you.playRound();
                    opponent.playRound();
                    syncBoards(you, opponent);
                }
                //updateWins
                setOpponentWinsNormal(particles[x],you,opponent);

                opponent = new AIPlayer(plyDepth,particles[i].neuralNetwork);

                while(true)
                { 
                    if(yourBest.isGameOver() || opponent.isGameOver())
                    {
                        break;
                    }
                    yourBest.playRound();
                    opponent.playRound();
                    syncBoards(you, opponent);
                }
                //updateWins
                setOpponentWinsBest(particles[x],yourBest, opponent);
            }

            //updating the personal best
            if(particles[x].bestFitness() < particles[x].fitness())
            {
                particles[x].personalBestFitness = particles[x].fitness();
                for(int y=0; y<particles[x].dimensions;y++)
                {
                    particles[x].personalBest[y] = particles[x].position[y];
                    particles[x].particleBestUpdate();
                }
            }
        }
        
       Particle winner = null;
       
       double tmpFitness = 0.0;
       for(int x=0; x<particles.length;x++)
       {
           if(particles[x].bestFitness()>tmpFitness)
           {
               winner = particles[x];
               tmpFitness = particles[x].bestFitness();
           }
           
       }
       return winner;
    }

    public double cognitiveCalculate(double ranVal, double pBest, double pos)
    {
        double cognitive =((c1*ranVal)*(pBest-pos));
        //System.out.println("cognitive: " +cognitive);
        return cognitive;
    }
    
    public double socialCalculate(double ranVal, double gBest, double pos)
    {
        double social =((c2*ranVal)*(gBest-pos));
        //System.out.println("Social: " +social);
        return social;
    }
    
    public NeuralNetwork Winner()
    {
        
        Particle tmp = null;
        tmp = particles[0];
        double max= particles[0].fitness();
        for(int x= 1; x<particles.length;x++)
        {
           if(particles[x].fitness() > max)
           {
               tmp= particles[x];
           }
        }
        //TODO tmp was null for some reason meaning there were no particles in the pool. This for me is strange
        //I think I fixed it
        return tmp.neuralNetwork;
    }
    
    public void printTmpFile() throws FileNotFoundException
    {
                List<String> lines = new ArrayList<String>();
                
                NeuralNetwork n = Winner();
                
                String w ="";
                double trainedWeights[] = n.weights;
                for(int x=0; x< trainedWeights.length;x++)
                {
                    
                    if(x != trainedWeights.length-1)
                        w+= trainedWeights[x]+";";
                    else
                        w+= trainedWeights[x];   
                }
                lines.add(w);
        
        
        PrintWriter writer = new PrintWriter("tmpFile.txt");
        writer.print("");
        for(int x=0; x< lines.size();x++)
            writer.print(lines.get(x));

        writer.close();
    }
    
}
