package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.ImagesAsset;



public abstract class Boxes {

	/*
	 * La clase box es la que almacena la informacion visual, sonora, y espacial de que mostrar y donde en cada trial. Sirve como punto de interaccion basico del usuario con el programa 
	 */

	// Contantes
	public static final String TAG = Boxes.class.getName();
	
	public static abstract class Box {
		
		// Variable generales que definen a la caja
		public ExperimentalObject contenido; // Esta variable guarda toda la informacion del contenido de la caja usando una clase especialmente dise�ada para eso
		public Vector2 posicionCenter; // Esta es la posicion de la caja dada por las coordenadas de su centro. 
		public Sprite spr; // Guarda la imagen que se va a mostrar (se genera a partir del contenido de la caja)
	
		// Variables especificas de cada tipo pero que estan en la clase general porque se llaman desde afuera
		public boolean answerActive = false; // Determina si se esta mostrando la respuesta o no
		public boolean answer=false; // Resultado de la respuesta
		
		public void render(SpriteBatch batch) {
			// Render the main content of the box
			float x;
			float y;
			// Find the position of the main imagen and setup it
			spr.setSize(Constants.Box.TAMANO, Constants.Box.TAMANO);
			x = posicionCenter.x - Constants.Box.TAMANO / 2;
			y = posicionCenter.y - Constants.Box.TAMANO / 2;
			spr.setPosition(x, y);
			spr.draw(batch);
			specificRender(batch);
		}
			
		protected abstract void specificRender (SpriteBatch batch);
		protected abstract void update(float deltaTime);
		public abstract void select();
		public abstract void unSelect();
		
		public void SetPosition(float xCenter, float yCenter) {
			this.posicionCenter.x = xCenter;
			this.posicionCenter.y = yCenter;
		}
			
		protected long waitForLoadCompleted(Sound sound) {
	        long id;
	        while ((id = sound.play(0)) == -1) {
	            long t = TimeUtils.nanoTime();
	            while (TimeUtils.nanoTime() - t < 100000000);
	        }
	        return id;
	    }

	}
	
	public static class TrainingBox extends Box {
	
		// Variables utiles para las cajas que son reproducibles
		public boolean runningSound; // Determina si se esta reproduciendo un sonido (para activar o no la animacion correspondiente)
		private float soundAvanceReproduccion; //Avance la reproduccion
		private float soundDuracionReproduccion; //Tiempo total establecido para el sonido (ojo que no es necesariamente el tiempo total del sonido, pero se trabaja con sonidos a priori de longitud fija 
		private Sprite soundAnimationSpr; // imagen para mostrar la animacion de reproduccion del sonido 

		public TrainingBox (ExperimentalObject contenido) {
			
			// Carga cosas relacionadas al contenido
			this.contenido = contenido;
			this.posicionCenter = new Vector2(0, 0);
			this.spr = this.contenido.imagen;
			
			// inicializa las variables que manejan la reproduccion del sonido
			this.runningSound = false;
			this.soundAvanceReproduccion = 0;
			this.soundDuracionReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA;	
			this.createSoundAnimationResources();
			
		}

		private void createSoundAnimationResources() {
			Pixmap pixmap = new Pixmap(10, 10, Format.RGBA8888);
			pixmap.setColor(0, 0, 0, 1);
			pixmap.fill();
			Texture texture = new Texture(pixmap);
			this.soundAnimationSpr = new Sprite (texture);
		}

	
		protected void specificRender(SpriteBatch batch) {
			// Render the animation of the box
			if (runningSound) {
				soundAnimationSpr.setSize(Constants.Box.TAMANO_CONTORNO_X,Constants.Box.TAMANO_CONTORNO_Y);
				float x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_X /2;
				float y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_Y;
				float xShift = Constants.Box.TAMANO * soundAvanceReproduccion / soundDuracionReproduccion;
				soundAnimationSpr.setPosition(x + xShift, y);
				soundAnimationSpr.draw(batch);
			}
		}

		public void update(float deltaTime) {
			if (runningSound) {
				this.soundAvanceReproduccion += deltaTime;
				if (this.soundAvanceReproduccion > this.soundDuracionReproduccion) {
					this.unSelect();
				}
			}
			
		}

		public void unSelect() {
			Gdx.app.debug(TAG, "Ha deseleccionado la imagen " + this.contenido.Id);
			this.runningSound = false;
			this.contenido.sonido.stop();
			soundAvanceReproduccion = 0; //reset the advance point of sound animation
		}
		
		public void select() {
			Gdx.app.debug(TAG, "Ha seleccionado la imagen " + this.contenido.Id);
			this.runningSound = true;
			this.soundAvanceReproduccion = 0;
			Sound sonido = this.contenido.sonido;
			waitForLoadCompleted(this.contenido.sonido);
			sonido.play();
		}
	}
		
	public static class AnswerBox extends Box {

		private float answerAnimationAdvance = 0; // Avance el la animacion de respuesta
		private float answerAnimationTime = Constants.Box.ANIMATION_ANSWER_TIME; // Tiempo predeterminado de muestra del estimulo de respuesta (si no se desa feedback se debe setear esto en cero)
		private Sprite answerUsedSprite; // Imagen con que se muestra la respuesta 
		private Sprite answerSprTrue; // Imagen para respuestas verdaderas
		private Sprite answerSprFalse; // Imagen para respuestas falsas
		
