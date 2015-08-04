package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.ImagesAsset;

public abstract class AbstractGameScreen implements Screen {
	
	public Game game;
	// Informacion general
	
	public AbstractGameScreen (Game game) {
		this.game = game;
	}
	
	public abstract void render (float deltaTime);
	public abstract void resize (int width, int height);
	public abstract void show ();
	public abstract void hide ();
	public abstract void pause ();
	
	public void resume () {
		Assets.instance.init(new AssetManager());
		ImagesAsset.instance.init(new AssetManager());
	}
	
	public void dispose () {
		Assets.instance.dispose();
		ImagesAsset.instance.dispose();
	}
}
