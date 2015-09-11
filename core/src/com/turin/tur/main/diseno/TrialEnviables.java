package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.turin.tur.main.diseno.Trial.TrialLog;

public class TrialEnviables extends Enviables {


	// constantes
	public static final String TAG = TrialEnviables.class.getName();
	
	@Override
	public void enviado() {
		Gdx.app.debug(TAG, "Historial enviado correctamente");
		// Procesa los datos del log general para mover los que se enviaron correctamente
		for (TrialLog trialLogEnviado: this.contenidoTrial){
			 for (TrialLog trialLogEnviable: this.trialLogHistory.historyPending){
				 if (trialLogEnviable==trialLogEnviado) {
					 // Cambia de lugar los logs
					 this.trialLogHistory.historySended.add(trialLogEnviable);
					 this.trialLogHistory.historyPending.removeValue(trialLogEnviable, true);
					 trialLogEnviable.status = STATUS.ENVIADO;
				 }
			 }
		}
		this.trialLogHistory.save();
	}

	@Override
	public void noEnviado() {
		Gdx.app.debug(TAG, "Historial no enviado correctamente");
		for (TrialLog trialLogEnviado: this.contenidoTrial){
			trialLogEnviado.status = STATUS.ENVIOFALLIDO;
		}
	}
}