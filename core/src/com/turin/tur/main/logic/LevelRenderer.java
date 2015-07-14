package com.turin.tur.main.logic;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.turin.tur.main.diseno.Boxes;
import com.turin.tur.main.diseno.LevelInterfaz;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.LevelInterfaz.Botones;
import com.turin.tur.main.util.Constants;

public class LevelRenderer implements Disposable {

	private static final String TAG = LevelRenderer.class.getName();
	public OrthographicCamera camera;
	private SpriteBatch batch;
	private LevelController levelController;
	private OrthographicCamera cameraGUI;
	
	public LevelRenderer(LevelController levelController) {
		this.levelController = levelController;
		camera = levelController.camera;
		init();
	}

	private void init() {
		batch = new SpriteBatch();
		camera.position.set(0, 0, 0);
		camera.update();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
	}

	public void render() {
		renderBoxObjects();
		renderGui(batch);
		
	}

	private void renderInterfaz(SpriteBatch batch) {
		
		 for (Botones boton:levelController.levelInterfaz.botones) {
			boton.render(batch);
		}
		
	}

	private void renderGui(SpriteBatch batch) { // estoy reemplazando cameraGUI por camera
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		this.levelController.levelInterfaz.renderFps(batch,cameraGUI);
		this.levelController.levelInterfaz.renderTitle(batch, cameraGUI);
		batch.end();
	}


	private void renderBoxObjects() {
		levelController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for (Box box : levelController.trialActive.allBox) {
			box.render(batch);
		}
		
		renderInterfaz(batch);
		
		batch.end();
		
	}

	public void resize(int width, int height) {
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) *
				width;
		camera.update();
		
	}

	public void dispose() {
		batch.dispose();	
	}

}
