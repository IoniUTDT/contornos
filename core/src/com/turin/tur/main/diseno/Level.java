package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.ImagesAsset;
import com.turin.tur.main.util.FileHelper;

public class Level {
	
	// Constantes
	private static final String TAG = Level.class.getName();
	public int Id;
	
	// Cosas que se cargan de archivo
	public Array<Trial> secuenciaTrials = new Array<Trial>();
	public int[] secuenciaTrailsId;
	
	// variable del nivel
	public int activeTrialPosition;
	
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
		this.secuenciaTrailsId = jsonLevel.trials;
		this.setActiveTrialId (this.activeTrialPosition);
	}

	private void setActiveTrialId(int activeTrialPosition) {
		if (activeTrialPosition < this.secuenciaTrailsId.length) {
			this.activeTrialPosition = activeTrialPosition;
		} else {
			Gdx.app.error(TAG, "El nivel "+this.Id+" no posee un trial "+activeTrialPosition+". se ha resetado el nivel");
		}
			this.activeTrialPosition=0;
	}

	public int IdTrial (int trialPosition){
		int IdTrial;
		if (trialPosition < this.secuenciaTrailsId.length) {
			IdTrial = this.secuenciaTrailsId[trialPosition];
		} else {
			Gdx.app.error(TAG,"El nivel "+this.Id+" no posee suficientes trial como para iniciarse en el trial numero "+trialPosition);
			IdTrial = this.secuenciaTrailsId[0];
		}
		return IdTrial;
	}

	
	
	/*
	 * Aca empieza info accesoria para el load
	 */
	
	private JsonLevel loadLevel(int level) {
		String savedData = FileHelper.readFile("experimentalconfig/"+ ImagesAsset.instance.version + "/level" + level + ".meta");
		if (!savedData.isEmpty()) {
			Json json = new Json();
			return json.fromJson(JsonLevel.class, savedData);
		}
		Gdx.app.error(TAG,"No se a podido encontrar la info del nivel " + level);
		return null;
	}


	public static class JsonLevel {
		public int Id; // Id q identifica al level
		public int[] trials; // Lista de ids de los trial que incluye el nivel
	}
	
	public void saveLevel(int level, int[] secuenciaTrials) {
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.Id=level;
		jsonLevel.trials=secuenciaTrials;
		Json json = new Json();
		FileHelper.writeFile("experimentalconfig/" + ImagesAsset.instance.version
				+ "/level" + level + ".meta", json.toJson(jsonLevel));
	}	
}
