package com.turin.tur.util;

import com.badlogic.gdx.math.Vector3;

public class TouchInfo {

	public Vector3 coordScreen = new Vector3();
	public Vector3 coordGame = new Vector3();
	public Boolean saved = false; 
	public String actionToDo = "nothing";
	public Boolean actionProcess = false;
	public int elementTouch = -1; // negative value mean nothing
	public String elementTouchType = "nothing"; 
	public float absolutTime;
	public float relativeTime;
	
	public TouchInfo (float absolutTime, float relativeTime) {
		this.absolutTime = absolutTime;
		this.relativeTime = absolutTime;
	}
}
