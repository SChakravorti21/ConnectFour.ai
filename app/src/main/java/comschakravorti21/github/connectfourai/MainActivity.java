package comschakravorti21.github.connectfourai;

import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final short PLAYER_1 = -1;
    public static final short PLAYER_2 = 1;
    //public static final byte CPU = 3;

    private Toolbar toolbar;
    private GridLayout gridBoard;
    //private short[] pieces;
    //private byte[][] pieces;
    private short player;
    public GameBoard gameboard;
    private int scoreP1, scoreP2;
    public CPU_Player cpu;
    public MediaPlayer mPlayer;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_reset:
                gameboard.resetBoard();
                scoreP1 = 0;
                TextView scoreView1 = (TextView)findViewById(R.id.score_P1);
                scoreView1.setText("0");
                scoreP2 = 0;
                TextView scoreView2 = (TextView)findViewById(R.id.score_P2);
                scoreView2.setText("0");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridBoard = (GridLayout)findViewById(R.id.board);
        //pieces = new short[GameBoard.ROWS];
        player = PLAYER_1;
        scoreP1 = scoreP2 = 0;

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gameboard = new GameBoard();

        cpu = new CPU_Player(gameboard.getState());
        cpu.initTree();

        //creates a media player
        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.clack);

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton button = (ImageButton)view;

                int col = Character.digit( ((String)button.getTag() ).charAt(1), 10) - 1;

                //plays mp3 file when piece is placed
                mPlayer.start();


                //Start from bottom when searching where to insert piece
                for(int i = GameBoard.ROWS - 1; i >= 0; i--) {
                    //Place the piece if its empty
                    //Log.d("Element", "" + gameboard.getElement(i, col));

                    if(gameboard.getElement(i, col) == 0) {

                        gameboard.placePiece(i, col, player);

                        if(gameboard.checkWin(i, col, player)) {
                            //Log.d("Check Win", "TRUE");
                            if(player == PLAYER_1) {
                                scoreP1++;
                                TextView scoreView = (TextView)findViewById(R.id.score_P1);
                                scoreView.setText("" + scoreP1);
                            }
//                            else {
//                                scoreP2++;
//                                TextView scoreView = (TextView)findViewById(R.id.score_P2);
//                                scoreView.setText("" + scoreP2);
//                            }

                            gameboard.resetBoard();

                            //Need to reset tree
                        } else{
                            //Log.d("Check Win", "FALSE");

                            //Otherwise move down tree and extend tree by 1 level
                        }

                        player = (short)~player;
                        break;
                    }
                }

                //After the user plays, we want the CPU to play
                int c = cpu.computeBestMove(); //gets the columns corresponding to the best move
                int r = CPU_Player.rowIfPlaced(c, gameboard.getState());
                gameboard.placePiece(r, c, player); //place the piece, change the color

                if(gameboard.checkWin(r, col, player)) {
                    //Log.d("Check Win", "TRUE");
                    scoreP2++;
                    TextView scoreView = (TextView)findViewById(R.id.score_P2);
                    scoreView.setText("" + scoreP2);

                    gameboard.resetBoard();

                    //Need to reset game tree
                } else{
                    //Log.d("Check Win", "FALSE");

                    //Need to move down game tree by 1 and extend tree by 1
                }


                player = (short)~player;
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
