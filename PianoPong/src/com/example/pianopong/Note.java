package com.example.pianopong;

public class Note {
	
	private int pitch;
	private float length;
	private boolean isUp;
	
	public Note(int pitch, float length, boolean isUp){
		this.length = length;
		this.pitch = pitch;
		this.isUp = isUp;
	}
	
	public boolean isUp(){
		return isUp;
	}
	
	public int getPitch(){
		return pitch;
	}
	
	public float getLength(){
		return length;
	}

}
