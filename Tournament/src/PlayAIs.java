
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hendrik
 */
public class PlayAIs {
    public static void main(String[] args) throws IOException, InterruptedException {
       PlayAIs p = new PlayAIs(); 
       p.load();
       for(int x=0; x<10;x++)
       {
        p.play();
        p.printScores();
       }
       
      
    }
    public int ply;
    
    public AIPlayer ai[];
    
    public PlayAIs()
    {
        ai = new AIPlayer[1];
        
       ply = Integer.parseInt(JOptionPane.showInputDialog("PlyDepth"));
    }
    
    public void load() throws IOException
    {
            List<String> lines;
        
            //read from file
            double weights[] = new double[65];
            NeuralNetwork n1 = new NeuralNetwork(14,1,4,1);
            String name = JOptionPane.showInputDialog("fileName");//"8ply.txt";
            lines = readSmallTextFile(name);
            if(lines.size()<1)
            {
                System.out.println("Cannot, file is empty please train first");
            }
            else
            {
                String tmp[] =lines.get(0).split(";");
                
                for(int x=0; x<weights.length;x++)
                {
                    weights[x] = Double.parseDouble(tmp[x]);
                }
                
                n1.updateWeights(weights);
            }

                //create the hopefully awesome AI
            
                AIPlayer player1 = new AIPlayer(6,1,n1);
                ai[0]=player1;
            

        
       
    }
    
    public void play() throws InterruptedException, IOException
    {
        
            for(int x=0; x<ai.length;x++)
            {
                    AIPlayer you = new AIPlayer(ply,0,ai[x].network);
                    
                    //selecting your opponents
                    

                    //scrol to line 293 to skip
                    for(int k =0; k<150;k++)
                    {
                        
                        //System.out.println("once");
                        
                        AIRandom opponent = new AIRandom(2,1,ai[0].network);

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
                        //System.out.println(board[0][0]);
                        setWinsNormal(ai[x],board);

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
                        //System.out.println(board[0][0]);
                        setWinsNormal(ai[x],board);


                    }
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
    
    public void setWinsNormal(AIPlayer you, int[][] board)
    {
            if(board[0][0] > board[0][7])
            {
               
               // System.out.println("lost");
                you.score+=(board[0][7]-board[0][0]);
                you.wons-=0;
            }
            else if(board[0][0] < board[0][7])
            {
                //System.out.println("won");
                you.score+=(board[0][7]-board[0][0]);
                you.wons+=2;
            }
            else
            {
                //System.out.println("tie");
                you.score+=0.0;
                you.wons+=1;
            }  
    }
    
    public void printScores()
    {
        
            System.out.println(" Results: "+ai[0].wons);
            double tmp = (ai[0].wons/600.0)*100;
            System.out.println(" Percentage Wins: "+tmp + "%");
            ai[0].wons = 0;
    }
    
    List<String> readSmallTextFile(String aFileName) throws IOException {
    
        List<String> tmp = new ArrayList<>();
        BufferedReader inFile = new BufferedReader(new FileReader(aFileName));    
        String line;
        while((line =inFile.readLine())!= null)
        {
            tmp.add(line);
        }
        inFile.close();
        
    return tmp;
  }
    
    
    
    void writeSmallTextFile(List<String> aLines, String aFileName) throws IOException {
        PrintWriter writer = new PrintWriter("AIPlayer.txt");
        writer.print("");
        for(int x=0; x< aLines.size();x++)
            writer.print(aLines.get(x));

        writer.close();
    
  }
    
}
