package comschakravorti21.github.connectfourai.try2;

/**
 * Created by Shoumyo Chakravorti on 11/21/17.
 */
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;

import comschakravorti21.github.connectfourai.R;

public class Gameboard2 {

    /**
     * The standard number of rows in a Connect Four board (6)
     */
    public static final int ROWS = 6;
    /**
     * The standard number of column in a Connect Four board (7)
     */
    public static final int COLUMNS = 7;

    private int[][] gameState;
    private ImageButton[][] buttons;

    /**
     * Creates a new Gameboard with an empty state and sets all
     * pieces to white (empty).
     */
    public Gameboard2() {
        gameState = new int[ROWS][COLUMNS];
        buttons = new ImageButton[ROWS][COLUMNS];
    }

    /**
     * Places a piece into the given position denoted by {@code (row, col)}.
     * Places a piece corresponding to the respective {@code player}.
     * @param row row to insert in
     * @param col column to insert in
     * @param playerNum the player whose piece is being inserted
     * @return
     */
    public int placePiece(int row, int col, int playerNum) {
        gameState[row][col] = playerNum;

        if(playerNum == MainActivity2.PLAYER_1) {
            this.changePieceColor(row, col, R.mipmap.piece_yellow);
        } else {
            this.changePieceColor(row, col, R.mipmap.piece_red);
        }

        return row;
    }

    /**
     * Checks whether the board has filled up, resulting in a tie
     * @return whether the board is full
     */
    public boolean isFull() {
        //If there are no possible moves, the board is full
        return (possibleMoves(this.gameState).isEmpty());
    }

    /**
     * Returns the row that a piece would get placed in given a
     * specific {@code column}.
     * Returns -1 if the column is full.
     * @param col the column to insert into
     * @param gameState the current game state
     * @return the lowest row in which a piece can be placed
     */
    public static int rowIfPlaced(int col, int[][] gameState) {
        //Start looking from the bottom since connect four pieces
        //fall as far down as possible
        for(int i = gameState.length - 1; i >= 0; i--) {
            if(gameState[i][col] == 0) {
                return i;
            }
        }

        //If there was no valid move, return -1 to denote this
        return -1;
    }

    /**
     * Returns an ArrayList consisting of the possible moves that can
     * be made on a board. Each list item is an integer array of the form
     * {@code [row, column]}.
     * @param currentState the current game state
     * @return all possible moves
     */
    public static ArrayList<Integer[]> possibleMoves(int[][] currentState) {
        ArrayList<Integer[]> possible = new ArrayList<>(COLUMNS);
        for(int i = 0; i < COLUMNS; i++) {
            //If this column is not empty, we can add the (row, column) pair to the
            //list
            int row = rowIfPlaced(i, currentState);
            if(row != -1)
                possible.add(new Integer[]{row, i});
        }

        return possible;
    }

    /**
     * Returns a deep copy of the given game state. Used in {@code CPUPlayer}
     * to prevent the existing game state from being modified between moves generated.
     * @param currentState the current game state
     * @return a deep copy of the game state
     */
    public static int[][] deepCopyState(int[][] currentState) {
        if(currentState == null)
            return null;

        //Just copy every entry into a new 2D array and return it.
        int[][] ret = new int[currentState.length][currentState[0].length];
        for(int i = 0; i < currentState.length; i++) {
            ret[i] = Arrays.copyOf(currentState[i], currentState[i].length);
        }
        return ret;
    }

    /**
     * Resets the current game board. Called if a player wins or the board fills up.
     */
    public void resetBoard() {
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                //Set all entries to 0 (empty)
                gameState[i][j] = 0;
                //Set all pieces to white for visual confirmation of clearing
                buttons[i][j].setImageResource(R.mipmap.piece_empty);
            }
        }
    }

    /**
     * Given a particular position at {@code (row, column)}, determines if a
     * piece played at the position resulted in a win for the player.
     * @param row the row of the piece
     * @param col the column of the piece
     * @param gameState the game state in which to look for a win
     * @param player the player to check a win for
     * @return
     */
    public static boolean checkWin(int row, int col, int[][] gameState, int player) {
        //dx and dy are used to check "lines" around the row and column
        final int[] dx = {0, 1, 1, 1};
        final int[] dy = {-1, -1, 0, 1};

        //At each iteration, reset the row and column and look through a new
        //set of delta values
        for(int i = 0, r = row, c = col ; i < dx.length && i < dy.length;
            i++, r = row, c = col) {

            //The delta values to use during this iteration
            int x = dx[i];
            int y = dy[i];

            //Look in the "positive" direction of these delta values
            int count = 0;
            while(r < ROWS && r >= 0 && c < COLUMNS && c >= 0
                    && count < 4 && gameState[r][c] == player) {
                count++;
                r += y;
                c += x;
            }

            //Now look in the "opposite" direction using the same delta values.
            //Do not look at the same starting position
            r = row - y;
            c = col - x;
            while(r < ROWS && r >= 0 && c < COLUMNS && c >= 0
                    && count < 4 && gameState[r][c] == player) {

                count++;
                r -= y;
                c -= x;
            }

            if(count == 4)
                return true;
        }

        return false;
    }

    /**
     * Sets the button at {@code (row, col)} in the instance's collection of buttons
     * to the one given.
     * @param row the row of the new button
     * @param col the column of the new button
     * @param button the ImageButton to save
     */
    public void setButton(int row, int col, ImageButton button){
        buttons[row][col] = button;
    }

    /**
     * Changes the image of the button at the respective position to the
     * one indicated by the resource id.
     * @param row the row of the button image to change
     * @param col the column of the button image to change
     * @param res the resource id of the image
     */
    public void changePieceColor(int row, int col, int res){
        buttons[row][col].setImageResource(res);
    }

    /**
     * Returns the current state of the game
     * @return current game state
     */
    public int[][] getBoard() {
        return this.gameState;
    }


}