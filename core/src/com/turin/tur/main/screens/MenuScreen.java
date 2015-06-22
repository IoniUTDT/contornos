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
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.GameConf;


public class MenuScreen extends AbstractGameScreen {
	
	private static final String TAG = MenuScreen.class.getName();

    private Skin skin;
   

    private Stage stage;
    private Table table;
    // For debug drawing
    private ShapeRenderer shapeRenderer;
    
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
	    
	    // Add widgets to the table here.
	  
	    TextButton buttonTrain = new TextButton("Entrenar", skin, "default");
	    buttonTrain.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){      
            	GameConf.instance.modo = Constants.Diseno.TIPOdeTRIAL.ENTRENAMIENTO;
            	GameConf.instance.save();
            	game.setScreen(new GameScreen(game));
            }
        });

	    TextButton buttonTest = new TextButton("Probar", skin, "default");
	    buttonTest.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){      
            	GameConf.instance.modo = Constants.Diseno.TIPOdeTRIAL.TEST;
            	GameConf.instance.save();
            	game.setScreen(new GameScreen(game));
            }
        });

	    table.add(buttonTrain);
	    table.row();
	    table.add(buttonTest);
	    
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

}
