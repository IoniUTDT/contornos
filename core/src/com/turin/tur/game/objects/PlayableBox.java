package com.turin.tur.game.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.turin.tur.game.Assets;
import com.turin.tur.util.Constants;

public class PlayableBox extends BoxContainer{

	public Boolean isSelected;
	public Boolean drawAnimation;
	public float avanceReproduccion;
	public float duracionReproduccion;
	public Sprite sprAnimacion;
	
	public PlayableBox(ExperimentalObject contenido) {
		super(contenido);
		this.isSelected = false;
		this.drawAnimation = false;
		this.sprAnimacion = new Sprite (Assets.instance.imagenes.animacionContorno);
		this.avanceReproduccion = 0;
		this.duracionReproduccion = Constants.Box.DURACION_REPRODUCCION_PREDETERMINADA;	}

	public void select() {
		this.drawAnimation = true;
		this.isSelected = true;
		Sound sonido = this.contenido.sonido;
		sonido.play();
	}


	public void unSelect() {
		this.drawAnimation = false;
		this.isSelected = false;
		this.contenido.sonido.stop();
		avanceReproduccion = 0; //reset the advance point of sound
	}

	@Override
	public void update(float deltaTime) {
		if (isSelected) {
			avanceReproduccion = avanceReproduccion + deltaTime;
			if (avanceReproduccion > duracionReproduccion) {
				unSelect();
			}
		}
	}


	public void specificRender(SpriteBatch batch) {
		// Find the position of the contour and stup it
		sprAnimacion.setSize(Constants.Box.TAMANO_CONTORNO_X,Constants.Box.TAMANO_CONTORNO_Y);
		float x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_X /2;
		float y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.TAMANO_CONTORNO_Y;
		float xShift = Constants.Box.TAMANO * avanceReproduccion / duracionReproduccion;
		sprAnimacion.setPosition(x + xShift, y);
		if (drawAnimation) {
			sprAnimacion.draw(batch);
		}
	}

	
	// REVISAR hay que hacer que cuando se seleccione este elemento haga algo diferente en cada BoxContainer
}
