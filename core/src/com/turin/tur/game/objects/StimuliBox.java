package com.turin.tur.game.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.turin.tur.game.Assets;
import com.turin.tur.util.Constants;

public class StimuliBox extends PlayableBox{

	public boolean wait=false;
	private Sound sonido;
	
	public StimuliBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = new Sprite (Assets.instance.imagenes.stimuliLogo);
		sonido = this.contenido.sonido;
		this.select();
	}

	@Override 
	public void update(float deltaTime) {
		if ((wait == true) & (avanceReproduccion >0.1)) {
			sonido.play();
			wait = false;
		}
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
		wait = true;
	}

	@Override
	public void unSelect() {
		this.contenido.sonido.stop();
	}
	

	// REVISAR hay que hacer que cuando se seleccione este elemento haga algo diferente en cada BoxContainer
}
