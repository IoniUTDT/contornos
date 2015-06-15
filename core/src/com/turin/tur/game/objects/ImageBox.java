package com.turin.tur.game.objects;

public class ImageBox extends PlayableBox{

	public ImageBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = this.contenido.imagen;
	}




}
