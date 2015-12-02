package com.turin.tur.main.diseno;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.Enviables.STATUS;
import com.turin.tur.main.diseno.Trial.TrialLog;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Internet;



public class TrialLogHistory {

	
	// constantes
	public static final String TAG = TrialLogHistory.class.getName();

		
	public String path = "logs/" + Constants.version() + "/TrialLogHistory.info";
	public String pathUploaded = path + ".uploaded";
	public Array<TrialLog> historyPending = new Array<TrialLog>();
	public Array<TrialLog> historySended = new Array<TrialLog>();

	public TrialLogHistory (){
		this.load(); // Carga los archivos guardados localmente
		this.restart(); // Corrije el estatus de lo que esta como ENVIANDO a ENVIOFALLIDO porque no tiene sentido que cuando se inicia haya datos enviandose. Si esto sucede es porque se aborto el programa en medio de un envio previo a recibir el ok o el fallido del server
	}

	private void restart() {
		for (TrialLog triallog:this.historyPending) {
			if (triallog.status == STATUS.ENVIANDO) {
				triallog.status = STATUS.ENVIOFALLIDO;
			}
		}

	}

	public void append(TrialLog log) {
		// Crea un id de este envio
		long idEnvio = TimeUtils.millis();
		// Indica que el log agregado se configra para enviar cuando se pueda
		log.status=STATUS.PENDIENTEDEENVIO;
		// Agrega el log a la lista de pendientes de envio
		this.historyPending.add(log);
		
		TrialEnviables trialEnviable = new TrialEnviables();
		trialEnviable.trialLogHistory = this; 
		// Revisa cuales estan pendientes de envio
		for (TrialLog triallog:this.historyPending) {
			if ((triallog.status == STATUS.PENDIENTEDEENVIO) || (triallog.status == STATUS.ENVIOFALLIDO)) {
				triallog.status = STATUS.ENVIANDO;
				triallog.idEnvio = idEnvio;
				trialEnviable.contenido.add(triallog);
				trialEnviable.contenidoTrial.add(triallog);
			}
		}
		
		// Envia solo los que corresponde (y que quedan marcados como "enviando")
		
		Internet.PUT(trialEnviable);
		
		this.save();
	}

	public void save() {
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(this.historyPending)); // Guarda lo que no se envio
		FileHelper.writeFile(pathUploaded, json.toJson(this.historySended)); // Guarda lo que si se envio
	}

	private void load() {
		// Lee los datos que no se enviaron
		String savedData = FileHelper.readLocalFile(path);
		if (!savedData.isEmpty()) {
			Json json = new Json();
			json.setUsePrototypes(false);
			this.historyPending = json.fromJson(this.historyPending.getClass(),savedData);
		} else {
			this.historyPending = new Array<TrialLog>();
		}
		// Lee los datos que si se enviaron
		savedData = FileHelper.readLocalFile(pathUploaded);
		if (!savedData.isEmpty()) {
			Json json = new Json();
			json.setUsePrototypes(false);
			this.historySended = json.fromJson(this.historySended.getClass(),savedData);
		} else {
			this.historySended = new Array<TrialLog>();
		}
	}

}
