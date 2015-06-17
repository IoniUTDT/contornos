package com.turin.tur.main.interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.turin.tur.main.util.Constants;

public class Wellcome {

	public Wellcome () {
		initprogram();
		initscreen();
	}

	private void initscreen() {
		// TODO Auto-generated method stub
		
	}

	private void initprogram() {
		boolean existsFileUser = Gdx.files.internal(Constants.Files.USERDATA).exists();
		if (!existsFileUser) {
			newUser();
		}
	}

	private void newUser() {
		MyTextInputListener listener = new MyTextInputListener();
		Gdx.input.getTextInput(listener, "Dialog Title", "Initial Textfield Value");
	}
	
	public class MyTextInputListener implements TextInputListener {
	   @Override
	   public void input (String text) {
	   }

	   @Override
	   public void canceled () {
		   }
	}
}
