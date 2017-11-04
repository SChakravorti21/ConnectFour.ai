package comschakravorti21.github.connectfourai;

import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import static android.R.attr.tag;

public class MainActivity extends AppCompatActivity {

    public static final int PLAYER_1 = -1;
    public static final int PLAYER_2 = 1;
    public static final int CPU = 3;

    private GridLayout board;
    private ImageButton[][] buttons;
    private int[][] pieces;
    private int player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        board = (GridLayout)findViewById(R.id.board);
        buttons = new ImageButton[6][7];
        pieces = new int[6][7];
        player = PLAYER_1;

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton button = (ImageButton)view;
                int col = Character.digit( ((String)button.getTag() ).charAt(1), 10) - 1;

                //Start from bottom when searching where to insert piece
                for(int i = buttons.length - 1; i >= 0; i--) {
                    //Place the piece if its empty
                    if(pieces[i][col] == 0) {
                        pieces[i][col] = 1;

                        if(player == PLAYER_1) {
                            buttons[i][col].setImageResource(R.mipmap.piece_yellow);
                        } else {
                            buttons[i][col].setImageResource(R.mipmap.piece_red);
                        }

                        player = -player;
                        break;
                    }
                }
            }
        };

        for(int i = 0; i < board.getChildCount(); i++) {
            ImageButton button = (ImageButton)board.getChildAt(i);
            button.setImageResource(R.mipmap.piece_empty);
            button.setOnClickListener(buttonClickListener);
            String tag = (String)button.getTag();
            int row = Character.digit(tag.charAt(0), 10);
            int col = Character.digit(tag.charAt(1), 10);
            buttons[row-1][col-1] = button;
        }

    }
}
