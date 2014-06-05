/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Hendrik
 */
public class TreeBuilder {
    public boolean gameOver = false;
    public int nodeCount =0;
    public int minMaxSwitcher = 0;
    public int plyDepth;
    public double mainA, mainB;
    public int globalPlayer = 2;
    public NeuralNetwork network;
 
    
    
    public TreeBuilder(int depth)
    {
        plyDepth = depth;
        mainA =-100000.0;
        mainB = 100000.0;
        
        
    }
    
    public TreeInterface build(TreeInterface node)
    {
        
        network = node.evaluation;
        TreeInterface root = node;
        globalPlayer = root.getPlayer();
        mainA = ABMax(root,mainA,mainB);
       
        TreeInterface tmpNode = root.children;
        TreeInterface finalNode = null;
        double tmpCount = -1000.0;
        
        while(tmpNode != null)
        {
           
            //System.out.println(tmpNode.nodeScore);
            if(tmpNode.nodeScore > tmpCount)
            {
                tmpCount = tmpNode.nodeScore;
                finalNode = tmpNode;
            }
            
            tmpNode = tmpNode.next;
            
        }
        
        //System.out.println("nodeCount: " + nodeCount);
        
        return finalNode;
    }
   
    
    public double ABMax(TreeInterface node, double a, double b)
    {
       if(!node.finalState && node.nodeDepth < plyDepth)
       {
            buildTree(node);
       }
        
        if(node.children == null || node.nodeDepth >= plyDepth)
            {
                
                node.evaluateMyself(globalPlayer);
               return node.nodeScore; 
            }
       TreeInterface newNodes = node.children;
       
       while(newNodes != null)
       {
         if(!node.repeat)
         {
            a = max(a,ABMin(newNodes,a,b));
         }
         else
         {
            a = max(a,ABMax(newNodes,a,b));
         }
         if (a >= b)
            {
                node.nodeScore = b;
                return b;
            }    
         
         newNodes = newNodes.next;
       }
       node.nodeScore = a;
       return a;
    }
    
    public double ABMin(TreeInterface node, double a, double b)
    {
        if(!node.finalState && node.nodeDepth< plyDepth)
       {
            buildTree(node);
       }
        
        if(node.children == null || node.nodeDepth >= plyDepth)
            {
                node.evaluateMyself(globalPlayer);
               return node.nodeScore; 
            }
       TreeInterface newNodes = node.children;
       while(newNodes != null)
       {
         if(!node.repeat)
         {
            b = min(b,ABMax(newNodes,a,b));
         }
         else
         { 
            b = min(b,ABMin(newNodes,a,b));
 
         }
         if (b <= a) 
            {
                node.nodeScore = a;
                return a;
            }

         newNodes = newNodes.next;
       }
       node.nodeScore = b;
       return b;
    }
    
    public double max(double a, double b)
    {
        if(a> b)
            return a;
        else
            return b;             
    }
    
    public double min(double a, double b)
    {
        if(a< b)
            return a;
        else
            return b;             
    }
    
    public void buildTree(TreeInterface node)
    {
        
        if(!node.finalState)
        {
            
        int tmpBoard[][] = new int[2][8];
        
        TreeInterface tmpNode = node;
  
            tmpBoard = tmpNode.getBoard();
           TreeInterface newNode;
           int changeBoard[][] = new int[2][8]; 
            
            if(tmpNode.getPlayer() == 1)
            {
                
                            
                            for(int x=1; x<7; x++)
                            {
                                if(tmpBoard[0][x] != 0)
                                {
                                    for(int k = 0;k<2;k++)
                                    {
                                        for(int j =0; j<8;j++)
                                        {
                                        changeBoard[k][j] = tmpBoard[k][j]; 
                                        }
                                    }
                                    
                                    if(tmpNode.repeat == false)
                                        newNode = makeMove(0,x,changeBoard); 
                                    else
                                        newNode = makeMove(1,x,changeBoard);
                                    nodeCount++;
                                    tmpNode.addChild(newNode); 
                                }
                            }
                              
            }
            else
            {
                            for(int x=1; x<7; x++)
                            {
                                if(tmpBoard[1][x] != 0)
                                {
                                    
                                    for(int k = 0;k<2;k++)
                                    {
                                        for(int j =0; j<8;j++)
                                        {
                                        changeBoard[k][j] = tmpBoard[k][j]; 
                                        }
                                    }
                                    
                                    if(tmpNode.repeat == false)
                                        newNode = makeMove(1,x,changeBoard); 
                                    else
                                        newNode = makeMove(0,x,changeBoard);
                                    nodeCount++;
                                    tmpNode.addChild(newNode); 
                                }
                            }  
            }
        }
    }
    
