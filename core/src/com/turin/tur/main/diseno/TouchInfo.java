package com.turin.tur.main.diseno;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.turin.tur.main.diseno.Boxes.Box;
import com.turin.tur.main.util.Constants;
import com.turin.tur.main.util.FileHelper;


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
	public Trial trialActive;
	public Level levelActive;
	
	public TouchInfo (float absolutTime, float relativeTime, Level levelInfo, Trial trialActive, User user) {
		this.absolutTime = absolutTime;
		this.relativeTime = absolutTime;
		this.levelActive = levelInfo;
		this.trialActive = trialActive;
		this.user = user;
	}
	
	public void logTouch (TouchInfo touch) {
		JsonTouch jsontouch = new JsonTouch(touch);
		jsontouch.log();
	}
	
	public static class JsonTouch {
		private Vector3 coordScreen;
		private Vector3 coordGame;
		private int experimentalObjectTouchId;
		private String elementTouchType;
		private float absolutTime;
		private float relativeTime;
		private boolean elementTouched;
		private int lastTouchBoxContentId;
		private long userId;
		private int trialActiveId;
		private int levelActiveId;
		private int trialNumber;
	
		public JsonTouch(TouchInfo touch) {
			this.coordScreen = touch.coordScreen;
			this.coordGame = touch.coordGame;
			if (touch.experimentalObjectTouch != null) {
				this.experimentalObjectTouchId = touch.experimentalObjectTouch.Id;
			} else {
				this.experimentalObjectTouchId = 0;
			}
			this.elementTouchType = touch.elementTouchType;
			this.absolutTime = touch.absolutTime;
			this.relativeTime = touch.relativeTime;
			this.elementTouched = touch.elementTouched;
			if (touch.lastTouchBox != null) {
				this.lastTouchBoxContentId = touch.lastTouchBox.contenido.Id;
			} else {
				this.lastTouchBoxContentId = 0;
			}
			this.userId = touch.user.id;
			this.trialActiveId = touch.trialActive.Id;
			this.levelActiveId = touch.levelActive.Id;
			this.trialNumber = touch.levelActive.activeTrialPosition;
		}
		
		public void log () {
			Json json = new Json();
			FileHelper.writeLog(Constants.TOUCHLOG, json.toJson(this));
		}
	}
}
