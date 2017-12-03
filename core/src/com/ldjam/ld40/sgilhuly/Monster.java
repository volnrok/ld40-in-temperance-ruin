package com.ldjam.ld40.sgilhuly;

public class Monster extends Creature {

	public Monster(int str, int per, int spd, int agi, int foc, int hp) {
		super(str, per, spd, agi, foc);
		hpMax = hp;
		this.hp = hp;
	}
}
