package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Trial.SoundLog;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources.Categorias;

public class RunningSound {
	
	private static final String TAG = RunningSound.class.getName();
	
	public ExperimentalObject contenido; // Todo el objeto que se esta reproduciendo
	public Sound sound; // Elemento de sonido
	public boolean running = false; // Si se esta reproduciendo o no
	public float start = -1; // Cuando comienza la reproduccion del ultimo sonido. Un "-1" equivale a no tener datos.
	public float ends = -1; // Cuando termina la reproduccion del ultimo sonido. Un "-1" equivale a no tener datos.
	public int id; // El id que identifica el recurso del ultimo sonido
	public int loopsNumber; // Numero de veces que re reproduce el mismo sonido en forma seguida 
	public long instance; // instancia que identifica cada reproduccion unequivocamente
	public Array<Integer> secuenceId = new Array<Integer>(); // secuencia de los sonidos reproducidos.
	public SoundLog soundLog = new SoundLog();
	public String stopReason = "";
	private Trial trial;

	public RunningSound (Trial trial) {
		this.trial = trial;
	}
	
	public void play(ExperimentalObject contenidoP) {
		// Primer detiene cualquier reproduccion previa 
		if (running) {
			stop();
			stopReason ="inicio";
		}
		// Crea un log nuevo
		soundLog = new SoundLog();
		
		// Prepara la info en la clase
		contenido = contenidoP;
		sound = contenido.sonido;
		running = true;
		start = TimeUtils.millis();
		ends = -1;
		id = contenido.resourceId.id;
		if (secuenceId.size > 0) {
			if (secuenceId.peek() == id) {
				loopsNumber++;
			} else {
				loopsNumber = 1;
			}
		} else {
			loopsNumber = 1;
		}
		secuenceId.add(id);
		instance = TimeUtils.millis();

		// Crea el log
		soundLog.soundInstance = instance;
		soundLog.soundId = contenido.resourceId;
		for (Categorias categoria : contenido.categorias) {
			soundLog.categorias.add(categoria);
		}
		soundLog.trialInstance = trial.log.trialInstance;
		soundLog.levelInstance = trial.log.levelInstance;
		soundLog.sessionInstance = trial.log.sessionId;
		soundLog.trialId = trial.Id;
		if (trial.jsonTrial.modo == TIPOdeTRIAL.TEST) {
			if (trial.stimuliBox.contenido == contenido) {
				soundLog.fromStimuli = true;

			} else {
				soundLog.fromStimuli = false;
			}
		} else {
			soundLog.fromStimuli = false;
		}
		soundLog.tipoDeTrial = trial.jsonTrial.modo;
		soundLog.numberOfLoop = loopsNumber;
		//soundLog.startTimeSinceTrial = timeInTrial;
		soundLog.numberOfSoundInTrial = secuenceId.size;
		soundLog.soundSecuenceInTrial = new Array<Integer>(secuenceId);

		long idsonido;
		// Espera a q se cargue el recurso (no se porque esto funciona pero lo saque de internet)
		while ((idsonido = sound.play(0)) == -1) {
			long t = TimeUtils.nanoTime();
			while (TimeUtils.nanoTime() - t < 50000000)
				;
		}
		sound.play();
	}

	
	public void stop() {
		if (running) {
			
			ends = TimeUtils.millis();
			// Completa el log y lo agrega a la lista
			soundLog.stopTime = TimeUtils.millis();
			Gdx.app.debug(TAG, stopReason);
			
			soundLog.stopByUnselect = (stopReason=="unselect");
			soundLog.stopByExit = (stopReason=="exit");
			soundLog.stopByEnd = (stopReason=="end");
			stopReason = "";
			trial.log.soundLog.add(soundLog);
			// Detiene el sonido
			sound.stop();
			running = false;
		}
	}
}