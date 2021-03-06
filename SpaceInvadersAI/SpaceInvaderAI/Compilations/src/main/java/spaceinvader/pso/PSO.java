package spaceinvader.pso;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import spaceinvader.entities.GameObject;
import spaceinvader.entities.PlayerBullet;
import spaceinvader.gameRunner.AlienController;
import spaceinvader.gameRunner.BulletController;
import spaceinvader.neuralNetwork.NeuralNetwork;
import spaceinvader.utilities.RandomGenerator;

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
    private final int gamesPlayed;
    private double maxIterationTime;
    private int maxIterationRound;
    private boolean aggresiveTactic;

    public PSO(int p, double w, double c1, double c2, int particalCount, double maxVel, int nIter, double uB, double lB, int inputs, int outputs, int hidden, int sigmoid, int gamesPlayed, boolean aggresiveTactic)
    {
        this.aggresiveTactic = aggresiveTactic;
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
//        tournamentSize = particalCount;
       tournamentSize = 10;
       plyDepth = p;
       this.gamesPlayed = gamesPlayed;
       maxIterationTime = 0;
       maxIterationRound = 0;
    }
    
    public void trainLocal2Player(int size) throws InterruptedException, IOException
    {
       //Particle init
       particles = new Particle[particleCount];
       localBestParticles = new Particle[particleCount];
         
       for(int x=0; x<particleCount;x++)
        {

           if(x == 7){
                initParticle(x,"train11.txt"); 
//            }else if(x == 27){
//                initParticle(x,"6Input_4Hidden_1stOn6Ply.txt"); 
//            }else if(x == 21){
//                initParticle(x,"potential_new_winner.txt"); 
//            }else if(x == 16){
//                initParticle(x,"6inputWinner.txt"); 
//            }else if(x == 35){
//                initParticle(x,"2015_07_07_veryVeryGood.txt"); 
            }else{
                initParticle(x);
            }
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
            double startTime = System.currentTimeMillis();
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
                Particle tournamentPool[] = getRandomTournamentPool(particles);
                
                for(int i = 0; i<tournamentPool.length;i++){                      
                        //creating new "you" players after every round to reset the board
                    NeuralNetwork backup = new NeuralNetwork(11,1,4,1);
                    AIPlayer you = new AIPlayer(plyDepth,particles[x].neuralNetwork, aggresiveTactic, backup);
                    AIPlayer yourBest = new AIPlayer(plyDepth,particles[x].bestNetwork, aggresiveTactic, backup);

                    AIPlayer opponent = new AIPlayer(plyDepth,tournamentPool[i].neuralNetwork, aggresiveTactic, backup);

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
                    opponent = new AIPlayer(plyDepth,tournamentPool[i].neuralNetwork, aggresiveTactic, backup);

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
            
            double endTime = System.currentTimeMillis();
            double iterationTime = endTime-startTime;
            
            if(iterationTime > this.maxIterationTime){
                this.maxIterationTime = iterationTime;
                this.maxIterationRound = currentIteration;
            }
            
            System.out.println("Longest Iteration = " + this.maxIterationRound + " (" + this.maxIterationTime + ")");
            
            currentIteration++;
            printTmpFile();
        }
    }

    private Particle[] getRandomTournamentPool(Particle[] particles) {
        Particle tournamentPool[];
        ArrayList<Particle> allParticles = new ArrayList(Arrays.asList(particles));
        tournamentPool = new Particle[tournamentSize];
        for (int i = 0; i < tournamentPool.length; i++) {
            int particleIndex = RandomGenerator.randInt(0, allParticles.size()-1);
            tournamentPool[i] = allParticles.get(particleIndex);
            allParticles.remove(particleIndex);
        }
        return tournamentPool;
    }
    
    private void initParticle(int index){
        particles[index] = new Particle(inputs,outputs,hidden,sigmoid);
        particles[index].initParticle(lowerBound,upperBound);
        localBestParticles[index] = new Particle(inputs,outputs,hidden,sigmoid);
        localBestParticles[index].initParticle(lowerBound,upperBound);
    }
    
    private void initParticle(int index, String fileName) throws IOException{
        particles[index] = new Particle(inputs,outputs,hidden,sigmoid);
        particles[index].initParticle(fileName);
        localBestParticles[index] = new Particle(inputs,outputs,hidden,sigmoid);
        localBestParticles[index].initParticle(fileName);
    }
    
    public void trainLocal(int size) throws InterruptedException, IOException
    {
       //Particle init
       particles = new Particle[particleCount];
       localBestParticles = new Particle[particleCount];
        
        for(int x=0; x<particleCount;x++)
        {
            if(x == 27){
                initParticle(x,"tmpFile - Copy.txt"); 
            }else if(x == 21){
                initParticle(x,"goodSolution2.txt"); 
            }else if(x == 16){
                initParticle(x,"6inputWinner.txt"); 
            }else if(x == 35){
                initParticle(x,"EqualWithCurrentWinner6Inputs.txt"); 
            }else{
                initParticle(x);
            }
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
            double startTime = System.currentTimeMillis();
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
                    NeuralNetwork backup = new NeuralNetwork(11,1,4,1);
                    AIPlayer you = new AIPlayer(plyDepth,particles[x].neuralNetwork, aggresiveTactic, backup);
                    AIPlayer yourBest = new AIPlayer(plyDepth,particles[x].bestNetwork, aggresiveTactic, backup);


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
            
            double endTime = System.currentTimeMillis();
            double iterationTime = endTime-startTime;
            
            if(iterationTime > this.maxIterationTime){
                this.maxIterationTime = iterationTime;
                this.maxIterationRound = currentIteration;
            }
            
            System.out.println("Longest Iteration = " + this.maxIterationRound + " (" + this.maxIterationTime + ")");
            
            currentIteration++;
            printTmpFile();
        }
    }
    
      //TODO consolidate this and the copy of this function in SpaceInvader.java
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
    
    public void setWinsNormal(Particle you, AIPlayer player)
    {
            you.wins += player.getCurrentPosition().boardFinalRating();
    }
    
    public void setOpponentWinsNormal(Particle you, AIPlayer player, AIPlayer opponent)
    {
        if(player.getRoundCount() >=200 && player.getKillCount() > opponent.getKillCount()){
            you.wins += 100;
        }else if(player.isGameOver() == opponent.isGameOver()
                && player.getKillCount() > opponent.getKillCount()){
            you.wins += 100;
        }else if(!player.isGameOver()){
            you.wins += 100;
        }else{
            you.losses +=100;
        }
        
        //This means that the player considers how well they won as well rather than just winning and losing
        you.wins += player.getCurrentPosition().boardFinalRating();
    }
    
    public void setWinsBest(Particle you, AIPlayer player)
    {
        you.bestWins += player.getCurrentPosition().boardFinalRating();
    }
    
        public void setOpponentWinsBest(Particle you, AIPlayer player, AIPlayer opponent)
    {
        if(player.getRoundCount() >=200 && player.getKillCount() > opponent.getKillCount()){
            you.bestWins += 100;
        }else if(player.isGameOver() == opponent.isGameOver()
                && player.getKillCount() > opponent.getKillCount()){
            you.bestWins += 100;
        }else if(!player.isGameOver()){
            you.bestWins += 1;
        }else{
            you.bestLosses +=100;
        }
        
        //This means that the player considers how well they won as well rather than just winning and losing
        you.bestWins += player.getCurrentPosition().boardFinalRating();
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
            for (int i = 0; i < this.gamesPlayed; i++) {
                //this will be your players
                NeuralNetwork backup = new NeuralNetwork(11,1,4,1);
                AIPlayer you = new AIPlayer(plyDepth,particles[x].neuralNetwork, aggresiveTactic, backup);
                AIPlayer yourBest = new AIPlayer(plyDepth,particles[x].bestNetwork, aggresiveTactic, backup);

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
                NeuralNetwork backup = new NeuralNetwork(11,1,4,1);
                //creating new "you" players after every round to reset the board
                AIPlayer you = new AIPlayer(plyDepth,particles[x].neuralNetwork, aggresiveTactic, backup);
                AIPlayer yourBest = new AIPlayer(plyDepth,particles[x].bestNetwork, aggresiveTactic, backup);

                AIPlayer opponent = new AIPlayer(plyDepth,particles[i].neuralNetwork, aggresiveTactic, backup);

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

                opponent = new AIPlayer(plyDepth,particles[i].neuralNetwork, aggresiveTactic, backup);

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
