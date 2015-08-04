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
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.LevelInterfaz;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.TouchInfo;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.LevelInterfaz.Botones;
import com.turin.tur.main.diseno.Trial.SoundLog;
import com.turin.tur.main.diseno.Trial.TouchLog;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.CameraHelper;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class LevelController implements InputProcessor {

	// Constantes generales del la interfaz
	private static final boolean autoChangeTrial = true;

	// Cosas relacionadas con la interfaz grafica
	public OrthographicCamera camera;
	public static final String TAG = LevelController.class.getName();
	public CameraHelper cameraHelper;

	// Cosas relacionadas con los elementos del juego
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	public Array<TouchInfo> completeTouchSecuence = new Array<TouchInfo>();
	public Trial trial;
	public LevelInterfaz levelInterfaz;
	private Game game;
	private Level levelInfo; //Informacion del nivel cargado

	// Variables que manejan la dinamica de flujo de informacion en el control del nivel
	public boolean nextTrialPending = false; // Genera la señal de que hay que cambiar de trial (para esperar a que finalicen cuestiones de animacion) 
	public float timeInLevel = 0; // Tiempo general dentro del nivel.
	public float timeInTrial = 0; // Tiempo desde que se inicalizo el ultimo trial.
	boolean elementoSeleccionado = false; // Sin seleccion
	public Session session;

	public static class RunningSound {
		public static Sound sound; // Elemento de sonido
		public static boolean running; // Si se esta reproduciendo o no
		public static float start; // Cuando comienza la reproduccion del ultimo sonido
		public static float ends; // Cuando termina la reproduccion del ultimo sonido
		public static int id; // El id que identifica el recurso del ultimo sonido
		public static int loopsNumber; // Numero de veces que re reproduce el mismo sonido en forma seguida 
		public static long instance; // instancia que identifica cada reproduccion unequivocamente
		public static Array<SoundLog> history = new Array<SoundLog>(); // Log de eventos de sonido
		public static Array<Integer> secuenceId = new Array<Integer>(); // secuencia de los sonidos reproducidos.

		public static void Play(Sound sonido) {
			sound = sonido;
			long id;
			while ((id = sound.play(0)) == -1) {
				long t = TimeUtils.nanoTime();
				while (TimeUtils.nanoTime() - t < 50000000)
					;
			}
			sound.play();
			running = true;
		}

		public static void Stop() {
			if (running) {
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
		this.trial = new Trial(this.levelInfo.IdTrial(this.levelInfo.activeTrialPosition));
		// Carga la info general del trial al log
		this.trial.loadLog(this.session, this.levelInfo);
		this.trial.log.timeStartTrial = this.timeInLevel;

		// Carga la interfaz
		this.levelInterfaz = new LevelInterfaz(this.levelInfo, this.levelInfo.activeTrialPosition, this.trial);
		this.timeInTrial = 0;
		this.nextTrialPending = false;

		// Registra el evento de la creacion del trial
		this.levelInfo.levelLog.trialsVisited.add(this.trial.Id);

	}

	private void initCamera() {
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		cameraHelper = new CameraHelper();
	}

	public void update(float deltaTime) {

		// Actualiza el trial
		this.trial.update(deltaTime);

		// actualiza cosas generales
		cameraHelper.update(deltaTime);
		timeInLevel = timeInLevel + deltaTime;
		timeInTrial = timeInTrial + deltaTime;

		// Procesa cambios de trial si los hay pendientes
		if ((this.trial.trialCompleted) & (this.autoChangeTrial)) {
			this.nextTrialPending = true;
		}
		this.changeTrial();
	}

	private void changeTrial() {
		if (this.nextTrialPending) {
			boolean wait = false;
			for (AnswerBox box : this.trial.answerBoxes) {
				if (box.answerActive) {
					wait = true;
				}
			}
			for (TrainingBox box : this.trial.trainigBoxes) {
				if (box.runningSound) {
					wait = true;
				}
			}
			if (!wait) {
				this.nextTrialPending = false;
				// Agrega al log que termino el trial
				this.trial.log.timeStopTrial = this.timeInLevel;
				this.trial.log.timeInTrial = this.timeInTrial;
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
		// Registra que se sale al menu principal en los logs (falta agregar el log dentro del trial)
		this.levelInfo.levelLog.exitTrialId = this.trial.Id;
		this.levelInfo.levelLog.exitTrialPosition = this.levelInfo.activeTrialPosition;
		this.levelInfo.levelLog.timeExit = TimeUtils.millis();

		// Guarda en el registro general de log de levels el log de esta instanciacion del nivel e intenta enviarlo por interner.
		Level.LevelLogHistory.append(this.levelInfo.levelLog);

		// continua con la logica del programa
		LevelController.RunningSound.Stop();
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
		for (Box box : this.trial.trainigBoxes) {
			if (box.spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)) {
				cargarInfoDelTouch(box, touchData);
				elementoSeleccionado = true;
			}
		}

		// se fija si se toco alguna imagen answer
		for (Box box : this.trial.answerBoxes) {
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
			Gdx.app.debug(TAG, "Voy a deseleccionar!");
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
			this.trial.checkTrialCompleted();
		}

		// Se fija si se toco algun elemento de la interfaz del nivel
		if (this.trial.trialCompleted) {
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
		this.trial.log.resourcesIdSelected.add(touchData.thisTouchBox.contenido.Id);
		// Agrega la info que corresponda al log creando un TouchLog nuevo
		TouchLog touchLog = new TouchLog();
		touchLog.touchInstance = TimeUtils.millis();
		touchLog.trialInstance = this.trial.log.trialInstance;
		touchLog.trialId = this.trial.log.trialId;
		touchLog.idResourceTouched = touchData.thisTouchBox.contenido.Id;
		for (Categorias categoria : touchData.thisTouchBox.contenido.categorias) {
			touchLog.categorias.add(categoria);
		}
		touchLog.tipoDeTrial = this.trial.jsonTrial.modo;
		touchLog.isTrue = touchData.thisTouchBox.answer;
		if (touchData.thisTouchBox.getClass() == StimuliBox.class) {
			touchLog.isStimuli = true;
		} else {
			touchLog.isStimuli = false;
		}
		touchLog.timeSinceTrialStarts = this.timeInTrial;
		// Carga la info relacionada al sonido que esta en ejecucion
		touchLog.soundInstance = LevelController.RunningSound.instance;
		touchLog.soundRunning = LevelController.RunningSound.running;
		touchLog.timeLastStartSound = LevelController.RunningSound.start;
		touchLog.numberOfSoundLoops = LevelController.RunningSound.loopsNumber;
		touchLog.soundIdSecuenceInTrial = LevelController.RunningSound.secuenceId;

	}

	private void verifyAnswer(TouchInfo touchData) {
		// revisa si se acerto a la respuesta o no en caso de ser un test trial. 
		if (this.trial.jsonTrial.modo == TIPOdeTRIAL.TEST) {
			Boolean correcta = false;
			if (this.trial.rtaCorrecta.Id == touchData.thisTouchBox.contenido.Id) {
				correcta = true;
			} // Significa que se toco la respuesta igual a la correcta
			if (touchData.thisTouchBox.contenido.categorias.contains(Categorias.Texto, true)) { // Significa q se selecciono un texto
				for (Categorias categoriaDelObjetoTocado : touchData.thisTouchBox.contenido.categorias) {
					if (this.trial.rtaCorrecta.categorias.contains(categoriaDelObjetoTocado, true)) { // Significa que la respuesta correcta incluye alguna categoria del boton tocado. Se supone que los botones tocados solo tienen categorias texto y la que corresponda
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
		LevelController.RunningSound.Stop();
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
