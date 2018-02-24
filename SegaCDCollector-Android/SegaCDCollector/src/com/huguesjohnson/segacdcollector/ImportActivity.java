/*
Imported from: Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2010-2014 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.segacdcollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ImportActivity extends Activity{
	private final static String TAG="ImportActivity";
	//menu constants
	private final static int MENU_BACK=0;
	private final static int MENU_HELP=1;
	//reference to the files being displayed
	private ArrayList<File> fileList;
	private int selectedIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try{
			ActionBar actionBar=getActionBar();
			actionBar.setIcon(R.drawable.banner);
			actionBar.setTitle("");
			this.setContentView(R.layout.importlayout);
	        ListView listViewImportFiles=(ListView)findViewById(R.id.listViewImportFiles);
	        listViewImportFiles.setOnItemClickListener(fileClickListener);
	        registerForContextMenu(listViewImportFiles);
		}catch(Exception x){
			Log.e(TAG,"onCreate");
			this.showErrorDialog(x);
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
	
	OnItemClickListener fileClickListener=new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent,View view,int position,long id){
			try{
				selectedIndex=position;
				confirmImport();
			}catch(Exception x){
				Log.e(TAG,"fileClickListener: position="+position,x);
				showErrorDialog(x);
			}
		}
	};
	
	private void confirmImport(){
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setTitle("Confirm import");
		builder.setMessage("Importing this file will overwrite your existing collection. This can not be undone. Do you wish to continue?");
		builder.setCancelable(false);
		builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int buttonId){
	        		//not implemented
	        }});
		builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int buttonId){
	        	importCollection();
	        }});
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.show();
	}
	
	private void importCollection(){
		File importFile=this.fileList.get(this.selectedIndex);
		try{
			if(ImportExportUtils.checkMediaState()){
				FileInputStream fin=new FileInputStream(importFile.getPath());
				StringBuilder builder=new StringBuilder();
				int character;
				while((character=fin.read())!= -1){
				    builder.append((char)character);
				}
				ArrayList<SegaCDRecord> records=ImportExportUtils.jsonStringToRecords(builder.toString());
				DatabaseHelper dbHelper=new DatabaseHelper(this.getApplicationContext());
				int length=records.size();
				for(int index=0;index<length;index++){
					dbHelper.updateGame(records.get(index));
				}
				this.finish();
			}
		}catch(Exception x){
			Log.e(TAG,"importCollection",x);
		   this.showErrorDialog(x);
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		try{
			this.refreshFileList();
		}catch(Exception x){
			Log.e(TAG,"onResume",x);
			this.showErrorDialog(x);
		}
	}
	
	private void refreshFileList(){
		this.fileList=new ArrayList<File>();
		ListView listViewImportFiles=(ListView)findViewById(R.id.listViewImportFiles);
		try{
			if(ImportExportUtils.checkMediaState()){
				File sdRoot=new File(Environment.getExternalStorageDirectory().getPath());
				if((sdRoot==null)||(!sdRoot.isDirectory())){
					listViewImportFiles.setVisibility(View.GONE);
					throw(new Exception("SD card is not ready."));
				}
				File[] sdRootFiles=sdRoot.listFiles(new FilenameFilter(){
					public boolean accept(File fullPath,String fileName){
						if(fileName.endsWith(".32x.json")/*||fileName.endsWith(".32x.xml")*/){
							return(true);
						}
						return(false);
					}
				});
				if((sdRootFiles!=null)&&(sdRootFiles.length>0)){
					this.fileList.addAll(Arrays.asList(sdRootFiles));
				}
				File appFolder=new File(Environment.getExternalStorageDirectory().getPath()+"/"+getResources().getString(R.string.app_name));
				File[] appFolderFiles=appFolder.listFiles(new FilenameFilter(){
					public boolean accept(File fullPath,String fileName){
						if(fileName.endsWith(".32x.json")/*||fileName.endsWith(".32x.xml")*/){
							return(true);
						}
						return(false);
					}
				});
				if((appFolderFiles!=null)&&(appFolderFiles.length>0)){
					this.fileList.addAll(Arrays.asList(appFolderFiles));
				}
			}
			//did we find anything?
			if(this.fileList.size()<1){
				listViewImportFiles.setVisibility(View.GONE);
				TextView textViewImportError=(TextView)this.findViewById(R.id.textViewImportError);
				textViewImportError.setText("No import records found.");
				textViewImportError.setVisibility(View.VISIBLE);
			}else{
				listViewImportFiles.setVisibility(View.VISIBLE);
				ImportFileArrayAdapter adapter=new ImportFileArrayAdapter(this.getApplicationContext(),R.layout.importfilelistitem,this.fileList);
				listViewImportFiles.setAdapter(adapter);
			}
		}catch(Exception x){
			Log.e(TAG,"refreshFileList",x);
			this.showErrorDialog(x);
		}
	}
	
	private void showErrorDialog(Exception x){
		try{
	        new AlertDialog.Builder(this)
	   		.setTitle(R.string.app_name)
	   		.setMessage(x.getMessage())
	   		.setPositiveButton("Close",null)
	   		.show();	
			TextView textViewImportError=(TextView)this.findViewById(R.id.textViewImportError);
			textViewImportError.setText(x.getMessage());
			textViewImportError.setVisibility(View.VISIBLE);
		}catch(Exception reallyBadTimes){
			Log.e(TAG,"showErrorDialog",reallyBadTimes);
		}
	}	
}
