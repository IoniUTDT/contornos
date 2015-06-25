package com.turin.tur.main.objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.turin.tur.main.Assets;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeCAJA;



public class Box {

	public ExperimentalObject contenido;
	public Vector2 posicionCenter;
	public TIPOdeCAJA tipoDeCaja;
	public Sprite spr;
	
	public Box (ExperimentalObject contenido, TIPOdeCAJA tipoDeCaja) {
		this.contenido = contenido;
		this.posicionCenter = new Vector2(0,0);
		this.tipoDeCaja = tipoDeCaja;
		if (this.tipoDeCaja.mostrarContenido) {
			this.spr = this.contenido.imagen;
		} else {
			this.spr = new Sprite (Assets.instance.imagenes.stimuliLogo);
		}
	}

	
	public void SetPosition (float xCenter, float yCenter) {
		this.posicionCenter.x = xCenter;
		this.posicionCenter.y = yCenter;
	}
	
	public void render(SpriteBatch batch) {
		float x;
		float y;
		// Find the position of the main imagen and setup it
		spr.setSize(Constants.Box.TAMANO,Constants.Box.TAMANO);
		x = posicionCenter.x - Constants.Box.TAMANO/2;
		y = posicionCenter.y - Constants.Box.TAMANO/2;
		spr.setPosition(x, y);
		spr.draw(batch);
		this.specificRender(batch);
	}


	public void specificRender(SpriteBatch batch) {
		// TODO Auto-generated method stub
	}

	public void update (float deltaTime) {};
	
	public void select(){};
	public void unSelect(){};
	
}
