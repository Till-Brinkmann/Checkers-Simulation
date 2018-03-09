# Checkers-Simulation

This project is made for the "Jugend Forscht" competition.

Goal is to program a versatile platform to test and compare different algorithms against one another by letting them play checkers.

In the finished program you will be able to add your one checkers playing algorithm as a module written in java.

## How to use
### Installing
This should be really easy. Just press the download button in the branch 'master' unzip 'CS-2.0' and start the included jar file. You will, however, need the latest [Java version](https://www.java.com/de/).
### Starting a game
Simply click on Game>new Game. In the upcoming window you can choose what players should participate in this new game, if the game should be recorded (for later analysis or reloading of certain situations. See Load and save games), the speed of the ai  (so you can see what it is doing) and an optional game name (only required for saving the game). In the two drop down menus you will see every valid player placed in the [resources/AI](resources/AI) directory and a 'player' option. This stands for the human player. When you are done just click confirm.
### Playing/managing games
By clicking on  Game>save game, the situation on the board, is saved as a .pfs file in the [resources/playfieldSaves](resources/playfieldSaves) directory. If you want to load this specific situation, you click Game>load game and selected the .pfs file.
During a game you can also stop and pause the game by clicking on Run. The console gives you current information about the happenings in the program.
### How to train your own Neural Network
For that you can use the checkers simulation nnt (neural network training) program.
### Adding your own player
If you want to program your own player in java, you can add it to the available players. But it has to have the interface player implemented. Moreover, the class file has to be copied in the [resources/AI](resources/AI)directory. After that it should appear in the two drop down menus when a new game is going to be created.
### More setup possibilities
Sometimes testing gets a little boring, don't you think? Therefore we implemented some extra features, so you can enjoy this vivid and amazing checker simulation experience even more. 
The own built-in Music Player comes with various special features including playing music, stopping music and skipping music. WOW! JUSt WOWW!
Or with our RGB full-HD color changer you can enhance your experience to the full extent. 
Personalizable content is a big deal for us, because when we develop such an amazing product we always think about the user. Everyone is different and therefore our product is, too. It's great. We love it.
