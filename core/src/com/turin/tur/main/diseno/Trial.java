package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Boxes.AnswerBox;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.Boxes.StimuliBox;
import com.turin.tur.main.diseno.Boxes.TrainingBox;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.ImagesAsset;
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
	public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones de
													// los elementos a mostrar

	// objetos que se cargan en el load o al inicializar
	public Array<ExperimentalObject> elementos = new Array<ExperimentalObject>();
	public ExperimentalObject rtaCorrecta;
	public Level levelActivo;
	public User userActivo;
	public float levelTime = 0;
	public Array<Box> boxes = new Array<Box>();
	
	// constantes
	public static final String TAG = Trial.class.getName();

	public Trial(int Id) {
		this.Id = Id;
		initTrial(Id);
		createElements();
	}

	private void createElements() {
		if (this.modo == Constants.Diseno.TIPOdeTRIAL.ENTRENAMIENTO) {
			for (ExperimentalObject elemento : this.elementos) {
				TrainingBox box = new TrainingBox(elemento);
				box.SetPosition(
						distribucion.X(this.elementos.indexOf(elemento, true)),
						distribucion.Y(this.elementos.indexOf(elemento, true)));
				this.boxes.add(box);
			}
		}

		if (this.modo == Constants.Diseno.TIPOdeTRIAL.TEST) {
			for (ExperimentalObject elemento : this.elementos) {
				AnswerBox box = new AnswerBox(elemento);
				box.SetPosition(
						distribucion.X(this.elementos.indexOf(elemento, true))
								+ Constants.Box.SHIFT_MODO_SELECCIONAR,
						distribucion.Y(this.elementos.indexOf(elemento, true)));
				this.boxes.add(box);
			}
			StimuliBox box = new StimuliBox(rtaCorrecta);
			box.SetPosition(0+ Constants.Box.SHIFT_ESTIMULO_MODO_SELECCIONAR, 0);
			this.boxes.add(box);
		}

	}

	private void initTrial(int Id) {
		Gdx.app.log(TAG, "Cargando info del trial");
		// Carga la info en bruto
		JsonTrial jsonLevel = loadTrial(Id);
		this.title = jsonLevel.title;
		this.modo = jsonLevel.modo;
		this.elementosId = jsonLevel.elementosId;
		this.rtaCorrectaId = jsonLevel.rtaCorrectaId;
		this.rtaRandom = jsonLevel.rtaRandom;
		this.distribucion = jsonLevel.distribucion;
		// Carga la info a partir de los Ids
		for (int elemento : this.elementosId) {
			this.elementos.add(new ExperimentalObject(elemento));
		}
		this.rtaCorrecta = new ExperimentalObject(this.rtaCorrectaId);
		// hace que la respuesta correcta sea una referencia al mismo item que
		// ya esta en la lista
		/*
		for (ExperimentalObject elemento : this.elementos) {
			if (this.rtaCorrectaId == elemento.Id) {
				this.rtaCorrecta = elemento;
			}
		}
		*/
		// falta cargar el level y el usuario
		Gdx.app.log(TAG, "Info del trial cargado");
	}

	
	public void update(float deltaTime) {
		// Actualiza las boxes
		for (Box box : boxes) {
			box.update(deltaTime);
		}
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
		public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones
														// de los elementos a
														// mostrar
	}

	public void saveTrial(int Id) {
		JsonTrial jsonLevel = new JsonTrial();
		jsonLevel.Id = 1;
		jsonLevel.title = "nivel de prueba";
		jsonLevel.modo = TIPOdeTRIAL.ENTRENAMIENTO;
		jsonLevel.elementosId = new int[] { 1, 2, 3, 4, 5, 6 };
		jsonLevel.rtaCorrectaId = 2;
		jsonLevel.rtaRandom = false;
		jsonLevel.distribucion = DISTRIBUCIONESenPANTALLA.BILINEALx6;

		Json json = new Json();
		FileHelper.writeFile("experimentalconfig/" + ImagesAsset.instance.version
				+ "/trial" + Id + ".meta", json.toJson(jsonLevel));
	}

	private JsonTrial loadTrial(int Id) {
		String savedData = FileHelper.readFile("experimentalconfig/"+ ImagesAsset.instance.version + "/trial" + Id + ".meta");
		
		if (!savedData.isEmpty()) {
			Json json = new Json();
			return json.fromJson(JsonTrial.class, savedData);
		}
		Gdx.app.error(TAG,
				"No se a podido encontrar la info del objeto experimental "
						+ Id);
		return null;
	}

}
