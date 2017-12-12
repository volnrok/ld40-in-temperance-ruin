package com.ldjam.ld40.sgilhuly;

public abstract class Effect {
	public abstract void apply(Creature target);
}

class StatMod extends Effect {
	
	enum Stat {
		STR, PER, SPD, AGI, FOC
	}
	
	public Stat stat;
	public int mod;
	
	public StatMod(Stat stat, int mod) {
		this.stat = stat;
		this.mod = mod;
	}
	
	public StatMod(int statnum, int mod) {
		this(numToStat(statnum), mod);
	}

	@Override
	public void apply(Creature target) {
		switch(stat) {
		case STR:
			target.strCalc += mod;
			break;
		case PER:
			target.perCalc += mod;
			break;
		case SPD:
			target.spdCalc += mod;
			break;
		case AGI:
			target.agiCalc += mod;
			break;
		case FOC:
			target.focCalc += mod;
			break;
		}
	}
	
	public static Stat numToStat(int num) {
		switch(num) {
		case 0:
			return Stat.STR;
		case 1:
			return Stat.PER;
		case 2:
			return Stat.SPD;
		case 3:
			return Stat.AGI;
		case 4:
		default:
			return Stat.FOC;
		}
	}
}