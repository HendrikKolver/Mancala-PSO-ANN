
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hendr_000
 */
public class AIPlayer {
    
    
    private int plyDepth;
    private int player;
    protected boolean gameOver;
    NeuralNetwork network;
    
    
    public AIPlayer( int depth, int player, NeuralNetwork n) throws IOException
    {
       plyDepth = depth;
       this.player = player;
       gameOver = false;
       network = n;
    }
 
    public TreeInterface guiPlay(int[][] board)
    {
        TreeInterface tmp = new TreeComposite(network);

        tmp.setBoard(board);
        
       if(!gameOver(tmp,board) && !gameOver)
       {
    
        TreeBuilder build = new TreeBuilder(plyDepth);
        TreeInterface root = new TreeComposite(network);
        
        root.setPlayer(player);
        root.repeat = true;
        
        root.setBoard(board);
        TreeInterface answer = build.build(root);
        
        return answer;
       }
       return null;
    }
    
    public int[][] AITurn(int[][] board) throws InterruptedException
    {
        TreeInterface tmp = new TreeComposite(network);

        tmp.setBoard(board);
        
       if(!gameOver(tmp,board) && !gameOver)
       {
    
        TreeBuilder build = new TreeBuilder(plyDepth);
        TreeInterface root = new TreeComposite(network);
        
        root.setPlayer(player);
        root.repeat = true;
        
        root.setBoard(board);
        TreeInterface answer = build.build(root);
             
        while(answer.repeat)
        {
            if(!gameOver(answer,answer.getBoard()) && ! gameOver)
            { 
                build = new TreeBuilder(plyDepth);
                root = new TreeComposite(network);

                root.setPlayer(player);
                root.repeat = true;
                
                root.setBoard(answer.getBoard());
                answer = build.build(root);

            }
            else
                break;
        }
        return answer.getBoard();
       }
       
       return board;
    }
    
    public boolean gameOver(TreeInterface node, int[][] board)
    {
        boolean over =false;
        if(player == 0)
        {
            if (node.getBoard()[1][1] == 0 && node.getBoard()[1][2] == 0 && node.getBoard()[1][3] == 0 && node.getBoard()[1][4] == 0  && node.getBoard()[1][5] == 0  && node.getBoard()[1][6] == 0)
            {
               over = true; 
            }
        }
        
        if(player == 1)
        {
            if (node.getBoard()[0][1] == 0 && node.getBoard()[0][2] == 0 && node.getBoard()[0][3] == 0 && node.getBoard()[0][4] == 0  && node.getBoard()[0][5] == 0  && node.getBoard()[0][6] == 0)
            {
               over = true; 
            }
        }
        
        if(over == true)
        {

            board[0][0]+= board[0][1] +board[0][2]+board[0][3]+board[0][4]+board[0][5]+board[0][6];
            board[0][7]+= board[1][1] +board[1][2]+board[1][3]+board[1][4]+board[1][5]+board[1][6];
            
            gameOver = true;
            return true;
        }
        return false;
    }
 
}
