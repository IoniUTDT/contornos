package com.turin.tur.main.objects;

import com.turin.tur.main.diseno.ExperimentalObject;

public class ImageBox extends PlayableBox{

	public ImageBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = this.contenido.imagen;
	}




}
