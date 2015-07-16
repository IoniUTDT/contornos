package com.turin.tur.main.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.ExperimentalObject;

public class ResourcesBuilder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el
	 * constructor en windows xq la idea es poder usar cosas de java por fuera
	 * de libgdx.
	 */

	static int contadorDeRecursos = 0;
	static int height = 100;
	static int width = 100;

	public static void buildNewSVG() {

		Array<Imagen> objetos = secuenciaLineasVertical();
		objetos.addAll(secuenciaLinesAngulo());
		objetos.addAll(secuenciaAngulos());
		for (Imagen im : objetos) {
			contadorDeRecursos += 1;
			im.id = contadorDeRecursos;
			SVG svg = new SVG(im);
		}

	}

	private static Array<Imagen> secuenciaAngulos() {
		float largo = 50;
		int cantidad = 36;
		float shiftAngulo = 360 / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();

		for (int i = 0; i < cantidad - 1; i++) {
			for (int j = 1; j + i < cantidad - 1; j++) {
				Imagen imagen = new Imagen();
				imagen.parametros.addAll(ExtremosLinea.Angulo(width / 2,
						height / 2, i * shiftAngulo, (j + i) *shiftAngulo, largo));
				imagen.name = "Angulo";
				imagen.comments = "Angulo generado automaticamente por secuenciaAngulos";
				imagen.categories.add(Constants.Diseno.Categorias.ANGULO.name());
				if ((j*shiftAngulo<90) || (j*shiftAngulo>270)) {
					imagen.categories.add(Constants.Diseno.Categorias.AGUDO.name());
				} else if (j*shiftAngulo==90) {
					imagen.categories.add(Constants.Diseno.Categorias.RECTO.name());
				} else {
					imagen.categories.add(Constants.Diseno.Categorias.GRAVE.name());	
				}
				objetos.add(imagen);	
			}
		}
		return objetos;
	}

	private static Array<Imagen> secuenciaLineasVertical() {
		float largo = 90;
		float angulo = 0;
		int cantidad = 5;
		float yCenter = height / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 1; i < cantidad + 1; i++) {
			Imagen imagen = new Imagen();
			imagen.parametros.addAll(ExtremosLinea.Linea(width / 2, yCenter * i
					- yCenter / 2, angulo, largo));
			imagen.name = "Linea " + i;
			imagen.comments = "Linea generada por secuenciaLineasVertical para tutorial";
			imagen.categories.add(Constants.Diseno.Categorias.LINEA.name());
			imagen.categories.add(Constants.Diseno.Categorias.TUTORIAL.name());
			objetos.add(imagen);
		}
		return objetos;
	}

	private static Array<Imagen> secuenciaLinesAngulo() {
		float largo = 90;
		int cantidad = 18;
		float angulo = 180 / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 1; i < cantidad + 1; i++) {
			Imagen imagen = new Imagen();
			imagen.parametros.addAll(ExtremosLinea.Linea(width / 2, height / 2,
					angulo * i, largo));
			imagen.name = "Linea " + i;
			imagen.comments = "Linea generada por secuenciaLinesAngulo para tutorial";
			imagen.categories.add(Constants.Diseno.Categorias.LINEA.name());
			imagen.categories.add(Constants.Diseno.Categorias.TUTORIAL.name());
			objetos.add(imagen);
		}
		return objetos;
	}

	public static class Imagen {
		int id;
		String name;
		String comments;
		Array<String> categories = new Array<String>();
		Array<ExtremosLinea> parametros = new Array<ExtremosLinea>();
	}

	public static class ExtremosLinea {
		float x1;
		float x2;
		float y1;
		float y2;

		public static Array<ExtremosLinea> Linea(float xCenter, float yCenter,
				float angle, float length) {
			/*
			 * Para encontrar el origen y el fin de la linea deseada utilizo las
			 * funcionalidades que tienen los Vector2. Para eso creo dos
			 * vectores en el origen (cada uno con la mitad del largo, uno
			 * angulo 0 y otro 180) Luego los roto lo necesario y los traslado a
			 * las coordenadas del centro
			 */
			Array<ExtremosLinea> lineas = new Array<ExtremosLinea>();
			Vector2 V1 = new Vector2(1, 1);
			Vector2 V2 = new Vector2(1, 1);
			V1.setLength(length / 2);
			V2.setLength(length / 2);
			V1.setAngle(0);
			V2.setAngle(180);
			V1.rotate(-angle);
			V2.rotate(-angle);
			V1.sub(-xCenter, -yCenter); // Por alguna razon Vector2 no tiene la
										// opcion de sumar pero side restar. Por
										// eso le resto el negativo
			V2.sub(-xCenter, -yCenter);
			ExtremosLinea p = new ExtremosLinea();
			p.x1 = V1.x;
			p.y1 = V1.y;
			p.x2 = V2.x;
			p.y2 = V2.y;
			lineas.add(p);
			return lineas;
		}

		public static Array<ExtremosLinea> Angulo(float xVertice,
				float yVertice, float angleInicial, float angleFinal,
				float length) {
			/*
			 * El angulo esta formado por dos linas, ambas del mismo largo
			 * orientado cada uno en un angulo diferente.
			 */
			Array<ExtremosLinea> lineas = new Array<ExtremosLinea>();
			Vector2 V1 = new Vector2(1, 1);
			Vector2 V2 = new Vector2(1, 1);
			V1.setLength(length);
			V2.setLength(length);
			V1.setAngle(angleInicial);
			V2.setAngle(angleFinal);
			V1.sub(-xVertice, -yVertice); // Por alguna razon Vector2 no tiene
											// la
											// opcion de sumar pero side restar.
											// Por
											// eso le resto el negativo
			V2.sub(-xVertice, -yVertice);
			ExtremosLinea p = new ExtremosLinea(); // Crea el primer lado
			p.x1 = xVertice;
			p.y1 = yVertice;
			p.x2 = V1.x;
			p.y2 = V1.y;
			lineas.add(p);
			ExtremosLinea p2 = new ExtremosLinea(); // Crea el segundo lado
			p2.x1 = xVertice;
			p2.y1 = yVertice;
			p2.x2 = V2.x;
			p2.y2 = V2.y;
			lineas.add(p2);
			return lineas;
		}
	}

	public static class SVG {

		int version = Constants.version(); // Version de la aplicacion en la que
											// se esta trabajando (esto
											// determina el paquete entero de
											// recursos
		int Id;
		String tempPath = "/temp/resourcesbuid/"; // Directorio donde se
													// almacenan los recursos
													// durante la construccion
													// antes de pasar todo a su
													// version final.
		String path; // Directorio final donde se deben guardar las cosas
		String content = "";
		String comment;

		public SVG(Imagen imagen) {
			path = "experimentalsource/" + this.version + "/";
			this.Id = imagen.id;
			add("<!-- Este archivo es creado automaticamente por el generador de contenido del programa contornos version "
					+ Constants.VERSION
					+ ". Este elementos es el numero "
					+ Id
					+ " de la serie actual-->"); // Comentario inicial
			add("<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"" + height
					+ "\" width=\"" + width + "\">"); // Inicializa el SVG
			for (ExtremosLinea par : imagen.parametros) {
				add("<line x1=\"" + par.x1 + "\" y1=\"" + par.y1 + "\" x2=\""
						+ par.x2 + "\" y2=\"" + par.y2
						+ "\" stroke-width=\"2\" stroke=\"black\" />"); // Agrega
																		// cada
																		// linea
			}
			add("</svg>"); // Finaliza el SVG
			createFile();
			createMetadata(imagen);
		}

		private void createMetadata(Imagen imagen) {
			ExperimentalObject.JsonMetaData.createJsonMetaData(tempPath,
					imagen.id, imagen.name, imagen.comments, imagen.categories);

		}

		private void add(String string) {
			this.content = this.content + string + "\r\n";
		}

		private void createFile() {
			FileHelper
					.writeFile(this.tempPath + this.Id + ".svg", this.content);
		}
	}
}
