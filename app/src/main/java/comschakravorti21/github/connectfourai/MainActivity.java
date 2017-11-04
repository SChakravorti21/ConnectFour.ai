package comschakravorti21.github.connectfourai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    public static final int PLAYER_1 = -1;
    public static final int PLAYER_2 = 1;
    public static final int CPU = 3;

    private GridLayout gridBoard;
    private int[][] pieces;
    private int player;
    public GameBoard gameboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridBoard = (GridLayout)findViewById(R.id.board);
        pieces = new int[GameBoard.ROWS][GameBoard.COLUMNS];
        player = PLAYER_1;

        gameboard = new GameBoard();

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton button = (ImageButton)view;

                int col = Character.digit( ((String)button.getTag() ).charAt(1), 10) - 1;

                //Start from bottom when searching where to insert piece
                for(int i = pieces.length - 1; i >= 0; i--) {
                    //Place the piece if its empty
                    if(gameboard.getState(i, col) == 0) {

                        gameboard.placePiece(i, col, player);

                        if(gameboard.checkWin(i, col, player)) {
                            Log.d("Check Win", "TRUE");
                        } else{
                            Log.d("Check Win", "FALSE");
                        }

                        player = -player;
                        break;
                    }
                }
            }
        };


        for(int i = 0; i < gridBoard.getChildCount(); i++) {
            ImageButton button = (ImageButton) gridBoard.getChildAt(i);
            button.setImageResource(R.mipmap.piece_empty);
            button.setOnClickListener(buttonClickListener);
            String tag = (String)button.getTag();
            int row = Character.digit(tag.charAt(0), 10);
            int col = Character.digit(tag.charAt(1), 10);
            gameboard.setButtonImg(row-1, col-1, button);
        }

    }
}