		public AnswerBox (ExperimentalObject contenido){
			// Carga cosas relacionadas al contenido
			this.contenido = contenido;
			this.posicionCenter = new Vector2(0, 0);
			this.spr = this.contenido.imagen;
			
			// inicializa las variables relacionadas a la dinamica de la respuesta
			this.createAnswerAnimationResources();
		}

		public void update(float deltaTime) {
			this.answerAnimationAdvance += deltaTime;
			if (answerAnimationAdvance > Constants.Box.ANIMATION_ANSWER_TIME) {
				this.answerActive = false;
			}
			
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

		private void createAnswerAnimationResources() {
			Pixmap pixmapTrue = createAnswerResources(true);
			Pixmap pixmapFalse = createAnswerResources(false);
			Texture textureTrue = new Texture(pixmapTrue);
			Texture textureFalse = new Texture(pixmapFalse);
			this.answerSprTrue = new Sprite(textureTrue);
			this.answerSprFalse = new Sprite(textureFalse);
		}

		public void select(){
			Gdx.app.debug(TAG, "Ha seleccionado la imagen " + this.contenido.Id);
			this.answerActive = true;
			this.answerAnimationAdvance = 0;
		}
		
		public void unSelect() {
			Gdx.app.debug(TAG, "Ha deseleccionado la imagen " + this.contenido.Id);
			this.answerActive = false;
			this.answerAnimationAdvance = 0;
		}
		
		protected void specificRender(SpriteBatch batch) {
			if (this.answerActive) {this.contourRender(batch);}
		}
		
		private void contourRender(SpriteBatch batch) {
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
	
	public static class StimuliBox extends Box {
		
		private float stimuliAvanceReproduccion; // Avance en la reproduccion del estimulo (incluye el tiempo entre loops)
		private float stimuliDuracionReproduccion; // Tiempo total que se supone que dura el sonido
		private Sprite stimuliAnimationSpr; // Sprite para la animacion del sonido 
		private boolean drawStimuli; // Variable que determina si se debe dibujar o no (cuando llega al fin del sonido deba de dibujar)

		public StimuliBox (ExperimentalObject contenido) {
			
			this.contenido = contenido;
			this.posicionCenter = new Vector2(0, 0);
			this.spr = new Sprite (Assets.instance.imagenes.stimuliLogo);
		
			// this.drawStimuli=false;
			// inicializa el tiempo de modo q se resetee apenas empieza
			this.stimuliAvanceReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA + Constants.Box.DELAY_ESTIMULO_MODO_SELECCIONAR;
			this.stimuliDuracionReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA;	
			// this.stimuliActive = true;
			
			this.createSoundAnimationResources();
		}

		
		protected void specificRender(SpriteBatch batch) {
			if (this.drawStimuli) {
				stimuliAnimationSpr.setSize(Constants.Box.TAMANO_CONTORNO_X,Constants.Box.TAMANO_CONTORNO_Y);
				float x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_X /2;
				float y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_Y;
				float xShift = Constants.Box.TAMANO * stimuliAvanceReproduccion / stimuliDuracionReproduccion;
				stimuliAnimationSpr.setPosition(x + xShift, y);
				stimuliAnimationSpr.draw(batch);
			}
		}

		
		protected void update(float deltaTime) {
			stimuliAvanceReproduccion = stimuliAvanceReproduccion + deltaTime;
			if (stimuliAvanceReproduccion > stimuliDuracionReproduccion) {
				this.drawStimuli=false;
			}
			if (stimuliAvanceReproduccion > stimuliDuracionReproduccion + Constants.Box.DELAY_ESTIMULO_MODO_SELECCIONAR) {
				this.drawStimuli=true;
				stimuliAvanceReproduccion = 0; //reset the advance point of sound
				waitForLoadCompleted(this.contenido.sonido);
				this.contenido.sonido.play();
			}
			
		}

		public void select() {} // No hace nada

		public void unSelect() {} // No hace nada
		
		private void createSoundAnimationResources() {
			Pixmap pixmap = new Pixmap(10, 10, Format.RGBA8888);
			pixmap.setColor(0, 0, 0, 1);
			pixmap.fill();
			Texture texture = new Texture(pixmap);
			this.stimuliAnimationSpr = new Sprite (texture);
		}

	}
}	
		
/*
/*
		



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
			if (runingSound) {
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
		if ((runingSound) & (this.tipoDeCaja.reproducible)) {
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
				waitForLoadCompleted(this.contenido.sonido);
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
			this.runingSound = true;
			Sound sonido = this.contenido.sonido;
			waitForLoadCompleted(this.contenido.sonido);
			sonido.play();
		}
	}

	public void unSelect() {
		Gdx.app.debug(TAG, "Ha deseleccionado la imagen " + this.contenido.Id);
		this.answerActive = false;
		this.runingSound = false;
		this.contenido.sonido.stop();
		soundAvanceReproduccion = 0; //reset the advance point of sound animation
		if (stopStimuli) {
			stimuliAvanceReproduccion = 0;
			stimuliActive = false;
			drawStimuli = false;
		}
	}


    private long waitForLoadCompleted(Sound sound) {
        long id;
        while ((id = sound.play(0)) == -1) {
            long t = TimeUtils.nanoTime();
            while (TimeUtils.nanoTime() - t < 100000000);
        }
        return id;
    }
} */
