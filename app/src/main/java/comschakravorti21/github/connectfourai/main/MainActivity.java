package comschakravorti21.github.connectfourai.main;

/**
 * Created by Shoumyo Chakravorti on 11/21/17.
 */
import android.content.ContentValues;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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

import java.util.Arrays;

import comschakravorti21.github.connectfourai.R;

public class MainActivity extends AppCompatActivity {

    /**
     * Player 1's corresponding player number in game states
     */
    public static final int PLAYER_1 = 1;

    /**
     * Player 2's corresponding player number in game states
     */
    public static final int PLAYER_2 = 2;

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private GridLayout gridBoard;
    private GameBoard gameboard;
    private CPUPlayer cpu;
    private int player;
    private int scoreP1, scoreP2;
    private boolean generatingMove;
    public MediaPlayer songPlayer;

    /**
     * Overrides AppCompatActivity's onOptionsItemsSelected method.
     * Currently the only option is to reset the game.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_reset:
                //If user wishes to restart the game, reset the board and scores
                gameboard.resetBoard();
                TextView scoreView1 = findViewById(R.id.score_P1);
                scoreView1.setText("0");
                TextView scoreView2 = findViewById(R.id.score_P2);
                scoreView2.setText("0");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        songPlayer.start();
    }


    @Override
    public void onPause(){
        super.onPause();
        songPlayer.pause();
    }

    @Override
    public void onStop(){
        super.onStop();
        songPlayer.stop();
        ContentValues values = new ContentValues();
        values.put("h", String.valueOf(this.gameboard));
    }
    /**
     * Overrides AppCompatActivity's onCreateOptionsMenu method.
     * Inflates the toolbar menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Overrides AppCompatActivity's onCreate method.
     * Instantiates all fields and attaches listeners to game board buttons
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridBoard = findViewById(R.id.board);
        player = PLAYER_1;
        scoreP1 = scoreP2 = 0;
        generatingMove = false;
        cpu = new CPUPlayer();

        //Set the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Progress bar to display during CPU move generation
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        gameboard = new GameBoard();

        //No need to redefine the OnClickListener for every button, so we can just use the
        //same one
        View.OnClickListener buttonClickListener = new ButtonClickListener();

        songPlayer = MediaPlayer.create(this, R.raw.connect4);
        songPlayer.start();

        //Get all ImageButtons, set their image resources to empty cell images, attach
        //listeners, and use the button tag to add the button to the gameboard object
        for(int i = 0; i < gridBoard.getChildCount(); i++) {
            ImageButton button = (ImageButton) gridBoard.getChildAt(i);
            button.setImageResource(R.mipmap.piece_empty);
            button.setOnClickListener(buttonClickListener);
            String tag = (String)button.getTag();
            int row = Character.digit(tag.charAt(0), 10);
            int col = Character.digit(tag.charAt(1), 10);
            gameboard.setButton(row-1, col-1, button);
        }

    }

    /**
     *  A private class implementing the View.OnClickListener interface to define
     *  game board button behavior.
     */
    private class ButtonClickListener implements View.OnClickListener {
        /**
         * Overrides View's onClick method. Since the only buttons are ones on the game
         * board, this method parses the button's row and column to place the user's piece.
         * Afterwards, a MoveGenerator AsyncTask is used to generate the CPU move.
         * @param view
         */
        @Override
        public void onClick(View view) {
            //Do not allow player to do anything while CPU is generating a move
            if(generatingMove)
                return;

            //All buttons are ImageButtons
            ImageButton button = (ImageButton)view;

            //Button tags were defined as Strings with "rowcol" information, so getting
            //the second character gives the column
            int col = Character.digit( ((String)button.getTag()).charAt(1), 10) - 1;

            //If this column can be played in, get the respective row to play in
            int row = GameBoard.rowIfPlaced(col, gameboard.getBoard());
            if(row != -1) {
                gameboard.placePiece(row, col, player);

                //Check for a win
                if(GameBoard.checkWin(row, col, gameboard.getBoard(), player)) {
                    if(player == PLAYER_1) {
                        scoreP1++;
                        TextView scoreView = (TextView)findViewById(R.id.score_P1);
                        scoreView.setText("" + scoreP1);
                    } else {
                        scoreP2++;
                        TextView scoreView = (TextView)findViewById(R.id.score_P2);
                        scoreView.setText("" + scoreP2);
                    }

                    //reset the board if someone wins
                    gameboard.resetBoard();
                }

                player = PLAYER_2; //Swap player

                //If the board fills up, clear it
                if(gameboard.isFull()) {
                    gameboard.resetBoard();
                }

                //Set global variable to true when a move is being generated
                //Prevents user from spamming buttons while CPU is generating a move
                generatingMove = true;
                MoveGenerator moveGenerator = new MoveGenerator();
                moveGenerator.execute();
            }
        }
    }

    /**
     * Private class extending AsyncTask to generate CPU moves. An AsyncTask was used
     * in order to update progress on the screen and prevent it from looking
     * like UI freeze.
     */
    private class MoveGenerator extends AsyncTask<Void, Void, Void> {
        private int row;
        private int bestCol;

        /**
         * Instantiates MoveGenerator with dummy row and column values.
         */
        public MoveGenerator() {
            //Negative values used for debugging
            row = -1;
            bestCol = -1;
        }

        /**
         * Indicates to user that a move generation process will begin
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Set the progress bar to visible because we will begin the move generation
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Decides the best move for CPU to play.
         * @param voids list of voids (required by AsyncTask))
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {
            long start = System.nanoTime();

            //Generate the best move and log how long it took
            bestCol = cpu.generateMove(gameboard.getBoard());

            Log.d("Calculation time", "" + (System.nanoTime() - start)/1000000 + " milliseconds");

            row = GameBoard.rowIfPlaced(bestCol, gameboard.getBoard());

            return null;
        }

        /**
         * Updates the game board with the best move that the CPU chose.
         * @param aVoid void parameter (required by AsyncTask)
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //Only allow CPU to play a move after a move is generated
            //This check was included because onPostExecute seems to update the
            //UI multiple times sometimes.
            if(generatingMove) {
                //Same functionality as when the user plays a piece
                if (row != -1) {
                    gameboard.placePiece(row, bestCol, PLAYER_2);

                    if (GameBoard.checkWin(row, bestCol, gameboard.getBoard(), player)) {
                        Log.d("Check Win", "TRUE");
                        for(int[] row : gameboard.getBoard()) {
                            Log.d("Rows ", Arrays.toString(row));
                        }

                        if (player == PLAYER_1) {
                            scoreP1++;
                            TextView scoreView = (TextView) findViewById(R.id.score_P1);
                            scoreView.setText("" + scoreP1);
                        } else {
                            scoreP2++;
                            TextView scoreView = (TextView) findViewById(R.id.score_P2);
                            scoreView.setText("" + scoreP2);
                        }

                        gameboard.resetBoard();
                    }
                }

                row = -1;
                bestCol = -1;
                player = PLAYER_1; //Swap player

                if (gameboard.isFull()) {
                    gameboard.resetBoard();
                }

                //Move generation is complete, allow user to make a move
                progressBar.setVisibility(View.GONE);
                generatingMove = false;
            }
        }
    }
}