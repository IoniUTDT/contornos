package com.turin.tur.main.util.builder;

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
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Level.JsonLevel;
import com.turin.tur.main.diseno.Trial.JsonTrial;
import com.turin.tur.main.util.FileHelper;
import com.turin.tur.main.util.SVGtoSound;
import com.turin.tur.main.util.Constants.Resources;
import com.turin.tur.main.util.SVGtoSound.SvgFileFilter;

import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;

public class LevelExport {

	private static final String TAG = LevelExport.class.getName();
	
	public static void createStructure() {
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
					json.setUsePrototypes(false);
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
