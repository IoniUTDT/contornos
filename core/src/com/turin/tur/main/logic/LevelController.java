package com.turin.tur.main.logic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Boxes.AnswerBox;
import com.turin.tur.main.diseno.Boxes.StimuliBox;
import com.turin.tur.main.diseno.Boxes.TrainingBox;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.LevelInterfaz;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.TouchInfo;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.LevelInterfaz.Botones;
import com.turin.tur.main.diseno.Trial.ResourceId;
import com.turin.tur.main.diseno.Trial.SoundLog;
import com.turin.tur.main.diseno.Trial.TouchLog;
import com.turin.tur.main.diseno.Trial.TrialLogHistory;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.CameraHelper;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class LevelController implements InputProcessor {

	// Constantes generales del la interfaz
	private final boolean autoChangeTrial = true;

	// Cosas relacionadas con la interfaz grafica
	public OrthographicCamera camera;
	public static final String TAG = LevelController.class.getName();
	public CameraHelper cameraHelper;

	// Cosas relacionadas con los elementos del juego
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	public Array<TouchInfo> completeTouchSecuence = new Array<TouchInfo>();
	public static Trial trial;
	public LevelInterfaz levelInterfaz;
	private Game game;
	private Level levelInfo; //Informacion del nivel cargado

	// Variables que manejan la dinamica de flujo de informacion en el control del nivel
	public boolean nextTrialPending = false; // Genera la señal de que hay que cambiar de trial (para esperar a que finalicen cuestiones de animacion) 
	public static float timeInLevel = 0; // Tiempo general dentro del nivel.
	public static float timeInTrial = 0; // Tiempo desde que se inicalizo el ultimo trial.
	boolean elementoSeleccionado = false; // Sin seleccion
	public Session session;

	public class RunningSound {
		public ExperimentalObject contenido; // Todo el objeto que se esta reproduciendo
		public Sound sound; // Elemento de sonido
		public boolean running = false; // Si se esta reproduciendo o no
		public float start = -1; // Cuando comienza la reproduccion del ultimo sonido. Un "-1" equivale a no tener datos.
		public float ends = -1; // Cuando termina la reproduccion del ultimo sonido. Un "-1" equivale a no tener datos.
		public int id; // El id que identifica el recurso del ultimo sonido
		public int loopsNumber; // Numero de veces que re reproduce el mismo sonido en forma seguida 
		public long instance; // instancia que identifica cada reproduccion unequivocamente
		public Array<Integer> secuenceId = new Array<Integer>(); // secuencia de los sonidos reproducidos.
		public SoundLog soundLog = new SoundLog();
		public String stopReason = "";

		public void play(ExperimentalObject contenidoP) {
			// Primer detiene cualquier reproduccion previa 
			if (running) {
				stop();
				stopReason ="inicio";
			}
			// Crea un log nuevo
			soundLog = new SoundLog();
			
			// Prepara la info en la clase
			contenido = contenidoP;
			sound = contenido.sonido;
			running = true;
			start = timeInTrial;
			ends = -1;
			id = contenido.resourceId.id;
			if (secuenceId.size > 0) {
				if (secuenceId.peek() == id) {
					loopsNumber++;
				} else {
					loopsNumber = 1;
				}
			} else {
				loopsNumber = 1;
			}
			secuenceId.add(id);
			instance = TimeUtils.millis();

			// Crea el log
			soundLog.soundInstance = instance;
			soundLog.soundId = contenido.resourceId;
			for (Categorias categoria : contenido.categorias) {
				soundLog.categorias.add(categoria);
			}
			soundLog.trialInstance = trial.log.trialInstance;
			soundLog.trialId = trial.Id;
			if (trial.jsonTrial.modo == TIPOdeTRIAL.TEST) {
				if (trial.stimuliBox.contenido == contenido) {
					soundLog.fromStimuli = true;

				} else {
					soundLog.fromStimuli = false;
				}
			} else {
				soundLog.fromStimuli = false;
			}
			soundLog.tipoDeTrial = trial.jsonTrial.modo;
			soundLog.numberOfLoop = loopsNumber;
			soundLog.startTimeSinceTrial = timeInTrial;
			soundLog.numberOfSoundInTrial = secuenceId.size;
			soundLog.soundSecuenceInTrial = new Array<Integer>(secuenceId);

			// Espera a q se cargue el recurso (no se porque esto funciona pero lo saque de internet)
			long id;
			while ((id = sound.play(0)) == -1) {
				long t = TimeUtils.nanoTime();
				while (TimeUtils.nanoTime() - t < 50000000)
					;
			}
			sound.play();
		}

		
		public void stop() {
			if (running) {
				
				ends = timeInTrial;
				// Completa el log y lo agrega a la lista
				soundLog.stopTime = timeInTrial;
				System.out.println(stopReason);
				soundLog.stopByUnselect = (stopReason=="unselect");
				soundLog.stopByExit = (stopReason=="exit");
				soundLog.stopByEnd = (stopReason=="end");
				stopReason = "";
				trial.log.soundLog.add(soundLog);
				// Detiene el sonido
				sound.stop();
				running = false;
			}
		}
	}

	public LevelController(Game game, int levelNumber, int trialNumber, Session session) {
		this.game = game;
		this.session = session;
		this.levelInfo = new Level(levelNumber);
		this.levelInfo.levelLog.sessionId = this.session.sessionLog.id;
		this.levelInfo.levelLog.idUser = this.session.user.id;
		this.levelInfo.levelLog.userName = this.session.user.name;
		this.initCamera();
		this.initTrial();
	}

	private void initTrial() {
		trial = new Trial(this.levelInfo.IdTrial(this.levelInfo.activeTrialPosition));
		trial.runningSound = new RunningSound();
		
		// Carga la info general del trial al log
		trial.newLog(this.session, this.levelInfo);
		trial.log.timeStartTrialInLevel = timeInLevel;

		// Carga la interfaz
		this.levelInterfaz = new LevelInterfaz(this.levelInfo, this.levelInfo.activeTrialPosition, trial);
		timeInTrial = 0;
		this.nextTrialPending = false;

		// Registra el evento de la creacion del trial
		this.levelInfo.levelLog.trialsVisited.add(trial.Id);

	}

	private void initCamera() {
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		cameraHelper = new CameraHelper();
	}

	public void update(float deltaTime) {

		// Actualiza el trial
		trial.update(deltaTime);

		// actualiza cosas generales
		cameraHelper.update(deltaTime);
		timeInLevel = timeInLevel + deltaTime;
		timeInTrial = timeInTrial + deltaTime;

		// Procesa cambios de trial si los hay pendientes
		if ((trial.trialCompleted) & (autoChangeTrial)) {
			this.nextTrialPending = true;
		}
		this.changeTrial();
	}

	private void changeTrial() {
		if (this.nextTrialPending) {
			boolean wait = false;
			for (AnswerBox box : trial.answerBoxes) {
				if (box.answerActive) {
					wait = true;
				}
			}
			for (TrainingBox box : trial.trainigBoxes) {
				if (box.runningSound) {
					wait = true;
				}
			}
			if (!wait) {
				this.nextTrialPending = false;
				// Agrega al log que termino el trial
				trial.log.timeStopTrialInLevel = timeInLevel;
				trial.log.timeInTrial = timeInTrial;
				if (isLastTrial()) {
					completeLevel();
				} else {
					goToNextTrial();
				}
			}
		}

	}

	private void completeLevel() {
		// Indica y guarda en la info del usuario que completo este nivel
		this.session.user.levelsCompleted.add(levelInfo.Id);
		this.session.user.save();
		// Indica en el log del level que se completo
		this.levelInfo.levelLog.levelCompleted = true;
		this.backToMenu();
	}

	private boolean isLastTrial() {
		if (levelInfo.activeTrialPosition + 1 == levelInfo.secuenciaTrailsId.size) {
			return true;
		}
		return false;
	}

	private void goToNextTrial() {
		this.exitTrial();
		this.levelInfo.activeTrialPosition += 1;
		this.initTrial();
	}

	private void goToPreviousTrial() {
		this.exitTrial();
		this.levelInfo.activeTrialPosition -= 1;
		this.initTrial();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// Back to Menu
		if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			backToMenu();
		}
		return false;
	}

	private void backToMenu() {
		this.exitTrial();
		// Registra que se sale al menu principal en los logs (falta agregar el log dentro del trial)
		this.levelInfo.levelLog.exitTrialId = trial.Id;
		this.levelInfo.levelLog.exitTrialPosition = this.levelInfo.activeTrialPosition;
		this.levelInfo.levelLog.timeExit = TimeUtils.millis();

		// Guarda en el registro general de log de levels el log de esta instanciacion del nivel e intenta enviarlo por interner.
		Level.LevelLogHistory.append(this.levelInfo.levelLog);

		// continua con la logica del programa
		trial.runningSound.stopReason="exit";
		trial.runningSound.stop();
		game.setScreen(new MenuScreen(game, this.session));
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// Crea un evento de toque
		TouchInfo touch = new TouchInfo();
		// calcula el toque en pantalla
		touch.coordScreen = new Vector3(screenX, screenY, 0);
		// calcula el toque en el juego 
		touch.coordGame = camera.unproject(touch.coordScreen.cpy()); // PREGUNTA: si no le pongo el copy, toma como el mismo vector y sobreescribe el coordScreen. RARO
		// procesa la info del toque en funcion de otros elementos del juego
		procesarToque(touch);
		// agrega el toque a la secuencia de toques acumulados
		touchSecuence.add(touch);
		return false;
	}

	private void procesarToque(TouchInfo touchData) {

		elementoSeleccionado = false; // Sin seleccion
		// Carga el ultimo elemento seleccionado
		if (touchSecuence.size > 0) {
			touchData.lastTouchBox = touchSecuence.peek().thisTouchBox;
		}

		// se fija si se toco alguna imagen training
		for (Box box : trial.trainigBoxes) {
			if (box.spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)) {
				cargarInfoDelTouch(box, touchData);
				elementoSeleccionado = true;
			}
		}

		// se fija si se toco alguna imagen answer
		for (Box box : trial.answerBoxes) {
			if (box.spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)) {
				cargarInfoDelTouch(box, touchData);
				elementoSeleccionado = true;
			}
		}

		// Actua si no se toco nada
		if (!elementoSeleccionado) {
			touchData.elementTouched = false;
		}

		// genera los eventos correspondientes al toque

		// anula la seleccion del evento previo
		if (touchData.lastTouchBox != null) {
			trial.runningSound.stopReason = "unselect";
			touchData.lastTouchBox.unSelect();
		}

		// Actua si hay elemento tocado
		if (touchData.elementTouched) {
			// Se fija si es la respuesta correcta y actua en consecuencia
			this.verifyAnswer(touchData);
			// Crea el log del toque
			this.logTouch(touchData);
			// Activa el elemento tocado
			touchData.thisTouchBox.select(touchData);
			// Se fija si se completo el nivel
			trial.checkTrialCompleted();
		}

		// Se fija si se toco algun elemento de la interfaz del nivel
		if (trial.trialCompleted) {
			for (Botones boton : this.levelInterfaz.botones) {
				if (boton.imagen.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)) {
					Gdx.app.debug(TAG, "Ha tocado el boton " + boton.getClass().getName());
					if (boton.getClass() == LevelInterfaz.BotonSiguiente.class) {
						this.goToNextTrial();
					}
					if (boton.getClass() == LevelInterfaz.BotonAnterior.class) {
						this.goToPreviousTrial();
					}
				}
			}
		}

	}

	private void logTouch(TouchInfo touchData) {
		// Agrega al log el elemento tocado
		trial.log.resourcesIdSelected.add(touchData.thisTouchBox.contenido.resourceId);
		// Agrega la info que corresponda al log creando un TouchLog nuevo
		TouchLog touchLog = new TouchLog();
		touchLog.touchInstance = TimeUtils.millis();
		touchLog.trialInstance = trial.log.trialInstance;
		touchLog.trialId = trial.log.trialId;
		touchLog.idResourceTouched = touchData.thisTouchBox.contenido.resourceId;
		for (Categorias categoria : touchData.thisTouchBox.contenido.categorias) {
			touchLog.categorias.add(categoria);
		}
		touchLog.tipoDeTrial = trial.jsonTrial.modo;
		touchLog.isTrue = touchData.thisTouchBox.answer;
		if (touchData.thisTouchBox.getClass() == StimuliBox.class) {
			touchLog.isStimuli = true;
		} else {
			touchLog.isStimuli = false;
		}
		touchLog.timeSinceTrialStarts = timeInTrial;
		// Carga la info relacionada al sonido que esta en ejecucion
		touchLog.soundInstance = trial.runningSound.instance;
		touchLog.soundRunning = trial.runningSound.running;
		touchLog.timeLastStartSound = trial.runningSound.start;
		touchLog.timeLastStopSound = trial.runningSound.ends;
		touchLog.numberOfSoundLoops = trial.runningSound.loopsNumber;
		touchLog.soundIdSecuenceInTrial = new Array<Integer>(trial.runningSound.secuenceId);
		trial.log.touchLog.add(touchLog);
	}

	private void verifyAnswer(TouchInfo touchData) {
		// revisa si se acerto a la respuesta o no en caso de ser un test trial. 
		if (trial.jsonTrial.modo == TIPOdeTRIAL.TEST) {
			Boolean correcta = false;
			if (trial.rtaCorrecta.resourceId.id == touchData.thisTouchBox.contenido.resourceId.id) {
				correcta = true;
			} // Significa que se toco la respuesta igual a la correcta
			if (touchData.thisTouchBox.contenido.categorias.contains(Categorias.Texto, true)) { // Significa q se selecciono un texto
				for (Categorias categoriaDelObjetoTocado : touchData.thisTouchBox.contenido.categorias) {
					if (trial.rtaCorrecta.categorias.contains(categoriaDelObjetoTocado, true)) { // Significa que la respuesta correcta incluye alguna categoria del boton tocado. Se supone que los botones tocados solo tienen categorias texto y la que corresponda
						correcta = true;
					}
				}
			}
			if (correcta) { // Significa q se selecciono la opcion correcta
				touchData.thisTouchBox.answer = true;
				this.nextTrialPending = true;
			}
		}

	}

	private void exitTrial() {
		TrialLogHistory.append(trial.log);
		trial.runningSound.stopReason="exit";
		trial.runningSound.stop();
	}

	private void cargarInfoDelTouch(Box box, TouchInfo thisTouch) {
		// carga la info en el touch
		thisTouch.elementTouched = true;
		thisTouch.thisTouchBox = box;
		thisTouch.experimentalObjectTouch = box.contenido;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
