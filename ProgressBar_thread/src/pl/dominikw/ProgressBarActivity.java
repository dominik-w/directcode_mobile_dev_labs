package pl.dominikw;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

public class ProgressBarActivity extends Activity {
	
	ProgressBar myProgressBar;
	int myProgress = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		myProgressBar = (ProgressBar)findViewById(R.id.progressbar_Horizontal);
		
		new Thread(myThread).start();
	}
	
	private Runnable myThread = new Runnable() {
		
		@Override
		public void run() {
			while (myProgress < 100) {
				try {
					myHandle.sendMessage(myHandle.obtainMessage());
					Thread.sleep(500);
				} catch(Throwable t) { }
			}
		}
		
		Handler myHandle = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				myProgress++;
				myProgressBar.setProgress(myProgress);
			}
		};
	};
}
