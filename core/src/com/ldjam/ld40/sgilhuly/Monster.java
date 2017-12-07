package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.graphics.Texture;

public class Monster extends Creature {
	
	public Texture image;
	public int statDamage = 0;
	public String spellName = "";
	public float spellChance = 0;
	public int element = Combat.PHYS;
	public int difficulty = 1;
	public boolean shadow = false;
	public boolean defeated = false;

	public Monster(String name, int str, int per, int spd, int agi, int foc, int hp) {
		super(str, per, spd, agi, foc, name, hp);
	}
	
	public Monster loadImage(String file, int from, int to) {
		image = Palette.loadSwapped(file, from, to);
		return this;
	}
	
	public Monster resist(int element, int amount) {
		resistances[element] = amount;
		return this;
	}
	
	public Monster statDamage(int statDamage) {
		this.statDamage = statDamage;
		return this;
	}
	
	public Monster difficulty(int difficulty) {
		this.difficulty = difficulty;
		if(statDamage == 0) {
			statDamage = difficulty / 10;
		}
		return this;
	}
	
	public Monster shadow(boolean shadow) {
		this.shadow = shadow;
		return this;
	}
	
	public Monster swings(int swings) {
		baseSwings = swings;
		return this;
	}
	
	public Monster spell(String spellName, float spellChance, int element) {
		this.spellName = spellName;
		this.spellChance = spellChance;
		this.element = element;
		return this;
	}
	
	public void chooseAction() {
		if(Math.random() * 100 < spellChance) {
			GameContext.combat.monsterAction(Combat.SPELL);
		} else {
			GameContext.combat.monsterAction(Combat.SWING);
		}
	}
	
	public void takeDamage(int damage) {
		super.takeDamage(damage);
		GameContext.combat.shakeMonster();
	}

	@Override
	public void noHealth() {
		GameContext.combat.endCombat(true);
		if(!defeated) {
			GameContext.player.gainGold(1);
			GameContext.player.save.monstersBeaten.add(this);
			defeated = true;
		}
	}

	@Override
	public int spellElement() {
		return element;
	}

	@Override
	public String spellText() {
		return name + " uses " + spellName + "!";
	}

	@Override
	public String swingText() {
		return name + " strikes";
	}
	
