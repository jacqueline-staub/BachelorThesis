package com.example.pianopong;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import jm.JMC;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.music.tools.Mod;
import jm.util.Write;

public class LevelManager implements JMC{
	
	private static Score aScore;
	private static int partCounter = 0;
	
	private static ArrayList<ArrayList<double[]>> phrases;
	private ArrayList<ArrayList<Note>> notephrases;
	
	private static int numOfFiles;
	private Context context;
	
	public static File mediaDir = new File(Environment.getExternalStorageDirectory().getPath()+"/documents/pianoPong");
	
	public LevelManager(Context c){
		this.context = c;
		phrases = new ArrayList<ArrayList<double[]>>();
		notephrases = new ArrayList<ArrayList<Note>>();
	}
	
	public int getLevelNumber(int difficulty, int level){
		return 5*difficulty+level;
	}
	
	public ArrayList<ArrayList<double[]>> getLevelsForCategory(int i){
		//how many extra levels are there?
		int nOfExtras = getNumOfFilesOnSDCard("e");
		if(i==5){//it's about the extra levels
			ArrayList<ArrayList<double[]>> levels = new ArrayList<ArrayList<double[]>>();
			
			for(int j=(numOfFiles-nOfExtras); j<numOfFiles; j++){
				levels.add(phrases.get(j));
			}
			return levels;
		}else{//about the normal levels
			//subtract the extra levels
			
			int start = i*(numOfFiles-nOfExtras+1)/5;
			int stop = (i+1)*(numOfFiles-nOfExtras)/5;
			
			ArrayList<ArrayList<double[]>> levels = new ArrayList<ArrayList<double[]>>();
			
			for(int j=start; j<stop; j++){
				levels.add(phrases.get(j));
			}
			return levels;
		}
	}

	public int getNumExtras(){
		loadExtrasFromAssets();
		try{
			String[] list = context.getAssets().list("store");
			return list.length;
		}catch(Exception e){}
		return 0;
	}
	
	public void deleteAccount(){
		//delete all files in mediaDir
		if(mediaDir.exists()) {
			File[] files = mediaDir.listFiles();
			for(int i=0; i<files.length; i++) {
				files[i].delete();
			}
		}
		//delete the folder
		mediaDir.delete();
	}
	
	public boolean checkCreateLevels(){
		
		//count number of predefined levels in assets
		AssetManager am = context.getAssets();
		try{
			String[] list = am.list("songs");
			numOfFiles = list.length;
		}catch(Exception e){}
		
		//create new folder for levels
		if(!mediaDir.exists()){
			mediaDir.mkdir();
			loadLevelsFromAssets();
			writeSongs();
			for (int i=0; i<numOfFiles; i++) {
				writeMidis(i);
			}
			return true;
		}
		return false;
	}
	
	public Song getSong(int difficulty, int level){
		
		//get song + reset notephrases
		ArrayList<double[]> s = getLevelsForCategory(difficulty).get(level);
		notephrases = new ArrayList<ArrayList<Note>>();
		
		//number of phrases
		int numOfPhrases = s.size();
		
		//prepare note object
		Note note;
		ArrayList<Note> phra = new ArrayList<Note>();
		
		//transform levels, easier to manage
		for(int j=0; j<numOfPhrases; j++){
			double[] p = s.get(j);
			for (int i=0; i<p.length; i+=2) {
				note = new Note((int)p[i], (float)p[i+1], j==0);
				phra.add(note);
			}
			notephrases.add(phra);
			phra = new ArrayList<Note>();
		}
		
		Song song = new Song(notephrases, difficulty*100+10*level+100);
				
		return song;			
	}
	
