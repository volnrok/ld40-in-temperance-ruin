package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
	
	public static final float FADE_IN_TIME = 0.2f;
	public static final float FADE_OUT_TIME = 0.8f;
	
	public enum FadeMode {
		NONE, FADE_IN, FADE_OUT
	}
	
	public Music dungeonMusic;
	public Music townMusic;
	
	public Sound impAppears;
	public Sound ratmanAppears;
	public Sound mimicAppears;
	public Sound beholderAppears;
	public Sound shadowAppears;
	
	public Sound[] spells;
	public Sound[] steps;
	public Sound[] hits;

	public Sound coin;
	public Sound dodge;
	public Sound error;
	public Sound monsterDefeated;
	public Sound stairs;
	
	private Music activeMusic = null;
	private float volume = 1;
	private boolean stopping = false;
	private int nextStep = 0;
	
	public AudioManager() {
		dungeonMusic = Gdx.audio.newMusic(Gdx.files.internal("music/dungeon_ambient_1.ogg"));
		townMusic = Gdx.audio.newMusic(Gdx.files.internal("music/SearchTheWorld.mp3"));
		
		impAppears = Gdx.audio.newSound(Gdx.files.internal("sound/ImpAppears.wav"));
		ratmanAppears = Gdx.audio.newSound(Gdx.files.internal("sound/RatmanAppears.wav"));
		mimicAppears = Gdx.audio.newSound(Gdx.files.internal("sound/MimicAppears.wav"));
		beholderAppears = Gdx.audio.newSound(Gdx.files.internal("sound/BeholderAppears.wav"));
		shadowAppears = Gdx.audio.newSound(Gdx.files.internal("sound/ShadowAppears.wav"));
		
		spells = new Sound[6];
		spells[Combat.PHYS] = Gdx.audio.newSound(Gdx.files.internal("sound/DistortionSpell.wav"));
		spells[Combat.FIRE] = Gdx.audio.newSound(Gdx.files.internal("sound/FireSpell.wav"));
		spells[Combat.ICE] = Gdx.audio.newSound(Gdx.files.internal("sound/IceSpell.wav"));
		spells[Combat.ELEC] = Gdx.audio.newSound(Gdx.files.internal("sound/LightningSpell.wav"));
		spells[Combat.POIS] = Gdx.audio.newSound(Gdx.files.internal("sound/PoisonSpell.wav"));
		spells[Combat.DEATH] = Gdx.audio.newSound(Gdx.files.internal("sound/DeathSpell.wav"));
		
		steps = new Sound[] {
				Gdx.audio.newSound(Gdx.files.internal("sound/Footstep1.wav")),
				Gdx.audio.newSound(Gdx.files.internal("sound/Footstep2.wav"))
		};
		
		hits = new Sound[] {
				Gdx.audio.newSound(Gdx.files.internal("sound/Hit1.wav")),
				Gdx.audio.newSound(Gdx.files.internal("sound/Hit2.wav")),
				Gdx.audio.newSound(Gdx.files.internal("sound/Hit3.wav"))
		};

		coin = Gdx.audio.newSound(Gdx.files.internal("sound/Coin.wav"));
		dodge = Gdx.audio.newSound(Gdx.files.internal("sound/Dodge.wav"));
		error = Gdx.audio.newSound(Gdx.files.internal("sound/Error.wav"));
		monsterDefeated = Gdx.audio.newSound(Gdx.files.internal("sound/MonsterDefeated.wav"));
		stairs = Gdx.audio.newSound(Gdx.files.internal("sound/Stairs.wav"));
	}
	
	public void update(float delta) {
		if(stopping) {
			volume -= delta / FADE_OUT_TIME;
			if(volume < 0) {
				activeMusic.stop();
				volume = 0;
				stopping = false;
			} else {
				activeMusic.setVolume(volume);
			}
		}
	}
	
	public void playMusic(Music newMusic) {
		
		if(newMusic != null && newMusic != activeMusic) {
			stopMusic();
			newMusic.play();
			newMusic.setLooping(true);
			volume = 1;
			stopping = false;
			newMusic.setVolume(volume);
			activeMusic = newMusic;
		}
	}
	
	public void stopMusic() {
		stopping = true;
	}
	
	public void playSound(Sound sound) {
		sound.play();
	}
	
	public void playSpell(int element) {
		playSound(spells[element]);
	}
	
	public void playHit() {
		playSound(hits[(int) (Math.random() * hits.length)]);
	}
	
	public void playStep() {
		playSound(steps[nextStep++]);
		nextStep %= steps.length;
	}
}
