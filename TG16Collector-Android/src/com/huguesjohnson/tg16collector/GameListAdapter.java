/*
TurboGrafx16Collector - Mobile application to manage a collection of TurboGrafx-16 games
Copyright (C) 2010 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.tg16collector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameListAdapter extends ArrayAdapter<TG16Record>{
	private Context context;
	private TG16Record[] records;
	private int resourceId;
	
	public GameListAdapter(Context context,int resourceId,TG16Record[] records){
		super(context,resourceId,records);
		this.context=context;
		this.records=records;
		this.resourceId=resourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		LayoutInflater inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout=inflater.inflate(resourceId,null);
		TextView text=(TextView)layout.findViewById(R.id.listitem_text);
		TG16Record record=this.records[position];
		text.setText(record.getTitle());
		ImageView icon=(ImageView)layout.findViewById(R.id.listitem_gameicon); 
		if(record.isHaveGame()){
			icon.setImageResource(R.drawable.listview_game_color);  
		}else{
			icon.setImageResource(R.drawable.listview_game_gray);  
		}
		icon=(ImageView)layout.findViewById(R.id.listitem_boxicon); 
		if(record.isHaveBox()){
			icon.setImageResource(R.drawable.listview_box_color);  
		}else{
			icon.setImageResource(R.drawable.listview_box_gray);  
		}
		icon=(ImageView)layout.findViewById(R.id.listitem_caseicon); 
		if(record.isHaveCase()){
			icon.setImageResource(R.drawable.listview_case_color);  
		}else{
			icon.setImageResource(R.drawable.listview_case_gray);  
		}
		icon=(ImageView)layout.findViewById(R.id.listitem_instructionsicon); 
		if(record.isHaveInstructions()){
			icon.setImageResource(R.drawable.listview_instructions_color);  
		}else{
			icon.setImageResource(R.drawable.listview_instructions_gray);  
		}
		icon=(ImageView)layout.findViewById(R.id.listitem_wishlisticon); 
		if(record.isOnWishlist()){
			icon.setImageResource(R.drawable.listview_wishlist_color);  
		}else{
			icon.setImageResource(R.drawable.listview_wishlist_gray);  
		}
		return(layout);  	
	}
}
