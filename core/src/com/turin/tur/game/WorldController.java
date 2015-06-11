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
	//public Sprite[] animacionContorno;
	//public Sound[] sonidos;
	//public Sprite[] contenidos;
	private long Id_sonido; //variable que guarda el Id de los sonidos activos 
	//private int imageSelected = -1; // Numero negativo significa que no hay nada seleccionado
	public CameraHelper cameraHelper;
	private float time = 0;
	private float time_selected = 0;
	public Array<TouchInfo> touchSecuence = new Array<TouchInfo>();
	//private Boolean animacionCompletada = false;
	public LevelInfo levelInfo;
	//public SoundBoxContainer objetoDePrueba;
	public ImageBoxContainer objetoDePrueba;
	public ExperimentalObject objetoDePruebaExperimental;
	public Array<ImageBoxContainer> trialElements = new Array<ImageBoxContainer>();
	
	public WorldController () {
		init();
	}
	
	private void init () {
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		cameraHelper = new CameraHelper();
		//initContornosObjects();
		//initAudioObjects();
		//initContenidosObjects();
		initLevel();
		//pruebas();
		initTrialElements();
	}
	
	/*
	private void pruebas() {
		Sound sonidoPrueba =Gdx.audio.newSound(Gdx.files.internal("sounds/sonido"+Integer.toString(0)+".wav"));
		Array<TextureRegion> regions = Assets.instance.contenido.contenido_serie;
		Sprite spritePrueba = new Sprite(regions.get(0));
		int Id = 0;
		
		objetoDePruebaExperimental = new ExperimentalObject (spritePrueba,sonidoPrueba,Id);
		//objetoDePrueba = new SoundBoxContainer(objetoDePruebaExperimental);
		objetoDePrueba = new ImageBoxContainer(objetoDePruebaExperimental);
		objetoDePrueba.Select();
		
	}
	*/

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
		//trialElements.get(5).Select();
	}
	
	/*
	private void initContornosObjects() {
		// Create new array for sprites of contornos
		animacionContorno = new Sprite[Constants.NUMERO_ELEMENTOS];
		
		// Create a list of texture regions
		Array<TextureRegion> regions = Assets.instance.cuadrado.cuadrado_serie;
		
		// Create new sprites using the just created texture
		for (int i = 0; i < animacionContorno.length; i++) {
			Sprite spr = new Sprite(regions.get(0));
			// Define sprite size to be 1m x 1m in game world
			spr.setSize(Constants.LADO_CUADROS*1.2f,Constants.LADO_CUADROS*1.2f);
			// Set origin to sprite's center
			//spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
			spr.setOriginCenter();
			float X = Constants.posiciones_elementos_vertice[i][0];
			float Y = Constants.posiciones_elementos_vertice[i][1];
			spr.setPosition(X, Y);
			// Put new sprite into array
			animacionContorno[i] = spr;
		}
	}
	
	private void initAudioObjects() {
		sonidos = new Sound[Constants.NUMERO_ELEMENTOS];
		for (int i = 0; i < sonidos.length; i++) {
			Sound sonido = Gdx.audio.newSound(Gdx.files.internal("sounds/sonido"+Integer.toString(i)+".wav"));
			sonidos[i] = sonido;
		}	
	}
	
	private void initContenidosObjects() {
		// Crea el array vacio
		contenidos = new Sprite[Constants.NUMERO_ELEMENTOS];
		// Carga el array de contenidos del atlas
		Array<TextureRegion> regions = Assets.instance.contenido.contenido_serie;

		// Create new sprites using the just created texture
		for (int i = 0; i < contenidos.length; i++) {
			Sprite spr = new Sprite(regions.get(i));
			//Define sprite size to be 1m x 1m in game world
			spr.setSize(Constants.LADO_CUADROS,Constants.LADO_CUADROS);
			// Set origin to sprite's center
			//spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
			//spr.setOriginCenter();
			float X = Constants.posiciones_elementos_vertice[i][0]+0.1f; // Corregir esta posicion q esta a mano
			float Y = Constants.posiciones_elementos_vertice[i][1]+0.1f; // Corregir esta posicion q esta a mano
			spr.setPosition(X, Y);
			// Put new sprite into array
			contenidos[i] = spr;
		}
	}
 	*/
	
	public void update (float deltaTime) {
		/*
		if ((imageSelected != -1) && !animacionCompletada) { 
			updateAnimacionContornos(deltaTime);
		}
		*/
		
		for (int i=0; i < Constants.NUMERO_ELEMENTOS; i++) {
			trialElements.get(i).update(deltaTime);
		}
		cameraHelper.update(deltaTime);
		time = time + deltaTime;
		//objetoDePrueba.update(deltaTime);
	}

	
	/*
	private void updateAnimacionContornos(float deltaTime) {
		float factor_velocidad = 2f; // Nota, para que quede bien tomando 10 imagenes el factor debe ser 10/numero de segundos del audio 
		time_selected = time_selected + deltaTime;
		//Gdx.app.debug(TAG, Float.toString(time_selected));
		float tiempo_modificado = time_selected*factor_velocidad;
		int numero_sprite = (int) tiempo_modificado; // toma la parte entera en segundos
		numero_sprite = numero_sprite + 1;
		//Gdx.app.debug(TAG, Float.toString(numero_sprite));
		animacionContorno[imageSelected].setRegion(Assets.instance.cuadrado.cuadrado_serie.get(numero_sprite));
		if (numero_sprite == 9) { // OJO que aca 9 esta puesto a mano, hay que referenciarlo a la cantidad de imagenes - 1 
			animacionCompletada = true;
			animacionContorno[imageSelected].setRegion(Assets.instance.cuadrado.cuadrado);
		}
	}
	*/
	
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
	
	