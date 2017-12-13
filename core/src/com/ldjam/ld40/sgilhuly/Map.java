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
	public static final byte WALL_CRACKED = 9;
	public static final byte WALL_GRATE = 10;
	public static final byte WALL_MOSAIC = 11;
	public static final byte WALL_VINE = 12;
	public static final byte WALL_MOLD = 13;
	public static final byte WALL_WEB = 14;
	public static final byte WALL_BLOODY = 15;
	public static final byte WALL_ARCH = 16;
	
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
		renderer.refreshDeco(posX, posY);
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
		case '%':
			return WALL_CRACKED;
		case 'G':
			return WALL_GRATE;
		case '@':
			return WALL_MOSAIC;
		case 'V':
			return WALL_VINE;
		case 'M':
			return WALL_MOLD;
		case 'W':
			return WALL_WEB;
		case 'B':
			return WALL_BLOODY;
		case 'A':
			return WALL_ARCH;
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
		case OPEN:
		case STAIRS_UP:
		case STAIRS_DOWN:
		case WALL_ARCH:
			return true;
		}
		return false;
	}
	
	public static boolean isWall(byte b) {
		switch(b) {
		case WALL:
		case WALL_CRACKED:
		case WALL_GRATE:
		case WALL_MOSAIC:
		case WALL_VINE:
		case WALL_MOLD:
		case WALL_WEB:
		case WALL_BLOODY:
		case WALL_ARCH:
			return true;
		}
		return false;
	}
	
	public static boolean isDeco(byte b) {
		switch(b) {
		case TREASURE:
		case TREASURE_USED:
		case BASIN:
		case BASIN_USED:
		case STAIRS_UP:
		case STAIRS_DOWN:
		case HOARD:
			return true;
		}
		return false;
	}
	
	public static final int START_X = 17;
	public static final int START_Y = 10;
	public static final int START_DIR = WEST;
	
	public static final Map MAP_1 = new Map(Palette.GREY, new String[] { // 14 treasures
			"#%########@####VVVVVV###",
			"% t# t ##         A    #",
			"#      @% ###VV V VVVV #",
			"@  M #  # %@### V    Vt#",
			"#       M   t%# #### @##",
			"#  #      ####         #",
			"#M#M bMG##%#### #GG@%  #",
			"M               #      #",
			"M MM MM@#M# ##@ # t    #",
			"M MG      # ##G G###%%#@",
			"M MMGMMM         ^# t  #",
			"M   M      M#@G G##    #",
			"#  tM  M   % t# ####  #@",
			"#%%##t %               #",
			"#  t#  V  V##   ### W%WG",
			"#   #%#V  #      %W Gt W",
			"#A###     #   %   # W  W",
			"%         #  @##  b #W %",
			"%  @GV VG#   t#        W",
			"# #       #       W v  W",
			"# @ V   V #       # %% #",
			"# @   v   #@@%###%# #  #",
			"%t# V   V ######t   %  #",
			"#####VVV################"
	}).addBossFight(new BossFight(16, 10, Monster.DARK_GUARDIAN, "...YoU WeRe WaRnEd...", Palette.PURPLE, new BooleanSupplier() {
		@Override
		public boolean getAsBoolean() {
			return GameContext.player.gold >= Player.MAX_GOLD;
		}
	}).shakingText());
	public static final Map MAP_2 = new Map(Palette.GREEN, new String[] { // 14 treasures (28)
			"####GG##########@#%#####",
			"#%   t#######%      ####",
			"## %##M        WW%# #%%#",
			"%       M#%%#WWW##     G",
			"# ## MM ##      %% # t #",
			"@  # M      ###    ###%#",
			"#      v%@%##    @    ##",
			"#%#@ ###    # #  %%@# %#",
			"###M %   %t   #  A  @ ##",
			"#t     b###@##G  V# # ##",
			"## B##           G  # ##",
			"#% #   %V#%@# VV @v #  #",
			"@  #t# #v     G#t#  @ t#",
			"# %### #t V  V##### #  #",
			"#   t@ #@     #G    ##B#",
			"###  #   #%VV #@ %###BAB",
			"##% @B #   t% ##   A # #",
			"### ## #   ##  MM###   #",
			"#@     #G#        A  M %",
			"@  ##AA@%%# @##%MMG ^Mt@",
			"# t##   ##@ttG##     %##",
			"#@### ^ %### %MM  MMG###",
			"#####%#####@      G#####",
			"##############G@%#######"
	});
	public static final Map MAP_3 = new Map(Palette.BLUE, new String[] { // 12 treasures (40)
			"###@G#%####%##G####%##%#",
			"%##    t%##v   B#   %%##",
			"#%# #######GGB %# t ####",
			"#                      #",
			"%G# W###%MMM#W %##% #VV#",
			"%                      V",
			"%G# #M ^ @# WW##@#G#VV @",
			"### @# MM## G% t##@ tV @",
			"%@      t#M     #%@    #",
			"##MMMMMM#MW ##MMM###% ##",
			"#                   @  #",
			"%MM ###@M @@@@@@@ ^ M# %",
			"M # G #M^ @  v  @MMM## #",
			"#   % %M  @     @      #",
			"@ #   @%M @ t@t @# %%# #",
			"#tM # BG# @  b  @V   ###",
			"#MM # v@B @     @V V   #",
			"%tM B     @@@A@@@% VVV #",
			"@ # #G%## ###      %@  G",
			"#         #### ## ##@ #%",
			"%#M %%#MMM#G#% #VtV    #",
			"#M               #  # t%",
			"##M  t ##G#%##VV   %G#%%",
			"#%##G##%####@#%#VV%##@@%"
	});
	public static final Map MAP_4 = new Map(Palette.RED, new String[] { // 12 treasures (52)
			"###########@###########%",
			"% t####### ^#######% t %",
			"# #   ##W     M#M  M   %",
			"# W W##W  GW@ @M    M MG",
			"# W  W@    ##         M#",
			"#          ##@##M  MM##%",
			"#W    %W  ##    % M%  ##",
			"#t# v#@%#G##t %    #t  #",
			"#  G#  A   ##%t%## %   #",
			"%     #G        ## #@# #",
			"%  @###%    @#####  @# #",
			"%t#Wv W#W              @",
			"##W    WW W% ^%%#    G#%",
			"#      W# ##G@%##@  ####",
			"@ #W      W#@###   ##t #",
			"@ ##W#%WW%##  #V # %#  %",
			"#  %% ^##@V      G  ## @",
			"@t ##          ###%  @ #",
			"#%###G@%VVG%  #%##     #",
			"#@  ## @#VV% ##t %    ##",
			"# t  @ V     ##  %#  ###",
			"%    %A# @##%t #    %###",
			"##   A A       ##G#%####",
			"##@##%##%#%#%%##########"
	});
	public static final Map MAP_5 = new Map(Palette.YELLOW, new String[] { // hoards
			"@####%###%##%##%#%###%%%",
			"#@#####%####HHHHHHHHHHH%",
			"##@%########HHHHHHHHHHH#",
			"#%%#%%###%%#HHHHHHHHHHH#",
			"##%#######@#HHHHHHHHHHH#",
			"@%#%###%@%##HHHHHHHHHHH@",
			"####%#G%####HHHHH HHHHH#",
			"%@#%^      #HHHH#A#HHHH@",
			"@GG# p p p G%##%# #%@#%#",
			"#G#%              #@#%##",
			"%##G p p p %#p%p%p#%%###",
			"%###^      @HHHHHHH#####",
			"%@##%%####G#@#@@#G#%%@%G",
			"###@G###G###%##%#%####%#",
			"%%###@#%#%##%##G######@#",
			"#%@######%#G%%####%#@###",
			"####%##%%G#G@@%#@G##%%#%",
			"##%%#%#@@###%#@#%%######",
			"%##%#%%#@###%#G@##%##%#%",
			"@%####@#%###%#@###%###%%",
			"###@#%###%#G%##%%@G%#G##",
			"###@%#%###%@#######%#%G#",
			"##G#@###@###%##%@%######",
			"@######%#######%#####%@#"
	}).addBossFight(new BossFight(17, 6, Monster.GUARDIAN, "This treasure is cursed!", Palette.YELLOW, new BooleanSupplier() {
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

/* Map data before adding special walls
  
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
			"# ######b        ^# t  #",
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
			return GameContext.player.gold == 1;//return GameContext.player.gold >= Player.MAX_GOLD;
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
	*/
