package com.turin.tur.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.game.objects.ExperimentalObject;
import com.turin.tur.game.objects.ImageBoxContainer;
import com.turin.tur.game.objects.SoundBoxContainer;
import com.turin.tur.util.CameraHelper;
import com.turin.tur.util.Constants;
import com.turin.tur.util.LevelInfo;
import com.turin.tur.util.TouchInfo;


public class WorldController implements InputProcessor  {

	public OrthographicCamera camera;
	public static final String TAG = WorldController.class.getName();
	private long Id_sonido; //variable que guarda el Id de los sonidos activos 
	public CameraHelper cameraHelper;
	private float time = 0;
	private float time_selected = 0;
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	public LevelInfo levelInfo;
	public Array<ImageBoxContainer> trialElements = new Array<ImageBoxContainer>();
	
	public WorldController () {
		init();
	}
	
	private void init () {
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		cameraHelper = new CameraHelper();
		initLevel();
		initTrialElements();
	}
	

	private void initLevel() {
		levelInfo = new LevelInfo();
	}

	private void initTrialElements (){
		
		// Crea el array de imagenes
		Array<TextureRegion> regions = Assets.instance.contenido.contenido_serie;
		// Crea el array de sonidos
		Sound[] sonidos = new Sound[Constants.NUMERO_ELEMENTOS];
		for (int i = 0; i < sonidos.length; i++) {
			Sound sonido = Gdx.audio.newSound(Gdx.files.internal("sounds/sonido"+Integer.toString(i)+".wav"));
			sonidos[i] = sonido;
		}	
		// Crea el array de objetos experimentales
		Array<ExperimentalObject> objetosExperimentales = new Array<ExperimentalObject>();
		for (int i = 0; i < sonidos.length; i++) {
			objetosExperimentales.add(new ExperimentalObject(new Sprite(regions.get(i)),sonidos[i],i));
		}
		for (int i=0; i < Constants.NUMERO_ELEMENTOS; i++) {
			trialElements.add(new ImageBoxContainer (objetosExperimentales.get(i)));
			trialElements.get(i).SetPosition(Constants.posiciones_elementos_centros[i][0],Constants.posiciones_elementos_centros[i][1]);
		}
	}
	

	public void update (float deltaTime) {

		for (int i=0; i < Constants.NUMERO_ELEMENTOS; i++) {
			trialElements.get(i).update(deltaTime);
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
	    	
	    	for (int i = 0; i < trialElements.size; i++) { // itera sobre todos los contenidos (que son las imagenes de los dibujos)
				if (trialElements.get(i).spr.getBoundingRectangle().contains(touchData.coordGame.x, touchData.coordGame.y)){
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
    		trialElements.get(lastSelection).unSelect();
    	}
    	time_selected = 0;
    	if (selection != -1) {
    		trialElements.get(selection).Select();
    	}
    }

}
	
	