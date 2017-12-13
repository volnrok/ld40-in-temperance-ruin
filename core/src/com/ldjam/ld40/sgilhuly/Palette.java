package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Palette {
	public static final int RED_1 = 0x94070aff;
	public static final int RED_2 = 0xed1c24ff;
	public static final int RED_3 = 0xf37b70ff;
	public static final int RED_4 = 0xfcd4d1ff;

	public static final int ORANGE_1 = 0x985006ff;
	public static final int ORANGE_2 = 0xf58220ff;
	public static final int ORANGE_3 = 0xf9a870ff;
	public static final int ORANGE_4 = 0xfedcc6ff;

	public static final int YELLOW_1 = 0xa09600ff;
	public static final int YELLOW_2 = 0xfff200ff;
	public static final int YELLOW_3 = 0xfff685ff;
	public static final int YELLOW_4 = 0xfffbccff;

	public static final int GREEN_1 = 0x006c3bff;
	public static final int GREEN_2 = 0x00a65dff;
	public static final int GREEN_3 = 0x65c295ff;
	public static final int GREEN_4 = 0xbee3d3ff;

	public static final int BLUE_1 = 0x003d73ff;
	public static final int BLUE_2 = 0x0066b3ff;
	public static final int BLUE_3 = 0x5e8ac7ff;
	public static final int BLUE_4 = 0xadc5e7ff;

	public static final int PURPLE_1 = 0x390a5dff;
	public static final int PURPLE_2 = 0x5c2d91ff;
	public static final int PURPLE_3 = 0x826aafff;
	public static final int PURPLE_4 = 0xbcaed5ff;

	public static final int GREY_1 = 0x343434ff;
	public static final int GREY_2 = 0x636363ff;
	public static final int GREY_3 = 0x9d9d9dff;
	public static final int GREY_4 = 0xd8d8d8ff;

	public static final int WHITE = 0xf2f2f2ff;
	public static final int BLACK = 0x1a1a1aff;

	public static final int GREY = 0;
	public static final int RED = 1;
	public static final int ORANGE = 2;
	public static final int YELLOW = 3;
	public static final int GREEN = 4;
	public static final int BLUE = 5;
	public static final int PURPLE = 6;
	
	public static final int[][] PALETTE = {
		{GREY_1, GREY_2, GREY_3, GREY_4},
		{RED_1, RED_2, RED_3, RED_4},
		{ORANGE_1, ORANGE_2, ORANGE_3, ORANGE_4},
		{YELLOW_1, YELLOW_2, YELLOW_3, YELLOW_4},
		{GREEN_1, GREEN_2, GREEN_3, GREEN_4},
		{BLUE_1, BLUE_2, BLUE_3, BLUE_4},
		{PURPLE_1, PURPLE_2, PURPLE_3, PURPLE_4},
	};
	
	public static Texture loadSwapped(String file, int from, int to) {
		Texture tex = new Texture(Gdx.files.internal(file));
		if(from == to) {
			return tex;
		}
		
		Texture tex2 = paletteSwap(tex, from, to);
		tex.dispose();
		return tex2;
	}
	
	public static Texture paletteSwap(Texture tex, int from, int to) {
		
		if(from == to) {
			return tex;
		}
		
		if(!tex.getTextureData().isPrepared()) {
			tex.getTextureData().prepare();
		}
		
		Pixmap pixmap = tex.getTextureData().consumePixmap();
		for(int y = 0; y < pixmap.getHeight(); y++) {
			for(int x = 0; x < pixmap.getWidth(); x++) {
				for(int i = 0; i < 4; i++) {
					if(pixmap.getPixel(x, y) == PALETTE[from][i]) {
						pixmap.drawPixel(x, y, PALETTE[to][i]);
						i = 4;
					}
				}
			}
		}
		return new Texture(pixmap);
	}
}
