package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Enviables.STATUS;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;

public class Session {

	private static final String TAG = Session.class.getName();
	public User user;
	public int numberOfLevels;
	public int nextLevel;
	public SessionLog sessionLog;
	public SessionLogHistory sessionLogHistory;

	public Session() {
		Internet.Check();
		loadUser();
		initSession();
		loadLevels();
	}

	private void loadLevels() {
		// Chequea la cantidad de niveles que hay disponibles. Se asume que estan numerados y empiezan en 1.
		boolean isFile = true;
		int i = 0;
		while (isFile) {
			if (Gdx.files.internal("experimentalsource/" + Constants.version() + "/level" + (i + 1) + ".meta").exists()) {
				isFile = true;
				i++;
			} else {
				isFile = false;
			}
		}
		this.numberOfLevels = i;
		this.nextLevel = 0;

	}

	private void initSession() {
		// Crea la session
		this.sessionLog = new SessionLog();
		this.sessionLog.userID = this.user.id;
		sessionLogHistory = new SessionLogHistory();
		sessionLogHistory.append(this.sessionLog);
	}

	private void loadUser() {
		// Chequea si el usuario ya existe o si es la primera vez
		if (!Gdx.files.local(Constants.USERFILE).exists()) {
			User.CreateUser();
			Gdx.app.debug(TAG, "Creando nuevo usuario");
		}
		this.user = User.Load();
	}

	

	public static class SessionLog { //Nota: tiene que ser static porque sino colapsa el JsonLoad al quere crear instancias
		public long userID;
		public long id;
		public STATUS status=STATUS.CREADO;
		public long idEnvio;

		public SessionLog() {
			this.id = TimeUtils.millis();
		}
	}
	
}
