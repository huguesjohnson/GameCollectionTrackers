/*
TurboGrafx16Collector - Mobile application to manage a collection of TurboGrafx-16 games
Copyright (C) 2010 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.tg16collector;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class BaseActivityTab extends ListActivity{
	private final static String TAG="BaseActivityTab";
	private static DatabaseHelper dbHelper;
	private static String titleFilter;
	private TG16Record[] activeRecords;
	private GameListAdapter adapter;
	private TG16Record selectedGame;
	//game dialog
	private AlertDialog gameDialog;
	private CheckBox haveGameCheckbox;
	private CheckBox haveBoxCheckbox;
	private CheckBox haveCaseCheckbox;
	private CheckBox haveInstructionsCheckbox;
	private CheckBox onWishListCheckbox;
	//filter dialog
	private AlertDialog filterDialog;
	private EditText filterTextbox;
	//menu constants
	private final static int MENU_QUIT=0;
	private final static int MENU_ABOUT=1;
	private final static int MENU_FILTER=2;
	private final static int MENU_EXPORT=3;
	
	@Override
	protected void onResume(){
		super.onResume();
		try{
			//refresh the game list
			this.refreshGameList(); 
		}catch(Exception x){
			Log.e(TAG,"onResume",x);
			this.showErrorDialog(x);
		}
	}
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		try{
			menu.add(0,MENU_FILTER,0,"Title Filter").setIcon(R.drawable.menu_filter);
			menu.add(0,MENU_EXPORT,1,"Export List").setIcon(R.drawable.menu_export);
			menu.add(0,MENU_ABOUT,2,"About").setIcon(R.drawable.menu_about);
			menu.add(0,MENU_QUIT,3,"Quit").setIcon(R.drawable.menu_quit);
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
			case MENU_FILTER:{this.showFilterDialog(); return(true);}
			case MENU_EXPORT:{this.exportList(); return(true);}
			case MENU_ABOUT:{this.showAboutDialog(); return(true);}
			case MENU_QUIT:{this.finish(); return(true);}
			default:{return(false);}
			}
		}catch(Exception x){
			if(item!=null){
				Log.e(TAG,"onOptionsItemSelected: item.getItemId()="+item.getItemId(),x);
			}else{
				Log.e(TAG,"onOptionsItemSelected: item is null",x);
			}
			return(false);
		}
	}
	
	protected static final DatabaseHelper getDbHelper(){return(dbHelper);}
	
	OnClickListener onGameDialogSaveListener=new OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog,int which){
			try{
				selectedGame.setHaveBox(haveBoxCheckbox.isChecked());
				selectedGame.setHaveCase(haveCaseCheckbox.isChecked());
				selectedGame.setHaveGame(haveGameCheckbox.isChecked());
				selectedGame.setHaveInstructions(haveInstructionsCheckbox.isChecked());
				selectedGame.setOnWishlist(onWishListCheckbox.isChecked());
				dbHelper.updateGame(selectedGame);
			}catch(Exception x){
				Log.e(TAG,"onGameDialogSaveListener.onClick: which="+which,x);
				showErrorDialog(x);
			}
			refreshGameList();
		}
	};
	
	OnClickListener onFilterDialogCancelListener=new OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog,int which){
			if(titleFilter!=null){
				titleFilter=null;
				refreshGameList();
			}
		}
	};
	
	OnClickListener onFilterDialogPositiveListener=new OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog,int which){
			String newFilter=filterTextbox.getText().toString();
			if(!newFilter.equals(titleFilter)){
				titleFilter=newFilter;
				refreshGameList();
			}
		}
	};
	
	OnClickListener onGameDialogCancelListener=new OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog,int which){/*not implemented*/}
	};	
	
	OnItemClickListener selectGameListener=new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent,View view,int position,long id){
			try{
				int recordId=((TG16Record)adapter.getItem(position)).getRecordId();
				//get the latest data in case this was modified on a different view
				selectedGame=dbHelper.getGame(recordId);
				showGameDialog();
			}catch(Exception x){
				Log.e(TAG,"onItemClick: position="+position,x);
				showErrorDialog(x);
			}
		}
	};
	
	private void showGameDialog(){
		try{
			//I don't think this can actually happen so this is just a sanity check
			if(this.selectedGame==null){return;}
			//create the game dialog
			if(this.gameDialog==null){
				LayoutInflater inflater=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout=inflater.inflate(R.layout.gamedialog,(ViewGroup)findViewById(R.id.layout_gamedialogroot));
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setView(layout);
				builder.setTitle(this.selectedGame.getTitle());
				builder.setPositiveButton(R.string.button_save,onGameDialogSaveListener);
				builder.setNegativeButton(R.string.button_cancel,onGameDialogCancelListener);
				this.haveGameCheckbox=(CheckBox)layout.findViewById(R.id.checkbox_game);
				this.haveBoxCheckbox=(CheckBox)layout.findViewById(R.id.checkbox_box);
				this.haveCaseCheckbox=(CheckBox)layout.findViewById(R.id.checkbox_case);
				this.haveInstructionsCheckbox=(CheckBox)layout.findViewById(R.id.checkbox_instructions);
				this.onWishListCheckbox=(CheckBox)layout.findViewById(R.id.checkbox_wishlist);
				this.gameDialog=builder.create();
			}
			//set the checkboxes
			this.haveBoxCheckbox.setChecked(this.selectedGame.isHaveBox());
			this.haveCaseCheckbox.setChecked(this.selectedGame.isHaveCase());
			this.haveGameCheckbox.setChecked(this.selectedGame.isHaveGame());
			this.haveInstructionsCheckbox.setChecked(this.selectedGame.isHaveInstructions());
			this.onWishListCheckbox.setChecked(this.selectedGame.isOnWishlist());
			//show the dialog
			this.gameDialog.setTitle(this.selectedGame.getTitle());
			this.gameDialog.show();
		}catch(Exception x){
			Log.e(TAG,"showGameDialog",x);
		}
	}
	
	private void showAboutDialog(){
		try{
			new AlertDialog.Builder(this)
	   		.setTitle(R.string.app_name)
	   		.setMessage(R.string.app_description)
	   		.setPositiveButton("Close", null)
	   		.show();	
		}catch(Exception x){
			Log.e(TAG,"showAboutDialog",x);
		}
	}

	private void showErrorDialog(Exception x){
		try{
	        new AlertDialog.Builder(this)
	   		.setTitle(R.string.app_name)
	   		.setMessage(x.getMessage())
	   		.setPositiveButton("Close", null)
	   		.show();	
		}catch(Exception reallyBadTimes){
			Log.e(TAG,"showErrorDialog",reallyBadTimes);
		}
	}
	
	private void showFilterDialog(){
		try{
			//create the dialog
			if(this.filterDialog==null){
				LayoutInflater inflater=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout=inflater.inflate(R.layout.filterdialog,(ViewGroup)findViewById(R.id.layout_filterdialogroot));
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setView(layout);
				builder.setTitle(R.string.filterdialog_title);
				builder.setPositiveButton(R.string.button_filter,onFilterDialogPositiveListener);
				builder.setNegativeButton(R.string.button_clearfilter,onFilterDialogCancelListener);
				this.filterTextbox=(EditText)layout.findViewById(R.id.edittext_titlefilter);
				this.filterDialog=builder.create();
			}
			this.filterTextbox.setText(titleFilter);
			//show the dialog
			this.filterDialog.show();
		}catch(Exception x){
			Log.e(TAG,"showFilterDialog",x);
		}
	}
	
	private void exportList(){
		try{
			ArrayList<String> export=this.getExportList();
			String state=Environment.getExternalStorageState();
			if(state.equals(Environment.MEDIA_MOUNTED)){
			    //export the list
				File dir=new File(Environment.getExternalStorageDirectory().getPath()+"/TG16Collector");
				if(!dir.exists()){
					dir.mkdir();
				}
				String fileName=((TabActivity)this.getParent()).getTabHost().getCurrentTabTag()+".csv";
				FileOutputStream fout=new FileOutputStream(dir.getPath()+"/"+fileName);
				Iterator<String> iterator=export.iterator();
				while(iterator.hasNext()){
					fout.write(iterator.next().getBytes());
				}
				fout.flush();
				fout.close();
		        new AlertDialog.Builder(this)
		   		.setTitle(R.string.app_name)
		   		.setMessage("Exported list to "+dir.getPath()+"/"+fileName)
		   		.setPositiveButton("Close", null)
		   		.show();	
			}else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
				throw(new Exception("Error exporting list - SD card is read-only. [state=MEDIA_MOUNTED_READ_ONLY]"));
			}else if(Environment.MEDIA_CHECKING.equals(state)){
				throw(new Exception("Error exporting list - SD card is being disk-checked. [state=MEDIA_CHECKING]"));
			}else if(Environment.MEDIA_BAD_REMOVAL.equals(state)){
				throw(new Exception("Error exporting list - SD card was removed before it was unmounted. [state=MEDIA_BAD_REMOVAL]"));
			}else if(Environment.MEDIA_NOFS.equals(state)){
				throw(new Exception("Error exporting list - SD card is blank or is using an unsupported filesystem. [state=MEDIA_NOFS]"));
			}else if(Environment.MEDIA_REMOVED.equals(state)){
				throw(new Exception("Error exporting list - SD card is missing. [state=MEDIA_REMOVED]"));
			}else if(Environment.MEDIA_SHARED.equals(state)){
				throw(new Exception("Error exporting list - SD card is shared. If you are connected to a PC via USB please disconnect and try again. [state=MEDIA_SHARED]"));
			}else if(Environment.MEDIA_UNMOUNTABLE.equals(state)){
				throw(new Exception("Error exporting list - SD card is present but cannot be mounted. [state=MEDIA_UNMOUNTABLE]"));
			}else if(Environment.MEDIA_UNMOUNTED.equals(state)){
				throw(new Exception("Error exporting list - SD card is not mounted. [state=MEDIA_UNMOUNTED]"));
			}else{
				throw(new Exception("Error exporting list - an undefined error occurred. [state="+state+"]"));
			}
		}catch(Exception x){
			Log.e(TAG,"exportList",x);
			this.showErrorDialog(x);
		}
	}
	
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		try{
			if(dbHelper==null){dbHelper=new DatabaseHelper(this.getApplicationContext());}
			ListView listView=this.getListView();
			listView.setTextFilterEnabled(true);
			listView.setOnItemClickListener(selectGameListener);
		}catch(Exception x){
			Log.e(TAG,"onCreate",x);
			this.showErrorDialog(x);
		}
    }
	
	protected TG16Record[] arrayListToArray(ArrayList<TG16Record> arrayList){
		return((TG16Record[])arrayList.toArray(new TG16Record[arrayList.size()]));
	}
	
	protected String getTitleFilter(){
		return(titleFilter);
	}
	
	private void refreshGameList(){
		try{
			this.activeRecords=this.arrayListToArray(this.getGameList());
			this.adapter=new GameListAdapter(this,R.layout.listitem,this.activeRecords);
			this.setListAdapter(this.adapter);
		}catch(Exception x){
			Log.e(TAG,"refreshGameList",x);
			this.showErrorDialog(x);
		}
	}
	
	public abstract ArrayList<TG16Record> getGameList();

	public abstract ArrayList<String> getExportList();
}
