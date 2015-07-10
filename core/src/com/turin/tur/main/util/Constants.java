package com.turin.tur.main.util;

public class Constants {

	// Version of game; Es importante porque dentro de una version (notada por la parte entera) se respeta compatibilidad de todos los identificadores. al cambiar la version no se cargan los datos viejo ni las estructuras viejas
	public static final float VERSION = 1.1f; 
	
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
			"test/uiskin.json";

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



	// Constantes relacionadas con el diseno de experimentos
	
	public static class Diseno {
		
		/*
		 * Hay tres tipo de trial: Entrenamiento, TestContenido, TestCategoria
		 * 
		 * En el primero de los casos se muestra un conjunto de estimulos experimentales y se los puede reproducir para escuchar como suenan. En principio el trial se supera cuando se tocaron todos los estimulos
		 * En el caso del TestId se reproduce (sin mostrar) un estimulo auditivo, y el usuario debe seleccionar el estimulo visual correspondiente
		 * En el caso de TestCategoria se reproduce un estimulo (no tiene porque ser previamente conocido) y el usuario debe seleccionar si pertenece o no a una categoria o a cual entre varias categorias pertenece
		 * 
		 */
		public enum TIPOdeTRIAL {ENTRENAMIENTO,TESTCONTENIDO,TESTCATEGORIA}

		public enum DISTRIBUCIONESenPANTALLA {
			LINEALx3(new float[][] {{-1.5f,0},{0,0},{+1.5f,0}}),
			LINEALx2(new float[][] {{-1.5f,0},{+1.5f,0}}),
			BILINEALx6(new float[][] {{-1.5f,1},{0,1},{1.5f,1},{-1.5f,-1},{0,-1},{1.5f,-1}}),
			BILINEALx4(new float[][] {{-1,1},{1,1},{-1,-1},{1,-1}}),
			BILINEALx2(new float[][] {{-1.5f,0},{+1.5f,0}});
			
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
	
		public enum TIPOdeCAJA {
			
			/*
			 * Hay diferentes tipos de caja, las de entrenamiento, las seleccionables, las de estimulo, y las de categoria.
			 * 
			 * Las de entrenamiento sirven para entrenar a el usuario. Son cajas que muestran un contenido que tiene asociado un sonido. Cuando el usuario las selecciona deben reproducir el sonido correspondiente
			 * Las seleccionables sirven para obtener una respuesta cuando se busca identificar un contenido especifico. Muestran su contenido, se pueden seleccionar, pero no reproducen nada
			 * Las de estimulo son las que sirven para generar un estimulo auditivo (no visual) que el usuario deba identificar ya sea en una categoria o buscando la imagen correspondiente. Estas cajas se reproducen solas y no son seleccionables
			 * Las de categoria poseen una imagen (con el nombre de la categoria) que no tiene un sonido asociado. Sirven para ofrecer respuestas al usuario. Son seleccionables, pero no reproducen sonido.  
			 * 
			 */
			
			ENTRENAMIENTO(true,true,false,true),
			SELECCIONABLE(true,true,false,false), 
			ESTIMULO(false,false,true,true),   
			CATEGORIA(true,true,false,false); 
			
			public boolean mostrarContenido; // Determina si se muestra el contenido o se muestra un signo de pregunta
			public boolean seleccionable; // Determina si se puede seleccionar o no (por ej las preguntas de los trials de test no son seleccionables)
			public boolean reproduccionAutomatica; // Determina si el sonido entra en loop o no (las de entranamiento no tienen loop de de test si)
			public boolean reproducible; // Determina si se puede reproducir o no
			
			private TIPOdeCAJA(boolean mostrarContenido, boolean seleccionable, boolean reproduccionAutomatica, boolean reproducible) {
				this.mostrarContenido = mostrarContenido;
				this.seleccionable = seleccionable;
				this.reproduccionAutomatica = reproduccionAutomatica;
				this.reproducible = reproducible;
			}
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
	
}





