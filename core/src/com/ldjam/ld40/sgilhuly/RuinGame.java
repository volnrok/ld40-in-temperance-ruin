package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RuinGame extends ApplicationAdapter {

	public static final int TOWN_TEXT_X = 8;
	public static final int TOWN_TEXT_Y = 55 + Constants.TEXT_OFFSET;
	public static final int TOWN_LABEL_X = 47;
	public static final int TOWN_LABEL_Y = 65 + Constants.TEXT_OFFSET;
	public static final int TOWN_ARROWS_X = 8;
	public static final int TOWN_ARROWS_Y = 62;
	public static final int TOWN_ITEM_X = 23;
	public static final int TOWN_ITEM_Y = 64;
	
	enum EventType {
		ITEM, LEVEL, HEAL, WIN, NAME
	}
	
	class TownEvent {
		EventType type;
		int selection;
		public Item[] items;
		
		public TownEvent(EventType type) {
			this.type = type;
			selection = 0;
		}
		
		public void addItems(Item[] items) {
			this.items = items;
		}
	}
	
	public static final float VIRTUAL_WIDTH = 160;
	public static final float VIRTUAL_HEIGHT = 90;
	
	OrthographicCamera camera;
	Viewport viewport;
	
	public BitmapFont font[];
	Texture fontTexture;
	
	SpriteBatch batch;
	
	MapDrawer[] drawers;
	Player player;
	Texture ui;
	Texture portrait;
	Texture townBackground;
	Texture townUI;
	Texture arrows;
	Texture noArrows;
	public Texture healthBar;
	TextureRegion healthBarFill;
	float camShake = 0;
	int colour = 0;
	
	public Queue<TownEvent> townEvents = new Queue<TownEvent>();
	
	public TextureRegion combatHealthBarFill;
	public Texture combatUI;
	
	@Override
	public void create () {
		GameContext.game = this;
		
		fontTexture = new Texture("texture/font-coloured.png");
		font = new BitmapFont[7];
		for(int i = 0; i < 7; i++) {
			TextureRegion region = new TextureRegion(fontTexture, 0.125f * i, 0f, 0.125f * (i + 1), 1f);
			font[i] = new BitmapFont(new FileHandle("texture/font.fnt"), region);
		}
		
		batch = new SpriteBatch();
		
		camera = new OrthographicCamera();
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		viewport.apply();
		
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		
		drawers = new MapDrawer[7];
		// Need grey to be 0 so it comes first
		for(int i = 0; i < 7; i++) {
			drawers[i] = new MapDrawer(i);
		}
		townEvents.addLast(new TownEvent(EventType.NAME));
		townEvents.addLast(new TownEvent(EventType.LEVEL));
		ui = new Texture("texture/ui.png");
		portrait = new Texture("texture/portrait.png");
		townBackground = new Texture("texture/townBackground.png");
		townUI = new Texture("texture/townUI.png");
		arrows = new Texture("texture/arrows.png");
		noArrows = new Texture("texture/noArrows.png");
		healthBar = new Texture("texture/healthBar.png");
		healthBarFill = new TextureRegion(healthBar);
		combatHealthBarFill = new TextureRegion(healthBar);
		combatUI = new Texture("texture/combatUI.png");
		player = new Player();
	}

	@Override
	public void render () {
		camera.update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(GameContext.combat != null) {
			if(GameContext.metronome.isEmpty()) {
				if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
					GameContext.combat.playerAction(Combat.SWING);
				} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
					GameContext.combat.playerAction(Combat.SPELL);
				} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
					GameContext.combat.playerAction(Combat.FLEE);
				}
			}
		} else if(GameContext.transition == null) {
			if(GameContext.currentMap == null) {
				if(townEvents.size == 0) {
					GameContext.transition = new Transition(1, false);
				} else {
					TownEvent event = townEvents.first();
					switch(event.type) {
					case ITEM:
						if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
							event.selection--;
						} else if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
							event.selection++;
						}
						event.selection = Helper.mod(event.selection, event.items.length);
						if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
							player.equip(event.items[event.selection]);
							player.recalcStats();
							townEvents.removeFirst();
						}
						break;
					case LEVEL:
						if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
							player.str++;
							player.lastGold++;
							player.recalcStats();
						} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
							player.per++;
							player.lastGold++;
							player.recalcStats();
						} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
							player.spd++;
							player.lastGold++;
							player.recalcStats();
						} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
							player.agi++;
							player.lastGold++;
							player.recalcStats();
						} else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
							player.foc++;
							player.lastGold++;
							player.recalcStats();
						}
						if(player.gold <= player.lastGold) {
							townEvents.removeFirst();
						}
						break;
					case HEAL:
						player.heal();
					case NAME:
						if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
							townEvents.removeFirst();
						}
						break;
					case WIN:
						if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
							//System.exit(0);
							Gdx.app.exit();
						}
						break;
					default:
						break;
					}
				}
			} else {
				if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
					player.turn(-1);
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
					player.turn(1);
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
					player.step(1);
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
					player.step(-1);
				}
				if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
					/*GameContext.metronome.queueEvent("The dungeon is quiet.", (int) (Math.random() * 7), new Runnable() {
						@Override
						public void run() {
							shakeScreen();
						}
					});*/
					//new Combat(player, Monster.chooseMonster(player.gold));
					//player.gold += 10;
				}
			}
		} 
		
		camShake *= 0.9f;
		GameContext.metronome.update(Gdx.graphics.getDeltaTime());
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		if(GameContext.currentMap != null) {
			drawers[GameContext.currentMap.palette].drawMap(player.posX, player.posY, player.posDir, GameContext.currentMap, batch, camShake);
		} else {
			drawTownUI();
		}
		
		// Draw combat?
		if(GameContext.combat != null) {
			GameContext.combat.updateAndDraw(Gdx.graphics.getDeltaTime(), batch);
		}
		
		if(GameContext.transition != null) {
			GameContext.transition.updateAndDraw(Gdx.graphics.getDeltaTime(), batch);
		}
		
		drawUI();
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		ui.dispose();
		// TODO cleaning up, probs doesn't matter now
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	public void shakeScreen() {
		camShake = 4.4f;
	}
	
	public void drawTownUI() {
		
		batch.draw(townBackground, 0, 0);
		if(GameContext.transition == null) {
			batch.draw(townUI, 0, 0);
			if(townEvents.size > 0) {
				TownEvent event = townEvents.first();
				switch(event.type) {
				case HEAL:
					font[Palette.GREY].draw(batch, "You are fully healed.", TOWN_TEXT_X, TOWN_TEXT_Y);
					break;
				case ITEM:
					if(event.items.length > 1) {
						batch.draw(arrows, TOWN_ARROWS_X, TOWN_ARROWS_Y);
					} else {
						batch.draw(noArrows, TOWN_ARROWS_X, TOWN_ARROWS_Y);
					}
					batch.draw(event.items[event.selection].icon, TOWN_ITEM_X, TOWN_ITEM_Y);
					font[Palette.GREEN].draw(batch, event.items[event.selection].name, TOWN_LABEL_X, TOWN_LABEL_Y);
					font[Palette.GREEN].draw(batch, event.items[event.selection].text, TOWN_TEXT_X, TOWN_TEXT_Y);
					break;
				case LEVEL:
					font[Palette.BLUE].draw(batch, (player.gold - player.lastGold) + " stat points\n\nPress 1 - 5 to spend", TOWN_TEXT_X, TOWN_TEXT_Y);
					break;
				case NAME:
					font[Palette.GREEN].draw(batch, "You are named Billy.\n\nPress ENTER to\nbegin your journey...", TOWN_TEXT_X, TOWN_TEXT_Y);
					break;
				case WIN:
					font[Palette.ORANGE].draw(batch, "You have won!\n\nThanks for playing.\n\nPress ENTER to exit", TOWN_TEXT_X, TOWN_TEXT_Y);
					break;
				}
			}
		}
	}
	
	public void drawNumber(BitmapFont font, SpriteBatch batch, int num, int width, int x, int y) {
		String s = Integer.toString(num);
		font.draw(batch, s, x + (width - s.length()) * 5, y);
	}
	
	public void drawUI() {
		// TODO Deal with hardcoded ui element locations? nah
		batch.draw(ui, 0, 0);
		batch.draw(portrait, 122, 63);
		healthBarFill.setRegionY(32 - (int) (healthBar.getHeight() * player.hp * 1.0f / player.hpMax));
		healthBarFill.setRegionHeight((int) (healthBar.getHeight() * player.hp * 1.0f / player.hpMax));
		batch.draw(healthBarFill, 143, 47);
		// Metronome
		font[GameContext.metronome.displayPalette].draw(batch, GameContext.metronome.displayText, 1, 83 + Constants.TEXT_OFFSET);
		// Name
		font[Palette.GREEN].draw(batch, player.name, 122, 82 + Constants.TEXT_OFFSET);
		// Gold
		drawNumber(font[Palette.YELLOW], batch, player.gold, 2, 131, 55 + Constants.TEXT_OFFSET);
		// Compass
		font[Palette.GREEN].draw(batch, Map.DIR_LETTERS[player.posDir], 129, 47 + Constants.TEXT_OFFSET);
		// Items
		batch.draw(player.weapon.icon, 150, 80);
		batch.draw(player.armour.icon, 150, 69);
		batch.draw(player.wand.icon, 150, 58);
		batch.draw(player.ring.icon, 150, 47);
		// Stats
		font[Palette.GREEN].draw(batch, "str", 122, 34 + Constants.TEXT_OFFSET);
		font[Palette.GREEN].draw(batch, "per", 122, 26 + Constants.TEXT_OFFSET);
		font[Palette.GREEN].draw(batch, "spd", 122, 18 + Constants.TEXT_OFFSET);
		font[Palette.GREEN].draw(batch, "agi", 122, 10 + Constants.TEXT_OFFSET);
		font[Palette.GREEN].draw(batch, "foc", 122, 2 + Constants.TEXT_OFFSET);

		drawNumber(font[player.strCalc < player.str ? Palette.RED : Palette.GREEN], batch, player.strCalc, 3, 144, 34 + Constants.TEXT_OFFSET);
		drawNumber(font[player.perCalc < player.per ? Palette.RED : Palette.GREEN], batch, player.perCalc, 3, 144, 26 + Constants.TEXT_OFFSET);
		drawNumber(font[player.spdCalc < player.spd ? Palette.RED : Palette.GREEN], batch, player.spdCalc, 3, 144, 18 + Constants.TEXT_OFFSET);
		drawNumber(font[player.agiCalc < player.agi ? Palette.RED : Palette.GREEN], batch, player.agiCalc, 3, 144, 10 + Constants.TEXT_OFFSET);
		drawNumber(font[player.focCalc < player.foc ? Palette.RED : Palette.GREEN], batch, player.focCalc, 3, 144, 2 + Constants.TEXT_OFFSET);
	}
	
	public void visitTown() {
		
		if(player.gold >= Player.MAX_GOLD) {
			townEvents.addLast(new TownEvent(EventType.WIN));
		} else {
			townEvents.addLast(new TownEvent(EventType.HEAL));
			
			// Check for new rings
			for(int i = 1; i <= 5; i++) {
				if(player.gold >= i * 10 - 5 && player.lastGold < i * 10 - 5) {
					TownEvent event = new TownEvent(EventType.ITEM);
					event.addItems(new Item[] {Item.ITEMS[3][i]});
					townEvents.addLast(event);
				}
			}
			
			// Check for new items
			for(int i = 1; i <= 10; i++) {
				if(player.gold >= i * 10 && player.lastGold < i * 10) {
					TownEvent event = new TownEvent(EventType.ITEM);
					event.addItems(new Item[] {
							Item.ITEMS[0][Math.min(i, 5)],
							Item.ITEMS[1][Math.min(i, 5)],
							Item.ITEMS[2][Math.min(i, 5)],
							Item.ITEMS[4][Math.min(i, 5)]
					});
					townEvents.addLast(event);
				}
			}
			
			// Check for level up
			if(player.gold > player.lastGold) {
				townEvents.addLast(new TownEvent(EventType.LEVEL));
			}
		}
	}
}
