Overview
--------
This program uses an Artificial Neural Network(NN) to control an AI player against which you can play the board game Mancala. This neural network was created in an unsupervised fasion using a PSO (Particle Swarm Optimizer) to train it. This means that the game rules are provided to the system but no other input or strategies are provided. It thus has to "learn" how to play the game by itself. The only feedback the PSO and NN revives is if the last game was a Win, Loss or Draw.

Furthermore an Alpha-Beta pruning tree is used to navigate through different game states (board positions). This needed to be adapted from the traditional implementation due to the fact that a player can make more than one move after each other in Mancala. The Alpha-Beta pruning tree uses the NN to evaluate each board position.

The provided code contains:
- The code used to train the NN
- The code used to play the game
- The code used to test the AI vs a Random Opponent (See 3)

NB: Ply depth refers to the number of moves the AI can see in advance. This effects how good the moves made by the AI are but also increases computational time. This becomes a consideration when choosing a ply depth for training. 4 should generally be enough. I have however included some files containing already trained data on different ply depths for immediate testing.


1. To run the program
---------------------
Ensure you have ant installed, Alternatively you can compile the code however you wish
cd into the "MancalaNN" folder and type 
	"ant compile jar run"

You will now be presented with a dialog asking you to choose you initial ply depth
Choose any value between 1-10 (you will be able to change this later)

2. Using the program 
--------------------

2.1 Training a new neural network
---------------------------------
To train a new AI player, simply press the "train" button.
You will be prompted to enter the desired number of iterations.
In the terminal window where you ran the program training progress will be displayed
and the program will notify you there once it is done training.

you can now simply start playing against the newly trained AI player.

This newly trained player is also saved in the file called AIPlayer.txt

(There are also intermittent saves made in tmpFile.txt so you can see the progress of the training while it is still busy,
this can be useful for long training )

NB! It is very important that the ply depth is set to the amount you want the AI to train
with before you start training

2.2 Loading a previously trained neural network
-----------------------------------------------
Simply press the Load AI button.
You will be prompted to enter the file name that contains the stored weights.
If the terminal window prints out "65" two times the load was successful.

NB! Please ensure that this file is located in the "MancalaNN" folder

2.3 Playing the Game
--------------------
You are always player 0 and the AI is always player 1

There are two Mancala boards displayed on the screen. You will always
only interact with the top one.

There is an indicator at the top of the screen which shows which player may move next.

To make a move click on the position you wish to move in the bottom row of the top board (Just above all the buttons).

Whenever the indicator says player 0 you may make a move.
Whenever the indicator says player 1 the AI may make a move. In order for this to happen you have to press the "Move Again" button
You have to press that button for every single move the AI makes (Basically until the indicator says player 0 again).
this is so that you can clearly see what each move is that the AI makes.

The bottom board shows the previous board position for easy comparison.

When you can no longer make any moves or the AI can no longer make any moves just press any of your empty slots (if the indicator says player 0)
or "Move again" (if the indicator says player 1)

The final scores will then be shown in the terminal.

If you wish to make the first move in a game simply make whatever move you wish.
If however you want the AI to start, simply press the AI Start button.

If you wish to restart the game or simply play another press the restart button.

2.4 Changing the ply depth
-------------------------

If you wish to change the ply depth press the "Ply Depth" button.
You will be prompted to enter the new desired ply depth.

3. Testing the solution against random players
---------------------------------------------
This is usefull to see how "smart" an AI player is

Simply cd into the "Tournament" folder and type ant compile jar run

The application will promt you to enter the required ply depth and the file name wich has the weights stored.

Once you entered these parameters the application will play the AI in 10 tournaments
Each tournament consists of 150 games where the opponent will only make random moves

At the end of each tournament it will show the AI's score out of a possible 600 and also the percentage(Basically what percentage of the games you won) out of that possible 600


NB Please ensure that the file you wish to read from is in the "Tournament" directory

