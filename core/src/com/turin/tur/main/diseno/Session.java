package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;

public class Session {

	private static final String TAG = Session.class.getName();
	public User user;
	public int numberOfLevels;
	public int nextLevel;
	public JsonSession session;

	public Session() {

		loadUser();
		initSession();
		uploadSession();
		loadLevels();
	}

	private void uploadSession() {
		// Esta rutina intenta subir los datos de la session al servidor
		JsonSessionHistory jsonHistory = JsonSessionHistory.Load();
		Internet.PUT(jsonHistory);
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
		this.session = new JsonSession();
		this.session.userID = this.user.id;
		JsonSessionHistory jsonHistory = JsonSessionHistory.Load();
		jsonHistory.history.add(this.session);
		jsonHistory.save();
	}

	private void loadUser() {
		// Chequea si el usuario ya existe o si es la primera vez
		if (!Gdx.files.local(Constants.USERFILE).exists()) {
			User.CreateUser();
			Gdx.app.debug(TAG, "Creando nuevo usuario");
		}
		this.user = User.Load();
	}

	public static class JsonSessionHistory extends Internet.Enviable{

		public static String path = "config/" + Constants.version() + "/sessionHistory.info";
		public static String pathUploaded = path + ".uploaded";
		public Array<JsonSession> history = new Array<JsonSession>();

		public static JsonSessionHistory Load() {
			String savedData = FileHelper.readLocalFile(path);
			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(JsonSessionHistory.class, savedData);
			} else {
				Gdx.app.error(TAG, "No se a podido encontrar la info del historial de sesiones");
			}
			return new JsonSessionHistory();
		}
		
		public static JsonSessionHistory LoadUploaded() {
			String savedData = FileHelper.readLocalFile(pathUploaded);
			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(JsonSessionHistory.class, savedData);
			} else {
				Gdx.app.error(TAG, "No se a podido encontrar la info del historial de sesiones uploaded");
			}
			return new JsonSessionHistory();
		}

		public void save() {
			Json json = new Json();
			FileHelper.writeFile(JsonSessionHistory.path, json.toJson(this));
		}
		public void saveUploaded() {
			Json json = new Json();
			FileHelper.writeFile(JsonSessionHistory.pathUploaded, json.toJson(this));
		}

		@Override
		public void enviado() {
			Gdx.app.debug(TAG, "Historial enviado");
			// Crea un nuevo json con la info que esta en saved, le agrega la que esta en no saved y despues guarda saved y limpia no saved
			JsonSessionHistory jsonHistorySaved = JsonSessionHistory.LoadUploaded();
			jsonHistorySaved.history.addAll(this.history);
			jsonHistorySaved.saveUploaded();
			this.history.clear();
			this.save();
		}

		@Override
		public void noEnviado() {
			Gdx.app.debug(TAG, "Historial no enviado correctamente");
		}
	}

	public static class JsonSession {
		public long userID;
		public long time;

		public JsonSession() {
			this.time = TimeUtils.millis();
		}
	}
}
