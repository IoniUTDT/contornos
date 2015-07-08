package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.turin.tur.main.util.Constants;



public class MenuScreen extends AbstractGameScreen {
	
	private static final String TAG = MenuScreen.class.getName();
	Preferences prefs = Gdx.app.getPreferences("User");
	
    // For debug drawing
    private ShapeRenderer shapeRenderer;
    
    // Elementos graficos
    private TextButton buttonUser;
    private Skin skin;
    private Stage stage;
    private Table table;
    
    
	public MenuScreen (Game game) {
		super(game);
	}
	
	
	@Override
	public void render (float deltaTime) {
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    stage.act(deltaTime);
	    stage.draw();
	    table.drawDebug(shapeRenderer); // This is optional, but enables debug lines for tables.
	
	}

	@Override
	public void resize(int width, int height) {
	    stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		
		stage = new Stage();
	    Gdx.input.setInputProcessor(stage);
		
	    table = new Table();
	    table.setFillParent(true);
	    stage.addActor(table);
	    shapeRenderer = new ShapeRenderer();
	    skin = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI));
	    
	    // Load user info
	    String User = prefs.getString("User", "NoName");
	    boolean firstUser = false;
	    if (User == "NoName") {firstUser=true;}
	    
	    String TextUser;
	    if (firstUser) {TextUser="Crear usuario";
	    } else {
	    	TextUser = "Usuario: "+ User + ". Click para cambiar";
	    }
	    

		// Add widgets to the table here.
	    this.buttonUser = new TextButton(TextUser, skin, "default");
	    buttonUser.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){      
            	Gdx.app.debug(TAG, super.getClass().getName());
            	
            }
        });
	    
	    TextButton buttonL1 = new TextButton("Nivel 1 (entrenamiento)", skin, "default");
	    buttonL1.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){      
            	game.setScreen(new LevelScreen(game,1));
            }
        });

		TextButton buttonL2 = new TextButton("Nivel 2 (Test)", skin, "default");
		buttonL2.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new LevelScreen(game,2));
			}
		});

		
		buttonL1.setVisible(true); 
		//buttonL1.setColor(1, 0, 1, 0.1f);
		
		table.add(buttonUser);
		table.row();
	    table.add(buttonL1);
	    table.row();
	    table.add(buttonL2);
	    
	    Gdx.app.debug(TAG, "Menu cargado");

	}

	public void Prueba (){
	    Gdx.app.debug(TAG, "Tuc Tuc");
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

}
