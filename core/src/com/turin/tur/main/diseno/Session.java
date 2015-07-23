package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.turin.tur.main.util.Constants;

public class Session {

	private static final String TAG = Session.class.getName();
	public User user;
	public int numberOfLevels;
	public int nextLevel;
	
	public Session () {

		// Chequea si el usuario ya existe o si es la primera vez
		if (!Gdx.files.local(Constants.USERFILE).exists()) {
			User.CreateUser();
			Gdx.app.debug(TAG, "Creando nuevo usuario");
		}
		user = User.Load();
		
		// Chequea la cantidad de niveles que hay disponibles. Se asume que estan numerados y empiezan en 1.
		boolean isFile=true;
		int i=1;
		while (isFile) {
			if (Gdx.files.local("experimentalsource/"+ Constants.version() + "/level" + i + ".meta").exists()) {
				i++;
				isFile=true;
			} else {
				isFile=false;
			}
		}
		this.numberOfLevels = i;

		this.nextLevel=0;
	}
}
