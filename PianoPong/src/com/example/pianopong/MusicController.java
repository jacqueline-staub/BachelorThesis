package com.example.pianopong;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicController {
	
	private float eps = 0.005f; //to detect natural multiples to be detected
	private float harmonyFactor = 0.1f; //subtract harmony
	private int refNote = 33; //the piano notes begin at the 33th half tone
	private float thresholdSoundLevel = 120; //to detect if no note is played
	private int refA4 = 36; //index of A4 in LUT
	
	//number of notes which are detected to be incorrect
	private int faultsDetected = 0;
	private boolean[] newNotes;
	private int[] notesBefore;
	private int maxVariance = 1;
	
	//LookUpTable with the Frequencies
	private double[] LUT;
	private float[] Mag;
	private double max;
	private int[] index;
	private Song song;
	public static float unit;
	private int[] lastNoteBeforePause = {1};
	private boolean lastNoteWasLong = false;
	
	public MusicController(Song song){
		this.song = song;
		buildLookUpTable();
		newNotes = new boolean[song.numberOfPhrases()];
		notesBefore = new int[song.numberOfPhrases()];
		
		//get smallest unit of note length
		unit = findShortestNote(song.getPhrases());
	}
	
	public int getFaultsDetected(){
		return this.faultsDetected;
	}
	
	private void buildLookUpTable() {
		//array with the frequencies. With reference point A4 = 440 Hz
		LUT = new double[76];
		LUT[refA4] = 440.0d;
		double factor = Math.pow(2, 1/12.0);
		for(int i=0; i<LUT.length; i++){
			LUT[i] = (LUT[refA4]*Math.pow(factor, i-refA4));
		}
	}
	
	private double[] performFFT(float[] measurements, int samplingFrequency) {
		
		FFT fft = new FFT(measurements.length, samplingFrequency);
		double[] real = new double[measurements.length];
		double[] imag= new double[measurements.length];
		double[] mag = new double[measurements.length];
		
	    fft.forward(measurements);
	    float[] tmpi = fft.getImaginaryPart();
	    float[] tmpr = fft.getRealPart();
	    
	    //compute fft's magnitude values
	    for (int i=0; i<measurements.length; i++){
	    	real[i] = (double) tmpr[i];
    		imag[i] = (double) tmpi[i];
    		mag[i] = Math.sqrt((real[i]*real[i]) + (imag[i]*imag[i]));
	    }
	    return mag;
	}
	
	public int[] getPitches(){
		return this.index;
	}

	private void findNMostProminentFrequencies(int numberOfRealNotes) {
		
		index = new int[numberOfRealNotes];
		max= -Math.pow(2, (15));
	    
	    //find the nOfPitches most prominent frequencies. given by midi file
	    for(int j=0; j<numberOfRealNotes; j++){
	    	
	    	//visit all candidates from Mag, which are already bandpass filtered
	    	for(int i=0; i<Mag.length; i++){
	    		//filter frequencies whose magnitude is too low
	    		boolean blockedByQuiety = Mag[i]<thresholdSoundLevel;
	    		if(!blockedByQuiety){
	    			findEntryWithBiggestMagnitude(i, j);
	    			filterHarmony(i);
	    		}
	    	}

		    max = -Math.pow(2, (15));
	    }
	}

	private void convertToHalfTones(int samplingFrequency, float[] measurements, double[] magnitudes) {
		//array to store the magnitude values measured for each half tone
		Mag = new float[LUT.length];
		
		//starting with the reference note as a start basis
		int shouldBe = refNote;
		double average = 0d;
		int numberOfBinsForFrequency = 0;
		int indexMemo = 0;
		
		for(int j=0; j<LUT.length; j++){//per entry in LUT
			
			for(int k=indexMemo; k<magnitudes.length; k++){//per entry in the mag array
				int now = findNearestPitch((double)(k+1)*samplingFrequency/(double)measurements.length);
				
				if(now > shouldBe){
					indexMemo = k;
					break;
				}
				if(shouldBe==now){
					average = average + magnitudes[k];
					numberOfBinsForFrequency++;
				}
			}
			if(numberOfBinsForFrequency==0){
				Mag[j] =0;
			}else{
				Mag[j] = (float) (average/(float)numberOfBinsForFrequency);
			}
			
			average = 0;
			numberOfBinsForFrequency = 0;
			shouldBe++;
		}
		
	}

	private void filterHarmony(int indexInLUT) {
		//correct magnitude of harmonies, normally the lowest frequency is the key tone
		double f1 = LUT[indexInLUT];
		for(int j = indexInLUT+12; j<Mag.length; j++){
			double f2 = LUT[j];
			double exactly = f2/f1;
			double nThHarmony = Math.round(exactly);
			
			boolean isNaturalPower = Math.abs(nThHarmony-exactly) < eps;
			if(isNaturalPower){
				double reduceBy = 1-Math.pow(harmonyFactor, nThHarmony-1);
				Mag[j] = (float) (Mag[j]*reduceBy);
			}
		}
	}

	private int findNearestPitch(double frequency) { 
		
		int indexInLUTforFrequency = -Arrays.binarySearch(LUT, frequency) - 1;
		
		if(indexInLUTforFrequency == 0){
			return refNote;
		}
		int res;
		
		double factor = Math.pow(2, 1/12.0);
		double LutLower = LUT[refA4]*Math.pow(factor, indexInLUTforFrequency-refA4);
		double LutUpper = LUT[refA4]*Math.pow(factor, (indexInLUTforFrequency-1)-refA4);
		
		if((LutLower-frequency)<(frequency-LutUpper)){
			res = indexInLUTforFrequency;
		}else{
			res = indexInLUTforFrequency-1;
		}
		
		return res + refNote;
	}

	private void findEntryWithBiggestMagnitude(int i, int j) {
		if(j==0){
			if(Mag[i]>=max){
    			index[j] = i+refNote;
    			max = Mag[i];
    		}
		}else{
    			boolean b = true;
				for(int n=0; n<j; n++){
					//determine whether this bin was never the chosen maximum
					b = b && index[n] != i+refNote;
				}
				if(Mag[i]>=max && b){
	    			index[j] = i+refNote;
	    			max = Mag[i];
    			}
		}
		
	}

	public Song performAnalysis(float[] microphoneMeasurements, int samplingFrequency) {
		if(song !=null && !song.isEmpty()){
			
			//set threshold for actual note, dependent on length and hight of the note
			ArrayList<Note> notes = song.getAllFirstNotes();
			float length=0; float height=0;
			for(Note n: notes){
				if(n.getLength()>length){
					length = n.getLength();
				}
				if(n.getPitch()>height){
					height = n.getPitch();
				}
			}
			
			//TODO find accurate values!
			if(height>85){
				thresholdSoundLevel = 80;
			}else if(height<55){
				thresholdSoundLevel = 80;
			}
					
			//fft from samples, produce magnitude array
			double[] mag = performFFT(microphoneMeasurements, samplingFrequency);
			
			//discretisation and bandpass Filtering
			convertToHalfTones(samplingFrequency, microphoneMeasurements, mag);
			
			//detemine number of non-REST notes
			int numberOfRealNotes = song.howManyNotes();
			
			//corresponding the expected notes from level now
			findNMostProminentFrequencies(numberOfRealNotes);
			
			//check correctness
			return checkNote(index);
		}
		return null;
	}
	
	//compare current measurements with the expected ones from the song
	public Song checkNote(int[] measurements) {
		//we are done if song is empty
		if(song.isEmpty()){
			return null;
		}else{
			try {
				ArrayList<Note> expectedNotes = song.getAllFirstNotes();				
				
				boolean bo = containsAll(expectedNotes, lastNoteBeforePause);
				boolean we = isQuiet(notesBefore);
				boolean pln = playingLongNote(expectedNotes);
				boolean cs = currentNoteIsShort(expectedNotes);
				
				//check if expected is the same note as played before if so: request a pause in between
				if(!bo || (bo && we) || (pln && cs)){
				
					//expected equals index in any order -> correct notes.
					if(containsAll(expectedNotes, measurements)){
						//react on screen by marking the note green or anything like this
						ArrayList<ArrayList<Note>> phrases = song.getPhrases();
						for (int i=0; i<phrases.size(); i++){
							ArrayList<Note> p = phrases.get(i);
							
							//TODO test
							boolean ob = onlybreak(expectedNotes);
							//inform renderer to print this note
							if(p.get(0).getPitch()>0 || ob){
								RendererGame.correctlyPlayed(p.get(0));
							}
							
							p.remove(0);
							newNotes[i] = true;
							
							//Adjust buffersize to measure in Recorder according to note lentgh TODO
							/*
							if(p.size()>1){
								if(p.get(1).getLength()>=0.25 && p.get(1).getLength() <= 1.0){
									Recorder.updateRecorder(1);
								}else if(p.get(1).getLength()>1.0 && p.get(1).getLength() <= 3.0){
									Recorder.updateRecorder(2);
								}else if(p.get(1).getLength()>3.0 && p.get(1).getLength() <= 4.0){
									Recorder.updateRecorder(4);
								}else{
									Recorder.updateRecorder(8);
								}
							}*/
							
							/*
							if(p.get(0).getLength() > unit){
								Note note;
								note = new Note(p.get(0).getPitch(), (int)p.get(0).getLength()-unit, p.get(0).isUp());
								p.set(0, note);
								newNotes[i] = false;
							}else{
								boolean ob = onlybreak(expectedNotes);
								//inform renderer to print this note
								if(p.get(0).getPitch()>0 || ob){
									RendererGame.correctlyPlayed(p.get(0));
								}
								
								p.remove(0);
								newNotes[i] = true;
							}*/
						}
					}else{//not the correct notes					
						//if there is a not in it that is further away than 2 -> mark it as wrong
						if(!isQuiet(measurements) && !isOvertoneOrSame(measurements, notesBefore) && noNewNotes() && moreThan1HalfNoteAway(measurements, expectedNotes)){
							for(int pitch: measurements){
								//find closest correct note
								Note cn = closestNote(pitch, expectedNotes);
								
								//inform renderer to print this note
								RendererGame.incorrectlyPlayed(cn);
							}
							faultsDetected++;
						}
						
						for(int i=0; i<newNotes.length; i++){
							newNotes[i]=false;
						}
					}
				}
			}catch(Exception e){}
			
			//update last notes
			if(!isQuiet(measurements)){
				lastNoteBeforePause = measurements;
			}
			notesBefore = measurements;
			
			return song;
		}
	}
	
	private Note closestNote(int pitch, ArrayList<Note> expectedNotes) {
		Note n = new Note(0, 1, false);
		int diff = Integer.MAX_VALUE;
		for(Note note: expectedNotes){
			if(diff >= Math.abs(note.getPitch()-pitch)){
				n = note;
				diff = Math.abs(note.getPitch()-pitch);
			}
		}
		return new Note(pitch, 1, n.isUp());
	}

	private boolean onlybreak(ArrayList<Note> expectedNotes) {
		boolean allbreak = true;
		for(Note n:expectedNotes){
			if(n.getPitch()>0){
				return false;
			}else{
				allbreak &=true;
			}
		}
		return allbreak;
	}

	private boolean noNewNotes(){
		for (boolean nN : newNotes) {
			if(nN==true){
				return false;
			}
		}
		return true;
	}
	
	private boolean currentNoteIsShort(ArrayList<Note> expectedNotes) {
		boolean res = true;
		for (Note note : expectedNotes) {
			if(note.getPitch()>0){
				if(note.getLength()==unit){
					res &= true;
				}else{
					return false;
				}
			}
		}
		return res;
	}

	private boolean playingLongNote(ArrayList<Note> expectedNotes) {
		boolean memo = lastNoteWasLong;
		
		for (Note note : expectedNotes) {
			if(note.getPitch()>0 && note.getLength()>unit){
				lastNoteWasLong = true;
				return true;
			}
		}
		lastNoteWasLong = false;
		return memo;
	}
	
	private boolean isQuiet(int[] measurements) {
		for (int m : measurements) {
			if(m!=0){
				return false;
			}
		}
		return true;
	}

	private boolean isOvertoneOrSame(int[] measurements, int[] refNotes) {
		for(int m: measurements){
			for(int r: refNotes){
				if(m!=0 && r!= 0){
					if(Math.abs(m-r)==12 || Math.abs(m-r)==0){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private float findShortestNote(ArrayList<ArrayList<Note>> phrases) {
		float res = Float.MAX_VALUE;
		for (ArrayList<Note> p : phrases) {
			for (int i=0; i<p.size(); i+=1) {
				if(p.get(i).getLength()<res){
					res=p.get(i).getLength();
					}
			}
		}
		return res;
	}

	private boolean moreThan1HalfNoteAway(int[] measurements, List<Note> expectedNotes) {
		boolean res = true;
		float[] distance = new float[measurements.length];
		
		//distance is MaxInt in the beginning
		Arrays.fill(distance, Integer.MAX_VALUE);
		
		//find the note which is the closest to i in expected
		for (int v=0; v<measurements.length; v++) {
			for (Note exp : expectedNotes) {
				if(Math.abs(exp.getPitch()-measurements[v])<distance[v]){
					distance[v] = Math.abs(exp.getPitch()-measurements[v]);
				}
			}
		}
		
		//go through distance array, if every entry is greater than 1 return true, else false
		for (float j : distance) {
			res = res && (Math.abs(j)>maxVariance);
		}
		return res;
	}

	private boolean containsAll(ArrayList<Note> expected, int[] measurements){
		boolean res = true;
		List<Integer> notelist = Arrays.asList(song.getNoteList(expected));
		for (int i : measurements) {
			res &= notelist.contains(i);
		}
		return res;
	}

}
