package com.turin.tur.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.turin.tur.util.CameraHelper;
import com.turin.tur.util.Constants;
import com.turin.tur.util.TouchInfo;


public class WorldController implements InputProcessor  {

	public OrthographicCamera camera;
	public static final String TAG = WorldController.class.getName();
	public Sprite[] animacionContorno;
	public Sound[] sonidos;
	public Sprite[] contenidos;
	public long Id_sonido; //variable que guarda el Id de los sonidos activos 
	public int imageSelected;
	public CameraHelper cameraHelper;
	public float time;
	public float time_selected;
	public ObjectMap<String, Object> touchInfo = new ObjectMap<String, Object>(); // revisar
	public TouchInfo[] touchSecuence;
	
	
	public WorldController () {
		init();
	}
	
	private void init () {
		time=0;
		time_selected=0;
		Gdx.input.setInputProcessor(this);
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		cameraHelper = new CameraHelper();
		initContornosObjects();
		initAudioObjects();
		initContenidosObjects();
		initTouch(); //revisar
	}
	
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
//			spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
			spr.setOriginCenter();
			float X = Constants.posiciones_elementos_vertice[i][0];
			float Y = Constants.posiciones_elementos_vertice[i][1];
			spr.setPosition(X, Y);
			// Put new sprite into array
			animacionContorno[i] = spr;
		}
		// Set first sprite as selected one
		imageSelected = 0;
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
//			spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
			//spr.setOriginCenter();
			float X = Constants.posiciones_elementos_vertice[i][0]+0.1f; // Corregir esta posicion q esta a mano
			float Y = Constants.posiciones_elementos_vertice[i][1]+0.1f; // Corregir esta posicion q esta a mano
			spr.setPosition(X, Y);
			// Put new sprite into array
			contenidos[i] = spr;
		}
	}

	private void initTouch () {
		touchInfo.put("UnproyectPendiente", false);
		touchInfo.put("ToquePantalla", null);
		touchInfo.put("ToqueJuego", null);
		touchInfo.put("ToquePendiente", false);
		touchInfo.put("ImagenSelecionada", null);
	}
	
	public void update (float deltaTime) {
		updatePendents(deltaTime);
		updateTestObjects(deltaTime);
		//handleDebugInput(deltaTime);
		cameraHelper.update(deltaTime);
		time = time + deltaTime;
	}
	

/*
*	private void handleDebugInput (float deltaTime) {
*		if (Gdx.app.getType() != ApplicationType.Desktop) return;
*
* 		// Selected Sprite Controls
*		float sprMoveSpeed = 5 * deltaTime;
*		if (Gdx.input.isKeyPressed(Keys.A)) moveSelectedSprite(
*		-sprMoveSpeed, 0);
*		if (Gdx.input.isKeyPressed(Keys.D))
*		moveSelectedSprite(sprMoveSpeed, 0);
*		if (Gdx.input.isKeyPressed(Keys.W)) moveSelectedSprite(0,
*		sprMoveSpeed);
*		if (Gdx.input.isKeyPressed(Keys.S)) moveSelectedSprite(0,
*		-sprMoveSpeed);
*
*		
*		// Camera Controls (move)
*		float camMoveSpeed = 5 * deltaTime;
*		float camMoveSpeedAccelerationFactor = 5;
*		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *=
*		camMoveSpeedAccelerationFactor;
*		if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed,
*		0);
*		if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed,
*		0);
*		if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
*		if (Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0,
*		-camMoveSpeed);
*		if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
*		cameraHelper.setPosition(0, 0);
*		// Camera Controls (zoom)
*		float camZoomSpeed = 1 * deltaTime;
*		float camZoomSpeedAccelerationFactor = 5;
*		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *=
*		camZoomSpeedAccelerationFactor;
*		if (Gdx.input.isKeyPressed(Keys.COMMA))
*		cameraHelper.addZoom(camZoomSpeed);
*		if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(
*		-camZoomSpeed);
*		if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
*
*	}
*/
	
/*	
*   private void moveCamera (float x, float y) {
*
*		x += cameraHelper.getPosition().x;
*		y += cameraHelper.getPosition().y;
*		cameraHelper.setPosition(x, y);
*	}
*/
	
