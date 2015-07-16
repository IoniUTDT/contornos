package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.MathUtils;

public class ImagesAsset implements Disposable, AssetErrorListener {

	public final String TAG = ImagesAsset.class.getName();
	public static final ImagesAsset instance = new ImagesAsset();
	private TextureAtlas atlas;

	private AssetManager assetManager;

	// singleton: prevent instantiation from other classes
	private ImagesAsset() {
	}

	// Variables creadas

	public void init(AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load("experimentalsource/" +  Constants.version() + "/images.pack.atlas", TextureAtlas.class);

		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG,
				"# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + a);
		}

		this.atlas = assetManager.get("experimentalsource/" +  Constants.version() + "/images.pack.atlas");
		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		// create game resource objects
	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}

	@Override
	public void error(@SuppressWarnings("rawtypes") AssetDescriptor asset,
			Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'",
				(Exception) throwable);
	}

	public Sprite imagen(int Id) {
		// return new Sprite(this.atlas.findRegion("" + Id));
		return new Sprite (new Texture(Gdx.files.internal("experimentalsource/"
				+  Constants.version() + "/" + Id + ".png")));
	}

	public Texture imagenFromFile(int Id) {
		return new Texture(Gdx.files.internal("experimentalsource/"
				+  Constants.version() + "/" + Id + ".png"));
	}
	
	public Sound sonido(int Id) {
		Sound sonido = Gdx.audio.newSound(Gdx.files.internal("experimentalsource/"
				+  Constants.version() + "/" + Id + ".wav"));
		return sonido;
	}
}