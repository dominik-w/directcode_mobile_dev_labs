/*
 * dwTTT - a simple Tic-Tac-Toe game.
 * 
 * Copyright (C) Dominik Wlazlowski <dominik-w@dominik-w.pl>
 */

package pl.dominikw.dwttt;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Paint;

/**
 * The main game board view.
 * @version 1.0
 */
@SuppressWarnings("unused")
public class MainBoardView extends View  {
	
	private Game mGame;
	
	// board grid lines width
	public static final int GRID_WIDTH = 6;
	
	private Paint mPaint;
	
	private int mBoardColor = Color.LTGRAY;
	
	private Bitmap mComputerBitmap;
	private Bitmap mHumanBitmap;
	
	/**
	 * Constructors overloaded.
	 */

	public MainBoardView(Context context) {
		super(context);
		initialize();
	}
	
	public MainBoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}
	
	public MainBoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	
	public void initialize() {
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		mComputerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o_image);
		mHumanBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x_image);
	}
	
	public void setGame(Game game) {
		mGame = game;
	}
	
	public void setBoardColor(int boardColor) {
		mBoardColor = boardColor;
	}
	
	public int getBoardColor() {
		return mBoardColor;
	}
	
	public int getBoardCellWidth() {
		return getWidth() / 3;
	}
	
	public int getBoardCellHeight() {
		return getHeight() / 3;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
		
		int boardWidth = getWidth();
		int boardHeight = getHeight();
		
		mPaint.setColor(mBoardColor);
		mPaint.setStrokeWidth(GRID_WIDTH);
		
		// draw the board lines
		int cellWidth = getBoardCellWidth();
		canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
		canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, boardHeight, mPaint);
		int cellHeight = getBoardCellHeight();
		canvas.drawLine(0, cellHeight, boardWidth, cellHeight, mPaint);
		canvas.drawLine(0, cellHeight * 2, boardWidth, cellHeight * 2, mPaint);
		
		// draw X, O images
		for (int i = 0; i < Game.sBOARD_SIZE; i++) {
			int col = i % 3;
			int row = i / 3;
			
			// define boundaries
			int left = col * cellWidth + GRID_WIDTH;
			int top = row * cellHeight + GRID_WIDTH;
			int right = left + cellWidth - 10;
			int bottom = top + cellHeight - GRID_WIDTH - 6;
			
			if (mGame != null && mGame.getBoardOccupant(i) == Game.sHUMAN_PLAYER) {
				canvas.drawBitmap(mHumanBitmap,
						null, // src
						new Rect(left, top, right, bottom), // dest
						null);
			} else if (mGame != null && mGame.getBoardOccupant(i) == Game.sCOMPUTER_PLAYER) {
				canvas.drawBitmap(mComputerBitmap,
						null, // src
						new Rect(left, top, right, bottom), // dest
						null);
			}
		}
	}
	
}
