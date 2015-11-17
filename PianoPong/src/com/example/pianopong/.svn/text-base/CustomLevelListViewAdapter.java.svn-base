package com.example.pianopong;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
  
public class CustomLevelListViewAdapter extends BaseAdapter{  
	
	private LayoutInflater inflater;
	private List<ListLevelViewItem> items;
	public static MediaPlayer mp = new MediaPlayer();
	
    public CustomLevelListViewAdapter(Activity context, List<ListLevelViewItem> items) {  
        super();
        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    @Override
    public int getCount() { 
        return items.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return null;  
    }  
  
    @Override  
    public long getItemId(int position) {
        return 0;  
    }
      
    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {
    	ListLevelViewItem item = items.get(position);
    	View vi=convertView;
        
        if(convertView==null){
            vi = inflater.inflate(R.layout.item_row_level, null);
        }
        
        ImageView imgThumbnail =(ImageView)vi.findViewById(R.id.imgThumbnail);
        TextView txtItem = (TextView)vi.findViewById(R.id.txtTitle);
        TextView txtSubTitle = (TextView)vi.findViewById(R.id.txtSubTitle);
        Button b1 = (Button)vi.findViewById(R.id.button1);
        b1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mp.stop();
				Intent i = new Intent(LevelPickerActivity.c, GameActivity.class);
	            Bundle extras = new Bundle();
	            extras.putInt("level", position);
	            extras.putInt("difficulty", LevelPickerActivity.difficulty);
	            i.putExtras(extras);
	            LevelPickerActivity.c.startActivity(i);
			}
		});
        Button b2 = (Button)vi.findViewById(R.id.button2);
        b2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//background music
				mp.stop();
				mp = new MediaPlayer();
				File dir = LevelManager.mediaDir;
				File file;
				if(LevelPickerActivity.difficulty==5){
					file = new File(dir, "level"+String.valueOf(MainActivity.lm.getLevelNumber(LevelPickerActivity.difficulty, position)+1)+".mid");
				}else{
					file = new File(dir, "level"+MainActivity.lm.getLevelNumber(LevelPickerActivity.difficulty, position)+".mid");
				}
			    

			    if (file.exists()){
			        try {
			        	FileInputStream fis = new FileInputStream(file);
			            FileDescriptor fd = fis.getFD();
			            mp.setDataSource( fd);
			            mp.prepare();
			            mp.start();
			        } catch (IOException e) {
			        }
			    }
			}
		});
          
        imgThumbnail.setImageResource(item.getThumbnailResource());
        txtItem.setText(item.getTitle());
        txtSubTitle.setText(item.getSubtitle());
        
        return vi;
    }
}