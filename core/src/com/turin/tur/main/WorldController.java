package com.turin.tur.main;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.objects.BoxContainer;
import com.turin.tur.main.objects.ImageBox;
import com.turin.tur.main.objects.ImageSelectableBox;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.CameraHelper;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.TouchInfo;
import com.turin.tur.main.util.TrialInfo;


public class WorldController implements InputProcessor  {

	public OrthographicCamera camera;
	public static final String TAG = WorldController.class.getName();
	public CameraHelper cameraHelper;
	private float time = 0;
	private float time_selected = 0;
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	public TrialInfo trialInfo;
	public Trial trial;
	
	private Game game;
	
	
	public WorldController (Game game) {
		this.game = game;
		init();
	}
	
	private void init () {
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		cameraHelper = new CameraHelper();
		initLevel();
	}
	

	private void initLevel() {
		trialInfo = new TrialInfo();
		trial = new Trial(1); //SEGUIR ACA
	}

	public void update (float deltaTime) {

		// Actualiza elementos del trial
		if (trialInfo.imageTrialElements != null) {
			for (ImageBox element : trialInfo.imageTrialElements) {
				element.update(deltaTime);
			}
		}
		if (trialInfo.optionsTrialElements != null) {
			for (ImageSelectableBox element : trialInfo.optionsTrialElements) {
				element.update(deltaTime);
			}
		}
		if (trialInfo.stimuliTrialElement != null) {
			trialInfo.stimuliTrialElement.update(deltaTime);
		}
		
		// actualiza el nivel
		trialInfo.Update(deltaTime);
		
		// actualiza cosas generales
		cameraHelper.update(deltaTime);
		time = time + deltaTime;

	}

	@Override
	public boolean keyUp (int keycode) {
		
		// Back to Menu
		if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			backToMenu();
		}
		return false;
	}
	
	@Override
    public boolean keyDown(int keycode) {
		return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    	
    	// Crea un evento de toque
    	TouchInfo touch = new TouchInfo(time,time_selected);
    	// calcula el toque en pantalla
   		touch.coordScreen = new Vector3 (screenX, screenY, 0);
   		// calcula el toque en el juego 
   		touch.coordGame = camera.unproject(touch.coordScreen);
   		// determina que accion es la siguiente
    	touch.actionToDo = Constants.Touch.ToDo.DETECTOVERLAP;
    	// procesa la info del toque en funcion de otros elementos del juego
    	procesarToque(touch);
    	// agrega el toque a la secuencia de toques acumulados
    	touchSecuence.add(touch);
    	return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
    private void procesarToque (TouchInfo touchData) {
    	boolean acierto = false;
    	if (touchData.actionToDo == Constants.Touch.ToDo.DETECTOVERLAP) {
	    	boolean elementoSeleccionado = false; // Sin seleccion
	    	if (touchSecuence.size > 0) {
	    		touchData.lastTouch = touchSecuence.peek().thisTouch;
	    	}
	    	
	    	// se fija si se toco alguna imagen de entrenamiento
	    	if (trialInfo.imageTrialElements != null) {
				for (ImageBox element : trialInfo.imageTrialElements) {
					if (element.spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)) {
						Gdx.app.debug(TAG, "Ha tocado la imagen de entrenamiento " + element.contenido.Id);
						cargarTouch (element,touchData);
						elementoSeleccionado = true;
					}
				}
	    	}
	    	// se fija si se toco alguna imagen de test
	    	if (trialInfo.optionsTrialElements != null) {
				for (ImageSelectableBox element : trialInfo.optionsTrialElements) {
					if (element.spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)) {
						Gdx.app.debug(TAG, "Ha tocado la imagen de seleccion " + element.contenido.Id);
						cargarTouch (element,touchData);
						elementoSeleccionado = true;
					}
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
			if (touchData.lastTouch != null) {
				touchData.lastTouch.unSelect();
			}
			if (touchData.elementTouched) {
				if (touchData.thisTouch.getClass() == ImageSelectableBox.class) {
					ImageSelectableBox elemento = (ImageSelectableBox) touchData.thisTouch; 
					acierto = elemento.itsTrue(trialInfo.stimuliTrialElement.contenido);
				}
				touchData.thisTouch.select();
			}
	    	
    	}
    	if (acierto) {
    		stopSound();
    		trialInfo.autoRestart = true;
    		trialInfo.restartTime = trialInfo.levelTime + 1;
    	}
    }
    
    private void cargarTouch (BoxContainer element, TouchInfo thisTouch) {
		// carga la info en el touch
		thisTouch.elementTouched=true;
		thisTouch.thisTouch = element;
		thisTouch.experimentalObjectTouch = element.contenido;
		thisTouch.elementTouchType = Constants.Touch.Type.IMAGE;
		thisTouch.actionToDo = Constants.Touch.ToDo.NOTHING;
		
    }

    private void backToMenu () {
    	// switch to menu screen
    	stopSound();
    	game.setScreen(new MenuScreen(game));
    }
 
    public void stopSound () {
    	if (trialInfo.imageTrialElements != null) {
    		for (ImageBox element: trialInfo.imageTrialElements) {
    			element.contenido.sonido.stop();
    		}
    	}
    	if (trialInfo.optionsTrialElements != null) {
    		for (ImageSelectableBox element: trialInfo.optionsTrialElements) {
    			element.contenido.sonido.stop();
    		}
    	}
    }
}
	
	