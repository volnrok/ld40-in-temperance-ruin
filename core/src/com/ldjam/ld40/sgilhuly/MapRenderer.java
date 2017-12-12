package com.ldjam.ld40.sgilhuly;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;

public class MapRenderer {
	
	// I have tuned deco objects so they display at native scaling when one space away at 60x60 image size
	// at FOV 82, and coordinates -0.6f,-0.6f, 0.25f,  0.6f,-0.6f, 0.25f,  0.6f, 0.6f, 0.25f, -0.6f, 0.6f, 0.25f

	public static final int SHAKE_AMOUNT = 4;
	public static final int VIEW_WIDTH = 120 + SHAKE_AMOUNT * 2;
	public static final int VIEW_HEIGHT = 82 + SHAKE_AMOUNT * 2;
	public static final int FOV = 82;
	public static final float OFFSET = 0.3f;

	public static final float MOVE_SPEED = 5;
	public static final float TURN_SPEED = 450;
	
	public PerspectiveCamera cam;
	public ModelBatch batch;
	public Model cube;
	public Model billboard;
	public ArrayList<ModelInstance> dungeon;
	public ArrayList<ModelInstance> deco;
	public ModelInstance[][] decoData;
	public FrameBuffer fbo;
	public Map map;
	public Texture background;
	
	public Texture stairsUp;
	public Texture stairsDown;
	public Texture pedestal;
	public Texture emptyPedestal;
	public Texture basin;
	public Texture emptyBasin;
	public Texture hoard;

	public Vector3 playerPosition = new Vector3();
	public Vector3 playerPositionTarget = new Vector3();
	public Vector3 playerFacing = new Vector3();
	public float playerDir = 0;
	public float playerDirTarget = 0;
	
