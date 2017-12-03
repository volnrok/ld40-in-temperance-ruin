package com.ldjam.ld40.sgilhuly;

public class Map {
	public static final int MAP_WIDTH = 8;
	public static final int MAP_HEIGHT = 8;
	
	public static final byte OPEN = 0;
	public static final byte WALL = 1;
	public static final byte TREASURE = 2;
	public static final byte TREASURE_USED = 3;
	public static final byte STAIRS_UP = 4;
	public static final byte STAIRS_DOWN = 5;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	
	public static final String[] DIR_LETTERS = {
		"n", "e", "s", "w"
	};
	
	private static final Basis[] BASES = new Basis[] {
			new Basis(NORTH),
			new Basis(EAST),
			new Basis(SOUTH),
			new Basis(WEST)
	};
	
	private byte[][] mapData;
	
	public Map(String[] data) {
		
		mapData = new byte[MAP_HEIGHT][MAP_WIDTH];
		
		for(int y = 0; y < MAP_HEIGHT; y++) {
			for(int x = 0; x < MAP_WIDTH; x++) {
				mapData[y][x] = charToData(data[y].charAt(x));
			}
		}
	}
	
	public byte accessMap(int posX, int posY, int posDir, int forward, int right) {
		Basis b = dirToBasis(posDir);
		posX += forward * b.forwardX + right * b.rightX;
		posY += forward * b.forwardY + right * b.rightY;
		posX = Helper.iclamp(posX, 0, MAP_WIDTH - 1);
		posY = Helper.iclamp(posY, 0, MAP_HEIGHT - 1);
		
		return mapData[posY][posX];
	}
	
	public static Basis dirToBasis(int dir) {
		return BASES[Helper.mod(dir, 4)];
	}
	
	public static byte charToData(char c) {
		switch(c) {
		case '#':
			return WALL;
		case 'T':
			return TREASURE;
		case '^':
			return STAIRS_UP;
		case 'v':
			return STAIRS_DOWN;
		case ' ':
		default:
			return OPEN;
		}
	}
	
	public static boolean isPassable(byte b) {
		switch(b) {
		case WALL:
		case TREASURE:
		case TREASURE_USED:
			return false;
		default:
			return true;
		}
	}
	
	public static final Map MAP_1 = new Map(new String[] {
			"########",
			"#      #",
			"## ### #",
			"##   # #",
			"# #  # #",
			"# # #  #",
			"#      #",
			"########"
	});
	
	public static Map getMap(int i) {
		switch(i) {
		case 1:
			return MAP_1;
		default:
			return null;
		}
	}
}

class Basis {
	public final int forwardY;
	public final int forwardX;
	public final int rightY;
	public final int rightX;
	
	public Basis(int dir) {
		switch(dir) {
		case Map.NORTH:
			forwardY = -1;
			forwardX = 0;
			rightY = 0;
			rightX = 1;
			break;
		case Map.SOUTH:
			forwardY = 1;
			forwardX = 0;
			rightY = 0;
			rightX = -1;
			break;
		case Map.EAST:
			forwardY = 0;
			forwardX = 1;
			rightY = 1;
			rightX = 0;
			break;
		case Map.WEST:
		default:
			forwardY = 0;
			forwardX = -1;
			rightY = -1;
			rightX = 0;
			break;
		}
	}
}
