package com.turin.tur.main.diseno;

import com.badlogic.gdx.utils.Array;

public class User {

	public String name;
	public int id;
	public String comments;
	private String pass;
	public Array<Level> levelHistory = new Array<Level>();
	public Array<Trial> trailHistory = new Array<Trial>();	
	
}
