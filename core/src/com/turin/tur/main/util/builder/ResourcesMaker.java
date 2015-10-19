package com.turin.tur.main.util.builder;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Trial.ResourceId;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.Categorias;

public class ResourcesMaker {

	private static final String TAG = ResourcesMaker.class.getName();
	
	public static int height = Resources.Paths.height;
	public static int width = Resources.Paths.width;
	public static int contadorDeRecursos = Constants.Resources.Reservados;
	
	
	public static void BuildResources() {
		
		// Verifica que no haya recursos ya numerados con la version marcada
		File file = new File(Resources.Paths.fullCurrentVersionPath);
		if (file.exists()) {
			System.out.println("Modifique la version de los recursos porque ya existe una carpeta con la version actual");
			return;
		}
		
		// Crea los objetos reservados (por ahora textos de botones y categorias)
		Array<Texto> objetosTexto = objetosTexto();
		for (Texto text : objetosTexto) {
			SVG.SVGtexto(text);
		}

		// Crea los objetos
		Array<Imagen> objetos = new Array<Imagen>();

		boolean geometrias = true;
		if (geometrias) {
			objetos.addAll(secuenciaLineasHorizontales()); // Agrega las lineas
			objetos.addAll(secuenciaLineasVerticales()); // Agrega un set de lineas verticales
			objetos.addAll(secuenciaLineasConAngulo()); // Agrega las lineas con angulo
			objetos.addAll(secuenciaAngulos()); // Agrega los angulos
			objetos.addAll(secuenciaRombos(40, 1f, 0.1f, 0, 50, false, true, false)); // Agrega cuadrados
			objetos.addAll(secuenciaParalelismo()); // Agrega recursos de paralelas dificiles
		}
		// Crea los archivos correspondientes
		for (Imagen im : objetos) {
			SVG.SVGimagen(im);
		}

		
	}

