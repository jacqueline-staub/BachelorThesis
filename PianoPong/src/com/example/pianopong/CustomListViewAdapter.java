package com.example.pianopong;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
  
public class CustomListViewAdapter extends BaseAdapter{  
	
	private LayoutInflater inflater;
	private List<ListViewItem> items;
	
    public CustomListViewAdapter(Activity context, List<ListViewItem> items) {  
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
    	ListViewItem item = items.get(position);
    	View vi=convertView;
        
        if(convertView==null){
            vi = inflater.inflate(R.layout.item_row, null);
        }
        
        ImageView imgThumbnail =(ImageView)vi.findViewById(R.id.imgThumbnail);
        TextView txtItem = (TextView)vi.findViewById(R.id.txtTitle);
        TextView txtSubTitle = (TextView)vi.findViewById(R.id.txtSubTitle);
          
        imgThumbnail.setImageResource(item.getThumbnailResource());
        txtItem.setText(item.getTitle());
        txtSubTitle.setText(item.getSubtitle());
        
        return vi;
    }
}