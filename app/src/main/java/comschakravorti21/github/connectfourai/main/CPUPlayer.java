package comschakravorti21.github.connectfourai.main;

/**
 * Created by Development on 11/23/17.
 */

public class CPUPlayer {

    /**
     * The depth through which CPU searches for a valuable move.
     * Currently set to 9 (so CPU essentially looks 9 moves ahead and picks the best one).
     */
    private static final int MAX_DEPTH = 9;
    /**
     * Stores the overall utilities of all positions on a board. Each utility value
     * determines how valuable a position is based on the number of winning scenarios
     * that includes the given position.
     */
    private static final int[][] allUtilities = new int[][]{{3, 4, 5, 7, 5, 4, 3},
                                                            {4, 6, 8, 10, 8, 6, 4},
                                                            {5, 8, 11, 13, 11, 8, 5},
                                                            {5, 8, 11, 13, 11, 8, 5},
                                                            {4, 6, 8, 10, 8, 6, 4},
                                                            {3, 4, 5, 7, 5, 4, 3}};

    /**
     * Generates the best possible move by looking 9 moves ahead in all possibilities.
     * @param currentState the current game state
     * @return the best CPU move
     */
    public int generateMove(int[][] currentState) {
        //Start by calling maximizePlay since we want to maximize CPU's score
        int[] bestMove = maximizePlay(currentState, 1, MainActivity.PLAYER_2,
                Integer.MIN_VALUE, Integer.MAX_VALUE);

        //maximizePlay returns a 1D int array as [column of best move, static eval]
        return bestMove[0];
    }

