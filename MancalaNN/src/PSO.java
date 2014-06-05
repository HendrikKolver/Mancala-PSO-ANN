
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hendrik
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
  
    }
    
    public void train() throws IOException, InterruptedException
    {
       particles = new Particle[particleCount];
       for(int x=0; x<particleCount;x++)
        {
           particles[x] = new Particle(inputs,outputs,hidden,sigmoid);
           particles[x].initParticle(lowerBound,upperBound);
        }
        
      
        int currentIteration = 0;
        globalBestParticle = new Particle(inputs,outputs,hidden,sigmoid);
        globalBestParticle.initParticle(lowerBound,upperBound);
        globalBest = new double[particles[0].dimensions];
        
        for(int x=0;x<globalBest.length;x++)
           globalBest[x] =1;

        int totalParticlesPassed =0;
        while(currentIteration<numIterations)
        {
            
            for(int x=0; x< particles.length;x++)
            {
                particles[x].bestLosses =0;
                particles[x].bestWins =0;
                particles[x].bestTies =0;
                particles[x].losses =0;
                particles[x].wins =0;
                particles[x].ties =0;
            }
            globalBestFitness = 0;
            
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
                double tmp = ((totalParticlesPassed*100.0)/(numIterations*30))*100;
                tmp = Math.round(tmp);
                totalProgress = tmp/100.0;
                System.out.println("Total Training Progress: " + totalProgress+"%");
                //this will be your players
                AIPlayer you = new AIPlayer(plyDepth,0,particles[x].neuralNetwork);
                AIPlayer yourBest = new AIPlayer(plyDepth,0,particles[x].bestNetwork);
                
                Particle opponents[] = new Particle[particles.length];
              
                Random groupSelect = new Random();
                
                //selecting your opponents
                for(int k=0; k< particles.length;k++)
                {
                    int ranNum = groupSelect.nextInt(competitionPool.length);
                    opponents[k] = competitionPool[k];
                }
                
                //scrol to line 293 to skip
                for(int k =0; k<opponents.length;k++)
                {
                    //System.out.println("playing---"+k);
                    AIPlayer opponent = new AIPlayer(plyDepth,1,opponents[k].neuralNetwork);
                    AIPlayer opponentBest = new AIPlayer(plyDepth,1,opponents[k].bestNetwork);
                    //actual play
                    int[][] board = new int[2][8];
                    //playOne
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(you.AITurn(board));
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(opponent.AITurn(board));
                    }
                    //updateWins
                    setWinsNormal(particles[x],board);
                    
                    //playTwo
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(opponent.AITurn(board));
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(you.AITurn(board));
                    }
                    //updateWins
                    setWinsNormal(particles[x],board);
                    
                    
                    //play 3
                    initBoard(board);
                    while(true)
                    { 
                        if(opponentBest.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponentBest.gameOver = false;
                            break;
                        }
                        board = updateGrid(you.AITurn(board));
                        if(opponentBest.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponentBest.gameOver = false;
                            break;  
                        }
                        board = updateGrid(opponentBest.AITurn(board));
                    }
                    //updateWins
                    setWinsNormal(particles[x],board);
                    
                    //play 4
                    initBoard(board);
                    while(true)
                    { 
                        if(opponentBest.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponentBest.gameOver = false;
                            break;
                        }
                        board = updateGrid(opponentBest.AITurn(board));
                        if(opponentBest.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponentBest.gameOver = false;
                            break;  
                        }
                        board = updateGrid(you.AITurn(board));
                    }
                    //updateWins
                    setWinsNormal(particles[x],board);
                    
                    //play 5
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(yourBest.AITurn(board));
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(opponent.AITurn(board));
                    }
                    //updateWins
                    setWinsBest(particles[x],board);
                    
                    //play 6
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(opponent.AITurn(board));
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(yourBest.AITurn(board));
                    }
                    //updateWins
                    setWinsBest(particles[x],board);
                    
                    
                    //play 7
                    initBoard(board);
                    while(true)
                    { 
                        if(opponentBest.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponentBest.gameOver = false;
                            break;
                        }
                        board = updateGrid(yourBest.AITurn(board));
                        if(opponentBest.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponentBest.gameOver = false;
                            break;  
                        }
                        board = updateGrid(opponentBest.AITurn(board));
                    }
                    //updateWins
                    setWinsBest(particles[x],board);
                    
                    //play8
                    initBoard(board);
                    while(true)
                    { 
                        if(opponentBest.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponentBest.gameOver = false;
                            break;
                        }
                        board = updateGrid(opponentBest.AITurn(board));
                        if(opponentBest.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponentBest.gameOver = false;
                            break;  
                        }
                        board = updateGrid(yourBest.AITurn(board));
                    }
                    //updateWins
                    setWinsBest(particles[x],board);
    
                }
                //scrol up to line 108 to skip

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
                totalParticlesPassed++;
            }
            //updating the global best
            bestParticleClaculate();
            //System.out.println("Global Best: " + globalBestFitness);
            //calculate new particle velocities
            double particleVelocity[] = new double[particles[0].dimensions];
            for(int x=0; x<particles.length;x++)
            {
                for(int y=0; y<particles[x].dimensions;y++)
                {

                    particleVelocity[y] = ((w*particles[x].avgMomentum())+
                            cognitiveCalculate(randomValue,particles[x].personalBest[y],particles[x].position[y])+ //(c1*randomValue*(particles[x].personalBest[y]-particles[x].position[y]))+
                            //cognitive
                            socialCalculate(randomValue,globalBest[y],particles[x].position[y])//(c2*randomValue*(globalBest[y]-particles[x].position[y]))
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
                    double noise = 0 + ((globalBest[y]-particles[x].personalBest[y]) - 0) * n.nextDouble();
                    double tmpVal = particles[x].position[y]+(particleVelocity[y]+noise);


                    //System.out.println("tmpVal: " + tmpVal);
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
        
        localBest = new double[particleCount][particles[0].dimensions];
        
        for(int x=0;x<localBest.length;x++)
            for(int k =0; k< localBest[x].length;k++)
                localBest[x][k] =1;

        int totalParticlesPassed =0;
        while(currentIteration<numIterations)
        {
            
            //System.out.println("Iteration---"+currentIteration);
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
               
                double tmp = ((totalParticlesPassed*100.0)/(numIterations*30))*100;
                tmp = Math.round(tmp);
                totalProgress = tmp/100.0;
                System.out.println("Total Training Progress: " + totalProgress+"%");
             
                //this will be your players
                AIPlayer you = new AIPlayer(plyDepth,0,particles[x].neuralNetwork);
                AIPlayer yourBest = new AIPlayer(plyDepth,0,particles[x].bestNetwork);
                
                Particle opponents[] = new Particle[particles.length];
              
                Random groupSelect = new Random();
                
                //selecting your opponents
                for(int k=0; k< particles.length;k++)
                {
                    int ranNum = groupSelect.nextInt(competitionPool.length);
                    opponents[k] = competitionPool[k];
                }
                
                //scrol to line 293 to skip
                for(int k =0; k<opponents.length;k++)
                {
                    //System.out.println("playing---"+k);
                    AIPlayer opponent = new AIPlayer(plyDepth,1,opponents[k].neuralNetwork);
                    AIPlayer opponentBest = new AIPlayer(plyDepth,1,opponents[k].bestNetwork);
                    //actual play
                    int[][] board = new int[2][8];
                    //playOne
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(you.AITurn(board));
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(opponent.AITurn(board));
                    }
                    //updateWins
                    setWinsNormal(particles[x],board);
                    
                    //playTwo
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(opponent.AITurn(board));
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(you.AITurn(board));
                    }
                    //updateWins
                    setWinsNormal(particles[x],board);

                    //play 5
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(yourBest.AITurn(board));
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(opponent.AITurn(board));
                    }
                    //updateWins
                    setWinsBest(particles[x],board);
                    
                    //play 6
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(opponent.AITurn(board));
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(yourBest.AITurn(board));
                    }
                    //updateWins
                    setWinsBest(particles[x],board);
                    
                    
                    
                    
                }
                //scrol up to line 108 to skip

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
                totalParticlesPassed++;
            }
            //updating the global best
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


                    //System.out.println("tmpVal: " + tmpVal);
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
    
    public int[][] updateGrid(int[][] board)
    {
        int[][] tmp = new int[2][8];
        for(int y=0; y<2;y++)
        {
            for(int i =0; i<8; i++)
            {  
                tmp[y][i] = board[y][i];
            }     
        } 
        return tmp;
    }
    
    public void initBoard(int[][] board)
    {
        board[0][0] = 0;
        board[0][7] = 0;
        
        for(int x=1; x< 7 ; x++)
        {
           board[0][x] = 4;
           board[1][x] = 4;
        }
    }
    
    public void setWinsNormal(Particle you, int[][] board)
    {
            if(board[0][0] > board[0][7])
            {
                you.losses+=1;
            }
            else if(board[0][0] < board[0][7])
            {
                you.wins+=1;
            }
            else
            {
                you.ties+=0.5;
            }  
    }
    
    public void setWinsBest(Particle you, int[][] board)
    {
            if(board[0][0] > board[0][7])
            {
                you.bestLosses+=1;
            }
            else if(board[0][0] < board[0][7])
            {
                you.bestWins+=1;
            }
            else
            {
                you.bestTies+=0.5;
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
                double tmp = ((totalParticlesPassed*100.0)/(30))*100;
                tmp = Math.round(tmp);
                totalProgress = tmp/100.0;
                System.out.println("Calculating Winning Particle Progress: " + totalProgress+"%");
                //this will be your players
                AIPlayer you = new AIPlayer(plyDepth,0,particles[x].neuralNetwork);
                AIPlayer yourBest = new AIPlayer(plyDepth,0,particles[x].bestNetwork);
                
                Particle opponents[] = new Particle[particles.length];
              
                Random groupSelect = new Random();
                
                //selecting your opponents
                for(int k=0; k< particles.length;k++)
                {
                    int ranNum = groupSelect.nextInt(competitionPool.length);
                    opponents[k] = competitionPool[k];
                }
                
                
                for(int k =0; k<opponents.length;k++)
                {
                    //System.out.println("playing---"+k);
                    AIPlayer opponent = new AIPlayer(plyDepth,1,opponents[k].neuralNetwork);
                    AIPlayer opponentBest = new AIPlayer(plyDepth,1,opponents[k].bestNetwork);
                    //actual play
                    int[][] board = new int[2][8];
                    //playOne
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(you.AITurn(board));
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(opponent.AITurn(board));
                    }
                    //updateWins
                    setWinsNormal(particles[x],board);
                    
                    //playTwo
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(opponent.AITurn(board));
                        if(opponent.gameOver || you.gameOver)
                        {
                            you.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(you.AITurn(board));
                    }
                    //updateWins
                    setWinsNormal(particles[x],board);
                    
                    
                    
                    
                    //play 5
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(yourBest.AITurn(board));
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(opponent.AITurn(board));
                    }
                    //updateWins
                    setWinsBest(particles[x],board);
                    
                    //play 6
                    initBoard(board);
                    while(true)
                    { 
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;
                        }
                        board = updateGrid(opponent.AITurn(board));
                        if(opponent.gameOver || yourBest.gameOver)
                        {
                            yourBest.gameOver = false;
                            opponent.gameOver = false;
                            break;  
                        }
                        board = updateGrid(yourBest.AITurn(board));
                    }
                    //updateWins
                    setWinsBest(particles[x],board);
                    
                    
                   
    
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
                totalParticlesPassed++;

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
        System.out.println("done");
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
        double max=0;
        Particle tmp = null;
        for(int x= 0; x<particles.length;x++)
        {
           if(particles[x].fitness() > max)
           {
               tmp= particles[x];
           }
        }
        
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
