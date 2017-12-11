package com.ldjam.ld40.sgilhuly;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

public class Map {
	public static final int MAP_WIDTH = 24;
	public static final int MAP_HEIGHT = 24;
	
	public static final byte OPEN = 0;
	public static final byte WALL = 1;
	public static final byte TREASURE = 2;
	public static final byte TREASURE_USED = 3;
	public static final byte STAIRS_UP = 4;
	public static final byte STAIRS_DOWN = 5;
	public static final byte HOARD = 6;
	public static final byte BASIN = 7;
	public static final byte BASIN_USED = 8;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	
	static class BossFight {
		public int x;
		public int y;
		public Monster monster;
		public BooleanSupplier condition;
		public String message;
		public int palette;
		public boolean encountered = false;
		public boolean shakingText = false;
		
		public BossFight(int x, int y, Monster monster, String message, int palette, BooleanSupplier condition) {
			this.x = x;
			this.y = y;
			this.monster = monster;
			this.condition = condition;
			this.message = message;
			this.palette = palette;
		}
		
		public BossFight shakingText() {
			shakingText = true;
			return this;
		}
	}
	
	public static final String[] DIR_LETTERS = {
		"n", "e", "s", "w"
	};
	
	private static final Basis[] BASES = {
			new Basis(NORTH),
			new Basis(EAST),
			new Basis(SOUTH),
			new Basis(WEST)
	};
	
	private byte[][] mapData;
	public int palette;
	public ArrayList<BossFight> bossFights = new ArrayList<BossFight>();
	public MapRenderer renderer;
	
	public Map(int palette, String[] data) {
		
		this.palette = palette;
		
		mapData = new byte[MAP_HEIGHT][MAP_WIDTH];
		
		for(int y = 0; y < MAP_HEIGHT; y++) {
			for(int x = 0; x < MAP_WIDTH; x++) {
				mapData[y][x] = charToData(data[y].charAt(x));
			}
		}
		
		renderer = new MapRenderer(this);
	}
	
	public Map addBossFight(BossFight fight) {
		bossFights.add(fight);
		return this;
	}
	
	public BossFight checkForBoss(int posX, int posY) {
		for(BossFight b : bossFights) {
			if(b.x == posX && b.y == posY && !b.encountered && b.condition.getAsBoolean()) {
				return b;
			}
		}
		return null;
	}
	
	public byte accessMap(int posX, int posY, int posDir, int forward, int right) {
		Basis b = dirToBasis(posDir);
		posX += forward * b.forwardX + right * b.rightX;
		posY += forward * b.forwardY + right * b.rightY;
		posX = Helper.iclamp(posX, 0, MAP_WIDTH - 1);
		posY = Helper.iclamp(posY, 0, MAP_HEIGHT - 1);
		
		return mapData[posY][posX];
	}
	
	public byte accessMap(int posX, int posY) {
		return mapData[posY][posX];
	}
	
	public void setMap(int posX, int posY, byte val) {
		posX = Helper.iclamp(posX, 0, MAP_WIDTH - 1);
		posY = Helper.iclamp(posY, 0, MAP_HEIGHT - 1);
		
		mapData[posY][posX] = val;
	}
	
	public void resetConsumables() {
		for(int y = 0; y < MAP_HEIGHT; y++) {
			for(int x = 0; x < MAP_WIDTH; x++) {
				if(mapData[y][x] == BASIN_USED) {
					mapData[y][x] = BASIN;
				}
			}
		}
		
		for(BossFight b : bossFights) {
			b.encountered = false;
		}
	}
	
	public static Basis dirToBasis(int dir) {
		return BASES[Helper.mod(dir, 4)];
	}
	
	public static byte charToData(char c) {
		switch(c) {
		case '#':
			return WALL;
		case 't':
			return TREASURE;
		case 'p':
			return TREASURE_USED;
		case 'b':
			return BASIN;
		case '^':
			return STAIRS_UP;
		case 'v':
			return STAIRS_DOWN;
		case 'H':
			return HOARD;
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
		case BASIN:
		case BASIN_USED:
		case HOARD:
			return false;
		default:
			return true;
		}
	}
	
	public static final int START_X = 17;
	public static final int START_Y = 10;
	public static final int START_DIR = WEST;
	
