/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
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

package com.huguesjohnson.sega32xcollector;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class Sega32XCollectorActivity extends Activity{
	//constants
	private final static int MENU_QUIT=0;
	private final static int MENU_ABOUT=1;
	//UI components
	private ListView gameListView;
	private Spinner selectViewSpinner;
	private CheckBox haveGameCheckbox;
	private CheckBox haveBoxCheckbox;
	private CheckBox haveInstructionsCheckbox;
	//other stuff used all over the place
	private DatabaseHelper dbHelper;
	private ArrayList<Sega32XRecord> activeRecords;
	private Sega32XRecord selectedGame;
	private boolean onGameViewPage=false;
	private int selectViewSpinnerIndex=0;

	OnClickListener backButtonListener=new OnClickListener(){
		@Override
		public void onClick(View view){
			saveActiveGame();
			loadMainView();
		}
	};
		
	OnItemClickListener selectGameListener=new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent,View view,int position,long id){
			selectedGame=(Sega32XRecord)gameListView.getAdapter().getItem(position);
			loadGameView();
		}
	};
	
	OnItemSelectedListener selectViewListener=new OnItemSelectedListener(){           
		@Override
		   public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
			   selectViewSpinnerIndex=selectViewSpinner.getSelectedItemPosition();
			   String selectedText=(String)selectViewSpinner.getSelectedItem();
			   if(selectedText.equals(parent.getContext().getResources().getString(R.string.option_allgames))){
					activeRecords=dbHelper.getAllGames();
			   } else if(selectedText.equals(parent.getContext().getResources().getString(R.string.option_mygames))){
					activeRecords=dbHelper.getMyGames();
			   } else if(selectedText.equals(parent.getContext().getResources().getString(R.string.option_mymissinggames))){
					activeRecords=dbHelper.getMissingGames(false,false);
			   } else if(selectedText.equals(parent.getContext().getResources().getString(R.string.option_mymissingboxesinstructions))){
					activeRecords=dbHelper.getMissingGames(true,true);
			   }
			   ArrayAdapter<Sega32XRecord> gameListAdapter=new ArrayAdapter<Sega32XRecord>(parent.getContext(),android.R.layout.simple_gallery_item,(Sega32XRecord[])activeRecords.toArray(new Sega32XRecord[activeRecords.size()]));
			   gameListView.setAdapter(gameListAdapter);
		   }
		   
		   public void onNothingSelected(AdapterView<?> parent){ /* not implemented */}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try{
			//open the database
			this.dbHelper=new DatabaseHelper(this.getApplicationContext());
			this.loadMainView();
		}catch(Exception x){
			TextView errorView=new TextView(this);
			errorView.setText(x.getMessage());
			this.setContentView(errorView);
		}
	}

	private void loadMainView(){
		this.onGameViewPage=false;
		this.setContentView(R.layout.main);
        //setup game list
        Sega32XRecord[] records=new Sega32XRecord[1];
        records[0]=new Sega32XRecord("Loading game list...");
        ArrayAdapter<Sega32XRecord> gameListAdapter=new ArrayAdapter<Sega32XRecord>(this,android.R.layout.simple_gallery_item,records);
        this.gameListView=(ListView)this.findViewById(R.id.listview_games);
        this.gameListView.setAdapter(gameListAdapter);
        this.gameListView.setOnItemClickListener(selectGameListener);
        //setup the drop-down list
        String[] selections=new String[]{
        		this.getResources().getString(R.string.option_allgames),
        		this.getResources().getString(R.string.option_mygames),
        		this.getResources().getString(R.string.option_mymissinggames),
        		this.getResources().getString(R.string.option_mymissingboxesinstructions)};
        ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,selections);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.selectViewSpinner=(Spinner)findViewById(R.id.spinner_selectview);
        this.selectViewSpinner.setAdapter(spinnerAdapter);
        this.selectViewSpinner.setOnItemSelectedListener(selectViewListener);
        this.selectViewSpinner.setSelection(this.selectViewSpinnerIndex);
	}
	
	private void loadGameView(){
		this.onGameViewPage=true;
		this.setContentView(R.layout.gameview);
		//set title
		TextView title=(TextView)this.findViewById(R.id.textview_gametitle);
		title.setText(this.selectedGame.getTitle());
		//set image
		ImageView boxImage=(ImageView)this.findViewById(R.id.imageview_box);
		String imageName=this.selectedGame.getAndroidImageName();
		String packageName=this.getPackageName();
		int resourceId=this.getResources().getIdentifier(imageName,"drawable",packageName);
		boxImage.setImageResource(resourceId);
		//set checkboxes
		this.haveGameCheckbox=(CheckBox)this.findViewById(R.id.checkbox_game);
		this.haveGameCheckbox.setChecked(this.selectedGame.hasGame());
		this.haveBoxCheckbox=(CheckBox)this.findViewById(R.id.checkbox_box);
		this.haveBoxCheckbox.setChecked(this.selectedGame.hasBox());
		this.haveInstructionsCheckbox=(CheckBox)this.findViewById(R.id.checkbox_instructions);
		this.haveInstructionsCheckbox.setChecked(this.selectedGame.hasInstructions());
		//set back button action
		Button backButton=(Button)this.findViewById(R.id.button_backtogamelist);
		backButton.setOnClickListener(backButtonListener);
	}
	
	private void saveActiveGame(){
		try{
			selectedGame.setGame(haveGameCheckbox.isChecked());
			selectedGame.setBox(haveBoxCheckbox.isChecked());
			selectedGame.setInstructions(haveInstructionsCheckbox.isChecked());
			dbHelper.updateGame(selectedGame);
		}catch(Exception x){
			TextView errorView=new TextView(this);
			errorView.setText(x.getMessage());
			this.setContentView(errorView);
		}	
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed(){
		if(this.onGameViewPage){
			this.saveActiveGame();
			this.loadMainView();
		}else{
			super.onBackPressed();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,MENU_QUIT,0,"Quit");
		menu.add(0,MENU_ABOUT,1,"About");
		return(true);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
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
		}
		return(false);
	}
}