	public int getNumOfFilesOnSDCard(final String startingChar){
		File[] files = mediaDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".txt") && filename.startsWith(startingChar);
			}
		});
		return files.length;
	}
	
	private void loadLevelsFromAssets(){
		AssetManager am = context.getAssets();
		try{
			String[] list = am.list("songs");
			for (String string : list) {
				InputStream inputStream = am.open("songs/"+string);
				ArrayList<double[]> p = new ArrayList<double[]>();
		        
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		        String line = reader.readLine();
		        
		        while (line != null) {
		        	String[] test = line.split(",");
		        	double[] phra = new double[test.length];
		        	for (int i = 0; i < test.length; i++) {
						phra[i] = Double.parseDouble(test[i]);
					};
		        	
		        	p.add(phra);
		        	
		        	line = reader.readLine();
		        }
		        phrases.add(p);
		        
			}
		}catch(Exception e){
			e.toString();
		}
	}
	
	private void loadExtrasFromAssets(){
		//phrases.clear();
		AssetManager am = context.getAssets();
		try{
			String[] list = am.list("store");
			for (String string : list) {
				InputStream inputStream = am.open("store/"+string);
				ArrayList<double[]> p = new ArrayList<double[]>();
		        
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		        String line = reader.readLine();
		        
		        while (line != null) {
		        	String[] test = line.split(",");
		        	double[] phra = new double[test.length];
		        	for (int i = 0; i < test.length; i++) {
						phra[i] = Double.parseDouble(test[i]);
					};
		        	
		        	p.add(phra);
		        	
		        	line = reader.readLine();
		        }
		        phrases.add(p);
			}
		}catch(Exception e){
			e.toString();
		}
		
	}
	
	public void loadLevelsFromSDCard(){
		
		//all Files in the Folder which are txt files
		File[] files = mediaDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".txt");
			}
		});
		
		numOfFiles = files.length;
		
		try{
			for (File f : files) {
				InputStream inputStream = new BufferedInputStream(new FileInputStream(f));
				ArrayList<double[]> p = new ArrayList<double[]>();
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		        String line = reader.readLine();
		        
		        while (line != null) {
		        	String[] test = line.split(",");
		        	double[] phra = new double[test.length];
		        	for (int i = 0; i < test.length; i++) {
						phra[i] = Double.parseDouble(test[i]);
					};
		        	
		        	p.add(phra);
		        	
		        	line = reader.readLine();
		        }
		        phrases.add(p);
		        
			}
		}catch(Exception e){
			
		}
	}

	public void writeMidis(int i) {
		aScore = new Score();
		
		//phrase set at this moment
		ArrayList<double[]> phra = phrases.get(i);
		
		// arrangement and orchestration
		for (int j=0; j<phra.size(); j++) {
			notesToPart(phra.get(j),0,0,PIANO, j);
		}
		
		// specify the tempo
		aScore.setTempo(100.0);
		Write.midi(aScore, mediaDir+"/level"+String.valueOf(i)+".mid");
		
	}
	
	public void writeExtraToSD(String filename){
		AssetManager assetManager = context.getAssets();
		InputStream in = null;
        OutputStream out = null;
        try {
          in = assetManager.open("store/"+filename);
          File outFile = new File(mediaDir, filename);
          out = new FileOutputStream(outFile);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
        } catch(IOException e) {
        }       
	}
	
	private void writeSongs() {
		AssetManager assetManager = context.getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list("songs");
	    } catch (IOException e) {
	    }
	    
	    for(String filename : files) {
	        InputStream in = null;
	        OutputStream out = null;
	        try {
	          in = assetManager.open("songs/"+filename);
	          File outFile = new File(mediaDir, filename);
	          out = new FileOutputStream(outFile);
	          copyFile(in, out);
	          in.close();
	          in = null;
	          out.flush();
	          out.close();
	          out = null;
	        } catch(IOException e) {
	        }       
	    }
		
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
	
	public static void notesToPart(double[] notes, double startTime, int repeats, int instrument, int channel) {
	    // create a new phrase from the notes and loop it
	    Phrase aPhrase = new Phrase(startTime);
	    aPhrase.addNoteList(notes);
	    Mod.repeat(aPhrase, repeats);
	    // create a new part and add the phrase to it
	    Part aPart = new Part("Part "+partCounter, instrument, channel);
	    aPart.addPhrase(aPhrase);
	    // keep track of how many parts have been created
	    partCounter++;
	    // add the part to the score
	    aScore.addPart(aPart);
	}

	public boolean notYetBought(final String string) {
		//find the corresponding file
		File[] files = mediaDir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				return filename.contentEquals(string);
			}
		});
		
		return (files.length==0);
	}

}
