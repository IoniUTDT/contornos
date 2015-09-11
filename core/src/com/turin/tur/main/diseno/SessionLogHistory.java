package com.turin.tur.main.diseno;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Enviables.STATUS;
import com.turin.tur.main.diseno.Session.SessionLog;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;

public class SessionLogHistory {


	// constantes
	public static final String TAG = SessionLogHistory.class.getName();
	
	public static String path = "logs/" + Constants.version() + "/sessionHistory.info";
	public static String pathUploaded = path + ".uploaded";
	public Array<SessionLog> historyPending = new Array<SessionLog>();
	public Array<SessionLog> historySended = new Array<SessionLog>();

	public SessionLogHistory() {
		this.load(); // Carga los archivos guardados localmente
		this.restart(); // Corrije el estatus de lo que esta como ENVIANDO a ENVIOFALLIDO porque no tiene sentido que cuando se inicia haya datos enviandose. Si esto sucede es porque se aborto el programa en medio de un envio previo a recibir el ok o el fallido del server
	}
	
	private void load() {
		// Lee los datos que no se enviaron
		String savedData = FileHelper.readLocalFile(path);
		if (!savedData.isEmpty()) {
			Json json = new Json();
			this.historyPending = json.fromJson(this.historyPending.getClass(),savedData);
		} else {
			this.historyPending = new Array<SessionLog>();
		}
		// Lee los datos que si se enviaron
		savedData = FileHelper.readLocalFile(pathUploaded);
		if (!savedData.isEmpty()) {
			Json json = new Json();
			this.historySended = json.fromJson(this.historySended.getClass(),savedData);
		} else {
			this.historySended = new Array<SessionLog>();
		}
	}
	
	private void restart() {
		for (SessionLog sessionLog:this.historyPending) {
			if (sessionLog.status == STATUS.ENVIANDO) {
				sessionLog.status = STATUS.ENVIOFALLIDO;
			}
		}

	}
	
	
	public void append(SessionLog log) {
		// Crea un id de este envio
		long idEnvio = TimeUtils.millis();
		// Indica que el log agregado se configra para enviar cuando se pueda
		log.status=STATUS.PENDIENTEDEENVIO;
		// Agrega el log a la lista de pendientes de envio
		this.historyPending.add(log);
		
		SessionEnviables sessionEnviable = new SessionEnviables();
		sessionEnviable.sessionLogHistory = this; 
		// Revisa cuales estan pendientes de envio
		for (SessionLog sessionLog:this.historyPending) {
			if ((sessionLog.status == STATUS.PENDIENTEDEENVIO) || (sessionLog.status == STATUS.ENVIOFALLIDO)) {
				sessionLog.status = STATUS.ENVIANDO;
				sessionLog.idEnvio = idEnvio;
				sessionEnviable.contenido.add(sessionLog);
				sessionEnviable.contenidoSession.add(sessionLog);
			}
		}
		
		// Envia solo los que corresponde (y que quedan marcados como "enviando")
		
		Internet.PUT(sessionEnviable);
		this.save();
	}

	public void save() {
		Json json = new Json();
		FileHelper.writeFile(path, json.toJson(this.historyPending)); // Guarda lo que no se envio
		FileHelper.writeFile(pathUploaded, json.toJson(this.historySended)); // Guarda lo que si se envio
	}


}