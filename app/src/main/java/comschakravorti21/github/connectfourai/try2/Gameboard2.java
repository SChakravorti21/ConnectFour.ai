package comschakravorti21.github.connectfourai.try2;

/**
 * Created by Development on 11/21/17.
 */
import android.media.Image;
import android.util.Log;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;

import comschakravorti21.github.connectfourai.R;

/**
 * Created by Athu on 11/3/2017.
 */

public class Gameboard2 {

    public static final int ROWS = 6;
    public static final int COLUMNS = 7;
    public static final int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
    public static final int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};

    private int[][] gameState;
    private ImageButton[][] buttons;

    public Gameboard2() {
        gameState = new int[ROWS][COLUMNS];
        buttons = new ImageButton[ROWS][COLUMNS];

        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                gameState[i][j] = 0;
            }
        }
    }

    //returns row that it was placed in
    //@param playerNum, 1 if player 1,     -1 if player 2
    public int placePiece(int row, int col, int playerNum) {
        gameState[row][col] = playerNum;

        if(playerNum == MainActivity2.PLAYER_1) {
            this.changePieceColor(row, col, R.mipmap.piece_yellow);
        } else {
            this.changePieceColor(row, col, R.mipmap.piece_red);
        }

        return row;
    }

    public static int rowIfPlaced(int col, int[][] gameState) {
        for(int i = gameState.length - 1; i >= 0; i--) {
            if(gameState[i][col] == 0) {
                return i;
            }
        }

        return -1;
    }

    public static ArrayList<Integer[]> possibleMoves(int[][] currentState) {
        ArrayList<Integer[]> possible = new ArrayList<>(COLUMNS);
        for(int i = 0; i < COLUMNS; i++) {
            int row = rowIfPlaced(i, currentState);
            if(row != -1)
                possible.add(new Integer[]{row, i});
        }

        return possible;
    }

    public static int[][] deepCopyState(int[][] currentState) {
        if(currentState == null)
            return null;

        int[][] ret = new int[currentState.length][];
        for(int i = 0; i < currentState.length; i++) {
            ret[i] = Arrays.copyOf(currentState[i], currentState[i].length);
        }
        return ret;
    }

    public void resetBoard() {
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                gameState[i][j] = 0;
                buttons[i][j].setImageResource(R.mipmap.piece_empty);
            }
        }
    }

    public static boolean checkWin(int row, int col, int[][] gameState, int player) {
        for(int i = 0, r = row, c = col ; i < dx.length/2 && i < dy.length/2;
            i++, r = row, c = col) {

            int x = dx[i];
            int y = dy[i];

            int count = 0;
            while(r < ROWS && r >= 0 && c < COLUMNS && c >= 0
                    && count < 4 && gameState[r][c] == player) {
                count++;
                //Log.d("COUNT, LOOP 1", "" + count);
                r += y;
                c += x;
            }

            r = row - y;
            c = col - x;
            while(r < ROWS && r >= 0 && c < COLUMNS && c >= 0
                    && count < 4 && gameState[r][c] == player) {

                count++;
                //Log.d("COUNT, LOOP 2", "" + count);
                r -= y;
                c -= x;
            }

            if(count == 4)
                return true;
        }

        return false;
    }

    public void setButtonImg(int row, int col, ImageButton button){
        buttons[row][col] = button;
    }

    public void changePieceColor(int row, int col, int res){
        buttons[row][col].setImageResource(res);
    }

    public int getState(int row, int col) {
        return gameState[row][col];
    }

    public int[][] getBoard() {
        return this.gameState;
    }


}