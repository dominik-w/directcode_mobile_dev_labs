package pl.dominikw;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class AsyncTaskActivity extends Activity {
	
	final private int START_PROGRESS = 0;
	final private int STOP_PROGRESS = 100;
	
	private Button button;
	private ProgressBar progressBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		progressBar = (ProgressBar)findViewById(R.id.progressbar_Horizontal);
		
		button = (Button)findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new WymagajacyWatek().execute("");
			}});
	}
	
	private class WymagajacyWatek extends AsyncTask<String, Integer, String> {
		
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			int i, a;
						
			for (i = 0; i < 100; i++) {
				publishProgress(i);
				
				a = 0;
				while (a < 1500) {
					a++;
				}
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			setProgressBar(STOP_PROGRESS);
			button.setEnabled(true);
		}
		
		@Override
		protected void onPreExecute() {
			//"wyzerujemy" progress bar
			setProgressBar(START_PROGRESS);
			//zablokujmy przycisk na czas dzialania watku
			button.setEnabled(false);
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			setProgressBar(progress[0]);
		}
	}
	
	private void setProgressBar(int progress) {
		progressBar.setProgress(progress);
	}
}
