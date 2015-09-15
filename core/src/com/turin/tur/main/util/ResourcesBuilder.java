package com.turin.tur.main.util;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

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

import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.diseno.ExperimentalObject.JsonMetaData;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.diseno.Trial.ResourceId;
import com.turin.tur.main.util.Constants.Resources.Categorias;
import com.turin.tur.main.util.Constants.Diseno.DISTRIBUCIONESenPANTALLA;
import com.turin.tur.main.util.Constants.Diseno.TIPOdeTRIAL;
import com.turin.tur.main.util.SVGtoSound.SvgFileFilter;
import com.turin.tur.main.util.SVGtoSound;

public class ResourcesBuilder {

	/*
	 * Esta clase crea los archivos SVG. Solo deberia ser llamada desde el constructor en windows xq la idea es poder usar cosas de java por fuera de libgdx.
	 */

	private static final String TAG = ResourcesBuilder.class.getName();

	static int contadorDeRecursos = Constants.Resources.Reservados;
	static int contadorLevels = 0;
	static int contadorTrials = 0;

	static int height = 100;
	static int width = 100;
	
	static final Boolean makeLevels = false;
	static final Boolean makeResources = false;
	
	public static final int ResourceVersion = 110;
	static String tempPath = "/temp/resourcesbuild/";
	static String fullTempPath = "." + tempPath;
	static String currentVersionPath = tempPath + ResourceVersion + "/";
	static String fullCurrentVersionPath = "." + currentVersionPath;
	static String fullLevelsPath = fullTempPath + "tempLevels/";
	static String levelsPath = tempPath + "tempLevels/";
	static String fullUsedResources = fullTempPath + "selected/";
	static String finalPath = "../android/assets/experimentalsource/" + Constants.version() + "/";

	public static void buildNewSVG() {

		if (makeResources) {

			File file = new File(fullCurrentVersionPath);
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
				//objetos.addAll(secuenciaLineasHorizontales()); // Agrega las lineas
				//objetos.addAll(secuenciaLineasVerticales()); // Agrega un set de lineas verticales
				//objetos.addAll(secuenciaLineasConAngulo()); // Agrega las lineas con angulo
				//objetos.addAll(secuenciaAngulos()); // Agrega los angulos
				//objetos.addAll(secuenciaDosRectasCentradasVerticalParalelas()); // Agrega rectas paralelas
				//objetos.addAll(secuenciaDosRectasCentradasVerticalNoParalelas()); //Agrega rectas no paralelas
				//objetos.addAll(secuenciaRombos(40, 1f, 0.1f, 0, 50, false, true, false)); // Agrega cuadrados
				objetos.addAll(secuenciaParalelismoDificiles()); // Agrega recursos de paralelas dificiles
			}
			// Crea los archivos correspondientes
			for (Imagen im : objetos) {
				SVG.SVGimagen(im);
			}
		}

