package com.turin.tur.main.diseno;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.util.ImagesAsset;

public class ExperimentalObject {

	public final Sprite imagen;
	public final Sound sonido;
	public final int Id; 
	public final String name;
	public String descripcion = "Aca va opcionalmente una descripcion del objeto";
	public Array<String> categoria = new Array<String>(); // Aca va quizas mas que un array de string, un array de categorias
	
	
	public ExperimentalObject (Sprite imagen, Sound sonido, int Id){
		this.imagen = imagen;
		this.sonido = sonido;
		this.Id = Id;
		this.name = "reemplazar";
	}
 
	
	public ExperimentalObject (int Id){ // Esto carga la info desde archivo
		this.Id = Id;
		this.imagen = ImagesAsset.instance.imagen(Id);
		this.sonido = ImagesAsset.instance.sonido(Id);
		this.descripcion = ImagesAsset.instance.MetaInfo(Id).comments;
		this.name = ImagesAsset.instance.MetaInfo(Id).name;
	}
	
}
