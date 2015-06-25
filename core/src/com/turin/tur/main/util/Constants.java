package com.turin.tur.main.util;

public class Constants {

	// Version of game; Es importante porque dentro de una version (notada por la parte entera) se respeta compatibilidad de todos los identificadores. al cambiar la version no se cargan los datos viejo ni las estructuras viejas
	public static final float VERSION = 1.1f; 
	
	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 5.0f;
	
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

	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;

	public static final String PREFERENCES = "prefFile.txt";

	public static final String CONFIGURACION = "confFile.txt";



	// Constantes relacionadas con el diseno de experimentos
	
	public static class Diseno {
		public enum TIPOdeTRIAL {ENTRENAMIENTO,TEST}

		public enum DISTRIBUCIONESenPANTALLA {
			LINEALx3(new float[][] {{-1.5f,0},{0,0},{+1.5f,0}}),
			LINEALx2(new float[][] {{-1.5f,0},{+1.5f,0}}),
			BILINEALx6(new float[][] {{-1.5f,1},{0,1},{1.5f,1},{-1.5f,-1},{0,-1},{1.5f,-1}}),
			BILINEALx4(new float[][] {{-1,1},{1,1},{-1,-1},{-1,1}}),
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
			
			ENTRENAMIENTO(true,true,false,true),
			SELECCIONABLE(true,true,false,false),
			PREGUNTA(false,false,true,false);
			
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
	

	// Contantes de nombres de regiones para imagenes
	public class Imagenes {
		public static final String LOGOAUDIO = "LogoAudio";
		public static final String ANIMACION = "Animacion";
		public static final String STIMULILOGO = "stimuliLogo";
	}
	
	// Constantes relacionadas a archivos con informacion
	public class Files {
		public static final String USERDATA = "UserData.txt";
	}
}





