package com.turin.tur.main.util.builder;

import java.io.File;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
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
	public static int contadorDeReferenciasUmbral = 0;
	
	
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
			if (Builder.AppVersion == "UmbralCompleto") {
				JsonSetupExpSensibilidad setup = new JsonSetupExpSensibilidad();

				// Creamos los recursos de -6 a 6 con saltos de 3
				// Vamos a trabajar todas las cuentas en radianes
				setup.nombre = "Eje horizontal";
				setup.tag = "H";
				setup.titaRefInicial = -6;
				setup.saltoTitaRefInt = 3;
				setup.saltoTitaRef = setup.saltoTitaRefInt;
				setup.anguloMinimo = 0.5f;  
				setup.anguloMaximo = 30;
				setup.largo=80; // Largo de las lineas
				setup.separacionMinima = 15; // Separacion predeterminada
				setup.separacionIncremento = 10;
				setup.cantidadReferencias = 5;
				setup.cantidadSeparaciones = 2;
				setup.cantidadDeltas = 50;   
				objetos.addAll(recursosParalelismoAnalisisUmbral(setup));
				
				// Creamos los recursos de 10 a 80 con saltos de 10
				// Vamos a trabajar todas las cuentas en radianes
				setup.nombre = "Primer cuadrante";
				setup.tag = "1C";
				setup.titaRefInicial = 10;
				setup.saltoTitaRefInt = 10;
				setup.saltoTitaRef = setup.saltoTitaRefInt;
				setup.anguloMinimo = 1f;  
				setup.anguloMaximo = 30;
				setup.largo=80; // Largo de las lineas
				setup.separacionMinima = 15; // Separacion predeterminada
				setup.separacionIncremento = 10;
				setup.cantidadReferencias = 8;
				setup.cantidadSeparaciones = 2;
				setup.cantidadDeltas = 50;   
				objetos.addAll(recursosParalelismoAnalisisUmbral(setup));
				
				// Creamos los recursos verticales
				// Vamos a trabajar todas las cuentas en radianes
				setup.nombre = "eje vertical";
				setup.tag = "V";
				setup.titaRefInicial = 86;
				setup.saltoTitaRefInt = 2;
				setup.saltoTitaRef = setup.saltoTitaRefInt;
				setup.anguloMinimo = 0.02f;  
				setup.anguloMaximo = 10;
				setup.largo=80; // Largo de las lineas
				setup.separacionMinima = 15; // Separacion predeterminada
				setup.separacionIncremento = 10;
				setup.cantidadReferencias = 5;
				setup.cantidadSeparaciones = 2;
				setup.cantidadDeltas = 50;   
				objetos.addAll(recursosParalelismoAnalisisUmbral(setup));

				// Creamos los recursos de 100 a 170 con saltos de 10
				// Vamos a trabajar todas las cuentas en radianes
				setup.nombre = "Segundo cuadrante";
				setup.tag = "2C";
				setup.titaRefInicial = 100;
				setup.saltoTitaRefInt = 10;
				setup.saltoTitaRef = setup.saltoTitaRefInt;
				setup.anguloMinimo = 1f;  
				setup.anguloMaximo = 30;
				setup.largo=80; // Largo de las lineas
				setup.separacionMinima = 15; // Separacion predeterminada
				setup.separacionIncremento = 10;
				setup.cantidadReferencias = 8;
				setup.cantidadSeparaciones = 2;
				setup.cantidadDeltas = 50;   
				objetos.addAll(recursosParalelismoAnalisisUmbral(setup));
			}
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
			recurso.nivelDificultad = -1; // -1 indica que no tiene sentido aplicar dificultad en este caso 
			recurso.resourceId.resourceVersion = Builder.ResourceVersion;
			objetos.add(recurso);
		}

		return objetos;
	}

	/*
	 * Esta es una clase para manejar el setup experimental del diseño de experimento donde se quiere medir la sensibilidad al detectar el delta tita
	 */
	public static class JsonSetupExpSensibilidad {
		public String tag;
		// Vamos a trabajar todas las cuentas en radianes
		String nombre; // Nombre del setup
		float titaRefInicial; // Angulo de referencia inicial
		int saltoTitaRefInt; // Salto del tita de referencia 
		float saltoTitaRef; //Salto del tita pero en formato float
		float anguloMinimo; //Angulo minimo del delta
		float anguloMaximo; //Angulo maximo del delta
		float largo; //Largo de los segmentos
		float separacionMinima; // Separacion minima de los segmentos
		float separacionIncremento; // Incremento de la separacion de los segmentos
		int cantidadReferencias; // Cantidad de angulos tita (referencia)
		int cantidadSeparaciones; // Cantidad de saltos en la separacion de las rectas
		int cantidadDeltas; // Cantidad de delta titas que se generan en cada condicion de angulo de referencia y de separacion	
		public String tagRefPos="+"; // Guarda el tag de la ref positiva
		public String tagRefNeg="-"; // Guarda el tag de la ref negativa
	}
	
	private static Array<Imagen> recursosParalelismoAnalisisUmbral(JsonSetupExpSensibilidad setup) {
		
		/*
		 * Queremos mapear una escala log en una lineal, es decir que parametro [pmin-->pmax] mapee angulos que van de anguloMin --> angluloMax de manera que p = 1/A*log(1/B*angulo)
		 * ==> angulo = B * e ^ (A * parametro)
		 * Porque la idea es que haya mas densidad de angulos en los angulos chicos que grandes
		 * Si pmin = 0 y pmax=cantidadDeltas-1, queda que  
		 * 0 = 1/ A log (1/B * anguloMin) ==> B=angMin
		 * Pmax = 1/A *log (1/AngMin * AngMax) ==> A = log(AngMax/AngMin)/Pmax 
		 */
		float parametroB = setup.anguloMinimo;
		float parametroA = (float) ((Math.log(setup.anguloMaximo/setup.anguloMinimo))/(setup.cantidadDeltas-1));
		
		
		Array<Imagen> objetos = new Array<Imagen>();
		
		
		for (int i=0; i<setup.cantidadReferencias; i++) {
		
			boolean recursoPosCreado = false;
			boolean recursoNegCreado = false;
			
			for (int j=0; j<setup.cantidadSeparaciones ; j++) {
				
				
				float separacion = setup.separacionMinima + j * setup.separacionIncremento; // Itera para separaciones cada vez mayores
				// Esto no lo estoy usando!
				// float anguloMaximoNoInterseccion = (float) Math.toDegrees(Math.asin(separacion/setup.largo)); // Calcula el maximo angulo permitido de manera que no corten las dos rectas.
				
				// Creamos la imagen paralela
				Imagen imagen = crearImagen();
				
				float anguloReferencia = setup.titaRefInicial + i * setup.saltoTitaRef;
				// Calculamos los centros de manera que esten separados en funcion del angulo
				float Xcenter1 = width/2 - separacion/2 * MathUtils.sinDeg(anguloReferencia);
				float Xcenter2 = width/2 + separacion/2 * MathUtils.sinDeg(anguloReferencia);
				float Ycenter1 = width/2 - separacion/2 * MathUtils.cosDeg(anguloReferencia);
				float Ycenter2 = width/2 + separacion/2 * MathUtils.cosDeg(anguloReferencia);
				imagen.infoConceptual.direccionAnguloReferencia = anguloReferencia;
				
				// agrega la primer linea
				InfoLinea infoLinea = new InfoLinea();
				infoLinea.angulo=anguloReferencia;
				infoLinea.largo=setup.largo;
				infoLinea.Xcenter = Xcenter1;
				infoLinea.Ycenter = Ycenter1;
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Agrega la segunda linea
				infoLinea = new InfoLinea();
				infoLinea.angulo=anguloReferencia;
				infoLinea.largo=setup.largo;
				infoLinea.Xcenter = Xcenter2;
				infoLinea.Ycenter = Ycenter2;
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Datos generales
				// Nota, aca no tiene sentido poner ni parametro linelizado ni si se juntan!
				imagen.infoConceptual.deltaAnguloLinealizado=0;
				imagen.infoConceptual.deltaAngulo=0;
				imagen.infoConceptual.separacion=separacion;
				
				imagen.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
				imagen.name = "Imagen de rectas no paralelas generada automaticamente";
				imagen.idVinculo = "R"+(contadorDeReferenciasUmbral+i)+"D0";
				imagen.categories.add(Categorias.Lineax2);
				imagen.categories.add(Categorias.Paralelas);
				imagen.nivelDificultad = -1;
				objetos.add(imagen);
				
				// Creamos las imagenes con deltas
				for (int k=1; k<=setup.cantidadDeltas; k++) {
					float anguloDelta = (float) (parametroB * Math.exp(parametroA*(k-1)));
					float anguloDeltaPos = anguloDelta/2;
					float anguloDeltaNeg = -anguloDelta/2;
					
					// Creamos la imagen con delta "positivo"
					imagen = crearImagen();
					
					// Almacenamos la data de la info de la geometria que queremos estudiar.
					imagen.infoConceptual.deltaAngulo = anguloDelta;
					imagen.infoConceptual.deltaAnguloLinealizado = k;
					imagen.infoConceptual.direccionAnguloReferencia = anguloReferencia;
					imagen.infoConceptual.seJuntan = true;
					imagen.infoConceptual.separacion = separacion;
					
					// agrega la primer linea
					infoLinea = new InfoLinea();
					infoLinea.angulo=anguloReferencia+anguloDeltaPos;
					infoLinea.largo=setup.largo;
					infoLinea.Xcenter = Xcenter1;
					infoLinea.Ycenter = Ycenter1;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Agrega la segunda linea
					infoLinea = new InfoLinea();
					infoLinea.angulo=anguloReferencia+anguloDeltaNeg;
					infoLinea.largo=setup.largo;
					infoLinea.Xcenter = Xcenter2;
					infoLinea.Ycenter = Ycenter2;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Datos generales
					imagen.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
					imagen.name = "Imagen de rectas no paralelas generada automaticamente";
					imagen.idVinculo = "R"+(contadorDeReferenciasUmbral+i)+"D"+k;
					imagen.categories.add(Categorias.Lineax2);
					imagen.categories.add(Categorias.NoParalelas);
					imagen.nivelDificultad = -1;
					
					if ((recursoNegCreado==false) & (anguloDelta>9) & (j==setup.cantidadSeparaciones-1)){
						recursoNegCreado = true;
						Imagen imagenRefNeg = crearImagen();
						
						System.out.println(imagenRefNeg.resourceId.id);
						
						// Almacenamos la data de la info de la geometria que queremos estudiar.
						imagenRefNeg.infoConceptual.deltaAngulo = anguloDelta;
						imagenRefNeg.infoConceptual.deltaAnguloLinealizado = k;
						imagenRefNeg.infoConceptual.direccionAnguloReferencia = anguloReferencia;
						imagenRefNeg.infoConceptual.seJuntan = true;
						imagenRefNeg.infoConceptual.separacion = separacion;
						
						// agrega la primer linea
						infoLinea = new InfoLinea();
						infoLinea.angulo=anguloReferencia+anguloDeltaPos;
						infoLinea.largo=setup.largo;
						infoLinea.Xcenter = Xcenter1;
						infoLinea.Ycenter = Ycenter1;
						imagenRefNeg.parametros.addAll(ExtremosLinea.Linea(infoLinea));
						imagenRefNeg.infoLineas.add(infoLinea);
						// Agrega la segunda linea
						infoLinea = new InfoLinea();
						infoLinea.angulo=anguloReferencia+anguloDeltaNeg;
						infoLinea.largo=setup.largo;
						infoLinea.Xcenter = Xcenter2;
						infoLinea.Ycenter = Ycenter2;
						imagenRefNeg.parametros.addAll(ExtremosLinea.Linea(infoLinea));
						imagenRefNeg.infoLineas.add(infoLinea);
						// Datos generales
						imagenRefNeg.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
						imagenRefNeg.name = "Imagen de rectas no paralelas generada automaticamente";
						imagenRefNeg.idVinculo = setup.tagRefNeg;
						imagenRefNeg.categories.add(Categorias.Lineax2);
						imagenRefNeg.categories.add(Categorias.NoParalelas);
						imagenRefNeg.nivelDificultad = -1;
						objetos.add(imagenRefNeg);
					}
					
					objetos.add(imagen);
					
					// Creamos la imagen con delta "positivo"
					imagen = crearImagen();
					
					// Almacenamos la data de la info de la geometria que queremos estudiar.
					imagen.infoConceptual.deltaAngulo = anguloDelta;
					imagen.infoConceptual.deltaAnguloLinealizado = k;
					imagen.infoConceptual.direccionAnguloReferencia = anguloReferencia;
					imagen.infoConceptual.seJuntan = false;
					imagen.infoConceptual.separacion = separacion;
					
					// agrega la primer linea
					infoLinea = new InfoLinea();
					infoLinea.angulo=anguloReferencia-anguloDeltaPos;
					infoLinea.largo=setup.largo;
					infoLinea.Xcenter = Xcenter1;
					infoLinea.Ycenter = Ycenter1;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Agrega la segunda linea
					infoLinea = new InfoLinea();
					infoLinea.angulo=anguloReferencia-anguloDeltaNeg;
					infoLinea.largo=setup.largo;
					infoLinea.Xcenter = Xcenter2;
					infoLinea.Ycenter = Ycenter2;
					imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
					imagen.infoLineas.add(infoLinea);
					// Datos generales
					imagen.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
					imagen.name = "Imagen de rectas no paralelas generada automaticamente";
					imagen.idVinculo = "R"+(contadorDeReferenciasUmbral+i)+"D"+k;
					imagen.categories.add(Categorias.Lineax2);
					imagen.categories.add(Categorias.NoParalelas);
					imagen.nivelDificultad = -1;
					
					if ((recursoPosCreado==false) & (anguloDelta>9) & (j==setup.cantidadSeparaciones-1)){
						recursoPosCreado = true;
						
						// Creamos la imagen con delta "positivo"
						Imagen imagenPos = crearImagen();
						System.out.println(imagenPos.resourceId.id);
						
						// Almacenamos la data de la info de la geometria que queremos estudiar.
						imagenPos.infoConceptual.deltaAngulo = anguloDelta;
						imagenPos.infoConceptual.deltaAnguloLinealizado = k;
						imagenPos.infoConceptual.direccionAnguloReferencia = anguloReferencia;
						imagenPos.infoConceptual.seJuntan = false;
						imagenPos.infoConceptual.separacion = separacion;
						
						// agrega la primer linea
						infoLinea = new InfoLinea();
						infoLinea.angulo=anguloReferencia-anguloDeltaPos;
						infoLinea.largo=setup.largo;
						infoLinea.Xcenter = Xcenter1;
						infoLinea.Ycenter = Ycenter1;
						imagenPos.parametros.addAll(ExtremosLinea.Linea(infoLinea));
						imagenPos.infoLineas.add(infoLinea);
						// Agrega la segunda linea
						infoLinea = new InfoLinea();
						infoLinea.angulo=anguloReferencia-anguloDeltaNeg;
						infoLinea.largo=setup.largo;
						infoLinea.Xcenter = Xcenter2;
						infoLinea.Ycenter = Ycenter2;
						imagenPos.parametros.addAll(ExtremosLinea.Linea(infoLinea));
						imagenPos.infoLineas.add(infoLinea);
						// Datos generales
						imagenPos.comments = "Imagen generada por secuencia automatica 'recursosParalelismoAnalisisUmbral'.";
						imagenPos.name = "Imagen de rectas no paralelas generada automaticamente";
						imagenPos.idVinculo = setup.tagRefPos;
						imagenPos.categories.add(Categorias.Lineax2);
						imagenPos.categories.add(Categorias.NoParalelas);
						imagenPos.nivelDificultad = -1;
						objetos.add(imagenPos);
					}
					
					objetos.add(imagen);
				} // Termina el loop en deltasTita
			} // Termina el loop de separaciones
			if (recursoPosCreado == false) {
				System.out.println("Warning! : No se creo el recurso positivo de referencia!");
			}
	
			if (recursoNegCreado == false) {
				System.out.println("Warning! : No se creo el recurso negativo de referencia!");
			}
		} // Termina el loop de referencias
		saveSetup(setup);
		contadorDeReferenciasUmbral = contadorDeReferenciasUmbral + setup.cantidadReferencias;
		return objetos;
	}
	
	private static void saveSetup(JsonSetupExpSensibilidad jsonSetup) {
		String path = Resources.Paths.currentVersionPath+"/extras/jsonSetup"+contadorDeReferenciasUmbral+".meta";
		Json json = new Json();
		json.setUsePrototypes(false);
		FileHelper.writeFile(path, json.toJson(jsonSetup));
	}
		
	private static Imagen crearImagen() {
		contadorDeRecursos += 1;
		Imagen imagen = new Imagen();
		imagen.resourceId.id = contadorDeRecursos;
		imagen.resourceId.resourceVersion = Builder.ResourceVersion;
		imagen.infoConceptual = new InfoConcelptualExpSensibilidad();
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
		int nivelDificultad = -1; // Define un nivel de dificultad, 1 es el mas facil. -1 implica que no esta catalogado por dificultad y 0 que es compatible con cualquier dificultad (en gral para usar en las referencias, por ej rectas paralelas con las que se compara)
		InfoConcelptualExpSensibilidad infoConceptual;
	}
	
	public static class InfoConcelptualExpSensibilidad {
		public float direccionAnguloReferencia;
		public float deltaAngulo;
		public int deltaAnguloLinealizado;
		public boolean seJuntan;
		public float separacion; 
		public String DescripcionDeParametros = "AnguloReferencia: direccion media entre las dos rectas; deltaAngulo: diferencia entre los angulos de ambas rectas, siempre en modulo; deltaAnguloLinealizado: el mismo parametro pero transformado de manera que una escala linea tenga mas densidad en angulos chicos; seJuntan: diferencia si las rectas se van juntando en la direccion de referencia o se van separando; separacion: deparacion en el punto medio"; 
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
			jsonMetaData.infoConceptual = imagen.infoConceptual;
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
