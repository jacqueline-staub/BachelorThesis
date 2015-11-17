package com.example.pianopong;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

public class AcknowledgementActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//turn off the title, full screen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_acknowledgement);
		
		//load acks from Assets
		List<ListViewItem> items = loadAcks();
				
		//setting of acks in GUI
		ListView l = (ListView)findViewById(R.id.acks);
		CustomListViewAdapter adapter = new CustomListViewAdapter(this, items);
	    l.setAdapter(adapter);
	}
	
	private ArrayList<ListViewItem> loadAcks(){
		AssetManager am = getAssets();
		ArrayList<ListViewItem> ack = new ArrayList<ListViewItem>();
		try{
			InputStream inputStream = am.open("ack/ack.txt");
	        
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	        String line = reader.readLine();
	        
	        while (line != null) {
	        	String[] test = line.split("-");
	        	ListViewItem item = new ListViewItem(R.drawable.flower, test[0], test[1]);        	
	        	ack.add(item);
	        	line = reader.readLine();
	        }
		}catch(Exception e){
		}
		return ack;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
