package comschakravorti21.github.connectfourai;

/**
 * Created by Athu on 11/3/2017.
 */

public class GameBoard {

    public static final int ROWS = 6;
    public static final int COLUMNS = 7;
    private int[][] gameState;
    //0 = empty, 1 = blue, 2 = red


    public GameBoard() {
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                gameState[i][j] = 0;
            }
        }
    }

    

}