	public static final Map MAP_1 = new Map(Palette.GREY, new String[] { // 14 treasures
			"########################",
			"# t# t ##              #",
			"#      ## ##### # #### #",
			"#  # #  # ##### #    #t#",
			"#       #   t## #### ###",
			"#  #      ####         #",
			"#### b######### #####  #",
			"#               #      #",
			"# ## ###### ### # t    #",
			"# ##      # ### ########",
			"# ######         ^# t  #",
			"#   #      #### ###    #",
			"#  t#  #   # t# ####  ##",
			"#####t #               #",
			"#  t#  #  ###   ### ####",
			"#   ####  #      ## #t #",
			"# ###     #   #   # #  #",
			"#         #  ###  b ## #",
			"#  ### ###   t#        #",
			"# #       #       # v  #",
			"# # #   # #       # ## #",
			"# #   v   ######### #  #",
			"#t# #   # ######t   #  #",
			"########################"
	}).addBossFight(new BossFight(16, 10, Monster.DARK_GUARDIAN, "...YoU WeRe WaRnEd...", Palette.PURPLE, new BooleanSupplier() {
		@Override
		public boolean getAsBoolean() {
			return GameContext.player.gold >= Player.MAX_GOLD;
		}
	}).shakingText());
	public static final Map MAP_2 = new Map(Palette.GREEN, new String[] { // 14 treasures (28)
			"########################",
			"##   t########      ####",
			"## ####        #### ####",
			"#       ##########     #",
			"# ## ## ##      ## # t #",
			"#  # #      ###    #####",
			"#      v#####    #    ##",
			"#### ###    # #  #### ##",
			"#### #   #t   #     # ##",
			"#t     b#######  ## # ##",
			"## ###           #  # ##",
			"## #   ###### ## #v #  #",
			"#  #t# #v     ##t#  # t#",
			"# #### #t #  ###### #  #",
			"#   t# ##     ##    ####",
			"###  #   #### ## ##### #",
			"### ## #   t# ##     # #",
			"### ## #   ##  #####   #",
			"##     ###           # #",
			"#  ##  #### ####### ^#t#",
			"# t##   ###tt###     ###",
			"##### ^ #### ###  ######",
			"############      ######",
			"########################"
	});
	public static final Map MAP_3 = new Map(Palette.BLUE, new String[] { // 12 treasures (40)
			"########################",
			"###    t###v   ##   ####",
			"### ########## ## t ####",
			"#                      #",
			"### ########## #### ####",
			"#                      #",
			"### ## ^ ## ########## #",
			"### ## #### ## t### t# #",
			"##      t##     ###    #",
			"########### ######### ##",
			"#                   #  #",
			"### ##### ####### ^ ## #",
			"# # # ##^ #  v  ###### #",
			"#   # ##  #     #      #",
			"# #   ### # t#t ## ### #",
			"#t# # ### #  b  ##   ###",
			"### # v## #     ## #   #",
			"#t# #     ### #### ### #",
			"# # ##### ###      ##  #",
			"#         #### ## ### ##",
			"### ########## ##t#    #",
			"##               #  # t#",
			"###  t #########   #####",
			"########################"
	});
	public static final Map MAP_4 = new Map(Palette.RED, new String[] { // 12 treasures (52)
			"########################",
			"# t####### ^######## t #",
			"# #   ###     ###  #   #",
			"# # ####  ### ##    # ##",
			"# #  ##    ##         ##",
			"#          ######  #####",
			"##    ##  ##    # ##  ##",
			"#t# v#######t #    #t  #",
			"#  ##      ###t### #   #",
			"#     ##        ## ### #",
			"#  #####    ######  ## #",
			"#t##v ###              #",
			"###    ## ## ^###    ###",
			"#      ## ########  ####",
			"# ##      ######   ##t #",
			"# ##########  ## # ##  #",
			"#  ## ^####      #  ## #",
			"#t ##          ####  # #",
			"############  ####     #",
			"##  ## ##### ##t #    ##",
			"# t  # #     ##  ##  ###",
			"#    # # ####t #    ####",
			"##             #########",
			"########################"
	});
	public static final Map MAP_5 = new Map(Palette.YELLOW, new String[] { // hoards
			"########################",
			"########################",
			"################HHHHHHH#",
			"################HHHHHHH#",
			"################HHHHHHH#",
			"################HHH HHH#",
			"#################H# #H##",
			"####^      #HHHHH## ####",
			"#### p p p ##p#p### ####",
			"####                ####",
			"#### p p p ##p#p########",
			"####^      #HHHHH#######",
			"########################",
			"########################",
			"########################",
			"########################",
			"########################",
			"########################",
			"########################",
			"########################",
			"########################",
			"########################",
			"########################",
			"########################"
	}).addBossFight(new BossFight(19, 5, Monster.GUARDIAN, "This treasure is cursed!", Palette.YELLOW, new BooleanSupplier() {
		@Override
		public boolean getAsBoolean() {
			return true;
		}
	}));
	// GREY, GREEN, BLUE, PURPLE, RED, YELLOW
	
	public static final Map[] MAPS = {
			null,
			MAP_1,
			MAP_2,
			MAP_3,
			MAP_4,
			MAP_5
	};
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
