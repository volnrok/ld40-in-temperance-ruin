package com.ldjam.ld40.sgilhuly;

public abstract class GameContext {
	
	public static RuinGame game;
	public static Player player;
	public static Combat combat;
	public static Map currentMap;
	public static Metronome metronome = new Metronome();
	public static Transition transition;
}
