package com.example.pianopong;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

public class DifficultyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//turn off the title, full screen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_difficulty);
		
		//prepare list of difficulty levels
		List<ListViewItem> items = new ArrayList<ListViewItem>();
		items.add(new ListViewItem(R.drawable.diffi0, "beginner", "welcome my mollycoddle!"));
		items.add(new ListViewItem(R.drawable.diffi1, "easy", "let's have a coffee party!"));
		items.add(new ListViewItem(R.drawable.diffi2, "medium", "a mouthful of challenge!"));
		items.add(new ListViewItem(R.drawable.diffi3, "hard", "a small leak will sink a great ship!"));
		items.add(new ListViewItem(R.drawable.diffi4, "harder", "fortune favors the bold!"));
		items.add(new ListViewItem(R.drawable.extras, "extras", "have fun, you worked hard for it!"));
				
		//setting in GUI
		ListView l = (ListView)findViewById(R.id.difficulty);
		CustomListViewAdapter adapter = new CustomListViewAdapter(this, items);
	    l.setAdapter(adapter);
	    
	    //set onClick Listener for ListView items, to specify selected difficulty
	    l.setClickable(true);
	    l.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	    		Intent i = new Intent(DifficultyActivity.this, LevelPickerActivity.class);
	    		i.putExtra("difficulty", position);
	    		startActivity(i);
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
