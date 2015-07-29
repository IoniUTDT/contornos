package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.Internet.Enviable;

public class Level {

	// Constantes
	private static final String TAG = Level.class.getName();
	public int Id;

	// Cosas que se cargan de archivo
	public Array<Integer> secuenciaTrailsId = new Array<Integer>();

	// variable del nivel
	public int activeTrialPosition; // Posicion del trial activo
	public String levelTitle;
	public LevelLog levelLog;

	public Level(int level) {
		this.activeTrialPosition = 0;
		Gdx.app.debug(TAG, "Cargando informacion del nivel " + level);
		this.initlevel(level);
	}

	public Level(int level, int activeTrialPosition) {
		Gdx.app.debug(TAG, "Cargando informacion del nivel " + level);
		this.activeTrialPosition = activeTrialPosition;
		this.initlevel(level);
	}

	private void initlevel(int level) {
		JsonLevel jsonLevel = loadLevel(level);
		this.Id = jsonLevel.Id;
		this.secuenciaTrailsId = jsonLevel.trials;
		this.levelTitle = jsonLevel.levelTitle;
		this.setActiveTrialId(this.activeTrialPosition);
		this.initLog();
	}

	private void initLog() {
		this.levelLog = new LevelLog();
		this.levelLog.timeStarts = TimeUtils.millis();
		this.levelLog.levelId = this.Id;
		this.levelLog.levelInstance = TimeUtils.millis();
		this.levelLog.levelLength = this.secuenciaTrailsId.size;
		this.levelLog.startTrialPosition = this.activeTrialPosition;
		this.levelLog.sortOfTrials = this.secuenciaTrailsId;
		this.levelLog.levelCompleted = false;
	}

	private void setActiveTrialId(int activeTrialPosition) {
		if (activeTrialPosition < this.secuenciaTrailsId.size) {
			this.activeTrialPosition = activeTrialPosition;
		} else {
			Gdx.app.error(TAG, "El nivel " + this.Id + " no posee un trial " + activeTrialPosition + ". se ha resetado el nivel");
		}
		this.activeTrialPosition = 0;
	}

	public int IdTrial(int trialPosition) {
		int IdTrial;
		if (trialPosition < this.secuenciaTrailsId.size) {
			IdTrial = this.secuenciaTrailsId.get(trialPosition);
		} else {
			Gdx.app.error(TAG, "El nivel " + this.Id + " no posee suficientes trial como para iniciarse en el trial numero " + trialPosition);
			IdTrial = this.secuenciaTrailsId.get(0);
		}
		return IdTrial;
	}

	/*
	 * Aca empieza info accesoria para el load
	 */

	private JsonLevel loadLevel(int level) {
		String savedData = FileHelper.readFile("experimentalsource/" + Constants.version() + "/level" + level + ".meta");
		if (!savedData.isEmpty()) {
			Json json = new Json();
			return json.fromJson(JsonLevel.class, savedData);
		}
		Gdx.app.error(TAG, "No se a podido encontrar la info del nivel " + level);
		return null;
	}

	public static class JsonLevel {
		public String levelTitle;
		public int Id; // Id q identifica al level
		public Array<Integer> trials = new Array<Integer>(); // Lista de ids de los trial que incluye el nivel
		public Array<JsonTrial> jsonTrials = new Array<JsonTrial>(); // Este se usa solamente en el proceso de creacion de niveles (pero por como esta diseñado el codigo que graba y carga el json completo se guarda   

		public static void CreateLevel(JsonLevel jsonLevel, String path) {
			Json json = new Json();
			FileHelper.writeFile(path + "level" + jsonLevel.Id + ".meta", json.toJson(jsonLevel));
		}

		public void build() {
			for (JsonTrial jsonTrial : this.jsonTrials) {
				this.trials.add(jsonTrial.Id);
				JsonTrial.CreateTrial(jsonTrial, "/temp/resourcesbuid/");
			}
			JsonLevel.CreateLevel(this, "/temp/resourcesbuid/");
		}
	}

	public void saveLevel(int level, Array<Integer> secuenciaTrials, String levelTitle) {
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.Id = level;
		jsonLevel.trials = secuenciaTrials;
		jsonLevel.levelTitle = levelTitle;
		Json json = new Json();
		FileHelper.writeFile("experimentalsource/" + Constants.version() + "/level" + level + ".meta", json.toJson(jsonLevel));
	}

	public static class LevelLog {

		public long sessionId;
		public long levelInstance;
		public int levelId;
		public long idUser;
		public long timeStarts;
		public long timeExit;
		public boolean levelCompleted;
		public int exitTrialId;
		public int exitTrialPosition;
		public int levelLength;
		public int startTrialPosition;
		public Array<Integer> sortOfTrials;
		public Array<Integer> trialsVisited = new Array<Integer>();

	}

	public static class LevelLogHistory extends Enviable {

		public static String path = "logs/" + Constants.version() + "/LevelLogHistory.info";
		public static String pathUploaded = path + ".uploaded";
		public Array<LevelLog> history = new Array<LevelLog>();

		public static void append(LevelLog log) {
			// first add the new log to the log history. If there is not log not sent, the list is empty
			LevelLogHistory history = LevelLogHistory.Load(path);
			history.history.add(log);
			history.save(LevelLogHistory.path);
			// then try to send the log
			Internet.PUT(history);
		}

		private void save(String path) {
			Json json = new Json();
			FileHelper.writeFile(path, json.toJson(this));
		}

		private static LevelLogHistory Load(String path) {

			String savedData = FileHelper.readLocalFile(path);
			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(LevelLogHistory.class, savedData);
			}
			return new LevelLogHistory();

		}

		@Override
		public void enviado() {
			// Crea un nuevo json con la info que esta en uploaded, le agrega la que esta en no uploaded y despues guarda uploaded y limpia no uploaded
			LevelLogHistory uploaded = LevelLogHistory.Load(pathUploaded);
			uploaded.history.addAll(this.history);
			uploaded.save(pathUploaded);
			this.history.clear();
			this.save(path);
		}

		@Override
		public void noEnviado() {
			Gdx.app.debug(TAG, "Log del level no enviado correctamente");
		}
	}
}
