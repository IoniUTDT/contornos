package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.ExperimentalObject.JsonMetaData;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;

public class ResourcesBuilder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	private static final String TAG = ResourcesBuilder.class.getName();
	
	static int contadorDeRecursos = Constants.Resources.Reservados;
	static int contadorLevels = 0;
	static int contadorTrials = 0;

	static int height = 100;
	static int width = 100;

	public static void buildNewSVG() {

		Boolean elements = false;
		if (elements) {

			// Crea los objetos reservados (por ahora textos de botones y categorias)
			Array<Texto> objetosTexto = objetosTexto();
			for (Texto text : objetosTexto) {
				SVG.SVGtexto(text);
			}
			// Crea los objetos
			Array<Imagen> objetos = new Array<Imagen>();

			boolean geometrias=true;
			if (geometrias) {
				//objetos.addAll(secuenciaLineasHorizontales()); // Agrega las lineas
				//objetos.addAll(secuenciaLineasConAngulo()); // Agrega las lineas con angulo
				//objetos.addAll(secuenciaAngulos()); // Agrega los angulos
				//objetos.addAll(secuenciaDosRectasCentradasVerticalParalelas()); // Agrega rectas paralelas
				//objetos.addAll(secuenciaDosRectasCentradasVerticalNoParalelas()); //Agrega rectas no paralelas
				objetos.addAll(secuenciaRombos(40, 1f, 0.1f, 0, 100, false, true, false)); // Agrega cuadrados
			}
			// Crea los archivos correspondientes
			for (Imagen im : objetos) {
				SVG.SVGimagen(im);
			}
		}

		Boolean rebuild = false;
		if (rebuild) {
			rebuildAtlasSource();
		}

		Boolean makeLevels = false;
		if (makeLevels) {
			/*
			 * Arma el nivel Tutorial
			 */

			// Crea el nivel tutorial
			JsonLevel tutorial = crearLevel();
			tutorial.levelTitle = "Tutorial";

			// Ahora vamos a ir creando los trials
			tutorial.jsonTrials.add(crearTrial("Bienvenido al juego", "Toque el boton para continuar", DISTRIBUCIONESenPANTALLA.LINEALx1,
					new int[] { Constants.Resources.Categorias.Siguiente.ID }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true));
			tutorial.jsonTrials.add(crearTrial("Rectas horizontales", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx4,
					new int[] { 21, 22, 24, 25 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true));
			tutorial.jsonTrials.add(crearTrial("Rectas diagonales", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 26, 27, 33, 34, 35, 42 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true));
			tutorial.jsonTrials.add(crearTrial("Algunos angulos", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 44, 51, 65, 70, 92, 100 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true));
			tutorial.jsonTrials.add(crearTrial("Rectas paralelas", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 181, 182, 186, 188, 191, 198 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true));
			tutorial.jsonTrials.add(crearTrial("Rectas no paralelas", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 216, 217, 226, 227, 228, 230 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true));

			tutorial.build();

			/*
			 * Arma el nivel 1 (test por imagenes)
			 */

			JsonLevel level1 = crearLevel();
			level1.levelTitle = "Primer desafio";

			// Ahora vamos a ir creando los trials
			level1.jsonTrials.add(crearTrial("�Hiciste la tarea?", "Escuche el sonido y toque la imagen que le corresponde",
					DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 22, 27, 40, 46, 66, 208 }, TIPOdeTRIAL.TEST, 27, false, true));
			level1.jsonTrials.add(crearTrial("�Hiciste la tarea?", "Escuche el sonido y toque la imagen que le corresponde",
					DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 22, 27, 40, 46, 66, 208 }, TIPOdeTRIAL.TEST, 208, false, true));
			level1.jsonTrials.add(crearTrial("�Hiciste la tarea?", "Escuche el sonido y toque la imagen que le corresponde",
					DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 22, 27, 40, 46, 66, 208 }, TIPOdeTRIAL.TEST, 22, false, true));
			level1.jsonTrials.add(crearTrial("�Hiciste la tarea?", "Escuche el sonido y toque la imagen que le corresponde",
					DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 22, 27, 40, 46, 66, 208 }, TIPOdeTRIAL.TEST, 40, false, true));
			level1.jsonTrials.add(crearTrial("�Hiciste la tarea?", "Escuche el sonido y toque la imagen que le corresponde",
					DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 22, 27, 40, 46, 66, 208 }, TIPOdeTRIAL.TEST, 46, false, true));
			level1.jsonTrials.add(crearTrial("�Hiciste la tarea?", "Escuche el sonido y toque la imagen que le corresponde",
					DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 22, 27, 40, 46, 66, 208 }, TIPOdeTRIAL.TEST, 66, false, true));
			level1.build();

			/*
			 * Arma el nivel 2 (test por categorias)
			 */

			JsonLevel level2 = crearLevel();
			level2.levelTitle = "Segundo Desafio";

			level2.jsonTrials.add(crearTrial("�Hiciste la tarea?", "Toque la categoria que corresponda", DISTRIBUCIONESenPANTALLA.BILINEALx2,
					new int[] { Categorias.Lineax1.ID, Categorias.Angulo.ID }, TIPOdeTRIAL.TEST, 46, false, true));
			level2.jsonTrials.add(crearTrial("�Hiciste la tarea?", "Toque la categoria que corresponda", DISTRIBUCIONESenPANTALLA.BILINEALx2,
					new int[] { Categorias.Lineax1.ID, Categorias.Lineax2.ID }, TIPOdeTRIAL.TEST, 27, false, true));
			level2.jsonTrials.add(crearTrial("�Hiciste la tarea?", "Toque la categoria que corresponda", DISTRIBUCIONESenPANTALLA.BILINEALx2,
					new int[] { Categorias.Paralelas.ID, Categorias.NoParalelas.ID }, TIPOdeTRIAL.TEST, 208, false, true));
			level2.build();

		}

	}

	private static void rebuildAtlasSource() {
		int version_temp = MathUtils.roundPositive(Constants.VERSION);
		int temp;
		if (version_temp > Constants.VERSION) {
			temp = -1;
		} else {
			temp = 0;
		}
		int version = version_temp + temp;

		Settings settings = new Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.duplicatePadding = false;
		settings.debug = false;
		TexturePacker.process(settings, "temp/resourcesbuid/", "../android/assets/experimentalsource/" + version + "/", "images");
	}

	private static JsonTrial crearTrial(String title, String caption, DISTRIBUCIONESenPANTALLA distribucion, int[] elementos, TIPOdeTRIAL modo,
			int rtaCorrecta, Boolean randomAnswer, Boolean randomSort) {
		// Crea un JsonTrial y aumenta en 1 el contador de trials
		contadorTrials += 1;
		JsonTrial jsonTrial = new JsonTrial();
		jsonTrial.Id = contadorTrials;
		jsonTrial.caption = caption;
		jsonTrial.distribucion = distribucion;
		jsonTrial.elementosId = elementos;
		jsonTrial.modo = modo;
		jsonTrial.rtaCorrectaId = rtaCorrecta;
		jsonTrial.rtaRandom = randomAnswer;
		jsonTrial.randomSort = randomSort;
		jsonTrial.title = title;
		return jsonTrial;
	}

	private static JsonLevel crearLevel() {
		// Crea un JsonLevel y aumenta en 1 el contador de niveles
		contadorLevels += 1;
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.Id = contadorLevels;
		return jsonLevel;
	}

	private static Array<Texto> objetosTexto() {
		Array<Texto> objetos = new Array<Texto>();

		// Crea un recurso para cada categoria
		for (Constants.Resources.Categorias categoria : Constants.Resources.Categorias.values()) {
			Texto recurso = new Texto();
			recurso.id = categoria.ID;
			recurso.comments = "Recurso experimental generado automaticamente correspondiente a la categoria: " + categoria.nombre;
			recurso.categories.add(categoria);
			recurso.categories.add(Categorias.Texto); // Marca que son textos
			recurso.name = categoria.nombre;
			recurso.texto = categoria.texto;
			objetos.add(recurso);
		}

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
				imagen.categories.add(Constants.Resources.Categorias.Angulo);
				if ((j * shiftAngulo < 90) || (j * shiftAngulo > 270)) {
					imagen.categories.add(Constants.Resources.Categorias.Agudo);
				} else if (j * shiftAngulo == 90) {
					imagen.categories.add(Constants.Resources.Categorias.Recto);
				} else {
					imagen.categories.add(Constants.Resources.Categorias.Grave);
				}
				objetos.add(imagen);
			}
		}
		return objetos;
	}

	private static Array<Imagen> secuenciaRombos(float ladoP, float excentricidadMaxima, float excentricidadMinima, float anguloP, int cantidad, 
			boolean centered, boolean rotados, boolean escalaFija) {
		/*
		 * Esta rutina devuelve una secuencia de rombos que se contruyen a partir de los siguientes parametros:
		 * 
		 * lado: longitud del lado del cuadrilatero
		 * angulo: inclinacion del rombo, un angulo de 0� significa que la diagonal menor esta horizontal. Un angulo de 45� para un cuadrado significa que los lados estan horizontales y verticales
		 * excentricidad: relacion entre la diagonal menor y la mayor. Si la excentricidad es mayor que uno calcula la inversa de manera que siempre la diagonal mayor se asuma vertical (previamente a la rotacion dada por angulo)
		 * 				  una excentricidad igual a 1 da un cuadrado. Si son diferentes la minima y la maxima hace random entre ellas, sino usa la maxima.
		 * 
		 * cantidad determina la cantidad de figuras que se generan
		 * centered determina si estan centradas o si se las posiciona al azar.
		 * rotados determina si se agraga una rotacion random al angulo o no. 
		 * escala determina si se modifica (para menos) el tama�o o no.
		 * 
		 * Se asume que todos los parametros son positivos!
		 */
		
		float margen = 20; // Esta variable determina cuanto espacio se debe dejar de margen para que la figura no este muy pegada al borde.
		
		// Parametros de la figura que se calculan durante la generacion
		float ancho;
		float alto;
		float diagMayor;
		float diagMenor;
		
		// Constantes con los que se pueden modificar los parametros si se lo indica
		float escalaMinima=0.2f;
		
		// parametros generales
		float angulo;
		float lado;
		float excentricidad;
		float xCenter;
		float yCenter;
		
		Vector2 nodo1;
		Vector2 nodo2;
		Vector2 nodo3;
		Vector2 nodo4;
		float angulo1;
		float angulo2;
		float angulo3;
		float angulo4;
		
		
		// El array de salida
		Array<Imagen> objetos = new Array<Imagen>();
		
		
		int errores = 0;
		
		for (int creados=0; creados<cantidad;) {
			// Verifica que no haya muchas figuras invalidas (por ej por no entrar)
			if (errores>cantidad){
				Gdx.app.error(TAG, "Demasiadas figuras no pudieron ser creadas en la rutina que crea cuadrilateros. Considere revisar los parametros");
				break;
			}
			
			// Generamos los parametros para cada cuadrilatero especifico si depende de factores random.
			if (MathUtils.randomBoolean()) {
				excentricidad = excentricidadMaxima - MathUtils.random(excentricidadMaxima-excentricidadMinima);
			} else { // Genera que la mitad sean cuadrados
				excentricidad=1;
			}
			
			if (rotados) {
				angulo = anguloP + MathUtils.random(180);
			} else {
				angulo = anguloP;
			}
			
			float anguloRad = (float) (angulo / 180 * Math.PI);
			
			if (escalaFija) {
				lado = ladoP;
			} else {
				lado = ladoP* MathUtils.random(escalaMinima,1);
			}
			
			// Primero calculamos las diagonales a partir de la medida del lado y la excentricidad
			
			
			
			/*
			 * De plantear que con d=semidiagonal mayor:  d^2 + (d*e)^2 = l^2 sale que  
			 */
			
			diagMayor = (float) (2 * Math.sqrt(lado*lado/(1+excentricidad*excentricidad)));
			diagMenor = diagMayor * excentricidad;
			
			// una vez que calcula las diagonales, sabiendo la inclinacion puede calcular el alto y el ancho
			float anchoDiagMayor = (float) (diagMayor * Math.sin(anguloRad));
			float altoDiagMayor = (float) (diagMayor * Math.cos(anguloRad));
			float anchoDiagMenor = (float) (diagMenor * Math.cos(anguloRad));
			float altoDiagMenor = (float) (diagMenor * Math.sin(anguloRad));
			ancho = Math.max(anchoDiagMayor,anchoDiagMenor);
			alto = Math.max(altoDiagMayor,altoDiagMenor);
			
			// Verifica que la figura entre en la imagen
			
			if ((alto+margen*2>=height) || (ancho+margen*2>=width)) {
				errores++;
				Gdx.app.log(TAG, "El cuadrilatero calculado ha sido descartado por no entrar en la figura");
				
			} else { // Si la figura entra sigue
				creados++;
				
				// setea el centro
				if (centered) {
					xCenter = width/2;
					yCenter = height/2;
				} else { //ERROR!
					xCenter =MathUtils.random(margen+ancho/2, (width - margen - ancho/2));
					yCenter =MathUtils.random(margen+alto/2, (height - margen - alto/2));
				}
				
				// encuentra el centro de los lados (nodos, que se numeran del primer al cuarto cuadrante en la orientacion original) 
				nodo1 = new Vector2();
				nodo1.x = (float) (diagMayor/4);
				nodo1.y = (float) (diagMenor/4);
				nodo2 = new Vector2();
				nodo2.x = (float) (-diagMayor/4);
				nodo2.y = (float) (diagMenor/4);
				nodo3 = new Vector2();
				nodo3.x = (float) (-diagMayor/4);
				nodo3.y = (float) (-diagMenor/4);
				nodo4 = new Vector2();
				nodo4.x = (float) (+diagMayor/4);
				nodo4.y = (float) (-diagMenor/4);
				// Los rota lo que corresponda segun el angulo del cuadrilatero
				nodo1.rotate(-angulo);
				nodo2.rotate(-angulo);
				nodo3.rotate(-angulo);
				nodo4.rotate(-angulo);
				Vector2 center = new Vector2();
				center.x = xCenter;
				center.y = yCenter;
				nodo1.add(center);
				nodo2.add(center);
				nodo3.add(center);
				nodo4.add(center);
				// Calcula los angulos con que esta orientado cada lado.
				float anguloInclinacionLadoRad = MathUtils.atan2(diagMenor, diagMayor);
				float anguloInclinacionLadoDeg = anguloInclinacionLadoRad * 180 / MathUtils.PI;
				// Nota!! Aca hice un lio con eltema de como se miden las coordenadas (creo que se miden de la esq superio izq). Los signos los saque a prueba y error!
				angulo2 = - anguloInclinacionLadoDeg + angulo;
				angulo4 = - anguloInclinacionLadoDeg + angulo;
				angulo1 = + anguloInclinacionLadoDeg + angulo;
				angulo3 = + anguloInclinacionLadoDeg + angulo;
				
				// Llegado este punto esta la informacion de los cuadro segmentos que forman la figura dados por sus centro y sus inclinaciones.
				// Ahora vamos a crear los datos como en todas las demas figuras.
				
				Imagen imagen = crearImagen();
				imagen.parametros.add(ExtremosLinea.Linea(nodo1.x, nodo1.y, angulo1, lado));
				imagen.parametros.add(ExtremosLinea.Linea(nodo2.x, nodo2.y, angulo2, lado));
				imagen.parametros.add(ExtremosLinea.Linea(nodo3.x, nodo3.y, angulo3, lado));
				imagen.parametros.add(ExtremosLinea.Linea(nodo4.x, nodo4.y, angulo4, lado));
				imagen.name = "Cuadrilatero";
				imagen.comments= "Imagen generada automaticamente con el generador de cuadrilateros";
				imagen.categories.add(Categorias.Cuadrilatero);
				if (excentricidad==1) {
					imagen.categories.add(Categorias.Cuadrado);
				} else {
					imagen.categories.add(Categorias.Rombo);
				}
				objetos.add(imagen);
			}
						
		}
		Gdx.app.log(TAG,  "Figuras fallidas: "+errores);
		return objetos;
		
		
		
	}
	
	private static Array<Imagen> secuenciaLineasHorizontales() {
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
			imagen.comments = "Linea generada por secuenciaLineasHorizontales para tutorial";
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax1);
			imagen.categories.add(Constants.Resources.Categorias.Tutorial);
			objetos.add(imagen);
		}
		return objetos;
	}

	private static Imagen crearImagen() {
		contadorDeRecursos += 1;
		Imagen imagen = new Imagen();
		imagen.id = contadorDeRecursos;
		return imagen;
	}

	private static Array<Imagen> secuenciaLineasConAngulo() {
		float largo = 90;
		int cantidad = 18;
		float angulo = 180 / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 1; i < cantidad + 1; i++) {
			Imagen imagen = crearImagen();
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2,
					angulo * i, largo));
			imagen.name = "Linea " + i;
			imagen.comments = "Linea generada por secuenciaLineasConAngulo para tutorial";
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax1);
			imagen.categories.add(Constants.Resources.Categorias.Tutorial);
			objetos.add(imagen);
		}
		return objetos;
	}

	private static Array<Imagen> secuenciaDosRectasCentradasVerticalParalelas() {

		/*
		 * Crea secuencias de dos rectas, ambas centradas en x, pero levemente por encima y por debajo del centro en y, rotando angulos y paralelas
		 */

		int cantidad = 30;
		float largo;
		float angulo;
		float offset;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 0; i < cantidad; i++) {
			largo = MathUtils.random(50f, 90f);
			angulo = MathUtils.random(180f);
			offset = MathUtils.random(10f, 30f);
			Imagen imagen = crearImagen();
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 + offset, angulo, largo)); // La primer linea
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 - offset, angulo, largo)); // La segunda linea
			imagen.name = "Rectas paralelas random, imagen numero " + i + " de la secuencia creada por secuenciaDosRectasCentradasVerticalParalelas";
			imagen.comments = "Parametros: " + "Largo: " + largo + " Angulo: " + angulo + " Offset: +-" + offset;
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax2);
			imagen.categories.add(Constants.Resources.Categorias.Paralelas);
			objetos.add(imagen);
		}
		return objetos;

	}

	private static Array<Imagen> secuenciaDosRectasCentradasVerticalNoParalelas() {

		/*
		 * Crea secuencias de dos rectas, ambas centradas en x, pero levemente por encima y por debajo del centro en y, rotando angulos y paralelas
		 */

		int cantidad = 30;
		float largo;
		float angulo1;
		float angulo2;
		float offset;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 0; i < cantidad; i++) {
			largo = MathUtils.random(50f, 90f);
			angulo1 = MathUtils.random(180f);
			angulo2 = angulo1 + MathUtils.random(10f, 180f);
			offset = MathUtils.random(10f, 30f);
			Imagen imagen = crearImagen();
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 + offset, angulo1, largo)); // La primer linea
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 - offset, angulo2, largo)); // La segunda linea
			imagen.name = "Rectas no paralelas random, imagen numero " + i + " de la secuencia creada por secuenciaDosRectasCentradasVerticalNoParalelas";
			imagen.comments = "Parametros: " + " Largo: " + largo + " Angulo1: " + angulo1 + " Angulo2: " + angulo2 + " Offset: " + offset;
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax2);
			imagen.categories.add(Constants.Resources.Categorias.NoParalelas);
			objetos.add(imagen);
		}
		return objetos;

	}

	public static class Imagen {
		int id;
		String name;
		String comments;
		Array<Constants.Resources.Categorias> categories = new Array<Constants.Resources.Categorias>();
		Array<ExtremosLinea> parametros = new Array<ExtremosLinea>();
	}

	public static class Texto {
		int id;
		String name;
		String comments;
		Array<Categorias> categories = new Array<Constants.Resources.Categorias>();
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
			 * Para encontrar el origen y el fin de la linea deseada utilizo las funcionalidades que tienen los Vector2. Para eso creo dos vectores en el origen
			 * (cada uno con la mitad del largo, uno angulo 0 y otro 180) Luego los roto lo necesario y los traslado a las coordenadas del centro
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
			 * El angulo esta formado por dos linas, ambas del mismo largo orientado cada uno en un angulo diferente.
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

			add("<text text-anchor=\"middle\" x=\"" + width / 2 + "\" y=\"" + height / 2 + "\">" + text.texto + "</text>");
			add("</svg>"); // Finaliza el SVG
			createFileText(text);
			createMetadataText(text);
		}

		private static void createMetadata(Imagen imagen) {
			JsonMetaData jsonMetaData = new JsonMetaData();
			jsonMetaData.Id = imagen.id;
			jsonMetaData.name = imagen.name;
			jsonMetaData.comments = imagen.comments;
			jsonMetaData.categories = imagen.categories;
			jsonMetaData.noSound = false;
			ExperimentalObject.JsonMetaData.CreateJsonMetaData(jsonMetaData, tempPath);

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
			jsonMetaData.Id = text.id;
			jsonMetaData.name = text.name;
			jsonMetaData.comments = text.comments;
			jsonMetaData.categories = text.categories;
			jsonMetaData.noSound = true;
			ExperimentalObject.JsonMetaData.CreateJsonMetaData(jsonMetaData, tempPath);

		}
	}
}
