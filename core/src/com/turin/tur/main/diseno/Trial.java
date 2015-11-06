package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Boxes.AnswerBox;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.Boxes.StimuliBox;
import com.turin.tur.main.diseno.Boxes.TrainingBox;
import com.turin.tur.main.diseno.Enviables.STATUS;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.builder.Builder;
import com.turin.tur.main.util.builder.LevelMaker.Dificultad;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

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
	public boolean alreadySelected = false; // indica si ya se elecciono algo o no
	
	// Variables que llevan el registro
	public TrialLog log;
	public RunningSound runningSound;
	
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
		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.EJEMPLOS) {
			for (ExperimentalObject elemento : this.elementos) {
				TrainingBox box = new TrainingBox(elemento);
				box.SetPosition(jsonTrial.distribucion.X(orden.get(this.elementos.indexOf(elemento, true))),
						jsonTrial.distribucion.Y(orden.get(this.elementos.indexOf(elemento, true))));
				this.trainigBoxes.add(box);
			}
		}
		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.TEST){
			for (ExperimentalObject elemento : this.elementos) {
				AnswerBox box = new AnswerBox(elemento,this.jsonTrial.feedback);
				box.SetPosition(jsonTrial.distribucion.X(orden.get(this.elementos.indexOf(elemento, true))) + Constants.Box.SHIFT_MODO_SELECCIONAR,
						jsonTrial.distribucion.Y(orden.get(this.elementos.indexOf(elemento, true))));
				this.answerBoxes.add(box);
			}
			stimuliBox = new StimuliBox(rtaCorrecta);
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
		
		boolean rtaEntreOpciones = false;
		for (int i: this.jsonTrial.elementosId) {
			if (this.jsonTrial.rtaCorrectaId == i){
				rtaEntreOpciones = true;
			}
		}
		if ((this.jsonTrial.rtaRandom) && (rtaEntreOpciones)){ // Pone una random solo si esta seteada como random y la rta esta entre las figuras
			this.rtaCorrecta = new ExperimentalObject(this.jsonTrial.elementosId[MathUtils.random(this.jsonTrial.elementosId.length-1)]);
		} else {
			this.rtaCorrecta = new ExperimentalObject(this.jsonTrial.rtaCorrectaId);
		}
		
		if (new ExperimentalObject(this.jsonTrial.rtaCorrectaId).categorias.contains(Categorias.Nada, false)) { // Pone si o si una respuesta random si la rta es nada.
			this.rtaCorrecta = new ExperimentalObject(this.jsonTrial.elementosId[MathUtils.random(this.jsonTrial.elementosId.length-1)]);
		}
		// Crea el log que se carga en el controller
		this.log = new TrialLog();
	}

	public void update(float deltaTime) {
		// Actualiza las boxes
		for (Box box : allBox) {
			box.update(deltaTime, this);
		}
	}

	public boolean checkTrialCompleted() { // Se encarga de ver si ya se completo trial o no
		if (this.jsonTrial.modo == TIPOdeTRIAL.EJEMPLOS) {
			boolean allCheck = true;
			for (TrainingBox box : trainigBoxes) {
				if (box.alreadySelected == false) {
					allCheck = false;
				}
			}
			if (allCheck) {
				trialCompleted = true;
			} else {
				trialCompleted = false;
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
		public boolean feedback=false; // Sirve para configurar que en algunos test no haya feedback
		public boolean randomSort;
		public int resourceVersion;
		public Dificultad dificultad; //= new Dificultad(-1);
		
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

	
	// Seccion de logs
	public static class TouchLog {
		// Todas las cosas se deberian generar al mismo tiempo
		public long touchInstance; // Instancia que identyifica a cada toque
		public long trialInstance; // Instancia q identifica al trial en el cual se toco
		public int trialId; // Id del trial en el que se toco
		public ResourceId idResourceTouched; // Id del recurso que se toco
		public Array<Categorias> categorias = new Array<Categorias>(); // Lista de categorias a las que pertenece el elemento tocado
		public TIPOdeTRIAL tipoDeTrial; // Tipo de trial (entrenamiento test, etc)
		public boolean isTrue; // Indica si se toco el recurso que era la respuesta
		public boolean isStimuli; // indica si el recurso tocado es el estimulo (en general se lo toca para que se reproduzca)
		public float timeSinceTrialStarts; // Tiempo desde que se muestra el trial
		public long soundInstance; // Intancia del ultimo sonido en ejecucion
		public boolean soundRunning; // indica si se esta ejecutando algun sonido
		public float timeLastStartSound; // Tiempo (en el trial) en que comenzo el ultimo sonido 
		public float timeLastStopSound; // Tiempo (en el trial en que terimo el ultimo sonido 
		public int numberOfSoundLoops; // Cantidad de veces que re reprodujo el ultimo sonido
		public Array<Integer> soundIdSecuenceInTrial; // Ids de todos los sonidos que se reprodujeron en el trial
		public long levelInstance; // Registra el level en el que se toco
		public long sessionInstance; // Registra la session en que se toco
		public JsonResourcesMetaData jsonMetaDataTouched; // Guarda la info completa de la meta data del objeto tocado
	}

	public static class SoundLog {
		// Variables que se crean con el evento
		public long soundInstance; // identificador de la instancia de sonido en particular
		public ResourceId soundId; // Id al recurso del cual se escucha el sonido
		public Array<Categorias> categorias = new Array<Categorias>(); // Categorias a las que pertenece el sonido en reproduccion
		public long trialInstance; // instancia del Trial en la que se reproduce el sonido
		public int trialId; // Id del trial en el que se reproduce el sonido
		public boolean fromStimuli; // Indica si el sonido viene de un estimulo o no (sino viene de una caja de entrenamiento)
		public TIPOdeTRIAL tipoDeTrial; // Tipo de trial en el que se reproduce este sonido
		public int numberOfLoop; // Numero de loop que corresponde a la reproduccion de este sonido
		public float startTimeSinceTrial;  // tiempo en que se inicia la reproduccion del sonido desde que comenzo el trial
		public int numberOfSoundInTrial; // Cantidad de sonidos reproducidos previamente
		public Array<Integer> soundSecuenceInTrial; // Listado de sonidos reproducidos
		// Variables que se generan una vez creado el evento
		public float stopTime; // tiempo en que se detiene el sonido 
		public boolean stopByExit; // Indica si se detuvo el sonido porque se inicio alguna secuencia de cierre del trial (porque se completo el trial, el level, etc)
		public boolean stopByUnselect; // Indica si se detuvo el sonido porque el usuario selecciono algo como parte de la dinamica del juego
		public boolean stopByEnd; // Indica si el sonido se detuvo porque se completo la reproduccion prevista (por ahora esta determinada por el tiempo preestablecido de duracion de los sonidos en 5s. No es el tiempo en que de verdad termina el sonido)
		public long sessionInstance; // Indica la instancia de session en que se reproduce este sonido
		public long levelInstance; // Indica la instancia de level en que se reproduce este sonido
	}

	public static class TrialLog {
		// Info del envio
		public STATUS status=STATUS.CREADO;
		public long idEnvio;
		
		// Info de arbol del evento 
		public long sessionId; // Instancia de la session a la que este trial pertence
		public long levelInstance; // Intancia del nivel al que este trial pertenece
		public long trialInstance; // identificador de la instancia de este trial.
		// Info del usuario y del trial
		public int trialId; // Id del trial activo
		public long userId; // Id del usuario activo
		public Array<Categorias> categoriasElementos = new Array<Categorias>(); // Listado de categorias existentes en este trial
		public Array<Categorias> categoriasRta = new Array<Categorias>(); // Listado de categorias a las que pertenece la rta valida / estimulo de este trial si la hay
		public ResourceId idRtaCorrecta; // id del recurso correspondiente a la rta correcta para este trial
		public int indexOfTrialInLevel; // posicion de este trial dentro del nivel
		public int trialsInLevel; // Cantidad total de trials en el nivel activo
		public JsonResourcesMetaData jsonMetaDataRta; // Info de la metadata del estimulo/rta  
		
		public long timeTrialStart; // Marca temporal absoluta de cuando se inicia el trial
		public long timeExitTrial; // Marca temporal absoluta de cuando se sale del trial
		public Array<Integer> resourcesIdSort = new Array<Integer>(); // Ids de los recursos en orden segun se completan en la distribucion. Esto es importante porque el orden puede estar randomizado instancia a instancia
		public DISTRIBUCIONESenPANTALLA distribucionEnPantalla; // Distribucion en pantalla de los recursos
		public TIPOdeTRIAL tipoDeTrial; // Tipo de trial (test, entrnamiento, etc)
		public float version = Constants.VERSION; // Version del programa. Esto es super importante porque de version a version pueden cambiar los recursos (es decir que el mismo id lleve a otro recurso) y tambien otras cosas como la distribucion en pantalla, etc
		public float resourcesVersion = Builder.ResourceVersion; // Version de los recursos generados

		// Informacion de lo que sucede durante la interaccion del usuario

		// public float timeStartTrialInLevel; // Tiempo en que se crea el trial en relacion al nivel  
		public float timeStopTrialInLevel; // Tiempo en que se termina el trial en relacion al nivel
		public float timeInTrial; // tiempo transcurrido dentro del trial
		public boolean trialCompleted; //Por ahora solo se puede completar un trial en modo training. En modo test no tiene sentido completar el nivel. Este dato se carga de cuando sehace el checkTrialCompleted 
		public Array<ResourceId> resourcesIdSelected = new Array<ResourceId>(); // Lista de elementos seleccionados
		public Array<TouchLog> touchLog = new Array<TouchLog>(); // Secuencia de la info detallada de todos los touch
		public Array<SoundLog> soundLog = new Array<SoundLog>(); // Secuencia de la info detallada de todos los sounds
		public boolean trialExitRecorded; // registra que se guardo la informacion de salida del trial. 
		public String trialTitle;
		public JsonTrial jsonTrial; // Json con toda la info que viene del archivo con los datos del trial
		
		public TrialLog() {
			this.trialInstance = TimeUtils.millis();
		}		
	}
	
	public static class ResourceId {
		public int id;
		public int resourceVersion;
	}
	
	public void newLog(Session session, Level levelInfo) {
		// Carga la info general del contexto
		this.log.levelInstance = levelInfo.levelLog.levelInstance;
		this.log.sessionId = session.sessionLog.id;
		this.log.timeTrialStart = TimeUtils.millis();  
		this.log.trialId = this.Id;
		this.log.trialTitle = this.jsonTrial.title;
		this.log.userId = session.sessionLog.userID;
		
		// Agrega las categorias de la rta correcta o estimulo
		if (this.rtaCorrecta!=null) {
			for (Categorias categoria: this.rtaCorrecta.categorias) {
				this.log.categoriasRta.add(categoria);
			}
			// Agrega el json de la rta correcta/estimulo
			this.log.jsonMetaDataRta = JsonResourcesMetaData.Load(this.rtaCorrecta.resourceId.id);
		}
		
		// Agrega las categorias de todas las cajas
		for (Box box: this.allBox) {
			for (Categorias categoria: box.contenido.categorias){
				this.log.categoriasElementos.add(categoria);
			}
		}
		this.log.idRtaCorrecta = this.rtaCorrecta.resourceId;
		this.log.indexOfTrialInLevel = levelInfo.activeTrialPosition;
		this.log.trialsInLevel = levelInfo.secuenciaTrailsId.size;
		// Recupera los Id de los recursos en el orden que estan en pantalla
		for (int orden : this.orden) {
			this.log.resourcesIdSort.add(this.elementos.get(orden).resourceId.id); // Recupera los ids de los recursos segun el orden en que esten
		}
		this.log.distribucionEnPantalla = this.jsonTrial.distribucion;
		this.log.tipoDeTrial = this.jsonTrial.modo;
		this.log.jsonTrial = this.jsonTrial;
		this.log.jsonMetaDataRta = JsonResourcesMetaData.Load(this.rtaCorrecta.resourceId.id);
	}
}
