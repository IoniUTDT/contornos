package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeCAJA;

public class Box {

	public ExperimentalObject contenido;
	public Vector2 posicionCenter;
	public TIPOdeCAJA tipoDeCaja;
	public Sprite spr;
	public boolean answerActive;
	private float answerAnimationAdvance;
	private Sprite answerSprTrue;
	private Sprite answerSprFalse;
	public boolean answer;
	private Sprite answerUsedSprite;
	private boolean runSound;
	private float soundAvanceReproduccion;
	private float soundDuracionReproduccion;
	private Sprite soundAnimationSpr;
	private float stimuliAvanceReproduccion;
	private float stimuliDuracionReproduccion;
	private boolean drawStimuli;
	public boolean stimuliActive;
	private boolean stopStimuli=true; // sirve para activar que se pueda detener el estimulo, por ahora esta siempre en false
	// OJO q aca hay un problema conceptual xq el touch detecta el sprite tocado y es el mismo en la box de la serie que en la box del estimulo!
	// con lo cual todos los eventos que se detectan y se guardan tienen que ver con la box de la serie!
	
	public static final String TAG = Box.class.getName();

	public Box(ExperimentalObject contenido, TIPOdeCAJA tipoDeCaja) {
		
		this.contenido = contenido;
		this.posicionCenter = new Vector2(0, 0);
		this.tipoDeCaja = tipoDeCaja;
		if (this.tipoDeCaja.mostrarContenido) {
			this.spr = this.contenido.imagen;
		} else {
			this.spr = new Sprite(Assets.instance.imagenes.stimuliLogo);			
		}
		// inicializa las variables que manejan las respuestas
		this.answerActive = false;
		this.answerAnimationAdvance = 0;
		createAnswerAnimationResources();
		this.answer=false;
		// inicializa las variables que manejan la reproduccion del sonido
		this.runSound = false;
		this.soundAvanceReproduccion = 0;
		this.soundDuracionReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA;	
		createSoundAnimationResources();
		// inicializa las variables que manejan los estimulos
		this.drawStimuli=false;
		// inicializa el tiempo de modo q se resetee apenas empieza
		this.stimuliAvanceReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA + Constants.Box.DELAY_ESTIMULO_MODO_SELECCIONAR;
		this.stimuliDuracionReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA;	
		this.stimuliActive = true;

	}

	private void createSoundAnimationResources() {
		Pixmap pixmap = new Pixmap(10, 10, Format.RGBA8888);
		pixmap.setColor(0, 0, 0, 1);
		pixmap.fill();
		Texture texture = new Texture(pixmap);
		this.soundAnimationSpr = new Sprite (texture);
	}

	private void createAnswerAnimationResources() {
		Pixmap pixmapTrue = createAnswerResources(true);
		Pixmap pixmapFalse = createAnswerResources(false);
		Texture textureTrue = new Texture(pixmapTrue);
		Texture textureFalse = new Texture(pixmapFalse);
		answerSprTrue = new Sprite(textureTrue);
		answerSprFalse = new Sprite(textureFalse);
	}

	private Pixmap createAnswerResources(boolean condicion) {
		Pixmap pixmap = new Pixmap(10, 10, Format.RGBA8888);
		if (condicion) {
			pixmap.setColor(0, 1, 0, 1);
		} else {
			pixmap.setColor(1, 0, 0, 1);
		}
		// crea un cuadrado relleno
		pixmap.fill();
		return pixmap;
	}

	public void SetPosition(float xCenter, float yCenter) {
		this.posicionCenter.x = xCenter;
		this.posicionCenter.y = yCenter;
	}

	public void render(SpriteBatch batch) {
		float x;
		float y;
		// Find the position of the main imagen and setup it
		spr.setSize(Constants.Box.TAMANO, Constants.Box.TAMANO);
		x = posicionCenter.x - Constants.Box.TAMANO / 2;
		y = posicionCenter.y - Constants.Box.TAMANO / 2;
		spr.setPosition(x, y);
		spr.draw(batch);
		this.specificRender(batch);
	}

	public void specificRender(SpriteBatch batch) {
		if ((this.answerActive) & (this.tipoDeCaja.seleccionable)) {
			this.contourRender(batch);
		}
		if (this.tipoDeCaja.reproducible) {
			// Find the position of the contour and stup it
			soundAnimationSpr.setSize(Constants.Box.TAMANO_CONTORNO_X,Constants.Box.TAMANO_CONTORNO_Y);
			float x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_X /2;
			float y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_Y;
			float xShift = Constants.Box.TAMANO * soundAvanceReproduccion / soundDuracionReproduccion;
			soundAnimationSpr.setPosition(x + xShift, y);
			if (runSound) {
				soundAnimationSpr.draw(batch);
			}	
		}
		
		if (this.tipoDeCaja.reproduccionAutomatica) {
			// Find the position of the contour and stup it
			soundAnimationSpr.setSize(Constants.Box.TAMANO_CONTORNO_X,Constants.Box.TAMANO_CONTORNO_Y);
			float x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_X /2;
			float y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_Y;
			float xShift = Constants.Box.TAMANO * stimuliAvanceReproduccion / stimuliDuracionReproduccion;
			soundAnimationSpr.setPosition(x + xShift, y);
			if (drawStimuli) {
				soundAnimationSpr.draw(batch);
			}	
		}

	}

