package com.turin.tur.main.diseno;

import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class Trial {
	
 	public int Id; // Id q identifica al trial
	public String title; // Titulo optativo q describe al trial
	public TIPOdeTRIAL modo; // Tipo de trial
	public int[] elementosId; // Lista de objetos del trial.
	public ExperimentalObject rtaCorrecta; // Respuesta correcta en caso de que sea test.
	public boolean rtaRandom; // Determina si se elije una rta random 
	public DISTRIBUCIONESenPANTALLA distribucion; // guarda las posiciones de los elementos a mostrar
	
	// objetos que se cargan en el load
	public ExperimentalObject[] elementos; // Lista de objetos del trial. Carga solo los objetos experimentales, xq todo lo demas se configura en funcion del modo
	public Level levelActivo;
	public User userActivo;
	
	
	// constantes
	public static final String TAG = Trial.class.getName();
	
	public Trial (int Id) {
		this.Id = Id;
		loadTrial (Id);
	}

	private void loadTrial(int Id) {
		
	}
}
