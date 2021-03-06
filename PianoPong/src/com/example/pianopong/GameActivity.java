package com.example.pianopong;

import java.io.File;

import java.io.FileInputStream;
import java.util.concurrent.LinkedBlockingQueue;

import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

	//array: mic recordings, fs: sampling frequency, level
	private float[] array;
	private int fs;
	public static int level;
	public static boolean running;
	
	private GLSurfaceView surf;
	public static int levelNum;
	public static int difficulty;
	
	//queue to store data for the two threads
	private static LinkedBlockingQueue<float[]> microQueue;
	private MusicController mc;
	public static Song song;
	public static LevelPickerActivity lpa;
	private Recorder rec;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		
		super.onCreate(savedInstanceState);
		//turn off the title, full screen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Bundle extras = getIntent().getExtras();
		prepare(extras.getInt("level"), extras.getInt("difficulty"));
	}

	public void prepare(int l, int d) {
		//get selected level from previous activity + difficulty
		level = l;
		difficulty = d;
		
		//set up song and phrases
		LevelManager lv  = MainActivity.lm;
		song = lv.getSong(difficulty, level);
		
		//get LevelNumber for Token generation
		levelNum = lv.getLevelNumber(difficulty, level);
		
		//how long to run the threads
		running = true;
		
		//reset recorder if this is the first game
		rec = new Recorder();
		
		//build LUT with reference frequencies and control unit
		mc = new MusicController(song);
		
		//prepare sharing medium between listeningThread, fftThread and guiThread
		microQueue = new LinkedBlockingQueue<float[]>();
		
		//set View to surface View
		RendererGame r = new RendererGame(this);
		surf = new GLSurf(this,r);
		surf.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
		surf.getHolder().setFormat(PixelFormat.RGBA_8888);
		surf.setRenderer(r);
		surf.requestFocus();
		surf.setFocusable(true);
		setContentView(surf);
		//setContentView(R.layout.activity_game);
		
		//create listening Thread and 4 fftThread, to keep load balance in Queue
		createListeningThread();
		createFFTThread();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		running = false;
		rec.releaseRec();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	public void playMidi(View view){
		//stop ListeningTask while playback of midi File!
		Thread playbackThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				//stop fftThread and listening Thread
				running = false;
				
				MediaPlayer mp = new MediaPlayer();
				String filePath = Environment.getExternalStorageDirectory().getPath()+"/documents/pianoPong/level"+String.valueOf(level)+".mid";
				File file = new File(filePath);
				try {
					FileInputStream inputStream = new FileInputStream(file);
					mp.setDataSource(inputStream.getFD());
					inputStream.close();
					mp.prepare();
				} catch (Exception e) {
				}
		         mp.start();
		         mp.setVolume(3, 3);
		         
		         while(mp.isPlaying()){
		        	 
		         }
		         
		         //restart listening thread and fftThread
		         running = true;
			}
		});
		playbackThread.start();
	}
	
	
	
	private void createListeningThread() {
		Thread listeningThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				//prepare recorder if not yet ready;
				if(rec==null){
					rec = new Recorder();
				}
				
				//continuously read the microphone and write it to the working-queue for the reader
				fs = rec.getSampleRate();
				
				while(running){
					rec.run();
					
					//get values from microphone, sample rate and samples, define index [0..4], how long is the expected note
					array = rec.getFloatArray();
					
					//add the array to the queue used to share data between reader and writer
					microQueue.add(array);
				}
			}
		});
		listeningThread.start();
	}
	
	private Thread getWorker() {
		Thread worker = new Thread(new Runnable() {
			
			@Override
			public void run() {
				//read input, how many pitches should be detected, prepare freq, pitch and index
				float[] data = new float[rec.getArraySize()];
				try {
					data = (float[]) microQueue.take();
					song = mc.performAnalysis(data, fs);
					
					//if level complete
					if(song == null){
						running = false;
					}
				} catch (InterruptedException e) {
				}
			}
		});
		return worker;
	}

	private void createFFTThread() {
		Thread fftThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(running){
					//every round create numWorkers Threads to run parallel and join them to get their results
					
					Thread worker = getWorker();
					worker.start();
					try {
						worker.join();
					} catch (InterruptedException e) {
					}
						
					//update gui TODO
					/*
					TextView no = (TextView)findViewById(R.id.notes);
					no.post(new GUIAgent(mc, GameActivity.this, (ListView)findViewById(R.id.listView), song, no, mc.getFaultsDetected()));
					*/
				}
			}
				
		});
		fftThread.start();
	}
}
	
