package com.turin.tur.util;

public class Constants {

	// Visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 5.0f;
	
	// Visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;
	
	// Archivo con imagenes del juego
	public static final String TEXTURE_ATLAS_OBJECTS =
			"images/cajas.pack.atlas";
	
	// Conjunto de configuraciones
	public static final int NUMERO_ELEMENTOS = 6;
	public static final float LADO_CUADROS = 1;
	public static final float[][] posiciones_elementos_centros = {{-1.5f,1},{0,1},{1.5f,1},{-1.5f,-1},{0,-1},{1.5f,-1}};
	// Nota! Ahora las dos variables no estan relacionadas
	public static final float[][] posiciones_elementos_vertice = {{-1.5f-LADO_CUADROS/2,-1-LADO_CUADROS/2},{0-LADO_CUADROS/2,-1-LADO_CUADROS/2},{1.5f-LADO_CUADROS/2,-1-LADO_CUADROS/2},{-1.5f-LADO_CUADROS/2,1-LADO_CUADROS/2},{0-LADO_CUADROS/2,1-LADO_CUADROS/2},{1.5f-LADO_CUADROS/2,1-LADO_CUADROS/2}};

	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;
	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;

	
	// Constantes relacionadas con las cajas
	public class Box {
		public static final float TAMANO = 1; //tamaño de la caja
		public static final float DURACION_REPRODUCCION_PREDETERMINADA = 5; // medida en segundos
		public static final float TAMANO_CONTORNO_X = 0.2f;
		public static final float TAMANO_CONTORNO_Y = 0.02f;
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
	}
}