
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hendr_000
 */
public class MainGameGui extends javax.swing.JFrame {

    /**
     * Creates new form MainGameGui
     */
    private boolean gameOver = false;
    private NeuralNetwork n;
    private JButton playerOneRow[];
    private JButton playerTwoRow[];
    private JButton playerOneMancala;
    private JButton playerTwoMancala;
    private JButton playerOneRowP[];
    private JButton playerTwoRowP[];
    private JButton playerOneMancalaP;
    private JButton playerTwoMancalaP;
    private int board[][];
    private int plyDepth;
    private int turn;
    private AIPlayer player0;
    private AIPlayer player1;
    private int iterations;
    private int p0Wins;
    private int p1Wins;
    private int ties;
    private int totalP1;
    private int totalP0;
    private int totalTies;
    private Trainer trainer;
   
    private double weights[];
    
    
    
    public MainGameGui() throws IOException, InterruptedException {
        initComponents();
        setBoard();
        turn = 0;
        plyDepth = Integer.parseInt(JOptionPane.showInputDialog("Initial ply depth: "));;
        int inputs = 14;
        int hidden = 4;
        int val = ((inputs+1)*hidden) + (hidden+1)*1;
        weights = new double[val];
       
        n = new NeuralNetwork(inputs,1,hidden,1);
        
         iterations = 10;
         p0Wins =0;
         p1Wins = 0;
         ties = 0;
         totalP1 = 0;
         totalP0 = 0;
         totalTies =0;
         trainer = new Trainer();
//         System.out.println("training");
//         n = trainer.train(iterations);
//         System.out.println("done");
//         player1 = new AIPlayer(plyDepth,1,n);
   
    }
    
    public void setBoard()
    {
        playerOneRow = new JButton[6];
        playerTwoRow = new JButton[6];
        playerOneRowP = new JButton[6];
        playerTwoRowP = new JButton[6];
        playerOneMancala = jButton14;
        playerTwoMancala = jButton13;
        
        playerOneRow[0] =  jButton7;
        playerOneRow[1] =  jButton8;
        playerOneRow[2] =  jButton9;
        playerOneRow[3] =  jButton10;
        playerOneRow[4] =  jButton11;
        playerOneRow[5] =  jButton12;
        
        playerTwoRow[0] =  jButton1;
        playerTwoRow[1] =  jButton2;
        playerTwoRow[2] =  jButton3;
        playerTwoRow[3] =  jButton4;
        playerTwoRow[4] =  jButton5;
        playerTwoRow[5] =  jButton6;
        
        playerOneMancalaP = jButton33;
        playerTwoMancalaP = jButton34;
        
        playerOneRowP[0] =  jButton26;
        playerOneRowP[1] =  jButton25;
        playerOneRowP[2] =  jButton24;
        playerOneRowP[3] =  jButton23;
        playerOneRowP[4] =  jButton22;
        playerOneRowP[5] =  jButton21;
        
        playerTwoRowP[0] =  jButton27;
        playerTwoRowP[1] =  jButton28;
        playerTwoRowP[2] =  jButton29;
        playerTwoRowP[3] =  jButton30;
        playerTwoRowP[4] =  jButton31;
        playerTwoRowP[5] =  jButton32;
        
        board = new int[2][8];
        
        board[0][0] = 0;
        board[0][7] = 0;
        
        for(int x=1; x< 7 ; x++)
        {
           board[0][x] = 4;
           board[1][x] = 4;
        }
        
        System.out.println("Board built success");
        
    }
    
