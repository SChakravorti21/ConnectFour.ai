package comschakravorti21.github.connectfourai;

import android.media.Image;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;

import java.util.Arrays;

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
    public int placePiece(int row, int col, short playerNum) {
        //gameState[row][col] = playerNum;
        placePiece(row, col, gameState, playerNum);

        if(playerNum == MainActivity.PLAYER_1) {
            this.changePieceColor(row, col, R.mipmap.piece_yellow);
        } else {
            this.changePieceColor(row, col, R.mipmap.piece_red);
        }

        return row;
    }

    public static void placePiece(int row, int col, short[] gameState, short playerNum) {
        short data = gameState[row];
        //Create the player position and shift it by the necessary number of columns
        //00 is empty, 01 is player 1, 10 is player 2
        short bitPlayer = (playerNum == PLAYER1_BIT) ? (short)(PLAYER1_BIT << (short)((COLUMNS - col - 1)*2))
                : (short)(PLAYER2_BIT << (short)((COLUMNS - col - 1)*2));

        //Use OR operator to insert piece, then reset piece in array
        short newRow = (short)(data | bitPlayer);
        gameState[row] = newRow;
    }


    public void setButtonImg(int row, int col, ImageButton button){
        buttons[row][col] = button;
    }

    public void changePieceColor(int row, int col, int res){
        buttons[row][col].setImageResource(res);
    }

    public int getElement(int row, int col) {
        return getElement(row, col, gameState);
    }

    public static int getElement(int row, int col, short[] gameState) {
        short data = gameState[row];
        //Log.d("Data", "" + data);
        short ret = (short)(data >> (short)((GameBoard.COLUMNS - col - 1)*2));
        ret = (short)(ret & 0b11);
        //Log.d("Value", "" + ret);
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
        //Log.d("Check Win", "Entered");
        short bitPlayer = (player == -1) ? PLAYER1_BIT : PLAYER2_BIT;

        for(int i = 0, r = row, c = col ; i < dx.length/2 && i < dy.length/2;
                i++, r = row, c = col) {

            int x = dx[i];
            int y = dy[i];

            //Log.d("dx ", "" + x);
            //Log.d("dy ", "" + y);

            int count = 0;
            while(r < ROWS && r >= 0 && c < COLUMNS && c >= 0 && count < 4) {

                short data = gameState[r];
                //Log.d("Data", "" + data);
                short shift = (short)((GameBoard.COLUMNS - c - 1)*2);
                data = (short)(data >> shift & 0b11);
                //Log.d("Modified Data", "row: " + r + ", col: " + c + ", data: " + data);

                if(data == bitPlayer) {
                    //Log.d("Check Win", "Found match");
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
                //Log.d("Data", "" + data);
                short shift = (short)((GameBoard.COLUMNS - c - 1)*2);
                data = (short)(data >> shift & 0b11);
                //Log.d("Modified Data", "row: " + r + ", col: " + c + ", data: " + data);

                if(data == bitPlayer) {
                    //Log.d("Check Win", "Found match");
                    count++;
                    //Log.d("COUNT, LOOP 1", "" + count);
                    r -= y;
                    c -= x;
                } else
                    break;
            }

            if(count == 4) {
                //Log.d("Check Win", "Exiting");
                return true;
            }
        }

        //Log.d("Check Win", "Exiting");
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

    public static short staticEval(short[] gameState, int lastMoveRow, int lastMoveCol,
                                   short runningEval, short player) {
        short ret = runningEval;

        //We will try to minimize if the player is PLAYER 1, otherwise maximize
        //if the player is PLAYER 2 (PLAYER 2 is CPU in this case)
        short bitPlayer = (player == -1) ? PLAYER1_BIT : PLAYER2_BIT;

        int index = 3, r = lastMoveRow, c = lastMoveCol;
        short[] players = new short[7];
        short[] empties = new short[7];
        for (int i = 0; i < dx.length / 2 && i < dy.length / 2; i++) {

            int x = dx[i];
            int y = dy[i];

            for (int j = 0; j < 4 && r < ROWS && r >= 0 && c < COLUMNS && c >= 0; j++) {

                short data = gameState[r];
                short shift = (short) ((GameBoard.COLUMNS - c - 1) * 2);
                //Modify data to reflect the piece at the position
                data = (short) (data >> shift & 0b11);

                players[index] = data;


                r += y;
                c += x;
                index++;
            }

            r = lastMoveRow - y;
            c = lastMoveCol - x;
            index = 2;
            for (int j = 0; j < 3 && r < ROWS && r >= 0 && c < COLUMNS && c >= 0; j++) {

                short data = gameState[r];
                short shift = (short) ((GameBoard.COLUMNS - c - 1) * 2);
                //Modify data to reflect the piece at the position
                data = (short) (data >> shift & 0b11);

                players[index] = data;

                r -= y;
                c -= x;
                index--;
            }

            //EXTRACT INTO SEPARATE METHOD FOR READABILITY
            //Now traverse the "players" array to find the possibilities
            index = 0;
            r = lastMoveRow - y * 3;
            c = lastMoveCol - x * 3;

            for (int j = 0; j < players.length; j++) {
                if (players[j] == 0 && CPU_Player.rowIfPlaced(c, gameState) == r) {
                    empties[j] = bitPlayer;
                }

                index++;
                r += y;
                c += x;
            }

            int numInRow = 0;
            for (int j = 0; j < empties.length; j++) {
                if (empties[j] == bitPlayer) {
                    players[j] = bitPlayer;

                    for (int k = 0; k < players.length; k++) {
                        if (players[k] == bitPlayer && numInRow < 4) {
                            numInRow++;
                        } else {
                            int toAdd = (numInRow >= 3) ? (int) Math.pow(numInRow, 3) : 0;
                            ret = (bitPlayer == PLAYER1_BIT) ? (short)(ret-toAdd) : (short)(ret+toAdd);
                            numInRow = 0;
                        }
                    }

                    players[j] = 0;
                }
            }

            //Reset index values
            Arrays.fill(players, (short) 0);
            Arrays.fill(empties, (short) 0);
            index = 3;
            r = lastMoveRow;
            c = lastMoveCol;
        }

        return ret;
    }
    

}
