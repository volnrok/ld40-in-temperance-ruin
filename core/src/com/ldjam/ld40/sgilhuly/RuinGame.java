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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RuinGame extends ApplicationAdapter {
	
	public static final float VIRTUAL_WIDTH = 160;
	public static final float VIRTUAL_HEIGHT = 90;
	
	OrthographicCamera camera;
	Viewport viewport;
	
	BitmapFont font[];
	Texture fontTexture;
	
	SpriteBatch batch;
	
	Map map;
	MapDrawer drawer;
	Player player;
	Texture ui;
	Texture portrait;
	Texture healthBar;
	TextureRegion healthBarFill;
	float camShake = 0;
	
	@Override
	public void create () {
		GameContext.game = this;
		
		fontTexture = new Texture("font-coloured.png");
		font = new BitmapFont[7];
		for(int i = 0; i < 7; i++) {
			TextureRegion region = new TextureRegion(fontTexture, 0.125f * i, 0f, 0.125f * (i + 1), 1f);
			font[i] = new BitmapFont(new FileHandle("font.fnt"), region);
		}
		
		batch = new SpriteBatch();
		
		camera = new OrthographicCamera();
		viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
		viewport.apply();
		
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		
		map = Map.MAP_1;
		drawer = new MapDrawer(0);
		ui = new Texture("ui.png");
		portrait = new Texture("portrait.png");
		healthBar = new Texture("healthBar.png");
		healthBarFill = new TextureRegion(healthBar);
		player = new Player();
	}

	@Override
	public void render () {
		camera.update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
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
			player.getWounded();
		}
		
		camShake *= 0.9f;
		GameContext.metronome.update(Gdx.graphics.getDeltaTime());
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		drawer.drawMap(player.posX, player.posY, player.posDir, map, batch, camShake);
		
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
	
	public void drawUI() {
		// TODO Deal with hardcoded ui element locations? nah
		batch.draw(ui, 0, 0);
		batch.draw(portrait, 122, 63);
		healthBarFill.setRegionHeight((int) (healthBar.getHeight() * player.hp * 1.0f / player.hpMax));
		batch.draw(healthBarFill, 143, 47);
		// Metronome
		font[GameContext.metronome.displayPalette].draw(batch, GameContext.metronome.displayText, 1, 83 + Constants.TEXT_OFFSET);
		// Name
		font[Palette.GREEN].draw(batch, player.name, 122, 82 + Constants.TEXT_OFFSET);
		// Gold
		font[Palette.YELLOW].draw(batch, String.format("%2d", player.gold), 131, 55 + Constants.TEXT_OFFSET);
		// Compass
		font[Palette.GREEN].draw(batch, Map.DIR_LETTERS[player.posDir], 129, 47 + Constants.TEXT_OFFSET);
		// Stats
		font[Palette.GREEN].draw(batch, "str", 122, 34 + Constants.TEXT_OFFSET);
		font[Palette.GREEN].draw(batch, "per", 122, 26 + Constants.TEXT_OFFSET);
		font[Palette.GREEN].draw(batch, "spd", 122, 18 + Constants.TEXT_OFFSET);
		font[Palette.GREEN].draw(batch, "agi", 122, 10 + Constants.TEXT_OFFSET);
		font[Palette.GREEN].draw(batch, "foc", 122, 2 + Constants.TEXT_OFFSET);

		font[player.strCalc < player.str ? Palette.RED : Palette.GREEN].draw(batch, String.format("%3d", player.strCalc), 144, 34 + Constants.TEXT_OFFSET);
		font[player.perCalc < player.per ? Palette.RED : Palette.GREEN].draw(batch, String.format("%3d", player.perCalc), 144, 26 + Constants.TEXT_OFFSET);
		font[player.spdCalc < player.spd ? Palette.RED : Palette.GREEN].draw(batch, String.format("%3d", player.spdCalc), 144, 18 + Constants.TEXT_OFFSET);
		font[player.agiCalc < player.agi ? Palette.RED : Palette.GREEN].draw(batch, String.format("%3d", player.agiCalc), 144, 10 + Constants.TEXT_OFFSET);
		font[player.focCalc < player.foc ? Palette.RED : Palette.GREEN].draw(batch, String.format("%3d", player.focCalc), 144, 2 + Constants.TEXT_OFFSET);
	}
}