		if (makeLevels) {
			makeLevels();
		}

	}

	private static Array<Imagen> secuenciaParalelismoDificiles() {
		/*
		 * Esta serie genera sets de 6 imagenes similares
		 * 3 paralelas con separacion levemente variable y 3 no paralelas con inclinacion levemenete variable 
		 */
		float largo=80; // Largo de las lineas
		float angulo; // Angulo de inclinacion
		int cantidad = 10;
		float separacion = 10;
		float limiteAngulo = 5; //grados (todo mi codigo trabaja en grados)
		Array<Imagen> objetos = new Array<Imagen>();
		
		for (int i=0; i<cantidad; i++) {
			angulo = 180/cantidad*i; // Define el angulo de la serie
			// Calculamos los centros de manera que esten separados en funcion del angulo
			// Para las tres que varia el angulo
			float Xcenter1 = width/2 - separacion/2 * MathUtils.sin(MathUtils.degRad*angulo);
			float Xcenter2 = width/2 + separacion/2 * MathUtils.sin(MathUtils.degRad*angulo);
			float Ycenter1 = width/2 - separacion/2 * MathUtils.cos(MathUtils.degRad*angulo);
			float Ycenter2 = width/2 + separacion/2 * MathUtils.cos(MathUtils.degRad*angulo);
			
			// Crea las 3 imagenes no paralelas
			for (int j=1;j<4;j++) {
				Imagen imagen = crearImagen();
				float anguloVariacion1 = MathUtils.random(-limiteAngulo, +limiteAngulo);
				float anguloVariacion2 = MathUtils.random(-limiteAngulo, +limiteAngulo);
				// agrega la primer linea
				InfoLinea infoLinea = new InfoLinea();
				infoLinea.angulo=angulo+anguloVariacion1;
				infoLinea.largo=largo;
				infoLinea.Xcenter = Xcenter1;
				infoLinea.Ycenter = Ycenter1;
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Agrega la segunda linea
				infoLinea = new InfoLinea();
				infoLinea.angulo=angulo+anguloVariacion2;
				infoLinea.largo=largo;
				infoLinea.Xcenter = Xcenter2;
				infoLinea.Ycenter = Ycenter2;
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Datos generales
				imagen.comments = "Imagen generada por secuencia automatica 'secuenciaParalelismoDificiles'.";
				imagen.name = "Imagen de rectas no paralelas generada automaticamente";
				imagen.idVinculo = "Paralelismo"+i;
				imagen.categories.add(Categorias.Lineax2);
				imagen.categories.add(Categorias.NoParalelas);
				objetos.add(imagen);
			}
			// crea la 3 imagenes paralelas
			for(int j=1; j<4; j++){
				Imagen imagen = crearImagen();
				InfoLinea infoLinea = new InfoLinea();
				// Arma la primer linea
				infoLinea.angulo=angulo;
				infoLinea.largo=largo;
				infoLinea.Xcenter = Xcenter1 - separacion/5 * j * MathUtils.sin(MathUtils.degRad*angulo);
				infoLinea.Ycenter = Ycenter1 - separacion/5 * j * MathUtils.cos(MathUtils.degRad*angulo);
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Arma la segunda
				infoLinea = new InfoLinea();
				infoLinea.angulo=angulo;
				infoLinea.largo=largo;
				infoLinea.Xcenter = Xcenter2 + separacion/5 * j * MathUtils.sin(MathUtils.degRad*angulo);
				infoLinea.Ycenter = Ycenter2 + separacion/5 * j * MathUtils.cos(MathUtils.degRad*angulo);
				imagen.parametros.addAll(ExtremosLinea.Linea(infoLinea));
				imagen.infoLineas.add(infoLinea);
				// Datos generales
				imagen.comments = "Imagen generada por secuencia automatica 'secuenciaParalelismoDificiles'.";
				imagen.name = "Imagen de rectas paralelas generada automaticamente";
				imagen.idVinculo = "Paralelismo"+i;
				imagen.categories.add(Categorias.Lineax2);
				imagen.categories.add(Categorias.Paralelas);
				objetos.add(imagen);
			}
			
		}
		return objetos;
	}

	private static void makeLevels () {

		// Se fija q exista el paquete de recursos de la version actual
		if (!new File(fullCurrentVersionPath).exists()) {
			System.out.println("Primero debe crear los recuros version:" + ResourceVersion);
			return;
		}

		// Limpia la carpeta de destino
		try {
			FileUtils.cleanDirectory(new File(fullLevelsPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		tutorial.jsonTrials.add(crearTrial("Rectas horizontales y verticales", "Escuche todos los sonidos para continuar",
				DISTRIBUCIONESenPANTALLA.BILINEALx6,
				new int[] { 22, 26, 25, 27, 23, 28 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
		// Muestra rectas en diagonal
		tutorial.jsonTrials.add(crearTrial("Rectas diagonales", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] { 29, 36, 41 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
		// Primer test sencillo
		tutorial.jsonTrials.add(crearTrial("Test por imagen", "Identifique cual imagen esta sonando", DISTRIBUCIONESenPANTALLA.BILINEALx6,
				new int[] { 25, 27, 28, 29, 36, 23 }, TIPOdeTRIAL.TEST, 25, true, true, true));
		// Muestra angulo y pares de rectas (un angulo agudo, uno recto y uno grave, y dos pares de rectas paralelas y unas q no.)
		tutorial.jsonTrials.add(crearTrial("Angulos y rectas", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx6,
				new int[] { 49, 55, 414, 691, 694, 708 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
		// Segundo test
		tutorial.jsonTrials.add(crearTrial("Test por imagen", "Identifique cual imagen esta sonando", DISTRIBUCIONESenPANTALLA.BILINEALx4,
				new int[] { 49, 55, 708, 691 }, TIPOdeTRIAL.TEST, Constants.Resources.Categorias.Nada.ID, true, true, true));
		// Ultima presentacion, cuadrilateros
		tutorial.jsonTrials.add(crearTrial("Cuadrilateros", "Escuche todos los sonidos para continuar", DISTRIBUCIONESenPANTALLA.BILINEALx4,
				new int[] { 742, 748, 750, 757 }, TIPOdeTRIAL.ENTRENAMIENTO, Constants.Resources.Categorias.Nada.ID, false, true, true));
		// tercer test (por categorias 1) */
		tutorial.jsonTrials.add(crearTrial("Test por categorias", "Identifique a que categoria pertenece la imagen que suena", DISTRIBUCIONESenPANTALLA.BILINEALx4,
				new int[] { Constants.Resources.Categorias.Cuadrilatero.ID, Constants.Resources.Categorias.Lineax2.ID, Constants.Resources.Categorias.Rombo.ID, Constants.Resources.Categorias.Cuadrado.ID }, TIPOdeTRIAL.TEST, 753, true, true, true));

		tutorial.build(levelsPath);

		/*
		 *  Armamos el nivel de test inicial
		 */

		// Crea el nivel tutorial
		JsonLevel Test1 = crearLevel();
		Test1.levelTitle = "Test Inicial";
		Test1.randomTrialSort=true;
		Test1.show = true;


		// Ahora vamos a ir creando los trials

		// seis test de reconocer paralelismo con imagenes entre imagenes
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {680, 708}, TIPOdeTRIAL.TEST, 680, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {709, 698}, TIPOdeTRIAL.TEST, 698, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {720, 701}, TIPOdeTRIAL.TEST, 720, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {712, 704}, TIPOdeTRIAL.TEST, 712, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {717, 686}, TIPOdeTRIAL.TEST, 686, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {728, 681}, TIPOdeTRIAL.TEST, 728, true, true, false));
		// siete test de reconocer entre categoria paralela, no paralela (tres q si 4 q no)
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 690, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 703, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 683, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 714, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 715, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 730, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Paralelas.ID, Categorias.NoParalelas.ID}, TIPOdeTRIAL.TEST, 721, false, true, false));
		// Creamos los test con angulos (6 de imagen y siete de categorias)
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {421, 425}, TIPOdeTRIAL.TEST, 425, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {467, 451}, TIPOdeTRIAL.TEST, 451, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {532, 548}, TIPOdeTRIAL.TEST, 532, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {557, 584}, TIPOdeTRIAL.TEST, 557, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {671, 670}, TIPOdeTRIAL.TEST, 671, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {624, 600}, TIPOdeTRIAL.TEST, 624, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 532, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 528, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 533, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 514, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 522, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 598, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx3,
				new int[] {Categorias.Agudo.ID, Categorias.Grave.ID, Categorias.Recto.ID}, TIPOdeTRIAL.TEST, 603, false, true, false));
		// Test de cuadrilateros
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {737, 764}, TIPOdeTRIAL.TEST, 737, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {759, 740}, TIPOdeTRIAL.TEST, 759, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {754, 750}, TIPOdeTRIAL.TEST, 754, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {762, 774}, TIPOdeTRIAL.TEST, 774, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {784, 781}, TIPOdeTRIAL.TEST, 781, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {772, 776}, TIPOdeTRIAL.TEST, 776, true, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 770, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 758, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 760, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 784, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 759, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 737, false, true, false));
		Test1.jsonTrials.add(crearTrial("", "Identifique la imagen o categoria del sonido", DISTRIBUCIONESenPANTALLA.LINEALx2,
				new int[] {Categorias.Cuadrado.ID, Categorias.Rombo.ID}, TIPOdeTRIAL.TEST, 738, false, true, false));
		Test1.build(levelsPath);


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
		Test1d.build(levelsPath);

		createStructure();
	}
	private static void createStructure() {
		seleccionarRecursos(); // Copia solo los recursos que se usan a una carpeta para su procesamiento
		System.out.println("Recursos seleccionados");
		convertirSVGtoPNG(fullUsedResources);
		System.out.println("Recursos transformados a png");
		SVGtoSound.Convert(fullUsedResources);
		System.out.println("Recursos transformados a sonido");
		WAVtoMP3(fullUsedResources);
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
			File out = new File(fullUsedResources + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".mp3");

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InputFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (EncoderException e) {
				// TODO Auto-generated catch block
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
				file = new File(fullUsedResources + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".png");
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TranscoderException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static void rebuildAtlasAndSource() {

		// Limpia la carpeta de destino
		try {
			FileUtils.cleanDirectory(new File(finalPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Crea el atlas
		Settings settings = new Settings();
		settings.maxWidth = 1024;
		settings.maxHeight = 1024;
		settings.duplicatePadding = false;
		settings.debug = false;
		TexturePacker.process(settings, fullUsedResources, finalPath, "images");

		// Copia los archivos meta para los recursos
		File[] archivos;
		// Primero busca la lista de archivos de interes
		File dir = new File(fullUsedResources);
		archivos = dir.listFiles(new MetaFileFilter());
		for (File file : archivos) {
			Path FROM = Paths.get(file.getAbsolutePath());
			File out = new File(finalPath + file.getName());
			Path TO = Paths.get(out.getAbsolutePath());
			//overwrite existing file, if exists
			CopyOption[] options = new CopyOption[] {
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
			};
			try {
				Files.copy(FROM, TO, options);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Copia los archivos mp3 para los recursos
		// Primero busca la lista de archivos de interes
		dir = new File(fullUsedResources);
		archivos = dir.listFiles(new Mp3FileFilter());
		for (File file : archivos) {
			Path FROM = Paths.get(file.getAbsolutePath());
			File out = new File(finalPath + file.getName());
			Path TO = Paths.get(out.getAbsolutePath());
			//overwrite existing file, if exists
			CopyOption[] options = new CopyOption[] {
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
			};
			try {
				Files.copy(FROM, TO, options);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Copia los archivos con la info de los niveles
		// Primero busca la lista de archivos de interes
		dir = new File(fullLevelsPath);
		archivos = dir.listFiles();
		for (File file : archivos) {
			Path FROM = Paths.get(file.getAbsolutePath());
			File out = new File(finalPath + file.getName());
			Path TO = Paths.get(out.getAbsolutePath());
			//overwrite existing file, if exists
			CopyOption[] options = new CopyOption[] {
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES
			};
			try {
				Files.copy(FROM, TO, options);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void seleccionarRecursos() {
		Array<Integer> listado = new Array<Integer>(); // Listado de ids de recursos utilizados
		boolean seguir = true;
		int i = 1;
		while (seguir) {
			File file = new File(fullLevelsPath + "level" + i + ".meta");
			if (file.exists()) {
				String savedData = FileHelper.readLocalFile(levelsPath + "level" + i + ".meta");
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
			FileUtils.cleanDirectory(new File(fullUsedResources));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Se copia solo los recursos utilizados
		for (int id : listado) {
			File file = new File(fullCurrentVersionPath + id + ".svg");
			Path FROM = Paths.get(file.getAbsolutePath());
			file = new File(fullUsedResources + id + ".svg");
			Path TO = Paths.get(file.getAbsolutePath());
			file = new File(fullCurrentVersionPath + id + ".meta");
			Path FROMmeta = Paths.get(file.getAbsolutePath());
			file = new File(fullUsedResources + id + ".meta");
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
				// TODO Auto-generated catch block
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
		jsonTrial.resourceVersion = ResourceVersion;
		jsonTrial.feedback = feedback;
		return jsonTrial;
	}

	private static JsonLevel crearLevel() {
		// Crea un JsonLevel y aumenta en 1 el contador de niveles
		contadorLevels += 1;
		JsonLevel jsonLevel = new JsonLevel();
		jsonLevel.Id = contadorLevels;
		jsonLevel.resourceVersion = ResourceVersion;
		return jsonLevel;
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
			recurso.resourceId.resourceVersion = ResourceVersion;
			objetos.add(recurso);
		}

		return objetos;
	}

	private static Array<Imagen> secuenciaAngulos() {
		float largo = 50;
		int cantidad = 36; // Nota: si no se pone una cantidad que sea correcta pueden no quedar angulos rectos!
		float shiftAngulo = 360 / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();

		for (int i = 0; i < cantidad; i++) {
			for (int j = 1; j + i < cantidad; j++) {
				Imagen imagen = crearImagen();
				imagen.parametros.addAll(ExtremosLinea.Angulo(width / 2,
						height / 2, i * shiftAngulo, (j + i) * shiftAngulo,
						largo));
				imagen.name = "Angulo";
				imagen.comments = "Angulo generado automaticamente por secuenciaAngulos. Parametros: Angulo inicial: " + (i * shiftAngulo) + "; Angulo final: "
						+ ((j + i) * shiftAngulo) + ";";
				imagen.categories.add(Constants.Resources.Categorias.Angulo);
				if ((j * shiftAngulo < 90) || (j * shiftAngulo > 270)) {
					imagen.categories.add(Constants.Resources.Categorias.Agudo);
				} else if (j * shiftAngulo == 90) {
					imagen.categories.add(Constants.Resources.Categorias.Recto);
				} else {
					imagen.categories.add(Constants.Resources.Categorias.Grave);
				}
				objetos.add(imagen);
			}
		}
		return objetos;
	}

	/**
	 * Crea un set de lineas verticales para el tutorial. En este caso se crean a mano las lineas utiles
	 * 
	 * @return
	 */
	private static Array<Imagen> secuenciaLineasVerticales() {
		Array<Imagen> objetos = new Array<Imagen>();
		Imagen imagen;
		// recta vertical completa centrada
		imagen = crearImagen();
		imagen.parametros.addAll(ExtremosLinea.Linea(height / 2, width / 2, 90, 90));
		imagen.name = "Linea vertical centrada completa";
		imagen.comments = "Imagen generada por sucuenciaLineasVerticuales";
		imagen.categories.add(Constants.Resources.Categorias.Tutorial);
		imagen.categories.add(Constants.Resources.Categorias.Lineas);
		imagen.categories.add(Constants.Resources.Categorias.Lineax1);
		objetos.add(imagen);
		// recta vertical incompleta superior
		imagen = crearImagen();
		imagen.parametros.addAll(ExtremosLinea.Linea(height / 6, width / 4, 90, height / 3));
		imagen.name = "Linea vertical superior";
		imagen.comments = "Imagen generada por sucuenciaLineasVerticuales";
		imagen.categories.add(Constants.Resources.Categorias.Tutorial);
		imagen.categories.add(Constants.Resources.Categorias.Lineas);
		imagen.categories.add(Constants.Resources.Categorias.Lineax1);
		objetos.add(imagen);
		// recta vertical incompleta inferior
		imagen = crearImagen();
		imagen.parametros.addAll(ExtremosLinea.Linea(height / 6 * 5, width / 4 * 3, 90, height / 3));
		imagen.name = "Linea vertical superior";
		imagen.comments = "Imagen generada por sucuenciaLineasVerticuales";
		imagen.categories.add(Constants.Resources.Categorias.Tutorial);
		imagen.categories.add(Constants.Resources.Categorias.Lineas);
		imagen.categories.add(Constants.Resources.Categorias.Lineax1);
		objetos.add(imagen);

		return objetos;
	}

	private static Array<Imagen> secuenciaRombos(float ladoP, float excentricidadMaxima, float excentricidadMinima, float anguloP, int cantidad,
			boolean centered, boolean rotados, boolean escalaFija) {
		/*
		 * Esta rutina devuelve una secuencia de rombos que se contruyen a partir de los siguientes parametros:
		 * 
		 * lado: longitud del lado del cuadrilatero angulo: inclinacion del rombo, un angulo de 0� significa que la diagonal menor esta horizontal. Un angulo de
		 * 45� para un cuadrado significa que los lados estan horizontales y verticales excentricidad: relacion entre la diagonal menor y la mayor. Si la
		 * excentricidad es mayor que uno calcula la inversa de manera que siempre la diagonal mayor se asuma vertical (previamente a la rotacion dada por
		 * angulo) una excentricidad igual a 1 da un cuadrado. Si son diferentes la minima y la maxima hace random entre ellas, sino usa la maxima.
		 * 
		 * cantidad determina la cantidad de figuras que se generan centered determina si estan centradas o si se las posiciona al azar. rotados determina si se
		 * agraga una rotacion random al angulo o no. escala determina si se modifica (para menos) el tama�o o no.
		 * 
		 * Se asume que todos los parametros son positivos!
		 */

		float margen = 20; // Esta variable determina cuanto espacio se debe dejar de margen para que la figura no este muy pegada al borde.

		// Parametros de la figura que se calculan durante la generacion
		float ancho;
		float alto;
		float diagMayor;
		float diagMenor;

		// Constantes con los que se pueden modificar los parametros si se lo indica
		float escalaMinima = 0.5f;

		// parametros generales
		float angulo;
		float lado;
		float excentricidad;
		float xCenter;
		float yCenter;

		Vector2 nodo1;
		Vector2 nodo2;
		Vector2 nodo3;
		Vector2 nodo4;
		float angulo1;
		float angulo2;
		float angulo3;
		float angulo4;

		// El array de salida
		Array<Imagen> objetos = new Array<Imagen>();

		int errores = 0;

		for (int creados = 0; creados < cantidad;) {
			// Verifica que no haya muchas figuras invalidas (por ej por no entrar)
			if (errores > cantidad) {
				Gdx.app.error(TAG, "Demasiadas figuras no pudieron ser creadas en la rutina que crea cuadrilateros. Considere revisar los parametros");
				break;
			}

			// Generamos los parametros para cada cuadrilatero especifico si depende de factores random.
			if (MathUtils.randomBoolean()) {
				excentricidad = excentricidadMaxima - MathUtils.random(excentricidadMaxima - excentricidadMinima);
			} else { // Genera que la mitad sean cuadrados
				excentricidad = 1;
			}

			if (rotados) {
				if (MathUtils.randomBoolean(0.6f)) {
					angulo = anguloP + MathUtils.random(180);
				} else {
					angulo = anguloP + 45;
				}
			} else {
				angulo = anguloP + 45;
			}

			float anguloRad = (float) (angulo / 180 * Math.PI);

			if (escalaFija) {
				lado = ladoP;
			} else {
				lado = ladoP * MathUtils.random(escalaMinima, 1);
			}

			// Primero calculamos las diagonales a partir de la medida del lado y la excentricidad

			/*
			 * De plantear que con d=semidiagonal mayor: d^2 + (d*e)^2 = l^2 sale que
			 */

			diagMayor = (float) (2 * Math.sqrt(lado * lado / (1 + excentricidad * excentricidad)));
			diagMenor = diagMayor * excentricidad;

			// una vez que calcula las diagonales, sabiendo la inclinacion puede calcular el alto y el ancho
			float anchoDiagMayor = (float) (diagMayor * Math.sin(anguloRad));
			float altoDiagMayor = (float) (diagMayor * Math.cos(anguloRad));
			float anchoDiagMenor = (float) (diagMenor * Math.cos(anguloRad));
			float altoDiagMenor = (float) (diagMenor * Math.sin(anguloRad));
			ancho = Math.max(anchoDiagMayor, anchoDiagMenor);
			alto = Math.max(altoDiagMayor, altoDiagMenor);

			// Verifica que la figura entre en la imagen

			if ((alto + margen * 2 >= height) || (ancho + margen * 2 >= width)) {
				errores++;
				Gdx.app.log(TAG, "El cuadrilatero calculado ha sido descartado por no entrar en la figura");

			} else { // Si la figura entra sigue
				creados++;

				// setea el centro
				if (centered) {
					xCenter = width / 2;
					yCenter = height / 2;
				} else { //ERROR!
					xCenter = MathUtils.random(margen + ancho / 2, (width - margen - ancho / 2));
					yCenter = MathUtils.random(margen + alto / 2, (height - margen - alto / 2));
				}

				// encuentra el centro de los lados (nodos, que se numeran del primer al cuarto cuadrante en la orientacion original) 
				nodo1 = new Vector2();
				nodo1.x = diagMayor / 4;
				nodo1.y = diagMenor / 4;
				nodo2 = new Vector2();
				nodo2.x = -diagMayor / 4;
				nodo2.y = diagMenor / 4;
				nodo3 = new Vector2();
				nodo3.x = -diagMayor / 4;
				nodo3.y = -diagMenor / 4;
				nodo4 = new Vector2();
				nodo4.x = +diagMayor / 4;
				nodo4.y = -diagMenor / 4;
				// Los rota lo que corresponda segun el angulo del cuadrilatero
				nodo1.rotate(-angulo);
				nodo2.rotate(-angulo);
				nodo3.rotate(-angulo);
				nodo4.rotate(-angulo);
				Vector2 center = new Vector2();
				center.x = xCenter;
				center.y = yCenter;
				nodo1.add(center);
				nodo2.add(center);
				nodo3.add(center);
				nodo4.add(center);
				// Calcula los angulos con que esta orientado cada lado.
				float anguloInclinacionLadoRad = MathUtils.atan2(diagMenor, diagMayor);
				float anguloInclinacionLadoDeg = anguloInclinacionLadoRad * 180 / MathUtils.PI;
				// Nota!! Aca hice un lio con eltema de como se miden las coordenadas (creo que se miden de la esq superio izq). Los signos los saque a prueba y error!
				angulo2 = -anguloInclinacionLadoDeg + angulo;
				angulo4 = -anguloInclinacionLadoDeg + angulo;
				angulo1 = +anguloInclinacionLadoDeg + angulo;
				angulo3 = +anguloInclinacionLadoDeg + angulo;

				// Llegado este punto esta la informacion de los cuadro segmentos que forman la figura dados por sus centro y sus inclinaciones.
				// Ahora vamos a crear los datos como en todas las demas figuras.

				Imagen imagen = crearImagen();
				imagen.parametros.add(ExtremosLinea.Linea(nodo1.x, nodo1.y, angulo1, lado));
				imagen.parametros.add(ExtremosLinea.Linea(nodo2.x, nodo2.y, angulo2, lado));
				imagen.parametros.add(ExtremosLinea.Linea(nodo3.x, nodo3.y, angulo3, lado));
				imagen.parametros.add(ExtremosLinea.Linea(nodo4.x, nodo4.y, angulo4, lado));
				imagen.name = "Cuadrilatero";
				imagen.comments = "Imagen generada automaticamente con el generador de cuadrilateros. Parametros: Excentricidad: " + excentricidad + "; Lado: "
						+ lado + "; Angulo: " + angulo + ";";
				imagen.categories.add(Categorias.Cuadrilatero);
				if (excentricidad == 1) {
					imagen.categories.add(Categorias.Cuadrado);
				} else {
					imagen.categories.add(Categorias.Rombo);
				}
				objetos.add(imagen);
			}

		}
		Gdx.app.log(TAG, "Figuras fallidas: " + errores);
		return objetos;

	}

	private static Array<Imagen> secuenciaLineasHorizontales() {
		float largo = 90;
		float angulo = 0;
		int cantidad = 5;
		float yCenter = height / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 1; i < cantidad + 1; i++) {
			Imagen imagen = crearImagen();
			imagen.parametros.addAll(ExtremosLinea.Linea(width / 2, yCenter * i
					- yCenter / 2, angulo, largo));
			imagen.name = "Linea " + i;
			imagen.comments = "Linea generada por secuenciaLineasHorizontales para tutorial. Parametros: Altura:" + (yCenter * i - yCenter / 2);
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax1);
			imagen.categories.add(Constants.Resources.Categorias.Tutorial);
			objetos.add(imagen);
		}
		return objetos;
	}

	private static Imagen crearImagen() {
		contadorDeRecursos += 1;
		Imagen imagen = new Imagen();
		imagen.resourceId.id = contadorDeRecursos;
		imagen.resourceId.resourceVersion = ResourceVersion;
		return imagen;
	}

	private static Array<Imagen> secuenciaLineasConAngulo() {
		float largo = 90;
		int cantidad = 18;
		float angulo = 180 / cantidad;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 1; i < cantidad + 1; i++) {
			Imagen imagen = crearImagen();
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2,
					angulo * i, largo));
			imagen.name = "Linea " + i;
			imagen.comments = "Linea generada por secuenciaLineasConAngulo para tutorial. Parametros: Posicion: centrada; Angulo: " + (angulo * i) + ";";
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax1);
			imagen.categories.add(Constants.Resources.Categorias.Tutorial);
			objetos.add(imagen);
		}
		return objetos;
	}

	private static Array<Imagen> secuenciaDosRectasCentradasVerticalParalelas() {

		/*
		 * Crea secuencias de dos rectas, ambas centradas en x, pero levemente por encima y por debajo del centro en y, rotando angulos y paralelas
		 */

		int cantidad = 30;
		float largo;
		float angulo;
		float offset;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 0; i < cantidad; i++) {
			largo = MathUtils.random(50f, 90f);
			angulo = MathUtils.random(180f);
			offset = MathUtils.random(10f, 30f);
			Imagen imagen = crearImagen();
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 + offset, angulo, largo)); // La primer linea
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 - offset, angulo, largo)); // La segunda linea
			imagen.name = "Rectas paralelas random, imagen numero " + i + " de la secuencia creada por secuenciaDosRectasCentradasVerticalParalelas";
			imagen.comments = "Parametros: " + "Largo: " + largo + " Angulo: " + angulo + " Offset: +-" + offset;
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax2);
			imagen.categories.add(Constants.Resources.Categorias.Paralelas);
			objetos.add(imagen);
		}
		return objetos;

	}

	private static Array<Imagen> secuenciaDosRectasCentradasVerticalNoParalelas() {

		/*
		 * Crea secuencias de dos rectas, ambas centradas en x, pero levemente por encima y por debajo del centro en y, rotando angulos y paralelas
		 */

		int cantidad = 30;
		float largo;
		float angulo1;
		float angulo2;
		float offset;

		Array<Imagen> objetos = new Array<Imagen>();
		for (int i = 0; i < cantidad; i++) {
			largo = MathUtils.random(50f, 90f);
			angulo1 = MathUtils.random(180f);
			angulo2 = angulo1 + MathUtils.random(10f, 180f);
			offset = MathUtils.random(10f, 30f);
			Imagen imagen = crearImagen();
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 + offset, angulo1, largo)); // La primer linea
			imagen.parametros.add(ExtremosLinea.Linea(width / 2, height / 2 - offset, angulo2, largo)); // La segunda linea
			imagen.name = "Rectas no paralelas random, imagen numero " + i + " de la secuencia creada por secuenciaDosRectasCentradasVerticalNoParalelas";
			imagen.comments = "Parametros: " + " Largo: " + largo + " Angulo1: " + angulo1 + " Angulo2: " + angulo2 + " Offset: " + offset;
			imagen.categories.add(Constants.Resources.Categorias.Lineas);
			imagen.categories.add(Constants.Resources.Categorias.Lineax2);
			imagen.categories.add(Constants.Resources.Categorias.NoParalelas);
			objetos.add(imagen);
		}
		return objetos;

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
			JsonMetaData jsonMetaData = new JsonMetaData();
			jsonMetaData.resourceId = imagen.resourceId;
			jsonMetaData.name = imagen.name;
			jsonMetaData.comments = imagen.comments;
			jsonMetaData.categories = imagen.categories;
			jsonMetaData.noSound = false;
			jsonMetaData.idVinculo = imagen.idVinculo;
			jsonMetaData.infoLineas = imagen.infoLineas;
			jsonMetaData.parametros = imagen.parametros;
			ExperimentalObject.JsonMetaData.CreateJsonMetaData(jsonMetaData, currentVersionPath);

		}

		private static void add(String string) {
			content = content + string + "\r\n";
		}

		private static void createFile(Imagen imagen) {
			FileHelper.writeFile(currentVersionPath + imagen.resourceId.id + ".svg", content);
		}

		private static void createFileText(Texto text) {
			FileHelper.writeFile(currentVersionPath + text.resourceId.id + ".svg", content);
		}

		private static void createMetadataText(Texto text) {
			JsonMetaData jsonMetaData = new JsonMetaData();
			jsonMetaData.resourceId = text.resourceId;
			jsonMetaData.name = text.name;
			jsonMetaData.comments = text.comments;
			jsonMetaData.categories = text.categories;
			jsonMetaData.noSound = true;
			ExperimentalObject.JsonMetaData.CreateJsonMetaData(jsonMetaData, currentVersionPath);

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
}
