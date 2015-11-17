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
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Level.AnalisisUmbral;
import com.turin.tur.main.diseno.Level.AnalisisUmbral.DetectionObject;
import com.turin.tur.main.diseno.Level.Significancia;
import com.turin.tur.main.diseno.Level.TIPOdeSIGNIFICANCIA;
import com.turin.tur.main.diseno.LevelInterfaz;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.TouchInfo;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.RunningSound;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.diseno.Trial.TouchLog;
import com.turin.tur.main.screens.ResultsScreen;
import com.turin.tur.main.util.CameraHelper;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeLEVEL;
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

	// Variable que sirve para ver si 
		
	public LevelController(Game game, int levelNumber, int trialNumber, Session session) {
		// Inicia los logs
		
		this.game = game; // Hereda la info del game (cosa de ventanas y eso)
		this.session = session; // Hereda la info de la session. Que registra en que session esta
		this.level = new Level(levelNumber); // Crea el nivel
		if (level.jsonLevel.randomTrialSort) {
			level.secuenciaTrailsId.shuffle();
		}
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
				
				
				if (this.level.jsonLevel.tipoDeLevel == TIPOdeLEVEL.UMBRAL) {
					
					AnalisisUmbral analisis = this.level.jsonLevel.analisisUmbral;
					
					// Creamos la info del objeto analizado
					DetectionObject detected = new DetectionObject();
					detected.answerTrue = this.trial.log.touchLog.peek().isTrue;
					detected.infoConceptual = this.trial.log.touchLog.peek().jsonMetaDataTouched.infoConceptual;
					

					// Primero nos fijamos que curva estamos analizando:
					if (analisis.curvaSuperiorActiva) {
						// Se fija en comparacion al ultimo intento para ver si hubo un "rebote o no" y en funcion de eso disminuir el salto
						if (analisis.historialAciertosCurvaSuperior.size>0) { // Si hay historia previa
							if (detected.answerTrue != analisis.historialAciertosCurvaSuperior.peek().answerTrue) { // Si hay rebote hay que disminuir el salto
								if (analisis.saltoCurvaSuperior!=1) {
									analisis.saltoCurvaSuperior=analisis.saltoCurvaSuperior-1;
								}
							}
						}
						// Modifica el nivel de señal del estimulo						
						if (detected.answerTrue) { // Si se detecto el estimulo bien hay que disminuir la señal de estimulo
							analisis.proximoNivelCurvaSuperior = analisis.proximoNivelCurvaSuperior - analisis.saltoCurvaSuperior; 
						} else { // Si no se detecto el estimulo hay que aumentar la señal de estimulo
							analisis.proximoNivelCurvaSuperior = analisis.proximoNivelCurvaSuperior + analisis.saltoCurvaSuperior;
						}
						// Limita el valor del proximo nivel entre los valores maximos y minimos.
						if (analisis.proximoNivelCurvaSuperior>analisis.cantidadDeNivelesDeDificultad) {
							analisis.proximoNivelCurvaSuperior=analisis.cantidadDeNivelesDeDificultad;
						}
						if (analisis.proximoNivelCurvaSuperior<1) {
							analisis.proximoNivelCurvaSuperior=1;
						}
						// Agrega el ultimo paso al historial.
						analisis.historialAciertosCurvaSuperior.add(detected);
					} else {
						// Se fija si hubo rebote para corregir el nivel del salto
						if (analisis.historialAciertosCurvaInferior.size>0) {
							if (detected.answerTrue != analisis.historialAciertosCurvaInferior.peek().answerTrue) {
								if (analisis.saltoCurvaInferior!=1) {
									analisis.saltoCurvaInferior = analisis.saltoCurvaInferior - 1;
								}
							}
						}
						if (!detected.answerTrue) {// si no detecta el estimulo hay que aumentar la señal
							analisis.proximoNivelCurvaInferior = analisis.proximoNivelCurvaInferior + analisis.saltoCurvaInferior;
						} else {
							analisis.proximoNivelCurvaInferior = analisis.proximoNivelCurvaInferior - analisis.saltoCurvaInferior;
						}
						// Limita el valor del proximo nivel entre los valores maximos y minimos.
						if (analisis.proximoNivelCurvaInferior>analisis.cantidadDeNivelesDeDificultad) {
							analisis.proximoNivelCurvaInferior=analisis.cantidadDeNivelesDeDificultad;
						}
						if (analisis.proximoNivelCurvaInferior<1) {
							analisis.proximoNivelCurvaInferior=1;
						}
						// Agrega el ultimo paso al historial.
						analisis.historialAciertosCurvaInferior.add(detected);
					}
					
					if (analisis.historialAciertosCurvaInferior.size + analisis.historialAciertosCurvaSuperior.size >=40) {
						completeLevel();
					}

					// Determina al azar si el proximo trial es de la curva superior o inferior
					// analisis.curvaSuperiorActiva = MathUtils.randomBoolean();

					int nextTrialPosition;
					if (analisis.curvaSuperiorActiva) {
						nextTrialPosition = findTrialId (analisis.indiceAnguloRefrencia, analisis.proximoNivelCurvaSuperior);
					} else {
						nextTrialPosition = findTrialId (analisis.indiceAnguloRefrencia, analisis.proximoNivelCurvaInferior);
					}
					this.level.activeTrialPosition = nextTrialPosition;
					this.initTrial();
					
				} else {
					if (isLastTrial()) {
						completeLevel();
					} else {
						this.level.activeTrialPosition += 1;
						this.initTrial();
					}
				}
			}
		}

	}
	
	private int findTrialId(int anguloReferencia, int nivelDificultad) {
		Array<Integer> listaDeTrialPosibles = new Array<Integer>();
		for (int idTrialaMirar : this.level.secuenciaTrailsId) {
			JsonTrial jsonTrial = Trial.JsonTrial.LoadTrial(idTrialaMirar);
			if ((jsonTrial.parametros.R == anguloReferencia) && (jsonTrial.parametros.D == nivelDificultad)) {
				listaDeTrialPosibles.add(idTrialaMirar);
			}
		}
		if (listaDeTrialPosibles.size == 0) {
			System.out.println("Error se esta buscando un trial con refrencia " + anguloReferencia + " y nivel de dificultad "+ nivelDificultad + " y no se ha encontrado");
			return 0;
		} else {
			int id = listaDeTrialPosibles.random();			
			return this.level.secuenciaTrailsId.indexOf(id, false);
		}
		
	}

	private void completeLevel() {
		// Indica y guarda en la info del usuario que completo este nivel
		// Se fija si le fue bien
		boolean pass = true;
		for (Significancia significancia: level.jsonLevel.significancias) {
			if (significancia.tipo == TIPOdeSIGNIFICANCIA.COMPLETO) {
				if (!(significancia.exitoMinimo<level.jsonLevel.aciertosTotales))
					if (level.jsonLevel.aciertosTotales!=significancia.distribucion.length-1) {
						pass=false;
						break;
					}
			}
			if (significancia.tipo == TIPOdeSIGNIFICANCIA.CATEGORIA) {
				if (!(significancia.exitoMinimo<level.jsonLevel.aciertosPorCategorias))
					if (level.jsonLevel.aciertosPorCategorias!=significancia.distribucion.length-1) {
						pass=false;
						break;
					}
			}
			if (significancia.tipo == TIPOdeSIGNIFICANCIA.IMAGEN) {
				if (!(significancia.exitoMinimo<level.jsonLevel.aciertosPorImagenes))
					if (level.jsonLevel.aciertosPorImagenes!=significancia.distribucion.length-1) {
						pass=false;
						break;
					}
			}
		}
		if (pass) {
			this.session.user.levelsCompleted.add(level.Id);
		}
		
		
		this.session.user.save();
		// Indica en el log del level que se completo
		this.level.levelLog.levelCompleted = true;
		this.goToResults();
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
			goToResults();
		}
		return false;
	}

	
	private void goToResults() {
		// Registra que se sale al menu principal en los logs
		this.level.levelLog.exitTrialId = trial.Id;
		this.level.levelLog.exitTrialPosition = this.level.activeTrialPosition;
		this.level.levelLog.timeExit = TimeUtils.millis();

		// Guarda en el registro general de log de levels el log de esta instanciacion del nivel e intenta enviarlo por internet.
		Gdx.app.debug(TAG, "Enviando log del trial");
		
		session.levelLogHistory.append(this.level.levelLog);
		//session.trialLogHistory.append(this.trial.log);

		// Marca en el sonido que se salio
		trial.runningSound.stopReason="exit";
		trial.runningSound.stop();
		
		//Aca reemplazamos esta linea por ir al menu de resultados
		game.setScreen(new ResultsScreen(game,this.session, this.level));
		//game.setScreen(new MenuScreen(game, this.session));
	}
	
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (Gdx.graphics.getFramesPerSecond()>40) {
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
		}
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
		touchLog.jsonMetaDataTouched = JsonResourcesMetaData.Load(touchData.thisTouchBox.contenido.resourceId.id);
		touchLog.touchInstance = TimeUtils.millis();
		touchLog.trialInstance = trial.log.trialInstance;
		touchLog.levelInstance = trial.log.levelInstance;
		touchLog.sessionInstance = trial.log.sessionId;
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
		Boolean correcta = false;
		if (this.level.jsonLevel.tipoDeLevel == TIPOdeLEVEL.UMBRAL) {
			
			// Verfica si se toco la opcion correcta o no.
			JsonResourcesMetaData JsonEstimulo = JsonResourcesMetaData.Load(trial.jsonTrial.rtaCorrectaId);
			JsonResourcesMetaData JsonSeleccion = JsonResourcesMetaData.Load(touchData.experimentalObjectTouch.resourceId.id);
			if (JsonEstimulo.infoConceptual.seJuntan == JsonSeleccion.infoConceptual.seJuntan) {
				correcta = true;
			}
			
		} else {
	
			// revisa si se acerto a la respuesta o no en caso de ser un test trial. 
			if (trial.jsonTrial.modo == TIPOdeTRIAL.TEST) {
				if (trial.rtaCorrecta.resourceId.id == touchData.thisTouchBox.contenido.resourceId.id) { // Significa que se toco la respuesta igual a la correcta
					correcta = true;
					if (!this.trial.alreadySelected) { // Evita que se cuenten las segundas selecciones en trials con feedback
						this.level.jsonLevel.aciertosPorImagenes++; //suma en uno los aciertos por imagen
						this.level.jsonLevel.aciertosTotales++; //suma en uno los aciertos totales
					}
				} 
				if (touchData.thisTouchBox.contenido.categorias.contains(Categorias.Texto, true)) { // Significa q se selecciono un texto
					for (Categorias categoriaDelObjetoTocado : touchData.thisTouchBox.contenido.categorias) {
						if (trial.rtaCorrecta.categorias.contains(categoriaDelObjetoTocado, true)) { // Significa que la respuesta correcta incluye alguna categoria del boton tocado. Se supone que los botones tocados solo tienen categorias texto y la que corresponda
							correcta = true;
							if (!this.trial.alreadySelected) { // Evita que se cuenten las segundas selecciones en trials con feedback
								this.level.jsonLevel.aciertosPorCategorias++; // Aumenta en uno los aciertos por categoria
								this.level.jsonLevel.aciertosTotales++; // Aumenta en uno los aciertos totales
							}
						}
					}
				}
			}
		}
		if (this.trial.jsonTrial.feedback) { // Evita que se pase de nivel si esta activado el feedback
			if (correcta) { // Significa q se selecciono la opcion correcta
				touchData.thisTouchBox.answer = true;
				this.nextTrialPending = true;
			}
		} else {
			if (correcta) { // Significa q se selecciono la opcion correcta
				touchData.thisTouchBox.answer = true;
			}
			this.nextTrialPending = true;
		}
		if (!this.trial.alreadySelected) { // Marca como que ya se selecciono algo despues de haber contado las cosas correctas y demas
			this.trial.alreadySelected=true;		
		}
	}

	private void logExitTrial() {
		// Agrega al log que termino el trial
		trial.log.timeExitTrial = TimeUtils.millis();
		trial.log.trialExitRecorded=true;
		trial.runningSound.stopReason="exit";
		trial.runningSound.stop();
		// Intenta enviar la info del trial y sino la guarda
		session.trialLogHistory.append(trial.log);
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
