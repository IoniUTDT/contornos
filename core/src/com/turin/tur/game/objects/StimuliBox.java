package com.turin.tur.game.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.turin.tur.game.Assets;
import com.turin.tur.util.Constants;

public class StimuliBox extends PlayableBox{

	public StimuliBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = new Sprite (Assets.instance.imagenes.stimuliLogo);
		this.select();
	}

	@Override 
	public void update(float deltaTime) {
		avanceReproduccion = avanceReproduccion + deltaTime;
		if (avanceReproduccion > duracionReproduccion) {
			this.drawAnimation=false;
		}
		if (avanceReproduccion > duracionReproduccion + Constants.Box.DELAY_ESTIMULO_MODO_SELECCIONAR) {
			avanceReproduccion = 0; //reset the advance point of sound
			this.select();
		}		
	}

	@Override
	public void select() {
		this.drawAnimation = true;
		Sound sonido = this.contenido.sonido;
		sonido.play();
	}

	@Override
	public void unSelect() {}
	

	// REVISAR hay que hacer que cuando se seleccione este elemento haga algo diferente en cada BoxContainer
}
