package com.ldjam.ld40.sgilhuly;

public abstract class Creature {
	
	public static final String[] STAT_NAMES = new String[] {
			"strength", "perception", "speed", "agility", "focus"
	};
	
	public int str;
	public int per;
	public int spd;
	public int agi;
	public int foc;
	
	public int strCalc;
	public int perCalc;
	public int spdCalc;
	public int agiCalc;
	public int focCalc;
	
	public int hp;
	public int hpMax;
	
	public Creature(int str, int per, int spd, int agi, int foc) {
		this.str = str;
		this.per = per;
		this.spd = spd;
		this.agi = agi;
		this.foc = foc;
		
		recalcStats();
	}
	
	public void recalcStats() {
		strCalc = str;
		perCalc = per;
		spdCalc = spd;
		agiCalc = agi;
		focCalc = foc;
	} 
}
