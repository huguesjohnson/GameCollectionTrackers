/*
Imported from: Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2010-2014 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
