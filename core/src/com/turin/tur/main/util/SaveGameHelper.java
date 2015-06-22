package com.turin.tur.main.util;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.User;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;


public class SaveGameHelper {
	
	public static class JsonTrial {
		public int Id; // Id q identifica al trial
		public String title; // Titulo optativo q describe al trial
		public TIPOdeTRIAL modo; // Tipo de trial
		public int[] elementosId; // Lista de objetos del trial.
		public ExperimentalObject rtaCorrecta; // Respuesta correcta en caso de que sea test.
		public boolean rtaRandom; // Determina si se elije una rta random 
		public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones de los elementos a mostrar
	}
	
	
}








/*
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class SaveGameHelper {
	 
	public static class JsonUser {
		public int Id;
		public String name;
		public String comments;
		public ArrayList<Trials> history = new ArrayList<Trials>(); 
		public int wave;
		public int lives;
		public int points;
		public ArrayList<JsonGun> gunList = new ArrayList<JsonGun>();
	}
 
	public static class JsonGun {
		public float x;
		public float y;
		public int type;
		public int level;
	}
 
	public static void saveWorld(TrialInfo trialInfo) {
		JsonWorld jWorld = new JsonWorld();
 
		for (Gun gun : world.guns) {
			JsonGun jGun = new JsonGun();
			jGun.x = gun.position.x;
			jGun.y = gun.position.y;
			jGun.type = gun.getGunType();
			jGun.level = gun.level;
			jWorld.gunList.add(jGun);
		}
 
		jWorld.money = world.money;
		jWorld.wave = world.wave;
		jWorld.lives = world.lives;
		jWorld.points = world.points;
 
		Json json = new Json();
		writeFile("game.sav", json.toJson(jWorld));
	}
 
	public static World loadWorld() {
		String save = readFile("game.sav");
		if (!save.isEmpty()) {
			World world = new World();
 
			Json json = new Json();
			JsonWorld jWorld = json.fromJson(JsonWorld.class, save);
 
			world.money = jWorld.money;
			world.wave = jWorld.wave;
			world.lives = jWorld.lives;
			world.points = jWorld.points;
 
			if (jWorld.gunList != null) {
				for (JsonGun jGun : jWorld.gunList) {
					Gun gun = new Gun(jGun.type, jGun.x, jGun.y);
					gun.level = jGun.level;
					world.guns.add(gun);
				}
			}
 
			return world;
		}
		return null;
	}
 
	public static void writeFile(String fileName, String s) {
		FileHandle file = Gdx.files.local(fileName);
		file.writeString(com.badlogic.gdx.utils.Base64Coder.encodeString(s), false);
	}
 
	public static String readFile(String fileName) {
		FileHandle file = Gdx.files.local(fileName);
		if (file != null && file.exists()) {
			String s = file.readString();
			if (!s.isEmpty()) {
				return com.badlogic.gdx.utils.Base64Coder.decodeString(s);
			}
		}
		return "";
	}
}
*/
