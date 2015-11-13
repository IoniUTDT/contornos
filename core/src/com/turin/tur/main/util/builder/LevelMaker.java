package com.turin.tur.main.util.builder;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Level.Significancia;
import com.turin.tur.main.diseno.Level.TIPOdeSIGNIFICANCIA;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.diseno.Trial.ParametrosSetup;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Stadistics;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.builder.ResourcesMaker.JsonSetupExpV1;
import com.turin.tur.main.util.builder.ResourcesSelectors.Agrupamientos;

public class LevelMaker {
	
	private static final String TAG = LevelMaker.class.getName();
	
	static Array<JsonResourcesMetaData> listadoRecursos = Builder.listadoRecursos;
	static Array<Array<Integer>> listadosId = Builder.listadosId;
	static Array<Agrupamientos> listadosGrupos = Builder.listadosGrupos;

	static int contadorLevels = 0;
	static int contadorTrials = 0;
	
	public static void makeLevels () {
		
		// Verifica que no haya niveles ya numerados con la version marcada
		File file = new File(Resources.Paths.fullLevelsPath + "level" + 1 + ".meta");
		if (file.exists()) {
			String savedData = FileHelper.readLocalFile(Resources.Paths.levelsPath + "level" + 1 + ".meta");
			if (!savedData.isEmpty()) {
				Json json = new Json();
				JsonLevel jsonLevel = json.fromJson(JsonLevel.class, savedData);
				if (jsonLevel.levelVersion >= Builder.levelVersion) {
					System.out.println("Cambie la version de los niveles a crear para que sean mayor a la version actual: " + Builder.levelVersion);
					System.exit(0);
				}
			}
		} 
		
		// Se fija q exista el paquete de recursos de la version actual
		if (!new File(Resources.Paths.fullCurrentVersionPath).exists()) {
			System.out.println("Primero debe crear los recursos version:" + Builder.ResourceVersion);
			return;
		}
		
		categorizeResources();// Categoriza los recursos para que despues se pueda seleccionar recursos conceptualmente
		
		// Manda los levels que ya estaban creados a una carpeta nueva para archivarlos
		File oldDir = new File(Resources.Paths.fullLevelsPath);
		String str = Resources.Paths.fullLevelsPath.substring(0, Resources.Paths.fullLevelsPath.length()-1)+"olds/"+TimeUtils.millis()+"/";
		File newDir = new File(str);
		newDir.mkdirs();
		System.out.println(oldDir.renameTo(newDir));
		new File(Resources.Paths.fullLevelsPath).mkdirs();


		// Crea los niveles
		Levels.MakeLevelParalelismo(0);
	}
	
	



	private static void categorizeResources() {
		for (int i=0;i<Categorias.values().length+1;i++) {
			listadosId.add(new Array<Integer>());
		}
		File[] archivos;
		// Primero busca la lista de archivos de interes
		File dir = new File(Resources.Paths.fullCurrentVersionPath);
		archivos = dir.listFiles(new MetaFileFilter());
		// Ahora carga la info de cada archivo encontrado
		for (File archivo: archivos) {
			String savedData = FileHelper.readFile(archivo.getPath());
			if (!savedData.isEmpty()) {
				Json json = new Json();
				listadoRecursos.add(json.fromJson(JsonResourcesMetaData.class, savedData));
			} else { Gdx.app.error(TAG,"Error leyendo el archivo de metadatos"); }
		}
		// Clasifica los recursos por categorias. Usa los id de las categorias para armar la lista. Tambien por grupos
		for (JsonResourcesMetaData metadata:listadoRecursos) {
			for (Categorias categoria:metadata.categories) {
				listadosId.get(categoria.ID).add(metadata.resourceId.id);
			}
			if (metadata.idVinculo != null) {
				boolean nuevo=true;
				for (Agrupamientos agrupamiento:listadosGrupos) {
					if (agrupamiento.nombre.equals(metadata.idVinculo)) {
						agrupamiento.ids.add(metadata.resourceId.id);
						nuevo = false;
						break;
					}
				}
				if (nuevo) {
					Agrupamientos agrupamiento = new Agrupamientos();
					agrupamiento.nombre = metadata.idVinculo;
					agrupamiento.ids.add(metadata.resourceId.id);
					listadosGrupos.add(agrupamiento);
				}
			}
		}
		System.out.println("Recursos catalogados");
		
	}

