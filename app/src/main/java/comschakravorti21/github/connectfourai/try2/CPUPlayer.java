package comschakravorti21.github.connectfourai.try2;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import comschakravorti21.github.connectfourai.MainActivity;

/**
 * Created by Development on 11/23/17.
 */

public class CPUPlayer {

    public static final int MAX_DEPTH = 7;

    public int generateMove(int[][] currentState) {
        int[] bestMove = maximizePlay(currentState, 1, MainActivity2.PLAYER_2);
        return bestMove[0];
    }

    public int[] maximizePlay(int[][] currentState, int depth, int player) {
        int[] ret = new int[]{-1, Integer.MIN_VALUE};

        if(depth == MAX_DEPTH) {
            ret[1] = (int)staticEval(currentState, player);
            return ret;
        }

        int otherPlayer = (player == MainActivity2.PLAYER_1) ?
                MainActivity2.PLAYER_2 : MainActivity2.PLAYER_1;

        for(Integer[] move : Gameboard2.possibleMoves(currentState)) {
            //Log.d("Move", Arrays.toString(move));
            int[][] newState = Gameboard2.deepCopyState(currentState);
            newState[move[0]][move[1]] = player;

            int[] minimizedPlay = minimizePlay(currentState, depth+1, otherPlayer);
            if(minimizedPlay[1] >= ret[1] || ret[0] == -1) {
                ret[0] = move[1];
                ret[1] = minimizedPlay[1];
            }
        }

        return ret;
    }

    public int[] minimizePlay(int[][] currentState, int depth, int player) {
        int[] ret = new int[]{-1, Integer.MAX_VALUE};

        if(depth == MAX_DEPTH) {
            ret[1] = (int)staticEval(currentState, player);
            return ret;
        }

        int otherPlayer = (player == MainActivity2.PLAYER_1) ?
                MainActivity2.PLAYER_2 : MainActivity2.PLAYER_1;

        for(Integer[] move : Gameboard2.possibleMoves(currentState)) {
            int[][] newState = Gameboard2.deepCopyState(currentState);
            newState[move[0]][move[1]] = player;

            int[] maximizedPlay = maximizePlay(currentState, depth+1, otherPlayer);
            if(maximizedPlay[1] < ret[1] || ret[0] == -1) {
                ret[0] = move[1];
                ret[1] = maximizedPlay[1];
            }
        }

        return ret;
    }

    public double staticEval(int[][] currentState, int player) {
        //THIS IS CURRENTLY A VERY NAIVE FORM OF STATIC EVALUATION, BUT IS
        //USEFUL FOR TESTING NONETHELESS

        //If we are evaluating for human player (PLAYER_1), then we'll
        //multiply the eval by -1 (minimizing human player score), otherwise
        //leave score unaltered
        double ret = 0;
        int maxInRow = 0;

        /*
            Start with primitive evaluation –– # of 3 in a row * 20 * mult
            If there are 4 in a row, return 1000 (game over)
         */

        int multiplier = (player == MainActivity2.PLAYER_1) ? 1 : 1; //-1 : 1

        //Check each row individually for # of consecutive pieces
        int deltaX = 1;
        int deltaY = 1;
        for (int row = 0; row < Gameboard2.ROWS; row += deltaY, maxInRow = 0) {
            for (int col = 0; col < Gameboard2.COLUMNS; col += deltaX) {

                //If we find a piece belonging to player, increment maxInRow
                if (currentState[row][col] == player) {
                    maxInRow++;
                }
                //If we don;t find a piece, then calculate the associated value
                //with the running max (not including single pieces)
                else if (maxInRow > 1) {
                    maxInRow += (maxInRow == 4) ? 2 : 0;
                    ret += Math.pow(10, maxInRow) * multiplier;
                    maxInRow = 0;
                } else {
                    maxInRow = 0;
                }
            }

            //It is possible that traversing through the column to the bottom never results
            //in a '0' position, so be sure to add remainder
            if (maxInRow != 0) {
                maxInRow += (maxInRow == 4) ? 2 : 0;
                ret += Math.pow(10, maxInRow) * multiplier;
            }
        }
        maxInRow = 0; //Reset max in row for next iteration set

        //Check each column individually for # of consecutive pieces
        for (int col = 0; col < Gameboard2.COLUMNS; col += deltaX, maxInRow = 0) {
            for (int row = 0; row < Gameboard2.ROWS; row += deltaY) {

                if (currentState[row][col] == player) {
                    maxInRow++;
                } else if (maxInRow > 1) {
                    maxInRow += (maxInRow == 4) ? 2 : 0;
                    ret += Math.pow(10, maxInRow) * multiplier;
                    maxInRow = 0;
                } else {
                    maxInRow = 0;
                }
            }

            if (maxInRow != 0) {
                maxInRow += (maxInRow == 4) ? 2 : 0;
                ret += Math.pow(10, maxInRow) * multiplier;
            }
        }
        maxInRow = 0;

        //Checking diagonally (top-left t0 bottom-right direction scenarios)
        int[] startingRows = new int[]{2, 1, 0, 0, 0, 0};
        int[] startingCols = new int[]{0, 0, 0, 1, 2, 3};
        for (int i = 0; i < startingRows.length && i < startingCols.length; i++) {
            for (int row = startingRows[i], col = startingCols[i]; row < Gameboard2.ROWS && col < Gameboard2.COLUMNS;
                 row += deltaY, col += deltaX) {

                if (currentState[row][col] == player) {
                    maxInRow++;
                } else if (maxInRow > 1) {
                    maxInRow += (maxInRow == 4) ? 2 : 0;
                    ret += Math.pow(10, maxInRow) * multiplier;
                    maxInRow = 0;
                } else {
                    maxInRow = 0;
                }
            }

            if (maxInRow != 0) {
                maxInRow += (maxInRow == 4) ? 2 : 0;
                ret += Math.pow(10, maxInRow) * multiplier;
            }
        }

        //Checking diagonally (top-right t0 bottom-left direction scenarios)
        deltaY = -1;
        startingRows = new int[]{3, 4, 5, 5, 5, 5};
        for (int i = 0; i < startingRows.length && i < startingCols.length; i++) {
            for (int row = startingRows[i], col = startingCols[i]; row >= 0 && col < Gameboard2.COLUMNS;
                 row += deltaY, col += deltaX) {

                if (currentState[row][col] == player) {
                    maxInRow++;
                } else if (maxInRow > 1) {
                    maxInRow += (maxInRow == 4) ? 2 : 0;
                    ret += Math.pow(10, maxInRow) * multiplier;
                    maxInRow = 0;
                } else {
                    maxInRow = 0;
                }
            }

            if (maxInRow != 0) {
                maxInRow += (maxInRow == 4) ? 2 : 0;
                ret += Math.pow(10, maxInRow) * multiplier;
            }
        }


        return ret;
    }
}
