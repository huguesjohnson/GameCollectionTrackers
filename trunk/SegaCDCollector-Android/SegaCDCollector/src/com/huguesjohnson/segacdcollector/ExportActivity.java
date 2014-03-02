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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ExportActivity extends Activity{
	private static final String TAG="ExportActivity";
	//menu constants
	private final static int MENU_BACK=0;
	private final static int MENU_HELP=1;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		ActionBar actionBar=getActionBar();
		actionBar.setIcon(R.drawable.banner);
		actionBar.setTitle("");
		this.setContentView(R.layout.exportlayout);
		//file name
		EditText editTextExportFileName=(EditText)this.findViewById(R.id.editTextExportFileName);
		editTextExportFileName.setText(getResources().getString(R.string.export_file_name)+getTimeStamp());
		//cancel button
		Button cancelButton=(Button)this.findViewById(R.id.buttonExportCancel);
		cancelButton.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v){
						finish();
					}
				}
		);
		//export button
		Button exportButton=(Button)this.findViewById(R.id.buttonExport);
		exportButton.setOnClickListener(
				new OnClickListener(){
					@Override
					public void onClick(View v){
						exportCollection();
					}
				}
		);

	}

	private static String getTimeStamp(){
		Calendar calendar=Calendar.getInstance();
		//brute-forcing this after getting tired of debugging SimpleDateFormat exceptions - whoever designed date handling in Java should never be allowed near a keyboard again
		StringBuilder dateString=new StringBuilder();
		dateString.append(calendar.get(Calendar.YEAR));
		dateString.append("-");
		dateString.append(calendar.get(Calendar.MONTH)+1); //see previous statement about the designer of the Java date/time APIs
		dateString.append("-");
		dateString.append(calendar.get(Calendar.DAY_OF_MONTH));
		return(dateString.toString());
	}
	
	private void exportCollection(){
		EditText editTextExportFileName=(EditText)this.findViewById(R.id.editTextExportFileName);
		//if more export formats are supported in the future this extension would need to change
		String destinationFileName=editTextExportFileName.getText()+".32x.json";
		try{
			if(ImportExportUtils.checkMediaState()){
				DatabaseHelper dbHelper=new DatabaseHelper(this.getApplicationContext());
				ArrayList<SegaCDRecord> records=dbHelper.getAllGames();
				String exportString=ImportExportUtils.gameListToJson(records);
				File dir=new File(Environment.getExternalStorageDirectory().getPath()+"/"+getResources().getString(R.string.app_name));
				if(!dir.exists()){
					dir.mkdir();
				}
				String filePath=dir.getPath()+"/"+destinationFileName;
				File f=new File(filePath);
				if(!f.exists()){
					f.createNewFile();
				}
				FileOutputStream fout=new FileOutputStream(f);
				fout.write(exportString.getBytes());
				fout.close();
		        finish();
	        }
	      }catch(Exception x){
	    	  Log.e(TAG,"exportCollection",x);
	    	  showErrorDialog(x);
	      }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		try{
			menu.add(0,MENU_BACK,0,"Back");
			menu.add(0,MENU_HELP,MENU_HELP,"Help..");
			return(true);
		}catch(Exception x){
			Log.e(TAG,"onCreateOptionsMenu",x);
			return(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		try{
			switch(item.getItemId()){
			case MENU_BACK:{this.finish();break;}
			case MENU_HELP:{
				Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(getResources().getString(R.string.help_url)));
				this.startActivity(browserIntent);
				break;
			}
			default:{return(false);}
			}
			return(true);
		}catch(Exception x){
			Log.e(TAG,"onOptionsItemSelected",x);
			return(false);
		}
	}
	
	private void showErrorDialog(Exception x){
		try{
	        new AlertDialog.Builder(this)
	   		.setTitle(R.string.app_name)
	   		.setMessage(x.getMessage())
	   		.setPositiveButton("Close",null)
	   		.show();	
		}catch(Exception reallyBadTimes){
			Log.e(TAG,"showErrorDialog",reallyBadTimes);
		}
	}	
}