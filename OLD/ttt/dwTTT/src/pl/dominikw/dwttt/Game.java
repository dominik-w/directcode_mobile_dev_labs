/*
 * dwTTT - a simple Tic-Tac-Toe game.
 * 
 * Copyright (C) Dominik Wlazlowski <dominik-w@dominik-w.pl>
 */

package pl.dominikw.dwttt;

import java.util.Random;

/**
 * Core game class. 
 * @version 1.0
 */
public class Game {
	
	public enum DifficultyLevel { Easy, Pro, Expert };
	public static final int sBOARD_SIZE = 9;
	
	// symbols
	public static final char sHUMAN_PLAYER = 'X';
	public static final char sCOMPUTER_PLAYER = 'O';
	public static final char sOPEN_SPOT = ' ';
	
	private Random mRand;
	private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;
	
	// main board
	private char mBoard[];
	
	/**
	 * Construct.
	 */
	public Game() {
		mBoard = new char[sBOARD_SIZE];
		mRand = new Random();
	}
	
	/**
	 * Board cleanup.
	 */
	public void clearBoard() {
		
		int i = 0;
		for (i = 0; i < sBOARD_SIZE; i++) {
			mBoard[i] = sOPEN_SPOT;
		}
	}
	
	/**
	 * Set the given player at the given location on the main board.
	 * 
	 * @param player Player ID
	 * @param location Position
	 * 
	 * @return If the move was made
	 */
	public boolean setMove(char player, int location) {
		if (location >= 0 && location < sBOARD_SIZE && mBoard[location] == sOPEN_SPOT) {
			mBoard[location] = player;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Return the board occupant ID.
	 * 
	 * @param location Position
	 * @return ID
	 */
	public char getBoardOccupant(int location) {
		if (location >= 0 && location < sBOARD_SIZE)
			return mBoard[location];
		
		return '?';
	}
	
	/**
	 * Check for the winner. Main TTT game algorithms implemented.
	 * 
	 * @return 0 if no winner or deadlock, 1 if it's a deadlock, 2 if X won, 3 if O won
	 */
	public int checkForWinner() {
		
		// horizontal
		for (int i = 0; i <= 6; i += 3) {
			if (mBoard[i] == sHUMAN_PLAYER && mBoard[i + 1] == sHUMAN_PLAYER &&
					mBoard[i + 2] == sHUMAN_PLAYER)
				return 2;
			
			if (mBoard[i] == sCOMPUTER_PLAYER && mBoard[i + 1] == sCOMPUTER_PLAYER &&
					mBoard[i + 2] == sCOMPUTER_PLAYER)
				return 3;
		}
		
		// vertical
		for (int i = 0; i <= 2; i++) {
			if (mBoard[i] == sHUMAN_PLAYER && mBoard[i + 3] == sHUMAN_PLAYER &&
					mBoard[i + 6] == sHUMAN_PLAYER)
				return 2;
			
			if (mBoard[i] == sCOMPUTER_PLAYER && mBoard[i + 3] == sCOMPUTER_PLAYER &&
					mBoard[i + 6] == sCOMPUTER_PLAYER)
				return 3;
		}
		
		if ((mBoard[0] == sHUMAN_PLAYER && mBoard[4] == sHUMAN_PLAYER && mBoard[8] == sHUMAN_PLAYER) ||
				(mBoard[2] == sHUMAN_PLAYER && mBoard[4] == sHUMAN_PLAYER && mBoard[6] == sHUMAN_PLAYER))
			return 2;
		
		if ((mBoard[0] == sCOMPUTER_PLAYER && mBoard[4] == sCOMPUTER_PLAYER && mBoard[8] == sCOMPUTER_PLAYER) ||
				(mBoard[2] == sCOMPUTER_PLAYER && mBoard[4] == sCOMPUTER_PLAYER && mBoard[6] == sCOMPUTER_PLAYER))
			return 3;
		
		// check for a deadlock
		for (int i = 0; i < sBOARD_SIZE; i++) {
			if (mBoard[i] != sHUMAN_PLAYER && mBoard[i] != sCOMPUTER_PLAYER)
				return 0;
		}
		
		return 1;
	}
	
	/**
	 * Get the difficulty level.
	 * 
	 * @return The AI's difficulty level.
	 */
	public DifficultyLevel getDifficultyLevel() {
		return mDifficultyLevel;
	}
	
	/**
	 * Set the difficulty level.
	 * 
	 * @param difficultyLevel
	 */
	public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
		mDifficultyLevel = difficultyLevel;
	}
	
	/**
	 * Simple AI - return the best move for the computer to make.
	 * 
	 * @return The best move for the computer (number)
	 */
	public int getComputerMove() {
		
		int move = -1;
		
		if (mDifficultyLevel == DifficultyLevel.Easy) {
			move = getRandomMove();
		} else if (mDifficultyLevel == DifficultyLevel.Pro) {
			move = getWinningMove();
			if (move == -1)
				move = getRandomMove();
		} else if (mDifficultyLevel == DifficultyLevel.Expert) {
			
			move = getWinningMove();
			if (move == -1)
				move = getBlockingMove();
			if (move == -1)
				move = getRandomMove();
		}
		
		return move;
	}
	
	/**
	 * Board state getter.
	 * 
	 * @return State
	 */
	public char[] getBoardState() {
		return mBoard;
	}
	
	/**
	 * Board state setter.
	 * 
	 * @param board Updated board
	 */
	public void setBoardState(char[] board) {
		mBoard = board.clone();
	}
	
	@Override
	public String toString() {
		String res = mBoard[0] + "|" + mBoard[1] + "|" + mBoard[2] + "\n";
		res += mBoard[3] + "|" + mBoard[4] + "|" + mBoard[5] + "\n";
		res += mBoard[6] + "|" + mBoard[7] + "|" + mBoard[8];
		
		return res;
	}
	
	/**
	 * Random moves generator.
	 * 
	 * @return Move (number)
	 */
	private int getRandomMove() {
		
		int move;
		do {
			move = mRand.nextInt(9);
		} while (mBoard[move] == sHUMAN_PLAYER || mBoard[move] == sCOMPUTER_PLAYER);
		
		return move;
	}
	
	/**
	 * Simple AI - get blocking move.
	 * 
	 * @return Move (number)
	 */
	private int getBlockingMove() {
		
		int i = 0;
		for (i = 0; i < sBOARD_SIZE; i++) {
			char curr = mBoard[i];
			
			if (curr != sHUMAN_PLAYER && curr != sCOMPUTER_PLAYER) {
				mBoard[i] = sHUMAN_PLAYER;
				if (checkForWinner() == 2) {
					mBoard[i] = sOPEN_SPOT;   // Restore space
					return i;
				} else mBoard[i] = sOPEN_SPOT;
			}
		}
		
		// blocking move impossible
		return -1;
	}
	
	/**
	 * Simple AI - get winning move.
	 * 
	 * @return Move (number)
	 */
	private int getWinningMove() {
		
		int i = 0;
		for (i = 0; i < sBOARD_SIZE; i++) {
			char curr = mBoard[i];
			
			if (curr != sHUMAN_PLAYER && curr != sCOMPUTER_PLAYER) {
				mBoard[i] = sCOMPUTER_PLAYER;
				if (checkForWinner() == 3) {
					mBoard[i] = sOPEN_SPOT;
					return i;
				} else
					mBoard[i] = sOPEN_SPOT;
			}
		}
		
		// winning move impossible
		return -1;
	}
	
}
