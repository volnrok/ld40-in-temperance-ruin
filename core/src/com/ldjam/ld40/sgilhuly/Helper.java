package com.ldjam.ld40.sgilhuly;

public abstract class Helper {
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
}
