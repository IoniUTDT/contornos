package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.Assets;
import com.turin.tur.main.objects.ExperimentalObject;
import com.turin.tur.main.objects.ImageBox;
import com.turin.tur.main.objects.ImageSelectableBox;
import com.turin.tur.main.objects.StimuliBox;
import com.badlogic.gdx.math.MathUtils; 



public class TrialInfo {

	public static final String TAG = TrialInfo.class.getName();
	public String levelTitle = "";
	public String levelMode = "";
	public Array<ImageBox> imageTrialElements;
	public Array<ImageSelectableBox> optionsTrialElements; 
	public StimuliBox stimuliTrialElement;
	public boolean autoRestart;
	public float levelTime;
	public float restartTime;
	
	public TrialInfo () {
		initLevel();
	}
	
	public void initLevel() {
		GameConf.instance.load();
		autoRestart=false;
		levelTime=0;
		restartTime=0;
		levelMode = GameConf.instance.modo;
		LoadExperimentalSetup();
		SetTexts();
	}
	private void LoadExperimentalSetup() {
		
		if (levelMode == Constants.Diseno.MODO_ENTRENAMIENTO) {
			// Crea el array de imagenes
			Array<TextureRegion> regions = Assets.instance.contenido.contenido_serie;
			// Crea el array de sonidos
			Array<Sound> sonidos=Assets.instance.audios.serieAudios;
			// Crea el array de objetos experimentales
			Array<ExperimentalObject> objetosExperimentales = new Array<ExperimentalObject>();
			for (int i = 0; i < sonidos.size; i++) {
				objetosExperimentales.add(new ExperimentalObject(new Sprite(regions.get(i)),sonidos.get(i),i));
			}
			// Crea el array de los elementos en la pantalla
			imageTrialElements = new Array<ImageBox>();
			for (int i=0; i < Constants.NUMERO_ELEMENTOS; i++) {
				imageTrialElements.add(new ImageBox (objetosExperimentales.get(i)));
				imageTrialElements.get(i).SetPosition(Constants.posiciones_elementos_centros[i][0],Constants.posiciones_elementos_centros[i][1]);
			}
		}
		
		if (levelMode == Constants.Diseno.MODO_SELECCION_IMAGEN) {
			// Crea un shift para hacer lugar a lo que hay que mostrar
			float xShift = Constants.Box.SHIFT_MODO_SELECCIONAR;
			// Crea el array de imagenes
			Array<TextureRegion> regions = Assets.instance.contenido.contenido_serie;
			// Crea el array de sonidos
			Array<Sound> sonidos=Assets.instance.audios.serieAudios;
			
			// Crea el array de objetos experimentales
			Array<ExperimentalObject> objetosExperimentales = new Array<ExperimentalObject>();
			for (int i = 0; i < sonidos.size; i++) {
				objetosExperimentales.add(new ExperimentalObject(new Sprite(regions.get(i)),sonidos.get(i),i));
			}
			// Crea el array de imagenes seleccionables
			optionsTrialElements = new Array<ImageSelectableBox>();
			for (int i=0; i < Constants.NUMERO_ELEMENTOS; i++) {
				optionsTrialElements.add(new ImageSelectableBox(objetosExperimentales.get(i)));
				optionsTrialElements.get(i).SetPosition(Constants.posiciones_elementos_centros[i][0]+xShift,Constants.posiciones_elementos_centros[i][1]);
			}
			// Crea el elemento estimulo con un random de la serie
			int elementoEstimulo = MathUtils.random(optionsTrialElements.size-1);
			Gdx.app.debug(TAG, "Se creo el estimulo " + elementoEstimulo);
			stimuliTrialElement = new StimuliBox (objetosExperimentales.get(elementoEstimulo));
			stimuliTrialElement.SetPosition(0 + Constants.Box.SHIFT_ESTIMULO_MODO_SELECCIONAR, 0);
		}
	}

	private void SetTexts () {
		if (levelMode == Constants.Diseno.MODO_ENTRENAMIENTO) {
			levelTitle = "A entrenar!";
		}
		if (levelMode == Constants.Diseno.MODO_SELECCION_IMAGEN) {
			levelTitle = "A ver ahora...";
		} 
	}

	public void RestartLevel() {
		Gdx.app.debug(TAG, "Reiniciando nivel...");
		stimuliTrialElement.unSelect();
		initLevel();
	}
	
	public void Update(float deltaTime) {
		levelTime += deltaTime;
		if ((levelTime > restartTime) & (autoRestart)) {RestartLevel();} 
	}
}
