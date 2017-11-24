package comschakravorti21.github.connectfourai.try2;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import comschakravorti21.github.connectfourai.MainActivity;

/**
 * Created by Development on 11/23/17.
 */

public class CPUPlayer {

    private static final int MAX_DEPTH = 9;
    private static final int[][] allUtilities = new int[][]{{3, 4, 5, 7, 5, 4, 3},
                                                            {4, 6, 8, 10, 8, 6, 4},
                                                            {5, 8, 11, 13, 11, 8, 5},
                                                            {5, 8, 11, 13, 11, 8, 5},
                                                            {4, 6, 8, 10, 8, 6, 4},
                                                            {3, 4, 5, 7, 5, 4, 3}};
    public static final int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
    public static final int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};

    public int generateMove(int[][] currentState) {
        int[] bestMove = maximizePlay(currentState, 1, MainActivity2.PLAYER_2,
                Integer.MIN_VALUE, Integer.MAX_VALUE);

        return bestMove[0];
    }

    public int[] maximizePlay(int[][] currentState, int depth, int player, int alpha, int beta) {
        int[] ret = new int[]{-1, Integer.MIN_VALUE};

        if(depth == MAX_DEPTH) {
            ret[1] = (int)betterStaticEval(currentState, player);
            return ret;
        }

        int otherPlayer = (player == MainActivity2.PLAYER_1) ?
                MainActivity2.PLAYER_2 : MainActivity2.PLAYER_1;

        for(Integer[] move : Gameboard2.possibleMoves(currentState)) {
            int[][] newState = Gameboard2.deepCopyState(currentState);
            int row = move[0];
            int col = move[1];
            newState[row][col] = player;
            if(Gameboard2.checkWin(row, col, newState, player)) {
                ret[0] = col;
                ret[1]= 1000000;
                break;
            }

            int[] minimizedPlay = minimizePlay(newState, depth+1, otherPlayer, alpha, beta);

            if(minimizedPlay[1] > ret[1] || ret[0] == -1) {
                ret[0] = move[1];
                ret[1] = minimizedPlay[1];
            }

            alpha = Math.max(alpha, ret[1]);
            if(beta <= alpha)
                break;
        }

        return ret;
    }

    public int[] minimizePlay(int[][] currentState, int depth, int player, int alpha, int beta) {
        int[] ret = new int[]{-1, Integer.MAX_VALUE};

        if(depth == MAX_DEPTH) {
            ret[1] = (int)betterStaticEval(currentState, player);
            return ret;
        }

        int otherPlayer = (player == MainActivity2.PLAYER_1) ?
                MainActivity2.PLAYER_2 : MainActivity2.PLAYER_1;

        for(Integer[] move : Gameboard2.possibleMoves(currentState)) {
            int[][] newState = Gameboard2.deepCopyState(currentState);
            int row = move[0];
            int col = move[1];
            newState[row][col] = player;
            if(Gameboard2.checkWin(row, col, newState, player)) {
                ret[0] = col;
                ret[1]= -1000000; //Fix to positive 100000?
                break;
            }

            int[] maximizedPlay = maximizePlay(newState, depth+1, otherPlayer, alpha, beta);

            if(maximizedPlay[1] < ret[1] || ret[0] == -1) {
                ret[0] = move[1];
                ret[1] = maximizedPlay[1];
            }

            beta = Math.min(beta, ret[1]);
            if(beta <= alpha)
                break;
        }

        return ret;
    }

    public int betterStaticEval(int[][] currentState, int player) {
        int otherPlayer = (player == MainActivity2.PLAYER_1) ? MainActivity2.PLAYER_2 : MainActivity2.PLAYER_1;
        int utility = 138;
        int sum = 0;

        for(int row = 0; row < allUtilities.length; row++) {
            for(int col = 0; col < allUtilities[row].length; col++) {
                if(currentState[row][col] == player) {
                    sum += allUtilities[row][col];
                } else if(currentState[row][col] == otherPlayer) {
                    sum -= allUtilities[row][col];
                }
            }
        }

        return utility + sum;
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
