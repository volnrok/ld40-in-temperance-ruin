package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ldjam.ld40.sgilhuly.Map.BossFight;

public class Combat {
	
	public static final int SWING = 0;
	public static final int SPELL = 1;
	public static final int FLEE = 2;

	public static final int PHYS = 0;
	public static final int FIRE = 1;
	public static final int ICE = 2;
	public static final int ELEC = 3;
	public static final int POIS = 4;
	public static final int DEATH = 5;
	
	public static final String[] ELEMENT_NAMES = {
			"physical",
			"fire",
			"ice",
			"electric",
			"poison",
			"death"
	};
	
	public static final int[] ELEMENT_PALETTES = {
			Palette.GREY,
			Palette.RED,
			Palette.BLUE,
			Palette.YELLOW,
			Palette.GREEN,
			Palette.PURPLE
	};
	
	public Player player;
	public Monster monster;
	public Metronome metronome;
	
	public Vector2 startPos = new Vector2(-50, 35);
	public Vector2 endPos = new Vector2(37, 35);
	float lerp = 0;
	float shake = 0;
	int fleeBonus = 0;
	
	public Combat(Player player, Monster monster) {
		this.player = player;
		this.monster = monster;
		monster.hp = monster.hpMax;
		metronome = GameContext.metronome;
		GameContext.combat = this;
		
		monster.recalcStats();
		player.recalcStats();
		
		metronome.queueEvent(monster.name + " attacks!", Palette.PURPLE, new Runnable() {
			@Override
			public void run() {
				startCombat();
			}
		});
	}
	
	public Combat(Player player, BossFight bossFight) {
		this(player, bossFight.monster);
		bossFight.encountered = true;
		if(bossFight.message != null) {
			if(bossFight.shakingText) {
				metronome.shaking = true;
			}
			metronome.queueEvent(bossFight.message, bossFight.palette);
		}
	}
	
	public void updateAndDraw(float delta, SpriteBatch batch) {
		
		shake *= 0.9f;
		int shakeX = (int) Math.round(Math.random() * shake * 2 - shake);
		int shakeY = (int) Math.round(Math.random() * shake * 2 - shake);
		
		lerp = 1 - (1 - lerp) * 0.95f;
		batch.draw(monster.image, (int) Helper.lerp(lerp, startPos.x, endPos.x) + shakeX, (int) Helper.lerp(lerp, startPos.y, endPos.y) + shakeY);
		batch.draw(GameContext.game.combatUI, 0, 0);
		batch.draw(player.weapon.icon, 10, 24);
		batch.draw(player.wand.icon, 10, 13);

		GameContext.game.font[metronome.isEmpty() ? Palette.BLUE : Palette.RED].draw(batch, "1 " + player.weapon.name, 21, 25 + Constants.TEXT_OFFSET);
		GameContext.game.font[metronome.isEmpty() ? Palette.BLUE : Palette.RED].draw(batch, String.format("2 %s %d/%d", player.wand.name, player.spells, player.spellsMax), 21, 14 + Constants.TEXT_OFFSET);
		GameContext.game.font[metronome.isEmpty() ? Palette.BLUE : Palette.RED].draw(batch, "3 Flee", 21, 3 + Constants.TEXT_OFFSET);
		
		GameContext.game.setHpRegion(GameContext.game.combatHealthBarFill, monster);
		batch.draw(GameContext.game.combatHealthBarFill, 86, 42);
	}
	
	public void startCombat() {
		// Does player get a spell back?
		if(Math.random() * 50 < player.spellsMax && player.spells < player.spellsMax) {
			metronome.queueEvent("Wand recharges!", Palette.PURPLE, new Runnable() {
				@Override
				public void run() {
					player.spells = Math.min(player.spells + (int) (Helper.rand(1, 3)), player.spellsMax);
				}
			});
		}
		// Initiative check - 3% per perception
		if(Math.random() < player.perCalc / 33.3f) {
			metronome.queueEvent("Free attack!", Palette.BLUE);
			swing(player, monster);
		}
		
		if(monster.sound != null) {
			GameContext.audio.playSound(monster.sound);
		}
	}
	