	public MapRenderer(Map map) {
		fbo = FrameBuffer.createFrameBuffer(Pixmap.Format.RGBA8888, VIEW_WIDTH, VIEW_HEIGHT, true);
		fbo.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		Material wallMaterial = new Material(TextureAttribute.createDiffuse(Palette.loadSwapped("texture/walls/3d/bricks.png", Palette.GREY, map.palette)));
		wallMaterial.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
		Texture wallCracked = Palette.loadSwapped("texture/walls/3d/bricksCracked.png", Palette.GREY, map.palette);
		Texture wallGrate = Palette.loadSwapped("texture/walls/3d/bricksGrate.png", Palette.GREY, map.palette);
		Texture wallMosaic = Palette.loadSwapped("texture/walls/3d/bricksMosaic.png", Palette.GREY, map.palette);

		stairsUp = Palette.loadSwapped("texture/deco/stairsUp.png", Palette.GREY, map.palette);
		stairsDown = Palette.loadSwapped("texture/deco/stairsDown.png", Palette.GREY, map.palette);
		pedestal = Palette.loadSwapped("texture/deco/pedestal.png", Palette.GREY, map.palette);
		emptyPedestal = Palette.loadSwapped("texture/deco/emptyPedestal.png", Palette.GREY, map.palette);
		basin = Palette.loadSwapped("texture/deco/basin.png", Palette.GREY, map.palette);
		emptyBasin = Palette.loadSwapped("texture/deco/emptyBasin.png", Palette.GREY, map.palette);
		hoard = Palette.loadSwapped("texture/deco/hoard.png", Palette.GREY, map.palette);
				
		cam = new PerspectiveCamera(FOV, VIEW_WIDTH, VIEW_HEIGHT);
		cam.near = 0.2f;
		cam.far = 2.7f + OFFSET;
		batch = new ModelBatch();
		background = Palette.loadSwapped("texture/walls/background.png", Palette.GREY, map.palette);
		
		ModelBuilder builder = new ModelBuilder();
		builder.begin();
		MeshPartBuilder meshPartBuilder = builder.part("box", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, wallMaterial);
		// Make a cube, but we don't need the top or bottom
		meshPartBuilder.rect( 0.5f,-0.5f,-0.5f, -0.5f,-0.5f,-0.5f, -0.5f, 0.5f,-0.5f,  0.5f, 0.5f,-0.5f, 0,0,-1);
        meshPartBuilder.rect(-0.5f,-0.5f, 0.5f,  0.5f,-0.5f, 0.5f,  0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0,0,1);
        meshPartBuilder.rect(-0.5f,-0.5f,-0.5f, -0.5f,-0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f,-0.5f, -1,0,0);
        meshPartBuilder.rect( 0.5f,-0.5f, 0.5f,  0.5f,-0.5f,-0.5f,  0.5f, 0.5f,-0.5f,  0.5f, 0.5f, 0.5f, 1,0,0);
        cube = builder.end();
        
        builder.begin();
        meshPartBuilder = builder.part("billboard", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, wallMaterial);
        meshPartBuilder.rect(-0.6f,-0.6f, 0.25f,  0.6f,-0.6f, 0.25f,  0.6f, 0.6f, 0.25f, -0.6f, 0.6f, 0.25f, 0,0,1);
		/*meshPartBuilder.rect( 0.6f,-0.6f,-0.1f, -0.6f,-0.6f,-0.1f, -0.6f, 0.6f,-0.1f,  0.6f, 0.6f,-0.1f, 0,0,-1);
        meshPartBuilder.rect(-0.6f,-0.6f, 0.1f,  0.6f,-0.6f, 0.1f,  0.6f, 0.6f, 0.1f, -0.6f, 0.6f, 0.1f, 0,0,1);
        meshPartBuilder.rect(-0.1f,-0.6f,-0.6f, -0.1f,-0.6f, 0.6f, -0.1f, 0.6f, 0.6f, -0.1f, 0.6f,-0.6f, -1,0,0);
        meshPartBuilder.rect( 0.1f,-0.6f, 0.6f,  0.1f,-0.6f,-0.6f,  0.1f, 0.6f,-0.6f,  0.1f, 0.6f, 0.6f, 1,0,0);*/
        billboard = builder.end();
		
		this.map = map;
		dungeon = new ArrayList<ModelInstance>();
		deco = new ArrayList<ModelInstance>();
		decoData = new ModelInstance[Map.MAP_HEIGHT][Map.MAP_WIDTH];
		for(int y = 0; y < Map.MAP_HEIGHT; y++) {
			for(int x = 0; x < Map.MAP_WIDTH; x++) {
				
				byte b = map.accessMap(x, y);
				
				if(Map.isWall(b)) {
					ModelInstance instance = new ModelInstance(cube);
					instance.transform.setToTranslation(new Vector3(x, 0, y));
					switch(b) {
					case Map.WALL:
						break;
					case Map.WALL_CRACKED:
						setMaterialTexture(instance, wallCracked);
						break;
					case Map.WALL_GRATE:
						setMaterialTexture(instance, wallGrate);
						break;
					case Map.WALL_MOSAIC:
						setMaterialTexture(instance, wallMosaic);
						break;
					}
					dungeon.add(instance);
				} else if(Map.isDeco(b)) {
					ModelInstance instance = new ModelInstance(billboard);
					instance.transform.setToTranslation(new Vector3(x, 0, y));
					deco.add(instance);
					decoData[y][x] = instance;
				}
			}
		}
		
		refreshDecos();
	}
	
