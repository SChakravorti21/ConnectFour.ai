package comschakravorti21.github.connectfourai;

import android.os.AsyncTask;
import android.telecom.Call;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Athu on 11/4/2017.
 */

public class CPU_Player {

    public MiniMaxNode root;
    private int difficulty;
    private short[] gameState;
    private static final int DEPTH = 6;
    private ArrayList<MiniMaxNode> lastLayer;

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
        lastLayer = new ArrayList<>(120000);
        //for loop find empty col, make nodes fo each empty column
        initTree(root, MainActivity.PLAYER_1, 0);

        GameBoard.printbool = true;
    }

    public void initTree(MiniMaxNode node, short playerNum, int currentDepth){
        //for loop find empty col, make nodes fo each empty column

        short[] gameState = node.getGameState();

        short newPlayer = (playerNum == MainActivity.PLAYER_1) ? MainActivity.PLAYER_2 : MainActivity.PLAYER_1;

        //Log.d("BEFORE INSERTS Rows", "depth " + currentDepth + ": " + Arrays.toString(gameState));

        for(int c = 0; c < GameBoard.COLUMNS; c++){
            short value = GameBoard.getElement(0, c, gameState);

            int r = rowIfPlaced(c, gameState);

            MiniMaxNode n = null;

            if(r != -1) {
                //Log.d("Thing", "row " + r + ", depth " + currentDepth);
                //Log.d("Rows", "" + Arrays.toString(gameState));
                n = new MiniMaxNode(gameState, r, c, newPlayer, node.getStaticValue());
                node.addChild(n);
            } else {
                //Log.d("unable to add child", "row " + r + ", depth " + currentDepth);
                //Log.d("Rows", "" + Arrays.toString(gameState));
            }

            if(n != null && currentDepth < DEPTH) {
                initTree(n, newPlayer, currentDepth + 1);
            } else {
                //At last layer
                lastLayer.add(n);
                //n.setStaticValue(GameBoard.strongStaticEval(node.getGameState(), GameBoard.PLAYER2_BIT));
            }
        }

        //Log.d("AFTER INSERT Num Child", "" + node.getChildren().size() + ", depth" + currentDepth + "\n\n");
        //Log.d("Rows", "" + Arrays.toString(gameState));
    }

    //No argument method
    public int computeBestMove() {
        //This method also needs to call the static version of the placePiece method
        //in order to alter the board.

        //maximizing is false b/c trying to minimize human's score
        int bestCol = computeBestMove(root, Integer.MIN_VALUE, Integer.MAX_VALUE, true); //CPU tries to minimize human's score

        return bestCol;
    }

    //recursive method
    public int computeBestMove(MiniMaxNode node, int alpha, int beta, boolean maximizing) {
        if(node == null) //safety null check, as such should not have to take place
            return 0;

        ArrayList<MiniMaxNode> children = node.getChildren();
        if(children == null || children.size() == 0) {
            //Log.d("Leaf Node", "Reached end of tree");
            return node.getStaticValue();
        }

        int bestColumn = 0;
        boolean hasFoundBetterMove = false;
        int[] vals = new int[children.size()];
        for(int i = 0; i < vals.length; i++) {
            vals[i] = computeBestMove(children.get(i), alpha, beta, !maximizing);

            if(maximizing) {
                if(vals[i] > vals[bestColumn]) {
                    bestColumn = i;
                    hasFoundBetterMove = true;
                    //alpha = vals[i];
                } else if(vals[i] < alpha) {
                    //return bestColumn;
                }
            } else if(!maximizing) {
                if(vals[i] < vals[bestColumn]) {
                    bestColumn = i;
                    hasFoundBetterMove = true;
                    //beta = vals[i];
                } else if(vals[i] > beta) {
                    //return bestColumn;
                }
            }
        }

        if(hasFoundBetterMove && bestColumn != 0)
            return bestColumn;
        else
            return (int)(Math.random()*7);
    }

    public void shiftRoot(int row, int col, short player) {
        //Check children and reset root once we find the corresponding player

        if(root == null || root.getChildren() == null) {
            Log.d("ROOT", "SOMETHING IS BROKEN");
            return;
        }

        for (MiniMaxNode child: root.getChildren()) {
            short val = GameBoard.getElement(row, col, child.getGameState());
            //Log.d("Value", "row: " + row + ", col: " + col + ", val: " + val);

            if( val == player) {
                //root.setChildren(null);
                Log.d("Shifting root", "TRUE");
                root = child;


                MiniMaxNode ptr = root;
                ArrayList<MiniMaxNode> children = ptr.getChildren();
                while(ptr != null && children != null && children.size() > 0) {
                    ptr = children.get(0);
                    children = ptr.getChildren();
                }
                int firstIndex = lastLayer.indexOf(ptr);
                if (firstIndex < 0)
                    firstIndex = 0;

                ptr = root;
                children = ptr.getChildren();
                while(ptr != null && children != null && children.size() > 0) {
                    for(int i = children.size() - 1; i >= 0; i--) {
                        if(children.get(i) != null) {
                            ptr = children.get(0);
                            children = ptr.getChildren();
                            break;
                        }
                    }

                }
                int lastIndex = lastLayer.indexOf(ptr);
                if(lastIndex < 0)
                    lastIndex = 0;

                //call replenish method
                //ReplenishAsync task = new ReplenishAsync(player);
                //task.execute();
                //replenishTree(root, player);
                replenishTree(player, firstIndex , lastIndex);
                return;
            }
        }
    }


    public void replenishTree(short lastPlayer, int startIndex, int endIndex) {
        //Log.d("Replenish", "call on replenish");
        ArrayList<MiniMaxNode> newSet = new ArrayList<>(120000);

        short newPlayer = (lastPlayer == MainActivity.PLAYER_1) ? MainActivity.PLAYER_2 : MainActivity.PLAYER_1;

        MiniMaxNode node = null;
        for(int i = startIndex; i < endIndex + 1; i++) {
            node = lastLayer.get(i);

            short[] gameState = Arrays.copyOf(node.getGameState(), 6);

            for(int c = 0; c < GameBoard.COLUMNS; c++){

                int r = rowIfPlaced(c, gameState);

                if(r != -1) {
                    MiniMaxNode n = new MiniMaxNode(gameState, r, c, newPlayer, node.getStaticValue());
                    node.addChild(n);
                    newSet.add(n);
                }
            }
        }

        //lastLayer = null;
        lastLayer = (ArrayList<MiniMaxNode>)newSet.clone();
        //Log.d("Replenish", "FINISH on replenish");
    }


    public void replenishTree(MiniMaxNode node, short player) {

        ArrayList<MiniMaxNode> children = node.getChildren();

        short newPlayer = (player == MainActivity.PLAYER_1) ? MainActivity.PLAYER_2 : MainActivity.PLAYER_1;

        //if no children, we must add a bunch of new children here.
        if(children == null || children.size() == 0){
            //Log.d("Reached End", "Found leaf node");
            short[] gameState = Arrays.copyOf(node.getGameState(), 7);

            for(int c = 0; c < GameBoard.COLUMNS; c++){

                int r = rowIfPlaced(c, gameState);

                if(r != -1) {
                    MiniMaxNode n = new MiniMaxNode(gameState, r, c, newPlayer, node.getStaticValue());
                    node.addChild(n);
                }
            }

        }
        else {
            for (int i = 0; i < children.size(); i++) {
                //replenishTree(children.get(i), newPlayer);
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


    private class ReplenishAsync extends AsyncTask<Short, Void, Void> {

        short player;
        public ReplenishAsync(short player) {
            this.player = player;
        }

        @Override
        protected Void doInBackground(Short... shorts) {
            replenishTree(root, player);

            Log.d("Replenish", "COMPLETE");
            return null;
        }
    }
}

