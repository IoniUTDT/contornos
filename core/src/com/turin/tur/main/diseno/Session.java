package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;

public class Session {

	
	// Prueba
	private static final String TAG = Session.class.getName();
	public User user;
	public int numberOfLevels;
	public int nextLevel;
	public JsonSession sessionInfo;
	
	public Session () {

		// Chequea si el usuario ya existe o si es la primera vez
		if (!Gdx.files.local(Constants.USERFILE).exists()) {
			User.CreateUser();
			Gdx.app.debug(TAG, "Creando nuevo usuario");
		}
		user = User.Load();
		
		// Chequea la cantidad de niveles que hay disponibles. Se asume que estan numerados y empiezan en 1.
		boolean isFile=true;
		int i=0;
		while (isFile) {
			if (Gdx.files.internal("experimentalsource/"+ Constants.version() + "/level" + (i+1) + ".meta").exists()) {
				isFile=true;
				i++;
			} else {
				isFile=false;
			}
		}
		this.numberOfLevels = i;
		this.nextLevel=0;
		
		
	}
	
	public static class JsonSession {
		public int userID;
		public long timeOfCreation;
		public Array<Long> logins = new Array<Long>();
		
		public static JsonSession Load() {
			
			JsonSession jsonSession;
			if (Gdx.files.internal("config/"+ Constants.version() + "/session.info").exists()) {
				jsonSession = LoadFromFile();
			} else {
				jsonSession = CreateFile();
			}
			return jsonSession;
		}

		private static JsonSession CreateFile() {
			JsonSession jsonsession = new JsonSession();
			//jsonsession.timeOfCreation
			return null;
		}

		private static JsonSession LoadFromFile() {
			Json json = new Json();
			String savedData = FileHelper.readFile("config/"+ Constants.version() + "/session.info");
			return json.fromJson(JsonSession.class, savedData);
		}
		
		
	}
}
