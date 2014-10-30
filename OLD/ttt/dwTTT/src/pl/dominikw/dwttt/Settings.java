/*
 * dwTTT - a simple Tic-Tac-Toe game.
 * 
 * Copyright (C) Dominik Wlazlowski <dominik-w@dominik-w.pl>
 */

package pl.dominikw.dwttt;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

/**
 * Application settings.
 * 
 * @version 1.1
 */
public class Settings extends PreferenceActivity {
	
	public static final String DIFFICULTY_PREFERENCE_KEY = "difficulty_level";
	public static final String WHO_FIRST_PREFERENCE_KEY = "who_first";
	public static final String VICTORY_MESSAGE_PREFERENCE_KEY = "victory_message";
	public static final String SOUND_PREFERENCE_KEY = "sound";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final ListPreference difficultyLevelPref = (ListPreference) findPreference(DIFFICULTY_PREFERENCE_KEY);
		final ListPreference goesFirstPref = (ListPreference) findPreference(WHO_FIRST_PREFERENCE_KEY);
		final EditTextPreference victoryMessagePref = (EditTextPreference) findPreference(VICTORY_MESSAGE_PREFERENCE_KEY);
		
		String difficulty = prefs.getString(DIFFICULTY_PREFERENCE_KEY, getResources().getString(R.string.difficulty_expert));
		difficultyLevelPref.setSummary((CharSequence) difficulty);
		
		difficultyLevelPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				difficultyLevelPref.setSummary((CharSequence) newValue);
				
				SharedPreferences.Editor ed = prefs.edit();
				ed.putString(DIFFICULTY_PREFERENCE_KEY, newValue.toString());
				ed.commit();
				
				return true;
			}
		});
		
		String goesFirst = prefs.getString(WHO_FIRST_PREFERENCE_KEY, "Random");
		goesFirstPref.setSummary((CharSequence) goesFirst);
		
		goesFirstPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				goesFirstPref.setSummary((CharSequence) newValue);
				
				SharedPreferences.Editor ed = prefs.edit();
				ed.putString(WHO_FIRST_PREFERENCE_KEY, newValue.toString());
				ed.commit();
				
				return true;
			}
		});
		
		String victoryMessage = prefs.getString(VICTORY_MESSAGE_PREFERENCE_KEY, getResources().getString(R.string.result_human_wins));
		victoryMessagePref.setSummary("\"" + victoryMessage + "\""); // !
		
		victoryMessagePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				victoryMessagePref.setSummary((CharSequence) newValue);
				
				SharedPreferences.Editor ed = prefs.edit();
				ed.putString(VICTORY_MESSAGE_PREFERENCE_KEY, newValue.toString());
				ed.commit();
				
				return true;
			}
		});
	}
	
}
