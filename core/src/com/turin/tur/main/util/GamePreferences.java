package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GamePreferences {
	
	public static final String TAG = GamePreferences.class.getName();
	public static final GamePreferences instance = new GamePreferences();
	public String hola;
	private Preferences prefs;
	// singleton: prevent instantiation from other classes
	private GamePreferences () {
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
	}

	public void load () { 
		hola = prefs.getString("hola", "Hola Mundo");
	}

	public void save () { 
		prefs.putString("hola", hola);
		prefs.flush();
	}
}

