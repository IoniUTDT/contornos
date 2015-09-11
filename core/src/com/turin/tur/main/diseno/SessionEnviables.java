package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.turin.tur.main.diseno.Session.SessionLog;



public class SessionEnviables extends Enviables {


	// constantes
	public static final String TAG = SessionEnviables.class.getName();
	
	@Override
	public void enviado() {
		Gdx.app.debug(TAG, "Historial enviado correctamente");
		// Procesa los datos del log general para mover los que se enviaron correctamente
		for (SessionLog sessionLogEnviado: this.contenidoSession){
			 for (SessionLog sessionLogEnviable: this.sessionLogHistory.historyPending){
				 if (sessionLogEnviado==sessionLogEnviable) {
					 // Cambia de lugar los logs
					 this.sessionLogHistory.historySended.add(sessionLogEnviable);
					 this.sessionLogHistory.historyPending.removeValue(sessionLogEnviable, true);
					 sessionLogEnviable.status = STATUS.ENVIADO;
				 }
			 }
		}
		this.sessionLogHistory.save();

	}

	@Override
	public void noEnviado() {
		Gdx.app.debug(TAG, "Historial no enviado correctamente");
		for (SessionLog sessionLogEnviado: this.contenidoSession){
			sessionLogEnviado.status = STATUS.ENVIOFALLIDO;
		}
	}
}