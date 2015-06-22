package com.turin.tur.main.util;

import com.badlogic.gdx.math.Vector3;
import com.turin.tur.main.diseno.ExperimentalObject;
import com.turin.tur.main.objects.BoxContainer;

public class TouchInfo {

	public Vector3 coordScreen = new Vector3();
	public Vector3 coordGame = new Vector3();
	public Boolean saved = false; 
	public String actionToDo = Constants.Touch.ToDo.NOTHING;
	public Boolean actionProcess = false;
	public ExperimentalObject experimentalObjectTouch;
	public String elementTouchType = Constants.Touch.Type.NOTHING; 
	public float absolutTime;
	public float relativeTime;
	public boolean elementTouched = false;
	public BoxContainer thisTouch;
	public BoxContainer lastTouch;
	
	public TouchInfo (float absolutTime, float relativeTime) {
		this.absolutTime = absolutTime;
		this.relativeTime = absolutTime;
	}
}
