package com.turin.tur.game.objects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

public class ExperimentalObject {

	public final Sprite imagen;
	public final Sound sonido;
	public final int Id; 
	public String descripcion = "Aca va opcionalmente una descripcion del objeto";
	public Array<String> categoria = new Array<String>(); // Aca va quizas mas que un array de string, un array de categorias
	
	public ExperimentalObject (Sprite imagen, Sound sonido, int Id){
		this.imagen = imagen;
		this.sonido = sonido;
		this.Id = Id;
	}
	
}
