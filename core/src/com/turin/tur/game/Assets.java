package com.turin.tur.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.turin.tur.util.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Assets implements Disposable, AssetErrorListener {
	
	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();
	
	
	private AssetManager assetManager;
	// singleton: prevent instantiation from other classes
	private Assets () {}
	
	
	// Variables creadas
	public Cuadrado cuadrado;
	public Contenido contenido;
	public Imagenes imagenes;	
	public AssetFonts fonts;
	
	public void init (AssetManager assetManager) {
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS,
				TextureAtlas.class);
		
		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: "
				+ assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames()){
			Gdx.app.debug(TAG, "asset: " + a);
		}
		
		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures()) {
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		// create game resource objects
		cuadrado = new Cuadrado(atlas,"TemplateTransparente");
		contenido = new Contenido(atlas,"Imagen");
		fonts = new AssetFonts();
		imagenes = new Imagenes(atlas); 
	}

	@Override
	public void dispose () {
		assetManager.dispose();
		fonts.defaultFont.dispose();
	}
	
	
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" +
				asset.fileName + "'", (Exception)throwable);
	}
	
	
	public class Cuadrado {
		
		public final AtlasRegion cuadrado;
		public final Array<TextureRegion> cuadrado_serie;
		
		public Cuadrado (TextureAtlas atlas, String imagen) {
			cuadrado = atlas.findRegion(imagen);
			cuadrado_serie = new Array<TextureRegion>();
			String nombre = imagen;
			int i = 0;
			while (atlas.findRegion(nombre) != null) {
				cuadrado_serie.add(atlas.findRegion(nombre));
				i++;
				nombre = imagen + Integer.toString(i);
			}
		}
	}
	
	public class Contenido {
		
		public final Array<TextureRegion> contenido_serie;

		public Contenido (TextureAtlas atlas, String imagen) {
			contenido_serie = new Array<TextureRegion>();
			String nombre = imagen;
			int i = 0;
			nombre = imagen + Integer.toString(i);
			while (atlas.findRegion(nombre) != null) {
				contenido_serie.add(atlas.findRegion(nombre));
				i++;
				nombre = imagen + Integer.toString(i);
			}
		}
	}
	
	public class AssetFonts {
		public final BitmapFont defaultFont;
		public AssetFonts () {
			// create font using Libgdx's 15px bitmap font
			defaultFont = new BitmapFont(
					Gdx.files.internal("images/verdana.fnt"), true);
			// enable linear texture filtering for smooth fonts
			defaultFont.getRegion().getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
			}
		}
	
	public class Imagenes {
		
		public AtlasRegion logoAudio;
		public AtlasRegion animacionContorno;
		
		public Imagenes (TextureAtlas atlas) {
			logoAudio = atlas.findRegion(Constants.Imagenes.LOGOAUDIO);
			animacionContorno = atlas.findRegion(Constants.Imagenes.ANIMACION);
		}
	}
}