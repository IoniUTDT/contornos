package com.turin.tur.game.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.turin.tur.game.Assets;
import com.turin.tur.util.Constants;

public class BoxContainer {

	public ExperimentalObject contenido;
	public Boolean isSelected;
	public float avanceReproduccion;
	public float duracionReproduccion;
	public Vector2 posicionCenter;
	public Sprite spr;
	public Sprite sprAnimacion;
	
	public BoxContainer (ExperimentalObject contenido) {
		this.contenido = contenido;
		this.isSelected = false;
		this.spr = new Sprite (Assets.instance.imagenes.logoAudio);
		this.sprAnimacion = new Sprite (Assets.instance.imagenes.animacionContorno);
		this.avanceReproduccion = 0;
		this.duracionReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA;
		this.posicionCenter = new Vector2(0,0);
	}

	public void SetPosition (float xCenter, float yCenter) {
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
		if (isSelected) {
			sprAnimacion.draw(batch);
		}
	}

}
