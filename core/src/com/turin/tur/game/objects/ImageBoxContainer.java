package com.turin.tur.game.objects;

public class ImageBoxContainer extends BoxContainer{

	public ImageBoxContainer(ExperimentalObject contenido) {
		super(contenido);
		this.spr = this.contenido.imagen;
	}

	
}