	public static void setMaterialTexture(ModelInstance instance, Texture texture) {
		for (Iterator<Attribute> ai = instance.materials.get(0).iterator(); ai.hasNext();){
	        Attribute att = ai.next();                        
	        if (att.type == TextureAttribute.Diffuse) {
	            ((TextureAttribute) att).textureDescription.set(texture, TextureFilter.Nearest, TextureFilter.Nearest, TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
	        }
	    }
	}
	
	public void renderFBO() {
		
		float t = Gdx.graphics.getDeltaTime();
		playerPosition.set(Helper.moveTo(playerPosition.x, playerPositionTarget.x, MOVE_SPEED * t), 0, Helper.moveTo(playerPosition.z, playerPositionTarget.z, MOVE_SPEED * t));
		playerDir = Helper.moveTo(playerDir, playerDirTarget, TURN_SPEED * t);
		playerFacing.set(0, 0, -1).rotate(Vector3.Y, -playerDir);
		
		cam.position.set(playerFacing).scl(-OFFSET).add(playerPosition);
		cam.direction.set(playerFacing);
		cam.update();
		
		for(int y = 0; y < Map.MAP_HEIGHT; y++) {
			for(int x = 0; x < Map.MAP_WIDTH; x++) {
				ModelInstance instance = decoData[y][x];
				if(instance != null) {
					//instance.transform.setToRotation(Vector3.Y, playerDir).translate(x, 0, y);
					instance.transform.setToTranslation(x, 0, y).rotate(Vector3.Y, -playerDir);
				}
			}
		}
		
		fbo.begin();
		Gdx.gl.glViewport(0, 0, VIEW_WIDTH, VIEW_HEIGHT);
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND);
	    /*Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	    Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);
	    Gdx.gl.glBlendEquation(GL20.GL_BLEND);*/
		
		batch.begin(cam);
		batch.render(dungeon);
		batch.render(deco);
		batch.end();
		
		fbo.end();
	}
	
	public void drawToScreen(SpriteBatch spriteBatch, float cameraShake) {
		
		int shakeX = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		int shakeY = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		spriteBatch.draw(background, -SHAKE_AMOUNT + shakeX, -SHAKE_AMOUNT + shakeY);
		
		shakeX = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		shakeY = (int) Math.round(Math.random() * cameraShake * 2 - cameraShake);
		spriteBatch.draw(fbo.getColorBufferTexture(), -SHAKE_AMOUNT + shakeX, -SHAKE_AMOUNT + shakeY, VIEW_WIDTH, VIEW_HEIGHT, 0, 0, 1, 1);
	}
	
	public void setPositionAndRotation(int x, int y, int dir) {
		playerPosition.set(x, 0, y);
		playerPositionTarget.set(playerPosition);
		playerDir = dir * 90;
		playerDirTarget = playerDir;
		
		System.out.println(String.format("Player is at %d %d facing %f", x, y, playerDir));
	}
	
	public void playerStepTo(int x, int y) {
		playerPosition.set(playerPositionTarget);
		playerDir = playerDirTarget;
		
		playerPositionTarget.set(x, 0, y);
		
		System.out.println(String.format("Player is at %d %d", x, y));
	}
	
	public void playerFace(int dir) {
		playerPosition.set(playerPositionTarget);
		playerDir = playerDirTarget;

		playerDirTarget = dir * 90;
		if(playerDirTarget - playerDir > 180) {
			playerDir += 360;
		} else if(playerDirTarget - playerDir < -180) {
			playerDir -= 360;
		}
		
		System.out.println(String.format("Player is facing %f", playerDir));
	}
	
	public void refreshDecos() {
		for(int y = 0; y < Map.MAP_HEIGHT; y++) {
			for(int x = 0; x < Map.MAP_WIDTH; x++) {
				refreshDeco(x, y);
			}
		}
	}
	
	public void refreshDeco(int x, int y) {
		ModelInstance instance = decoData[y][x];
		if(instance != null) {
			byte b = map.accessMap(x, y);
			switch(b) {
			case Map.STAIRS_UP:
				setMaterialTexture(instance, stairsUp);
				break;
			case Map.STAIRS_DOWN:
				setMaterialTexture(instance, stairsDown);
				break;
			case Map.TREASURE:
				setMaterialTexture(instance, pedestal);
				break;
			case Map.TREASURE_USED:
				setMaterialTexture(instance, emptyPedestal);
				break;
			case Map.BASIN:
				setMaterialTexture(instance, basin);
				break;
			case Map.BASIN_USED:
				setMaterialTexture(instance, emptyBasin);
				break;
			case Map.HOARD:
				setMaterialTexture(instance, hoard);
				break;
			}
		}
	}
}
