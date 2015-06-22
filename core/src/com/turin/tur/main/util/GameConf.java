package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class GameConf {
	
	public static final String TAG = GameConf.class.getName();
	public static final GameConf instance = new GameConf();
	public TIPOdeTRIAL modo;
	public String modoString;
	private Preferences prefs;
	// singleton: prevent instantiation from other classes
	private GameConf () {
		prefs = Gdx.app.getPreferences(Constants.CONFIGURACION);
	}

	public void load () { 
		modoString = prefs.getString(Constants.Diseno.TIPOdeTRIAL.class.getName(), Constants.Diseno.TIPOdeTRIAL.ENTRENAMIENTO.toString());
		modo = TIPOdeTRIAL.valueOf(modoString);
	}

	public void save () { 
		modoString = modo.toString();
		prefs.putString(Constants.Diseno.TIPOdeTRIAL.class.getName(), modoString);
		prefs.flush();
	}
}