	private static Array<Texto> objetosTexto() {
		Array<Texto> objetos = new Array<Texto>();

		// Crea un recurso para cada categoria
		for (Constants.Resources.Categorias categoria : Constants.Resources.Categorias.values()) {
			Texto recurso = new Texto();
			recurso.resourceId.id = categoria.ID;
			recurso.comments = "Recurso experimental generado automaticamente correspondiente a la categoria: " + categoria.nombre;
			recurso.categories.add(categoria);
			recurso.categories.add(Categorias.Texto); // Marca que son textos
			recurso.name = categoria.nombre;
			recurso.texto = categoria.texto;
			recurso.resourceId.resourceVersion = Builder.ResourceVersion;
			objetos.add(recurso);
		}

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
			imagen.comments = "Linea generada por secuenciaLineasHorizontales para tutorial. Parametros: Altura:" + (yCenter * i - yCenter / 2);
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax1);
			imagen.categories.add(Constants.Resources.Categorias.Tutorial);
			objetos.add(imagen);
		}
		return objetos;
	}
	
	/**
	 * Crea un set de lineas verticales para el tutorial. En este caso se crean a mano las lineas utiles
	 * 
	 * @return
	 */
	private static Array<Imagen> secuenciaLineasVerticales() {
		Array<Imagen> objetos = new Array<Imagen>();
		Imagen imagen;
		// recta vertical completa centrada
		imagen = crearImagen();
		imagen.parametros.addAll(ExtremosLinea.Linea(height / 2, width / 2, 90, 90));
		imagen.name = "Linea vertical centrada completa";
		imagen.comments = "Imagen generada por sucuenciaLineasVerticuales";
		imagen.categories.add(Constants.Resources.Categorias.Tutorial);
		imagen.categories.add(Constants.Resources.Categorias.Lineas);
		imagen.categories.add(Constants.Resources.Categorias.Lineax1);
		objetos.add(imagen);
		// recta vertical incompleta superior
		imagen = crearImagen();
		imagen.parametros.addAll(ExtremosLinea.Linea(height / 6, width / 4, 90, height / 3));
		imagen.name = "Linea vertical superior";
		imagen.comments = "Imagen generada por sucuenciaLineasVerticuales";
		imagen.categories.add(Constants.Resources.Categorias.Tutorial);
		imagen.categories.add(Constants.Resources.Categorias.Lineas);
		imagen.categories.add(Constants.Resources.Categorias.Lineax1);
		objetos.add(imagen);
		// recta vertical incompleta inferior
		imagen = crearImagen();
		imagen.parametros.addAll(ExtremosLinea.Linea(height / 6 * 5, width / 4 * 3, 90, height / 3));
		imagen.name = "Linea vertical superior";
		imagen.comments = "Imagen generada por sucuenciaLineasVerticuales";
		imagen.categories.add(Constants.Resources.Categorias.Tutorial);
		imagen.categories.add(Constants.Resources.Categorias.Lineas);
		imagen.categories.add(Constants.Resources.Categorias.Lineax1);
		objetos.add(imagen);

		return objetos;
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
			imagen.comments = "Linea generada por secuenciaLineasConAngulo para tutorial. Parametros: Posicion: centrada; Angulo: " + (angulo * i) + ";";
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax1);
			imagen.categories.add(Constants.Resources.Categorias.Tutorial);
			objetos.add(imagen);
		}
		return objetos;
	}
	
	private static Array<Imagen> secuenciaAngulos() {
		float largo = 50;
		int cantidad = 36; // Nota: si no se pone una cantidad que sea correcta pueden no quedar angulos rectos!
		float shiftAngulo = 360 / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();

		for (int i = 0; i < cantidad; i++) {
			for (int j = 1; j + i < cantidad; j++) {
				Imagen imagen = crearImagen();
				imagen.parametros.addAll(ExtremosLinea.Angulo(width / 2,
						height / 2, i * shiftAngulo, (j + i) * shiftAngulo,
						largo));
				imagen.name = "Angulo";
				imagen.comments = "Angulo generado automaticamente por secuenciaAngulos. Parametros: Angulo inicial: " + (i * shiftAngulo) + "; Angulo final: "
						+ ((j + i) * shiftAngulo) + ";";
				imagen.categories.add(Constants.Resources.Categorias.Angulo);
				if ((j * shiftAngulo < 90) || (j * shiftAngulo > 270)) {
					imagen.categories.add(Constants.Resources.Categorias.Agudo);
				} else if ((j * shiftAngulo == 90)||(j * shiftAngulo == 270)) {
					imagen.categories.add(Constants.Resources.Categorias.Recto);
					// Se fija si esta rotado
					if (i * shiftAngulo == 0) {
						imagen.categories.add(Categorias.SinRotar);
					} else if (i * shiftAngulo == 90){
						imagen.categories.add(Categorias.SinRotar);
					} else if (i * shiftAngulo == 180){
						imagen.categories.add(Categorias.SinRotar);
					} else if (i * shiftAngulo == 270){
						imagen.categories.add(Categorias.SinRotar);
					} else {
						imagen.categories.add(Categorias.Rotado);
					}
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
		 * lado: longitud del lado del cuadrilatero angulo: inclinacion del rombo, un angulo de 0� significa que la diagonal menor esta horizontal. Un angulo de
		 * 45� para un cuadrado significa que los lados estan horizontales y verticales excentricidad: relacion entre la diagonal menor y la mayor. Si la
		 * excentricidad es mayor que uno calcula la inversa de manera que siempre la diagonal mayor se asuma vertical (previamente a la rotacion dada por
		 * angulo) una excentricidad igual a 1 da un cuadrado. Si son diferentes la minima y la maxima hace random entre ellas, sino usa la maxima.
		 * 
		 * cantidad determina la cantidad de figuras que se generan centered determina si estan centradas o si se las posiciona al azar. rotados determina si se
		 * agraga una rotacion random al angulo o no. escala determina si se modifica (para menos) el tama�o o no.
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
		float escalaMinima = 0.5f;

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

		for (int creados = 0; creados < cantidad;) {
			// Verifica que no haya muchas figuras invalidas (por ej por no entrar)
			if (errores > cantidad) {
				Gdx.app.error(TAG, "Demasiadas figuras no pudieron ser creadas en la rutina que crea cuadrilateros. Considere revisar los parametros");
				break;
			}

			// Generamos los parametros para cada cuadrilatero especifico si depende de factores random.
			if (MathUtils.randomBoolean()) {
				excentricidad = excentricidadMaxima - MathUtils.random(excentricidadMaxima - excentricidadMinima);
			} else { // Genera que la mitad sean cuadrados
				excentricidad = 1;
			}

			if (rotados) {
				if (MathUtils.randomBoolean(0.6f)) {
					angulo = anguloP + MathUtils.random(180);
				} else {
					angulo = anguloP + 45;
				}
			} else {
				angulo = anguloP + 45;
			}
			// Endereza los rombos
			if (excentricidad!=1) {
				if (angulo == 45) {
					angulo=angulo-45;
				}
			}
			
			float anguloRad = (float) (angulo / 180 * Math.PI);

			if (escalaFija) {
				lado = ladoP;
			} else {
				lado = ladoP * MathUtils.random(escalaMinima, 1);
			}

			// Primero calculamos las diagonales a partir de la medida del lado y la excentricidad

			/*
			 * De plantear que con d=semidiagonal mayor: d^2 + (d*e)^2 = l^2 sale que
			 */

			diagMayor = (float) (2 * Math.sqrt(lado * lado / (1 + excentricidad * excentricidad)));
			diagMenor = diagMayor * excentricidad;

			// una vez que calcula las diagonales, sabiendo la inclinacion puede calcular el alto y el ancho
			float anchoDiagMayor = (float) (diagMayor * Math.sin(anguloRad));
			float altoDiagMayor = (float) (diagMayor * Math.cos(anguloRad));
			float anchoDiagMenor = (float) (diagMenor * Math.cos(anguloRad));
			float altoDiagMenor = (float) (diagMenor * Math.sin(anguloRad));
			ancho = Math.max(anchoDiagMayor, anchoDiagMenor);
			alto = Math.max(altoDiagMayor, altoDiagMenor);

			// Verifica que la figura entre en la imagen

			if ((alto + margen * 2 >= height) || (ancho + margen * 2 >= width)) {
				errores++;
				Gdx.app.log(TAG, "El cuadrilatero calculado ha sido descartado por no entrar en la figura");

			} else { // Si la figura entra sigue
				creados++;

				// setea el centro
				if (centered) {
					xCenter = width / 2;
					yCenter = height / 2;
				} else { //ERROR!
					xCenter = MathUtils.random(margen + ancho / 2, (width - margen - ancho / 2));
					yCenter = MathUtils.random(margen + alto / 2, (height - margen - alto / 2));
				}

				// encuentra el centro de los lados (nodos, que se numeran del primer al cuarto cuadrante en la orientacion original) 
				nodo1 = new Vector2();
				nodo1.x = diagMayor / 4;
				nodo1.y = diagMenor / 4;
				nodo2 = new Vector2();
				nodo2.x = -diagMayor / 4;
				nodo2.y = diagMenor / 4;
				nodo3 = new Vector2();
				nodo3.x = -diagMayor / 4;
				nodo3.y = -diagMenor / 4;
				nodo4 = new Vector2();
				nodo4.x = +diagMayor / 4;
				nodo4.y = -diagMenor / 4;
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
				angulo2 = -anguloInclinacionLadoDeg + angulo;
				angulo4 = -anguloInclinacionLadoDeg + angulo;
				angulo1 = +anguloInclinacionLadoDeg + angulo;
				angulo3 = +anguloInclinacionLadoDeg + angulo;

				// Llegado este punto esta la informacion de los cuadro segmentos que forman la figura dados por sus centro y sus inclinaciones.
				// Ahora vamos a crear los datos como en todas las demas figuras.

				Imagen imagen = crearImagen();
				imagen.parametros.add(ExtremosLinea.Linea(nodo1.x, nodo1.y, angulo1, lado));
				imagen.parametros.add(ExtremosLinea.Linea(nodo2.x, nodo2.y, angulo2, lado));
				imagen.parametros.add(ExtremosLinea.Linea(nodo3.x, nodo3.y, angulo3, lado));
				imagen.parametros.add(ExtremosLinea.Linea(nodo4.x, nodo4.y, angulo4, lado));
				imagen.name = "Cuadrilatero";
				imagen.comments = "Imagen generada automaticamente con el generador de cuadrilateros. Parametros: Excentricidad: " + excentricidad + "; Lado: "
						+ lado + "; Angulo: " + angulo + ";";
				imagen.categories.add(Categorias.Cuadrilatero);
				if (excentricidad == 1) {
					imagen.categories.add(Categorias.Cuadrado);
					if ((angulo==45)||(angulo==135)||(angulo==225)||(angulo==315)) {
						imagen.categories.add(Categorias.SinRotar);
					} else {
						imagen.categories.add(Categorias.Rotado);
					}
				} else {
					imagen.categories.add(Categorias.Rombo);
					if ((angulo==0)||(angulo==90)||(angulo==180)||(angulo==270)) {
						imagen.categories.add(Categorias.SinRotar);
					} else {
						imagen.categories.add(Categorias.Rotado);
					}
					
				}
					
				objetos.add(imagen);
			}

		}
		Gdx.app.log(TAG, "Figuras fallidas: " + errores);
		return objetos;

	}
	
	private static Array<Imagen> secuenciaParalelismo() {
		/*
		 * Esta serie genera sets de 6 imagenes similares
		 * 3 paralelas con separacion levemente variable y 3 no paralelas con inclinacion levemenete variable
		 * Va cambiando la dificultad 
		 */
		float largo=80; // Largo de las lineas
		float angulo; // Angulo de inclinacion
		int cantidad = 10;
		float separacion = 15; // Separacion predeterminada
		float limiteAnguloParametro = 30; // Establece un parametro para el maximo angulo que se puede desviar la recta del paralelismo (en grados)
		float limiteAnguloMinimoParametro = 10; // Establece un parametro para el angulo minimo de separacion que tiene q haber en ambos rectas 
		Array<Imagen> objetos = new Array<Imagen>();
		
		for (int i=0; i<cantidad; i++) {
			angulo = 180/cantidad*i; // Define el angulo de la serie
			// Calculamos los centros de manera que esten separados en funcion del angulo

			float Xcenter1 = width/2 - separacion/2 * MathUtils.sin(MathUtils.degRad*angulo);
			float Xcenter2 = width/2 + separacion/2 * MathUtils.sin(MathUtils.degRad*angulo);
			float Ycenter1 = width/2 - separacion/2 * MathUtils.cos(MathUtils.degRad*angulo);
			float Ycenter2 = width/2 + separacion/2 * MathUtils.cos(MathUtils.degRad*angulo);

			for (int dificultad=1; dificultad<10; dificultad++) {
				
				// Crea las 3 imagenes no paralelas
				for (int j=1;j<4;j++) {
					Imagen imagen = crearImagen();
					float limiteAngulo; // Establece el maximo angulo que se puede desviar la recta del paralelismo (en grados)
					float limiteAnguloMinimo; // Establece el angulo minimo de separacion que tiene q haber en ambos rectas
					limiteAngulo = limiteAnguloParametro / dificultad; // Establece un parametro que dependa del nivel de dificultad, a mas dificultad mas parecidas las rectas
					limiteAnguloMinimo = limiteAnguloMinimoParametro / dificultad; // Establece un parametro que dependa del nivel de dificultad, a mas dificultad mas parecidas las rectas
					float anguloVariacion1 = MathUtils.random(limiteAnguloMinimo, +limiteAngulo);
					float anguloVariacion2;
					if (MathUtils.randomBoolean()) {
						anguloVariacion1 = anguloVariacion1 * -1;
					}
					if (anguloVariacion1>0) {
						anguloVariacion2 = anguloVariacion1 + MathUtils.random(-limiteAnguloMinimo, -(limiteAngulo+anguloVariacion1));
					} else {
						anguloVariacion2 = anguloVariacion1 + MathUtils.random(+limiteAnguloMinimo, +(limiteAngulo-anguloVariacion1));
					}
					// agrega la primer linea
					InfoLinea infoLinea = new InfoLinea();
					infoLinea.angulo=angulo+anguloVariacion1;
					infoLinea.largo=largo;
					infoLinea.Xcenter = Xcenter1;
					infoLinea.Ycenter = Ycenter1;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Agrega la segunda linea
					infoLinea = new InfoLinea();
					infoLinea.angulo=angulo+anguloVariacion2;
					infoLinea.largo=largo;
					infoLinea.Xcenter = Xcenter2;
					infoLinea.Ycenter = Ycenter2;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Datos generales
					imagen.comments = "Imagen generada por secuencia automatica 'secuenciaParalelismoDificiles'.";
					imagen.name = "Imagen de rectas no paralelas generada automaticamente";
					imagen.idVinculo = "Paralelismo"+i;
					imagen.categories.add(Categorias.Lineax2);
					imagen.categories.add(Categorias.NoParalelas);
					imagen.nivelDificultad = dificultad;
					objetos.add(imagen);
				}
			}
			// crea la 3 imagenes paralelas
			for(int j=1; j<4; j++){
				Imagen imagen = crearImagen();
				InfoLinea infoLinea = new InfoLinea();
				// Arma la primer linea
				infoLinea.angulo=angulo;
				infoLinea.largo=largo;
				infoLinea.Xcenter = Xcenter1 - separacion/5 * j * MathUtils.sin(MathUtils.degRad*angulo);
				infoLinea.Ycenter = Ycenter1 - separacion/5 * j * MathUtils.cos(MathUtils.degRad*angulo);
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Arma la segunda
				infoLinea = new InfoLinea();
				infoLinea.angulo=angulo;
				infoLinea.largo=largo;
				infoLinea.Xcenter = Xcenter2 + separacion/5 * j * MathUtils.sin(MathUtils.degRad*angulo);
				infoLinea.Ycenter = Ycenter2 + separacion/5 * j * MathUtils.cos(MathUtils.degRad*angulo);
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Datos generales
				imagen.comments = "Imagen generada por secuencia automatica 'secuenciaParalelismoDificiles'.";
				imagen.name = "Imagen de rectas paralelas generada automaticamente";
				imagen.idVinculo = "Paralelismo"+i;
				imagen.categories.add(Categorias.Lineax2);
				imagen.categories.add(Categorias.Paralelas);
				imagen.nivelDificultad = -1; // Significa que no califica de dificultad, o que es dificultad universal.
				objetos.add(imagen);
			}
		}
		return objetos;
	}
	
	private static Imagen crearImagen() {
		contadorDeRecursos += 1;
		Imagen imagen = new Imagen();
		imagen.resourceId.id = contadorDeRecursos;
		imagen.resourceId.resourceVersion = Builder.ResourceVersion;
		return imagen;
	}
	
	public static class Imagen {
		ResourceId resourceId = new ResourceId();
		String name;
		String comments;
		Array<Constants.Resources.Categorias> categories = new Array<Constants.Resources.Categorias>();
		Array<ExtremosLinea> parametros = new Array<ExtremosLinea>();
		Array<InfoLinea> infoLineas = new Array<InfoLinea>();
		String idVinculo; // Sirve para identificar cuando varias imagenes pertenecen a un mismo subgrupo
		int nivelDificultad = -1; // Define un nivel de dificultad, 1 es el mas facil. -1 implica que no esta catalogado por dificultad y que es compatible con cualquier dificultad
	}
	
	public static class SVG {

		static int version = Constants.version(); // Version de la aplicacion en la que
		// se esta trabajando (esto
		// determina el paquete entero de
		// recursos
		static String content = "";

		public static void SVGimagen(Imagen imagen) {
			content = "";
			add("<!-- Este archivo es creado automaticamente por el generador de contenido del programa contornos version "
					+ Constants.VERSION
					+ ". Este elementos es el numero "
					+ imagen.resourceId.id
					+ " de la serie " + imagen.resourceId.resourceVersion + " -->"); // Comentario inicial
			add("<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"" + height
					+ "\" width=\"" + width + "\">"); // Inicializa el SVG
			add("<rect stroke-width=\"5\" stroke=\"#ffffff\" fill=\"#ffffff\" height=\"100\" width=\"100\" y=\"0\" x=\"0\"/>"); // crea el fondo blanco
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
					+ text.resourceId.id
					+ " de la serie " + text.resourceId.resourceVersion + " de textos-->"); // Comentario inicial
			add("<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"" + height
					+ "\" width=\"" + width + "\">"); // Inicializa el SVG
			add("<rect stroke-width=\"5\" stroke=\"#ffffff\" fill=\"#ffffff\" height=\"100\" width=\"100\" y=\"0\" x=\"0\"/>"); // crea el fondo blanco

			add("<text text-anchor=\"middle\" x=\"" + width / 2 + "\" y=\"" + height / 2 + "\">" + text.texto + "</text>");
			add("</svg>"); // Finaliza el SVG
			createFileText(text);
			createMetadataText(text);
		}

		private static void createMetadata(Imagen imagen) {
			JsonResourcesMetaData jsonMetaData = new JsonResourcesMetaData();
			jsonMetaData.resourceId = imagen.resourceId;
			jsonMetaData.name = imagen.name;
			jsonMetaData.comments = imagen.comments;
			jsonMetaData.categories = imagen.categories;
			jsonMetaData.noSound = false;
			jsonMetaData.idVinculo = imagen.idVinculo;
			jsonMetaData.infoLineas = imagen.infoLineas;
			jsonMetaData.parametros = imagen.parametros;
			jsonMetaData.nivelDificultad = imagen.nivelDificultad;
			ExperimentalObject.JsonResourcesMetaData.CreateJsonMetaData(jsonMetaData, Resources.Paths.currentVersionPath);

		}

		private static void add(String string) {
			content = content + string + "\r\n";
		}

		private static void createFile(Imagen imagen) {
			FileHelper.writeFile(Resources.Paths.currentVersionPath + imagen.resourceId.id + ".svg", content);
		}

		private static void createFileText(Texto text) {
			FileHelper.writeFile(Resources.Paths.currentVersionPath + text.resourceId.id + ".svg", content);
		}

		private static void createMetadataText(Texto text) {
			JsonResourcesMetaData jsonMetaData = new JsonResourcesMetaData();
			jsonMetaData.resourceId = text.resourceId;
			jsonMetaData.name = text.name;
			jsonMetaData.comments = text.comments;
			jsonMetaData.categories = text.categories;
			jsonMetaData.noSound = true;
			jsonMetaData.nivelDificultad = text.nivelDificultad;
			ExperimentalObject.JsonResourcesMetaData.CreateJsonMetaData(jsonMetaData, Resources.Paths.currentVersionPath);

		}
	}
	
	public static class Texto {
		ResourceId resourceId = new ResourceId();
		String name;
		String comments;
		Array<Categorias> categories = new Array<Constants.Resources.Categorias>();
		int nivelDificultad = -1;
		String texto;
	}

	public static class InfoLinea {
		float Xcenter;
		float Ycenter;
		float angulo;
		float largo;
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

		public static ExtremosLinea Linea(InfoLinea infoLinea) {
			return Linea(infoLinea.Xcenter, infoLinea.Ycenter, infoLinea.angulo, infoLinea.largo);
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
}
