package com.turin.tur.main.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.turin.tur.main.Assets;

public class SoundBox extends PlayableBox{

	public SoundBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = new Sprite (Assets.instance.imagenes.logoAudio);
	}
}
