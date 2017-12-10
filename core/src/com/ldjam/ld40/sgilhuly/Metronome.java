package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.utils.Queue;

public class Metronome {

	private static final float WRITE_DELAY = 0.015f;
	private static final float WAIT_DELAY = 0.5f;
	private static final float COOLDOWN_DELAY = 0.05f;
	
	//public static // What was I going to write here ???
	
	enum State {
		NONE, WRITE, WAIT, CLEAR, COOLDOWN
	}
	
	class TextEvent {
		String text;
		int palette;
		Runnable event;
		
		public TextEvent(String t, int p, Runnable e) {
			text = t;
			palette = p;
			event = e;
			
			System.out.println("Text queued: " + text);
		}
	}
	
	private Queue<TextEvent> events;
	private TextEvent current;
	private State state = State.NONE;
	private float progress;
	
	public boolean shaking = false;
	public String displayText = "";
	public int displayPalette = 0;
	
	public Metronome() {
		events = new Queue<TextEvent>();
	}
	
	public boolean isEmpty() {
		return state == State.NONE && events.size == 0;
	}
	
	public void update(float delta) {
		if(state == State.NONE && events.size > 0) {
			nextState();
		}
		
		progress += delta;
		int displayed = (int) (progress / WRITE_DELAY);
		
		if(current != null && shaking) {
			int i = (int) (Math.random() * (current.text.length() - 2)) + 1;
			char c = current.text.charAt(i);
			if(c >= 'a' && c <= 'z') {
				c += 'A' - 'a';
			} else if(c >= 'A' && c <= 'Z') {
				c += 'a' - 'A';
			}
			current.text = current.text.substring(0, i) + c + current.text.substring(i + 1);
		}
		
		switch(state) {
		case NONE:
			break;
		case WRITE:
			if(displayed >= current.text.length()) {
				nextState();
			} else {
				displayText = current.text.substring(0, displayed);
			}
			break;
		case WAIT:
			if(progress >= WAIT_DELAY) {
				nextState();
			}
			break;
		case CLEAR:
			if(displayed >= current.text.length()) {
				nextState();
			} else {
				String spaces = new String(new char[displayed]).replace('\0', ' ');
				displayText = spaces + current.text.substring(displayed);
			}
			break;
		case COOLDOWN:
			if(progress >= COOLDOWN_DELAY) {
				nextState();
			}
			break;
		}
		
		displayed = (int) (progress / WRITE_DELAY);
		
		switch(state) {
		case WRITE:
			displayText = current.text.substring(0, displayed);
			break;
		case WAIT:
			displayText = current.text;
			break;
		case CLEAR:
			String spaces = new String(new char[displayed]).replace('\0', ' ');
			displayText = spaces + current.text.substring(displayed);
			break;
		case NONE:
		case COOLDOWN:
			displayText = "";
		}
	}
	
	public void queueEvent(String text, int palette, Runnable event) {
		events.addLast(new TextEvent(text, palette, event));
	}
	
	public void queueEvent(String text, int palette) {
		queueEvent(text, palette, null);
	}
	
	public void interruptEvent(String text, int palette, Runnable event) {
		events.addFirst(new TextEvent(text, palette, event));
	}
	
	public void interruptEvent(String text, int palette) {
		interruptEvent(text, palette, null);
	}
	
	public void clearEvents() {
		events.clear();
	}
	
	private void nextState() {
		switch(state) {
		case NONE:
			state = State.WRITE;
			current = events.removeFirst();
			displayPalette = current.palette;
			break;
		case WRITE:
			state = State.WAIT;
			if(current.event != null) {
				current.event.run();
			}
			break;
		case WAIT:
			state = State.CLEAR;
			break;
		case CLEAR:
			state = State.COOLDOWN;
			break;
		case COOLDOWN:
			state = State.NONE;
			break;
		}
		
		progress = 0;
	}
}
