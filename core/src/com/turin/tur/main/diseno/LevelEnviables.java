package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.turin.tur.main.diseno.Enviables.STATUS;
import com.turin.tur.main.diseno.Level.LevelLog;
import com.turin.tur.main.diseno.Trial.TrialLog;



public class LevelEnviables extends Enviables {


	// constantes
	public static final String TAG = LevelEnviables.class.getName();
	
	@Override
	public void enviado() {
		Gdx.app.debug(TAG, "Historial enviado correctamente");
		// Procesa los datos del log general para mover los que se enviaron correctamente
		for (LevelLog levelLogEnviado: this.contenidoLevel){
			 for (LevelLog levelLogEnviable: this.levelLogHistory.historyPending){
				 if (levelLogEnviable==levelLogEnviado) {
					 // Cambia de lugar los logs
					 this.levelLogHistory.historySended.add(levelLogEnviable);
					 this.levelLogHistory.historyPending.removeValue(levelLogEnviable, true);
					 levelLogEnviable.status = STATUS.ENVIADO;
				 }
			 }
		}
		this.trialLogHistory.save();
	}

	@Override
	public void noEnviado() {
		Gdx.app.debug(TAG, "Historial no enviado correctamente");
		for (LevelLog levelLogEnviado: this.contenidoLevel){
			levelLogEnviado.status = STATUS.ENVIOFALLIDO;
		}
	}
}