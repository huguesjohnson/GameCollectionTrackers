/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2010-2013 Hugues Johnson

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

package com.huguesjohnson.sega32xcollector;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class Sega32XCollectorActivity extends SherlockActivity{
	private static final String TAG="Sega32XCollectorActivity";
	//constants
	private final static int MENU_HELP=0;
	private final static int MENU_EXPORT=1;
	private final static int MENU_IMPORT=2;
	private final static int MENU_ABOUT=3;
	private final static int MENU_QUIT=4;
	private final static int CONTEXT_MENU_EDIT=0;
	private final static int CONTEXT_MENU_EBAY=1;
	//UI components
	private ListView gameListView;
	//game dialog
	private AlertDialog gameDialog;
	private CheckBox haveGameCheckbox;
	private CheckBox haveBoxCheckbox;
	private CheckBox haveInstructionsCheckbox;
	//other stuff used all over the place
	private DatabaseHelper dbHelper;
	private Sega32XRecord[] activeRecords;
	private Sega32XRecord selectedGame;
	private int selectedGameIndex;
	//used to see if we need to refresh after an import
	private static boolean importFlag=false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try{
			//setup actionbar
			ActionBar actionBar=getSupportActionBar();
			actionBar.setIcon(R.drawable.banner);
			actionBar.setTitle("");
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			SpinnerAdapter spinnerAdapter=ArrayAdapter.createFromResource(this,R.array.action_list,android.R.layout.simple_dropdown_item_1line);			
			actionBar.setListNavigationCallbacks(spinnerAdapter,onNavigationListener);
			//open the database
			this.dbHelper=new DatabaseHelper(this.getApplicationContext());
			//load the view
			this.setContentView(R.layout.main);
	        this.gameListView=(ListView)this.findViewById(R.id.listview_games);
	        this.gameListView.setOnItemClickListener(selectGameListener);
	        this.gameListView.setLongClickable(true);
	        registerForContextMenu(this.gameListView);
		}catch(Exception x){
			Log.e(TAG,"onCreate()",x);
			TextView errorView=new TextView(this);
			errorView.setText(x.getMessage());
			this.setContentView(errorView);
		}
	}	
	
	@Override
	protected void onResume(){
		super.onResume();
		if(importFlag){
			//force the list to refresh
			importFlag=false;
			ActionBar actionBar=getSupportActionBar();
			setGameList(actionBar.getSelectedNavigationIndex());
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu,View view,ContextMenuInfo menuInfo){
		try{
			if(view.getId()==R.id.listview_games){
				AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)menuInfo;
				menu.setHeaderTitle(activeRecords[info.position].getTitle());
				menu.add(Menu.NONE,CONTEXT_MENU_EDIT,CONTEXT_MENU_EDIT,"Edit");
				menu.add(Menu.NONE,CONTEXT_MENU_EBAY,CONTEXT_MENU_EBAY,"Search ebay");
			}
		}catch(Exception x){
			Log.e(TAG,"onCreateContextMenu",x);
		}
	}
	
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item){
		try{
			AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			selectedGameIndex=info.position;  
			if(selectedGameIndex>=0){
				selectedGame=(Sega32XRecord)gameListView.getAdapter().getItem(selectedGameIndex);
				int menuItemIndex=item.getItemId();
				if(menuItemIndex==CONTEXT_MENU_EDIT){
					showGameDialog();
				}else if(menuItemIndex==CONTEXT_MENU_EBAY){
					EbayActivity.setSearchTerm(selectedGame.getTitle()+" 32X");
					Intent ebayIntent=new Intent(getApplicationContext(),EbayActivity.class);
					startActivityForResult(ebayIntent,0);
				}
			}
		}catch(Exception x){
			Log.e(TAG,"onContextItemSelected",x);
		}
		  return(true);
	}
	
	OnNavigationListener onNavigationListener=new OnNavigationListener(){
		  @Override
		  public boolean onNavigationItemSelected(int position,long itemId){
			  try{
				  setGameList(position);
			  }catch(Exception x){
					Log.e(TAG,"onNavigationListener.onNavigationItemSelected",x);
			  }return(true);
		  }
	};	
	
	private void setGameList(int position){
		  switch(position){
		  case 0:{activeRecords=dbHelper.getAllGames(); break;}
		  case 1:{activeRecords=dbHelper.getMyGames(); break;}
		  case 2:{activeRecords=dbHelper.getMissingGames(false,false); break;}
		  case 3:{activeRecords=dbHelper.getMissingGames(true,true); break;}
		  }
		  GameListArrayAdapter gameListAdapter=new GameListArrayAdapter(getApplicationContext(),R.layout.listitemgame,activeRecords);
		  gameListView.setAdapter(gameListAdapter);
	}
	
	
	OnItemClickListener selectGameListener=new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent,View view,int position,long id){
			try{
				selectedGameIndex=position;  
				selectedGame=(Sega32XRecord)gameListView.getAdapter().getItem(selectedGameIndex);
				showGameDialog();
			}catch(Exception x){
				Log.e(TAG,"onItemClick: position="+position,x);
				showErrorDialog(x);
			}
		}
	};
	
	private void showGameDialog(){
		try{
			//sanity check - it's probably impossible for this condition to occur
			if(this.selectedGame==null){return;}
			//create the game dialog
			if(this.gameDialog==null){
				LayoutInflater inflater=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout=inflater.inflate(R.layout.gamedialog,(ViewGroup)findViewById(R.id.layout_gamedialogroot));
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setView(layout);
				builder.setTitle(this.selectedGame.getTitle());
				builder.setPositiveButton("Save",onGameDialogSaveListener);
				builder.setNegativeButton("Cancel",onGameDialogCancelListener);
				this.haveGameCheckbox=(CheckBox)layout.findViewById(R.id.gamedialog_checkbox_game);
				this.haveBoxCheckbox=(CheckBox)layout.findViewById(R.id.gamedialog_checkbox_box);
				this.haveInstructionsCheckbox=(CheckBox)layout.findViewById(R.id.gamedialog_checkbox_instructions);
				this.gameDialog=builder.create();
			}
			//set the checkboxes
			this.haveBoxCheckbox.setChecked(this.selectedGame.hasBox());
			this.haveGameCheckbox.setChecked(this.selectedGame.hasGame());
			this.haveInstructionsCheckbox.setChecked(this.selectedGame.hasInstructions());
			//show the dialog
			this.gameDialog.setTitle(this.selectedGame.getTitle());
			this.gameDialog.show();
		}catch(Exception x){
			Log.e(TAG,"showGameDialog",x);
			showErrorDialog(x);
		}
	}

	DialogInterface.OnClickListener onGameDialogSaveListener=new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog,int which){
			try{
				selectedGame.setBox(haveBoxCheckbox.isChecked());
				selectedGame.setGame(haveGameCheckbox.isChecked());
				selectedGame.setInstructions(haveInstructionsCheckbox.isChecked());
				dbHelper.updateGame(selectedGame);
				GameListArrayAdapter adapter=(GameListArrayAdapter)gameListView.getAdapter();
				adapter.update(selectedGameIndex,selectedGame);
			}catch(Exception x){
				Log.e(TAG,"onGameDialogSaveListener.onClick: which="+which,x);
				showErrorDialog(x);
			}
		}
	};	
	
	DialogInterface.OnClickListener onGameDialogCancelListener=new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog,int which){
			//not implemented
		}
	};	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,MENU_HELP,MENU_HELP,"Help..");
		menu.add(0,MENU_EXPORT,MENU_EXPORT,"Export..");
		menu.add(0,MENU_IMPORT,MENU_IMPORT,"Import..");
		menu.add(0,MENU_ABOUT,MENU_ABOUT,"About");
		menu.add(0,MENU_QUIT,MENU_QUIT,"Quit");
		return(true);
	}

	@Override
	public boolean onMenuItemSelected(int featureId,MenuItem item){
		switch(item.getItemId()){
			case MENU_QUIT:{
				this.finish();
				break;
			}
			case MENU_ABOUT:{
               new AlertDialog.Builder(this)
               		.setTitle(R.string.app_name)
               		.setMessage(R.string.app_description)
               		.setPositiveButton("Close", null)
               		.show();
				break;
			}
			case MENU_EXPORT:{
				Intent exportIntent=new Intent(getApplicationContext(),ExportActivity.class);
				startActivityForResult(exportIntent,0);
				break;
			}
			case MENU_IMPORT:{
				importFlag=true;
				Intent importIntent=new Intent(getApplicationContext(),ImportActivity.class);
				startActivityForResult(importIntent,0);
				break;
			}
			case MENU_HELP:{
				Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(getResources().getString(R.string.help_url)));
				this.startActivity(browserIntent);
				break;
			}
		}
		return(false);
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