    /**
     * Searches through all possible plays in the 7 game board columns for the move
     * that will maximize the current {@code player}'s score.
     * Utilizes alpha-beta pruning to significantly reduce move generation and static
     * evaluation.
     * @param currentState the current game state
     * @param depth the current depth at which a move is being searched. Method terminates
     *              and returns the board's utility evaluation if {@code MAX_DEPTH} is reached.
     * @param player the current player for whom a move is being optimized
     * @param alpha the alpha value used for alpha-beta pruning (lower bound)
     * @param beta the beta value used for alpha beta pruning (upper bound)
     * @return an integer array constructed using {@code [col of best move, static eval]}
     */
    public int[] maximizePlay(int[][] currentState, int depth, int player, int alpha, int beta) {
        int[] ret = new int[]{-1, Integer.MIN_VALUE};

        //If max depth is reached, simply return static eval
        if(depth == MAX_DEPTH) {
            ret[1] = utilityStaticEval(currentState, player);
            return ret;
        }

        //The player who will play in the next round
        int otherPlayer = (player == MainActivity.PLAYER_1) ?
                MainActivity.PLAYER_2 : MainActivity.PLAYER_1;

        //Get all possible moves and start iterating to find best move
        for(Integer[] move : GameBoard.possibleMoves(currentState)) {
            //get a deep copy of the board to prevent modifications to original calling board
            int[][] newState = GameBoard.deepCopyState(currentState);
            int row = move[0];
            int col = move[1];
            newState[row][col] = player;

            //If the player wins, there is no point in checking further
            //Returns a massive static eval so that the calling minimizePlay node knows
            //not to pick this move
            if(GameBoard.checkWin(row, col, newState, player)) {
                ret[0] = col;
                ret[1]= 1000000;
                break;
            }

            //Call on next depth level
            int[] minimizedPlay = minimizePlay(newState, depth+1, otherPlayer, alpha, beta);

            //Update best move and alpha as necessary
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

    /**
     * Searches through all possible plays in the 7 game board columns for the move
     * that will minimize the current {@code player}'s score.
     * Utilizes alpha-beta pruning just like maximizePlay.
     * @param currentState the current game state
     * @param depth the current depth at which a move is being searched. Method terminates
     *              and returns the board's utility evaluation if {@code MAX_DEPTH} is reached.
     * @param player the current player for whom a move is being minimized
     * @param alpha the alpha value used for alpha-beta pruning (lower bound)
     * @param beta the beta value used for alpha beta pruning (upper bound)
     * @return an integer array constructed using {@code [col of best move, static eval]}
     */
    public int[] minimizePlay(int[][] currentState, int depth, int player, int alpha, int beta) {
        //All functionality is essentially the same as maximizePlay, except we're looking for
        //the lowest possible score now
        int[] ret = new int[]{-1, Integer.MAX_VALUE};

        if(depth == MAX_DEPTH) {
            ret[1] = utilityStaticEval(currentState, player);
            return ret;
        }

        int otherPlayer = (player == MainActivity.PLAYER_1) ?
                MainActivity.PLAYER_2 : MainActivity.PLAYER_1;

        for(Integer[] move : GameBoard.possibleMoves(currentState)) {
            int[][] newState = GameBoard.deepCopyState(currentState);
            int row = move[0];
            int col = move[1];
            newState[row][col] = player;

            //Return a large negative number so that calling maximizePlay node knows
            //not to go down this path.
            if(GameBoard.checkWin(row, col, newState, player)) {
                ret[0] = col;
                ret[1]= -1000000;
                break;
            }

            int[] maximizedPlay = maximizePlay(newState, depth+1, otherPlayer, alpha, beta);

            if(maximizedPlay[1] < ret[1] || ret[0] == -1) {
                ret[0] = move[1];
                ret[1] = maximizedPlay[1];
            }

            //Instead of alpha, update beta for pruning
            beta = Math.min(beta, ret[1]);
            if(beta <= alpha)
                break;
        }

        return ret;
    }

    /**
     * A fast utility evaluator to determine how useful the current board is for the given
     * player. This is determined by how many high utility spots the player has vs. how
     * many high utility spots the opponent has.
     * Conceptually borrowed from
     * https://softwareengineering.stackexchange.com/questions/263514/why-does-this-evaluation-function-work-in-a-connect-four-game-in-java.
     * @param currentState the current game state
     * @param player the player to evaluate for
     * @return the static evaluation
     */
    public int utilityStaticEval(int[][] currentState, int player) {
        int otherPlayer = (player == MainActivity.PLAYER_1) ? MainActivity.PLAYER_2 : MainActivity.PLAYER_1;
        int utility = 138;
        int sum = 0;

        //Loop through all utilities. If current player has a position, add the utility,
        //otherwise subtract the utility as a penalty
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

    /**
     * Another static evaluation method that we originally developed ourselves and thought
     * would be useful. However, some testing showed that this evaluation method is rather
     * useless since it does not give a wide enough variety of evaluations for MiniMax to
     * predict a useful move. This method works by rewarding the player for the more pieces
     * it has in a row.
     * @param currentState the current game state
     * @param player the current player
     * @return the calculated static evaluation
     */
    public double strongStaticEval(int[][] currentState, int player) {
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

        int multiplier = (player == MainActivity.PLAYER_1) ? 1 : 1; //-1 : 1

        //Check each row individually for # of consecutive pieces
        int deltaX = 1;
        int deltaY = 1;
        for (int row = 0; row < GameBoard.ROWS; row += deltaY, maxInRow = 0) {
            for (int col = 0; col < GameBoard.COLUMNS; col += deltaX) {

                //If we find a piece belonging to player, increment maxInRow
                if (currentState[row][col] == player) {
                    maxInRow++;
                }
                //If we don't find a piece, then calculate the associated value
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
        for (int col = 0; col < GameBoard.COLUMNS; col += deltaX, maxInRow = 0) {
            for (int row = 0; row < GameBoard.ROWS; row += deltaY) {

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

        //Checking diagonally (top-left to bottom-right direction scenarios)
        int[] startingRows = new int[]{2, 1, 0, 0, 0, 0};
        int[] startingCols = new int[]{0, 0, 0, 1, 2, 3};
        for (int i = 0; i < startingRows.length && i < startingCols.length; i++) {
            for (int row = startingRows[i], col = startingCols[i]; row < GameBoard.ROWS && col < GameBoard.COLUMNS;
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

        //Checking diagonally (top-right to bottom-left direction scenarios)
        deltaY = -1;
        startingRows = new int[]{3, 4, 5, 5, 5, 5};
        for (int i = 0; i < startingRows.length && i < startingCols.length; i++) {
            for (int row = startingRows[i], col = startingCols[i]; row >= 0 && col < GameBoard.COLUMNS;
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
