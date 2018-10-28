package glass.stephen.rowan_tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import android.os.Handler;

public class MainActivity extends AppCompatActivity
        implements OnClickListener {

    private Button gameButton[] = new Button[9];
    private Button newGameButton;
    private TextView messageText;

    private boolean playerTurn = false; // true = X (human)
    private boolean gameOver = false;

    private char mBoard[] = {'1','2','3','4','5','6','7','8','9'};
    private final int BOARD_SIZE = 9;

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';

    private Random mRand;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRand = new Random();

        newGameButton = findViewById(R.id.newgame);
        messageText = findViewById(R.id.message);
        gameButton[0] = findViewById(R.id.square1);
        gameButton[1] = findViewById(R.id.square2);
        gameButton[2] = findViewById(R.id.square3);
        gameButton[3] = findViewById(R.id.square4);
        gameButton[4] = findViewById(R.id.square5);
        gameButton[5] = findViewById(R.id.square6);
        gameButton[6] = findViewById(R.id.square7);
        gameButton[7] = findViewById(R.id.square8);
        gameButton[8] = findViewById(R.id.square9);

        for(int i =0; i < BOARD_SIZE; i++){
            gameButton[i].setOnClickListener(this);
            gameButton[i].setText("");
        }
        messageText.setText(R.string.newGame);
        newGameButton.setOnClickListener(this);
        gameOver = true;
        playerTurn = true;
    }

    /** Start new game by clearing the board and set the starting values */
    private void startNewGame() {
        if(playerTurn) { // can only start new game when its turn (or no game has been started)
            // this is to prevent a user from pressing new game during the artificial computer delay
            clearGrid();
            setStartingValues();
        }
    }

    /** set starting values, gameOver = false, turn = 1, message = Player X’s turn, game status = “ “ */
    private void setStartingValues() {
        gameOver = false;
        playerTurn = true;
        for(int i = 0; i < BOARD_SIZE; i++) { // reset boards
            mBoard[i]=Character.forDigit(49+i, 10);
        }
        clearGrid();
        setTurnText();
    }

    /** Clear the board of all X's and O's. */
    public void clearGrid() {
        for(int i = 0; i < 9; i++) {
            gameButton[i].setText(""); // set the button text blank
        }
    }

    private void setTurnText() {
        if(playerTurn) messageText.setText(R.string.turnX);
        else messageText.setText(R.string.turnO);
    }

    /* Do the stuff when a game is over e.g setting gameOver to true */
    private void doGameOver(int gameType) {
        gameOver = true;
        playerTurn = true;
        if(gameType == 1) { // tie
            messageText.setText(R.string.gameTie);
        }
        else if(gameType == 2) { // X won
            messageText.setText(R.string.gameX);
        }
        else if(gameType == 3) { // O won
            messageText.setText(R.string.gameO);
        }
        else messageText.setText(R.string.gameZ);
    }

    /**
     * Check for a winner and set gameOver to true if there is a winner or tie
     */
    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    private int checkForGameOver() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1;
    }

    private void gameButtonTap(int square) {
        if(!gameOver) {
            if (playerTurn) { // only available when it's human's turn
                if (mBoard[square-1] != HUMAN_PLAYER && mBoard[square-1] != COMPUTER_PLAYER) {
                    gameButton[square-1].setText("X");
                    mBoard[square-1] = HUMAN_PLAYER;
                    if (checkForGameOver() == 0) { // the game isn't over...go to next turn
                        playerTurn = false;
                        setTurnText();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() { // artificial delay of 500 to simulate AI turn
                                computerMove();
                                // wait for the computer to do it's move
                                if (checkForGameOver() == 0 && !gameOver) { // the game isn't over after computer, back to player
                                    playerTurn = true;
                                    setTurnText();
                                } else doGameOver(checkForGameOver());
                            }
                        }, 500);
                    } else doGameOver(checkForGameOver());
                } else {
                    Log.d(TAG, "[Error] Space already occupied");
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.errorSpace,
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else {
                Log.d(TAG, "[Error] Not human turn, cannot choose now");
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.errorTurn,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        else {
            Log.d(TAG, "[Error] Game over, can't click here");
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.errorNoGame,
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void computerMove() {
        int move;

        // First see if there's a move O can make to win
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForGameOver() == 3) {
                    gameButton[i].setText("O");
                    Log.d(TAG, "Computer is moving to " + (i + 1));
                    return;
                }
                else mBoard[i] = curr;
            }
        }

        // See if there's a move O can make to block X from winning
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                char curr = mBoard[i];   // Save the current number
                mBoard[i] = HUMAN_PLAYER;
                if (checkForGameOver() == 2) {
                    mBoard[i] = COMPUTER_PLAYER;
                    gameButton[i].setText("O");
                    Log.d(TAG, "Computer is moving to " + (i + 1));
                    return;
                }
                else mBoard[i] = curr;
            }
        }

        // Generate random move
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        }
        while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);

        Log.d(TAG, "Computer is moving to " + (move + 1));

        mBoard[move] = COMPUTER_PLAYER;
        gameButton[move].setText("O");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newgame:
                startNewGame();
                break;
            case R.id.square1:
                gameButtonTap(1);
                break;
            case R.id.square2:
                gameButtonTap(2);
                break;
            case R.id.square3:
                gameButtonTap(3);
                break;
            case R.id.square4:
                gameButtonTap(4);
                break;
            case R.id.square5:
                gameButtonTap(5);
                break;
            case R.id.square6:
                gameButtonTap(6);
                break;
            case R.id.square7:
                gameButtonTap(7);
                break;
            case R.id.square8:
                gameButtonTap(8);
                break;
            case R.id.square9:
                gameButtonTap(9);
                break;
            default:
                break;
        }
    }
}
