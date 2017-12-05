package com.ldjam.ld40.sgilhuly;

import java.util.ArrayList;

public class Player extends Creature {
	
	class Savegame {
		public int gold;
		public ArrayList<int[]> goldPicked = new ArrayList<int[]>();
	}
	
	public Savegame save = new Savegame();
	
	public int posLevel;
	public int posX;
	public int posY;
	public int posDir;
	public int lastGold = 0;
	public int gold = 0;
	
	public int spells;
	public int spellsMax;
	
	public ArrayList<StatMod> wounds = new ArrayList<StatMod>();
	public Item weapon;
	public Item armour;
	public Item wand;
	public Item ring;
	
	public Player() {
		super(10, 10, 10, 10, 10, "Billy", 100);
		//super(100, 100, 100, 100, 100, "Billy", 100);
		
		posLevel = 0;
		posX = Map.START_X;
		posY = Map.START_Y;
		posDir = Map.START_DIR;

		weapon = Item.ITEMS[0][0];
		armour = Item.ITEMS[1][0];
		wand = Item.ITEMS[2][0];
		//ring = Item.ITEMS[3][5];
		ring = Item.ITEMS[3][0];
		
		GameContext.player = this;
		GameContext.currentMap = Map.MAPS[posLevel];
		
		savegame();
		
		for(int i : resistances) {
			System.out.println(i);
		}
	}
	
	public void savegame() {
		
		save.gold = gold;
		save.goldPicked.clear();
		
		heal();
	}
	
	public void loadgame() {
		
		gold = save.gold;
		
		for(int[] loc : save.goldPicked) {
			Map.MAPS[loc[2]].setMap(loc[0], loc[1], Map.TREASURE);
		}
		
		heal();
		
		posLevel = 1;
		GameContext.currentMap = Map.MAPS[1];
		posX = Map.START_X;
		posY = Map.START_Y;
		posDir = Map.START_DIR;
		GameContext.combat = null;
	}
	
	public void step(int amount) {
		byte space = GameContext.currentMap.accessMap(posX, posY, posDir, amount, 0);
		if(space == Map.TREASURE) {
			Basis b = Map.dirToBasis(posDir);
			int goldX = posX + b.forwardX * amount;
			int goldY = posY + b.forwardY * amount;
			GameContext.currentMap.setMap(goldX, goldY, Map.TREASURE_USED);
			save.goldPicked.add(new int[] {goldX, goldY, posLevel});
			GameContext.metronome.queueEvent("Gained 1 gold!", Palette.YELLOW);
			if(gold < 99) {
				gold++;
			}
		} else if(space == Map.HOARD) {
			gold = 99;
			GameContext.metronome.queueEvent("Gained 999 gold!", Palette.YELLOW);
		} else if(Map.isPassable(space)) {
			Basis b = Map.dirToBasis(posDir);
			posX += b.forwardX * amount;
			posY += b.forwardY * amount;
			
			if(space == Map.OPEN) {
				float chance = 0.06f;
				if(gold > 60) {
					chance = 0.1f;
				}
				if(Math.random() < chance) {
					new Combat(this, Monster.chooseMonster(gold));
				}
			} else if(space == Map.STAIRS_UP) {
				GameContext.transition = new Transition(posLevel - 1, true);
				if(posLevel == 1) {
					GameContext.game.visitTown();
				}
			} else if(space == Map.STAIRS_DOWN) {
				GameContext.transition = new Transition(posLevel + 1, false);
			}
		} else {
			GameContext.game.shakeScreen();
		}
	}
	
	/*public void changeFloor(boolean up) {
		if(up && )
	}*/
	
	public void turn(int amount) {
		posDir = Helper.mod(posDir + amount, 4);
	}
	
	public void recalcStats() {
		super.recalcStats();
		
		if(wounds != null) {
			// Make sure constructor has gone
			for(StatMod s : wounds) {
				s.apply(this);
			}

		weapon.apply(this);
		armour.apply(this);
		wand.apply(this);
		ring.apply(this);
		}
		
		spellsMax = 5 + (int) (focCalc / 3);
	}
	
	public void noHealth() {
		int extraDamage = 0;
		if(GameContext.combat != null && GameContext.combat.monster != null) {
			extraDamage = GameContext.combat.monster.statDamage;
		}
		final float damage = Helper.rand(4, 8) + extraDamage;
		float resistance = focCalc * 2;
		// Threshold the resistance amount
		if(focCalc > 30) {
			resistance = 45 + (focCalc - 30) * 0.5f;
		} else if(focCalc > 20) {
			resistance = 35 + (focCalc - 20) * 1;
		} else if(focCalc > 10) {
			resistance = 20 + (focCalc - 10) * 1.5f;
		}
		final int totalDamage = (int) Helper.resist(damage, resistance);
		final int affectedStat = (int) (Math.random() * 5);
		
		GameContext.metronome.interruptEvent(String.format("Lost %d %s! <%d>", totalDamage, Creature.STAT_NAMES[affectedStat], (int) damage), Palette.RED, new Runnable() {
			@Override
			public void run() {
				getWounded(totalDamage, affectedStat);
			}
		});
	}
	
	public void getWounded(int damage, int affectedStat) {
		GameContext.game.shakeScreen();
		wounds.add(new StatMod(affectedStat, -damage));
		recalcStats();
		
		if(strCalc <= 0 || perCalc <= 0 || spdCalc <= 0 || agiCalc <= 0 || focCalc <= 0) {
			GameContext.metronome.clearEvents();
			GameContext.metronome.queueEvent("You have died!", Palette.RED, new Runnable() {
				@Override
				public void run() {
					GameContext.game.shakeScreen();
					loadgame();
				}
			});
		} else {
			hp = hpMax;
		}
	}
	
	public void heal() {
		wounds.clear();
		hp = hpMax;
		recalcStats();
		spells = spellsMax;
	}
	
	public void equip(Item item) {
		switch(item.slot) {
		case ARMOUR:
			armour = item;
			break;
		case BLESSING:
			str += item.ringPower;
			per += item.ringPower;
			spd += item.ringPower;
			agi += item.ringPower;
			foc += item.ringPower;
			break;
		case RING:
			ring = item;
			break;
		case WAND:
			wand = item;
			break;
		case WEAPON:
			weapon = item;
			break;
		}
	}
	
	public void takeDamage(int damage) {
		super.takeDamage(damage);
		GameContext.game.shakeScreen();
	}

	@Override
	public int spellElement() {
		return wand.element;
	}

	@Override
	public String spellText() {
		return String.format("%s used!", wand.name);
	}

	@Override
	public String swingText() {
		return "You strike";
	}
}
