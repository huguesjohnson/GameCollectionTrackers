/*
Imported from: Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2010-2014 Hugues Johnson

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

package com.huguesjohnson.segacdcollector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ImportFileArrayAdapter extends ArrayAdapter<File>{
	private Context context;
	private int resourceId;
	private ArrayList<File> fileList;
	
	public ImportFileArrayAdapter(Context context,int resourceId,ArrayList<File> fileList){
		super(context,resourceId,fileList);
		this.context=context;
		this.resourceId=resourceId;
		this.fileList=fileList;
	}
	
	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		try{
			LayoutInflater inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout=inflater.inflate(resourceId,null);
			File f=this.fileList.get(position);
			TextView fileName=(TextView)layout.findViewById(R.id.listitemImportfileFilename);
			fileName.setText(f.getName());
			TextView date=(TextView)layout.findViewById(R.id.listitemImportfileDate);
			String simpleDate=new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(f.lastModified());
			date.setText(simpleDate);
			return(layout);
		}catch(Exception x){
			Log.e("SavedEpisodeListArrayAdapter.getView",x.getMessage(),x);
			return(null);
		}
	}
}