/*
	private void moveSelectedSprite (float x, float y) {
		animacionContorno[imageSelected].translate(x, y);
	}
*/	

	
	private void updateTestObjects(float deltaTime) {
		float factor_velocidad = 2f; // Nota, para que quede bien tomando 10 imagenes el factor debe ser 10/numero de segundos del audio 
		time_selected = time_selected + deltaTime;
		//Gdx.app.debug(TAG, Float.toString(time_selected));
		float tiempo_modificado = time_selected*factor_velocidad;
		int numero_sprite = (int) tiempo_modificado; // toma la parte entera en segundos
		numero_sprite = numero_sprite % 10; // REVISAR (aca hay que reemplazar el 10 por alguna variable relacionada a la cantidad de imagenes para la animacion del sprite
		numero_sprite = numero_sprite + 1;
		//Gdx.app.debug(TAG, Float.toString(numero_sprite));
		animacionContorno[imageSelected].setRegion(Assets.instance.cuadrado.cuadrado_serie.get(numero_sprite));
	}
	
	private void updatePendents(float deltaTime) {
		if ((Boolean) touchInfo.get("ToquePendiente")) { // Se fija que haya algun toque pendiente de ser procesado que viene del frame anterior
			if (!(touchInfo.get("ToqueJuego") == null)){ // Se fija que la info del toque sea valida
				if (touchInfo.get("ToqueJuego").getClass().equals(Vector3.class)) {
					int seleccion_previa = -1; // por default la seleccion previa es nula 
					// Carga la seleccion previa para detectar cambios
					if (touchInfo.get("ImagenSeleccionada") == null) {
						seleccion_previa = -1;
					} else {
						seleccion_previa = (Integer) touchInfo.get("ImagenSeleccionada");
					}
					// Se fija cual es la seleccion actual
					Vector3 touchGame = (Vector3) touchInfo.get("ToqueJuego");
					// Revisa si el toque se superpone con alguna de las figuras
					int seleccion = -1; // establece en -1 que no se toco ninguna imagen
					for (int i = 0; i < contenidos.length; i++) { // itera sobre todos los contenidos (que son las imagenes de los dibujos)
						if (contenidos[i].getBoundingRectangle().contains(touchGame.x, touchGame.y)){
							Gdx.app.debug(TAG, "Ha tocado la imagen" + i);
							seleccion = i;
						}
					}
				
					// actualiza el valor de la imagen seleccionada
					if (seleccion == -1) { // Si no se toco ninguna imagen pone en null la imagen seleccionada
						touchInfo.put("ImagenSeleccionada", null);
					} else { // Si se toco una imagen la marca como la seleccionada
						touchInfo.put("ImagenSeleccionada", seleccion);						
					}
					
					// activa el cambio de seleccion si corresponde
					//if (!(seleccion==seleccion_previa)) {
					//	updateSelected();
					//}
					updateSelected();
				} else {
					Gdx.app.debug(TAG, "La informacion del toque a procesar no parece ser un vector");
				}
			} else {
				Gdx.app.debug(TAG, "La informacion del toque a procesar no parece estar cargada");
			}
			touchInfo.put("ToquePendiente", false);
		}
    	
	}

	private void updateSelected () {
		if (!(touchInfo.get("ImagenSeleccionada")== null)) {
			
			// Detiene el sonido actual
	    	sonidos[imageSelected].stop();
	    	// Resetea el tiempo del contador asociaciado a la seleccion actual
	    	time_selected = 0;
	    	// Pone la imagen de la seleccion actual (previo cambio) en la default
	    	animacionContorno[imageSelected].setRegion(Assets.instance.cuadrado.cuadrado);
	    	
	    	// cambia la imagen seleccionada
			imageSelected = (Integer) touchInfo.get("ImagenSeleccionada");

			if (cameraHelper.hasTarget()) { //REVISAR: no me queda muy claro que hace esto o para que sirve...
	    		cameraHelper.setTarget(animacionContorno[imageSelected]);
	    	}
	    	Gdx.app.debug(TAG, "Sprite #" + imageSelected + " selected");
	    	Id_sonido = sonidos[imageSelected].play();
	    	//sonidos[selectedSprite].setLooping(Id_sonido, true);
	    	
			
		}
	}
	
	@Override
	public boolean keyUp (int keycode) {
/*	
		// Reset game world
		if (keycode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}
		// Select next sprite
		else if (keycode == Keys.SPACE) {
			// Resetea el tiempo del contador asociaciado a la seleccion actual
			time_selected = 0;
			// Pone la imagen de la seleccion actual (previo cambio) en la default
			animacionContorno[imageSelected].setRegion(Assets.instance.cuadrado.cuadrado);
			// cambia la seleccion 
			imageSelected = (imageSelected + 1) % animacionContorno.length;
			// Update camera's target to follow the currently
			// selected sprite
			if (cameraHelper.hasTarget()) {
				cameraHelper.setTarget(animacionContorno[imageSelected]);
			}
			Gdx.app.debug(TAG, "Sprite #" + imageSelected + " selected");
		}
		// Toggle camera follow
		else if (keycode == Keys.ENTER) {
			cameraHelper.setTarget(cameraHelper.hasTarget() ? null :
			animacionContorno[imageSelected]);
			Gdx.app.debug(TAG, "Camera follow enabled: " +
			cameraHelper.hasTarget());
		}
*/
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
    	// Registra el toque en pantalla
    	touchInfo.put("UnproyectPendiente", true);
    	Vector3 touchScreenOld = new Vector3();
    	touchScreenOld.set(screenX, screenY, 0);
    	touchInfo.put("ToquePantalla", touchScreenOld);
    	
    	// Version mejorada
    	// Crea un evento de toque
    	TouchInfo touchData = new TouchInfo(time,time_selected);
    	// Crea el vector toque en pantalla y lo almacena
    	Vector3 touchScreen = new Vector3();
    	touchScreen.set(screenX, screenY, 0);
   		touchData.coordScreen = touchScreen;
    	
    	
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
}
	
	