	private static class Levels {
			


		
		private static void MakeLevelParalelismo (int indiceReferencia) {
			JsonSetupExpV1 setup = loadSetup();
			// Creamos el nivel
			JsonLevel level = crearLevel();
			level.levelTitle = "Analisis de sensibilidad";
			level.randomTrialSort=false;
			level.show = true;
			level.trueRate = 0.9f;
			
			int R = indiceReferencia;
			// Agregamos los niveles sin estimulo
			for (int S=0; S<setup.cantidadSeparaciones; S++) {
				String tag = "R"+R+"S"+S;
				JsonTrial trial = crearTrial("Seleccione si es paralelo o no", "", DISTRIBUCIONESenPANTALLA.LINEALx2,
						new int[] {Constants.Resources.Categorias.Paralelas.ID,Constants.Resources.Categorias.NoParalelas.ID }, TIPOdeTRIAL.TEST, ResourcesSelectors.findResourceByTag(tag) , false, true, false);
				trial.parametros.D=0;
				trial.parametros.R=R;
				trial.parametros.S=S;
				level.jsonTrials.add(trial);
			}
			// Agregamos dos niveles por cada delta 
			for (int D=1; D<=setup.cantidadDeltas; D++) {
				int S = MathUtils.random(0, setup.cantidadSeparaciones-1);
				String tag = "R"+R+"S"+S+"D"+D;
				if (MathUtils.randomBoolean()) {
					tag = tag+"+";
				} else {
					tag = tag+"-";
				}
				JsonTrial trial = crearTrial("Seleccione si es paralelo o no", "", DISTRIBUCIONESenPANTALLA.LINEALx2,
						new int[] {Constants.Resources.Categorias.Paralelas.ID,Constants.Resources.Categorias.NoParalelas.ID }, TIPOdeTRIAL.TEST, ResourcesSelectors.findResourceByTag(tag) , false, true, false);
				trial.parametros.D=D;
				trial.parametros.R=R;
				trial.parametros.S=S;
				level.jsonTrials.add(trial);
				// Otro
				S = MathUtils.random(0, setup.cantidadSeparaciones-1);
				tag = "R"+R+"S"+S+"D"+D;
				if (MathUtils.randomBoolean()) {
					tag = tag+"+";
				} else {
					tag = tag+"-";
				}
				trial = crearTrial("Seleccione si es paralelo o no", "", DISTRIBUCIONESenPANTALLA.LINEALx2,
						new int[] {Constants.Resources.Categorias.Paralelas.ID,Constants.Resources.Categorias.NoParalelas.ID }, TIPOdeTRIAL.TEST, ResourcesSelectors.findResourceByTag(tag) , false, true, false);
				trial.parametros.D=D;
				trial.parametros.R=R;
				trial.parametros.S=S;
				level.jsonTrials.add(trial);
			}
			
			
			level.build(Resources.Paths.levelsPath);
		}
		
		private static JsonSetupExpV1 loadSetup() {
			String path = Resources.Paths.currentVersionPath+"extras/jsonSetup.meta";
			String savedData = FileHelper.readLocalFile(path);
			Json json = new Json();
			return json.fromJson(JsonSetupExpV1.class, savedData);
		}
		
		
		/**
		 * Agrega al json del level la entrada que corresponde al analisis de significancia considerando todo el nivel
		 * @param level
		 */
		private static void addSignificanciaTotal(JsonLevel level) {
			Significancia significancia = new Significancia();
			significancia.tipo=TIPOdeSIGNIFICANCIA.COMPLETO;
			// Filtra los trials que son test para no procesar los que son "entrenamiento"
			Array<Integer> listaIdsSoloTests = new Array<Integer>();
			for (JsonTrial json:level.jsonTrials) {
				if (json.modo == TIPOdeTRIAL.TEST) {
					listaIdsSoloTests.add(json.Id);
				}
			}
			// Nota, aca tengo el mismo problema de siempre de acceder a las listas! Estoy haciendo mucho codigo pero no se como solucionarlo
			significancia.trialIncluidos = new Integer[listaIdsSoloTests.size];
			for (int i=0; i<listaIdsSoloTests.size;i++) {
				significancia.trialIncluidos[i]=listaIdsSoloTests.get(i);
			}
			addSignificancia (significancia, level);
		}
		
		/**
		 * Agrega al json del level la entrada que corresponde al analisis de significancia considerando los trials que son seleccion de imagen
		 * @param level
		 */
		private static void addSignificanciaImagen(JsonLevel level) {
			Significancia significancia = new Significancia();
			significancia.tipo = TIPOdeSIGNIFICANCIA.IMAGEN;
			// Filtra los trials que son test para no procesar los que son "entrenamiento"
			Array<Integer> listaIds = new Array<Integer>();
			for (JsonTrial json:level.jsonTrials) {
				if (json.modo == TIPOdeTRIAL.TEST){
					boolean categoria=true;
					for (int id:json.elementosId) {
						if (id > Constants.Resources.Reservados) { // Se fija si los elementos apuntan a una categoria o no en funcion de que las categorias estan asociadas a IDs reservados
							categoria=false;
							break;
						}
					}
					if (!categoria) {
						listaIds.add(json.Id);
					}
				}
			}
			// Nota, aca tengo el mismo problema de siempre de acceder a las listas! Estoy haciendo mucho codigo pero no se como solucionarlo
			significancia.trialIncluidos = new Integer[listaIds.size];
			for (int i=0; i<listaIds.size;i++) {
				significancia.trialIncluidos[i]=listaIds.get(i);
			}
			addSignificancia (significancia, level);
		}
		
