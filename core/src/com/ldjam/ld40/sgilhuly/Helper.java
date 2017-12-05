package com.ldjam.ld40.sgilhuly;

public abstract class Helper {
	
	public static float lerp(float n, float start, float end) {
		return start + n * (end - start);
	}
	
	public static int iclamp(int n, int min, int max) {
		return n < min ? min : n > max ? max : n;
	}
	
	public static float clamp(float n, float min, float max) {
		return n < min ? min : n > max ? max : n;
	}
	
	public static int mod(int a, int b) {
		a = a % b;
		if(a < 0) a += b;
		return a;
	}
	
	public static float rand(float min, float max) {
		return rand(min, max, 1);
	}
	
	public static float rand(float min, float max, int offset) {
		return ((float) Math.random()) * (max - min + offset) + min;
	}
	
	public static float resist(float amount, float resist) {
		float resistFactor = 1 - rand(resist / 200, resist / 100, 0);
		//System.out.println(String.format("Resist: %f by %f, factor: %f, total: %f", amount, resist, resistFactor, Math.max(amount * resistFactor, 0)));
		return Math.max(amount * resistFactor, 0);
	}
	
	public static float sigmoid(float x) {
		return (float) (Math.exp(x / 20) / (Math.exp(x / 20) + 1));
	}
}
