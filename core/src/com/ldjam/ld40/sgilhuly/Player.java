package com.ldjam.ld40.sgilhuly;

import java.util.ArrayList;

public class Player extends Creature {
	public int posLevel;
	public int posX;
	public int posY;
	public int posDir;
	public int gold = 0;
	public String name = "Billy";
	
	public ArrayList<StatMod> wounds = new ArrayList<StatMod>();
	
	public Player() {
		super(10, 10, 10, 10, 10);
		
		posLevel = 1;
		posX = 1;
		posY = 1;
		posDir = Map.EAST;
		
		hp = 50;
		hpMax = 100;
		
		GameContext.player = this;
		GameContext.currentMap = Map.MAP_1;
	}
	
	public void step(int amount) {
		byte space = GameContext.currentMap.accessMap(posX, posY, posDir, amount, 0);
		if(Map.isPassable(space)) {
			Basis b = Map.dirToBasis(posDir);
			posX += b.forwardX * amount;
			posY += b.forwardY * amount;
		} else {
			GameContext.game.shakeScreen();
		}
	}
	
	public void turn(int amount) {
		posDir = Helper.mod(posDir + amount, 4);
	}
	
	public void recalcStats() {
		super.recalcStats();
		
		if(wounds != null) {
			for(StatMod s : wounds) {
				s.apply(this);
			}
		}
	}
	
	public void getWounded() {
		int damage = (int) (Math.random() * 6 + 4);
		float reduction = (float) (Math.random() * focCalc / 40);
		int totalDamage = (int) (damage * (1 - reduction));
		int affectedStat = (int) (Math.random() * 5);
		wounds.add(new StatMod(affectedStat, -totalDamage));
		recalcStats();
		
		GameContext.metronome.interruptEvent(String.format("Lost %d <%d> %s", totalDamage, damage, Creature.STAT_NAMES[affectedStat]), Palette.RED);
	}
}
