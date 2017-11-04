package comschakravorti21.github.connectfourai;

import android.media.Image;
import android.widget.ImageButton;

/**
 * Created by Athu on 11/3/2017.
 */

public class GameBoard {

    public static final int ROWS = 6;
    public static final int COLUMNS = 7;
    private int[][] gameState;
    private ImageButton[][] buttons;
    //0 = empty, 1 = blue, 2 = red


    public GameBoard() {
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

    

}
