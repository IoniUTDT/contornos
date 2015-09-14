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
	
	@Override
	public abstract void render (float deltaTime);
	@Override
	public abstract void resize (int width, int height);
	@Override
	public abstract void show ();
	@Override
	public abstract void hide ();
	@Override
	public abstract void pause ();
	
	@Override
	public void resume () {
		Assets.instance.init(new AssetManager());
		ImagesAsset.instance.init(new AssetManager());
	}
	
	@Override
	public void dispose () {
		Assets.instance.dispose();
		ImagesAsset.instance.dispose();
	}
}
