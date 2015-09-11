package com.turin.tur.main.diseno;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Level.LevelLog;
import com.turin.tur.main.diseno.Session.SessionLog;
import com.turin.tur.main.diseno.Trial.TrialLog;

public abstract class Enviables {
	public Array<TrialLog> contenidoTrial = new Array<TrialLog>();
	public Array<SessionLog> contenidoSession = new Array<SessionLog>();
	public Array<LevelLog> contenidoLevel = new Array<LevelLog>();
	public Array<Object> contenido = new Array<Object>();
	public abstract void enviado();
	public abstract void noEnviado();
	public TrialLogHistory trialLogHistory;
	public SessionLogHistory sessionLogHistory;
	public LevelLogHistory levelLogHistory;
	
	public static enum STATUS {
		CREADO, PENDIENTEDEENVIO, ENVIANDO, ENVIADO, ENVIOFALLIDO
	}
}

