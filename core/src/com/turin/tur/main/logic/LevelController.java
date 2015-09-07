package com.turin.tur.main.logic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
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
import com.turin.tur.main.diseno.RunningSound;
import com.turin.tur.main.diseno.Trial.TouchLog;
import com.turin.tur.main.diseno.Trial.TrialLogHistory;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.CameraHelper;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class LevelController implements InputProcessor {

	// Cosas relacionadas con la interfaz grafica
	public OrthographicCamera camera;
	public static final String TAG = LevelController.class.getName();
	public CameraHelper cameraHelper;

	// Cosas relacionadas con los elementos del juego
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	public Trial trial;
	public LevelInterfaz levelInterfaz;
	private Game game;
	private Level level; //Informacion del nivel cargado

	// Variables que manejan la dinamica de flujo de informacion en el control del nivel
	public boolean nextTrialPending = false; // Genera la señal de que hay que cambiar de trial (para esperar a que finalicen cuestiones de animacion) 
	public float timeInLevel = 0; // Tiempo general dentro del nivel.
	public float timeInTrial = 0; // Tiempo desde que se inicalizo el ultimo trial.
	boolean elementoSeleccionado = false; // Sin seleccion
	public Session session;

	

	public LevelController(Game game, int levelNumber, int trialNumber, Session session) {
		this.game = game; // Hereda la info del game (cosa de ventanas y eso)
		this.session = session; // Hereda la info de la session. Que registra en que session esta
		this.level = new Level(levelNumber); // Crea el nivel
		// Agrega la info del log del level asociada a la creacion
		this.level.levelLog.sessionId = this.session.sessionLog.id;
		this.level.levelLog.idUser = this.session.user.id;
		this.initCamera();
		this.initTrial();
	}

	private void initTrial() {
		trial = new Trial(this.level.IdTrial(this.level.activeTrialPosition));
		trial.runningSound = new RunningSound(this.trial);
		
		// Carga la info general del trial al log
		trial.newLog(this.session, this.level);
		
		// Carga la interfaz
		this.levelInterfaz = new LevelInterfaz(this.level, this.level.activeTrialPosition, trial);
		timeInTrial = 0;
		// this.nextTrialPending = false;

		// Registra el evento de la creacion del trial
		this.level.levelLog.trialsVisited.add(trial.Id);

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
		if (trial.trialCompleted) {
			this.nextTrialPending = true;
		}
		this.tryChangeTrial();
	}

	private void tryChangeTrial() {
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
				this.logExitTrial();
				if (isLastTrial()) {
					completeLevel();
				} else {
					this.level.activeTrialPosition += 1;
					this.initTrial();
				}
			}
		}

	}

	private void completeLevel() {
		// Indica y guarda en la info del usuario que completo este nivel
		this.session.user.levelsCompleted.add(level.Id);
		this.session.user.save();
		// Indica en el log del level que se completo
		this.level.levelLog.levelCompleted = true;
		this.backToMenu();
	}

	private boolean isLastTrial() {
		if (level.activeTrialPosition + 1 == level.secuenciaTrailsId.size) {
			return true;
		}
		return false;
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
			// Indica en el log del level que se completo
			this.level.levelLog.levelCompleted = false;
			this.logExitTrial();
			backToMenu();
		}
		return false;
	}

	
	private void backToMenu() {
		// Registra que se sale al menu principal en los logs
		this.level.levelLog.exitTrialId = trial.Id;
		this.level.levelLog.exitTrialPosition = this.level.activeTrialPosition;
		this.level.levelLog.timeExit = TimeUtils.millis();

		// Guarda en el registro general de log de levels el log de esta instanciacion del nivel e intenta enviarlo por interner.
		Level.LevelLogHistory.append(this.level.levelLog);

		// Marca en el sonido que se salio
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
			touchData.lastTouchBox.unSelect(trial);
		}

		// Actua si hay elemento tocado
		if (touchData.elementTouched) {
			// Se fija si es la respuesta correcta y actua en consecuencia
			this.verifyAnswer(touchData);
			// Crea el log del toque
			this.logTouch(touchData);
			// Activa el elemento tocado
			touchData.thisTouchBox.select(touchData,trial);
			// Se fija si se completo el nivel
			trial.checkTrialCompleted();
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

	private void logExitTrial() {
		// Agrega al log que termino el trial
		trial.log.timeExitTrial = TimeUtils.millis();
		trial.log.trialExitRecorded=true;
		trial.runningSound.stopReason="exit";
		trial.runningSound.stop();
		// Intenta enviar la info del trial y sino la guarda
		TrialLogHistory.append(trial.log);
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
