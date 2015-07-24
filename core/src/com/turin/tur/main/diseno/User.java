package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;




public class User {

	private static final String TAG = User.class.getName();
	
	public String name;
	public long id;
	public String comments;
	public Array<Integer> levelHistory = new Array<Integer>();
	public Array<Integer> trialHistory = new Array<Integer>();
	public Array<Integer> levelsCompleted = new Array<Integer>();
	// public int lastLevelCompletedId;
	
	public void save() {
		JsonUser jsonUser = new JsonUser();
		// Tranfiere los datos del usuario al Json
		jsonUser.comments = this.comments;
		jsonUser.name = this.name;
		jsonUser.Id = this.id;
		jsonUser.levelsCompleted = this.levelsCompleted;
		jsonUser.levelHistoryId = this.levelHistory;
		jsonUser.trialHistoryId = this.trialHistory;
		// Graba el json
		jsonUser.save();
	}

	static public void CreateUser() {
		User user = LoadNewUser();
		CreateUserLog();
		user.save();
	}

	
	private static void CreateUserLog() {
		String encabezado = "Este archivo guarda la info de todo lo que va haciendo el usuario" + ".\r\n";
		FileHelper.writeFile(Constants.USERLOG, encabezado);
	}

	private static User LoadNewUser() {
		User user = new User();
		user.name = "Unnamed";
		user.id = GenerateId();
		user.comments = "Usuario generado automaticamente";
		user.levelsCompleted = new Array<Integer>();
		return user;
	}

	public static User Load() {
		User user = new User();
		JsonUser jsonUser = new JsonUser();
		jsonUser = JsonUser.load();
		// Transpasa los datos
		user.comments = jsonUser.comments;
		user.name = jsonUser.name;
		user.id = jsonUser.Id;
		user.levelsCompleted = jsonUser.levelsCompleted;
		user.levelHistory = jsonUser.levelHistoryId;
		user.trialHistory = jsonUser.trialHistoryId;
		return user;
	}
	
	private static long GenerateId() {
		long Id = TimeUtils.millis();
		return Id;
	}

	public static class JsonUser {
		public String name;
		public long Id;
		public String comments;
		public Array<Integer> levelHistoryId = new Array<Integer>();
		public Array<Integer> trialHistoryId = new Array<Integer>();
		public Array<Integer> levelsCompleted = new Array<Integer>();
		
		public void save(){
			Json json = new Json();
			FileHelper.writeFile(Constants.USERFILE, json.toJson(this));
		}
		
		public static JsonUser load(){
			String savedData = FileHelper.readLocalFile(Constants.USERFILE);
			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(JsonUser.class, savedData);
			} else { Gdx.app.error(TAG,"No se a podido encontrar la info del usuario"); }
			return null;
		}
	}
}