		/**
		 * Agrega al json del level la entrada que corresponde al analisis de significancia considerando los trials que son seleccion de imagen
		 * @param level
		 */
		private static void addSignificanciaCategoria(JsonLevel level) {
			Significancia significancia = new Significancia();
			significancia.tipo = TIPOdeSIGNIFICANCIA.CATEGORIA;
			// Filtra los trials que son test para no procesar los que son "entrenamiento"
			Array<Integer> listaIds = new Array<Integer>();
			for (JsonTrial json:level.jsonTrials) {
				if (json.modo == TIPOdeTRIAL.TEST){
					boolean categoria=true;
					for (int id:json.elementosId) {
						if (id > Constants.Resources.Reservados) { // Se fija si los elementos apuntan a una categoria o no en funcion de que las categorias estan asociadas a IDs reservados
							categoria=false;
							break;
						}
					}
					if (categoria) { 
						listaIds.add(json.Id);
					}
				}
			}
			// Nota, aca tengo el mismo problema de siempre de acceder a las listas! Estoy haciendo mucho codigo pero no se como solucionarlo
			significancia.trialIncluidos = new Integer[listaIds.size];
			for (int i=0; i<listaIds.size;i++) {
				significancia.trialIncluidos[i]=listaIds.get(i);
			}
			addSignificancia (significancia, level);
		}

		/**
		 *  Ultimo paso del calculo de significancias que es independiente del subconjunto de datos elegidos
		 * @param significancia
		 * @param level
		 */
		private static void addSignificancia (Significancia significancia, JsonLevel level) {
			
			significancia.histogramaTrials = trialHistograma(significancia.trialIncluidos,level);
			significancia.distribucion = Stadistics.distribucion(significancia.histogramaTrials);
			Float acumulado=0f;
			significancia.exitoMinimo=0;
			for (int i=0; i<significancia.distribucion.length; i++) {
				acumulado = acumulado + significancia.distribucion[i];
				if (acumulado > 1-significancia.tipo.pValue) {
					significancia.exitoMinimo=i;
					break;
				}
			}
			level.significancias.add(significancia);
		}
		
		private static int[] trialHistograma(Integer[] trialIncluidos, JsonLevel level) {
			int[] histograma_t = new int[10]; // Nota, lo inicializamos en 10 porque asumimos que nunca va a haber ms de 10, despues se recorta
			// Contamos cuantos trials de cada numero de opciones hay 
			for (JsonTrial json:level.jsonTrials) {
				int n = json.elementosId.length;
				if (Arrays.asList(trialIncluidos).contains(json.Id)) {
					histograma_t[n]++;
				}
			}
			// Recortamos los resultados
			int longitud = histograma_t.length;
			while (true) {
				if ((histograma_t[longitud-1]!=0) || (longitud==1)){
					break;
				} else { longitud--;}
			}
			int[] histograma = new int[longitud];
			for (int i=0;i<longitud;i++) {
				histograma[i]=histograma_t[i];
			}
			return histograma;
		}
	}
	
	private static JsonTrial crearTrial(String title, String caption, DISTRIBUCIONESenPANTALLA distribucion, int[] elementos, TIPOdeTRIAL modo,
			int rtaCorrecta, Boolean randomAnswer, Boolean randomSort, Boolean feedback) {
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
		jsonTrial.resourceVersion = Builder.ResourceVersion;
		jsonTrial.feedback = feedback;
		jsonTrial.parametros = new ParametrosSetup();
		return jsonTrial;
	}

	private static JsonLevel crearLevel() {
		// Crea un JsonLevel y aumenta en 1 el contador de niveles
		contadorLevels += 1;
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.Id = contadorLevels;
		jsonLevel.resourceVersion = Builder.ResourceVersion;
		jsonLevel.levelVersion = Builder.levelVersion;
		return jsonLevel;
	}
	
	public static class MetaFileFilter implements FileFilter
	{
		private final String[] okFileExtensions =
				new String[] { "meta" };

		@Override
		public boolean accept(File file)
		{
			for (String extension : okFileExtensions)
			{
				if (file.getName().toLowerCase().endsWith(extension))
				{
					return true;
				}
			}
			return false;
		}
	}
	
	public static class Dificultad {
		
		int dificultad;
		
		public Dificultad (int dificultad){
			this.dificultad=dificultad;
		}
		
		public Dificultad(){
			
		}
	}
}
