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

    public static boolean printbool = false;
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
    //@param playerNum, 01 if player 1, 10 if player 2
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
        short bitPlayer = (playerNum == MainActivity.PLAYER_1) ? (short)(MainActivity.PLAYER_1 << (short)((COLUMNS - col - 1)*2))
                : (short)(MainActivity.PLAYER_2 << (short)((COLUMNS - col - 1)*2));

        //Use OR operator to insert piece, then reset piece in array
        short newRow = (short)(data | bitPlayer);
        //Log.d("New rows", "" + newRow);
        gameState[row] = newRow;
    }


    public void setButtonImg(int row, int col, ImageButton button){
        buttons[row][col] = button;
    }

    public void changePieceColor(int row, int col, int res){
        buttons[row][col].setImageResource(res);
    }

    public short getElement(int row, int col) {
        return getElement(row, col, gameState);
    }

    public static void print(String msg){
        if(printbool){
            //Log.d("print", msg);
        }
    }

    public static short getElement(int row, int col, short[] gameState) {
        short data = gameState[row];
        //Log.d("Data", "" + data);
        short ret = (short)(data >> (short)((GameBoard.COLUMNS - col - 1)*2));
        ret = (short)(ret & 0b11);
        //Log.d("Value in getElement", "" + ret);

        //print("data: " + data);
        //print("ret: " + ret);

        return ret;
    }

    public short[] getState(){
        return this.gameState;
    }

    public boolean checkWin(int row, int col, short player) {
        //Log.d("Check Win", "Entered");
        short bitPlayer = player;

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
            gameState[i] = 0b00;
            for(int j = 0; j < COLUMNS; j++) {
                buttons[i][j].setImageResource(R.mipmap.piece_empty);
            }
        }
    }

    public static short staticEval(short[] gameStateCopy, short player) {
        short[] gameState = Arrays.copyOf(gameStateCopy, gameStateCopy.length);
        short[] players = new short[7];

        short ret = 0;

        int r = 5;
        int c = 0;

        //check rows for horizontal maxNums at columns 4 (index 3)
        //Use only dx
        for(; r >= 0; r--) {
            for(int index = 0; index < players.length; index++) {
                short element = GameBoard.getElement(r, c, gameState);
                players[index] = element;
                c++;
            }

            ret += maxNumInRowForPlayer(players, player);
            ret -= maxNumInRowForOpposing(players, player);
            c = 0;
        }
        players = new short[7];


        //check columns for vertical maxNums at row 4 (index 3)
        //use only dy
        c = 0; r = 0;
        for(; c < COLUMNS; c++) {
            for(int index = 0; index < players.length && index < gameState.length; index++) {
                short element = GameBoard.getElement(r, c, gameState);
                players[index] = element;
                r++;
            }

            ret += maxNumInRowForPlayer(players, player);
            ret -= maxNumInRowForOpposing(players, player);

            r = 0;
        }
        players = new short[7];

        //check NE diagonals for maxNums from (3, 0) to (3, 5) –– can exclude last element in row
        //use only dy - and dx +
        int dx = 1;
        int dy = 1;
        r = 2; c = 0;
        int[] rs = {2, 1, 0, 0, 0, 0};
        int[] cs = {0, 0, 0, 1, 2, 3};

        for(int i = 0; i < COLUMNS - 1; i++) {
            for(int index = 0; r < gameState.length && index < players.length; index++) {
                short element = GameBoard.getElement(r, c, gameState);
                players[index] = element;
                r += dy;
                c += dx;
            }

            ret += maxNumInRowForPlayer(players, player);
            ret -= maxNumInRowForOpposing(players, player);

            r = rs[i];
            c = cs[i];
        }
        players = new short[7];


        //check SE diagonals for maxNums from (3, 1) to (3, 6) –– can exclude first element in row
        //use only dy + and dx +
        dx = -1;
        dy = 1;
        r = 2; c = 6;
        rs = new int[]{2, 1, 0, 0, 0, 0};
        cs = new int[]{0, 0, 0, 1, 2, 3};

        for(int i = 0; i < COLUMNS - 1; i++) {
            for (int index = 0; r < gameState.length && index < players.length; index++) {
                short element = GameBoard.getElement(r, c, gameState);
                players[index] = element;
                r += dy;
                c += dx;
            }

            ret += maxNumInRowForPlayer(players, player);
            ret -= maxNumInRowForOpposing(players, player);

            r = rs[i];
            c = cs[i];
        }

        return ret;
    }

    private static int maxNumInRowForPlayer(short[] players, short id) {
        int max = 0;
        int count = 0;
        int ret = 0;

        for (int i = 0; i < players.length; i++) {
            if (players[i] == id) {
                count++;
            } else if (count > max) {
                max = count;
                count = 0;
            }
        }


        if(max >= 4) {
            ret = 1000;
        } else if(max == 3) {
            ret = 100;
        }

        return ret;
    }

    private static int maxNumInRowForOpposing(short[] players, short id) {
        int max = 0;
        int count = 0;
        int ret = 0;
        id = (id == PLAYER1_BIT) ? PLAYER2_BIT : PLAYER1_BIT;

        for (int i = 0; i < players.length; i++) {
            if (players[i] == id) {
                count++;
            } else if (count > max) {
                max = count;
                count = 0;
            }
        }


        if(max >= 4) {
            ret = 2000;
        } else if(max == 3) {
            ret = 1000;
        }

        return ret;
    }

    public static short staticEval(short[] gameStateCopy, int lastMoveRow, int lastMoveCol,
                                   short runningEval, short player) {

        short[] gameState = Arrays.copyOf(gameStateCopy, gameStateCopy.length);
        short ret = runningEval;

        //We will try to minimize if the player is PLAYER 1, otherwise maximize
        //if the player is PLAYER 2 (PLAYER 2 is CPU in this case)
        short bitPlayer = player;
        short opposingPlayer = (player == PLAYER1_BIT) ? PLAYER2_BIT : PLAYER1_BIT;

        int index = 3, r = lastMoveRow, c = lastMoveCol;
        short[] players = new short[7];
        short[] empties = new short[7];
        short[] opposingEmpties = new short[7];
        for (int i = 0; i < dx.length && i < dy.length; i++) {

            int x = dx[i];
            int y = dy[i];

            for (int j = 0; j < 4 && r < ROWS && r >= 0 && c < COLUMNS && c >= 0; j++) {

                short data = gameState[r];
                short shift = (short) ((GameBoard.COLUMNS - c - 1) * 2);
                //Modify data to reflect the piece at the position
                data = (short) ((data >>> shift) & 0b11);

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
                data = (short) ((data >>> shift) & 0b11);

                players[index] = data;

                r -= y;
                c -= x;
                index--;
            }

            //EXTRACT INTO SEPARATE METHOD FOR READABILITY
            //Now traverse the "players" array to find the possibilities
            index = 0;
            r = lastMoveRow - (y * 3);
            c = lastMoveCol - (x * 3);

            for (int j = 0; j < players.length; j++) {
                if (players[j] == 0 && CPU_Player.rowIfPlaced(c, gameState) == r) {
                    empties[j] = bitPlayer;
                    opposingEmpties[j] = opposingPlayer;
                }

                index++;
                r += y;
                c += x;
            }

            for (int j = 0; j < empties.length; j++) {
                if (empties[j] == bitPlayer) {
                    short temp = players[j];
                    players[j] = bitPlayer;

                    int maxNumInRow = maxNumInRow(players, bitPlayer);
                    int add = 0;
                    if(maxNumInRow >= 4)
                        add = 1000;
                    else if(maxNumInRow == 3)
                        add = 100;

                    if(add != 0)
                        ret = (bitPlayer == PLAYER2_BIT) ? (short)(ret+add) : (short)(ret-add);

                    players[j] = temp;
                }
            }

            //Also calculate how changes in game state affect opponent's situation
            for (int j = 0; j < opposingEmpties.length; j++) {
                if (opposingEmpties[j] == opposingPlayer) {
                    short temp = players[j];
                    players[j] = opposingPlayer;

                    int maxNumInRow = maxNumInRow(players, opposingPlayer);
                    int add = 0;
                    if(maxNumInRow >= 4)
                        add = 2000;
                    else if(maxNumInRow == 3)
                        add = 1000;

                    if(add != 0)
                        ret = (bitPlayer == PLAYER1_BIT) ? (short)(ret-add) : (short)(ret+add);

                    players[j] = temp;
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
    

    private static int maxNumInRow(short[] players, short id) {
        int max = 0;
        int count = 0;
        for(int i = 0; i < players.length; i++) {
            if(players[i] == id) {
                count++;
            } else if(count > max) {
                max = count;
                count = 0;
            }
        }
        return max;
    }
}
