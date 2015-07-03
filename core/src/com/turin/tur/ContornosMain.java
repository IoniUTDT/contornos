package com.turin.tur;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.turin.tur.main.Assets;
import com.turin.tur.main.ImagesAsset;
import com.turin.tur.main.diseno.Trial;
import com.turin.tur.main.screens.MenuScreen;

public class ContornosMain extends Game {

	
	private static final String TAG = ContornosMain.class.getName();

	@Override
	public void create () {
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		// Load assets
		Assets.instance.init(new AssetManager());
		ImagesAsset.instance.init(new AssetManager());
		// Start game at menu screen
		setScreen(new MenuScreen(this));
	}
	
}