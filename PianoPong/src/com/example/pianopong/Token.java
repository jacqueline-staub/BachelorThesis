package com.example.pianopong;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Token {
	
	private int health;
	private int points;
	private Context c;
	
	public Token(Context c){
		this.c = c;
		this.health=100;
		
		//get values from stored preferences
		SharedPreferences pref = c.getSharedPreferences("MyPref", 0); // 0 - for private mode
		int p = pref.getInt("points", -1);
		
		if(p<0){//first session initialisation
			storeToPrefs(c, 0);
			this.points=0;
		}else{//not first session, set values from last session
			this.points=p;
		}
	}
	
	public void payForExtra(int price){
		//price is always a multiple od 100, so it affects only the level
		points -= price;
		storeToPrefs(c, points);
	}
	
	public int getPoints(){
		return points;
	}
	
	public void setPoints(int p){
		points = p;
		storeToPrefs(c, 0);
	}
	
	public void storeToPrefs(Context c, int points){
		SharedPreferences pref = c.getSharedPreferences("MyPref", 0); // 0 - for private mode
		Editor editor = pref.edit();
		editor.putInt("points", points);
		editor.commit();
	}
	
	public boolean makeDamage(int damage){
		if(health-damage<0){//token is dead
			return false;
		}else{
			health -= damage;
			return true;
		}
	}
	
	public void increaseExperience(int factor){
		points += factor;
	}
	
	public int getExperience(){
		return this.points%100;
	}
	
	public int getHealth(){
		return health;
	}

}
