package com.example.pianopong;

import android.widget.Button;

public class ListLevelViewItem {

	private int thumbnailResource;
	private String title;
	private String subtitle;
	private Button audioExample;
	private Button play;
	
	ListLevelViewItem(int tr, String t, String s, Button b1, Button b2){
		thumbnailResource = tr;
		title = t;
		subtitle = s;
		play = b1;
		audioExample = b2;
	}
	
	public Button getPlayButton(){
		return play;
	}
	
	public Button getAudioExampleButton(){
		return audioExample;
	}
	
	public int getThumbnailResource(){
		return thumbnailResource;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getSubtitle(){
		return subtitle;
	}
}