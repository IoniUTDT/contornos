package com.turin.tur.main.diseno;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;

public class Pruebas {

	public class Animal {
		
		public int hambre = 0;
		public int peso; // Esto es el peso en kg
		
		public void digerir (InfoNutricional comida){
			if (this.hambre<=0) {
				this.peso = this.peso + comida.valorNutritivo;
			} else {
				this.hambre = this.hambre - comida.valorNutritivo;
			}
		}
	}
	
	public class Perro extends Animal {
		 
		public String tipoDeAnimal = Perro.class.getName();
		public String nombre; 
		public final int hambreMaximaContento = 5;
		public int felicidad =0;
		
		public Perro () {
			this.peso = 10;
		}
		
		public void comer (Comidas comida, int gramos) {
			InfoNutricional infoNutricional = new InfoNutricional();
			if (comida == Comidas.carne) {
				infoNutricional.valorNutritivo = 1;
			} else {
				infoNutricional.valorNutritivo = 0;
			}
			infoNutricional.valorNutritivo = infoNutricional.valorNutritivo*gramos;
			this.digerir(infoNutricional);
		}
		
		public void saltar () {
			if (this.hambre<this.hambreMaximaContento) {
				Gdx.app.debug(this.nombre, "Guau!");
			} else {
				Gdx.app.debug(this.nombre, "Grrr!");
			}
			this.hambre ++;
			this.felicidad++;
		}
		
		public void aprederNombre () {
			if (this.felicidad>5) {	
				// Se crea el cuadro para ingresar el nombre
				MyTextInputListener listener = new MyTextInputListener();
				Gdx.input.getTextInput(listener,
						"Decime mi nombre", "Perrooooo!", "Ponele un nombre mas original");
			} else {
				Gdx.app.debug(this.nombre, "Tenes q hacerme mas feliz :P");
			}
		}
		
		public class MyTextInputListener implements TextInputListener {

			@Override
			public void input(String text) {
				nombre=text;
			}

			@Override
			public void canceled() {
				nombre="guau";
			}
		}
	}
	
		
	public enum Comidas {
		carne,verdura,papafritas
	}
	
	public class InfoNutricional {
		int valorNutritivo;
	}
	
	public Pruebas (){
		
		Perro myPerro = new Perro();
		myPerro.saltar();
		myPerro.aprederNombre();
		myPerro.saltar();
		myPerro.saltar();
		myPerro.saltar();
		myPerro.saltar();
		myPerro.saltar();
		myPerro.aprederNombre();
		myPerro.saltar();
		myPerro.comer(Comidas.carne, 3);
		myPerro.saltar();
		myPerro.saltar();
	}
}
