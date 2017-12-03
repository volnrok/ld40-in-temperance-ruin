package com.ldjam.ld40.sgilhuly;

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
	
	public static Texture paletteSwap(Texture tex, int[][] swaps) {
		
		if(!tex.getTextureData().isPrepared()) {
			tex.getTextureData().prepare();
		}
		
		Pixmap pixmap = tex.getTextureData().consumePixmap();
		//TODO swaps
		tex.draw(pixmap, 0, 0);
		
		return null;
	}
}
