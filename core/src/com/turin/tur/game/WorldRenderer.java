package com.turin.tur.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.turin.tur.util.Constants;

public class WorldRenderer implements Disposable {

	private static final String TAG = WorldRenderer.class.getName();
	public OrthographicCamera camera;
	private SpriteBatch batch;
	private WorldController worldController;

	public WorldRenderer (WorldController worldController) { 
		this.worldController = worldController;
		camera = worldController.camera;
		init();
	}

	private void init () { 
		batch = new SpriteBatch();
		//camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
	}
	
	public void render () { 
		renderTestObjects();
		if ((Boolean) worldController.touchInfo.get("UnproyectPendiente")) {
			processTouch ();
		}
	}

	private void renderTestObjects() {
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(Sprite sprite : worldController.animacionContorno) {
			sprite.draw(batch);
		}
		for(Sprite sprite : worldController.contenidos) {
			sprite.setOriginCenter();
			sprite.draw(batch);
		}
		batch.end();
	}
	
	public void resize (int width, int height) { 
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) *
				width;
		camera.update();
	}
	
	@Override 
	public void dispose () { 
		batch.dispose();
	}
	
	private void processTouch () {
		if (!(worldController.touchInfo.get("ToquePantalla") == null)){
			if (worldController.touchInfo.get("ToquePantalla").getClass().equals(Vector3.class)) {
				Vector3 touchScreen = (Vector3) worldController.touchInfo.get("ToquePantalla");
				Vector3 touchGame = camera.unproject(touchScreen); 
				worldController.touchInfo.put("ToqueJuego", touchGame);
				worldController.touchInfo.put("UnproyectPendiente", false);
				worldController.touchInfo.put("ToquePendiente", true);
			} else {
				Gdx.app.debug(TAG, "Error! se intenta desproyectar una coordenada que no es un vector");
			}
		} else {
			Gdx.app.debug(TAG, "Error! se intenta desproyectar una coordenada null");
		}
	}
}