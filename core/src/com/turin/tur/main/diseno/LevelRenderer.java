package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.turin.tur.main.Assets;
import com.turin.tur.main.objects.Box;
import com.turin.tur.main.util.Constants;

public class LevelRenderer implements Disposable {

	private static final String TAG = TrialRenderer.class.getName();
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

	private void renderGui(SpriteBatch batch) {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		// draw collected gold coins icon + text
		// (anchored to top left edge)
		renderGuiLevelInfo(batch);
		// draw FPS text (anchored to bottom right edge)
		renderGuiFpsCounter(batch);
		batch.end();
	}

	private void renderGuiFpsCounter(SpriteBatch batch) {
		float x = cameraGUI.viewportWidth - 130;
		float y = cameraGUI.viewportHeight - 30;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultFont;
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

	private void renderGuiLevelInfo(SpriteBatch batch) {
		float x = Constants.VIEWPORT_GUI_WIDTH/2 - 100; //DISENO
		float y = 30; //DISENO
		//Assets.instance.fonts.defaultFont.draw(batch, trialController.trial.levelTitle, x, y, 200, 1, true); //DISENO NOTA: la alineacion es una constante al parecer el 1 es centrado, el 0 izq y el 3 der
		
	}

	private void renderBoxObjects() {
		levelController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		for (Box box : levelController.trialActive.boxes) {
			box.render(batch);
		}
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
