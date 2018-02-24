/*
SegaCDCollector - Mobile application to manage a collection of Sega CD games
Copyright (C) 2010-2014 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.segacdcollector;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameListAdapter extends ArrayAdapter<SegaCDRecord>{
	private Context context;
	private ArrayList<SegaCDRecord> records;
	private int resourceId;
	
	public GameListAdapter(Context context,int resourceId,ArrayList<SegaCDRecord> records){
		super(context,resourceId,records);
		this.context=context;
		this.records=records;
		this.resourceId=resourceId;
	}
	
	public void update(int index,SegaCDRecord record){
		this.records.set(index,record);
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		LayoutInflater inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout=inflater.inflate(resourceId,null);
		TextView text=(TextView)layout.findViewById(R.id.listitem_text);
		SegaCDRecord record=this.records.get(position);
		text.setText(record.getTitle());
		ImageView icon=(ImageView)layout.findViewById(R.id.listitem_gameicon); 
		if(record.hasGame()){
			icon.setImageResource(R.drawable.listview_game_color);  
		}else{
			icon.setImageResource(R.drawable.listview_game_gray);  
		}
		icon=(ImageView)layout.findViewById(R.id.listitem_boxicon); 
		if(record.boxAvailable()){
			if(record.hasBox()){
				icon.setImageResource(R.drawable.listview_box_color);  
			}else{
				icon.setImageResource(R.drawable.listview_box_gray);  
			}
		}else{
			icon.setVisibility(View.GONE);
		}
		icon=(ImageView)layout.findViewById(R.id.listitem_caseicon); 
		if(record.hasCase()){
			icon.setImageResource(R.drawable.listview_case_color);  
		}else{
			icon.setImageResource(R.drawable.listview_case_gray);  
		}
		icon=(ImageView)layout.findViewById(R.id.listitem_instructionsicon); 
		if(record.hasInstructions()){
			icon.setImageResource(R.drawable.listview_instructions_color);  
		}else{
			icon.setImageResource(R.drawable.listview_instructions_gray);  
		}
//TODO re-enable if you figure out what to do about this feature in the future
//		icon=(ImageView)layout.findViewById(R.id.listitem_wishlisticon); 
//		if(record.isOnWishlist()){
//			icon.setImageResource(R.drawable.listview_wishlist_color);  
//		}else{
//			icon.setImageResource(R.drawable.listview_wishlist_gray);  
//		}
		return(layout);  	
	}
}
