/*
 * Alcohol control. A simple calculator of BAC.
 * 
 * Copyright (C) Dominik Wlazlowski <dominik-w@dominik-w.pl>
 */

package pl.dominikw.ALControl;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Button;
import android.view.*;
import android.content.Intent;
import android.content.res.Configuration;
import java.util.Locale;
// import android.util.Log;

/**
 * Core class.
 * 
 * @version 1.1
 */
public class ALControlActivity extends Activity {
	
	// setup
	// private static final String TAG = ALControlActivity.class.getSimpleName();
	
	private EditText mPercentage  = null;
	private EditText mQuantity    = null;
	private EditText mMass        = null;
	private RadioGroup mGender    = null;
	private TextView mResult      = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// get values from UI inputs
		mPercentage  = (EditText) findViewById(R.id.percentage);
		mQuantity    = (EditText) findViewById(R.id.quantity);
		mMass        = (EditText) findViewById(R.id.mass);
		mGender      = (RadioGroup) findViewById(R.id.gender);
		mResult      = (TextView) findViewById(R.id.result);
		
		Button calcBtn = (Button) findViewById(R.id.calculate);
		calcBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ALControlActivity.this.calculate();
			}
		});
		
		ImageButton plBtn = (ImageButton) findViewById(R.id.plBtn);
		plBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Log.i(TAG, "Set polish");
				
				Locale locale = new Locale("pl");
				Locale.setDefault(locale);
				Configuration config = new Configuration();
				config.locale = locale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());
				
				// refresh for apply changes
				refresh();
			}
		});
		
		ImageButton enBtn = (ImageButton) findViewById(R.id.enBtn);
		enBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Log.i(TAG, "Set english");
				
				Locale locale = new Locale("en");
				Locale.setDefault(locale);
				Configuration config = new Configuration();
				config.locale = locale;
				getBaseContext().getResources().updateConfiguration(config,
						getBaseContext().getResources().getDisplayMetrics());
				
				// refresh for apply changes
				refresh();
			}
		});
		
		// dbg turned off
	}
	
	/**
	 * Reload intent for apply configuration changes.
	 */
	private void refresh() {
		
		finish();
		Intent myIntent = new Intent(ALControlActivity.this, ALControlActivity.class);
		startActivity(myIntent);
	}
	
	/**
	 * Calculate the result.
	 */
	private void calculate() {
    	
    	boolean isAMan = false;
    	float calcRes  = 0.0f;
    	String resMsg  = "";
    	
    	if (mPercentage.getText().toString().equals("") || mMass.getText().toString().equals("") ||
    			mQuantity.getText().toString().equals("") ) {
    		mResult.setText(R.string.invalid_data_error);
    	} else {
    		
    		int percentageVal  = Integer.valueOf(mPercentage.getText().toString());
    		int massVal        = Integer.valueOf(mMass.getText().toString());
    		int quantityVal    = Integer.valueOf(mQuantity.getText().toString());
    		
    		if (percentageVal == 0 || massVal == 0 || quantityVal == 0) {
    			mResult.setText(R.string.invalid_data_error);
    		} else {
    			
    			isAMan = mGender.getCheckedRadioButtonId() == R.id.gender_male;
    			// Log.d(TAG, "Dbg val: " + isAMan);
    			
    			// see: http://piro.wikidot.com/teoria
    			calcRes = percentageVal * quantityVal * 79 / massVal / (isAMan ? 70 : 60) / 100f;
    			
    			// convert to string
    			String str = Float.toString(calcRes);
    			resMsg = getApplication().getResources().getString(R.string.result_label) + " " + str + "‰"; // &#8240;
    			
    			mResult.setText(resMsg);
    		}
    	}
    }
    
}
