package com.turin.tur;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.turin.tur.main.diseno.Pruebas;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.screens.MenuScreen;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.ImagesAsset;
import com.turin.tur.main.util.ResourcesBuilder;

public class ContornosMain extends Game {

	
	private static final String TAG = ContornosMain.class.getName();
	private static boolean buildResources = true;
	public Session session;
	
	@Override
	public void create () {
		
		if ((buildResources) & (Gdx.app.getType() == ApplicationType.Desktop)) {
			ResourcesBuilder.buildNewSVG();
		}
		
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		/*
		// Load assets
		Assets.instance.init(new AssetManager());
		ImagesAsset.instance.init(new AssetManager());
		*/
		
		// Inicializa la session y el juego
		
		Pruebas prueba = new Pruebas();
		
		/*
		this.session = new Session();
		setScreen(new MenuScreen(this, this.session));
		*/
	}
	
	
}