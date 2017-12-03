package com.ldjam.ld40.sgilhuly;

import java.util.Random;

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
			GameContext.metronome.queueEvent("You run into a wall.", (int) (Math.random() * 7), new Runnable() {
				@Override
				public void run() {
					shakeScreen();
				}
			});
		}
		
		camShake *= 0.9f;
		GameContext.metronome.update(Gdx.graphics.getDeltaTime());
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		drawer.drawMap(player.posX, player.posY, player.posDir, map, batch, camShake);
		
		// Draw UI
		batch.draw(ui, 0, 0);
		font[GameContext.metronome.displayPalette].draw(batch, GameContext.metronome.displayText, 1, 70);
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
}
