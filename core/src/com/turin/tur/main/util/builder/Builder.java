package com.turin.tur.main.util.builder;

import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.builder.ResourcesSelectors.Agrupamientos;


public class Builder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	private static final String TAG = Builder.class.getName();

	public static int height = Resources.Paths.height;
	public static int width = Resources.Paths.width;
	public static final int ResourceVersion = 125;
	public static final int levelVersion = 9;
	
	static final Boolean makeLevels = false;
	static final Boolean makeResources = false;
	
	public static Array<JsonResourcesMetaData> listadoRecursos = new Array<JsonResourcesMetaData>();
	public static Array<Array<Integer>> listadosId = new Array<Array<Integer>>();
	public static Array<Agrupamientos> listadosGrupos = new Array<Agrupamientos>();
	
	public static void build() {

		if (makeResources) {
			ResourcesMaker.BuildResources();
			System.exit(0);
		}	
		if (makeLevels) {
			
			LevelMaker.makeLevels();
			LevelExport.createStructure();
			System.exit(0);
		}

	}
}
