package comschakravorti21.github.connectfourai;

import android.media.Image;
import android.util.Log;
import android.widget.ImageButton;

/**
 * Created by Athu on 11/3/2017.
 */

public class GameBoard {

    public static final int ROWS = 6;
    public static final int COLUMNS = 7;
    public static final int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
    public static final int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};

    private int[][] gameState;
    private ImageButton[][] buttons;
    //0 = empty, 1 = blue, 2 = red


    public GameBoard() {
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

        if(playerNum == MainActivity.PLAYER_1) {
            this.changePieceColor(row, col, R.mipmap.piece_yellow);
        } else {
            this.changePieceColor(row, col, R.mipmap.piece_red);
        }

        return row;
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

    public boolean checkWin(int row, int col, int player) {
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
    

}
