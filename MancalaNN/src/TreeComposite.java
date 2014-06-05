
import java.util.Random;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hendrik
 */
public class TreeComposite extends TreeInterface {
     
     Random tmpScore;
     
     
     public TreeComposite(NeuralNetwork n)
     {
         evaluation = n;
         children = null;
         next = null;
         boardPosition = new int[2][8];
         nodeScore = 0;
         player = 2;
         minMaxScore =0;
         alpha = -100000;
        beta = 100000;
         tmpScore = new Random();
         
         
     }
     
     public void setBoard(int[][] newPosition)
     {
         for(int x=0; x<2;x++)
         {
             for(int i =0; i<8; i++)
             {
                boardPosition[x][i] = newPosition[x][i];
             }    
         }

        if(win(this))
            finalState = true;
        else
            finalState = false;
     }
     
     public int[][] getBoard()
     {
        return boardPosition; 
     }
     
     public TreeInterface getNext()
     {
         return next;
     }
     
     public void setNext(TreeInterface newNode)
     {
         next = newNode;
     }
     
     public void setPlayer(int num)
     {
        player = num; 
     }
     
     public int getPlayer()
     {
        return player; 
     }
     
     public void addChild(TreeInterface node)
     {
        
         if(children == null)
        {
            children = node;
            children.nodeDepth = nodeDepth+1;
        }
        else
        {
            TreeInterface tmpNode = children;
            
            while(tmpNode.next != null)
            {
                tmpNode = tmpNode.next;
            }
            tmpNode.next = node; 
            node.nodeDepth = nodeDepth+1;
        }
     }

     public void evaluateMyself(int gPlayer)
     {
         double input[] = convertPositionToInput(gPlayer,boardPosition);
         //node evaluation
        Neuron out[] = evaluation.calculate(input);
        this.nodeScore = out[0].fireOutput();
        out[0].clear();   
     }
     
     public double[] convertPositionToInput(int gPlayer,int[][] in)
    {
        double tmp[] = new double[14];
        int counter =0;
         if(gPlayer == 1)
        {
            for(int x=1; x<7;x++)
            {
                tmp[counter] = in[0][x];
                
                counter++;
            }
            
            for(int x=6; x>0;x--)
            {
               tmp[counter] = in[1][x];
                
                counter++;
                
            }
            
            tmp[counter] = in[0][0];
            
            counter++;
            tmp[counter] = in[0][7];
            counter++;
            
        }else
        {
            for(int x=6; x>0;x--)
            {
               tmp[counter] = in[1][x];
                
                counter++;
                
            }
            
            for(int x=1; x<7;x++)
            {
                tmp[counter] = in[0][x];
                
                counter++;
            }
 
            tmp[counter] = in[0][7];
            counter++;
            tmp[counter] = in[0][0];
            counter++;
        }
        return tmp;
  
    }
    
     
     public boolean win(TreeInterface node)
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
        
        return over;
    }
         
}
