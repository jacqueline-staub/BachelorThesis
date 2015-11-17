package com.example.pianopong;

import java.util.ArrayList;
import java.util.Arrays;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GUIAgent implements Runnable{
	
	MusicController mc;
	GameActivity ga;
	ListView listView;
	Song song;
	TextView notes;
	int faults;
	
	public GUIAgent(MusicController mc, GameActivity ga, ListView listView, Song song, TextView notes, int faults){
		this.ga = ga;
		this.listView = listView;
		this.mc = mc;
		this.notes = notes;
		this.song = song;
		this.faults = faults;
	}

	@Override
	public void run() {
	    
	    //get phrase 0
	    if(song != null){
	    	ArrayList<String> p = new ArrayList<String>();
	    	
	    	ArrayList<ArrayList<Note>> phrases = song.getPhrases();
	    	
	    	for (ArrayList<Note> a : phrases) {
				p.add(Arrays.toString(song.getNoteList(a)));
			}
	    	
	    	ArrayList<String> list = new ArrayList<String>();
	    	for(String s: p){
	    		list.add(s);
	    	}
		    
		    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ga, android.R.layout.simple_list_item_1, list);
		    listView.setAdapter(adapter);
		    
	    	notes.setText(Arrays.toString(mc.getPitches())+ "number of faults: "+String.valueOf(faults));
	    }else{
	    	notes.setText("congrats, you finished this level! \n"+ "number of faults in total in this level: "+String.valueOf(faults));
	    }
	}

}
