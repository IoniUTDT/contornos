package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;

public class Level {
	
	// Constantes
	private static final String TAG = Level.class.getName();
	public int Id;
	
	// Cosas que se cargan de archivo
	public Array<Integer> secuenciaTrailsId = new Array<Integer>();
	
	// variable del nivel
	public int activeTrialPosition; // Posicion del trial activo
	public String levelTitle; 
	
	public Level(int level) {
		this.activeTrialPosition=0;
	    Gdx.app.debug(TAG, "Cargando informacion del nivel "+level);
	    this.initlevel(level);
	}
	
	public Level(int level,int activeTrialPosition) {
	    Gdx.app.debug(TAG, "Cargando informacion del nivel "+level);
	    this.activeTrialPosition=activeTrialPosition;
	    this.initlevel(level);
	}

	
	private void initlevel(int level) {
		JsonLevel jsonLevel = loadLevel(level);
		this.Id=jsonLevel.Id;
		this.secuenciaTrailsId = jsonLevel.trials;
		this.levelTitle = jsonLevel.levelTitle;
		this.setActiveTrialId (this.activeTrialPosition);
	}

	private void setActiveTrialId(int activeTrialPosition) {
		if (activeTrialPosition < this.secuenciaTrailsId.size) {
			this.activeTrialPosition = activeTrialPosition;
		} else {
			Gdx.app.error(TAG, "El nivel "+this.Id+" no posee un trial "+activeTrialPosition+". se ha resetado el nivel");
		}
			this.activeTrialPosition=0;
	}

	
	public int IdTrial (int trialPosition){
		int IdTrial;
		if (trialPosition < this.secuenciaTrailsId.size) {
			IdTrial = this.secuenciaTrailsId.get(trialPosition);
		} else {
			Gdx.app.error(TAG,"El nivel "+this.Id+" no posee suficientes trial como para iniciarse en el trial numero "+trialPosition);
			IdTrial = this.secuenciaTrailsId.get(0);
		}
		return IdTrial;
	}
	
	
	
	/*
	 * Aca empieza info accesoria para el load
	 */
	
	private JsonLevel loadLevel(int level) {
		String savedData = FileHelper.readFile("experimentalsource/"+ Constants.version() + "/level" + level + ".meta");
		if (!savedData.isEmpty()) {
			Json json = new Json();
			return json.fromJson(JsonLevel.class, savedData);
		}
		Gdx.app.error(TAG,"No se a podido encontrar la info del nivel " + level);
		return null;
	}


	public static class JsonLevel {
		public String levelTitle;
		public int Id; // Id q identifica al level
		public Array<Integer> trials = new Array<Integer>(); // Lista de ids de los trial que incluye el nivel
		public Array<JsonTrial> jsonTrials = new Array<JsonTrial>(); // Este se usa solamente en el proceso de creacion de niveles (pero por como esta diseñado el codigo que graba y carga el json completo se guarda   
		
		public static void CreateLevel (JsonLevel jsonLevel, String path){
			Json json = new Json();
			FileHelper.writeFile(path+"level"+jsonLevel.Id+".meta", json.toJson(jsonLevel));
		}

		public void build() {
			for (JsonTrial jsonTrial: this.jsonTrials) {
				this.trials.add(jsonTrial.Id);
				JsonTrial.CreateTrial(jsonTrial,"/temp/resourcesbuid/");
			}		
			JsonLevel.CreateLevel(this, "/temp/resourcesbuid/");
		}
	}
	
	public void saveLevel(int level, Array<Integer> secuenciaTrials, String levelTitle) {
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.Id=level;
		jsonLevel.trials=secuenciaTrials;
		jsonLevel.levelTitle=levelTitle;
		Json json = new Json();
		FileHelper.writeFile("experimentalsource/" + Constants.version()+ "/level" + level + ".meta", json.toJson(jsonLevel));
	}	
}
