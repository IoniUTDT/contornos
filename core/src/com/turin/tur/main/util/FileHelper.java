package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;


public class FileHelper {

	private static final String TAG = FileHelper.class.getName();
	
	public static String readFile(String fileName) {
		FileHandle file = Gdx.files.internal(fileName);
		if (file != null && file.exists()) {
			String s = file.readString();
			if (!s.isEmpty()) {
				return s;
			}
		}
		Gdx.app.error(TAG,"Archivo: "+fileName+". No se lo ha encontrado o esta vacio");
		return "";
	}

	public static String readLocalFile(String fileName) {
		FileHandle file = Gdx.files.local(fileName);
		if (file != null && file.exists()) {
			String s = file.readString();
			if (!s.isEmpty()) {
				return s;
			}
		}
		Gdx.app.error(TAG,"Archivo: "+fileName+". No se lo ha encontrado o esta vacio");
		return "";
	}
	
	public static void writeFile(String fileName, String s) {
		FileHandle file = Gdx.files.local(fileName);
		file.writeString(s, false);
	}

	public static void appendFile(String fileName, String s) {
		FileHandle file = Gdx.files.local(fileName);
		file.writeString(s+"\r\n", true);
	}

	public static void writeLog(String fileName, String log) {
		FileHandle file = Gdx.files.local(fileName);
		file.writeString(log+"\r\n", true);
		
	}
}
