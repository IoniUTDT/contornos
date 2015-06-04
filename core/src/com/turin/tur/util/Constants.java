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
	public static final float[][] posiciones_elementos_centros = {{-1,1},{0,1},{1,1},{-1,-1},{0,-1},{1,-1}};
	// Nota! Ahora las dos variables no estan relacionadas
	public static final float[][] posiciones_elementos_vertice = {{-1.5f-LADO_CUADROS/2,-1-LADO_CUADROS/2},{0-LADO_CUADROS/2,-1-LADO_CUADROS/2},{1.5f-LADO_CUADROS/2,-1-LADO_CUADROS/2},{-1.5f-LADO_CUADROS/2,1-LADO_CUADROS/2},{0-LADO_CUADROS/2,1-LADO_CUADROS/2},{1.5f-LADO_CUADROS/2,1-LADO_CUADROS/2}};
	

}