package com.turin.tur.main.util;

import com.badlogic.gdx.math.MathUtils;

public class Constants {

	// Version of game; Es importante porque dentro de una version (notada por la parte entera) se respeta compatibilidad de todos los identificadores. al cambiar la version no se cargan los datos viejo ni las estructuras viejas
	public static final float VERSION = 1.1f; 

	
	// Version of game for internal use 
	public static int version () {
		int version_temp = MathUtils.roundPositive(Constants.VERSION);
		int temp;
		if (version_temp > Constants.VERSION) {
			temp = -1;
		} else {
			temp = 0;
		}
		return version_temp + temp;
	}
	
	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 7.0f;
	
	// Visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;
	
	// Archivo con imagenes del juego
	public static final String TEXTURE_ATLAS_OBJECTS =
			"images/cajas.pack.atlas";
	public static final String TEXTURE_ATLAS_IMAGES_EXP =
			"images/imagesexp.pack.atlas";
	
	public static final String TEXTURE_ATLAS_LIBGDX_UI =
			"images/uiskin.atlas";
	// Location of description file for skins
	public static final String SKIN_LIBGDX_UI =
			"skins/uiskin.json";

	// Conjunto de configuraciones
	public static final int NUMERO_ELEMENTOS = 6;
	public static final float LADO_CUADROS = 1;
	public static final float[][] posiciones_elementos_centros = {{-1.5f,1},{0,1},{1.5f,1},{-1.5f,-1},{0,-1},{1.5f,-1}};

	
	// ESTO no parece hacer nada!
	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 100.0f;
	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 80.0f;

	public static final String PREFERENCES = "prefFile.txt";

	public static final String CONFIGURACION = "confFile.txt";

	public static final String USERFILE = "experimentalconfig/user.txt";

	public static final String USERLOG = "logs/user.log";

	public static final String TOUCHLOG = "logs/touchs.log";

	public static final int NUMERODENIVELES = 2; // Nota esto es temporal, hay que implementar una clase session que maneje todas las cuestiones globales del juego



	// Constantes relacionadas con el diseno de experimentos
	
	public static class Diseno {
		
		/*
		 * Hay dos tipo de trial: Entrenamiento, Test
		 * 
		 * En el primero de los casos se muestra un conjunto de estimulos experimentales y se los puede reproducir para escuchar como suenan. En principio el trial se supera cuando se tocaron todos los estimulos
		 * En el caso del Test, se muestra un estimulo especifico y el usuario debe elegir entre diferentes opciones, que pueden ser imagenes especificas o imagenes que representen una categoria
		 *  
		 */
		public enum TIPOdeTRIAL {ENTRENAMIENTO,TEST}

		public enum DISTRIBUCIONESenPANTALLA {
			LINEALx3(new float[][] {{-1.5f,0},{0,0},{+1.5f,0}}),
			LINEALx2(new float[][] {{-1.5f,0},{+1.5f,0}}),
			BILINEALx6(new float[][] {{-1.5f,1},{0,1},{1.5f,1},{-1.5f,-1},{0,-1},{1.5f,-1}}),
			BILINEALx4(new float[][] {{-1,1},{1,1},{-1,-1},{1,-1}}),
			BILINEALx2(new float[][] {{-1.5f,0},{+1.5f,0}}), LINEALx1(new float[][] {{0,0}});
			
			public float[][] distribucion;
			DISTRIBUCIONESenPANTALLA (final float[][] distribucion) {
				this.distribucion=distribucion;
			}
			
			public float X (int i){
				return this.distribucion[i][0];
			}
			public float Y (int i){
				return this.distribucion[i][1];
			}
		}
	
		/*
		public enum TIPOdeCAJA {
			
			/*
			 * Hay diferentes tipos de caja, las de entrenamiento, las imagenes, las de estimulo, y las de categoria.
			 * 
			 * Las de entrenamiento sirven para entrenar a el usuario. Son cajas que muestran un contenido que tiene asociado un sonido. Cuando el usuario las selecciona deben reproducir el sonido correspondiente
			 * Las de imagenes sirven para obtener una respuesta cuando se busca identificar un contenido ya sea como una imagen especifica o una que represente una categoria. Muestran su contenido, se pueden seleccionar, pero no reproducen nada
			 * Las de estimulo son las que sirven para generar un estimulo auditivo (no visual) que el usuario deba identificar ya sea en una categoria o buscando la imagen correspondiente. Estas cajas se reproducen solas y no son seleccionables
			 * 
			 */
			/*
			ENTRENAMIENTO(true,true,false,false,true),
			IMAGEN(true,true,true,false,false), 
			ESTIMULO(false,false,false,true,true);   
			
			public boolean mostrarContenido; // Determina si se muestra el contenido o se muestra un signo de pregunta
			public boolean seleccionable; // Determina si se puede seleccionar o no (por ej las preguntas de los trials de test no son seleccionables)
			public boolean respondibles; // Determina si contienen una respuesta o no
			public boolean reproduccionAutomatica; // Determina si el sonido entra en loop o no (las de entranamiento no tienen loop de de test si)
			public boolean reproducible; // Determina si se puede reproducir o no
			
			
			private TIPOdeCAJA(boolean mostrarContenido, boolean seleccionable, boolean respondibles, boolean reproduccionAutomatica, boolean reproducible) {
				this.mostrarContenido = mostrarContenido;
				this.seleccionable = seleccionable;
				this.respondibles = respondibles;
				this.reproduccionAutomatica = reproduccionAutomatica;
				this.reproducible = reproducible;
			}
		}
		*/
		
		public enum Categorias {
			LINEA, TUTORIAL, ANGULO, AGUDO, GRAVE, RECTO, LINEASx2, PARALELAS, noPARALELAS, CUADRILATERO, CUADRADO, RECTANGULO, ROMBO, TEXTO 
		}
	}
	
	// Constantes relacionadas con las cajas
	public class Box {
		public static final float TAMANO = 1; //tamaño de la caja
		public static final float TAMANO_ESTIMULO = 2; //tamaño de la caja
		public static final float DURACION_REPRODUCCION_PREDETERMINADA = 5; // medida en segundos
		public static final float TAMANO_CONTORNO_X = 0.2f;
		public static final float TAMANO_CONTORNO_Y = 0.02f;
		public static final float SHIFT_MODO_SELECCIONAR = 1.5f;
		public static final float SHIFT_ESTIMULO_MODO_SELECCIONAR = -2.5f;
		public static final float DELAY_ESTIMULO_MODO_SELECCIONAR = 2f;
		public static final float ANIMATION_ANSWER_TIME = 1f;
		public static final float SELECT_BOX_ANCHO_RTA = 0.1f;
	}
	
	// Constantes para los touch
	public class Touch {
	
		public class Type {
			public static final String NOTHING = "nothing";
			public static final String IMAGE = "image";
		}
		
		public class ToDo {
			public static final String NOTHING = "nothing";
			public static final String DETECTOVERLAP = "detectOverlap";
		}
	}

	// Constantes relacionadas a IDs de cosas fijas
	
	public class IDs {
		
		public class Resources {
			public static final int Reservados = 20;
			public static final int sinDatos = 0;
			public static final int textSiguiente = 1;
			public static final int textNull = 0;
		}
	}
}





