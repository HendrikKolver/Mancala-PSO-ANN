package spaceinvader.pso;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
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
       gamesPlayed = 10;
       
  
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
    
    public void setWinsNormal(Particle you, AIPlayer player)
    {
            you.wins += player.getCurrentPosition().boardFinalRating();
    }
    
    public void setWinsBest(Particle you, AIPlayer player)
    {
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
