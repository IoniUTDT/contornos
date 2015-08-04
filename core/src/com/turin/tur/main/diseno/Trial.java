package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Boxes.AnswerBox;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.Boxes.StimuliBox;
import com.turin.tur.main.diseno.Boxes.TrainingBox;
import com.turin.tur.main.diseno.Level.LevelLogHistory;
import com.turin.tur.main.logic.LevelController;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Internet;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Internet.Enviable;

public class Trial {

	public int Id; // Id q identifica al trial

	public JsonTrial jsonTrial;

	// objetos que se cargan en el load o al inicializar
	public Array<ExperimentalObject> elementos = new Array<ExperimentalObject>();
	public ExperimentalObject rtaCorrecta;
	public Level levelActivo;
	public User userActivo;
	public float levelTime = 0;
	public Array<TrainingBox> trainigBoxes = new Array<TrainingBox>();
	public Array<AnswerBox> answerBoxes = new Array<AnswerBox>();
	public StimuliBox stimuliBox;
	public Array<Box> allBox = new Array<Box>();
	public Array<Integer> orden = new Array<Integer>();

	// Variable que tiene que ver con el estado del trial
	public boolean trialCompleted = false;
	// public Sound activeSound;
	// public boolean runningSound = false;
	
	// Variables que llevan el registro
	public TrialLog log;

	// constantes
	public static final String TAG = Trial.class.getName();

	public Trial(int Id) {
		this.Id = Id;
		initTrial(Id);
		createElements();
	}

