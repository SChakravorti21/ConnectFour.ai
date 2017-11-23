package comschakravorti21.github.connectfourai.try2;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.GenericArrayType;
import java.util.concurrent.Executor;

import comschakravorti21.github.connectfourai.R;

public class MainActivity2 extends AppCompatActivity {

    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int CPU = 3;

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private GridLayout gridBoard;
    //private int[][] pieces;
    private int player;
    public Gameboard2 gameboard;
    private int scoreP1, scoreP2;
    private CPUPlayer cpu;
    boolean waitingForGeneration;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_reset:
                gameboard.resetBoard();
                TextView scoreView1 = findViewById(R.id.score_P1);
                scoreView1.setText("0");
                TextView scoreView2 = findViewById(R.id.score_P2);
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
        setContentView(R.layout.activity_main2);
        gridBoard = findViewById(R.id.board);
        //pieces = new int[Gameboard2.ROWS][Gameboard2.COLUMNS];
        player = PLAYER_1;
        scoreP1 = scoreP2 = 0;
        cpu = new CPUPlayer();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        gameboard = new Gameboard2();
        waitingForGeneration = false;

        //No need to redefine the OnClickListener for every button, so we can just use the
        //same one
        View.OnClickListener buttonClickListener = new ButtonClickListener();


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


    private class ButtonClickListener implements View.OnClickListener {
        Executor executor;
        Runnable run;

        public ButtonClickListener() {
            executor = new Executor() {
                @Override
                public void execute(@NonNull Runnable runnable) {
                    new Thread(runnable).start();
                }
            };

            run = new Runnable() {
                @Override
                public void run() {
                    final int bestCol = cpu.minimax(gameboard.getBoard(), player); //get the best move for CPU
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                    final int row = Gameboard2.rowIfPlaced(bestCol, gameboard.getBoard());
                    if(row != -1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Idk why this doesn't work when param is just "player"
                                gameboard.placePiece(row, bestCol, PLAYER_2);
                            }
                        });

                        Log.d("Static eval for player " + player,
                                "" + cpu.staticEval(gameboard.getBoard(), player));

                        if(Gameboard2.checkWin(row, bestCol, gameboard.getBoard(), player)) {
                            Log.d("Check Win", "TRUE");
                            if(player == PLAYER_1) {
                                scoreP1++;
                                TextView scoreView = (TextView)findViewById(R.id.score_P1);
                                scoreView.setText("" + scoreP1);
                            } else {
                                scoreP2++;
                                TextView scoreView = (TextView)findViewById(R.id.score_P2);
                                scoreView.setText("" + scoreP2);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gameboard.resetBoard();
                                }
                            });
                        }
                    }

                    player = PLAYER_1; //Swap player
                    waitingForGeneration = false; //Move generation is complete, user can play again
                }
            };
        }

        @Override
        public void onClick(View view) {
            if(waitingForGeneration)
                return;

            ImageButton button = (ImageButton)view;
            waitingForGeneration = true;

            //Button tags were defined as Strings with "rowcol" information, so getting
            //the second character gives the column
            int col = Character.digit( ((String)button.getTag() ).charAt(1), 10) - 1;

            //Start from bottom when searching where to insert piece
            int row = Gameboard2.rowIfPlaced(col, gameboard.getBoard());
            if(row != -1) {
                gameboard.placePiece(row, col, player);

                Log.d("Static eval for player " + player,
                        "" + cpu.staticEval(gameboard.getBoard(), player));

                if(Gameboard2.checkWin(row, col, gameboard.getBoard(), player)) {
                    Log.d("Check Win", "TRUE");
                    if(player == PLAYER_1) {
                        scoreP1++;
                        TextView scoreView = (TextView)findViewById(R.id.score_P1);
                        scoreView.setText("" + scoreP1);
                    } else {
                        scoreP2++;
                        TextView scoreView = (TextView)findViewById(R.id.score_P2);
                        scoreView.setText("" + scoreP2);
                    }

                    gameboard.resetBoard();
                }

            }

            player = PLAYER_2; //Swap player

            //Set the progress bar to be visible to prevent it from  looking like UI freeze
            progressBar.setVisibility(View.VISIBLE);
            executor.execute(run);
        }
    }
}