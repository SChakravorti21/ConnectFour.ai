package comschakravorti21.github.connectfourai.try2;

/**
 * Created by Development on 11/21/17.
 */
import android.media.Image;
import android.util.Log;
import android.widget.ImageButton;

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

    public static int staticEval(int[][] currentState, int player) {
        //If we are evaluating for human player (PLAYER_1), then we'll
        //multiply the eval by -1 (minimizing human player score), otherwise
        //leave score unaltered
        int multiplier = (player == MainActivity2.PLAYER_1) ? -1 : 1;
        int ret = 0;
        int maxInRow = 0;

        /*
            Start with primitive evaluation –– # of 3 in a row * 20 * mult
            If there are 4 in a row, return 1000 (game over)
         */

        //Check each row individually for # of consecutive pieces
        int deltaX = 1;
        int deltaY = 1;
        for(int row = 0; row < ROWS; row += deltaY) {
            for(int col = 0; col < COLUMNS; col += deltaX) {
                //If there is a win, there is no point in checking anymore
                if (checkWin(row, col, currentState, player)) {
                    return (multiplier * 100000);
                }

                if (currentState[row][col] == player) {
                    maxInRow++;
                } else {
                    ret += Math.pow(10, maxInRow);
                    maxInRow = 0;
                }
            }
        }
        maxInRow = 0; //Reset max in row for next iteration set


        //Check each column individually for # of consecutive pieces
        deltaX = 1;
        deltaY = 1;
        for(int col = 0; col < COLUMNS; col += deltaX) {
            for(int row = 0; row < ROWS; row += deltaY) {
                //If there is a win, there is no point in checking anymore
                if (checkWin(row, col, currentState, player)) {
                    return (multiplier * 100000);
                }

                if (currentState[row][col] == player) {
                    maxInRow++;
                } else {
                    ret += Math.pow(10, maxInRow);
                    maxInRow = 0;
                }
            }
        }
        maxInRow = 0;

        return ret*multiplier;

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