package com.ldjam.ld40.sgilhuly;

import com.badlogic.gdx.utils.Queue;

public class Metronome {

	private static final float WRITE_DELAY = 0.02f;
	private static final float WAIT_DELAY = 1f;
	private static final float COOLDOWN_DELAY = 0.25f;
	
	public static 
	
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
	
	public String displayText = "";
	public int displayPalette = 0;
	
	public Metronome() {
		events = new Queue<TextEvent>();
	}
	
	public void update(float delta) {
		if(state == State.NONE && events.size > 0) {
			nextState();
		}
		
		progress += delta;
		int displayed = (int) (progress / WRITE_DELAY);
		
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
