package com.ldjam.ld40.sgilhuly;

public class Player extends Creature {
	public int posLevel;
	public int posX;
	public int posY;
	public int posDir;
	
	public Player() {
		posLevel = 1;
		posX = 2;
		posY = 2;
		posDir = Map.NORTH;
	}
	
	public void step(int amount) {
		Map m = Map.getMap(posLevel);
		byte space = m.accessMap(posX, posY, posDir, amount, 0);
		if(Map.isPassable(space)) {
			Basis b = Map.dirToBasis(posDir);
			posX += b.forwardX * amount;
			posY += b.forwardY * amount;
		}
	}
	
	public void turn(int amount) {
		posDir = Helper.mod(posDir + amount, 4);
	}
}