	private void createElements() {
		// Crea un orden random o no segun corresponda
		for (int i = 0; i < this.jsonTrial.distribucion.distribucion.length; i++) {
			orden.add(i);
		}
		if (this.jsonTrial.randomSort) {
			orden.shuffle();
		}

		// Crea las cajas segun corresponda a su tipo
		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.ENTRENAMIENTO) {
			for (ExperimentalObject elemento : this.elementos) {
				TrainingBox box = new TrainingBox(elemento, this);
				box.SetPosition(jsonTrial.distribucion.X(orden.get(this.elementos.indexOf(elemento, true))),
						jsonTrial.distribucion.Y(orden.get(this.elementos.indexOf(elemento, true))));
				this.trainigBoxes.add(box);
			}
		}

		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.TEST) {
			for (ExperimentalObject elemento : this.elementos) {
				AnswerBox box = new AnswerBox(elemento, this);
				box.SetPosition(jsonTrial.distribucion.X(orden.get(this.elementos.indexOf(elemento, true))) + Constants.Box.SHIFT_MODO_SELECCIONAR,
						jsonTrial.distribucion.Y(orden.get(this.elementos.indexOf(elemento, true))));
				this.answerBoxes.add(box);
			}
			stimuliBox = new StimuliBox(rtaCorrecta, this);
			stimuliBox.SetPosition(0 + Constants.Box.SHIFT_ESTIMULO_MODO_SELECCIONAR, 0);
			allBox.add(stimuliBox);
		}
		// Junta todas las cajas en una unica lista para que funcionen los
		// update, etc.
		for (Box box : answerBoxes) {
			allBox.add(box);
		}
		for (Box box : trainigBoxes) {
			allBox.add(box);
		}
	}

	private void initTrial(int Id) {
		Gdx.app.log(TAG, "Cargando info del trial");
		// Carga la info en bruto
		JsonTrial jsonTrial = JsonTrial.LoadTrial(Id);
		this.jsonTrial = jsonTrial;
		// Carga la info a partir de los Ids
		for (int elemento : this.jsonTrial.elementosId) {
			this.elementos.add(new ExperimentalObject(elemento));
		}
		this.rtaCorrecta = new ExperimentalObject(this.jsonTrial.rtaCorrectaId);
		// Crea el log que se carga en el controller
		this.log = new TrialLog();
		Gdx.app.log(TAG, "Info del trial cargado");
	}

	public void update(float deltaTime) {
		// Actualiza las boxes
		for (Box box : allBox) {
			box.update(deltaTime);
		}
	}

	public boolean checkTrialCompleted() { // Se encarga de ver si ya se
											// completo trial o no
		if (this.jsonTrial.modo == TIPOdeTRIAL.ENTRENAMIENTO) {
			boolean allCheck = true;
			for (TrainingBox box : trainigBoxes) {
				if (box.alreadySelected == false) {
					allCheck = false;
				}
			}
			if (allCheck) {
				trialCompleted = true;
			}
		}
		//Agrega al log el estado de trial
		this.log.trialCompleted=trialCompleted;
		return trialCompleted;
	}

	// Seccion encargada de guardar y cargar info de trials

	// devuelve la info de la metadata

	public static class JsonTrial {
		public String caption; // Texto que se muestra debajo
		public int Id; // Id q identifica al trial
		public String title; // Titulo optativo q describe al trial
		public TIPOdeTRIAL modo; // Tipo de trial
		public int[] elementosId; // Lista de objetos del trial.
		public int rtaCorrectaId; // Respuesta correcta en caso de que sea test.
		public boolean rtaRandom; // Determina si se elije una rta random
		public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones
														// de los elementos a
														// mostrar
		public boolean randomSort;

		public static void CreateTrial(JsonTrial jsonTrial, String path) {
			Json json = new Json();
			FileHelper.writeFile(path + "trial" + jsonTrial.Id + ".meta", json.toJson(jsonTrial));
		}

		private static JsonTrial LoadTrial(int Id) {
			String savedData = FileHelper.readFile("experimentalsource/" + Constants.version() + "/trial" + Id + ".meta");

			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(JsonTrial.class, savedData);
			}
			Gdx.app.error(TAG, "No se a podido encontrar la info del objeto experimental " + Id);
			return null;
		}
	}

	public static class TouchLog {
		// Todas las cosas se deberian generar al mismo tiempo
		public long touchInstance; // Instancia que identyifica a cada toque
		public long trialInstance; // Instancia q identifica al trial en el cual se toco
		public int trialId; // Id del trial en el que se toco
		public int idResourceTouched; // Id del recurso que se toco
		public Array<Categorias> categorias = new Array<Categorias>(); // Lista de categorias a las que pertenece el elemento tocado
		public TIPOdeTRIAL tipoDeTrial; // Tipo de trial (entrenamiento test, etc)
		public boolean isTrue; // Indica si se toco el recurso que era la respuesta
		public boolean isStimuli; // indica si el recurso tocado es el estimulo (en general se lo toca para que se reproduzca)
		public float timeSinceTrialStarts; // Tiempo desde que se muestra el trial
		public long soundInstance; // Intancia del ultimo sonido en ejecucion
		public boolean soundRunning; // indica si se esta ejecutando algun sonido
		public float timeLastStartSound; // Tiempo desde que comenzo el ultimo sonido
		public int numberOfSoundLoops; // Cantidad de veces que re reprodujo el ultimo sonido
		public Array<Integer> soundIdSecuenceInTrial; // Ids de todos los sonidos que se reprodujeron en el trial
	}

	public static class SoundLog {
		// Variables que se crean con el evento
		public long soundInstance; // identificador de la instancia de sonido en particular
		public int soundId; // Id al recurso del cual se escucha el sonido
		public Array<Categorias> categorias = new Array<Categorias>(); // Categorias a las que pertenece el sonido en reproduccion
		public long trialInstance; // instancia del Trial en la que se reproduce el sonido
		public int trialId; // Id del trial en el que se reproduce el sonido
		public boolean fromStimuli; // Indica si el sonido viene de un estimulo o no (sino viene de una caja de entrenamiento)
		public TIPOdeTRIAL tipoDeTrial; // Tipo de trial en el que se reproduce este sonido
		public int numberOfLoop; // Numero de loop que corresponde a la reproduccion de este sonido
		public float startTimeSinceTrial;  // tiempo en que se inicia la reproduccion del sonido desde que comenzo el trial
		public int numberOfSoundInTrial; // Cantidad de sonidos reproducidos previamente 
		// Variables que se generan una vez creado el evento
		public float stopTime; // tiempo en que se detiene el sonido 
		public boolean stopByExit; // Indica si se detuvo el sonido porque se inicio alguna secuencia de cierre del trial (porque se completo el trial, el level, etc)
		public boolean stopByUser; // Indica si se detuvo el sonido porque el usuario selecciono algo como parte de la dinamica del juego
		public boolean stopByEnd; // Indica si el sonido se detuvo porque se completo la reproduccion prevista (por ahora esta determinada por el tiempo preestablecido de duracion de los sonidos en 5s. No es el tiempo en que de verdad termina el sonido)
	}

	public static class TrialLog {
		// Info de arbol del evento 
		public long sessionId; 
		public long levelInstance;
		public long trialInstance; // identificador de la instancia de este trial.
		// Info del usuario y del trial
		public int trialId;
		public long userId;
		public String userName;
		public Array<Categorias> categoriasElementos = new Array<Categorias>();
		public Array<Categorias> categoriasEstimulo = new Array<Categorias>();
		public Array<Categorias> categoriasRta = new Array<Categorias>();
		public int idRtaCorrecta;
		public int indexOfTrialInLevel;
		public int trialsInLevel;
		public Array<Integer> resourcesIdSort = new Array<Integer>();
		public DISTRIBUCIONESenPANTALLA distribucionEnPantalla;
		public TIPOdeTRIAL tipoDeTrial;

		// Informacion de lo que sucede durante la interaccion del usuario
		public float timeStartTrial; //since level start
		public float timeStopTrial; //since level start
		public float timeInTrial;
		public boolean trialCompleted; //Por ahora solo se puede completar un trial en modo training. En modo test no tiene sentido completar el nivel. Este dato se carga de cuando sehace el checkTrialCompleted 
		public Array<Integer> resourcesIdSelected = new Array<Integer>();
		public Array<TouchLog> touchLog = new Array<TouchLog>(); // se crean y cargan en la parte de procesamiento de toque en LevelController
		public Array<SoundLog> soundLog = new Array<SoundLog>();
		
		public TrialLog() {
			this.trialInstance = TimeUtils.millis();
		}		
	}

	public static class TrialLogHistory extends Enviable {

		public static String path = "logs/" + Constants.version() + "/TrialLogHistory.info";
		public static String pathUploaded = path + ".uploaded";
		public Array<TrialLog> history = new Array<TrialLog>();

		public static void append(TrialLog log) {
			// first add the new log to the log history. If there is not log not sent, the list is empty
			TrialLogHistory history = TrialLogHistory.Load(path);
			history.history.add(log);
			history.save(LevelLogHistory.path);
			// then try to send the log
			Internet.PUT(history);
		}

		private void save(String path) {
			Json json = new Json();
			FileHelper.writeFile(path, json.toJson(this));
		}

		private static TrialLogHistory Load(String path) {

			String savedData = FileHelper.readLocalFile(path);
			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(TrialLogHistory.class, savedData);
			}
			return new TrialLogHistory();
		}

		@Override
		public void enviado() {
			// Crea un nuevo json con la info que esta en uploaded, le agrega la que esta en no uploaded y despues guarda uploaded y limpia no uploaded
			TrialLogHistory uploaded = TrialLogHistory.Load(pathUploaded);
			uploaded.history.addAll(this.history);
			uploaded.save(pathUploaded);
			this.history.clear();
			this.save(path);
			Gdx.app.debug(TAG, "Log del trial enviado correctamente");
		}

		@Override
		public void noEnviado() {
			Gdx.app.debug(TAG, "Log del trial no enviado correctamente");
		}

	}

	public void loadLog(Session session, Level levelInfo) {
		// Carga la info general del contexto
		this.log.levelInstance = levelInfo.levelLog.levelInstance;
		this.log.sessionId = session.sessionLog.id;
		this.log.trialId = this.Id;
		this.log.userId = session.sessionLog.userID;
		this.log.userName = session.sessionLog.userName;
		// Agrega las categorias del estimulo
		if (this.stimuliBox !=null) {
			for (Categorias categoria: this.stimuliBox.contenido.categorias){
				this.log.categoriasEstimulo.add(categoria);
			}
		}
		// Agrega las categorias de la rta correcta
		if (this.rtaCorrecta!=null) {
			for (Categorias categoria: this.rtaCorrecta.categorias) {
				this.log.categoriasRta.add(categoria);
			}
		}
		// Agrega las categorias de todas las cajas
		for (Box box: this.allBox) {
			for (Categorias categoria: box.contenido.categorias){
				this.log.categoriasElementos.add(categoria);
			}
		}
		this.log.idRtaCorrecta = this.rtaCorrecta.Id;
		this.log.indexOfTrialInLevel = levelInfo.activeTrialPosition;
		this.log.trialsInLevel = levelInfo.secuenciaTrailsId.size;
		// Recupera los Id de los recursos en el orden que estan en pantalla
		for (int orden : this.orden) {
			this.log.resourcesIdSort.add(this.elementos.get(orden).Id);
		}
		this.log.distribucionEnPantalla = this.jsonTrial.distribucion;
		this.log.tipoDeTrial = this.jsonTrial.modo;
	}
}
