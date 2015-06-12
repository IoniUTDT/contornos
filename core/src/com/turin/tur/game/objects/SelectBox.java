package com.turin.tur.game.objects;

public class SelectBox extends BoxContainer{

	public SelectBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = this.contenido.imagen;
	}

	@Override
	public void Select() {
		
	}

	@Override
	public void unSelect() {
	}

	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
	}


	
	// REVISAR hay que hacer que cuando se seleccione este elemento haga algo diferente en cada BoxContainer
}
