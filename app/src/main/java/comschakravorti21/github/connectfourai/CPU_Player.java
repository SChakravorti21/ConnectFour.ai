package comschakravorti21.github.connectfourai;

import android.util.Log;

import java.util.ArrayList;

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

    public void resetTree() {
        root = new MiniMaxNode();
        this.gameState = new short[6];

        initTree();
    }

    //preloads a tree of a certain depth
    public void initTree(){
        //for loop find empty col, make nodes fo each empty column
        initTree(root, MainActivity.PLAYER_1, 0);

        GameBoard.printbool = true;
    }

    public void initTree(MiniMaxNode node, short playerNum, int currentDepth){
        //for loop find empty col, make nodes fo each empty column

        short[] gameState = node.getGameState();

        short newPlayer = ((playerNum & 0b11) == MainActivity.PLAYER_1) ? MainActivity.PLAYER_2 : MainActivity.PLAYER_1;

        for(int c = 0; c < GameBoard.COLUMNS; c++){
            //short value = GameBoard.getElement(0, c, gameState);

            int r = rowIfPlaced(c, gameState);
            if(r != -1){

                MiniMaxNode n = new MiniMaxNode(gameState, r, c, newPlayer, node.getStaticValue());
                //n.setGameState(r, c, newPlayer);
                node.addChild(n);
                //Log.d("current depth",  " " + currentDepth );

                if(currentDepth < DEPTH){
                    n.instantiateChildrenList();
                    initTree(n, newPlayer, currentDepth + 1);
                }
            }
        }
    }

    //No argument method
    public int computeBestMove() {
        //This method also needs to call the static version of the placePiece method
        //in order to alter the board.

        //maximizing is false b/c trying to minimize human's score
        int bestCol = computeBestMove(root, false); //CPU tries to minimize human's score

        return bestCol;
    }

    //recursive method
    public int computeBestMove(MiniMaxNode node, boolean maximizing) {
        if(node == null) //safety null check, as such should not have to take place
            return 0;

        ArrayList<MiniMaxNode> children = node.getChildren();
        if(children == null) {
            //Log.d("Leaf Node", "Reached end of tree");
            return node.getStaticValue();
        }

        int bestColumn = 0;
        int[] vals = new int[children.size()];
        for(int i = 0; i < vals.length; i++) {
            vals[i] = computeBestMove(children.get(i), !maximizing);
        }

        for(int i = 0; i < vals.length; i++) {
            if(!maximizing && vals[i] > vals[bestColumn]) {
                bestColumn = i;
            } else if(maximizing && vals[i] < vals[bestColumn]) {
                bestColumn = i;
            }
        }

        return bestColumn;
    }

    public void shiftRoot(int row, int col, short player) {
        //Check children and reset root once we find the corresponding player
        for (MiniMaxNode child: root.getChildren()) {
            short val = GameBoard.getElement(row, col, child.getGameState());
            Log.d("Value", "row: " + row + ", col: " + col + ", val: " + val);

            if( val == player) {
                Log.d("Shifting root", "TRUE");
                root = child;
                return;
            }
        }
    }

    public static int rowIfPlaced(int col, short[] gameState) {
        short c = (short)((GameBoard.COLUMNS - col - 1)*2);
        short zero = 0b11;

        for(int i = GameBoard.ROWS- 1; i >= 0; i--) {

            short data = gameState[i];

            if( ((data >> c) & zero) == 0b00){
                return i;
            }
        }

        return -1;
    }


    private int rowIfPlaced(int col){
        return rowIfPlaced(col, gameState);
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

