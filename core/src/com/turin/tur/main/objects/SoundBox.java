package com.turin.tur.main.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.turin.tur.main.Assets;
import com.turin.tur.main.diseno.ExperimentalObject;

public class SoundBox extends PlayableBox{

	public SoundBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = new Sprite (Assets.instance.imagenes.logoAudio);
	}
}
