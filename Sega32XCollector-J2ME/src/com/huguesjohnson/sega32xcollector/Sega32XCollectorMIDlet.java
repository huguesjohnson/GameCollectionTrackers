/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2009 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.sega32xcollector;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

import com.huguesjohnson.sega32xcollector.forms.GameForm;
import com.huguesjohnson.sega32xcollector.forms.HomeForm;
import com.huguesjohnson.sega32xcollector.forms.MainMenuForm;

/**
 * Sega32XCollectorMIDlet
 * @author Hugues Johnson
 */
public class Sega32XCollectorMIDlet extends MIDlet implements CommandListener{
	private final static String RS_NAME="32XCollection";
	private RecordStore rs;
	private MainMenuForm mainMenu;
	private Sega32XGameList gameList;
	
	public Sega32XCollectorMIDlet(){

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.midlet.MIDlet#startApp()
	 */
	protected void startApp() throws MIDletStateChangeException{
		Display display=Display.getDisplay(this);
		HomeForm form=new HomeForm();
		form.setCommandListener(this);
		display.setCurrent(form);
		form.setProgress("Looking for record store",RS_NAME);
		try{
			this.rs=RecordStore.openRecordStore(RS_NAME,true);
			if(this.rs.getNumRecords()==0){ 
				form.setProgress("Creating record store",RS_NAME);
				this.initializeRecordSet(); 
				form.setProgress("Created record store",RS_NAME);
			} else{
				form.setProgress("Found record store",RS_NAME);
			}
			this.mainMenu=new MainMenuForm();
			this.mainMenu.setCommandListener(this);
			display.setCurrent(this.mainMenu);
		} catch(RecordStoreFullException e){
			form.setProgress("RecordStoreFullException",e.getMessage());
		} catch(RecordStoreNotFoundException e){
			form.setProgress("RecordStoreNotFoundException",e.getMessage());
		} catch(RecordStoreException e){
			form.setProgress("RecordStoreFullException",e.getMessage());
		} catch(IOException e){
			form.setProgress("IOException",e.getMessage());
		}
	}
	
	private void initializeRecordSet() throws IOException, RecordStoreNotOpenException, RecordStoreException, RecordStoreException{
		InputStream in=this.getClass().getResourceAsStream("/GameList.txt");
		byte[] b=new byte[1024];
		in.read(b,0,1024);
		in.close();
		String gameList=new String(b);
		int indexOf=gameList.indexOf("|");
		int startIndex=0;
		while(indexOf>-1){
			String currentGame=gameList.substring(startIndex,indexOf);
			Sega32XRecord record=new Sega32XRecord(currentGame);
			byte[] tobytes=record.toBytes();
			this.rs.addRecord(tobytes,0,tobytes.length);
			startIndex=indexOf+1;
			indexOf=gameList.indexOf("|",startIndex);
		}
		//last record
		Sega32XRecord record=new Sega32XRecord(gameList.substring(startIndex).trim());
		byte[] tobytes=record.toBytes();
		this.rs.addRecord(tobytes,0,tobytes.length);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.midlet.MIDlet#destroyApp(boolean)
	 */
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException{
		if(this.rs!=null){
			try{
				this.rs.closeRecordStore();
			} 
			catch(RecordStoreNotOpenException e){/* not implemented */} 
			catch(RecordStoreException e){/* not implemented */}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.midlet.MIDlet#pauseApp()
	 */
	protected void pauseApp(){}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command command,Displayable displayable){
		switch(command.getCommandType()){
		case Command.EXIT:{ this.notifyDestroyed(); break; }
		case Command.OK: {
			//where is this command coming from?
			if(displayable instanceof MainMenuForm){
				this.loadGameList();
			} else if(displayable instanceof Sega32XGameList){
				Sega32XGameList list=(Sega32XGameList)displayable;
				GameForm gameForm=new GameForm(list.getSelectedGame());
				gameForm.setCommandListener(this);
				Display.getDisplay(this).setCurrent(gameForm);
			}
			break;
		}
		case Command.BACK:{
			//where is this command coming from?
			if(displayable instanceof Sega32XGameList){
				Display.getDisplay(this).setCurrent(this.mainMenu);
			} else if(displayable instanceof GameForm){
				//check if the current record was updated
				Sega32XRecord record=((GameForm)displayable).getRecord();
				if(record.getDirtyFlag()){
					byte[] bytes=record.toBytes();
					try{
						this.rs.setRecord(record.getRecordId(),bytes,0,bytes.length);
					} catch(RecordStoreNotOpenException e){/* not implemented */}
					catch(InvalidRecordIDException e){/* not implemented */}
					catch(RecordStoreFullException e){/* not implemented */}
					catch(RecordStoreException e){/* not implemented */}
					this.loadGameList();
				} else{
					Display.getDisplay(this).setCurrent(this.gameList);
				}
				//select the game if it's still in the list
				if(this.gameList.size()>0){
					this.gameList.setSelectedGame(record);
				}
			}
			break; 
		}
		case Command.SCREEN:{
			if(displayable instanceof Sega32XGameList){
				Sega32XGameList list=(Sega32XGameList)displayable;
				GameForm gameForm=new GameForm(list.getSelectedGame());
				gameForm.setCommandListener(this);
				Display.getDisplay(this).setCurrent(gameForm);
			}
			break;
		}
		}
	}
	
	private void loadGameList(){
		String title=this.mainMenu.getSelectedText();
		this.gameList=new Sega32XGameList(title);
		//these are initialized to true -- or "All 32X games"
		boolean myGames=true;
		boolean missingGames=true;
		boolean missingBoxes=true;
		boolean missingInstructions=true;
		int selection=this.mainMenu.getSelectedIndex();
		if(selection==MainMenuForm.SELECTION_MY_COLLECTION){
			missingGames=false;
			missingBoxes=false;
			missingInstructions=false;
		} else if(selection==MainMenuForm.SELECTION_MY_MISSING){
			myGames=false;
			missingBoxes=this.mainMenu.getOptionMissingBoxes();
			missingInstructions=this.mainMenu.getOptionMissingInstructions();
		}
		try{
			int recordCount=this.rs.getNumRecords();
			for(int index=0;index<recordCount;index++){
				int recordId=index+1; //why is this not 0-based?!
				byte[] bytes=this.rs.getRecord(recordId);
				Sega32XRecord record=new Sega32XRecord(recordId,bytes);
				if(myGames&&missingGames){ //all games
					this.gameList.addGame(record); 
				} else if(myGames){
					if(record.hasGame()){
						this.gameList.addGame(record);
					}
				} else if(missingGames){
					boolean addRecord=(!record.hasGame());
					if(!addRecord){ //they have the game, check for missing box & instructions
						if(missingBoxes){
							addRecord=(!record.hasBox());
						}
						if(!addRecord){
							if(missingInstructions){
								addRecord=(!record.hasInstructions());
							}
						}
					}
					if(addRecord){
						this.gameList.addGame(record);
					}
				}
			}
		} catch(RecordStoreNotOpenException e){/* not implemented */}
		catch(RecordStoreException e){ e.printStackTrace(); /* not implemented */}
		Display.getDisplay(this).setCurrent(this.gameList);
		if(this.gameList.size()>0){
			this.gameList.addCommand(new Command("SELECT",Command.OK,1));
		}
		this.gameList.setCommandListener(this);
	}
}
