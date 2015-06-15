package com.turin.tur.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
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

		for (int i=0; i < levelInfo.trialElements.size; i++) {
			levelInfo.trialElements.get(i).update(deltaTime);
		}
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
    	// recupera la ultima seleccion y la actual
    	int lastSelection;
    	try {
    		lastSelection = touchSecuence.peek().elementTouch;
		} catch (Exception e) {
			lastSelection = -1; // Esto sucede cuando se busca el elemento anterior y todavia no se toco nunca la pantalla
		} 
    	// actualiza la seleccion 
		updateSelection (lastSelection,touch.elementTouch);
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
    	if (touchData.actionToDo == Constants.Touch.ToDo.DETECTOVERLAP) {
	    	int seleccion = -1; // Sin seleccion
	    	
	    	for (int i = 0; i < levelInfo.trialElements.size; i++) { // itera sobre todos los contenidos (que son las imagenes de los dibujos)
				if (levelInfo.trialElements.get(i).spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)){
					Gdx.app.debug(TAG, "Ha tocado la imagen" + i);
					seleccion = i;
				}
			}
			
	    	touchData.elementTouch=seleccion;
	    	touchData.elementTouchType= Constants.Touch.Type.IMAGE;
	    	touchData.actionToDo = Constants.Touch.ToDo.NOTHING;
    	}
    }
    

    private void updateSelection (int lastSelection, int selection) {
    	// restaura todo como si no hubiera seleccion
    	if (lastSelection != -1) {
    		levelInfo.trialElements.get(lastSelection).unSelect();
    	}
    	time_selected = 0;
    	if (selection != -1) {
    		levelInfo.trialElements.get(selection).select();
    	}
    }

}
	
	