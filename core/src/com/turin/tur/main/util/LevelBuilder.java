package com.turin.tur.main.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.ExperimentalObject.JsonResourcesMetaData;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.diseno.Trial.ResourceId;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.SVGtoSound.SvgFileFilter;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

public class LevelBuilder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	private static final String TAG = LevelBuilder.class.getName();

	public static int height = Resources.Paths.height;
	public static int width = Resources.Paths.width;

	
	static int contadorLevels = 0;
	static int contadorTrials = 0;

	
	static final Boolean makeLevels = true;
	static final Boolean makeResources = true;
	
	public static Array<JsonResourcesMetaData> listadoRecursos = new Array<JsonResourcesMetaData>();
	public static Array<Array<Integer>> listadosId = new Array<Array<Integer>>();
	public static Array<Agrupamientos> listadosGrupos = new Array<Agrupamientos>();
	
	public static void build() {

		if (makeResources) {
			ResourcesMaker.BuildResources();
		}	
		if (makeLevels) {
			makeLevels();
		}

	}


	private static void makeLevels () {

		// Se fija q exista el paquete de recursos de la version actual
		if (!new File(Resources.Paths.fullCurrentVersionPath).exists()) {
			System.out.println("Primero debe crear los recuros version:" + Resources.Paths.ResourceVersion);
			return;
		}
		
		categorizeResources();// Categoriza los recusos
		
		// Manda los levels que ya estaban creados a una carpeta nueva
		File oldDir = new File(Resources.Paths.fullLevelsPath);
		String str = Resources.Paths.fullLevelsPath.substring(0, Resources.Paths.fullLevelsPath.length()-1)+"olds/"+TimeUtils.millis()+"/";
		File newDir = new File(str);
		newDir.mkdirs();
		System.out.println(oldDir.renameTo(newDir));
		new File(Resources.Paths.fullLevelsPath).mkdirs();

		MakeTutorial();
		MakeTest();
		//MakeTestDificil(); // No tiene mucho sentido
		MakeTrainingLines();
		MakeTest();
		createStructure();
	}
	
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
					rsGetAllGrupo(grupo), TIPOdeTRIAL.TEST, rsGetGrupo(grupo), true, true, true));
			trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGetGrupo(grupo), true, true, true));
		}
		grupo="Paralelismo3";
		for (int i=0; i<numeroDeParesDeTrialporGrupo; i++) {
			trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.BILINEALx6,
					rsGetAllGrupo(grupo), TIPOdeTRIAL.TEST, rsGetGrupo(grupo), true, true, true));
			trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGetGrupo(grupo), true, true, true));
		}
		grupo="Paralelismo7";
		for (int i=0; i<numeroDeParesDeTrialporGrupo; i++) {
			trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.BILINEALx6,
					rsGetAllGrupo(grupo), TIPOdeTRIAL.TEST, rsGetGrupo(grupo), true, true, true));
			trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGetGrupo(grupo), true, true, true));
		}
		grupo="Paralelismo9";
		for (int i=0; i<numeroDeParesDeTrialporGrupo; i++) {
			trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.BILINEALx6,
					rsGetAllGrupo(grupo), TIPOdeTRIAL.TEST, rsGetGrupo(grupo), true, true, true));
			trainingLines.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
					new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGetGrupo(grupo), true, true, true));
		}
		
		trainingLines.build(Resources.Paths.levelsPath);
		
	}

	private static int[] rsGetAllGrupo(String agrupamientoPedido) {
		int[] recursos = new int[] {0,0,0,0,0,0}; //Inicializa el vector con datos nulos, total solo puede tener 6 elementos
		for (Agrupamientos agrupamiento : listadosGrupos) {
			if (agrupamiento.nombre.equals(agrupamientoPedido)) {
				if (agrupamiento.ids.size==6) {
					for (int i=0; i<6; i++) {
						recursos[i] = agrupamiento.ids.get(i); 
					}
					return recursos;
				} 
			}
		}
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
		return null;
	}
	

	@SuppressWarnings("unused")
	private static void MakeTestDificil() {
		/*
		 * 
		 * Vamos a crear un nivel mas dificil
		 * 
		 */

		// Crea el nivel test inicial mas dificil
		JsonLevel Test1d = crearLevel();
		Test1d.levelTitle = "Test Inicial Dificil";
		Test1d.randomTrialSort=true;
		Test1d.show = true;


		// Ahora vamos a ir creando los trials

		// seis test de reconocer paralelismo con imagenes entre imagenes
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {681, 683}, TIPOdeTRIAL.TEST, 681, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {680, 684}, TIPOdeTRIAL.TEST, 680, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {690, 691}, TIPOdeTRIAL.TEST, 690, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {708, 711}, TIPOdeTRIAL.TEST, 708, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {727, 728}, TIPOdeTRIAL.TEST, 727, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {732, 734}, TIPOdeTRIAL.TEST, 732, true, true, false));
		// siete test de reconocer entre categoria paralela, no paralela
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 683, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 715, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 728, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 703, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 720, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 685, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 723, false, true, false));
		// Creamos los test con angulos (6 de imagen y siete de categorias)
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {421, 425}, TIPOdeTRIAL.TEST, 425, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {467, 451}, TIPOdeTRIAL.TEST, 451, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {532, 548}, TIPOdeTRIAL.TEST, 532, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {557, 584}, TIPOdeTRIAL.TEST, 557, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {671, 670}, TIPOdeTRIAL.TEST, 671, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {624, 600}, TIPOdeTRIAL.TEST, 624, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 532, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 528, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 533, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 514, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 522, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 598, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 603, false, true, false));
		// Test de cuadrilateros
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {737, 764}, TIPOdeTRIAL.TEST, 737, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {759, 740}, TIPOdeTRIAL.TEST, 759, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {754, 750}, TIPOdeTRIAL.TEST, 754, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {762, 774}, TIPOdeTRIAL.TEST, 774, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {784, 781}, TIPOdeTRIAL.TEST, 781, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {772, 776}, TIPOdeTRIAL.TEST, 776, true, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 770, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 758, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 760, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 784, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 759, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 737, false, true, false));
		Test1d.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 738, false, true, false));
		Test1d.build(Resources.Paths.levelsPath);
		
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
		seleccion = rsGetGrupo(grupo);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		seleccion = rsGetGrupo(grupo);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		grupo = "Paralelismo4"; 
		seleccion = rsGetGrupo(grupo);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		seleccion = rsGetGrupo(grupo);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		grupo = "Paralelismo7";
		seleccion = rsGetGrupo(grupo);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		seleccion = rsGetGrupo(grupo);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGetGrupo(grupo,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		
		// siete test de reconocer entre categoria paralela, no paralela (tres q si 4 q no)
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGet(Categorias.Paralelas), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGet(Categorias.Paralelas), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGet(Categorias.Paralelas), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGet(Categorias.NoParalelas), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGet(Categorias.NoParalelas), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGet(Categorias.NoParalelas), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, rsGet(Categorias.NoParalelas), false, true, false));
		
		// Creamos los test con angulos (6 de imagen (angulos todos random)  y siete de categorias, dos agudos, dos graves dos rectos rotados y uno rectos sin rotar
		Categorias categoria;
		categoria = Categorias.Angulo;
		
		seleccion = rsGet(categoria);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		seleccion = rsGet(categoria);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		seleccion = rsGet(categoria);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		seleccion = rsGet(categoria);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		seleccion = rsGet(categoria);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		seleccion = rsGet(categoria);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		seleccion = rsGet(categoria);
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion, rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		
		categoria = Categorias.Agudo;
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, rsGet(categoria), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, rsGet(categoria), false, true, false));
		
		categoria = Categorias.Grave;
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, rsGet(categoria), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, rsGet(categoria), false, true, false));
		
		categoria = Categorias.Recto;
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.Rotado), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.Rotado), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.SinRotar), false, true, false));
		
		// Test de cuadrilateros. Hay 6 por imagenes y 7 por categorias
		
		categoria = Categorias.Cuadrilatero;
		seleccion = rsGet(categoria);
		
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion,rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion,rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion,rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion,rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion,rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {seleccion,rsGet(categoria,seleccion)}, TIPOdeTRIAL.TEST, seleccion, true, true, false));
		
		
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.Rotado), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.Rotado), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.Rotado), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.Rotado), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.SinRotar), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.SinRotar), false, true, false));
		test.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, rsGet(categoria,Categorias.SinRotar), false, true, false));
		
		
		// Calculamos el nivel de rtas correctas para una significancia del 0.05 a partir del numero de trials y el tipo para este nivel
		// IMPORTANTE: se asume que todos los trials son de dos opciones en el caso de categorias y de 6 en el de imagen
		
		Array<Integer> prueba = new Array<Integer>();
		prueba.add(0);
		prueba.add(20);
		prueba.add(0);
		prueba.add(0);
		prueba.add(0);
		prueba.add(20);
		Float[] Resultado = Stadistics.distribucion(prueba);
		Resultado = Resultado;
		
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
				new int[] { rsGet(Categorias.Agudo), rsGet(Categorias.Recto,Categorias.SinRotar), rsGet(Categorias.Grave), rsGetGrupo("Paralelismo1",Categorias.Paralelas), rsGetGrupo("Paralelismo4",Categorias.NoParalelas), rsGetGrupo("Paralelismo9",Categorias.Paralelas) }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
		// Segundo test
		tutorial.jsonTrials.add(crearTrial("Test por imagen", "Identifique cual imagen esta sonando", DISTRIBUCIONESenPANTALLA.BILINEALx4,
				new int[] { rsGet(Categorias.Agudo), rsGet(Categorias.Recto,Categorias.SinRotar), rsGetGrupo("Paralelismo7"), rsGetGrupo("Paralelismo2") }, TIPOdeTRIAL.TEST, Constants.Resources.Categorias.Nada.ID, true, true, true));
		// Ultima presentacion, cuadrilateros
		tutorial.jsonTrials.add(crearTrial("Cuadrilateros", "Toque las imagenes y escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx4,
				new int[] { rsGet(Categorias.Cuadrado,Categorias.SinRotar), rsGet(Categorias.Rombo,Categorias.Rotado), rsGet(Categorias.Cuadrado,Categorias.Rotado), rsGet(Categorias.Rombo,Categorias.SinRotar) }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
		// tercer test (por categorias 1) */
		tutorial.jsonTrials.add(crearTrial("Test por categorias", "Identifique a que categoria pertenece la imagen que suena", DISTRIBUCIONESenPANTALLA.BILINEALx4,
				new int[] { Constants.Resources.Categorias.Cuadrilatero.ID, Constants.Resources.Categorias.Lineax2.ID, Constants.Resources.Categorias.Rombo.ID, Constants.Resources.Categorias.Cuadrado.ID }, TIPOdeTRIAL.TEST, rsGet(Categorias.Cuadrado,Categorias.Rotado), true, true, true));
		
		tutorial.build(Resources.Paths.levelsPath);

		
	}

	/**
	 * 
	 * @param agrupamientoPedido 
	 *			Indica el nombre del agrupamiento del que se quiere extraer un elemento.
	 * @param categoria
	 * 			Indica la categoria a la que debe pertenecer dicho elemento.
	 * 
	 * @return
	 * 			Devuelve el int con el id del elemento seleccionado o 0 si no se encuetra ninguno.
	 */
	private static int rsGetGrupo(String agrupamientoPedido, Categorias categoria) {
		int recurso;
		recurso=0;
		for (Agrupamientos agrupamiento : listadosGrupos) {
			if (agrupamiento.nombre.equals(agrupamientoPedido)) {
				Array<Integer> listadoValido = new Array<Integer>();
				for (int i=0;i<agrupamiento.ids.size;i++) {
					int elemento = agrupamiento.ids.get(i);
					// Carga la info de la metada 
					String savedData = FileHelper.readFile(Resources.Paths.fullCurrentVersionPath + elemento + ".meta");
					Json json = new Json();
					JsonResourcesMetaData jsonMetaData =  json.fromJson(JsonResourcesMetaData.class, savedData);
					// Lo agrega a la lista valida si corresponde 
					if (jsonMetaData.categories.contains(categoria, false)) {
						listadoValido.add(elemento);
					}
				}
				if (listadoValido.size!=0) {
					recurso = listadoValido.random();
					return recurso;
				}
			}
		}
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
		return recurso;
	}
	
	private static int rsGetGrupo(String agrupamientoPedido, int omitir) {
		int recurso;
		recurso=0;
		for (Agrupamientos agrupamiento : listadosGrupos) {
			if (agrupamiento.nombre.equals(agrupamientoPedido)) {
				for (int i=0;i<agrupamiento.ids.size;i++) {
					recurso = agrupamiento.ids.random();
					if (recurso != omitir) {
						return recurso;
					}
				}
			}
		}
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
		return recurso;
	}

	private static int rsGetGrupo (String agrupamientoPedido) {
		int recurso;
		recurso=0;
		for (Agrupamientos agrupamiento : listadosGrupos) {
			if (agrupamiento.nombre.equals(agrupamientoPedido)) {
				recurso = agrupamiento.ids.random();
				return recurso;
			}
		}
		System.out.println("Se ha solicitado un recurso del grupo "+agrupamientoPedido+" y no se ha podido encontrar dicho agrupamiento. Se devuelve un elemento nulo.");
		return recurso;
	}

	private static int rsGet(Categorias categoria) {
		return rsGet(categoria,0);
	}

	private static int rsGet(Categorias categoria, int omitir) {
		int seleccionado;
		for (int i=1; i<100; i++) {
			seleccionado = listadosId.get(categoria.ID).get(MathUtils.random(listadosId.get(categoria.ID).size-1));
			if (seleccionado != omitir) {
				return seleccionado;
			}
		}
		System.out.println("No se puedo seleccionar un recuros de la categoria "+categoria.texto+" que sea diferente a la "+omitir+" en al menos 100 intentos");
		return 0;
	}
	
	private static int rsGet(Categorias categoria, Categorias categoria2) {
		return rsGet(categoria, categoria2, 0);
	}
	
	private static int rsGet(Categorias categoria, Categorias categoria2, int omitir) {
		
		for (int i=0;i<100;i++) {
			int elemento = listadosId.get(categoria.ID).get(MathUtils.random(listadosId.get(categoria.ID).size-1));
			// Carga la info de la metada 
			String savedData = FileHelper.readFile(Resources.Paths.fullCurrentVersionPath + elemento + ".meta");
			Json json = new Json();
			JsonResourcesMetaData jsonMetaData =  json.fromJson(JsonResourcesMetaData.class, savedData);
			if (jsonMetaData.categories.contains(categoria2, false)) {
				if (elemento != omitir) { 
					return elemento;
				}
			}
		}
		System.out.println("Imposible conseguir un " + categoria.texto + " que sea " + categoria2.texto + " y sea diferente al elemento "+omitir+" en al menos 100 intentos");
		return 0;
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

	private static void createStructure() {
		seleccionarRecursos(); // Copia solo los recursos que se usan a una carpeta para su procesamiento
		System.out.println("Recursos seleccionados");
		convertirSVGtoPNG(Resources.Paths.fullUsedResources);
		System.out.println("Recursos transformados a png");
		SVGtoSound.Convert(Resources.Paths.fullUsedResources);
		System.out.println("Recursos transformados a sonido");
		WAVtoMP3(Resources.Paths.fullUsedResources);
		System.out.println("sonido pasado a mp3");
		rebuildAtlasAndSource();
	}

	/**
	 * Conversion de wav a mp3 que usa el paquete JAVE. Para documentacion mirar
	 * http://www.sauronsoftware.it/projects/jave/manual.php?PHPSESSID=lgde8c08ha8mrbcn74259ap3d4
	 * 
	 * @param path
	 */
	private static void WAVtoMP3(String path) {

		File[] archivos;
		// Primero busca la lista de archivos de interes
		File dir = new File(path);
		archivos = dir.listFiles(new WavFileFilter());

		for (File file : archivos) {
			File out = new File(Resources.Paths.fullUsedResources + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".mp3");

			AudioAttributes audio = new AudioAttributes();
			audio.setCodec("libmp3lame");
			audio.setBitRate(new Integer(128000));
			audio.setChannels(new Integer(1));
			audio.setSamplingRate(new Integer(44100));
			audio.setVolume(256);
			EncodingAttributes attrs = new EncodingAttributes();
			attrs.setFormat("mp3");
			attrs.setAudioAttributes(audio);
			Encoder encoder = new Encoder();
			try {
				encoder.encode(file, out, attrs);
				file.delete();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InputFormatException e) {
				e.printStackTrace();
			} catch (EncoderException e) {
				e.printStackTrace();
			}
		}

	}

	private static void convertirSVGtoPNG(String path) {

		File[] archivos;
		// Primero busca la lista de archivos de interes
		File dir = new File(path);
		archivos = dir.listFiles(new SvgFileFilter());

		// Convertimos los SVG a PNG

		for (File file : archivos) {
			try {
				//Step -1: We read the input SVG document into Transcoder Input
				//We use Java NIO for this purpose
				String svg_URI_input = Paths.get(file.getAbsolutePath()).toUri().toURL().toString();
				TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
				//Step-2: Define OutputStream to PNG Image and attach to TranscoderOutput
				OutputStream png_ostream;
				file = new File(Resources.Paths.fullUsedResources + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".png");
				png_ostream = new FileOutputStream(file);

				TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);
				// Step-3: Create PNGTranscoder and define hints if required
				PNGTranscoder my_converter = new PNGTranscoder();
				// Step-4: Convert and Write output
				my_converter.transcode(input_svg_image, output_png_image);
				// Step 5- close / flush Output Stream
				png_ostream.flush();
				png_ostream.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (TranscoderException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void rebuildAtlasAndSource() {

		// Limpia la carpeta de destino
		try {
			FileUtils.cleanDirectory(new File(Resources.Paths.finalPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Crea el atlas
		Settings settings = new Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.duplicatePadding = false;
		settings.debug = false;
		TexturePacker.process(settings, Resources.Paths.fullUsedResources, Resources.Paths.finalPath, "images");

		// Copia los archivos meta para los recursos
		File[] archivos;
		// Primero busca la lista de archivos de interes
		File dir = new File(Resources.Paths.fullUsedResources);
		archivos = dir.listFiles(new MetaFileFilter());
		for (File file : archivos) {
			Path FROM = Paths.get(file.getAbsolutePath());
			File out = new File(Resources.Paths.finalPath + file.getName());
			Path TO = Paths.get(out.getAbsolutePath());
			//overwrite existing file, if exists
			CopyOption[] options = new CopyOption[] {
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
			};
			try {
				Files.copy(FROM, TO, options);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Copia los archivos mp3 para los recursos
		// Primero busca la lista de archivos de interes
		dir = new File(Resources.Paths.fullUsedResources);
		archivos = dir.listFiles(new Mp3FileFilter());
		for (File file : archivos) {
			Path FROM = Paths.get(file.getAbsolutePath());
			File out = new File(Resources.Paths.finalPath + file.getName());
			Path TO = Paths.get(out.getAbsolutePath());
			//overwrite existing file, if exists
			CopyOption[] options = new CopyOption[] {
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
			};
			try {
				Files.copy(FROM, TO, options);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Copia los archivos con la info de los niveles
		// Primero busca la lista de archivos de interes
		dir = new File(Resources.Paths.fullLevelsPath);
		archivos = dir.listFiles();
		for (File file : archivos) {
			Path FROM = Paths.get(file.getAbsolutePath());
			File out = new File(Resources.Paths.finalPath + file.getName());
			Path TO = Paths.get(out.getAbsolutePath());
			//overwrite existing file, if exists
			CopyOption[] options = new CopyOption[] {
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
			};
			try {
				Files.copy(FROM, TO, options);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void seleccionarRecursos() {
		Array<Integer> listado = new Array<Integer>(); // Listado de ids de recursos utilizados
		boolean seguir = true;
		int i = 1;
		while (seguir) {
			File file = new File(Resources.Paths.fullLevelsPath + "level" + i + ".meta");
			if (file.exists()) {
				String savedData = FileHelper.readLocalFile(Resources.Paths.levelsPath + "level" + i + ".meta");
				if (!savedData.isEmpty()) {
					Json json = new Json();
					JsonLevel jsonLevel = json.fromJson(JsonLevel.class, savedData);
					for (JsonTrial trial : jsonLevel.jsonTrials) { // busca en cada trial del nivel
						for (int id : trial.elementosId) { // busca dentro de cada trial en la lista de elementos
							listado.add(id);
						}
						listado.add(trial.rtaCorrectaId); // Agrega la que esta marcada como respuesta.
					}
				} else {
					Gdx.app.error(TAG, "No se a podido encontrar la info del nivel " + i);
				}
				// Incrementa el contador para que pase al proximo level
				i = i + 1;
			} else {
				seguir = false;
			}
		}
		// Aca ya se selecciono toda la lista de recursos.

		// Se limpia el directorio de detino
		try {
			FileUtils.cleanDirectory(new File(Resources.Paths.fullUsedResources));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Se copia solo los recursos utilizados
		for (int id : listado) {
			File file = new File(Resources.Paths.fullCurrentVersionPath + id + ".svg");
			Path FROM = Paths.get(file.getAbsolutePath());
			file = new File(Resources.Paths.fullUsedResources + id + ".svg");
			Path TO = Paths.get(file.getAbsolutePath());
			file = new File(Resources.Paths.fullCurrentVersionPath + id + ".meta");
			Path FROMmeta = Paths.get(file.getAbsolutePath());
			file = new File(Resources.Paths.fullUsedResources + id + ".meta");
			Path TOmeta = Paths.get(file.getAbsolutePath());

			//overwrite existing file, if exists
			CopyOption[] options = new CopyOption[] {
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
			};
			try {
				Files.copy(FROM, TO, options);
				Files.copy(FROMmeta, TOmeta, options);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		jsonTrial.resourceVersion = Resources.Paths.ResourceVersion;
		jsonTrial.feedback = feedback;
		return jsonTrial;
	}

	private static JsonLevel crearLevel() {
		// Crea un JsonLevel y aumenta en 1 el contador de niveles
		contadorLevels += 1;
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.Id = contadorLevels;
		jsonLevel.resourceVersion = Resources.Paths.ResourceVersion;
		return jsonLevel;
	}

	
	

	

	

	public static class Imagen {
		ResourceId resourceId = new ResourceId();
		String name;
		String comments;
		Array<Constants.Resources.Categorias> categories = new Array<Constants.Resources.Categorias>();
		Array<ExtremosLinea> parametros = new Array<ExtremosLinea>();
		Array<InfoLinea> infoLineas = new Array<InfoLinea>();
		String idVinculo; // Sirve para identificar cuando varias imagenes pertenecen a un mismo subgrupo
	}

	public static class Texto {
		ResourceId resourceId = new ResourceId();
		String name;
		String comments;
		Array<Categorias> categories = new Array<Constants.Resources.Categorias>();
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
			ExperimentalObject.JsonResourcesMetaData.CreateJsonMetaData(jsonMetaData, Resources.Paths.currentVersionPath);

		}
	}

	public static class WavFileFilter implements FileFilter
	{
		private final String[] okFileExtensions =
				new String[] { "wav" };

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

	public static class Mp3FileFilter implements FileFilter
	{
		private final String[] okFileExtensions =
				new String[] { "mp3" };

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
	
	public static class Agrupamientos {
		public String nombre;
		public Array<Integer> ids = new Array<Integer>();
	}
	
}
