package comschakravorti21.github.connectfourai;

import android.util.Log;

/**
 * Created by Athu on 11/4/2017.
 */

public class CPU_Player {

    public MiniMaxNode root;
    private int difficulty;
    private short[] gameState;
    private final int DEPTH = 6;

    public CPU_Player(short[] gameS){
        this.root = new MiniMaxNode();
        this.gameState = gameS;
    }

    //preloads a tree of a certain depth
    public void initTree(){
        //for loop find empty col, make nodes fo each empty column
        initTree(root, MainActivity.PLAYER_1, 0);

    }

    public void initTree(MiniMaxNode node, byte playerNum, int currentDepth){
        //for loop find empty col, make nodes fo each empty column

        short[] gameState = node.getGamestate();

        for(int c = 0; c < GameBoard.COLUMNS; c++){
//            if(gameState[0][c] == 0){
//
//                int r = returnRowIfPlaced(c);
//
//                MiniMaxNode n = new MiniMaxNode(gameState, r, c, playerNum);
//                n.setGameState(r, c, playerNum);
//                node.addChild(n);
//                //Log.d("current dept",  " " + currentDepth );
//
//                if(currentDepth < DEPTH){
//                    initTree(n, (byte)(-1 *playerNum), currentDepth + 1);
//                }
//            }
        }
    }
    private int returnRowIfPlaced(int col){
        short c = (short)(GameBoard.COLUMNS - col);
        short zero = (short)0b11;

        for(int i = GameBoard.ROWS- 1; i >= 0; i--) {

            short data = gameState[i];

            if( ((data >> c) & zero) == 0){
                return i;
            }
        }

        return -1;
    }

    //adds an extra layer to the bottom of the tree after each move has been made,
    //so the depth of the tree stays constant
    public void addTreeLayer(){

    }

    //This set method works with the following method instrction .Basicaly, we pass in a game
    //state and then the class variable game state gets updated!!!!!!!!!!
    public void setGameState(short[] gs){
        this.gameState = gs;
    }

}