    public TreeInterface makeMove(int player, int position,int b[][])
    {
         
        boolean repeat = true;
        int board[][]= new int[2][8];
        
        for(int x=0; x<2;x++)
        {
            for(int j=0; j<8;j++)
            {
              board[x][j] = b[x][j];  
            }
        }
        
        int row;
        if(player == 1)
            row = 0;
        else
            row = 1;
        
        int seeds = board[row][position];
         
        
        board[row][position] = 0;
        
        
        if(row == 0)
        {
            position -=1;
            seeds--;
            boolean tmpTest = false;
            if(seeds == 0)
            {
                if(board[0][position] == 0  && board[1][position] != 0)
                {
                    board[0][0] += board[1][position];
                    board[0][position] = 0;
                    board[0][0]++;
                    board[1][position] = 0;
                    tmpTest = true;
                   
                }
            }
            if(!tmpTest)
                board[row][position] += 1;
            position -= 1;
            
            if(position == -1)
            {
                position = 1;
                row = 1;
                
            }
            else
            {
                
                if(seeds == 0)
                {
                    repeat = false;
                    
                }
            }
        }
        else
        {
            position +=1;
            if(position == 7)
            {
                
                row = 0;
            }
            seeds--;
            
            boolean tmpTest = false;
            if(seeds == 0)
            {
                if(board[row][position] == 0   && board[0][position] != 0)
                {
                    board[0][7] += board[0][position];
                    board[0][position] = 0;
                    board[0][7]++;
                    board[1][position] = 0;
                    tmpTest = true;
                   
                }
            }
            if(!tmpTest)
                board[row][position] += 1;
            position+=1;
            if(position == 8)
            {
                
                position = 6;
                row = 0;
            }
            else
            {
                
                if(seeds == 0)
                {
                    repeat = false;
                   
                }
            }
            
        }
        
        
        while(seeds > 0)
        {
            
            if(seeds > 1)
            {
                
                if(position == 0)
                {
                    if(player == 1)
                    {
                        seeds--;
                        board[0][position] += 1;
                        position =1;
                        row = 1;
                    }
                    else
                    {
                        position =1;
                        row = 1; 
                    }
                }
                else
                {
                    if(position == 7)
                    {
                        if(player == 1)
                        {

                            position =6;
                            row = 0;
                        }
                        else
                        {
                            seeds--;
                            row = 0;
                            board[0][position] += 1;
                            position =6;

                        }
                    } else if(row == 0)
                    {
                        seeds--;
                        board[row][position] += 1;
                        position -= 1;
                    }
                    else
                    {
                    seeds--;
                        board[row][position] += 1;
                        position += 1; 
                    }
                }
            }
            else //last seed
            {
                if(position == 0)
                {
                    if(player == 1)
                    {
                        seeds--;
                        board[row][position] += 1;
                        break;
                    }
                    else
                    {
                        position =1;
                        row = 1;
                        //repeat = false;
                        
                    }
                }
                else
                {
                    if(position == 7)
                    {
                        if(player == 1)
                        {

                            position =6;
                            row = 0;
                            //repeat = false;
                            
                        }
                        else
                        {
                            seeds--;
                            board[0][position] += 1;
                            break;
                            

                        }
                    } else if(row == 0)
                    {
                        boolean tmpTest = false;
                        seeds--;
                        if(player == 1 && board[row][position] == 0  && board[1][position]!=0) //do you take opponents seeds
                        {
                            board[0][0] += board[1][position];
                           board[0][0]++;
                           board[1][position] = 0;
                           board[0][position] = 0;
                           tmpTest = true;
                        }
                        if(!tmpTest)
                            board[row][position] += 1;
                        repeat = false;
                        break;
                        
                    }
                    else
                    {
                    seeds--;
                     boolean tmpTest = false;   
                    if(player == 0 && board[row][position] == 0  && board[0][position]!=0)//do you take opponents seeds
                        {
                            board[0][7] += board[0][position];
                           board[0][7]++;
                           board[1][position] = 0;
                           board[0][position] = 0;
                           tmpTest = true;
                        }
                        if(!tmpTest)
                        board[row][position] += 1;
                        repeat = false;
                        break;
                       
                         
                    }
                } 
            }
        }
        
        TreeInterface newNode = new TreeComposite(network);
        newNode.setBoard(board);
        newNode.setPlayer(player);
        newNode.repeat = repeat;
     return newNode;   
        
    }
    
    
}
