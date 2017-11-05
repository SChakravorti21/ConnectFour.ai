package comschakravorti21.github.connectfourai;

import android.media.MediaPlayer;
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

    public static final short PLAYER_1 = 0b01;
    public static final short PLAYER_2 = 0b10;
    //public static final byte CPU = 3;

    private Toolbar toolbar;
    private GridLayout gridBoard;
    //private short[] pieces;
    //private byte[][] pieces;
    private short player;
    public GameBoard gameBoard;
    private int scoreP1, scoreP2;
    public CPU_Player CPU;
    public MediaPlayer mPlayer, songPlayer;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_reset:
                gameBoard.resetBoard();
                CPU.resetTree();

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
    protected void onPause() {
        super.onPause();
        songPlayer.stop();
        songPlayer.release();

    }

    public void onResume(){
        super.onResume();
        songPlayer.start();
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

        gameBoard = new GameBoard();

        CPU = new CPU_Player(gameBoard.getState());
        CPU.initTree();

        songPlayer = MediaPlayer.create(getApplicationContext(), R.raw.connect4);

        songPlayer.start();

        //creates a media player
        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.clack);

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton button = (ImageButton)view;

                int col = Character.digit( ((String)button.getTag() ).charAt(1), 10) - 1;

                //plays mp3 file when piece is placed
                mPlayer.start();


                //Start from bottom (now we're here) when searching where to insert piece
                for(int i = GameBoard.ROWS - 1; i >= 0; i--) {
                    //Place the piece if its empty
                    //Log.d("Element", "" + gameBoard.getElement(i, col));

                    if(gameBoard.getElement(i, col) == 0) {

                        gameBoard.placePiece(i, col, player);

                        if(gameBoard.checkWin(i, col, player)) {
                            //Log.d("Check Win", "TRUE");
                            if(player == PLAYER_1) {
                                scoreP1++;
                                TextView scoreView = (TextView)findViewById(R.id.score_P1);
                                scoreView.setText("" + scoreP1);
                            }

                            gameBoard.resetBoard();

                            CPU.resetTree();
                        } else{
                            //Log.d("Check Win", "FALSE");

                            //Otherwise move down tree and extend tree by 1 level
                            CPU.shiftRoot(i, col, player);
                        }

                        player = PLAYER_2;
                        break;
                    }
                }

                //After the user plays, we want the CPU to play
                int c = CPU.computeBestMove(); //gets the columns corresponding to the best move
                int r = CPU_Player.rowIfPlaced(c, gameBoard.getState());
                Log.d("Coords", "Row: " + r + ", Col: " + c);
                while (r == -1)
                    r = CPU_Player.rowIfPlaced((int)(Math.random()*7), gameBoard.getState());

                gameBoard.placePiece(r, c, player); //place the piece, change the color


                if(gameBoard.checkWin(r, c, player)) {
                    //Log.d("Check Win", "TRUE");
                    scoreP2++;
                    TextView scoreView = (TextView)findViewById(R.id.score_P2);
                    scoreView.setText("" + scoreP2);

                    gameBoard.resetBoard();

                    CPU.resetTree();
                } else{
                    //Log.d("Check Win", "FALSE");

                    //Need to move down game tree by 1 and extend tree by 1
                    CPU.shiftRoot(r, c, player);
                }

                player = PLAYER_1;
            }
        };


        for(int i = 0; i < gridBoard.getChildCount(); i++) {
            ImageButton button = (ImageButton) gridBoard.getChildAt(i);
            button.setImageResource(R.mipmap.piece_empty);
            button.setOnClickListener(buttonClickListener);
            String tag = (String)button.getTag();
            int row = Character.digit(tag.charAt(0), 10);
            int col = Character.digit(tag.charAt(1), 10);
            gameBoard.setButtonImg(row-1, col-1, button);
        }

    }
}
