package com.turin.tur.main.objects;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.turin.tur.main.util.Constants;

public class ImageSelectableBox extends BoxContainer{

	private boolean clicked;
	private float animationAdvance;
	public Sprite sprTrue;
	public Sprite sprFalse;
	public boolean answer;
	private Sprite usedSprite;
	
	public ImageSelectableBox(ExperimentalObject contenido) {
		super(contenido);
		this.spr = contenido.imagen;
		this.clicked = false;
		this.animationAdvance = 0;
		initAnimation();
	}

	private void initAnimation() {
		Pixmap pixmapTrue = createAnimation (true);
		Pixmap pixmapFalse = createAnimation (false);
		Texture textureTrue = new Texture(pixmapTrue); 
		Texture textureFalse = new Texture(pixmapFalse);
		sprTrue = new Sprite(textureTrue);
		sprFalse = new Sprite(textureFalse);
	}

	private Pixmap createAnimation(boolean condicion) {
		Pixmap pixmap = new Pixmap(10, 10, Format.RGBA8888);
		if (condicion) {
			pixmap.setColor(0, 1, 0, 1);
		} else {
			pixmap.setColor(1, 0, 0, 1);
		}
		// crea un cuadrado relleno
		pixmap.fill();
		return pixmap;
	}

	public void update(float deltaTime) {
		if (clicked) {
			animationAdvance += deltaTime;
			if (animationAdvance > Constants.Box.ANIMATION_ANSWER_TIME) {
				this.clicked=false;
			}
		}
	}

	public void specificRender(SpriteBatch batch) {
		if (this.clicked) {
			float x;
			float y;
			if (this.answer) {
				usedSprite = sprTrue;
			} else {
				usedSprite = sprFalse;
			}
			// dibuja las esquinas
			usedSprite.setSize(Constants.Box.SELECT_BOX_ANCHO_RTA,Constants.Box.SELECT_BOX_ANCHO_RTA);
			// sup izq
			x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.SELECT_BOX_ANCHO_RTA;
			y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.SELECT_BOX_ANCHO_RTA;
			usedSprite.setPosition(x, y);
			usedSprite.draw(batch);
			// sup der
			x = posicionCenter.x + Constants.Box.TAMANO/2;
			y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.SELECT_BOX_ANCHO_RTA;
			usedSprite.setPosition(x, y);
			usedSprite.draw(batch);
			// inf izq
			x = posicionCenter.x - Constants.Box.TAMANO/2 - Constants.Box.SELECT_BOX_ANCHO_RTA;
			y = posicionCenter.y + Constants.Box.TAMANO/2;
			usedSprite.setPosition(x, y);
			usedSprite.draw(batch);
			// inf der
			x = posicionCenter.x + Constants.Box.TAMANO/2;
			y = posicionCenter.y + Constants.Box.TAMANO/2;
			usedSprite.setPosition(x, y);
			usedSprite.draw(batch);
			// dibuja los bordes
			// verticales
			usedSprite.setSize(Constants.Box.SELECT_BOX_ANCHO_RTA,Constants.Box.TAMANO);
			//izq
			x = posicionCenter.x - Constants.Box.TAMANO/2 -Constants.Box.SELECT_BOX_ANCHO_RTA;
			y = posicionCenter.y - Constants.Box.TAMANO/2;
			usedSprite.setPosition(x, y);
			usedSprite.draw(batch);
			//der
			x = posicionCenter.x + Constants.Box.TAMANO/2;
			y = posicionCenter.y - Constants.Box.TAMANO/2;
			usedSprite.setPosition(x, y);
			usedSprite.draw(batch);
			// horizontal
			usedSprite.setSize(Constants.Box.TAMANO,Constants.Box.SELECT_BOX_ANCHO_RTA);
			// arriba
			x = posicionCenter.x - Constants.Box.TAMANO/2;
			y = posicionCenter.y - Constants.Box.TAMANO/2 - Constants.Box.SELECT_BOX_ANCHO_RTA;
			usedSprite.setPosition(x, y);
			usedSprite.draw(batch);
			// abajo
			x = posicionCenter.x - Constants.Box.TAMANO/2;
			y = posicionCenter.y + Constants.Box.TAMANO/2;
			usedSprite.setPosition(x, y);
			usedSprite.draw(batch);
						
			
		}
	}

	public void select() {
		this.clicked = true;
		this.animationAdvance = 0;
	}

	public void unSelect() {
		this.clicked=false;
	}

	public boolean itsTrue (ExperimentalObject contenido) {
		answer=false;
		if (this.contenido.Id == contenido.Id) {answer=true;}
		if (this.contenido.Id != contenido.Id) {answer=false;}
		return answer;
	}
	

}
