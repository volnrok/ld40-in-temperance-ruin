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
	private Texture[] pedestal;
	private Texture[] emptyPedestal;
	private Texture[] hoard;
	private Texture[] basin;
	private Texture[] emptyBasin;
	
	public MapDrawer(int palette) {
		
		background = Palette.loadSwapped("texture/walls/background.png", Palette.GREY, palette);
		sideLeft = Palette.loadSwapped("texture/walls/side_left.png", Palette.GREY, palette);
		sideLeftFar = Palette.loadSwapped("texture/walls/side_left_far.png", Palette.GREY, palette);
		sideRight = Palette.loadSwapped("texture/walls/side_right.png", Palette.GREY, palette);
		sideRightFar = Palette.loadSwapped("texture/walls/side_right_far.png", Palette.GREY, palette);
		wall = Palette.loadSwapped("texture/walls/wall.png", Palette.GREY, palette);
		wallFar = Palette.loadSwapped("texture/walls/wall_far.png", Palette.GREY, palette);

		stairsUp = new Texture[] {
				Palette.loadSwapped("texture/deco/stairsUp.png", Palette.GREY, palette),
				Palette.loadSwapped("texture/deco/stairsUpFar.png", Palette.GREY, palette)
		};
		stairsDown = new Texture[] {
				Palette.loadSwapped("texture/deco/stairsDown.png", Palette.GREY, palette),
				Palette.loadSwapped("texture/deco/stairsDownFar.png", Palette.GREY, palette)
		};
		pedestal = new Texture[] {
				Palette.loadSwapped("texture/deco/pedestal.png", Palette.GREY, palette),
				Palette.loadSwapped("texture/deco/pedestalFar.png", Palette.GREY, palette)
		};
		emptyPedestal = new Texture[] {
				Palette.loadSwapped("texture/deco/emptyPedestal.png", Palette.GREY, palette),
				Palette.loadSwapped("texture/deco/emptyPedestalFar.png", Palette.GREY, palette)
		};
		hoard = new Texture[] {
				Palette.loadSwapped("texture/deco/hoard.png", Palette.GREY, palette),
				Palette.loadSwapped("texture/deco/hoardFar.png", Palette.GREY, palette)
		};
		basin = new Texture[] {
				Palette.loadSwapped("texture/deco/basin.png", Palette.GREY, palette),
				Palette.loadSwapped("texture/deco/basinFar.png", Palette.GREY, palette)
		};
		emptyBasin = new Texture[] {
				Palette.loadSwapped("texture/deco/emptyBasin.png", Palette.GREY, palette),
				Palette.loadSwapped("texture/deco/emptyBasinFar.png", Palette.GREY, palette)
		};
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
		case Map.TREASURE:
			return pedestal;
		case Map.TREASURE_USED:
			return emptyPedestal;
		case Map.BASIN:
			return basin;
		case Map.BASIN_USED:
			return emptyBasin;
		case Map.HOARD:
			return hoard;
		default:
			return null;
		}
	}
}
