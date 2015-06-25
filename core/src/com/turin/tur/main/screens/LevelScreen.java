package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.turin.tur.main.diseno.TrialController;
import com.turin.tur.main.diseno.TrialRenderer;


public class LevelScreen extends AbstractGameScreen  {
	
	// Constantes 
	private static final String TAG = LevelScreen.class.getName();
	
	// Clases que se crean para manipular el contenido
	private TrialController levelController;
	private TrialRenderer levelRenderer;
	
	
	private boolean paused;
	
	public LevelScreen (Game game) {
		super(game);
	}

	@Override
	public void render (float deltaTime) {
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			levelController.update(deltaTime);
		}
		// Sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f,0xed /
				255.0f, 0xff / 255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render game world to screen
		levelRenderer.render();
	}
	
	@Override
	public void resize (int width, int height) {
		levelRenderer.resize(width, height);
	}

	@Override
	public void show () {
	    Gdx.app.debug(TAG, "Level");
	    levelController = new TrialController(game, 2); // ACA esta puesto que cargue el nivel 1 para debug 
	    levelRenderer = new TrialRenderer(levelController);
		Gdx.input.setCatchBackKey(true);
	}

	@Override
	public void hide () {
		levelRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
	}
	
	@Override
	public void pause () {
		paused = true;
	}
	
	@Override
	public void resume () {
		super.resume();
		// Only called on Android!
		paused = false;
	}
	
}
