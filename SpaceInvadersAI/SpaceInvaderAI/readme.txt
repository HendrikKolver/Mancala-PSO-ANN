-------------------------------
Compile:
-------------------------------

To compile the project run compile.bat

-------------------------------
Run:
-------------------------------

To run the project run, run.bat with a single parameter: The relative path where the test harnass will output the state.json file as well as the map file

-------------------------------
Project structure and strategy
-------------------------------
The strategy used was to create a Artificial neural network that has been trained using a Particle swarm optimizer (PSO) in a semi-unsupervised fashion. A group of AI bots
was thus created at random and provided with the game rules and a means to play against each other and then used a PSO to develop their own strategies for the game.
The neural network is used to evaluate different board positions. A game tree is used to create possible game positions that are then evaluated by the neural network to determine
the best possible move to make in a given situation.

The project thus consists of the following logical components:
-A basic game engine capable of playing the game
-A PSO that can be used to train a neural network
-A neural network that is used to evaluate board positions
-A board position builder that is used to build a game tree of the different game states

Feel free to contact me if you want any more information
hendrik.kolver@gmail.com