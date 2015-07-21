package com.turin.tur.main.logic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Boxes.AnswerBox;
import com.turin.tur.main.diseno.Boxes.TrainingBox;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.LevelInterfaz;
import com.turin.tur.main.diseno.TouchInfo;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.LevelInterfaz.Botones;
import com.turin.tur.main.diseno.User;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.CameraHelper;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class LevelController implements InputProcessor {

	// Cosas relacionadas con la interfaz grafica
	public OrthographicCamera camera; 
	public static final String TAG = LevelController.class.getName();
	public CameraHelper cameraHelper;
	
	// Cosas relacionadas con los elementos del juego
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	public Array<TouchInfo> completeTouchSecuence = new Array<TouchInfo>();
	public Trial trialActive;
	public LevelInterfaz levelInterfaz;
	private Game game;
	private Level levelInfo; //Informacion del nivel cargado
	public User user = User.Load();
	
	// Variables que manejan la dinamica de flujo de informacion en el control del nivel
	public boolean nextTrialPending = false; // Genera la señal de que hay que cambiar de trial (para esperar a que finalicen cuestiones de animacion) 
	public float timeInLevel = 0; // Tiempo general dentro del nivel.
	public float timeInTrial = 0; // Tiempo desde que se inicalizo el ultimo trial.
	boolean elementoSeleccionado = false; // Sin seleccion
	
	public LevelController(Game game, int levelNumber, int trialNumber) {
		this.game = game;
		this.levelInfo = new Level(levelNumber);
		this.initCamera();
		this.initTrial();
	}

	private void initTrial() {
		this.trialActive = new Trial (this.levelInfo.IdTrial(this.levelInfo.activeTrialPosition));
		this.levelInterfaz = new LevelInterfaz (this.levelInfo, this.levelInfo.activeTrialPosition, this.trialActive);
		this.timeInTrial=0;
		String logText = TAG + ": Inicializado el trial " + this.trialActive.Id + ".\r\n";
		FileHelper.appendFile(Constants.USERLOG, logText);
	}

	private void initCamera() {
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		cameraHelper = new CameraHelper();		
	}

	public void update(float deltaTime) {
		// Actualiza el trial
		this.trialActive.update(deltaTime);
				
		// actualiza cosas generales
		cameraHelper.update(deltaTime);
		timeInLevel = timeInLevel + deltaTime;
		timeInTrial = timeInTrial + deltaTime;
		
		// Procesa cambios de trial si los hay pendientes
		if (this.trialActive.trialCompleted) {
				this.nextTrialPending=true;
			}
		this.changeTrial();
	}

	private void changeTrial() {
		if (this.nextTrialPending) {
			boolean wait = false;
			for (AnswerBox box :this.trialActive.answerBoxes) {
				if (box.answerActive) {wait=true;}
			}
			for (TrainingBox box :this.trialActive.trainigBoxes) {
				if (box.runningSound) {wait=true;}
			}
			if (!wait) {
				this.nextTrialPending=false;
				if (isLastTrial()) {completeLevel();} else {goToNextTrial();}
			}
		}
		
	}

	private void completeLevel() {
		// TODO Auto-generated method stub
	}

	private boolean isLastTrial() {
		if (levelInfo.activeTrialPosition + 1 == levelInfo.secuenciaTrailsId.size) {
			Gdx.app.debug(TAG, "Ultimo trial del nivel");
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
		stopSound();
		game.setScreen(new MenuScreen(game));
	}

	private void stopSound() {
		for (Box box: this.trialActive.allBox) {
			box.stopSound();
		}
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// Crea un evento de toque
    	TouchInfo touch = new TouchInfo(timeInLevel,timeInTrial,levelInfo,trialActive,user);
    	// calcula el toque en pantalla
   		touch.coordScreen = new Vector3 (screenX, screenY, 0);
   		// calcula el toque en el juego 
   		touch.coordGame = camera.unproject(touch.coordScreen.cpy()); // PREGUNTA: si no le pongo el copy, toma como el mismo vector y sobreescribe el coordScreen. RARO
   		// determina que accion es la siguiente
    	touch.actionToDo = Constants.Touch.ToDo.DETECTOVERLAP;
    	// procesa la info del toque en funcion de otros elementos del juego
    	procesarToque(touch);
    	// agrega el toque a la secuencia de toques acumulados
    	touchSecuence.add(touch);
		return false;
	}
	

	private void procesarToque(TouchInfo touchData) {
    	if (touchData.actionToDo == Constants.Touch.ToDo.DETECTOVERLAP) {
	    	elementoSeleccionado = false; // Sin seleccion
	    	if (touchSecuence.size > 0) {
	    		touchData.lastTouchBox = touchSecuence.peek().thisTouchBox;
	    	}
	    	
	    	// se fija si se toco alguna imagen training
	    	for (Box box : this.trialActive.trainigBoxes) {
	    		if (box.spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)){
	    			Gdx.app.debug(TAG, "Ha tocado la imagen " + box.contenido.Id);
	    			cargarInfoDelTouch (box,touchData);
					elementoSeleccionado = true;
	    		}
	    	}

	    	// se fija si se toco alguna imagen answer
	    	for (Box box : this.trialActive.answerBoxes) {
	    		if (box.spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)){
	    			Gdx.app.debug(TAG, "Ha tocado la imagen " + box.contenido.Id);
	    			cargarInfoDelTouch (box,touchData);
					elementoSeleccionado = true;
	    		}
	    	}

	    	
			// Actua si no se toco nada
			if (!elementoSeleccionado) {
				Gdx.app.debug(TAG, "No se ha tocado ninguna imagen ");
				touchData.elementTouched=false;
				touchData.elementTouchType = Constants.Touch.Type.NOTHING;
				touchData.actionToDo = Constants.Touch.ToDo.NOTHING;
			}
	    	
			// genera los eventos correspondientes al toque
			
			// anula la seleccion del evento previo
			if (touchData.lastTouchBox != null) {
				Gdx.app.debug(TAG, "Voy a deseleccionar!");
				touchData.lastTouchBox.unSelect();
			}
			
			// selecciona el elemento actual si hay elemento tocado
			if (touchData.elementTouched) {
				// revisa si se acerto a la respuesta o no en caso de ser un test trial. 
				if (this.trialActive.modo == TIPOdeTRIAL.TEST) {
					if (this.trialActive.rtaCorrecta.Id == touchData.thisTouchBox.contenido.Id) { // Significa q se selecciono la opcion correcta
						touchData.thisTouchBox.answer=true;
						this.trialActive.stimuliBox.stopSound();
			    		this.nextTrialPending=true;
					}
				}
				// Activa el elemento tocado
				touchData.thisTouchBox.select();
			}
	    	
			// Se fija si se toco algun elemento de la interfaz del nivel
			for (Botones boton:this.levelInterfaz.botones) {
				if (boton.imagen.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)){
					Gdx.app.debug(TAG, "Ha tocado el boton " + boton.getClass().getName());
					elementoSeleccionado = true;
					if (boton.getClass() == LevelInterfaz.BotonSiguiente.class){ this.goToNextTrial();}
					if (boton.getClass() == LevelInterfaz.BotonAnterior.class){ this.goToPreviousTrial();}
	
				}
			}
			this.trialActive.checkTrialCompleted();
    	}		
	}


	private void exitTrial() {
		saveTouches();
		this.stopSound();		
	}
	

	private void saveTouches() {
		for (TouchInfo touch : touchSecuence) {
			touch.logTouch(touch);
		}
		completeTouchSecuence.addAll(touchSecuence);
		touchSecuence.clear();
	}

	private void cargarInfoDelTouch(Box box, TouchInfo thisTouch) {
		// carga la info en el touch
		thisTouch.elementTouched=true;
		thisTouch.thisTouchBox = box;
		thisTouch.experimentalObjectTouch = box.contenido;
		thisTouch.elementTouchType = Constants.Touch.Type.IMAGE;
		thisTouch.actionToDo = Constants.Touch.ToDo.NOTHING;	
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