    public void makeMove(int player, int position) throws InterruptedException
    {
        int seeds = board[player][position];
        int row = player;
        boolean doubleTurn = false;
        board[player][position] = 0;
        
        
        if(row == 0)
        {
            position -=1;
            seeds--;
            boolean tmpTest = false;
            if(seeds == 0)
            {
                
                if(board[row][position] == 0 && board[1][position] != 0)
                {
                    
                    board[0][0] += board[1][position];
                    board[1][position] = 0;
                    board[0][0]++;
                    board[0][position] = 0;
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
                    
                    changeTurn();
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
                if(board[row][position] == 0 && board[0][position] != 0)
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
                    changeTurn();
                }
            }
            
        }
        
        
        while(seeds != 0)
        {
            if(seeds > 1)
            {
                if(position == 0)
                {
                if(player == 0)
                {
                    seeds--;
                    board[row][position] += 1;
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
                        if(player == 0)
                        {

                            position =6;
                            row = 0;
                        }
                        else
                        {
                            seeds--;
                            row = 0;
                            board[row][position] += 1;
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
                    if(player == 0)
                    {
                        seeds--;
                        board[row][position] += 1;
                        position =1;
                        row = 1;
                    }
                    else
                    {
                        position =1;
                        row = 1;
                        //changeTurn();
                    }
                }
                else
                {
                    if(position == 7)
                    {
                        if(player == 0)
                        {

                            position =6;
                            row = 0;
                            //changeTurn();
                        }
                        else
                        {
                            seeds--;
                            row = 0;
                            board[row][position] += 1;
                            position =6;

                        }
                    } else if(row == 0)
                    {
                        boolean tmpTest = false;
                        seeds--;
                        if(player == 0 && board[row][position] == 0 && board[1][position]!=0) //do you take opponents seeds
                        {
                           //System.out.println("here");
                           board[0][0] += board[1][position];
                           board[0][0]++;
                           board[1][position] = 0;
                           board[0][position] = 0;
                           tmpTest = true;
                        }
                        if(!tmpTest)
                            board[row][position] += 1;
                        changeTurn();
                    }
                    else
                    {
                    seeds--;
                       boolean tmpTest = false; 
                    if(player == 1 && board[row][position] == 0  && board[0][position]!=0)//do you take opponents seeds
                        {
                            
                           board[0][7] += board[0][position];
                           board[0][7]++;
                           board[1][position] = 0;
                           board[0][position] = 0;
                           tmpTest = true;
                        }
                        
                        if(!tmpTest)
                            board[row][position] += 1;
                        changeTurn();
                         
                    }
                } 
            }
        }
        
        updateBoard(board);
        
    }
    
    public void updateBoard(int board[][]) throws InterruptedException
    {
        for(int x = 0; x<6; x++)
        {
            playerOneRowP[x].setText(playerOneRow[x].getText());
            
            playerTwoRowP[x].setText(playerTwoRow[x].getText());
        }
        
        playerOneMancalaP.setText(playerOneMancala.getText());
        playerTwoMancalaP.setText(playerTwoMancala.getText());
        
        for(int x = 0; x<6; x++)
        {
            playerOneRow[x].setText(Integer.toString(board[1][x+1]));
            
            playerTwoRow[x].setText(Integer.toString(board[0][x+1]));
        }
        
        playerOneMancala.setText(Integer.toString(board[0][7]));
        playerTwoMancala.setText(Integer.toString(board[0][0]));
        jLabel4.setText(Integer.toString(turn));
        
        
        
        
        //Thread.sleep(500);
    }
    
    public void changeTurn()
    {
        if (turn == 1)
        {
//            for(int x=0; x< playerOneRow.length;x++)
//               playerOneRow[x].setEnabled(true);
            jLabel4.setText("0");            
           turn = 0; 
        }    
        else
        {
//            for(int x=0; x< playerOneRow.length;x++)
//                playerOneRow[x].setEnabled(false); 
            jLabel4.setText("1"); 
            turn = 1;
        }
    }
    
    public boolean gameOver()
    {
        boolean over =false;
        if(turn == 0)
        {
            if (board[1][1] == 0 && board[1][2] == 0 && board[1][3] == 0 && board[1][4] == 0  && board[1][5] == 0  && board[1][6] == 0)
            {
               over = true; 
            }
        }
        
        if(turn == 1)
        {
            if (board[0][1] == 0 && board[0][2] == 0 && board[0][3] == 0 && board[0][4] == 0  && board[0][5] == 0  && board[0][6] == 0)
            {
               over = true; 
            }
        }
        
        if(over == true)
        {
            
            System.out.println("GameOver");
            
            board[0][0]+= board[0][1] +board[0][2]+board[0][3]+board[0][4]+board[0][5]+board[0][6];
            board[0][7]+= board[1][1] +board[1][2]+board[1][3]+board[1][4]+board[1][5]+board[1][6];
            System.out.println("Game Over!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("Scores:\n Player 0 = " + board[0][7] + "\n Player 1 = " + board[0][0]);
            
            gameOver = true;
            return true;
        }
        return false;
    }
    
    public void updateGrid(int grid[][])
    {
      for(int x=0; x<2;x++)
         {
             for(int i =0; i<8; i++)
             {  
                 board[x][i] = grid[x][i];
             }     
         }   
    }
    
    
    public void printBoard()
    {
        String s = "";
        for(int x =0; x<2;x++)
        {
            for(int j =0; j<8;j++)
            {
               s += ("["+ board[x][j] + "]" + ";");
            }
            s+=("\n");
        }
        System.out.println(s);
    }
    
    
    List<String> readSmallTextFile(String aFileName) throws IOException {
    
        List<String> tmp = new ArrayList<String>();
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField6 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        jTextField6.setText("jTextField6");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("4");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("4");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("4");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("4");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("4");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("4");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("4");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("4");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("4");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("4");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("4");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton13.setText("0");

        jButton14.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton14.setText("0");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Player 0");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Player 1");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Previous Position");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel4.setText("0");

        jButton15.setText("Train");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setText("AI Start");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setText("Load AI");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setText("MoveAgain");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setText("Ply Depth");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText("Restart");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setText("4");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setText("4");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton23.setText("4");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton24.setText("4");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jButton25.setText("4");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jButton26.setText("4");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jButton27.setText("4");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jButton28.setText("4");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton29.setText("4");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton30.setText("4");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jButton31.setText("4");
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jButton32.setText("4");
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jButton33.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton33.setText("0");

        jButton34.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton34.setText("0");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Player 1");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Player 0");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setText("Player Turn:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jButton13))
                                .addGap(46, 46, 46)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton7)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton8)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton9)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton10)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton11)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton12)
                                        .addGap(51, 51, 51)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButton14)
                                            .addComponent(jLabel1)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton1)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton2)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton3)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton4)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton5)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton6))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel5)
                                        .addComponent(jButton34))
                                    .addGap(46, 46, 46)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jButton26)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton25)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton24)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton23)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton22)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton21)
                                            .addGap(51, 51, 51)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jButton33)
                                                .addComponent(jLabel6)))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jButton27)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton28)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton29)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton30)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton31)
                                            .addGap(18, 18, 18)
                                            .addComponent(jButton32))))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jButton15)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButton19)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButton17)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButton16)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButton20)
                                    .addGap(18, 18, 18)
                                    .addComponent(jButton18))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(184, 184, 184)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(191, 191, 191)
                        .addComponent(jLabel3)
                        .addGap(185, 185, 185)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jLabel2))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton13)
                    .addComponent(jButton14))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton9)
                    .addComponent(jButton10)
                    .addComponent(jButton11)
                    .addComponent(jButton12)
                    .addComponent(jLabel1))
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton15)
                    .addComponent(jButton16)
                    .addComponent(jButton17)
                    .addComponent(jButton18)
                    .addComponent(jButton19)
                    .addComponent(jButton20))
                .addGap(42, 42, 42)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton27)
                    .addComponent(jButton28)
                    .addComponent(jButton29)
                    .addComponent(jButton30)
                    .addComponent(jButton31)
                    .addComponent(jButton32)
                    .addComponent(jLabel5))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton34)
                    .addComponent(jButton33))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton26)
                    .addComponent(jButton25)
                    .addComponent(jButton24)
                    .addComponent(jButton23)
                    .addComponent(jButton22)
                    .addComponent(jButton21)
                    .addComponent(jLabel6))
                .addGap(32, 32, 32))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Player 1 position 1
      
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        if(turn == 0 && !gameOver() && board[1][1] != 0)
        {
            try {
                makeMove(1,1);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        if(turn == 0 && !gameOver() && board[1][2] != 0)
        {
            try {
                makeMove(1,2);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        if(turn == 0 && !gameOver() && board[1][3] != 0)
        {
            try {
                makeMove(1,3);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        if(turn == 0 && !gameOver() && board[1][4] != 0)
        {
            try {
                makeMove(1,4);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        if(turn == 0 && !gameOver() && board[1][5] != 0)
        {
            try {
                makeMove(1,5);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        if(turn == 0 && !gameOver() && board[1][6] != 0)
        {
            try {
                makeMove(1,6);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
       
        
        try {
            try {
                // TODO add your handling code here:
                 double begin = System.currentTimeMillis();
                List<String> lines = new ArrayList<String>();
                n = trainer.train(iterations, plyDepth);
                player1 = new AIPlayer(plyDepth,1,n);
                
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
                
                writeSmallTextFile(lines, "AIPlayer.txt");
                 double end = System.currentTimeMillis();
                 double finalTime = ((end-begin)/3600000);
                 System.out.println("Training time(Hours): " + finalTime);
                
            } catch (IOException ex) {
                Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
             turn = 1;  
             TreeInterface answer = player1.guiPlay(board);
             if(!answer.repeat)
                 changeTurn();
             updateGrid(answer.getBoard());
        try {
             updateBoard(answer.getBoard());
        } catch (InterruptedException ex) {
            Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
                
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
        List<String> lines;
        
        //read from file
        try {
            String name = JOptionPane.showInputDialog("Name of file");
            lines = readSmallTextFile(name);
            if(lines.size()<1)
            {
                System.out.println("Cannot, file is empty please train first");
            }
            else
            {
                String tmp[] =lines.get(0).split(";");
                System.out.println(tmp.length);
                System.out.println(weights.length);
                for(int x=0; x<weights.length;x++)
                {
                    weights[x] = Double.parseDouble(tmp[x]);
                }
                n.updateWeights(weights);
            }

                //create the hopefully awesome AI
                player1 = new AIPlayer(plyDepth,1,n);
            
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
        }
       String w ="";

       
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
        if(turn== 1 && !gameOver())
        {
            try {
                TreeInterface answer = player1.guiPlay(board);
                updateGrid(answer.getBoard());
                updateBoard(answer.getBoard());
                if(!answer.repeat)
                {
                    changeTurn();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        plyDepth = Integer.parseInt(JOptionPane.showInputDialog("Enter Ply Depth (Larger than 12 not recommended)"));
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        turn = 0;
        setBoard();
        try {
            updateBoard(board);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            
            player1 = new AIPlayer(plyDepth,1,n);
        } catch (IOException ex) {
            Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton31ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton32ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainGameGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainGameGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainGameGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGameGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    new MainGameGui().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainGameGui.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
