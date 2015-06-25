package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.ImagesAsset;
import com.turin.tur.main.objects.Box;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class Trial {
	
 	public int Id; // Id q identifica al trial
 	
 	// Cosas que se cargan desde archivos
	public String title; // Titulo optativo q describe al trial
	public TIPOdeTRIAL modo; // Tipo de trial
	public int[] elementosId; // Lista de objetos del trial.
	public int rtaCorrectaId; // Respuesta correcta en caso de que sea test.
	public boolean rtaRandom; // Determina si se elije una rta random 
	public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones de los elementos a mostrar
	
	// objetos que se cargan en el load o al inicializar
	public Array<ExperimentalObject> elementos = new Array<ExperimentalObject>(); // Lista de objetos del trial. Carga solo los objetos experimentales, xq todo lo demas se configura en funcion del modo
	public ExperimentalObject rtaCorrecta;
	public Level levelActivo;
	public User userActivo;
	public float levelTime=0;
	private Array<Box> boxes = new Array<Box>();
	
	// constantes
	public static final String TAG = Trial.class.getName();
	
	public Trial (int Id) {
		this.Id = Id;
		initTrial (Id);
		createElements();
	}

	private void createElements() {
		for (ExperimentalObject elemento : this.elementos) {
			if (this.modo == Constants.Diseno.TIPOdeTRIAL.ENTRENAMIENTO) {
				this.boxes.add(new Box (elemento,Constants.Diseno.TIPOdeCAJA.ENTRENAMIENTO));
			}
			
		}
	}

	private void initTrial(int Id) {
		Gdx.app.log(TAG, "Cargando info del nivel");
		// Carga la info en bruto
		JsonTrial jsonLevel = loadTrial(Id);
		this.title = jsonLevel.title;
		this.modo = jsonLevel.modo;
		this.elementosId = jsonLevel.elementosId;
		this.rtaCorrectaId = jsonLevel.rtaCorrectaId;
		this.rtaRandom = jsonLevel.rtaRandom;
		this.distribucion = jsonLevel.distribucion;
		// Carga la info a partir de los Ids
		for (int elemento: this.elementosId) {
			this.elementos.add(new ExperimentalObject(elemento));
		}
		this.rtaCorrecta = new ExperimentalObject(this.rtaCorrectaId);
		// falta cargar el level y el usuario
	}
	
	
	
	
	
	
	
	
	
	
	
	// Seccion encargada de guardar y cargar info de trials
	
	// devuelve la info de la metadata
	
	
	public static class JsonTrial {
		public int Id; // Id q identifica al trial
		public String title; // Titulo optativo q describe al trial
		public TIPOdeTRIAL modo; // Tipo de trial
		public int[] elementosId; // Lista de objetos del trial.
		public int rtaCorrectaId; // Respuesta correcta en caso de que sea test.
		public boolean rtaRandom; // Determina si se elije una rta random 
		public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones de los elementos a mostrar
	}
		
	public void saveTrial(int Id) {
		JsonTrial jsonLevel = new JsonTrial();
		jsonLevel.Id=1;
		jsonLevel.title = "nivel de prueba";
		jsonLevel.modo = TIPOdeTRIAL.ENTRENAMIENTO;
		jsonLevel.elementosId = new int[] {1,2,3,4,5,6};
		jsonLevel.rtaCorrectaId = 2;
		jsonLevel.rtaRandom = false;
		jsonLevel.distribucion = DISTRIBUCIONESenPANTALLA.BILINEALx6;
		
		Json json = new Json();
		writeFile("experimentalconfig/"+ImagesAsset.instance.version+"/level"+Id+".meta", json.toJson(jsonLevel));
	}
	
	private JsonTrial loadTrial(int Id) {
		String save = readFile("experimentalconfig/"+ImagesAsset.instance.version+"/level"+Id+".meta");
		if (!save.isEmpty()) {
			Json json = new Json();
			return json.fromJson(JsonTrial.class, save);
		}
		Gdx.app.error(TAG, "No se a podido encontrar la info del objeto experimental " +Id);
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
		Gdx.app.error(TAG, "No se a podido encontrar la info del objeto experimental");
		return "";
	}
	
	private static void writeFile(String fileName, String s) {
		FileHandle file = Gdx.files.local(fileName);
		file.writeString(s, false);
	}
		
}
