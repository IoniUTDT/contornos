package com.turin.tur.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.util.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.MathUtils;




public class ImagesAsset implements Disposable, AssetErrorListener {
	
	public static final String TAG = ImagesAsset.class.getName();
	public static final ImagesAsset instance = new ImagesAsset();
	public static int version;
	
	private AssetManager assetManager;
	// singleton: prevent instantiation from other classes
	private ImagesAsset () {}
	
	
	// Variables creadas
	
	public void init (AssetManager assetManager) {
		int version_temp = MathUtils.roundPositive(Constants.VERSION);
		int temp;
		if (version_temp>Constants.VERSION) {temp=-1;} else {temp=0;} 
		version = version_temp + temp;
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load("images/experimentalsource/"+version+"/images.pack.atlas",TextureAtlas.class);
		
		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: "
				+ assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()){
			Gdx.app.debug(TAG, "asset: " + a);
		}
		
		TextureAtlas atlas = assetManager.get("images/experimentalsource/"+version+"/images.pack.atlas");
		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		// create game resource objects
	}

	@Override
	public void dispose () {
		assetManager.dispose();
	}
	
	
	@Override
	public void error(@SuppressWarnings("rawtypes") AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" +
				asset.fileName + "'", (Exception)throwable);
	}
	
	
	public AtlasRegion Imagen(int Id, TextureAtlas atlas){
		return atlas.findRegion("Imagen"+Id);
	}

	public Sound Sonido(int Id){
		return Gdx.audio.newSound(Gdx.files.internal("experimentalsource/"+version+"/sound"+Id+".wav"));
	}

	public static class JsonMetaData {
		public int Id;
		public String name;
		public String comments;
	}
	
	public void saveMetaData(int Id) {
		JsonMetaData metaData = new JsonMetaData();
		metaData.Id=Id;
		metaData.name="Prueba";
		metaData.comments="Esto despues se guarda automaticamente";
		
		Json json = new Json();
		writeFile("experimentalsource/"+"Id"+".meta", json.toJson(metaData));
	}
	
	public JsonMetaData MetaInfo (int Id) {
		
		return null;
		
	}
	
	public static void writeFile(String fileName, String s) {
		FileHandle file = Gdx.files.local(fileName);
		file.writeString(com.badlogic.gdx.utils.Base64Coder.encodeString(s), false);
	}
	
	
	

}