package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.User;
import com.turin.tur.main.util.Assets;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.Internet;


public class MenuScreen extends AbstractGameScreen {

	private static final String TAG = MenuScreen.class.getName();

	// For debug drawing
	private ShapeRenderer shapeRenderer;

	// Elementos graficos
	private Skin skin;
	private Stage stage;
	private Table table;
	private TextButton buttonUserName;
	private Array<TextButton> levelButtons = new Array<TextButton>();

	public User user;
	public Session session;
	
	// Variables para funcionamiento interno
	int levelIterator;

	public SpriteBatch batch;
	public OrthographicCamera cameraGUI;
	
	
	public MenuScreen(Game game, Session session) {
		super(game);
		this.session=session;
		this.user = this.session.user;
	}

	@Override
	public void render(float deltaTime) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(deltaTime);
		stage.draw();
		table.drawDebug(shapeRenderer); // This is optional, but enables debug
										// lines for tables.
		guiRender();
		
	}

	private void guiRender() {
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		renderServerStatus();
		//this.levelController.levelInterfaz.renderFps(batch,cameraGUI);
		//this.levelController.levelInterfaz.renderTitle(batch, cameraGUI);
		batch.end();
	}

	private void renderServerStatus() {
		float x = cameraGUI.viewportWidth - 70;
		float y = cameraGUI.viewportHeight - 30;
		BitmapFont fpsFont = Assets.instance.fonts.defaultSmallFont;
		if (Internet.serverOk) {
			// show up in green
			fpsFont.setColor(0, 1, 0, 1);
		} else {
			// show up in red
			fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.draw(batch, "Server", x, y);
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
		shapeRenderer = new ShapeRenderer();
		skin = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI));
		
		// Crea los botones de los niveles
		
		Array<Integer> levelIteration = new Array<Integer>();
		for (int i=1; i < this.session.numberOfLevels+1; i++) {
			levelIteration.add(i);
		}
		for (final int levelIterator : levelIteration) {
			Level level = new Level(levelIterator);
			TextButton button = new TextButton("Level: "+ level.levelTitle, skin, "default");
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.setScreen(new LevelScreen(game,levelIterator, session));
				}
			});
			if (session.user.levelsCompleted.contains(levelIterator, false)) {
				button.setColor(1, 0, 0, 1);
			} else {
				button.setColor(0, 1, 0, 1);
			}
			levelButtons.add(button);
			Gdx.app.debug(TAG, "agregado boton" + button.getText());
			guiRenderInit();
		}
		
		// Boton que carga el nombre del usuario y permite modificarlo
		buttonUserName = new TextButton("Usuario: "+this.user.name, skin, "default");
		buttonUserName.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				MyTextInputListener listener = new MyTextInputListener();
				Gdx.input.getTextInput(listener,
						"Ingrese un nombre de usuario", "", null);
			}
		});

		// Arma el menu
		table.add(buttonUserName);
		table.row();
		for (TextButton button : levelButtons) {
			table.add(button);
			table.row();
		}

		Gdx.app.debug(TAG, "Menu cargado");

	}

	@Override
	public void hide() {
		stage.dispose();
		shapeRenderer.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	public class MyTextInputListener implements TextInputListener {

		@Override
		public void input(String text) {
			user.name = text;
			buttonUserName.setText(text);
			user.save();
		}

		@Override
		public void canceled() {
		}
	}
}
