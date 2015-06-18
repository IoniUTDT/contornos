package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameConf {
	
	public static final String TAG = GameConf.class.getName();
	public static final GameConf instance = new GameConf();
	public String modo;
	private Preferences prefs;
	// singleton: prevent instantiation from other classes
	private GameConf () {
		prefs = Gdx.app.getPreferences(Constants.CONFIGURACION);
	}

	public void load () { 
		modo = prefs.getString(Constants.Diseno.MODO_ACTIVO, Constants.Diseno.MODO_ENTRENAMIENTO);
	}

	public void save () { 
		prefs.putString(Constants.Diseno.MODO_ACTIVO, modo);
		prefs.flush();
	}
}

