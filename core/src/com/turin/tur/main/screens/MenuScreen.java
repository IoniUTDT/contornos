package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.turin.tur.ContornosMain;
import com.turin.tur.main.diseno.Level;
import com.turin.tur.main.diseno.Session;
import com.turin.tur.main.diseno.User;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;


public class MenuScreen extends AbstractGameScreen {

	private static final String TAG = MenuScreen.class.getName();

	// For debug drawing
	private ShapeRenderer shapeRenderer;

	// Elementos graficos
	private Skin skin;
	private Stage stage;
	private Table table;
	private TextButton buttonUserName;
	private TextButton buttonL1;

	public User user;
	
	public MenuScreen(Game game) {
		super(game);
	}

	@Override
	public void render(float deltaTime) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(deltaTime);
		stage.draw();
		table.drawDebug(shapeRenderer); // This is optional, but enables debug
										// lines for tables.

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {

		// Chequea si el usuario ya existe o si es la primera vez
		if (!Gdx.files.local(Constants.USERFILE).exists()) {
			User.CreateUser();
			Gdx.app.debug(TAG, "Creando nuevo usuario");
		}
		user = User.Load();
		
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		stage.addActor(table);
		shapeRenderer = new ShapeRenderer();
		skin = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI));
		
		Level nextLevel = new Level(this.user.lastLevelCompletedId + 1);

		// Boton que lleva al nivel 1
		buttonL1 = new TextButton("Next level: " + nextLevel.levelTitle, skin,
				"default");
		buttonL1.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				goToLevelLog();
				game.setScreen(new LevelScreen(game,user.lastLevelCompletedId+1));
			}
		});

		// Boton que carga el nombre del usuario y permite modificarlo
		buttonUserName = new TextButton(this.user.name, skin, "default");
		buttonUserName.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				/*
				 * Zona de prueba
				 */


				User user = new User();
				user.name = "Unnamed";
				user.id = 1;
				Json json = new Json();
				json.setOutputType(OutputType.json);
				String requestJson = json.toJson(user);
				// String requestJson = "{name:\"Unnamed\",comments:\"Usuario generado automaticamente\"}";

				Net.HttpRequest request = new Net.HttpRequest(HttpMethods.POST);
				final String url = "http://localhost:3000/posts";
				request.setUrl(url);
				
				
				request.setContent(requestJson);

				Gdx.app.debug(TAG, requestJson);
				request.setHeader("Content-Type", "application/json");
				request.setHeader("Accept", "application/json");

				Gdx.net.sendHttpRequest(request,
						new Net.HttpResponseListener() {

							public void handleHttpResponse(
									Net.HttpResponse httpResponse) {

								int statusCode = httpResponse.getStatus()
										.getStatusCode();
								if (statusCode != HttpStatus.SC_OK) {
									Gdx.app.debug(TAG, httpResponse.getResultAsString());
									System.out.println("Request Failed");
									return;
								}

								String responseJson = httpResponse
										.getResultAsString();
								try {

									// DO some stuff with the response string

								} catch (Exception exception) {

									exception.printStackTrace();
								}
							}

							public void failed(Throwable t) {
								System.out.println("Request Failed Completely");
							}

							@Override
							public void cancelled() {
								System.out.println("request cancelled");

							}

						});

				/*
				 * Fin zona de prueba
				 */

				MyTextInputListener listener = new MyTextInputListener();
				Gdx.input.getTextInput(listener,
						"Ingrese un nombre de usuario", "", null);
			}
		});

		// Arma el menu
		table.add(buttonUserName);
		table.row();
		table.add(buttonL1);

		Gdx.app.debug(TAG, "Menu cargado");

	}

	protected void goToLevelLog() {
		String logText = TAG + ": " + this.user.name + " goes to level "
				+ (this.user.lastLevelCompletedId + 1) + ".\r\n";
		FileHelper.appendFile(Constants.USERLOG, logText);
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
			String logText = TAG + ": " + user.name + " change name to " + text
					+ ".\r\n";
			FileHelper.appendFile(Constants.USERLOG, logText);
			user.name = text;
			buttonUserName.setText(text);
			user.save();
		}

		@Override
		public void canceled() {
		}
	}
}