	public void update(float deltaTime) {
		if ((answerActive) & (this.tipoDeCaja.seleccionable)) {
			this.answerAnimationAdvance += deltaTime;
			// Gdx.app.debug(TAG, "Tiempo de answer: " + deltaTime);
			if (answerAnimationAdvance > Constants.Box.ANIMATION_ANSWER_TIME) {
				this.answerActive = false;
			}
		}
		if ((runSound) & (this.tipoDeCaja.reproducible)) {
			soundAvanceReproduccion = soundAvanceReproduccion + deltaTime;
			if (soundAvanceReproduccion > soundDuracionReproduccion) {
				unSelect();
			}
		}
		if ((this.tipoDeCaja.reproduccionAutomatica) & (stimuliActive)){
			stimuliAvanceReproduccion = stimuliAvanceReproduccion + deltaTime;
			if (stimuliAvanceReproduccion > stimuliDuracionReproduccion) {
				this.drawStimuli=false;
			}
			if (stimuliAvanceReproduccion > stimuliDuracionReproduccion + Constants.Box.DELAY_ESTIMULO_MODO_SELECCIONAR) {
				this.drawStimuli=true;
				stimuliAvanceReproduccion = 0; //reset the advance point of sound
				this.contenido.sonido.play();
			}
		}
	}

	public void select() {
		Gdx.app.debug(TAG, "Ha seleccionado la imagen " + this.contenido.Id);
		if (this.tipoDeCaja.seleccionable) {
			this.answerActive = true;
			this.answerAnimationAdvance = 0;
		}
		if (this.tipoDeCaja.reproducible) {
			this.runSound = true;
			Sound sonido = this.contenido.sonido;
			sonido.play();
		}
	}

	public void unSelect() {
		Gdx.app.debug(TAG, "Ha deseleccionado la imagen " + this.contenido.Id);
		this.answerActive = false;
		this.runSound = false;
		this.contenido.sonido.stop();
		soundAvanceReproduccion = 0; //reset the advance point of sound animation
		if (stopStimuli) {
			stimuliAvanceReproduccion = 0;
			stimuliActive = false;
			drawStimuli = false;
		}
	}

	public void contourRender(SpriteBatch batch) {
		float x;
		float y;
		if (this.answer) {
			answerUsedSprite = answerSprTrue;
		} else {
			answerUsedSprite = answerSprFalse;
		}
		// dibuja las esquinas
		answerUsedSprite.setSize(Constants.Box.SELECT_BOX_ANCHO_RTA,
				Constants.Box.SELECT_BOX_ANCHO_RTA);
		// sup izq
		x = posicionCenter.x - Constants.Box.TAMANO / 2
				- Constants.Box.SELECT_BOX_ANCHO_RTA;
		y = posicionCenter.y - Constants.Box.TAMANO / 2
				- Constants.Box.SELECT_BOX_ANCHO_RTA;
		answerUsedSprite.setPosition(x, y);
		answerUsedSprite.draw(batch);
		// sup der
		x = posicionCenter.x + Constants.Box.TAMANO / 2;
		y = posicionCenter.y - Constants.Box.TAMANO / 2
				- Constants.Box.SELECT_BOX_ANCHO_RTA;
		answerUsedSprite.setPosition(x, y);
		answerUsedSprite.draw(batch);
		// inf izq
		x = posicionCenter.x - Constants.Box.TAMANO / 2
				- Constants.Box.SELECT_BOX_ANCHO_RTA;
		y = posicionCenter.y + Constants.Box.TAMANO / 2;
		answerUsedSprite.setPosition(x, y);
		answerUsedSprite.draw(batch);
		// inf der
		x = posicionCenter.x + Constants.Box.TAMANO / 2;
		y = posicionCenter.y + Constants.Box.TAMANO / 2;
		answerUsedSprite.setPosition(x, y);
		answerUsedSprite.draw(batch);
		// dibuja los bordes
		// verticales
		answerUsedSprite.setSize(Constants.Box.SELECT_BOX_ANCHO_RTA,
				Constants.Box.TAMANO);
		// izq
		x = posicionCenter.x - Constants.Box.TAMANO / 2
				- Constants.Box.SELECT_BOX_ANCHO_RTA;
		y = posicionCenter.y - Constants.Box.TAMANO / 2;
		answerUsedSprite.setPosition(x, y);
		answerUsedSprite.draw(batch);
		// der
		x = posicionCenter.x + Constants.Box.TAMANO / 2;
		y = posicionCenter.y - Constants.Box.TAMANO / 2;
		answerUsedSprite.setPosition(x, y);
		answerUsedSprite.draw(batch);
		// horizontal
		answerUsedSprite.setSize(Constants.Box.TAMANO,
				Constants.Box.SELECT_BOX_ANCHO_RTA);
		// arriba
		x = posicionCenter.x - Constants.Box.TAMANO / 2;
		y = posicionCenter.y - Constants.Box.TAMANO / 2
				- Constants.Box.SELECT_BOX_ANCHO_RTA;
		answerUsedSprite.setPosition(x, y);
		answerUsedSprite.draw(batch);
		// abajo
		x = posicionCenter.x - Constants.Box.TAMANO / 2;
		y = posicionCenter.y + Constants.Box.TAMANO / 2;
		answerUsedSprite.setPosition(x, y);
		answerUsedSprite.draw(batch);
	}

}
