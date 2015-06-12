package com.turin.tur.game.objects;

import com.badlogic.gdx.audio.Sound;

public class ImageBox extends BoxContainer{

	public ImageBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = this.contenido.imagen;
	}

	public void Select () {
		this.drawAnimation = true;
		this.isSelected = true;
		Sound sonido = this.contenido.sonido;
		sonido.play();
	}
	
	public void unSelect () {
		this.drawAnimation = false;
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
}
