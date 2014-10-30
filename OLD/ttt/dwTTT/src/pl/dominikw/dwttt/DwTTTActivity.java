/*
 * dwTTT - a simple Tic-Tac-Toe game.
 * 
 * Copyright (C) Dominik Wlazlowski <dominik-w@dominik-w.pl>
 */
package pl.dominikw.dwttt;

import pl.dominikw.dwttt.MainBoardView;
import pl.dominikw.dwttt.Settings;
import pl.dominikw.dwttt.Game;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.app.Dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.graphics.Color;
import android.media.MediaPlayer;

public class DwTTTActivity extends Activity {
	
	// the game board
	private MainBoardView mBoardView;
	
	// dialog boxes IDs
	static final int sDIALOG_QUIT_ID = 1;
	static final int sDIALOG_ABOUT = 2;
	
	// states
	private Game mGame;
	private boolean mGameOver = false;
	private char mWhoFirst = Game.sHUMAN_PLAYER;
	private char mTurn = Game.sCOMPUTER_PLAYER;
	
	private int mHumanWins = 0;
	private int mComputerWins = 0;
	private int mDeadlocks = 0;
	
	// settings
	private SharedPreferences mPrefs;
	
	// labels
	private TextView mInfoTextView;
	private TextView mHumanScoreTextView;
	private TextView mComputerScoreTextView;
	private TextView mDeadlockscoreTextView;
	
	public boolean mSoundOn;
	
	MediaPlayer mHumanSoundPlayer;
	MediaPlayer mComputerSoundPlayer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// setup
		mGame = new Game();
		mBoardView = (MainBoardView) findViewById(R.id.board);
		mBoardView.setGame(mGame);
		
