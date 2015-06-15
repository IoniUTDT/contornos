package com.turin.tur.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.turin.tur.game.Assets;

public class SoundBox extends PlayableBox{

	public SoundBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = new Sprite (Assets.instance.imagenes.logoAudio);
	}
}
