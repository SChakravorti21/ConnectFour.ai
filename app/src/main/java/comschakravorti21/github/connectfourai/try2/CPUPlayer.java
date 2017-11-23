package comschakravorti21.github.connectfourai.try2;

import android.util.Log;

import java.util.Arrays;

import comschakravorti21.github.connectfourai.MainActivity;

/**
 * Created by Development on 11/23/17.
 */

public class CPUPlayer {

    public static final int MAX_DEPTH = 7;
    public static final int MAX_SCORE = 1000000;

    public int minimax(int[][] currentState, int player) {
        int ret = (int)(Math.random()*7);
        double eval;
        double highestEval = 0;

        for(int i = 0; i < Gameboard2.COLUMNS; i++) {
            int row = Gameboard2.rowIfPlaced(i, currentState);
            if(row != -1) {
                //currentState[row][i] = MainActivity2.PLAYER_2;
                eval = minimaxRec(currentState, MainActivity2.PLAYER_2, true, 1);
                Log.d("EVAL", "" + eval);
                if(eval > highestEval) {
                    highestEval = eval;
                    ret = i;
                }

                currentState[row][i] = 0; //undo the move that was previously done
            }
        }

        return ret;
    }

    //Returns the static evaluations from levels 1 through depth (level 0 being root)
    public double minimaxRec(int[][] currentState, int player, boolean maximizing, int depth) {
        if(depth == MAX_DEPTH) {
            return staticEval(currentState, player);
        }

        double bestResult = 0;
        double eval = 0;
        int newPlayer = (player == MainActivity2.PLAYER_1) ?
                MainActivity2.PLAYER_2 : MainActivity2.PLAYER_1; //The player on next round

        if(maximizing) {
            //int highestEvalCol = (int)Math.random()*7; //pick random column as default
            double highestEval = Integer.MIN_VALUE;

            for(int i = 0; i < Gameboard2.COLUMNS; i++) {
                int row = Gameboard2.rowIfPlaced(i, currentState);
                if(row != -1) {
                    currentState[row][i] = player;
                    eval = minimaxRec(currentState, newPlayer, !maximizing, depth+1);
                    if(depth == 1) {
                        Log.d("EVAL depth 1", "" + eval);
                        Log.d("Current state", Arrays.deepToString(currentState));
                    }

                    if(eval > highestEval) {
                        highestEval = eval;
                    }

                    currentState[row][i] = 0; //undo the move that was previously done
                }
            }

            bestResult = highestEval;
        } else {
            //int highestEvalCol = (int)Math.random()*7; //pick random column as default
            double lowestEval = Integer.MAX_VALUE;

            for(int i = 0; i < Gameboard2.COLUMNS; i++) {
                int row = Gameboard2.rowIfPlaced(i, currentState);
                if(row != -1) {
                    currentState[row][i] = player;
                    eval = minimaxRec(currentState, newPlayer, !maximizing, depth+1);

//                    if(depth == 2) {
//                        Log.d("EVAL depth 2", "" + eval);
//                        Log.d("Current state", Arrays.deepToString(currentState));
//                    }

                    if(eval < lowestEval) {
                        lowestEval = eval;
                    }

                    currentState[row][i] = 0; //undo the move that was previously done
                }
            }

            bestResult = lowestEval;
        }

        return bestResult;
    }

    public double staticEval(int[][] currentState, int player) {
        //THIS IS CURRENTLY A VERY NAIVE FORM OF STATIC EVALUATION, BUT IS
        //USEFUL FOR TESTING NONETHELESS

        //If we are evaluating for human player (PLAYER_1), then we'll
        //multiply the eval by -1 (minimizing human player score), otherwise
        //leave score unaltered
        int multiplier = (player == MainActivity2.PLAYER_1) ? 1 : 1; //-1 : 1
        double ret = 0;
        int maxInRow = 0;

        /*
            Start with primitive evaluation –– # of 3 in a row * 20 * mult
            If there are 4 in a row, return 1000 (game over)
         */

        //Check each row individually for # of consecutive pieces
        int deltaX = 1;
        int deltaY = 1;
        for(int row = 0; row < Gameboard2.ROWS; row += deltaY, maxInRow = 0) {
            for(int col = 0; col < Gameboard2.COLUMNS; col += deltaX) {

                //If there is a win, there is no point in checking anymore
                if (Gameboard2.checkWin(row, col, currentState, player)) {
                    return (multiplier * MAX_SCORE);
                }

                //If we find a piece belonging to player, increment maxInRow
                if (currentState[row][col] == player) {
                    maxInRow++;
                }
                //If we don;t find a piece, then calculate the associated value
                //with the running max (not including single pieces)
                else if(maxInRow > 1){
                    ret += Math.pow(10, maxInRow);
                    maxInRow = 0;
                } else {
                    maxInRow = 0;
                }
            }

            //It is possible that traversing through the column to the bottom never results
            //in a '0' position, so be sure to add remainder
            if(maxInRow != 0){
                ret += Math.pow(10, maxInRow);
            }
        }
        maxInRow = 0; //Reset max in row for next iteration set

        //Check each column individually for # of consecutive pieces
        for(int col = 0; col < Gameboard2.COLUMNS; col += deltaX, maxInRow = 0) {
            for(int row = 0; row < Gameboard2.ROWS; row += deltaY) {

                if (Gameboard2.checkWin(row, col, currentState, player)) {
                    return (multiplier * MAX_SCORE);
                }

                if (currentState[row][col] == player) {
                    maxInRow++;
                } else if(maxInRow > 1){
                    ret += Math.pow(10, maxInRow);
                    maxInRow = 0;
                } else {
                    maxInRow = 0;
                }
            }

            if(maxInRow != 0){
                ret += Math.pow(10, maxInRow);
            }
        }
        maxInRow = 0;

        //Checking diagonally (top-left t0 bottom-right direction scenarios)
        int[] startingRows = new int[]{2, 1, 0, 0, 0, 0};
        int[] startingCols = new int[]{0, 0, 0, 1, 2, 3};
        for(int i = 0; i < startingRows.length && i < startingCols.length; i++) {
            for(int row = startingRows[i], col = startingCols[i]; row < Gameboard2.ROWS && col < Gameboard2.COLUMNS;
                row += deltaY, col += deltaX) {

                if (Gameboard2.checkWin(row, col, currentState, player)) {
                    return (multiplier * MAX_SCORE);
                }

                if (currentState[row][col] == player) {
                    maxInRow++;
                } else if(maxInRow > 1){
                    ret += Math.pow(10, maxInRow);
                    maxInRow = 0;
                } else {
                    maxInRow = 0;
                }
            }

            if(maxInRow != 0){
                ret += Math.pow(10, maxInRow);
            }
        }

        //Checking diagonally (top-right t0 bottom-left direction scenarios)
        deltaY = -1;
        startingRows = new int[]{3, 4, 5, 5, 5, 5};
        for(int i = 0; i < startingRows.length && i < startingCols.length; i++) {
            for(int row = startingRows[i], col = startingCols[i]; row >= 0 && col < Gameboard2.COLUMNS;
                row += deltaY, col += deltaX) {

                if (Gameboard2.checkWin(row, col, currentState, player)) {
                    return (multiplier * MAX_SCORE);
                }

                if (currentState[row][col] == player) {
                    maxInRow++;
                } else if(maxInRow > 1){
                    ret += Math.pow(10, maxInRow);
                    maxInRow = 0;
                } else {
                    maxInRow = 0;
                }
            }

            if(maxInRow != 0){
                ret += Math.pow(10, maxInRow);
            }
        }
        return ret*multiplier;

    }
}
