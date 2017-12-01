# ConnectFour.ai
An Android App to play Connect Four against an AI

## Motivation
We began building this project at HackUMass 2017, our first hackathon!

After having completed half of the Data Structures course at Rutgers (New Brunswick), we had learned the true
power of recursion in manipulating certain data structures and solving algorithmic problems. We figured that
a good way to test our newly-acquired problem-solving skills, would be to create an AI for a zero-sum game.
Tic-tac-toe seemed too simple, so we decided to work on something related to Connect Four. We had heard of
Minimax, but never knew how it worked. Over the course of 24 hours, we learned the basics of Minimax and tried to
create an implementation of it. Of course, the app was nowhere near complete after the hackathon ended, but we have refined
the algorithm to work now.

## Minimax Algorithm
The Minimax algorithm essentially looks ahead a certain (defined) number of moves in every possible game state to guage 
which situation is best for the computer. This is achieved through a series of alternating calls to ___maximizePlay___ (when the
computer's score needs to maximized) and ___minimizePlay___ (when the human's score needs to be minimized). Once the depth
limit is reached, both methods simply return the ___static evaluation___ of the board; the static eval is simply
a measure of how _useful_ a board is for a specified player. To put it more simply, at each turn, the computer tries
to maximize its score and the human tries to minimize the computer's score. Since we defined the max depth to be 9,
the computer looks ahead 9 moves in every possible scenario and picks the safest move or the move that will bring it
to a fast victory. 

## Alpha-Beta Pruning
With an original max depth of 7 and a branching factor of 7 (since there are 7 columns that a player might be able to
play a piece in), the time complexity of vanilla Minimax is O(b^m), or about 40 million cases analyzed. This was unacceptably
slow, so we added alpha-beta pruning to our implementation of minimax. Alpha-beta pruning simply eliminates certain game
trees if a player can already guarantee a __better__ move. This significantly cuts downs move generation (literally
pruning the game tree) and static evaluation, improving the runtime of minimax. With this improved version, we were able
to increase the max depth to 9 with pretty decent performance, creating a virtually unbeatable Connect Four AI.

## Media
Here are some sample results of playing against Minimax's decisions.

(The human is Player 1 with yellow pieces).

### Minimax forces a win

![Image of computer forcing a win](https://github.com/SChakravorti21/ConnectFour.ai/blob/master/media/minimax%203.png)

### I was able to get a very, very narrow win

![Image of computer forcing a win](https://github.com/SChakravorti21/ConnectFour.ai/blob/master/media/minimax%202.png)

### A sample game against Minimax

[![Video of game against Minimax](https://img.youtube.com/vi/mPIiHaM41Ys/0.jpg)](https://www.youtube.com/watch?v=mPIiHaM41Ys)]
