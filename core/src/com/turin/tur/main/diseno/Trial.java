package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.oracle.webservices.internal.api.message.DistributedPropertySet;
import com.turin.tur.main.diseno.Boxes.AnswerBox;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.diseno.Boxes.StimuliBox;
import com.turin.tur.main.diseno.Boxes.TrainingBox;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class Trial {

	public int Id; // Id q identifica al trial

	// Cosas que se cargan desde archivos
	// public String title; // Titulo optativo q describe al trial
	// public String caption; // Texto que se muestra debajo
	// public TIPOdeTRIAL modo; // Tipo de trial
	// public int[] elementosId; // Lista de objetos del trial.
	// public int rtaCorrectaId; // Respuesta correcta en caso de que sea test.
	// public boolean rtaRandom; // Determina si se elije una rta random
	// public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones de
	// los elementos a mostrar
	public JsonTrial jsonTrial;

	// objetos que se cargan en el load o al inicializar
	public Array<ExperimentalObject> elementos = new Array<ExperimentalObject>();
	public ExperimentalObject rtaCorrecta;
	public Level levelActivo;
	public User userActivo;
	public float levelTime = 0;
	public Array<TrainingBox> trainigBoxes = new Array<TrainingBox>();
	public Array<AnswerBox> answerBoxes = new Array<AnswerBox>();
	public StimuliBox stimuliBox;
	public Array<Box> allBox = new Array<Box>();

	// Variable que tiene que ver con el estado del trial
	public boolean trialCompleted = false;

	// constantes
	public static final String TAG = Trial.class.getName();

	public Trial(int Id) {
		this.Id = Id;
		initTrial(Id);
		createElements();
	}

	private void createElements() {
		// Crea las cajas segun corresponda a su tipo
		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.ENTRENAMIENTO) {
			// Crea una lista random de numeros para buscar las posiciones
			Array<Integer> ordenNoRandom = new Array<Integer>();
			for (int i = 0; i < this.jsonTrial.distribucion.distribucion.length; i++) {
				ordenNoRandom.add(i);
			}
			Array<Integer> ordenRandom = new Array<Integer>();
			for (int i = 0; i < this.jsonTrial.distribucion.distribucion.length; i++) {
				int random = MathUtils.random(ordenNoRandom.size - 1);
				int item = ordenNoRandom.removeIndex(random);
				ordenRandom.add(item);
			}

			for (ExperimentalObject elemento : this.elementos) {
				TrainingBox box = new TrainingBox(elemento);
				if (this.jsonTrial.randomSort) {
					box.SetPosition(jsonTrial.distribucion.X(ordenRandom.get(this.elementos.indexOf(elemento, true))),
							jsonTrial.distribucion.Y(ordenRandom.get(this.elementos.indexOf(elemento, true))));
				} else {
					box.SetPosition(jsonTrial.distribucion.X(this.elementos.indexOf(elemento, true)),
							jsonTrial.distribucion.Y(this.elementos.indexOf(elemento, true)));
				}
				this.trainigBoxes.add(box);
			}
		}

		if (this.jsonTrial.modo == Constants.Diseno.TIPOdeTRIAL.TEST) {
			// Crea una lista random de numeros para buscar las posiciones
			Array<Integer> ordenNoRandom = new Array<Integer>();
			for (int i = 0; i < this.jsonTrial.distribucion.distribucion.length; i++) {
				ordenNoRandom.add(i);
			}
			Array<Integer> ordenRandom = new Array<Integer>();
			for (int i = 0; i < this.jsonTrial.distribucion.distribucion.length; i++) {
				int random = MathUtils.random(ordenNoRandom.size - 1);
				int item = ordenNoRandom.removeIndex(random);
				ordenRandom.add(item);
			}

			for (ExperimentalObject elemento : this.elementos) {
				AnswerBox box = new AnswerBox(elemento);
				if (this.jsonTrial.randomSort) {
					box.SetPosition(jsonTrial.distribucion.X(ordenRandom.get(this.elementos.indexOf(elemento, true))) + Constants.Box.SHIFT_MODO_SELECCIONAR,
							jsonTrial.distribucion.Y(ordenRandom.get(this.elementos.indexOf(elemento, true))));
				} else {
					box.SetPosition(jsonTrial.distribucion.X(this.elementos.indexOf(elemento, true)) + Constants.Box.SHIFT_MODO_SELECCIONAR,
							jsonTrial.distribucion.Y(this.elementos.indexOf(elemento, true)));
				}
				this.answerBoxes.add(box);
			}
			stimuliBox = new StimuliBox(rtaCorrecta);
			stimuliBox.SetPosition(0 + Constants.Box.SHIFT_ESTIMULO_MODO_SELECCIONAR, 0);
			allBox.add(stimuliBox);
		}
		// Junta todas las cajas en una unica lista para que funcionen los
		// update, etc.
		for (Box box : answerBoxes) {
			allBox.add(box);
		}
		for (Box box : trainigBoxes) {
			allBox.add(box);
		}
	}

	private void initTrial(int Id) {
		Gdx.app.log(TAG, "Cargando info del trial");
		// Carga la info en bruto
		JsonTrial jsonTrial = JsonTrial.LoadTrial(Id);
		this.jsonTrial = jsonTrial;
		// Carga la info a partir de los Ids
		for (int elemento : this.jsonTrial.elementosId) {
			this.elementos.add(new ExperimentalObject(elemento));
		}
		this.rtaCorrecta = new ExperimentalObject(this.jsonTrial.rtaCorrectaId);
		Gdx.app.log(TAG, "Info del trial cargado");
	}

	public void update(float deltaTime) {
		// Actualiza las boxes
		for (Box box : allBox) {
			box.update(deltaTime);
		}
	}

	public boolean checkTrialCompleted() { // Se encarga de ver si ya se
											// completo trial o no
		if (this.jsonTrial.modo == TIPOdeTRIAL.ENTRENAMIENTO) {
			boolean allCheck = true;
			for (TrainingBox box : trainigBoxes) {
				if (box.alreadySelected == false) {
					allCheck = false;
				}
			}
			if (allCheck) {
				trialCompleted = true;
			}
		}
		return trialCompleted;
	}

	// Seccion encargada de guardar y cargar info de trials

	// devuelve la info de la metadata

	public static class JsonTrial {
		public String caption; // Texto que se muestra debajo
		public int Id; // Id q identifica al trial
		public String title; // Titulo optativo q describe al trial
		public TIPOdeTRIAL modo; // Tipo de trial
		public int[] elementosId; // Lista de objetos del trial.
		public int rtaCorrectaId; // Respuesta correcta en caso de que sea test.
		public boolean rtaRandom; // Determina si se elije una rta random
		public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones
														// de los elementos a
														// mostrar
		public boolean randomSort;

		public static void CreateTrial(JsonTrial jsonTrial, String path) {
			Json json = new Json();
			FileHelper.writeFile(path + "trial" + jsonTrial.Id + ".meta", json.toJson(jsonTrial));
		}

		private static JsonTrial LoadTrial(int Id) {
			String savedData = FileHelper.readFile("experimentalsource/" + Constants.version() + "/trial" + Id + ".meta");

			if (!savedData.isEmpty()) {
				Json json = new Json();
				return json.fromJson(JsonTrial.class, savedData);
			}
			Gdx.app.error(TAG, "No se a podido encontrar la info del objeto experimental " + Id);
			return null;
		}
	}
}
