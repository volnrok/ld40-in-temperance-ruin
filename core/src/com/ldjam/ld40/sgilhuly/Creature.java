package com.ldjam.ld40.sgilhuly;

public abstract class Creature {
	
	public static final String[] STAT_NAMES = {
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
	
	public int atk;
	public int def;
	
	public int hp;
	public int hpMax;
	public String name;
	
	public int baseSwings = 1;
	public int totalSwings;
	
	public int[] resistances = {
			0, 0, 0, 0, 0, 0, 0
	};
	
	public Creature(int str, int per, int spd, int agi, int foc, String name, int hp) {
		this.str = str;
		this.per = per;
		this.spd = spd;
		this.agi = agi;
		this.foc = foc;
		
		this.hp = hp;
		hpMax = hp;
		this.name = name;
		
		recalcStats();
	}
	
	public void recalcStats() {
		strCalc = str;
		perCalc = per;
		spdCalc = spd;
		agiCalc = agi;
		focCalc = foc;
		
		atk = perCalc + agiCalc;
		def = spdCalc + agiCalc;
		totalSwings = baseSwings;
	}
	
	public abstract void noHealth();
	
	public void takeDamage(int damage) {
		hp -= damage;
		if(hp <= 0) {
			hp = 0;
			noHealth();
		}
	}
	
	public boolean didHit(Creature other) {
		int diff = atk - other.def;
		float chance = Helper.sigmoid(diff * 2 + Constants.ACCURACY_BONUS);
		GameContext.metronome.queueEvent(String.format("%f%% to hit", chance), Palette.YELLOW);
		return Math.random() < chance;
	}
	
	public int numSwings(Creature other) {
		if(Math.random() < (spdCalc - other.spdCalc) * 1.0f / other.spdCalc) {
			return totalSwings + 1;
		}
		return totalSwings;
	}
	
	public float swingDamage() {
		float damage = 0;
		// roll 5d(str/2)
		for(int i = 0; i < 5; i++) {
			damage += Helper.rand(0, strCalc / 2.0f);
		}
		return damage;
	}
	
	public float spellDamage() {
		// roll foc - foc*6
		return Helper.rand(focCalc, focCalc * 6);
	}
	
	public float resistDamage(float damage, int element) {
		return Helper.resist(damage, resistances[element]);
	}
	
	public abstract int spellElement();
	public abstract String spellText();
	public abstract String swingText();
}
