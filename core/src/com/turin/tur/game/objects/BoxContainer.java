package com.turin.tur.game.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.turin.tur.game.Assets;
import com.turin.tur.util.Constants;

public abstract class BoxContainer {

	public ExperimentalObject contenido;
	public Vector2 posicionCenter;
	public Sprite spr;
	
	public BoxContainer (ExperimentalObject contenido) {
		this.contenido = contenido;
		this.spr = new Sprite (Assets.instance.imagenes.logoAudio);
		this.posicionCenter = new Vector2(0,0);
	}

	public void SetPosition (float xCenter, float yCenter) {
		this.posicionCenter.x = xCenter;
		this.posicionCenter.y = yCenter;
	}
	
	public abstract void update(float deltaTime);
	
	public void render(SpriteBatch batch) {
		float x;
		float y;
		// Find the position of the main imagen and setup it
		spr.setSize(Constants.Box.TAMANO,Constants.Box.TAMANO);
		x = posicionCenter.x - Constants.Box.TAMANO/2;
		y = posicionCenter.y - Constants.Box.TAMANO/2;
		spr.setPosition(x, y);
		this.specificRender(batch);
	}

	public abstract void specificRender(SpriteBatch batch);
	
	public abstract void select();
	public abstract void unSelect();
	
}
