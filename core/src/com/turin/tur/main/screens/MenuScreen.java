package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.turin.tur.main.diseno.User;
import com.turin.tur.main.util.Constants;


public class MenuScreen extends AbstractGameScreen {
	
	
	private static final String TAG = MenuScreen.class.getName();
	
    // For debug drawing
    private ShapeRenderer shapeRenderer;
    
    // Elementos graficos
    private Skin skin;
    private Stage stage;
    private Table table;
    
    // Informacion general
    User user;
    
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
	    
	    // Aca carga la info del usuario
	    if (! Gdx.files.internal("experimentalconfig/userinfo.txt").exists()) {User.createUser();}
	    loadUser();

	    TextButton buttonL1 = new TextButton("Nivel 1", skin, "default");
	    buttonL1.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){      
            	game.setScreen(new LevelScreen(game,1));
            }
        });
		
		buttonL1.setVisible(true); 
		//buttonL1.setColor(1, 0, 1, 0.1f);
		
		table.add(buttonL1);
	    
	    Gdx.app.debug(TAG, "Menu cargado");

	}

	private void loadUser() {		
		user = User.load();
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
