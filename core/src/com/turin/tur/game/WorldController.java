package com.turin.tur.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.game.objects.BoxContainer;
import com.turin.tur.game.objects.ImageBox;
import com.turin.tur.game.objects.ImageSelectableBox;
import com.turin.tur.game.objects.StimuliBox;
import com.turin.tur.util.CameraHelper;
import com.turin.tur.util.Constants;
import com.turin.tur.util.LevelInfo;
import com.turin.tur.util.TouchInfo;


public class WorldController implements InputProcessor  {

	public OrthographicCamera camera;
	public static final String TAG = WorldController.class.getName();
	public CameraHelper cameraHelper;
	private float time = 0;
	private float time_selected = 0;
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	public LevelInfo levelInfo;
	
	public WorldController () {
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
		levelInfo = new LevelInfo();
	}

	public void update (float deltaTime) {

		// Actualiza elementos del trial
		if (levelInfo.imageTrialElements != null) {
			for (ImageBox element : levelInfo.imageTrialElements) {
				element.update(deltaTime);
			}
		}
		if (levelInfo.optionsTrialElements != null) {
			for (ImageSelectableBox element : levelInfo.optionsTrialElements) {
				element.update(deltaTime);
			}
		}
		if (levelInfo.stimuliTrialElement != null) {
			levelInfo.stimuliTrialElement.update(deltaTime);
		}
		
		// actualiza el nivel
		levelInfo.Update(deltaTime);
		
		// actualiza cosas generales
		cameraHelper.update(deltaTime);
		time = time + deltaTime;

	}

	@Override
	public boolean keyUp (int keycode) {
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
	    	if (levelInfo.imageTrialElements != null) {
				for (ImageBox element : levelInfo.imageTrialElements) {
					if (element.spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)) {
						Gdx.app.debug(TAG, "Ha tocado la imagen de entrenamiento " + element.contenido.Id);
						cargarTouch (element,touchData);
						elementoSeleccionado = true;
					}
				}
	    	}
	    	// se fija si se toco alguna imagen de test
	    	if (levelInfo.optionsTrialElements != null) {
				for (ImageSelectableBox element : levelInfo.optionsTrialElements) {
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
					acierto = elemento.itsTrue(levelInfo.stimuliTrialElement.contenido);
				}
				touchData.thisTouch.select();
			}
	    	
    	}
    	if (acierto) {
    		levelInfo.autoRestart = true;
    		levelInfo.restartTime = levelInfo.levelTime + 1;
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


}
	
	