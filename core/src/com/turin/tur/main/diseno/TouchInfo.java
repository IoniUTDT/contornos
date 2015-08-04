package com.turin.tur.main.diseno;

import com.badlogic.gdx.math.Vector3;
import com.turin.tur.main.diseno.Boxes.Box;


public class TouchInfo {

	public Vector3 coordScreen = new Vector3();
	public Vector3 coordGame = new Vector3();
	public ExperimentalObject experimentalObjectTouch;
	public boolean elementTouched = false;
	public Box thisTouchBox;
	public Box lastTouchBox;
	
}
