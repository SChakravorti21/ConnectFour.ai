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
    public static final short PLAYER1_BIT = 0b01;
    public static final short PLAYER2_BIT = 0b10;

    public static final int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
    public static final int[] dy = {-1, -1, 0, 1, 1, 1, 0, -1};

    private short[] gameState;
    private ImageButton[][] buttons;
    //0 = empty, 1 = blue, 2 = red


    public GameBoard() {
        gameState = new short[ROWS];
        buttons = new ImageButton[ROWS][COLUMNS];
    }

    //returns row that it was placed in
    //@param playerNum, 1 if player 1,     -1 if player 2
    public int placePiece(int row, int col, byte playerNum) {
        //gameState[row][col] = playerNum;
        short data = gameState[row];
        //Create the player position and shift it by the necessary number of columns
        //00 is empty, 01 is player 1, 10 is player 2
        short bitPlayer = (playerNum == PLAYER1_BIT) ? (short)(0b01 << (short)((COLUMNS - col - 1)*2)): (short)(0b10 << (short)((COLUMNS - col - 1)*2));

        //Use OR operator to insert piece, then reset piece in array
        short newRow = (short)(data | bitPlayer);
        gameState[row] = newRow;

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

    public int getElement(int row, int col) {
        short data = gameState[row];
        Log.d("Data", "" + data);
        short ret = (short)(data >> (short)((GameBoard.COLUMNS - col - 1)*2));
        ret = (short)(ret & 0b11);
        Log.d("Value", "" + ret);
        if(ret == PLAYER1_BIT)
            return -1;
        else if (ret == PLAYER2_BIT)
            return 1;
        else
            return 0;
    }

    public short[] getState(){
        return this.gameState;
    }

    public boolean checkWin(int row, int col, int player) {
        Log.d("Check Win", "Entered");
        short bitPlayer = (player == -1) ? PLAYER1_BIT : PLAYER2_BIT;

        for(int i = 0, r = row, c = col ; i < dx.length/2 && i < dy.length/2;
                i++, r = row, c = col) {

            int x = dx[i];
            int y = dy[i];

            int count = 0;
            while(r < ROWS && r >= 0 && c < COLUMNS && c >= 0 && count < 4) {

                short data = gameState[r];
                short shift = (short)((GameBoard.COLUMNS - col - 1)*2);
                data = (short)((data >>> shift) & 0b11);

                if(data == bitPlayer) {
                    count++;
                    //Log.d("COUNT, LOOP 1", "" + count);
                    r += y;
                    c += x;
                } else
                    break;
            }

            r = row - y;
            c = col - x;
            while(r < ROWS && r >= 0 && c < COLUMNS && c >= 0
                    && count < 4) {

                short data = gameState[r];
                short shift = (short)((GameBoard.COLUMNS - col - 1)*2);
                data = (short)((data >>> shift) & 0b11);

                if(data == bitPlayer) {
                    count++;
                    //Log.d("COUNT, LOOP 1", "" + count);
                    r += y;
                    c += x;
                } else
                    break;
            }

            if(count == 4) {
                Log.d("Check Win", "Exiting");
                return true;
            }
        }

        Log.d("Check Win", "Exiting");
        return false;
    }

    public void resetBoard() {
        for(int i = 0; i < ROWS; i++) {
            gameState[i] = 0b0;
            for(int j = 0; j < COLUMNS; j++) {
                buttons[i][j].setImageResource(R.mipmap.piece_empty);
            }
        }
    }
    

}
