package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MapDrawer {

	private Texture background;
	private Texture sideLeft;
	private Texture sideLeftFar;
	private Texture sideRight;
	private Texture sideRightFar;
	private Texture wall;
	private Texture wallFar;
	
	private Texture[] stairsUp;
	private Texture[] stairsDown;
	
	public MapDrawer(int palette) {
		background = new Texture("walls/background.png");
		sideLeft = new Texture("walls/side_left.png");
		sideLeftFar = new Texture("walls/side_left_far.png");
		sideRight = new Texture("walls/side_right.png");
		sideRightFar = new Texture("walls/side_right_far.png");
		wall = new Texture("walls/wall.png");
		wallFar = new Texture("walls/wall_far.png");

		stairsUp = new Texture[] {
				new Texture("deco/stairsUp.png"),
				new Texture("deco/stairsUpFar.png")
		};
		stairsDown = new Texture[] {
				new Texture("deco/stairsDown.png"),
				new Texture("deco/stairsDownFar.png")
		};
		
		//TODO palette swaps
	}

	public void drawMap(int posX, int posY, int posDir, Map map, SpriteBatch batch, float cameraShake) {
		byte b;
		
		int shakeX = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		int shakeY = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		
		// draw background
		batch.draw(background, -4 + shakeX, -4 + shakeY);
		
		// shake walls separately
		shakeX = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		shakeY = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		
		// draw far decos
		for(int i = 0; i < 3; i++) {
			b = map.accessMap(posX, posY, posDir, 2, -1 + i);
			if(getDeco(b) != null) {
				batch.draw(getDeco(b)[1], 31 + i * 16 + shakeX, 28 + shakeY);
			}
		}
		
		// draw far walls
		for(int i = 0; i < 5; i++) {
			if(map.accessMap(posX, posY, posDir, 2, -2 + i) == Map.WALL) {
				batch.draw(wallFar, -5 + i * 26 + shakeX, 28 + shakeY);
			}
		}
		
		// draw far sides
		for(int i = 0; i < 2; i++) {
			if(map.accessMap(posX, posY, posDir, 1, -2 + i) == Map.WALL) {
				batch.draw(sideLeftFar, -30 + i * 52 + shakeX, 3 + shakeY, sideLeftFar.getWidth() * (2 - i), sideLeftFar.getHeight());
			}
		}
		for(int i = 0; i < 2; i++) {
			if(map.accessMap(posX, posY, posDir, 1, 1 + i) == Map.WALL) {
				batch.draw(sideRightFar, 73 + i * 26 + shakeX, 3 + shakeY, sideLeftFar.getWidth() * (i + 1), sideLeftFar.getHeight());
			}
		}
		
		// draw decos
		for(int i = 0; i < 3; i++) {
			b = map.accessMap(posX, posY, posDir, 1, -1 + i);
			if(getDeco(b) != null) {
				batch.draw(getDeco(b)[0], -24 + i * 46 + shakeX, 3 + shakeY);
			}
		}
		
		// draw walls
		for(int i = 0; i < 3; i++) {
			if(map.accessMap(posX, posY, posDir, 1, -1 + i) == Map.WALL) {
				batch.draw(wall, -54 + i * 76 + shakeX, 3 + shakeY);
			}
		}
		
		// draw sides
		if(map.accessMap(posX, posY, posDir, 0, -1) == Map.WALL) {
			batch.draw(sideLeft, -4 + shakeX, -4 + shakeY);
		}
		if(map.accessMap(posX, posY, posDir, 0, 1) == Map.WALL) {
			batch.draw(sideRight, 98 + shakeX, -4 + shakeY);
		}
	}
	
	public Texture[] getDeco(byte b) {
		switch(b) {
		case Map.STAIRS_UP:
			return stairsUp;
		case Map.STAIRS_DOWN:
			return stairsDown;
		default:
			return null;
		}
	}
}
