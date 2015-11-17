package com.example.pianopong;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StoreActivity extends Activity {
	
	private TextView score;
	private Token token;
	private int numExtras;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//turn off the title, full screen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_store);
		
		token = new Token(this);
		
		score = (TextView)findViewById(R.id.points);
		score.setText("Points: "+token.getPoints());
		
		//get the number of extras and load them
		numExtras = MainActivity.lm.getNumExtras();
		lv = (ListView)findViewById(R.id.store);
		
		refreshListView();
		
	}
	
	protected void refreshListView() {
		//setting of levels in GUI
				List<ListViewItem> list = new ArrayList<ListViewItem>();
				for (int i = 1; i <= numExtras; ++i) {
					int icon = 0;
					//if allready bought
					if(!MainActivity.lm.notYetBought("e"+String.valueOf(89+i)+".txt")){
						icon = R.drawable.starbought;
					}else{
						if(token.getPoints()<i*100){//not enough money to purchase this extra
							icon = R.drawable.stargrey;
						}else{//enough money to buy it
							icon = R.drawable.star;
						}
					}
					list.add(new ListViewItem(icon, "Extra "+String.valueOf(i), "cost "+String.valueOf(i*100)));
				}
				
				CustomListViewAdapter adapter = new CustomListViewAdapter(this, list);
			    lv.setAdapter(adapter);
			    
			    //set onClick Listener for ListView items, to specify selected level
			    lv.setClickable(true);
			    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			        @Override
			        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			        	
			        	//not yet bought
			        	if(MainActivity.lm.notYetBought("e"+String.valueOf(90+position)+".txt")){
			        		//is it usable?
				        	if(token.getPoints()>=position*100+100){
				        		//noch nicht gekauft
				        		token.payForExtra(position*100+100);
				        		score.setText("Points: "+token.getPoints());
				        		
				        		//create a midi file for this extra
				        		MainActivity.lm.writeMidis(MainActivity.lm.getLevelNumber(5, position)+1);
				        		
				        		//lade extralevel auf sdkarte um ihn für später verfügbar zu machen
				        		MainActivity.lm.writeExtraToSD("e"+String.valueOf(90+position)+".txt");
				        		
				        		//update ListView, to indicate, that the item was bought
				        		refreshListView();
				        		
				        		//informiere user, dass er das lied gekauft hat und ihm jetzt zur verfügung steht
				        		Toast.makeText(StoreActivity.this, "successfully added extra #"+String.valueOf(position+1)+" to the library", Toast.LENGTH_SHORT).show();
				        	}else{
				        		//informiere user, dass er nicht genug geld hat
				        		Toast.makeText(StoreActivity.this, "not enough points to buy this extra", Toast.LENGTH_SHORT).show();
				        	}
			        	}else{
			        		//informiere user, dass er das extra schon gekauft hat
			        		Toast.makeText(StoreActivity.this, "you already bought this extra", Toast.LENGTH_SHORT).show();
			        	}
			        }
			    });

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}
