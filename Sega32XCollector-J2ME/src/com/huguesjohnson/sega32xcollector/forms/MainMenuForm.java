/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2009 Hugues Johnson

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

package com.huguesjohnson.sega32xcollector.forms;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;

/**
 * MainMenuForm
 * 
 * @author Hugues Johnson
 */
public class MainMenuForm extends BaseForm implements ItemStateListener{
	public final static int SELECTION_ALL=0;
	public final static int SELECTION_MY_COLLECTION=1;
	public final static int SELECTION_MY_MISSING=2;
	public final static int OPTION_MISSING_BOXES=0;
	public final static int OPTION_MISSING_INSTRUCTIONS=1;
	private ChoiceGroup choices;
	private ChoiceGroup options;
	
	/**
	 * add exit command and controls
	 */
	public MainMenuForm(){
		super();
		//append this first so the user can quit right away
		this.addCommand(new Command("EXIT",Command.EXIT,0));
		//add controls
		this.choices=new ChoiceGroup("Display:",Choice.EXCLUSIVE);
		this.choices.insert(SELECTION_ALL,"All 32X games",null);
		this.choices.insert(SELECTION_MY_COLLECTION,"My 32X collection",null);
		this.choices.insert(SELECTION_MY_MISSING,"My missing games",null);
		this.append(this.choices);
		this.options=new ChoiceGroup("Options:",Choice.MULTIPLE);
		this.append(this.options);
		//set the item state listener
		this.setItemStateListener(this);
		//add a select command
		this.addCommand(new Command("CONTINUE",Command.OK,0));
	}
	
	/**
	 * returns the index of the selected item
	 * @return index of the selected item
	 */
	public int getSelectedIndex(){
		return(this.choices.getSelectedIndex());
	}
	
	/**
	 * returns the text of the selected item
	 * @return text of the selected item
	 */
	public String getSelectedText(){
		return(this.choices.getString(this.choices.getSelectedIndex()));
	}
	
	/**
	 * returns whether the "Include missing boxes" checkbox is selected
	 * @return true if the "Include missing boxes" checkbox is selected
	 */
	public boolean getOptionMissingBoxes(){
		if(this.options.size()!=0){
			if(this.options.isSelected(OPTION_MISSING_BOXES)){
				return(true);
			}
		}
		return(false);
	}

	/**
	 * returns whether the "Include missing instructions" checkbox is selected
	 * @return true if the "Include missing instructions" checkbox is selected
	 */
	public boolean getOptionMissingInstructions(){
		if(this.options.size()!=0){
			if(this.options.isSelected(OPTION_MISSING_INSTRUCTIONS)){
				return(true);
			}
		}
		return(false);
	}

	/**
	 * enable/disables checkboxes based on selection
	 */
	public void itemStateChanged(final Item item){
		if(item==this.choices){
			if(this.choices.getSelectedIndex()==SELECTION_MY_MISSING){
				if(this.options.size()==0){
					this.options.insert(OPTION_MISSING_BOXES,"Include missing boxes",null);
					this.options.insert(OPTION_MISSING_INSTRUCTIONS,"Include missing instructions",null);
				}
			} else{
				if(this.options.size()>0){
					this.options.deleteAll();
				}
			}
		}
	}
}