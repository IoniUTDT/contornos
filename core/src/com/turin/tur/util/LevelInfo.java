package com.turin.tur.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.game.Assets;
import com.turin.tur.game.WorldController;
import com.turin.tur.game.objects.BoxContainer;
import com.turin.tur.game.objects.ExperimentalObject;
import com.turin.tur.game.objects.ImageBox;
import com.turin.tur.game.objects.SelectBox;
import com.turin.tur.game.objects.StimuliBox;
import com.badlogic.gdx.math.MathUtils; 



public class LevelInfo {

	public static final String TAG = LevelInfo.class.getName();
	public String levelTitle = "";
	public String levelMode = "";
	public Array<BoxContainer> trialElements = new Array<BoxContainer>();
	
	public LevelInfo () {
		levelMode = Constants.Diseno.MODO_ACTIVO;
		LoadExperimentalSetup();
		SetTexts();
	}
	
	private void LoadExperimentalSetup() {
		if (levelMode == Constants.Diseno.MODO_ENTRENAMIENTO) {
			// Crea el array de imagenes
			Array<TextureRegion> regions = Assets.instance.contenido.contenido_serie;
			// Crea el array de sonidos
			Sound[] sonidos = new Sound[Constants.NUMERO_ELEMENTOS];
			for (int i = 0; i < sonidos.length; i++) {
				Sound sonido = Gdx.audio.newSound(Gdx.files.internal("sounds/sonido"+Integer.toString(i)+".wav"));
				sonidos[i] = sonido;
			}	
			// Crea el array de objetos experimentales
			Array<ExperimentalObject> objetosExperimentales = new Array<ExperimentalObject>();
			for (int i = 0; i < sonidos.length; i++) {
				objetosExperimentales.add(new ExperimentalObject(new Sprite(regions.get(i)),sonidos[i],i));
			}
			for (int i=0; i < Constants.NUMERO_ELEMENTOS; i++) {
				trialElements.add(new ImageBox (objetosExperimentales.get(i)));
				trialElements.get(i).SetPosition(Constants.posiciones_elementos_centros[i][0],Constants.posiciones_elementos_centros[i][1]);
			}
		}
		
		if (levelMode == Constants.Diseno.MODO_SELECCION_IMAGEN) {
			// Crea un shift para hacer lugar a lo que hay que mostrar
			float xShift = Constants.Box.SHIFT_MODO_SELECCIONAR;
			// Crea el array de imagenes
			Array<TextureRegion> regions = Assets.instance.contenido.contenido_serie;
			// Crea el array de sonidos
			Sound[] sonidos = new Sound[Constants.NUMERO_ELEMENTOS];
			for (int i = 0; i < sonidos.length; i++) {
				Sound sonido = Gdx.audio.newSound(Gdx.files.internal("sounds/sonido"+Integer.toString(i)+".wav"));
				sonidos[i] = sonido;
			}	
			// Crea el array de objetos experimentales
			Array<ExperimentalObject> objetosExperimentales = new Array<ExperimentalObject>();
			for (int i = 0; i < sonidos.length; i++) {
				objetosExperimentales.add(new ExperimentalObject(new Sprite(regions.get(i)),sonidos[i],i));
			}
			for (int i=0; i < Constants.NUMERO_ELEMENTOS; i++) {
				trialElements.add(new SelectBox (objetosExperimentales.get(i)));
				trialElements.get(i).SetPosition(Constants.posiciones_elementos_centros[i][0]+xShift,Constants.posiciones_elementos_centros[i][1]);
			}
			// Crea el elemento estimulo con un random de la serie
			int elementoEstimulo = MathUtils.random(Constants.NUMERO_ELEMENTOS-1);
			Gdx.app.debug(TAG, "Se creo el estimulo " + elementoEstimulo);
			trialElements.add(new StimuliBox (objetosExperimentales.get(elementoEstimulo)));
			trialElements.peek().SetPosition(0 + Constants.Box.SHIFT_ESTIMULO_MODO_SELECCIONAR, 0);
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
}
