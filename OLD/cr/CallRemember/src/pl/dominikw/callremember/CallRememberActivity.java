/*
 * Simple recorder of phone calls.
 * Copyright (C) Dominik Wlazlowski <dominik-w@dominik-w.pl>
 */

package pl.dominikw.callremember;

import android.os.Bundle;
// import android.os.Environment;
import android.app.Activity;
import java.io.*;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;

/**
 * Core activity.
 * 
 * @author Dominik Wlazlowski
 * @version 0.5
 * 
 * Todo: see TODO.txt
 */
public class CallRememberActivity extends Activity {
	
	private MediaPlayer mediaPlayer;
	private MediaRecorder recorder;
	
	private static final String TAG = "CallRememberActivity";
	
	private static String OUTPUT_FILE= "/sdcard/callrec.3gpp"; // default
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button startBtn = (Button) findViewById(R.id.startBtn);
		Button endBtn = (Button) findViewById(R.id.stopBtn);
		Button playRecordingBtn = (Button) findViewById(R.id.playBtn);
		
		Button stpPlayingRecordingBtn = (Button) findViewById(R.id.stopPlayBtn);
		
		startBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				try {
					beginRecording();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		endBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				try {
					stopRecording();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		playRecordingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				try {
					playRecording();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		stpPlayingRecordingBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				try {
					stopPlayingRecording();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void beginRecording() throws Exception {
		
		killMediaRecorder();
		
		// advenced
		/*
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath() + "/callrec");
		dir.mkdirs();
		File mSampleFile = File.createTempFile("myPrefix", ".3gpp", dir);
		// System.out.println(mSampleFile.getAbsolutePath());
		FileOutputStream f = new FileOutputStream(mSampleFile);
		*/

		// simple
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String cdt = sdf.format(new Date());

		OUTPUT_FILE = "/sdcard/callrec_" + cdt + ".3gpp";
		Log.i(TAG, cdt);
		
		File outFile = new File(OUTPUT_FILE);
		
		if (outFile.exists()) {
			outFile.delete();
		}
		
		recorder = new MediaRecorder();
		// define type of call, output format and encoder
		recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		recorder.setOutputFile(OUTPUT_FILE);
		
		// optional control
		// recorder.setMaxDuration(60000); // [ms]
		// recorder.setMaxFileSize(1048576); // bytes
		
		recorder.prepare();
		recorder.start();
	}
	
	private void stopRecording() throws Exception {
		if (recorder != null) {
			recorder.stop();
		}
	}
	
	private void killMediaRecorder() {
		if (recorder != null) {
			recorder.release();
		}
	}
	
	private void killMediaPlayer() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void playRecording() throws Exception {
		
		killMediaPlayer();
		
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(OUTPUT_FILE);
		
		mediaPlayer.prepare();
		mediaPlayer.start();
	}
	
	private void stopPlayingRecording() throws Exception {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		killMediaRecorder();
		killMediaPlayer();
	}
}
