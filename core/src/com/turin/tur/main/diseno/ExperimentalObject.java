package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.ImagesAsset;


public class ExperimentalObject {

	public final Sprite imagen;
	public final Sound sonido;
	public final int Id; 
	public String name;
	public String comments = "Aca va opcionalmente una descripcion del objeto";
	public Array<Constants.Resources.Categorias> categorias = new Array<Constants.Resources.Categorias>();
	public boolean noSound;
	
	// Constantes
	private static final String TAG = ExperimentalObject.class.getName();
	
	public ExperimentalObject (int Id){ // Esto carga la info desde archivo
		this.Id = Id;
		// Carga ma metadata
		this.loadMetaData();
		// Crea los recursos graficos y sonoros
		this.imagen = ImagesAsset.instance.imagen(Id);
		if (!this.noSound) {
			this.sonido = ImagesAsset.instance.sonido(Id);
		} else  {
			this.sonido=null;
		}
	}

	private void loadMetaData() {
		JsonMetaData jsonMetaData = JsonMetaData.Load(this.Id);
		this.comments = jsonMetaData.comments;
		this.name = jsonMetaData.name;
		this.categorias = jsonMetaData.categories;
		this.noSound =jsonMetaData.noSound ;
	}


	public static class JsonMetaData {
		public boolean noSound;
		public int Id;
		public String name;
		public String comments;
		public Array<Constants.Resources.Categorias> categories = new Array<Constants.Resources.Categorias>();
		
		public static void CreateJsonMetaData (JsonMetaData jsonMetaData, String path) {
			Json json = new Json();
			FileHelper.writeFile(path + jsonMetaData.Id + ".meta", json.toJson(jsonMetaData));			
		} 
		
		public void save() {
			Json json = new Json();
			FileHelper.writeFile("experimentalsource/" + Constants.version() + "/" + Id + ".meta", json.toJson(this));
		}
		
		public static JsonMetaData Load(int Id) {
			String savedData = FileHelper.readFile("experimentalsource/" + Constants.version() + "/" + Id + ".meta");
			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(JsonMetaData.class, savedData);
			} else { Gdx.app.error(TAG,"No se a podido encontrar la info del recurso experimental" + Id); }
			return null;
		}
	}


	public static Array<ExperimentalObject> Search(int id) {
		return new Array<ExperimentalObject>();
	}
}
