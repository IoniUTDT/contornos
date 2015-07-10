package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.Constants;


public class LevelInterfaz {
	
	private static final String TAG = LevelInterfaz.class.getName();
	public Array<Botones> botones = new Array<Botones>(); // conjunto de botones
	private Level levelInfo;   
	private int trialNumber; // Numero de trial que esta activo
	private Trial trialActive;
	
	public LevelInterfaz (Level levelInfo, int trialNumber, Trial trialActive){
		this.trialActive = trialActive;
		this.levelInfo = levelInfo;
		this.trialNumber = trialNumber;
		if (this.trialNumber!=0) {botones.add(new BotonAnterior());}
		if (this.trialNumber!=this.levelInfo.secuenciaTrailsId.length-1) {botones.add(new BotonSiguiente());}
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
			this.setPosition(3.5f, 0);
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
			this.setPosition(-3.5f, 0);
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

	public void renderFps(SpriteBatch batch, OrthographicCamera cameraGUI) {
		float x = cameraGUI.viewportWidth - 70;
		float y = cameraGUI.viewportHeight - 30;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultSmallFont;
		if (fps >= 45) {
			// 45 or more FPS show up in green
			fpsFont.setColor(0, 1, 0, 1);
		} else if (fps >= 30) {
			// 30 or more FPS show up in yellow
			fpsFont.setColor(1, 1, 0, 1);
		} else {
			// less than 30 FPS show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
		
	}
	
	public void renderTitle (SpriteBatch batch, OrthographicCamera cameraGUI) {
		Assets.instance.fonts.defaultSmallFont.draw(batch, levelInfo.levelTitle, cameraGUI.viewportWidth/5 , 50); 
		Assets.instance.fonts.defaultSmallFont.draw(batch, trialActive.title , cameraGUI.viewportWidth/5*2 , 50);
		Assets.instance.fonts.defaultSmallFont.draw(batch, "Trial #" + trialNumber + " Id: "+ trialActive.Id , cameraGUI.viewportWidth/5 *4, 50); 
	}
}
