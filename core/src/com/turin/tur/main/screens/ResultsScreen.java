package com.turin.tur.main.screens;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Level.Significancia;
import com.turin.tur.main.diseno.Level.TIPOdeSIGNIFICANCIA;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.User;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.Constants;

public class ResultsScreen extends AbstractGameScreen {

	private static final String TAG = ResultsScreen.class.getName();

	public User user;
	public Session session;
	public Level level;
	
	public SpriteBatch batch;
	public OrthographicCamera cameraGUI;
	
	int page=1; // Maneja el numero de pagina que se muestra
	
	// Elementos graficos
	private Skin skin;
	private Stage stage;
	private Table table;
		
	public ResultsScreen(Game game, Session session, Level level) {
		super(game);
		this.session=session;
		this.user = this.session.user;
		this.level = level;
	}

	@Override
	public void render(float deltaTime) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(deltaTime);
		stage.draw();

		guiRender();
		
	}

	private void guiRender() {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		renderPage();
		batch.end();
	}

	private void renderPage() {
		// Muestra el contenido en funcion de la pagina
		float x = cameraGUI.viewportWidth/10;
		float y = cameraGUI.viewportHeight/8;
		BitmapFont fpsFont = Assets.instance.fonts.defaultFont;
		fpsFont.getData().setScale(Constants.factorEscala()/2);
		fpsFont.draw(batch, "Ha completado el nivel.", x, y);
		
		boolean levelPass = true;
		
		for (Significancia significancia:level.jsonLevel.significancias) {
			
			if (significancia.tipo==TIPOdeSIGNIFICANCIA.COMPLETO) {
				if (significancia.exitoMinimo<this.level.jsonLevel.aciertosTotales) {
					fpsFont.setColor(0, 1, 0, 1); // green
				} else {
					fpsFont.setColor(1, 0, 0, 1); // red
					levelPass = false;
				}
				if (level.jsonLevel.aciertosTotales==significancia.distribucion.length-1) {
					levelPass=true;
				}
				
				y = y + cameraGUI.viewportHeight/20;
				fpsFont.draw(batch, "Ha respondido bien "+this.level.jsonLevel.aciertosTotales+" de "+(significancia.distribucion.length-1)+" intentos posibles.", x, y);
				y = y + cameraGUI.viewportHeight/20;
				fpsFont.draw(batch, "En el nivel jugado se considera que hay un acierto significativo con mas de "+significancia.exitoMinimo+" intentos correctos.", x, y);
			}
			if (significancia.tipo==TIPOdeSIGNIFICANCIA.IMAGEN) {
				if (significancia.exitoMinimo<this.level.jsonLevel.aciertosPorImagenes) {
					fpsFont.setColor(0, 1, 0, 1); // green
				} else {
					fpsFont.setColor(1, 0, 0, 1); // red
					levelPass = false;
				}
				if (level.jsonLevel.aciertosPorImagenes==significancia.distribucion.length-1) {
					levelPass=true;
				}
				
				y = y + cameraGUI.viewportHeight/20;
				fpsFont.draw(batch, "Ha respondido bien "+this.level.jsonLevel.aciertosPorImagenes+" de "+(significancia.distribucion.length-1)+" en los trials por imagen.", x, y);
				y = y + cameraGUI.viewportHeight/20;
				fpsFont.draw(batch, "En el nivel jugado se considera que hay un acierto significativo con mas de "+significancia.exitoMinimo+" intentos correctos.", x, y);
			}
			
			if (significancia.tipo==TIPOdeSIGNIFICANCIA.CATEGORIA) {
				if (significancia.exitoMinimo<this.level.jsonLevel.aciertosPorCategorias) {
					fpsFont.setColor(0, 1, 0, 1); // green
				} else {
					fpsFont.setColor(1, 0, 0, 1); // red
					levelPass=false;
				}
				if (level.jsonLevel.aciertosPorCategorias==significancia.distribucion.length-1) {
					levelPass=true;
				}
				
				y = y + cameraGUI.viewportHeight/20;
				fpsFont.draw(batch, "Ha respondido bien "+this.level.jsonLevel.aciertosPorCategorias+" de "+(significancia.distribucion.length-1)+" en los trials por categoria.", x, y);
				y = y + cameraGUI.viewportHeight/20;
				fpsFont.draw(batch, "En el nivel jugado se considera que hay un acierto significativo con mas de "+significancia.exitoMinimo+" intentos correctos.", x, y);
			}

			fpsFont.setColor(1, 1, 1, 1); // white
		}
		
		x = cameraGUI.viewportWidth/2 - cameraGUI.viewportWidth/8;
		y = cameraGUI.viewportHeight- cameraGUI.viewportHeight/8;
		if (levelPass) {
			fpsFont.setColor(0, 1, 0, 1); // green
			fpsFont.draw(batch, "Nivel superado", x, y);
			
		} else {
			fpsFont.setColor(1, 0, 0, 1); // red
			fpsFont.draw(batch, "Nivel no superado", x, y);
		}
		fpsFont.setColor(1, 1, 1, 1); // white	

	}

	private void guiRenderInit() {
		batch = new SpriteBatch();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		// Crea las cosas que tienen que ver con los graficos.
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		skin = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI));

		TextButton button = new TextButton("Continuar", skin, "default");
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new MenuScreen(game, session));
			}
		});
		
		// Arma el menu

		button.getStyle().font.getData().setScale(Constants.factorEscala()*3,Constants.factorEscala()*3);
		table.add(button).width(Gdx.graphics.getWidth()/2.5f).space(Gdx.graphics.getHeight()/10f);
		table.row();
		table.align(Align.bottom);
		//table.row();


		guiRenderInit();
		Gdx.app.debug(TAG, "Resultados cargados");

	}

	@Override
	public void hide() {
		stage.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}
}
