package com.ldjam.ld40.sgilhuly;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;

public abstract class GameSaver {
	
	static class SaveFile {
		public String playerName;
		public int playerPortrait;
		public int playerStr;
		public int playerPer;
		public int playerSpd;
		public int playerAgi;
		public int playerFoc;
		public int[][] playerItems;
		public int gold;
		public byte[][] treasureFound;
		public boolean[] monstersDefeated;
	}
	
	public static final String SAVE_FILE = "%s.itrsave";

	public static boolean doesSavegameExist(String name) {
		File f = new File(String.format(SAVE_FILE, name));
		return f.exists();
	}
	
	public static void SaveGame() {
		SaveFile save = new SaveFile();
		Player player = GameContext.player;
		
		save.playerName = player.name;
		save.playerPortrait = player.portrait;
		save.playerStr = player.str;
		save.playerPer = player.per;
		save.playerSpd = player.spd;
		save.playerAgi = player.agi;
		save.playerFoc = player.foc;
		save.gold = player.gold;
		
		save.playerItems = new int[4][2];
		save.playerItems[0][0] = player.weapon.y;
		save.playerItems[0][1] = player.weapon.x;
		save.playerItems[1][0] = player.armour.y;
		save.playerItems[1][1] = player.armour.x;
		save.playerItems[2][0] = player.wand.y;
		save.playerItems[2][1] = player.wand.x;
		save.playerItems[3][0] = player.ring.y;
		save.playerItems[3][1] = player.ring.x;
		
		ArrayList<byte[]> pedestalLocations = new ArrayList<byte[]>();
		for(byte i = 1; i < Map.MAPS.length; i++) {
			for(byte y = 0; y < Map.MAP_HEIGHT; y++) {
				for(byte x = 0; x < Map.MAP_WIDTH; x++) {
					if(Map.MAPS[i].accessMap(x, y) == Map.TREASURE_USED) {
						pedestalLocations.add(new byte[] {i, y, x});
					}
				}
			}
		}
		save.treasureFound = pedestalLocations.toArray(new byte[0][0]);
		
		save.monstersDefeated = new boolean[Monster.MONSTERS.length];
		for(int i = 0; i < Monster.MONSTERS.length; i++) {
			save.monstersDefeated[i] = Monster.MONSTERS[i].defeated;
		}
		
		Json json = new Json();
		System.out.println(json.toJson(save));
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(String.format(SAVE_FILE, player.name)));
			writer.write(json.toJson(save));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void LoadGame() {
		Player player = GameContext.player;
		String j = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(String.format(SAVE_FILE, player.name)));
			j = reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Json json = new Json();
		SaveFile save = json.fromJson(SaveFile.class, j);
		
		player.name = save.playerName;
		player.portrait = save.playerPortrait;
		player.str = save.playerStr;
		player.per = save.playerPer;
		player.spd = save.playerSpd;
		player.agi = save.playerAgi;
		player.foc = save.playerFoc;
		player.gold = save.gold;
		player.lastGold = save.gold;

		player.weapon = Item.ITEMS[save.playerItems[0][0]][save.playerItems[0][1]];
		player.armour = Item.ITEMS[save.playerItems[1][0]][save.playerItems[1][1]];
		player.wand = Item.ITEMS[save.playerItems[2][0]][save.playerItems[2][1]];
		player.ring = Item.ITEMS[save.playerItems[3][0]][save.playerItems[3][1]];
		
		for(byte[] loc : save.treasureFound) {
			Map.MAPS[loc[0]].setMap(loc[2], loc[1], Map.TREASURE_USED);
		}
		
		for(int i = 0; i < Monster.MONSTERS.length; i++) {
			Monster.MONSTERS[i].defeated = save.monstersDefeated[i];
		}
	}
}
