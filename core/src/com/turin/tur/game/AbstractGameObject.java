package com.turin.tur.game;

import com.turin.tur.util.Level;
import com.turin.tur.util.User;

public abstract class AbstractGameObject {

	User activeUser;
	Level activeLevel;
	
	public AbstractGameObject () {
		activeUser = new User();
		activeLevel = new Level();
	}
	
	public void update(float deltaTime) {
		
	}
	
	public abstract void render (float deltaTime);
}

