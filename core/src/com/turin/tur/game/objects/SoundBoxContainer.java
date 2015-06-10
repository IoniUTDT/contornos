package com.turin.tur.game.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.turin.tur.game.Assets;
import com.turin.tur.util.Constants;

public class SoundBoxContainer {

	private ExperimentalObject contenido;
	private Boolean isSelected;
	private float avanceReproduccion;
	private float duracionReproduccion;
	private Vector2 posicionCenter;
	private Sprite spr;
	private Sprite sprAnimacion;
	
	public SoundBoxContainer (ExperimentalObject contenido) {
		this.contenido = contenido;
		this.isSelected = false;
		this.spr = new Sprite (Assets.instance.imagenes.logoAudio);
		this.sprAnimacion = new Sprite (Assets.instance.imagenes.animacionContorno);
		this.avanceReproduccion = 0;
		this.duracionReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA;
		this.posicionCenter = new Vector2(0,0);
	}

	public void SetPosition (int xCenter, int yCenter) {
		this.posicionCenter.x = xCenter;
		this.posicionCenter.y = yCenter;
	}
	
	public void Select () {
		this.isSelected = true;
		Sound sonido = this.contenido.sonido;
		sonido.play();
	}
	
	public void unSelect () {
		this.isSelected = false;
		this.contenido.sonido.stop();
		avanceReproduccion = 0; //reset the advance point of sound
	}
	
	public void update(float deltaTime) {
		if (isSelected) {
			avanceReproduccion = avanceReproduccion + deltaTime;
			if (avanceReproduccion > duracionReproduccion) {
				unSelect();
			}
		}
	}

	public void render(SpriteBatch batch) {
		float x;
		float y;
		float xShift;
		// Find the position of the main imagen and setup it
		spr.setSize(Constants.Box.TAMANO,Constants.Box.TAMANO);
		x = posicionCenter.x - Constants.Box.TAMANO/2;
		y = posicionCenter.y - Constants.Box.TAMANO/2;
		spr.setPosition(x, y);
		// Find the position of the contour and stup it
		sprAnimacion.setSize(Constants.Box.TAMANO_CONTORNO_X,Constants.Box.TAMANO_CONTORNO_Y);
		x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_X /2;
		y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_Y;
		xShift =  Constants.Box.TAMANO * avanceReproduccion / duracionReproduccion;
		sprAnimacion.setPosition(x + xShift, y);
		// Draws
		spr.draw(batch);
		sprAnimacion.draw(batch);
	}

}
