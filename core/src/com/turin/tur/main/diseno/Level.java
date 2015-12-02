package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Enviables.STATUS;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeLEVEL;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.builder.ResourcesMaker.InfoConcelptualExpSensibilidad;


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
	public JsonLevel jsonLevel;

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
		this.jsonLevel = loadLevel(level);
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
		this.levelLog.levelTitle = this.jsonLevel.levelTitle;
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
			json.setUsePrototypes(false);
			return json.fromJson(JsonLevel.class, savedData);
		}
		Gdx.app.error(TAG, "No se a podido encontrar la info del nivel " + level);
		return null;
	}


	public static class JsonLevel {
		public String appVersion; // Identifica que version de la aplicacion se esta construyendo.
		public String levelTitle;
		public int levelVersion;
		public int Id; // Id q identifica al level
		public Array<Integer> trials = new Array<Integer>(); // Lista de ids de los trial que incluye el nivel
		public Array<JsonTrial> jsonTrials = new Array<JsonTrial>(); // Este se usa solamente en el proceso de creacion de niveles (pero por como esta dise�ado el codigo que graba y carga el json completo se guarda   
		public int resourceVersion;
		public boolean randomTrialSort;
		public boolean show;
		public Array<Significancia> significancias = new Array<Significancia>();
		public int aciertosTotales; // Esto guarda el numero de aciertos totales. Deberia servir como info gneneral en todos los trials de test y de entrenamiento
		public int aciertosPorCategorias; // Esto guarda el numero de aciertos en trials por categoria. Al generar el level hay que inlcuir un numero de aciertos que vuelve significativo el resultado y comparar con eso.
		public int aciertosPorImagenes; // Esto guarda el numero de aciertos en trials por imagenes. Al generar el level hay que incluir el numero de aciertos que vuelve significativo el resultado
		// Informacion relacionada al procesamiento en tiempo real.
		public TIPOdeLEVEL tipoDeLevel = TIPOdeLEVEL.UMBRAL;
		public AnalisisUmbral analisisUmbral = new AnalisisUmbral();
		
		
		public static void CreateLevel(JsonLevel jsonLevel, String path) {
			Json json = new Json();
			json.setUsePrototypes(false);
			FileHelper.writeFile(path + "level" + jsonLevel.Id + ".meta", json.toJson(jsonLevel));
		}

		public void build(String path) {
			for (JsonTrial jsonTrial : this.jsonTrials) {
				this.trials.add(jsonTrial.Id);
				JsonTrial.CreateTrial(jsonTrial, path);
			}
			JsonLevel.CreateLevel(this, path);
		}
	}

	public void saveLevel(int level, Array<Integer> secuenciaTrials, String levelTitle) {
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.Id = level;
		jsonLevel.trials = secuenciaTrials;
		jsonLevel.levelTitle = levelTitle;
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile("experimentalsource/" + Constants.version() + "/level" + level + ".meta", json.toJson(jsonLevel));
	}

	public static class LevelLog {
		// Info del envio
		public STATUS status=STATUS.CREADO;
		public long idEnvio;
		
		public String levelTitle;
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
	
	/*
	 * Esta clase sirve para cargar info de analisis de significancias en la info de levels. La idea es que se cree la info complicada de calcular cuando se crea el nivel (y se puede hacer calculos largos y con instrucciones que no son LibGDX) y que luego se verifique el desempeño durante el juego
	 */
	public static class Significancia {
		public TIPOdeSIGNIFICANCIA tipo; // Categoriza el tipo de significancia
		public Float[] distribucion; // Distribucion de probabilidad del conjunto seleccionado
		public Integer[] trialIncluidos; // Trials que se consideraron para este test de significancia.
		public int exitoMinimo; // Numero de trials que deben ser bien respondidos 
		public int[] histogramaTrials; //Histograma de numero de preguntas
	}
	
	public enum TIPOdeSIGNIFICANCIA {
		COMPLETO("Evaluacion Global","Informacion de la significancia total del nivel considerando al ciego como una respuesta random",0.05f),
		IMAGEN("Evaluacion de selección de imagenes","Informacion de la significancia total del nivel considerando al ciego como una respuesta random solo en los trials que se debe seleccionar entre imagenes",0.05f),
		CATEGORIA("Evaluacion de selección de imagenes","Informacion de la significancia total del nivel considerando al ciego como una respuesta random solo en los trials que se debe seleccionar entre imagenes",0.05f);
		
		public String title;
		public String description;
		public float pValue;
		
		TIPOdeSIGNIFICANCIA(String title, String descripcion, float pValue) {
			this.description=descripcion;
			this.title=title;
			this.pValue=pValue;
		}
	}
	
	public static class AnalisisUmbral {
		public static class DetectionObject {
			public boolean answerTrue;
			public InfoConcelptualExpSensibilidad infoConceptual;
		}
		
		public float anguloReferencia;
		public int indiceAnguloRefrencia;
		public int cantidadDeNivelesDeDificultad;
		public float trueRate; // Nivel de aciertos de deteccion de señal que se quiere medir. Sirve para el setup experimental de umbral
		public Array<DetectionObject> historialAciertosCurvaSuperior = new Array<DetectionObject>();
		public int saltoCurvaSuperior;
		public int proximoNivelCurvaSuperior;  
	}
}
