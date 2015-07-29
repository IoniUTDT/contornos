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
		initSessionLog();
		JsonSessionLog.upload();
		uploadSessionLog();
		countLevels();
	}

	private void uploadSessionLog() {
		// Esta rutina intenta subir los datos de la session al servidor
		JsonSessionLog jsonHistory = JsonSessionLog.Load();
		Internet.PUT(jsonHistory);
	}

	private void countLevels() {
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

	private void initSessionLog() {
		// Crea la session
		this.session = new JsonSession();
		this.session.userID = this.user.id;
		this.session.userName = this.user.name;
		JsonSessionLog jsonHistory = JsonSessionLog.Load();
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

	public static class JsonSessionLog extends Internet.Enviable{

		public Array<JsonSession> history = new Array<JsonSession>();

		public static JsonSessionLog Load() {
			String savedData = FileHelper.readLocalFile(path);
			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(JsonSessionLog.class, savedData);
			} else {
				Gdx.app.error(TAG, "No se a podido encontrar la info del historial de sesiones");
			}
			return new JsonSessionLog();
		}
		
		public static JsonSessionLog LoadUploaded() {
			String savedData = FileHelper.readLocalFile(pathUploaded);
			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(JsonSessionLog.class, savedData);
			} else {
				Gdx.app.error(TAG, "No se a podido encontrar la info del historial de sesiones uploaded");
			}
			return new JsonSessionLog();
		}

		public void save() {
			Json json = new Json();
			FileHelper.writeFile(JsonSessionLog.path, json.toJson(this));
		}
		public void saveUploaded() {
			Json json = new Json();
			FileHelper.writeFile(JsonSessionLog.pathUploaded, json.toJson(this));
		}

		@Override
		public void enviado() {
			Gdx.app.debug(TAG, "Historial enviado");
			// Crea un nuevo json con la info que esta en saved, le agrega la que esta en no saved y despues guarda saved y limpia no saved
			JsonSessionLog jsonHistorySaved = JsonSessionLog.LoadUploaded();
			jsonHistorySaved.history.addAll(this.history);
			jsonHistorySaved.saveUploaded();
			this.history.clear();
			this.save();
		}

		@Override
		public void noEnviado() {
			Gdx.app.debug(TAG, "Historial no enviado correctamente");
		}

		@Override
		public void enviar() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void load() {
			// TODO Auto-generated method stub
		}

		@Override
		public String path() {
			return "config/" + Constants.version() + "/sessionHistory.info";
		}

		@Override
		public Object getObject() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Class getMyclass() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static class JsonSession {
		public long userID;
		public long time;
		public String userName;

		public JsonSession() {
			this.time = TimeUtils.millis();
		}
	}
	
}