	// Max name should be 8 characters
	public static Monster[] MONSTERS = {
			new Monster("Imp", 8, 8, 10, 6, 0, 40).loadImage("texture/monsters/imp.png", 0, 0),
			new Monster("Familiar", 9, 10, 11, 6, 0, 40).loadImage("texture/monsters/imp.png", Palette.RED, Palette.BLUE).difficulty(3),
			new Monster("Quasit", 10, 12, 12, 8, 4, 40).loadImage("texture/monsters/imp.png", Palette.RED, Palette.GREEN).difficulty(5).spell("Flare", 30, Combat.FIRE),
			new Monster("Darkling", 12, 13, 15, 10, 6, 40).loadImage("texture/monsters/imp.png", Palette.RED, Palette.PURPLE).difficulty(10).spell("Flare", 40, Combat.FIRE).resist(Combat.FIRE, 40).resist(Combat.ELEC, 40),
			new Monster("Zapper", 14, 14, 24, 12, 7, 60).loadImage("texture/monsters/imp.png", Palette.RED, Palette.YELLOW).difficulty(25).spell("Zap", 50, Combat.ELEC).resist(Combat.ELEC, 80),

			new Monster("Ratman", 14, 10, 12, 10, 0, 60).loadImage("texture/monsters/rat.png", 0, 0).difficulty(8).resist(Combat.PHYS, 20),
			new Monster("Sneaker", 10, 11, 13, 12, 0, 60).loadImage("texture/monsters/rat.png", Palette.YELLOW, Palette.ORANGE).difficulty(13).resist(Combat.PHYS, 20).swings(2),
			new Monster("Virulent", 15, 12, 14, 14, 6, 70).loadImage("texture/monsters/rat.png", Palette.YELLOW, Palette.GREEN).difficulty(16).resist(Combat.PHYS, 20).resist(Combat.POIS, 60).spell("Spit", 40, Combat.POIS),
			new Monster("Hirsute", 17, 12, 14, 18, 12, 70).loadImage("texture/monsters/rat.png", Palette.YELLOW, Palette.BLUE).difficulty(22).resist(Combat.PHYS, 20).resist(Combat.ICE, 60).spell("Frost", 60, Combat.ICE),
			new Monster("Lurker", 12, 16, 25, 18, 0, 90).loadImage("texture/monsters/rat.png", Palette.YELLOW, Palette.PURPLE).difficulty(31).resist(Combat.PHYS, 30).resist(Combat.FIRE, 20).resist(Combat.ICE, 20).swings(2),

			new Monster("Mimic", 20, 20, 8, 15, 10, 120).loadImage("texture/monsters/mimic.png", 0, 0).difficulty(19).resist(Combat.FIRE, 30).resist(Combat.ELEC, 60).spell("Blast", 20, Combat.FIRE),
			new Monster("Fridge", 24, 24, 11, 15, 13, 130).loadImage("texture/monsters/mimic.png", Palette.ORANGE, Palette.BLUE).difficulty(28).resist(Combat.PHYS, 30).resist(Combat.ICE, 60).spell("Chill", 20, Combat.ICE),
			new Monster("Fusebox", 25, 26, 12, 15, 14, 140).loadImage("texture/monsters/mimic.png", Palette.ORANGE, Palette.YELLOW).difficulty(34).resist(Combat.PHYS, 30).resist(Combat.ICE, 60).spell("Short", 40, Combat.ELEC),
			new Monster("Barbecue", 26, 27, 13, 15, 15, 150).loadImage("texture/monsters/mimic.png", Palette.ORANGE, Palette.RED).difficulty(37).resist(Combat.PHYS, 30).resist(Combat.POIS, 80).spell("Char", 40, Combat.FIRE),
			new Monster("Dumpster", 28, 28, 14, 15, 20, 160).loadImage("texture/monsters/mimic.png", Palette.ORANGE, Palette.GREEN).difficulty(46).resist(Combat.PHYS, 30).resist(Combat.POIS, 80).spell("Rot", 60, Combat.POIS),

			new Monster("Gazer", 14, 30, 20, 10, 15, 150).loadImage("texture/monsters/gazer.png", 0, 0).difficulty(40).resist(Combat.DEATH, 60).resist(Combat.PHYS, 40).spell("Death Ray", 70, Combat.DEATH).swings(3),
			new Monster("Observer", 14, 30, 25, 10, 20, 150).loadImage("texture/monsters/gazer.png", Palette.PURPLE, Palette.RED).difficulty(43).resist(Combat.DEATH, 30).resist(Combat.FIRE, 60).resist(Combat.PHYS, 40).spell("Heat Ray", 70, Combat.FIRE).swings(3),
			new Monster("Watcher", 14, 35, 20, 10, 20, 150).loadImage("texture/monsters/gazer.png", Palette.PURPLE, Palette.BLUE).difficulty(49).resist(Combat.DEATH, 30).resist(Combat.ICE, 40).spell("Ice Ray", 70, Combat.ICE).swings(3),
			new Monster("Beholder", 19, 30, 20, 10, 25, 150).loadImage("texture/monsters/gazer.png", Palette.PURPLE, Palette.GREEN).difficulty(52).resist(Combat.DEATH, 30).resist(Combat.POIS, 40).spell("Acid Ray", 70, Combat.POIS).swings(3),
	};
	
	public static Monster[] SHADOW_MONSTERS = {
			new Monster("Shadow", 30, 20, 40, 30, 30, 200).shadow(true).loadImage("texture/monsters/darkimp.png", 0, 0).spell("Death Touch", 60, Combat.DEATH).statDamage(10).resist(Combat.DEATH, 40).resist(Combat.FIRE, 50),
			new Monster("Shadow", 40, 30, 25, 30, 30, 200).shadow(true).loadImage("texture/monsters/darkrat.png", 0, 0).spell("Death Touch", 60, Combat.DEATH).statDamage(10).resist(Combat.DEATH, 40).resist(Combat.POIS, 50),
			new Monster("Shadow", 50, 25, 20, 25, 30, 200).shadow(true).loadImage("texture/monsters/darkmimic.png", 0, 0).spell("Death Touch", 60, Combat.DEATH).statDamage(10).resist(Combat.DEATH, 40).resist(Combat.PHYS, 50),
			new Monster("Shadow", 30, 25, 30, 25, 40, 200).shadow(true).loadImage("texture/monsters/darkgazer.png", 0, 0).spell("Death Ray", 80, Combat.DEATH).statDamage(10).resist(Combat.DEATH, 40).resist(Combat.ELEC, 50),
	};
	
	public static Monster chooseMonster(int gold) {
		if(gold <= 0) {
			return MONSTERS[0];
		} else if(gold < Player.MAX_GOLD) {
			int totalDifficulty = 0;
			for(Monster m : MONSTERS) {
				if(m.difficulty <= gold) {
					totalDifficulty += (m.difficulty + 10);
				}
			}
			float selectedDifficulty = (float) (Math.random() * totalDifficulty);
			for(Monster m : MONSTERS) {
				if(m.difficulty <= gold) {
					selectedDifficulty -= (m.difficulty + 10);
					if(selectedDifficulty <= 0) {
						return m;
					}
				}
			}
			System.err.println("Monster selection problems");
			return MONSTERS[0];
		} else {
			return SHADOW_MONSTERS[(int) (Math.random() * SHADOW_MONSTERS.length)];
		}
	}
}
