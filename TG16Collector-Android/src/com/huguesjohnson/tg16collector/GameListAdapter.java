/*
TurboGrafx16Collector - Mobile application to manage a collection of TurboGrafx-16 games
Copyright (C) 2010 Hugues Johnson

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software 
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
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