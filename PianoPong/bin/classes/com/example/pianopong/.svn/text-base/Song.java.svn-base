package com.example.pianopong;

import java.util.ArrayList;

import jm.JMC;

public class Song implements JMC{
	
	private ArrayList<ArrayList<Note>> phrases = new ArrayList<ArrayList<Note>>();
	public int length;
	private int earnings;
	
	public ArrayList<Note> getAllFirstNotes(){
		ArrayList<Note> first = new ArrayList<Note>();
		for(ArrayList<Note> phrase: phrases){
			first.add(phrase.get(0));
		}
		return first;
	}
	
	public void setEarnings(int e){
		earnings = e;
	}
	
	public int getEarnings(){
		return earnings;
	}
	
	public Song(ArrayList<ArrayList<Note>> phrases, int e){
		this.phrases = phrases;
		length = lengthOfSong(phrases);
		earnings = e;
	}
	
	public int lengthOfSong(ArrayList<ArrayList<Note>> p){
		ArrayList<Note> firstPhrase = p.get(0);
		int length = 0;
		for(int i=1; i<firstPhrase.size(); i+=2){
			length += firstPhrase.get(i).getLength();
		}
		return length;
	}
	
	public boolean isEmpty(){
		//the three phrases are supposed to be equally long, so they are empty at the same time
		for (ArrayList<Note> p : phrases) {
			if(p.isEmpty()){
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<ArrayList<Note>> getPhrases(){
		return this.phrases;
	}
	
	public int howManyNotes(){
		try{
			ArrayList<Note> firsts = getAllFirstNotes();
			ArrayList<Note> res = new ArrayList<Note>();
			boolean add = true;
			for(int i=0; i<firsts.size(); i++){
				for(Note n:res){
					if(n.getPitch()==firsts.get(i).getPitch()){
						add = false;
					}
				}
				if(firsts.get(i).getPitch()>0 && add){
					res.add(firsts.get(i));
				}
				add = true;
			}
			return res.size();
		}catch(Exception e){
			return 0;
		}
	}
	
	public int numberOfPhrases(){
		return phrases.size();
	}
	
	public Integer[] getNoteList(ArrayList<Note> phrase){
		Integer[] res = new Integer[phrase.size()];
		for (int i=0; i<phrase.size(); i++) {
			res[i] = phrase.get(i).getPitch();
		}
		return res;
	}
	
}
