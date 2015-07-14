package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.util.ImagesAsset;


public class ExperimentalObject {

	public final Sprite imagen;
	public final Sound sonido;
	public final int Id; 
	public final String name;
	public String descripcion = "Aca va opcionalmente una descripcion del objeto";
	// public Array<String> categoria = new Array<String>(); // Aca va quizas mas que un array de string, un array de categorias
	
	
	public ExperimentalObject (Sprite imagen, Sound sonido, int Id){
		this.imagen = imagen;
		this.sonido = sonido;
		this.Id = Id;
		this.name = "reemplazar";
	}
 
	
	public ExperimentalObject (int Id){ // Esto carga la info desde archivo
		this.Id = Id;
		// Crea los recursos graficos y sonoros
		this.imagen = ImagesAsset.instance.imagen(Id);
		this.sonido = ImagesAsset.instance.sonido(Id);
		// Carga ma metadata
		this.loadMetaData(Id);
		this.descripcion = ImagesAsset.instance.MetaInfo(Id).comments;
		this.name = ImagesAsset.instance.MetaInfo(Id).name;
	}

	
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

	public static class JsonMetaData {
		public int Id;
		public String name;
		public String comments;
	}

	public void saveMetaData(int Id) {
		JsonMetaData metaData = new JsonMetaData();
		metaData.Id = Id;
		metaData.name = "Prueba";
		metaData.comments = "Esto despues se guarda automaticamente";

		Json json = new Json();
		writeFile("experimentalsource/" + this.version + "/" + Id + ".meta",
				json.toJson(metaData));
	}

}
