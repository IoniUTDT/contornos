package com.turin.tur.main.diseno;

import com.badlogic.gdx.math.Vector3;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.util.Constants;


public class TouchInfo {

	public Vector3 coordScreen = new Vector3();
	public Vector3 coordGame = new Vector3();
	public Boolean saved = false; 
	public Boolean actionProcess = false;
	public ExperimentalObject experimentalObjectTouch;
	public String elementTouchType = Constants.Touch.Type.NOTHING; 
	public float absolutTime;
	public float relativeTime;
	public boolean elementTouched = false;
	public String actionToDo;
	public Box thisTouchBox;
	public Box lastTouchBox;
	public User user;
	
	public TouchInfo (float absolutTime, float relativeTime) {
		this.absolutTime = absolutTime;
		this.relativeTime = absolutTime;
	}
	
	
}
