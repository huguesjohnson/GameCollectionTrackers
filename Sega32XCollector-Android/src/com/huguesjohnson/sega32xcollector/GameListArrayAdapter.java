package com.huguesjohnson.sega32xcollector;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GameListArrayAdapter extends ArrayAdapter<Sega32XRecord> {
	private Context context;
	private int resourceId;
	private Sega32XRecord[] records;
	private String packageName;

	public GameListArrayAdapter(Context context,int resourceId,Sega32XRecord[] records){
		super(context,resourceId,records);
		this.context=context;
		this.resourceId=resourceId;
		this.records=records;
		this.packageName=context.getPackageName();
	}

	public void update(int index,Sega32XRecord record){
		this.records[index].setBox(record.hasBox());
		this.records[index].setGame(record.hasGame());
		this.records[index].setInstructions(record.hasInstructions());
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount(){
		return(this.records.length);
	}

	@Override
	public Sega32XRecord getItem(int position){
		return(this.records[position]);
	}
  
	@Override
	public long getItemId(int position) {
		return(position);
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		try{
			Sega32XRecord record=this.records[position];
			LayoutInflater inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view=inflater.inflate(resourceId,null);
			//title
			TextView textViewName=(TextView)view.findViewById(R.id.listitem_game_name);
			textViewName.setText(record.getTitle());
			//image
	        String imageName=record.getAndroidImageName();
			int resourceId=context.getResources().getIdentifier(imageName,"drawable",packageName);
			ImageView imageViewBox=(ImageView)view.findViewById(R.id.listitem_game_imageview_box);
			if(resourceId>0){imageViewBox.setImageResource(resourceId);}
			//icons
			ImageView imageViewHasGame=(ImageView)view.findViewById(R.id.imageview_game_havegame);
			ImageView imageViewHasBox=(ImageView)view.findViewById(R.id.imageview_game_havebox);
			ImageView imageViewHasInstructions=(ImageView)view.findViewById(R.id.imageview_game_haveinstructions);
			if(record.hasGame()){
				imageViewHasGame.setImageResource(R.drawable.icon_game_on);
			}else{
				imageViewHasGame.setImageResource(R.drawable.icon_game_off);
			}
			if(record.hasBox()){
				imageViewHasBox.setImageResource(R.drawable.icon_box_on);
			}else{
				imageViewHasBox.setImageResource(R.drawable.icon_box_off);
			}
			if(record.hasInstructions()){
				imageViewHasInstructions.setImageResource(R.drawable.icon_instructions_on);
			}else{
				imageViewHasInstructions.setImageResource(R.drawable.icon_instructions_off);
			}
			return(view);
		}catch(Exception x){
			Log.e("GameListArrayAdapter","getView, position="+position,x);
			return(null);
		}
	}
	
}
