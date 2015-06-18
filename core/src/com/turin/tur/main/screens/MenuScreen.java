package com.turin.tur.main.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private SpriteBatch batch;
    private Skin skin;
   

    private Stage stage;
    private Table table;
	
	public MenuScreen (Game game) {
		super(game);
	}
	
	
	@Override
	public void render (float deltaTime) {
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// if(Gdx.input.isTouched())
		// 	game.setScreen(new GameScreen(game));
		
		batch.begin();
        stage.draw();
        batch.end();
		//Gdx.app.debug(TAG, "draw");
	
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {
		
		
		
		batch = new SpriteBatch();
        skin = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI));
        stage = new Stage();
        TextButton buttonTrain = new TextButton("Entrenar", skin, "default");
        Table layer = new Table();
        
        layer.add(buttonTrain);
       
        buttonTrain.setWidth(200f);
        buttonTrain.setHeight(20f);
        buttonTrain.setPosition(Gdx.graphics.getWidth() /2 - 100f, Gdx.graphics.getHeight()/2 - 10f);

        buttonTrain.addListener(new ClickListener(){
            @Override 
            public void clicked(InputEvent event, float x, float y){      
            	GameConf.instance.modo = Constants.Diseno.MODO_ENTRENAMIENTO;
            	GameConf.instance.save();
            	game.setScreen(new GameScreen(game));
            }
        });
	
        stage.addActor(buttonTrain);
        Gdx.input.setInputProcessor(stage);
        
        
        Gdx.app.debug(TAG, "Menu cargado");

	}

	@Override
	public void hide() {
		batch.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}