		// labels
		mInfoTextView = (TextView) findViewById(R.id.information);
		mDeadlockscoreTextView = (TextView) findViewById(R.id.deadlock_score);
		mHumanScoreTextView = (TextView) findViewById(R.id.player_score);
		mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);
		
		// restore scores if exists
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mSoundOn = mPrefs.getBoolean(Settings.SOUND_PREFERENCE_KEY, true);
		mHumanWins = mPrefs.getInt("mHumanWins", 0);
		mComputerWins = mPrefs.getInt("mComputerWins", 0);
		mDeadlocks = mPrefs.getInt("mDeadlocks", 0);
		
		mBoardView.setBoardColor(Color.GREEN);
		
		// touch listener
		mBoardView.setOnTouchListener(mTouchListener);
		
		// set the level
		String difficultyLevel = mPrefs.getString(Settings.DIFFICULTY_PREFERENCE_KEY,
				getResources().getString(R.string.difficulty_pro));
		if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
			mGame.setDifficultyLevel(Game.DifficultyLevel.Easy);
		else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_pro)))
			mGame.setDifficultyLevel(Game.DifficultyLevel.Pro);
		else
			mGame.setDifficultyLevel(Game.DifficultyLevel.Expert);
		
		if (savedInstanceState == null) {
			startNewGame();
		}
		else {
			// restore the state
			mGame.setBoardState(savedInstanceState.getCharArray("board"));
			mGameOver = savedInstanceState.getBoolean("mGameOver");
			mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
			mTurn = savedInstanceState.getChar("mTurn");
			mWhoFirst = savedInstanceState.getChar("mWhoFirst");
			
			if (!mGameOver && mTurn == Game.sCOMPUTER_PLAYER) {
				int move = mGame.getComputerMove();
				setMove(Game.sCOMPUTER_PLAYER, move);
			}
		}
		
		showScores();
	}
	
	/**
	 * Menu items handler.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.new_game:
			startNewGame();
			
			return true;
		case R.id.reset_scores:
			mDeadlocks = 0;
			mHumanWins = 0;
			mComputerWins = 0;
			showScores();
			
			return true;
		case R.id.settings:
			startActivityForResult(new Intent(this, Settings.class), 0);
			
			return true;
		case R.id.about:
			showDialog(sDIALOG_ABOUT);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ttt_menu, menu);
		
		return true;
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		mComputerSoundPlayer.release();
		mHumanSoundPlayer.release();
	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
		
		// save the current score
		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putInt("mDeadlocks", mDeadlocks);
		ed.putInt("mHumanWins", mHumanWins);
		ed.putInt("mComputerWins", mComputerWins);
		ed.commit();
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		
		// the same sound
		mComputerSoundPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gong);
		mHumanSoundPlayer = MediaPlayer.create(getApplicationContext(), R.raw.gong);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putCharArray("board", mGame.getBoardState());
		outState.putBoolean("mGameOver", mGameOver);
		outState.putCharSequence("info", mInfoTextView.getText());
		outState.putChar("mWhoFirst", mWhoFirst);
		outState.putChar("mTurn", mTurn);
	}
	
	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent data) {
		
		// process
		if (reqCode == RESULT_CANCELED) {
			
			mSoundOn = mPrefs.getBoolean(Settings.SOUND_PREFERENCE_KEY, true);
			
			String difficultyLevel = mPrefs.getString(Settings.DIFFICULTY_PREFERENCE_KEY,
					getResources().getString(R.string.difficulty_pro));
			if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
				mGame.setDifficultyLevel(Game.DifficultyLevel.Easy);
			else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_pro)))
				mGame.setDifficultyLevel(Game.DifficultyLevel.Pro);
			else
				mGame.setDifficultyLevel(Game.DifficultyLevel.Expert);
			
			String who_first = mPrefs.getString(Settings.WHO_FIRST_PREFERENCE_KEY, "Random");
			if (!who_first.equals("Random")) {
				for (int i = 0; i < 8; i++)
					if (mGame.getBoardOccupant(i) != Game.sOPEN_SPOT)
						return;
				
				startNewGame();
			}
		}
	}
	
	/**
	 * Game board setup.
	 */
	private void startNewGame() {
		
		// reset the board
		mGame.clearBoard();
		mBoardView.invalidate();
		
		// who first
		String whoFirst = mPrefs.getString(Settings.WHO_FIRST_PREFERENCE_KEY, "Random");
		
		if (whoFirst.equals("Random")) {
			if (mWhoFirst == Game.sCOMPUTER_PLAYER) {
				mWhoFirst = Game.sHUMAN_PLAYER;
				mTurn = Game.sCOMPUTER_PLAYER;
			} else {
				mWhoFirst = Game.sCOMPUTER_PLAYER;
				mTurn = Game.sHUMAN_PLAYER;
			}
		} else if (whoFirst.equals("Human"))
			mTurn = Game.sHUMAN_PLAYER;
		else
			mTurn = Game.sCOMPUTER_PLAYER;
		
		if (mTurn == Game.sCOMPUTER_PLAYER) {
			mInfoTextView.setText(R.string.first_computer);
			int move = mGame.getComputerMove();
			setMove(Game.sCOMPUTER_PLAYER, move);
		} else
			mInfoTextView.setText(R.string.first_human);
		
		mGameOver = false;
	}
	
	/**
	 * Move maker.
	 * 
	 * @param player Player's ID
	 * @param location Position on the board
	 * @return boolean
	 */
	private boolean setMove(char player, int location) {
		
		if (player == Game.sCOMPUTER_PLAYER) {
			// computer makes move after 500ms
			final int loc = location;
			Handler handler = new Handler();
			
			handler.postDelayed(new Runnable() {
				public void run() {
					mGame.setMove(Game.sCOMPUTER_PLAYER, loc);
					mBoardView.invalidate();
					
					try {
						// play sound
						if (mSoundOn) mComputerSoundPlayer.start();
					} catch (IllegalStateException e) {};
					
					int winner = mGame.checkForWinner();
					if (winner == 0) {
						mTurn = Game.sHUMAN_PLAYER;
						mInfoTextView.setText(R.string.turn_human);
					} else 
						endGame(winner);
				}
			}, 500);
			
			return true;
		} else if (mGame.setMove(Game.sHUMAN_PLAYER, location)) {
			mTurn = Game.sCOMPUTER_PLAYER;
			mBoardView.invalidate();
			if (mSoundOn) mHumanSoundPlayer.start();
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Game stop handler.
	 * 
	 * @param winner Who wins
	 */
	private void endGame(int winner) {
		
		if (winner == 1) {
			mDeadlocks++;
			mDeadlockscoreTextView.setText(Integer.toString(mDeadlocks));
			mInfoTextView.setText(R.string.result_tie);
		} else if (winner == 2) {
			mHumanWins++;
			mHumanScoreTextView.setText(Integer.toString(mHumanWins));
			
			String defaultMessage = getResources().getString(R.string.result_human_wins);
			mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
		} else if (winner == 3) {
			mComputerWins++;
			mComputerScoreTextView.setText(Integer.toString(mComputerWins));
			mInfoTextView.setText(R.string.result_computer_wins);
		}
		
		mGameOver = true;
	}
	
	/**
	 * Touch listener.
	 */
	private OnTouchListener mTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			
			int col = (int) event.getX() / mBoardView.getBoardCellWidth();
			int row = (int) event.getY() / mBoardView.getBoardCellHeight();
			int pos = row * 3 + col;
			
			if (!mGameOver && mTurn == Game.sHUMAN_PLAYER && setMove(Game.sHUMAN_PLAYER, pos)) {
				
				int winner = mGame.checkForWinner();
				if (winner == 0) {
					mInfoTextView.setText(R.string.turn_computer);
					int move = mGame.getComputerMove();
					setMove(Game.sCOMPUTER_PLAYER, move);
				} else
					endGame(winner);
			}
			
			return false;
		}
	};
	
	/**
	 * Show scores.
	 */
	private void showScores() {
		mHumanScoreTextView.setText(Integer.toString(mHumanWins));
		mComputerScoreTextView.setText(Integer.toString(mComputerWins));
		mDeadlockscoreTextView.setText(Integer.toString(mDeadlocks));
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		switch (id) {
		case sDIALOG_ABOUT:
			Context context = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.about, null);
			
			builder.setView(layout);
			builder.setPositiveButton("OK", null);
			dialog = builder.create();
			
			break;
			
		case sDIALOG_QUIT_ID:
			builder.setMessage(R.string.quit_question)
			.setCancelable(false)
			.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					DwTTTActivity.this.finish();
				}
			})
			.setNegativeButton(R.string.no, null);
			dialog = builder.create();
			
			break;
		}
		
		return dialog;
	}
	
}
