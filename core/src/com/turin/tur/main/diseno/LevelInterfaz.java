package com.turin.tur.main.diseno;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.util.Assets;


public class LevelInterfaz {
	
	private static final String TAG = LevelInterfaz.class.getName();
	Array<Botones> botones = new Array<Botones>(); // conjunto de botones
	Level levelInfo;   
	int activeTrial; // Numero de trial que esta activo
	
	public LevelInterfaz (Level levelInfo, int activeTrial){
		this.levelInfo = levelInfo;
		this.activeTrial = activeTrial;
		if (this.activeTrial!=0) {botones.add(new BotonAnterior());}
		if (this.activeTrial!=this.levelInfo.secuenciaTrailsId.length-1) {botones.add(new BotonSiguiente());}
	}
	
	public void render(SpriteBatch batch) {
		for (Botones boton:botones) {
			boton.render(batch);
		}
	}
		
	

	public abstract class Botones {
		public Sprite imagen; // Imagen del boton
		public float tamano; // Tamano del boton
		public Vector2 posicionCenter = new Vector2(0, 0); // posicion del vector
		public float size;
		
		public abstract void toDo();
		public abstract void specificRender(SpriteBatch batch);
		
		public void render(SpriteBatch batch) {
			float x;
			float y;
			// Find the position of the main imagen and setup it
			x = posicionCenter.x - size / 2;
			y = posicionCenter.y - size / 2;
			imagen.setPosition(x, y);
			imagen.setSize(this.size, this.size);
			imagen.draw(batch);
			this.specificRender(batch);
		}
		
		public void setPosition(float x, float y) {
			this.posicionCenter.x = x;
			this.posicionCenter.y = y;
		}
		
	}

	public class BotonSiguiente extends Botones {
		
		public BotonSiguiente(){
			this.imagen = new Sprite(Assets.instance.imagenes.arrowLevel);
			this.setPosition(2f, 0);
			this.size=0.4f;
		}

		@Override
		public void toDo() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void specificRender(SpriteBatch batch) {
			// TODO Auto-generated method stub
			
		}


	}
	
	public class BotonAnterior extends Botones {

		public BotonAnterior(){
			this.imagen = new Sprite(Assets.instance.imagenes.arrowLevel);
			this.imagen.rotate90(true);
			this.imagen.rotate90(true);
			this.setPosition(-2f, 0);
			this.size=0.4f;
		}
		@Override
		public void toDo() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void specificRender(SpriteBatch batch) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
