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
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class Trial {

	public int Id; // Id q identifica al trial

	// Cosas que se cargan desde archivos
	public String title; // Titulo optativo q describe al trial
	public String caption; // Texto que se muestra debajo
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
		if (this.modo == Constants.Diseno.TIPOdeTRIAL.ENTRENAMIENTO) {
			for (ExperimentalObject elemento : this.elementos) {
				TrainingBox box = new TrainingBox(elemento);
				box.SetPosition(
						distribucion.X(this.elementos.indexOf(elemento, true)),
						distribucion.Y(this.elementos.indexOf(elemento, true)));
				this.trainigBoxes.add(box);
			}
		}

		if (this.modo == Constants.Diseno.TIPOdeTRIAL.TEST) {
			for (ExperimentalObject elemento : this.elementos) {
				AnswerBox box = new AnswerBox(elemento);
				box.SetPosition(
						distribucion.X(this.elementos.indexOf(elemento, true))
								+ Constants.Box.SHIFT_MODO_SELECCIONAR,
						distribucion.Y(this.elementos.indexOf(elemento, true)));
				this.answerBoxes.add(box);
			}
			stimuliBox = new StimuliBox(rtaCorrecta);
			stimuliBox.SetPosition(0+ Constants.Box.SHIFT_ESTIMULO_MODO_SELECCIONAR, 0);
			allBox.add(stimuliBox);
		}
		// Junta todas las cajas en una unica lista para que funcionen los update, etc.
		for (Box box:answerBoxes) {allBox.add(box);}
		for (Box box:trainigBoxes) {allBox.add(box);}
	}

	private void initTrial(int Id) {
		Gdx.app.log(TAG, "Cargando info del trial");
		// Carga la info en bruto
		JsonTrial jsonTrial = JsonTrial.LoadTrial(Id);
		this.caption = jsonTrial.caption;
		this.title = jsonTrial.title;
		this.modo = jsonTrial.modo;
		this.elementosId = jsonTrial.elementosId;
		this.rtaCorrectaId = jsonTrial.rtaCorrectaId;
		this.rtaRandom = jsonTrial.rtaRandom;
		this.distribucion = jsonTrial.distribucion;
		// Carga la info a partir de los Ids
		for (int elemento : this.elementosId) {
			this.elementos.add(new ExperimentalObject(elemento));
		}
		this.rtaCorrecta = new ExperimentalObject(this.rtaCorrectaId);
		Gdx.app.log(TAG, "Info del trial cargado");
	}

	
	public void update(float deltaTime) {
		// Actualiza las boxes
		for (Box box : allBox) {
			box.update(deltaTime);
		}
	}

	
	public boolean checkTrialCompleted (){ // Se encarga de ver si ya se completo trial o no
		if (modo==TIPOdeTRIAL.ENTRENAMIENTO) {
			boolean allCheck = true;
			for (TrainingBox box: trainigBoxes) {if (box.alreadySelected==false) {allCheck=false;}}
			if (allCheck) {trialCompleted=true;}
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
		

		public static void CreateTrial (JsonTrial jsonTrial, String path) {
			Json json = new Json();
			FileHelper.writeFile(path+"trial"+jsonTrial.Id+".meta", json.toJson(jsonTrial));
		}
		
		private static JsonTrial LoadTrial(int Id) {
			String savedData = FileHelper.readFile("experimentalsource/"+  Constants.version() + "/trial" + Id + ".meta");
			
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
}
