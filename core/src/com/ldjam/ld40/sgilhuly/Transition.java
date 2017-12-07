package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Transition {
	
	public static final int WIDTH = 120;
	public static final int HEIGHT = 82;
	public static final float DURATION = 0.5f;
	
	public int targetLevel;
	public boolean goingUp;
	public float progress = 0;
	public Texture black;
	
	public Transition(int targetLevel, boolean goingUp) {
		this.targetLevel = targetLevel;
		this.goingUp = goingUp;
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.drawPixel(0, 0, Palette.BLACK);
		black = new Texture(pixmap);
	}
	
	public void updateAndDraw(float delta, SpriteBatch batch) {
		progress += delta;
		int y = (int) (progress * HEIGHT / DURATION);
		if(goingUp) {
			batch.draw(black, 0, HEIGHT - y, WIDTH, HEIGHT * 2);
		} else {
			batch.draw(black, 0, y - HEIGHT * 2, WIDTH, HEIGHT * 2);
		}
		if(progress > DURATION * 1.5) {
			GameContext.player.posLevel = targetLevel;
			if(targetLevel == 1 && goingUp == false) {
				GameContext.player.posDir = Map.START_DIR;
			}
			GameContext.currentMap = Map.MAPS[targetLevel];
		}
		if(progress > DURATION * 3) {
			GameContext.transition = null;
			if(targetLevel == 1 && goingUp == false) {
				GameContext.player.savegame();
			}
		}
	}
}
