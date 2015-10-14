package com.turin.tur.main.util.builder;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Level.Significancia;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.Stadistics;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.builder.ResourcesSelectors.Agrupamientos;

public class LevelMaker {
	
	private static final String TAG = LevelMaker.class.getName();
	
	static Array<JsonResourcesMetaData> listadoRecursos = Builder.listadoRecursos;
	static Array<Array<Integer>> listadosId = Builder.listadosId;
	static Array<Agrupamientos> listadosGrupos = Builder.listadosGrupos;

	static int contadorLevels = 0;
	static int contadorTrials = 0;
	
	public static void makeLevels () {

		// Se fija q exista el paquete de recursos de la version actual
		if (!new File(Resources.Paths.fullCurrentVersionPath).exists()) {
			System.out.println("Primero debe crear los recuros version:" + Builder.ResourceVersion);
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
		Levels.MakeTutorial();
		Levels.MakeTest();
		Levels.MakeTrainingLines();
		Levels.MakeTest();
		
		
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
		
		/**
		 *  Este proceso crea un nivel de entrenamiento en paralelismo (usa los grupos impares (excepto el 5 que es justo especial) para poder medir diferencias respecto a los grupos entrenados y los no entrenados).	 *  Cada trial esta repetido dos veces.
		 */
		private static void MakeTrainingLines() {
			int numeroDeParesDeTrialporGrupo = 5; 
			
			// Crea la estructura de datos
			JsonLevel trainingLines = crearLevel();
			trainingLines.levelTitle = "Entrenamiento";
			trainingLines.randomTrialSort=true;
			trainingLines.show = true;
			
			String grupo;
			
			grupo="Paralelismo1";
			for (int i=0; i<numeroDeParesDeTrialporGrupo; i++) {
				trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.BILINEALx6,
						ResourcesSelectors.rsGetAllGrupo(grupo), TIPOdeTRIAL.TEST, ResourcesSelectors.rsGetGrupo(grupo), true, true, true));
				trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
						new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGetGrupo(grupo), true, true, true));
			}
			grupo="Paralelismo3";
			for (int i=0; i<numeroDeParesDeTrialporGrupo; i++) {
				trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.BILINEALx6,
						ResourcesSelectors.rsGetAllGrupo(grupo), TIPOdeTRIAL.TEST, ResourcesSelectors.rsGetGrupo(grupo), true, true, true));
				trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
						new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGetGrupo(grupo), true, true, true));
			}
			grupo="Paralelismo7";
			for (int i=0; i<numeroDeParesDeTrialporGrupo; i++) {
				trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.BILINEALx6,
						ResourcesSelectors.rsGetAllGrupo(grupo), TIPOdeTRIAL.TEST, ResourcesSelectors.rsGetGrupo(grupo), true, true, true));
				trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
						new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGetGrupo(grupo), true, true, true));
			}
			grupo="Paralelismo9";
			for (int i=0; i<numeroDeParesDeTrialporGrupo; i++) {
				trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.BILINEALx6,
						ResourcesSelectors.rsGetAllGrupo(grupo), TIPOdeTRIAL.TEST, ResourcesSelectors.rsGetGrupo(grupo), true, true, true));
				trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
						new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGetGrupo(grupo), true, true, true));
			}
			
			trainingLines.build(Resources.Paths.levelsPath);
			
		}
		
		private static void MakeTest() {
			/*
			 *  Armamos un nivel de test
			 */

			// Crea el nivel tutorial
			JsonLevel test = crearLevel();
			test.levelTitle = "Test";
			test.randomTrialSort=true;
			test.show = true;


			// Ahora vamos a ir creando los trials

			// seis test de reconocer paralelismo con imagenes entre imagenes
			int seleccion;
			String grupo;
			
			grupo = "Paralelismo1";
			seleccion = ResourcesSelectors.rsGetGrupo(grupo);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			seleccion = ResourcesSelectors.rsGetGrupo(grupo);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			grupo = "Paralelismo4"; 
			seleccion = ResourcesSelectors.rsGetGrupo(grupo);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			seleccion = ResourcesSelectors.rsGetGrupo(grupo);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			grupo = "Paralelismo7";
			seleccion = ResourcesSelectors.rsGetGrupo(grupo);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			seleccion = ResourcesSelectors.rsGetGrupo(grupo);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			
			// siete test de reconocer entre categoria paralela, no paralela (tres q si 4 q no)
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(Categorias.Paralelas), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(Categorias.Paralelas), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(Categorias.Paralelas), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(Categorias.NoParalelas), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(Categorias.NoParalelas), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(Categorias.NoParalelas), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(Categorias.NoParalelas), false, true, false));
			
			// Creamos los test con angulos (6 de imagen (angulos todos random)  y siete de categorias, dos agudos, dos graves dos rectos rotados y uno rectos sin rotar
			Categorias categoria;
			categoria = Categorias.Angulo;
			
			seleccion = ResourcesSelectors.rsGet(categoria);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			seleccion = ResourcesSelectors.rsGet(categoria);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			seleccion = ResourcesSelectors.rsGet(categoria);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			seleccion = ResourcesSelectors.rsGet(categoria);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			seleccion = ResourcesSelectors.rsGet(categoria);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			seleccion = ResourcesSelectors.rsGet(categoria);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			seleccion = ResourcesSelectors.rsGet(categoria);
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion, ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			
			categoria = Categorias.Agudo;
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
					new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
					new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria), false, true, false));
			
			categoria = Categorias.Grave;
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
					new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
					new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria), false, true, false));
			
			categoria = Categorias.Recto;
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
					new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.Rotado), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
					new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.Rotado), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
					new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.SinRotar), false, true, false));
			
			// Test de cuadrilateros. Hay 6 por imagenes y 7 por categorias
			
			categoria = Categorias.Cuadrilatero;
			seleccion = ResourcesSelectors.rsGet(categoria);
			
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion,ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion,ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion,ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion,ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion,ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {seleccion,ResourcesSelectors.rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
			
			
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.Rotado), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.Rotado), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.Rotado), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.Rotado), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.SinRotar), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.SinRotar), false, true, false));
			test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(categoria,Categorias.SinRotar), false, true, false));
			
			
			// Calculamos el nivel de rtas correctas para una significancia del 0.05 a partir del numero de trials y el tipo para este nivel
			// IMPORTANTE: se asume que todos los trials son de dos opciones en el caso de categorias y de 6 en el de imagen
			/*
			Array<Integer> prueba = new Array<Integer>();
			prueba.add(0);
			prueba.add(20);
			prueba.add(0);
			prueba.add(0);
			prueba.add(0);
			prueba.add(20);
			//Float[] Resultado = Stadistics.distribucion(prueba);
			//Resultado = Resultado;
			*/
			
			/*
			int numberTrialsCategorias=0;
			int numberTrialsImagen=0;
			for (JsonTrial trial:test.jsonTrials) {
				boolean esCategoria=true;
				for (int id: trial.elementosId) { // Se fija si es categoria o no viendo si hay un id mayor a los reservados
					if (id>Resources.Reservados) {
						esCategoria=false;
						break;
					}
				}
				if (esCategoria) { // agrega el contador donde corresponda
					if (trial.elementosId.length!=2) {System.out.println("OJO! se encontre un trial por categoria con mas de dos opciones en el test y no es lo que se espera");}
					numberTrialsCategorias++;
				} else {
					if (trial.elementosId.length!=6) {System.out.println("OJO! se encontre un trial por imagen con menos de seis opciones en el test y no es lo que se espera");}
					numberTrialsImagen++;
				}
			}
			// ahora hacemos la estadistica para cada caso
			Array<Float> distribucionBaseDos = new Array<Float>();
			float p = 0.5f;
			for (int i=1;i<=numberTrialsCategorias;i++) {
				distribucionBaseDos.items[i-1] = (float) (Math.pow(p, i)*Math.pow((1-p), numberTrialsCategorias-i));
			}
			// TODO Me quede aca... tengo que chequear que efectivamente de una distribucion con sentido, normalizar y sumar
			 *  
			 */
			test.build(Resources.Paths.levelsPath);

			
		}
		
		private static void MakeTutorial() {
			/*
			 * Arma el nivel Tutorial
			 */

			// Armado con la version 110 

			// Crea el nivel tutorial
			JsonLevel tutorial = crearLevel();
			tutorial.levelTitle = "Tutorial";
			tutorial.randomTrialSort=false;
			tutorial.show = true;

			// Ahora vamos a ir creando los trials

			// Bienvenida
			tutorial.jsonTrials.add(crearTrial("Bienvenido al juego", "Toque el boton para completar la pantalla", DISTRIBUCIONESenPANTALLA.LINEALx1,
					new int[] { Constants.Resources.Categorias.Siguiente.ID }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
			// Muestra rectas horizontales y verticales
			tutorial.jsonTrials.add(crearTrial("Rectas horizontales y verticales", "Toque las imagenes y escuche todos los sonidos para continuar",
					DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 22, 26, 25, 27, 23, 28 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
			// Muestra rectas en diagonal
			tutorial.jsonTrials.add(crearTrial("Rectas diagonales", "Toque las imagenes y escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.LINEALx3,
					new int[] { 29, 36, 41 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
			// Primer test sencillo
			tutorial.jsonTrials.add(crearTrial("Test por imagen", "Identifique cual imagen esta sonando", DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { 25, 27, 28, 29, 36, 23 }, TIPOdeTRIAL.TEST, 25, true, true, true));
			// Muestra angulo y pares de rectas (un angulo agudo, uno recto y uno grave, y dos pares de rectas paralelas y unas q no.)
			tutorial.jsonTrials.add(crearTrial("Angulos y rectas", "Toque las imagenes y escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6,
					new int[] { ResourcesSelectors.rsGet(Categorias.Agudo), ResourcesSelectors.rsGet(Categorias.Recto,Categorias.SinRotar), ResourcesSelectors.rsGet(Categorias.Grave), ResourcesSelectors.rsGetGrupo("Paralelismo1",Categorias.Paralelas), ResourcesSelectors.rsGetGrupo("Paralelismo4",Categorias.NoParalelas), ResourcesSelectors.rsGetGrupo("Paralelismo9",Categorias.Paralelas) }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
			// Segundo test
			tutorial.jsonTrials.add(crearTrial("Test por imagen", "Identifique cual imagen esta sonando", DISTRIBUCIONESenPANTALLA.BILINEALx4,
					new int[] { ResourcesSelectors.rsGet(Categorias.Agudo), ResourcesSelectors.rsGet(Categorias.Recto,Categorias.SinRotar), ResourcesSelectors.rsGetGrupo("Paralelismo7"), ResourcesSelectors.rsGetGrupo("Paralelismo2") }, TIPOdeTRIAL.TEST, Constants.Resources.Categorias.Nada.ID, true, true, true));
			// Ultima presentacion, cuadrilateros
			tutorial.jsonTrials.add(crearTrial("Cuadrilateros", "Toque las imagenes y escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx4,
					new int[] { ResourcesSelectors.rsGet(Categorias.Cuadrado,Categorias.SinRotar), ResourcesSelectors.rsGet(Categorias.Rombo,Categorias.Rotado), ResourcesSelectors.rsGet(Categorias.Cuadrado,Categorias.Rotado), ResourcesSelectors.rsGet(Categorias.Rombo,Categorias.SinRotar) }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
			// tercer test (por categorias 1) */
			tutorial.jsonTrials.add(crearTrial("Test por categorias", "Identifique a que categoria pertenece la imagen que suena", DISTRIBUCIONESenPANTALLA.BILINEALx4,
					new int[] { Constants.Resources.Categorias.Cuadrilatero.ID, Constants.Resources.Categorias.Lineax2.ID, Constants.Resources.Categorias.Rombo.ID, Constants.Resources.Categorias.Cuadrado.ID }, TIPOdeTRIAL.TEST, ResourcesSelectors.rsGet(Categorias.Cuadrado,Categorias.Rotado), true, true, true));
			
			addSignificanciaImagen(tutorial);
			addSignificanciaCategoria(tutorial);
			addSignificanciaTotal(tutorial);
			
			tutorial.build(Resources.Paths.levelsPath);
			
			
		}

		/**
		 * Agrega al json del level la entrada que corresponde al analisis de significancia considerando todo el nivel
		 * @param level
		 */
		private static void addSignificanciaTotal(JsonLevel level) {
			Significancia significancia = new Significancia();
			significancia.title = "Evaluacion Global";
			significancia.descripcion = "Informacion de la significancia total del nivel considerando al ciego como una respuesta random";
			significancia.pValue=0.05f;
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
			significancia.title = "Evaluacion de selección de imagenes";
			significancia.descripcion = "Informacion de la significancia total del nivel considerando al ciego como una respuesta random solo en los trials que se debe seleccionar entre imagenes";
			significancia.pValue=0.05f;
			// Filtra los trials que son test para no procesar los que son "entrenamiento"
			Array<Integer> listaIds = new Array<Integer>();
			for (JsonTrial json:level.jsonTrials) {
				if (json.modo == TIPOdeTRIAL.TEST){
					if (json.rtaCorrectaId > Constants.Resources.Reservados) { // Se fija si la rta correcta apunta a una categoria o no en funcion de que las categorias estan asociadas a IDs reservados
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
			significancia.title = "Evaluacion de selección de imagenes";
			significancia.descripcion = "Informacion de la significancia total del nivel considerando al ciego como una respuesta random solo en los trials que se debe seleccionar entre imagenes";
			significancia.pValue=0.05f;
			// Filtra los trials que son test para no procesar los que son "entrenamiento"
			Array<Integer> listaIds = new Array<Integer>();
			for (JsonTrial json:level.jsonTrials) {
				if (json.modo == TIPOdeTRIAL.TEST){
					if (json.rtaCorrectaId <= Constants.Resources.Reservados) { // Se fija si la rta correcta apunta a una categoria o no en funcion de que las categorias estan asociadas a IDs reservados
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
				if (acumulado > 1-significancia.pValue) {
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
		return jsonTrial;
	}

	private static JsonLevel crearLevel() {
		// Crea un JsonLevel y aumenta en 1 el contador de niveles
		contadorLevels += 1;
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.Id = contadorLevels;
		jsonLevel.resourceVersion = Builder.ResourceVersion;
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
}
