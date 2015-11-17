package com.example.pianopong;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private MediaPlayer mp = new MediaPlayer();
	public static Recorder rec;
	public static LevelManager lm;
	public static MusicController mc;
	public Token token;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mp.stop();
		mp = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//turn off the title, full screen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);	
		
		//font
		TextView start = (TextView) findViewById(R.id.start);  
		TextView ack = (TextView) findViewById(R.id.ack);
		TextView store = (TextView) findViewById(R.id.store); 
		TextView delAcc = (TextView) findViewById(R.id.deleteAccount);
		Typeface font = Typeface.createFromAsset(getAssets(), "sub/Amputation.ttf");  
		start.setTypeface(font);
		ack.setTypeface(font);
		store.setTypeface(font);
		delAcc.setTypeface(font);
		
		//prepare Token
		token = new Token(this);
		TextView points = (TextView)findViewById(R.id.points);
		points.setTypeface(font);
		points.setText("Score: "+token.getPoints());
		
		//prepare Songs (load from mem etc.)
		prepareSongs();
		
		playBgMusic();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		token = new Token(this);
		TextView points = (TextView)findViewById(R.id.points);
		points.setText("Points: "+token.getPoints());
		
		//prepare Songs again (new extras added, maybe)
		prepareSongs();
	}
	
	public void prepareSongs(){
		//get and set up the LevelManager
		lm = new LevelManager(this);
		
		//check wheter this is the first session
		boolean newUser =lm.checkCreateLevels();
		
		//if it is not, get values for the token level and experience
		if(!newUser){
			lm.loadLevelsFromSDCard();
		}
	}
	
	public void playBgMusic(){
		//background music
		try {
			AssetFileDescriptor descriptor = getAssets().openFd("sub/mwb.mid");
            mp.setDataSource(descriptor.getFileDescriptor(),descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mp.prepare();
            mp.setLooping(true);
            mp.start();
            mp.setVolume(3, 3);
		} catch (IOException e) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	public void startGame(View view){
		mp.stop();
		Intent intent1 = new Intent(this, DifficultyActivity.class);
		startActivity(intent1);
	}
	
	public void deleteAccount(View view){
		//ask user whether he really wants to delete the account
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
		myAlertDialog.setTitle("Delete Account");
		myAlertDialog.setMessage("Do you really want do delete this account?");
		myAlertDialog.setPositiveButton("Yes", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {//ok button was clicked
				//reset points
				token.setPoints(0);
				TextView points = (TextView)findViewById(R.id.points);
				points.setText("Points: "+ token.getPoints());
				//delete all elements in mediaDir
				lm.deleteAccount();
				//write all basis levels back into mediaDir
				prepareSongs();

			}});
		myAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				// do nothing
			}});
		myAlertDialog.show();
	}
	
	public void startAcks(View view){
		Intent intent2 = new Intent(this, AcknowledgementActivity.class);
		startActivity(intent2);
	}

	public void startStore(View view){
		Intent intent3 = new Intent(this, StoreActivity.class);
		startActivity(intent3);
	}

}
