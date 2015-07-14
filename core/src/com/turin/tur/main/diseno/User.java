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
	public Array<Level> levelHistory = new Array<Level>();
	public Array<Trial> trailHistory = new Array<Trial>();
	public int lastLevelCompletedId;
	
	public void save() {
		JsonUser jsonUser = new JsonUser();
		// Tranfiere los datos del usuario al Json
		jsonUser.comments = this.comments;
		jsonUser.name = this.name;
		jsonUser.Id = this.id;
		jsonUser.lastLevelCompletedId = this.lastLevelCompletedId;
		for (Trial trial: this.trailHistory) {
			jsonUser.trailHistoryId.add(trial.Id);
		}
		for (Level level: this.levelHistory) {
			jsonUser.levelHistoryId.add(level.Id);
		}
		// Graba el json
		jsonUser.save();
	}

	static public void CreateUser() {
		User user = LoadNewUser();
		user.save();
	}

	
	private static User LoadNewUser() {
		User user = new User();
		user.name = "Unnamed";
		user.id = GenerateId();
		user.comments = "Usuario generado automaticamente";
		user.lastLevelCompletedId = 0;
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
		user.lastLevelCompletedId = jsonUser.lastLevelCompletedId;
		for (int id: jsonUser.trailHistoryId) {
			user.trailHistory.add(new Trial(id));
		}
		for (int id: jsonUser.levelHistoryId) {
			user.levelHistory.add(new Level(id));
		}
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
		public Array<Integer> trailHistoryId = new Array<Integer>();
		public int lastLevelCompletedId;
		
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
