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
	public Array<String> categoria = new Array<String>();
	
	// Constantes
	private static final String TAG = ExperimentalObject.class.getName();
	
	
	
	/*
	public ExperimentalObject (Sprite imagen, Sound sonido, int Id){
		this.imagen = imagen;
		this.sonido = sonido;
		this.Id = Id;
		this.name = "reemplazar";
	}
 	*/
	
	public ExperimentalObject (int Id){ // Esto carga la info desde archivo
		this.Id = Id;
		// Crea los recursos graficos y sonoros
		this.imagen = ImagesAsset.instance.imagen(Id);
		this.sonido = ImagesAsset.instance.sonido(Id);
		// Carga ma metadata
		this.loadMetaData();
		//this.descripcion = ImagesAsset.instance.MetaInfo(Id).comments;
		//this.name = ImagesAsset.instance.MetaInfo(Id).name;
	}

	
	private void loadMetaData() {
		JsonMetaData jsonMetaData = JsonMetaData.Load(this.Id);
		this.comments = jsonMetaData.comments;
		this.name = jsonMetaData.name;
		this.categoria = jsonMetaData.categories;
	}


	public static class JsonMetaData {
		public int Id;
		public String name;
		public String comments;
		public Array<String> categories = new Array<String>();
		
		public static void createJsonMetaData (int Id, String name, String comments, Array<String> categories) {
			Json json = new Json();
			JsonMetaData jsonMetaData = new JsonMetaData();
			jsonMetaData.Id = Id;
			jsonMetaData.name = name;
			jsonMetaData.comments = comments;
			jsonMetaData.categories = categories;
			FileHelper.writeFile("experimentalsource/" + Constants.version() + "/" + Id + ".meta", json.toJson(jsonMetaData));			
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

	/*
	public static void SaveMetaData(int Id, String name, String comments, Array<String> categories) {
		JsonMetaData metaData = new JsonMetaData();
		metaData.Id = Id;
		metaData.name = "Prueba";
		metaData.comments = "Esto despues se guarda automaticamente";
		metaData.categories = categories;

		Json json = new Json();
		FileHelper.writeFile("experimentalsource/" + Constants.version() + "/" + Id + ".meta", json.toJson(metaData));
	}
	*/
	
	
	
	
	/*
	public JsonMetaData MetaInfo(int Id) {
		return loadMetaData(Id);
	}

	private JsonMetaData loadMetaData(int Id) {
		String save = readFile("experimentalsource/" + this.version + "/" + Id
				+ ".meta");
		if (!save.isEmpty()) {
			Json json = new Json();
			JsonMetaData metaData = json.fromJson(JsonMetaData.class, save);
			return metaData;
		}
		Gdx.app.error(TAG,
				"No se a podido encontrar la info del objeto experimental "
						+ Id);
		return null;
	}
	
	
	private String readFile(String fileName) {
		FileHandle file = Gdx.files.internal(fileName);
		if (file != null && file.exists()) {
			String s = file.readString();
			if (!s.isEmpty()) {
				return s;
			}
		}
		Gdx.app.error(TAG,
				"No se a podido encontrar la info del objeto experimental");
		return "";
	}

	private static void writeFile(String fileName, String s) {
		FileHandle file = Gdx.files.local(fileName);
		file.writeString(s, false);
	}
	*/


}