	public void swing(Creature a, final Creature b) {
		int numSwings = a.numSwings(b);
		metronome.queueEvent(a.swingText() + (numSwings == 1 ? "!" : " " + numSwings + " times!"), Palette.GREY);
		
		for(int i = 0; i < numSwings; i++) {
			if(a.didHit(b)) {
				float damage = a.swingDamage();
				final int totalDamage = (int) b.resistDamage(damage, PHYS);
				metronome.queueEvent(String.format("Hit for %d! <%d>", totalDamage, (int) damage), Palette.GREY, new Runnable() {
					@Override
					public void run() {
						b.takeDamage(totalDamage);
						GameContext.audio.playHit();
					}
				});
			} else {
				metronome.queueEvent("Miss!", Palette.PURPLE, new Runnable() {
					@Override
					public void run() {
						GameContext.audio.playSound(GameContext.audio.dodge);
					}
				});
			}
		}
	}
	
	public void spell(final Creature a, final Creature b) {
		metronome.queueEvent(a.spellText(), Palette.GREY);
		float damage = a.spellDamage();
		final int totalDamage = (int) b.resistDamage(damage, a.spellElement());
		metronome.queueEvent(String.format("%d %s! <%d>", totalDamage, ELEMENT_NAMES[a.spellElement()], (int) damage), ELEMENT_PALETTES[a.spellElement()], new Runnable() {
			@Override
			public void run() {
				b.takeDamage(totalDamage);
				GameContext.audio.playSpell(a.spellElement());
			}
		});
	}
	
	public void endCombat(final boolean defeated) {
		
		if(defeated) {
			if(monster.shadow) {
				player.gracePeriod = 4;
			} else {
				player.gracePeriod = 6;
			}
			metronome.shaking = false;
			metronome.clearEvents();
			metronome.queueEvent(monster.shortName + " was defeated!", Palette.BLUE, new Runnable() {
				@Override
				public void run() {
					moveMonster(defeated);
					GameContext.audio.playSound(GameContext.audio.monsterDefeated);
				}
			});
		} else {
			player.gracePeriod = 1;
			metronome.queueEvent("Fled the battle!", Palette.BLUE, new Runnable() {
				@Override
				public void run() {
					moveMonster(defeated);
					GameContext.audio.playSound(GameContext.audio.dodge);
				}
			});
		}
		metronome.queueEvent("", 0, new Runnable() {
			@Override
			public void run() {
				GameContext.combat = null;
			}
		});
	}
	
	public void moveMonster(boolean defeated) {
		startPos = endPos;
		lerp = 0;
		if(defeated) {
			endPos = new Vector2(37, -50);
		} else {
			endPos = new Vector2(-50, 35);
		}
	}
	
	public void playerAction(int action) {
		switch(action) {
		case SWING:
			swing(player, monster);
			break;
		case SPELL:
			if(player.spells > 0) {
				player.spells--;
				spell(player, monster);
			} else {
				GameContext.game.shakeScreen();
				GameContext.audio.playSound(GameContext.audio.error);
				return;
			}
			break;
		}
		
		monster.chooseAction();
		
		if(action == FLEE) {
			int adjusted = player.spdCalc - 20 + fleeBonus;
			if(monster.shadow) {
				metronome.queueEvent("Shadows slow you!", Palette.PURPLE, new Runnable() {
					@Override
					public void run() {
						GameContext.audio.playSound(GameContext.audio.shadowAppears);
					}
				});
				adjusted -= 10;
			}
			System.out.println(String.format("Flee chance is %f", Helper.sigmoid(adjusted, 20)));
			if(Math.random() < Helper.sigmoid(adjusted, 20)) {
				endCombat(false);
			} else {
				fleeBonus += 10;
				metronome.queueEvent("Unable to escape!", Palette.ORANGE, new Runnable() {
					@Override
					public void run() {
						GameContext.game.shakeScreen();
						GameContext.audio.playSound(GameContext.audio.error);
					}
				});
			}
		}
	}
	
	public void monsterAction(int action) {
		switch(action) {
		case SWING:
			swing(monster, player);
			break;
		case SPELL:
			spell(monster, player);
			break;
		}
	}
	
	public void shakeMonster() {
		shake = 5f;
	}
}
