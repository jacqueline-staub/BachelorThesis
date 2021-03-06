package com.example.pianopong;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.AudioFormat;

public class Recorder extends Thread{
	
	private static short[] audioData;
	private static float[] floatArray;
	
	//values to set up the AudioRecorder
	private AudioRecord recorder;
	public static int buffersizefactor = 1;
	private static int buffersize;
	private static int audioSource = MediaRecorder.AudioSource.MIC;
	private static int sampleRate;
	private static int channel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private static int encoding = AudioFormat.ENCODING_PCM_16BIT;
	
	/*
	public static void updateRecorder(int factor){
		if(AudioRecord.getMinBufferSize(sampleRate, channel, encoding)*factor != buffersize){//next note has different length
			buffersize = AudioRecord.getMinBufferSize(sampleRate, channel, encoding)*factor;
			audioData = new short[buffersize];
			floatArray = new float[buffersize];
			if(recorder.getState() == AudioRecord.STATE_INITIALIZED){
				recorder.stop();
				recorder.release();
				recorder = null;
			}
			recorder = new AudioRecord(audioSource, sampleRate, channel, encoding, buffersize);
			recorder.startRecording();
		}
	}*/
	
	public Recorder(){
		
		//find the devices preferred sampling rate
		sampleRate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM);
		
		//get buffer size to use, has to be power of two for fft algo
		buffersize = AudioRecord.getMinBufferSize(sampleRate, channel, encoding); //ab 33 bis 108
		
		//make sure buffersize is power of two TODO is this even needed?
		/*
		int powerTwo = buffersize;
		int exp = 0;
		while(powerTwo > 1){
			powerTwo >>>= 1;
			exp++;
		}
		
		
		//update buffersize to power of two
		buffersize = (int)Math.pow(2, exp);
		*/
		
		//here we will store the recorded pcm data
		audioData = new short[buffersize];
		
		//instantiate floatArray to store samples as float further on
		floatArray = new float[buffersize];
		
		//instantiate Recorder
		recorder = new AudioRecord(audioSource, sampleRate, channel, encoding, buffersize);
		prepare();		
	}
	
	public int getSampleRate(){
		return this.sampleRate;
	}
	
	public float[] getFloatArray(){
		return this.floatArray;
	}
	
	public int getArraySize(){
		return this.floatArray.length;
	}
	
	public int getBuffersize(){
		return this.buffersize;
	}
	
	public void prepare(){
		//start microphone and get input
		recorder.startRecording();
	}
	
	public void releaseRec(){
		if(recorder!=null){
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}
	
	@Override
	public void run() {
		super.run();
		try{
			//read microphone into a byte[] called array
			recorder.read(audioData, 0, buffersize);
		}catch(Exception e){
		}
		
		//conversion from short[] to float[]
		for(int i=0; i<buffersize; i++){
			floatArray[i] = ((float) audioData[i])/32768.0f;
		}
	}
}