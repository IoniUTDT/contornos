package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.logic.LevelController;
import com.turin.tur.main.logic.LevelRenderer;

public class LevelScreen extends AbstractGameScreen  {
	
	// Constantes 
	private static final String TAG = LevelScreen.class.getName();
	
	// Clases que se crean para manipular el contenido
	private LevelController levelController;
	private LevelRenderer levelRenderer;
	
	// Variables del level
	private int levelNumber;
	
	private boolean paused;

	public Session session;
	
	public LevelScreen (Game game, int level, Session session) {
		super(game);
		this.session = session;
		this.levelNumber=level;
	}

	@Override
	public void render (float deltaTime) {
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			levelController.update(deltaTime);
			levelController.trial.runningSound.update(deltaTime);
		}
		// Sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f,0xed /
				255.0f, 0xff / 255.0f);
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render level to screen
		levelRenderer.render();
	}
	
	@Override
	public void resize (int width, int height) {
		levelRenderer.resize(width, height);
	}

	@Override
	public void show () {
	    levelController = new LevelController(game, this.levelNumber, 0, this.session); 
	    levelRenderer = new LevelRenderer(levelController);
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
