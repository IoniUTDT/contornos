package com.turin.tur.main.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.ExperimentalObject.JsonMetaData;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.Constants.Diseno.Categorias;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class ResourcesBuilder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el
	 * constructor en windows xq la idea es poder usar cosas de java por fuera
	 * de libgdx.
	 */

	public static class comments {
		static String body="chau!";
	}
	
	
	static int contadorDeRecursos = Constants.IDs.Resources.Reservados;
	static int contadorLevels = 0;
	static int contadorTrials = 0;

	static int height = 100;
	static int width = 100;
	
	public static void buildNewSVG() {

		Boolean elements = false;
		if (elements) {
			
			// Crea los objetos reservados (por ahora textos de botones y categorias)
			Array<Texto> objetosTexto = objetosTexto();
			for (Texto text: objetosTexto) {
				SVG.SVGtexto(text);
			}
			// Crea los objetos
			Array<Imagen> objetos = new Array<Imagen>();
			objetos.addAll(secuenciaLineasVertical()); // Agrega las lineas
			objetos.addAll(secuenciaLinesAngulo()); // Agrega las lineas con angulo
			objetos.addAll(secuenciaAngulos()); // Agrega los angulos
			objetos.addAll(secuenciaDosRectasCentradasVerticalParalelas()); // Agrega rectas paralelas
			objetos.addAll(secuenciaDosRectasCentradasVerticalNoParalelas()); //Agrega rectas no paralelas
			
			// Crea los archivos correspondientes
			for (Imagen im : objetos) {
				SVG.SVGimagen(im);
			}
		}

		Boolean makeLevels = true;
		if (makeLevels) {
			/*
			 * Arma el nivel Tutorial
			 */

			// Crea el nivel tutorial
			JsonLevel tutorial = crearLevel();
			tutorial.Id = 1;
			tutorial.levelTitle = "Tutorial";

			Array<JsonTrial> trialsTutorial = new Array<JsonTrial>();
			// Ahora vamos a ir creando los trials
			trialsTutorial.add(crearTrial("Bienvenido al juego", "Toque el boton para continuar", DISTRIBUCIONESenPANTALLA.LINEALx1, 
					new int[] {Constants.IDs.Resources.textSiguiente}, TIPOdeTRIAL.ENTRENAMIENTO, Constants.IDs.Resources.sinDatos, false));
			trialsTutorial.add(crearTrial("Rectas horizontales", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx4, 
					new int[] {21,22,24,25}, TIPOdeTRIAL.ENTRENAMIENTO, Constants.IDs.Resources.sinDatos, false));
			trialsTutorial.add(crearTrial("Rectas diagonales", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6, 
					new int[] {26,27,33,34,35,42}, TIPOdeTRIAL.ENTRENAMIENTO, Constants.IDs.Resources.sinDatos, false));
			trialsTutorial.add(crearTrial("Algunos angulos", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6, 
					new int[] {44,51,65,70,92,100}, TIPOdeTRIAL.ENTRENAMIENTO, Constants.IDs.Resources.sinDatos, false));
			trialsTutorial.add(crearTrial("Rectas paralelas", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6, 
					new int[] {181,182,186,188,191,198}, TIPOdeTRIAL.ENTRENAMIENTO, Constants.IDs.Resources.sinDatos, false));
			trialsTutorial.add(crearTrial("Rectas no paralelas", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6, 
					new int[] {216,217,226,227,228,230}, TIPOdeTRIAL.ENTRENAMIENTO, Constants.IDs.Resources.sinDatos, false));
			
			
			for (JsonTrial jsonTrial: trialsTutorial) {
				tutorial.trials.add(jsonTrial.Id);
				JsonTrial.CreateTrial(jsonTrial,"/temp/resourcesbuid/");
			}
			
			JsonLevel.CreateLevel(tutorial, "/temp/resourcesbuid/");
		}

	}

	private static JsonTrial crearTrial(String title, String caption, DISTRIBUCIONESenPANTALLA distribucion, int[] elementos, TIPOdeTRIAL modo, int rta, Boolean random) {
		// Crea un JsonTrial y aumenta en 1 el contador de trials
		contadorTrials += 1;
		JsonTrial jsonTrial = new JsonTrial();
		jsonTrial.Id=contadorTrials;
		jsonTrial.caption = caption;
		jsonTrial.distribucion = distribucion;
		jsonTrial.elementosId = elementos;
		jsonTrial.modo = modo;
		jsonTrial.rtaCorrectaId = rta;
		jsonTrial.rtaRandom = random;
		jsonTrial.title = title;
		return jsonTrial;
	}

	private static JsonLevel crearLevel() {
		// Crea un JsonLevel y aumenta en 1 el contador de niveles
		contadorLevels += 1;
		return new JsonLevel();
	}

	private static Array<Texto> objetosTexto() {
		Array<Texto> objetos = new Array<Texto>();
		
		// Crea un boton que diga siguiente
		Texto textoSiguiente = new Texto();
		textoSiguiente.id=Constants.IDs.Resources.textSiguiente;
		textoSiguiente.comments = "Este boton esta pensado para ir en alguna pantalla de bienvenida";
		textoSiguiente.categories.add(Constants.Diseno.Categorias.TEXTO);
		textoSiguiente.name = "Boton siguiente";
		textoSiguiente.texto= "Continuar";
		objetos.add(textoSiguiente);
		
		Texto textoBlanco = new Texto();
		textoBlanco.id=Constants.IDs.Resources.textNull;
		textoBlanco.comments = "Equivale a no seleccionar nada";
		textoBlanco.categories.add(Constants.Diseno.Categorias.TEXTO);
		textoBlanco.name = "Null";
		textoBlanco.texto= "";
		objetos.add(textoBlanco);
		
		return objetos;
	}
	
	private static Array<Imagen> secuenciaAngulos() {
		float largo = 50;
		int cantidad = 18;
		float shiftAngulo = 360 / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();

		for (int i = 0; i < cantidad - 1; i++) {
			for (int j = 1; j + i < cantidad - 1; j++) {
				Imagen imagen = crearImagen();
				imagen.parametros.addAll(ExtremosLinea.Angulo(width / 2,
						height / 2, i * shiftAngulo, (j + i) * shiftAngulo,
						largo));
				imagen.name = "Angulo";
				imagen.comments = "Angulo generado automaticamente por secuenciaAngulos";
				imagen.categories
						.add(Constants.Diseno.Categorias.ANGULO);
				if ((j * shiftAngulo < 90) || (j * shiftAngulo > 270)) {
					imagen.categories.add(Constants.Diseno.Categorias.AGUDO);
				} else if (j * shiftAngulo == 90) {
					imagen.categories.add(Constants.Diseno.Categorias.RECTO);
				} else {
					imagen.categories.add(Constants.Diseno.Categorias.GRAVE);
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
			Imagen imagen = crearImagen();
			imagen.parametros.addAll(ExtremosLinea.Linea(width / 2, yCenter * i
					- yCenter / 2, angulo, largo));
			imagen.name = "Linea " + i;
			imagen.comments = "Linea generada por secuenciaLineasVertical para tutorial";
			imagen.categories.add(Constants.Diseno.Categorias.LINEA);
			imagen.categories.add(Constants.Diseno.Categorias.TUTORIAL);
			objetos.add(imagen);
		}
		return objetos;
	}

	private static Imagen crearImagen() {
		contadorDeRecursos += 1;
		Imagen imagen = new Imagen();
		imagen.id=contadorDeRecursos;
		return imagen;
	}

	private static Array<Imagen> secuenciaLinesAngulo() {
		float largo = 90;
		int cantidad = 18;
		float angulo = 180 / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 1; i < cantidad + 1; i++) {
			Imagen imagen = crearImagen();
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2,
					angulo * i, largo));
			imagen.name = "Linea " + i;
			imagen.comments = "Linea generada por secuenciaLinesAngulo para tutorial";
			imagen.categories.add(Constants.Diseno.Categorias.LINEA);
			imagen.categories.add(Constants.Diseno.Categorias.TUTORIAL);
			objetos.add(imagen);
		}
		return objetos;
	}

	private static Array<Imagen> secuenciaDosRectasCentradasVerticalParalelas(){
	
		/*
		 *  Crea secuencias de dos rectas, ambas centradas en x, pero levemente por encima y por debajo del centro en y, rotando angulos y paralelas  
		 */
		
		int cantidad = 30;
		float largo;
		float angulo;
		float offset;
		
		Array<Imagen> objetos = new Array<Imagen>();
		for (int i=0; i<cantidad; i++) {
			largo = MathUtils.random(50f,90f);
			angulo = MathUtils.random(180f);
			offset = MathUtils.random(10f,30f);
			Imagen imagen = crearImagen();
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 + offset, angulo, largo)); // La primer linea
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 - offset, angulo, largo)); // La segunda linea
			imagen.name = "Rectas paralelas random, imagen numero " + i + " de la secuencia creada por secuenciaDosRectasCentradasVerticalParalelas";
			imagen.comments="Parametros: "+"Largo: "+largo+" Angulo: "+angulo+" Offset: +-"+offset;
			imagen.categories.add(Constants.Diseno.Categorias.PARALELAS);
			objetos.add(imagen);
		}
		return objetos;
		
	}
	  
	private static Array<Imagen> secuenciaDosRectasCentradasVerticalNoParalelas(){
		
		/*
		 *  Crea secuencias de dos rectas, ambas centradas en x, pero levemente por encima y por debajo del centro en y, rotando angulos y paralelas  
		 */
		
		int cantidad = 30;
		float largo;
		float angulo1;
		float angulo2;
		float offset;
		
		Array<Imagen> objetos = new Array<Imagen>();
		for (int i=0; i<cantidad; i++) {
			largo = MathUtils.random(50f,90f);
			angulo1 = MathUtils.random(180f);
			angulo2 = angulo1 + MathUtils.random(10f,180f);
			offset = MathUtils.random(10f,30f);
			Imagen imagen = crearImagen();
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 + offset, angulo1, largo)); // La primer linea
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 - offset, angulo2, largo)); // La segunda linea
			imagen.name = "Rectas no paralelas random, imagen numero " + i + " de la secuencia creada por secuenciaDosRectasCentradasVerticalNoParalelas";
			imagen.comments="Parametros: "+" Largo: "+largo+" Angulo1: "+angulo1+" Angulo2: "+angulo2+" Offset: "+offset;
			imagen.categories.add(Constants.Diseno.Categorias.noPARALELAS);
			objetos.add(imagen);
		}
		return objetos;
		
	}
	 
	public static class Imagen {
		int id;
		String name;
		String comments;
		Array<Constants.Diseno.Categorias> categories = new Array<Constants.Diseno.Categorias>();
		Array<ExtremosLinea> parametros = new Array<ExtremosLinea>();
	}

	public static class Texto {
		int id;
		String name;
		String comments;
		Array <Categorias> categories = new Array<Constants.Diseno.Categorias>();
		String texto;
	}
	public static class ExtremosLinea {
		float x1;
		float x2;
		float y1;
		float y2;

		public static ExtremosLinea Linea(float xCenter, float yCenter,
				float angle, float length) {
			/*
			 * Para encontrar el origen y el fin de la linea deseada utilizo las
			 * funcionalidades que tienen los Vector2. Para eso creo dos
			 * vectores en el origen (cada uno con la mitad del largo, uno
			 * angulo 0 y otro 180) Luego los roto lo necesario y los traslado a
			 * las coordenadas del centro
			 */
			
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
			return p;
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

		static int version = Constants.version(); // Version de la aplicacion en la que
											// se esta trabajando (esto
											// determina el paquete entero de
											// recursos
		static String tempPath = "/temp/resourcesbuid/"; // Directorio donde se
													// almacenan los recursos
													// durante la construccion
													// antes de pasar todo a su
													// version final.
		static String content = "";

		public static void SVGimagen(Imagen imagen) {
			content = "";
			add("<!-- Este archivo es creado automaticamente por el generador de contenido del programa contornos version "
					+ Constants.VERSION
					+ ". Este elementos es el numero "
					+ imagen.id
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
			createFile(imagen);
			createMetadata(imagen);
		}

		public static void SVGtexto(Texto text) {
			content = "";
			add("<!-- Este archivo es creado automaticamente por el generador de contenido del programa contornos version "
					+ Constants.VERSION
					+ ". Este elementos es el numero "
					+ text.id
					+ " de la serie de textos-->"); // Comentario inicial
			add("<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"" + height
					+ "\" width=\"" + width + "\">"); // Inicializa el SVG

		    add ("<text text-anchor=\"middle\" x=\"" +width/2+ "\" y=\"" +height/2+ "\">" +text.texto+ "</text>");
			add("</svg>"); // Finaliza el SVG
			createFileText(text);
			createMetadataText(text);
		}

		private static void createMetadata(Imagen imagen) {
			JsonMetaData jsonMetaData = new JsonMetaData();
			jsonMetaData.Id=imagen.id;
			jsonMetaData.name=imagen.name;
			jsonMetaData.comments=imagen.comments;
			jsonMetaData.categories=imagen.categories;
			jsonMetaData.noSound=false;
			ExperimentalObject.JsonMetaData.CreateJsonMetaData(jsonMetaData,tempPath);

		}

		private static void add(String string) {
			content = content + string + "\r\n";
		}

		private static void createFile(Imagen imagen) {
			FileHelper.writeFile(tempPath + imagen.id + ".svg", content);
		}
		
		private static void createFileText(Texto text) {
			FileHelper.writeFile(tempPath + text.id + ".svg", content);
		}
		
		private static void createMetadataText(Texto text) {
			JsonMetaData jsonMetaData = new JsonMetaData();
			jsonMetaData.Id=text.id;
			jsonMetaData.name=text.name;
			jsonMetaData.comments=text.comments;
			jsonMetaData.categories=text.categories;
			jsonMetaData.noSound=true;
			ExperimentalObject.JsonMetaData.CreateJsonMetaData(jsonMetaData,tempPath);

		}
	}
}
