package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item extends Effect {
	
	enum SlotType {
		WEAPON, ARMOUR, WAND, RING, BLESSING
	}
	
	private static Texture masterTexture;
	private static TextureRegion tex[][];
	private static Texture blessingTexture;
	private static TextureRegion blessingRegion;
	
	public static final Item ITEMS[][];
	
	static {
		masterTexture = new Texture("texture/items.png");
		tex = new TextureRegion[4][6];
		blessingTexture = new Texture("texture/blessing.png");
		blessingRegion = new TextureRegion(blessingTexture);
		
		for(int y = 0; y < 4; y++) {
			for(int x = 0; x < 6; x++) {
				tex[y][x] = new TextureRegion(masterTexture, x * 8, y * 8, 8, 8);
			}
		}
		
		ITEMS = new Item[][] {
			{
				new Item(SlotType.WEAPON, "Iron Sword", tex[0][0], 0, 0, 0, 0, 0),
				new Item(SlotType.WEAPON, "War Axe", tex[0][1], 5, 0, 0, 0, 0),
				new Item(SlotType.WEAPON, "Great Lance", tex[0][2], 10, 0, 0, 0, 0),
				new Item(SlotType.WEAPON, "Wind Blade", tex[0][3], 2, 0, 8, 0, 0).swings(1),
				new Item(SlotType.WEAPON, "Silken Bow", tex[0][4], 0, 12, 5, 0, 0),
				new Item(SlotType.WEAPON, "Golden Foil", tex[0][5], 4, 0, 10, 10, 0).swings(1)
			},
			{
				new Item(SlotType.ARMOUR, "Tunic", tex[1][0], 0, 0, 0, 0, 0),
				new Item(SlotType.ARMOUR, "Padded Mail", tex[1][1], 0, 0, 0, 0, 0).phys(10),
				new Item(SlotType.ARMOUR, "Ring Mail", tex[1][2], 0, 0, 0, 0, 0).phys(20),
				new Item(SlotType.ARMOUR, "Magician Robe", tex[1][3], 0, 0, 0, 0, 10).phys(10),
				new Item(SlotType.ARMOUR, "Plate Mail", tex[1][4], 0, 0, 0, 0, 0).phys(30),
				new Item(SlotType.ARMOUR, "Meteor  Mail", tex[1][5], 0, 0, 0, 0, 0).phys(40)
			},
			{
				new Item(SlotType.WAND, "Sparks", tex[2][0], 0, 0, 0, 0, 0).element(Combat.ELEC),
				new Item(SlotType.WAND, "Firebolt", tex[2][1], 2, 0, 0, 0, 3).element(Combat.FIRE),
				new Item(SlotType.WAND, "Acid Burst", tex[2][2], 0, 0, 2, 0, 6).element(Combat.POIS),
				new Item(SlotType.WAND, "Ice Shards", tex[2][3], 0, 0, 0, 2, 9).element(Combat.ICE),
				new Item(SlotType.WAND, "Distortion", tex[2][4], 0, 2, 0, 0, 12).element(Combat.PHYS),
				new Item(SlotType.WAND, "Death Ray", tex[2][5], 0, 0, 0, 0, 20).element(Combat.DEATH)
			},
			{
				new Item(SlotType.RING, "No Ring", tex[3][0], 0, ""),
				new Item(SlotType.RING, "Copper Ring", tex[3][1], 1, "Ring discovered!\n+50 Fire Resist"),
				new Item(SlotType.RING, "Silver Ring", tex[3][2], 2, "Ring discovered!\n+50 Ice Resist"),
				new Item(SlotType.RING, "Golden Ring", tex[3][3], 3, "Ring discovered!\n+60 Electric Resist"),
				new Item(SlotType.RING, "Jasper Ring", tex[3][4], 4, "Ring discovered!\n+80 Poison Resist"),
				new Item(SlotType.RING, "Stariron Ring", tex[3][5], 5, "Ring discovered!\n+120 Death Resist")
			},
			{
				new Item(SlotType.BLESSING, 0),
				new Item(SlotType.BLESSING, 1),
				new Item(SlotType.BLESSING, 1),
				new Item(SlotType.BLESSING, 2),
				new Item(SlotType.BLESSING, 3),
				new Item(SlotType.BLESSING, 4)
			}
		};
		
		for(int y = 0; y < ITEMS.length; y++) {
			for(int x = 0; x < ITEMS[0].length; x++) {
				ITEMS[y][x].x = x;
				ITEMS[y][x].y = y;
			}
		}
	}
	
	public SlotType slot;
	public TextureRegion icon;
	public String name;
	public String text;
	public int strMod = 0;
	public int perMod = 0;
	public int spdMod = 0;
	public int agiMod = 0;
	public int focMod = 0;
	public int ringPower = 0;
	public int physResist = 0;
	public int element = Combat.PHYS;
	public int swings = 0;
	
	public int x;
	public int y;
	
	public Item(SlotType slot, int ringPower) {
		this.slot = slot;
		this.name = "Blessing";
		this.icon = blessingRegion;
		this.ringPower = ringPower;
		this.text = String.format("+%d to all stats",  ringPower);
	}
	
	public Item(SlotType slot, String name, TextureRegion icon, int ringPower, String text) {
		this.slot = slot;
		this.name = name;
		this.icon = icon;
		this.ringPower = ringPower;
		this.text = text;
	}
	
	public Item(SlotType slot, String name, TextureRegion icon, int str, int per, int spd, int agi, int foc) {
		this.slot = slot;
		strMod = str;
		perMod = per;
		spdMod = spd;
		agiMod = agi;
		focMod = foc;
		
		this.name = name;
		this.icon = icon;
		
		this.text = "";
		if(str > 0) {
			 this.text += String.format("+%d str\n", str);
		}
		if(per > 0) {
			 this.text += String.format("+%d per\n", per);
		}
		if(spd > 0) {
			 this.text += String.format("+%d spd\n", spd);
		}
		if(agi > 0) {
			 this.text += String.format("+%d agi\n", agi);
		}
		if(foc > 0) {
			 this.text += String.format("+%d foc\n", foc);
		}
	}
	
	public Item phys(int physResist) {
		this.physResist = physResist;
		this.text = String.format("%d damage resistance\n", physResist) + this.text;
		return this;
	}
	
	public Item element(int element) {
		this.element = element;
		return this;
	}
	
	public Item swings(int swings) {
		this.swings = swings;
		this.text = String.format("+%d extra swing\n", swings) + this.text;
		return this;
	}

	@Override
	public void apply(Creature target) {
		target.strCalc += strMod;
		target.perCalc += perMod;
		target.spdCalc += spdMod;
		target.agiCalc += agiMod;
		target.focCalc += focMod;
		
		target.totalSwings += swings;
		
		if(ringPower > 0) {
			switch(ringPower) {
			case 5:
				target.resistances[Combat.DEATH] = 120;
			case 4:
				target.resistances[Combat.POIS] = 80;
			case 3:
				target.resistances[Combat.ELEC] = 60;
			case 2:
				target.resistances[Combat.ICE] = 50;
			case 1:
				target.resistances[Combat.FIRE] = 50;
			}
		}
	}
}
