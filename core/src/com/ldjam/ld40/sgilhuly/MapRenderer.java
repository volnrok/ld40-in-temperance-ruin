package com.ldjam.ld40.sgilhuly;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;

public class MapRenderer {

	public static final int SHAKE_AMOUNT = 4;
	public static final int VIEW_WIDTH = 120 + SHAKE_AMOUNT * 2;
	public static final int VIEW_HEIGHT = 82 + SHAKE_AMOUNT * 2;
	public static final int FOV = 65;
	
	public PerspectiveCamera cam;
	public ModelBatch batch;
	public Model cube;
	public ArrayList<ModelInstance> dungeon;
	public FrameBuffer fbo;
	public Map map;
	public Material wallMaterial;
	public Material testMaterial;
	public Environment environment;
	public Texture background;

	/*public Vector3 playerPosition = new Vector3();
	public Vector3 playerFacing = new Vector3();
	public Vector3 temp = new Vector3();
	public float playerDir = 0;*/
	
	public MapRenderer(Map map) {
		fbo = FrameBuffer.createFrameBuffer(Pixmap.Format.RGBA8888, VIEW_WIDTH, VIEW_HEIGHT, true);
		fbo.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
		
		wallMaterial = new Material(TextureAttribute.createDiffuse(Palette.loadSwapped("texture/walls/3d/bricks.png", Palette.GREY, map.palette)));
		testMaterial = new Material(ColorAttribute.createDiffuse(Color.GREEN));
		
		cam = new PerspectiveCamera(FOV, VIEW_WIDTH, VIEW_HEIGHT);
		cam.near = 0.1f;
		cam.far = 3.1f;
		batch = new ModelBatch();
		ModelBuilder builder = new ModelBuilder();
		cube = builder.createBox(1, 1, 1, wallMaterial, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		background = Palette.loadSwapped("texture/walls/background.png", Palette.GREY, map.palette);
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		
		this.map = map;
		dungeon = new ArrayList<ModelInstance>();
		for(int y = 0; y < Map.MAP_HEIGHT; y++) {
			for(int x = 0; x < Map.MAP_WIDTH; x++) {
				switch(map.accessMap(x, y)) {
				case Map.WALL:
					ModelInstance instance = new ModelInstance(cube);
					instance.transform.setToTranslation(new Vector3(x, 0, y));
					dungeon.add(instance);
				}
			}
		}
	}
	
	public void renderFBO() {
		//playerPosition.set(GameContext.player.posX, 0, GameContext.player.posY);
		//playerFacing.set(GameContext.player.basis.forwardX, 0, GameContext.player.basis.forwardY);
		
		/*cam.position.set(playerFacing).scl(-0.5f).add(playerPosition);
		temp.set(playerPosition).add(playerFacing);
		cam.lookAt(temp);*/
		cam.update();
		
		fbo.begin();
		Gdx.gl.glViewport(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		//Gdx.gl.glClearDepthf(0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		batch.begin(cam);
		batch.render(dungeon, environment);
		batch.end();
		
		fbo.end();
	}
	
	public void drawToScreen(SpriteBatch spriteBatch, float cameraShake) {
		
		int shakeX = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		int shakeY = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		spriteBatch.draw(background, -SHAKE_AMOUNT + shakeX, -SHAKE_AMOUNT + shakeY);
		
		//spriteBatch.enableBlending();
		//spriteBatch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_SRC_ALPHA);
		
		shakeX = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		shakeY = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		spriteBatch.draw(fbo.getColorBufferTexture(), -SHAKE_AMOUNT + shakeX, -SHAKE_AMOUNT + shakeY, VIEW_WIDTH, VIEW_HEIGHT, 0, 0, 1, 1);
		
		//spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

}
