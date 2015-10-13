package com.turin.tur;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.ImagesAsset;
import com.turin.tur.main.util.builder.Builder;

public class Visound extends Game {

	
	@SuppressWarnings("unused")
	private static final String TAG = Visound.class.getName();
	private static boolean buildResources = true;
	public Session session;
	
	@Override
	public void create () {
		
		if ((buildResources) & (Gdx.app.getType() == ApplicationType.Desktop)) {
			Builder.build();
		}
		
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		// Load assets
		Assets.instance.init(new AssetManager());
		ImagesAsset.instance.init(new AssetManager());
		
		
		
		// Inicializa la session y el juego
		this.session = new Session();
		setScreen(new MenuScreen(this, this.session));
	}
	
	
}