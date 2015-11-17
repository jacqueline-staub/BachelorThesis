package com.example.pianopong;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

public class LevelPickerActivity extends Activity {
	
	public static int difficulty;
	public static ListView lv;
	public static Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//turn off the title, full screen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_level_picker);
		
		//get extras (difficulty) from last activity
		Intent intent = getIntent();
		difficulty = intent.getExtras().getInt("difficulty");
		
		//get the number of levels in the chosen category
		int numOfLevelsInCategory = MainActivity.lm.getLevelsForCategory(difficulty).size();
		
		//setting of levels in GUI
		lv = (ListView)findViewById(R.id.levels);
		List<ListLevelViewItem> list = new ArrayList<ListLevelViewItem>();
		for (int i = 1; i <= numOfLevelsInCategory; ++i) {
			Button b1 = (Button)findViewById(R.id.button1);
			Button b2 = (Button)findViewById(R.id.button2);
			list.add(new ListLevelViewItem(R.drawable.star, "Level "+String.valueOf(i), "earn additional "+String.valueOf(difficulty*100+(i-1)*10+100)+ " Points for completion", b1, b2));
		}
		
		CustomLevelListViewAdapter adapter = new CustomLevelListViewAdapter(this, list);
	    lv.setAdapter(adapter);
	    
	    c = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		CustomLevelListViewAdapter.mp.stop();
	}

}
