/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2009 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.sega32xcollector.forms;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;

import com.huguesjohnson.sega32xcollector.Sega32XRecord;

/**
 * GameForm - display a game
 * 
 * @author Hugues Johnson
 */
public class GameForm extends Form implements ItemStateListener{
	public final static int CHECKBOX_HAS_GAME=0;
	public final static int CHECKBOX_HAS_BOX=1;
	public final static int CHECKBOX_HAS_INSTRUCTIONS=2;
	private Sega32XRecord record;
	private ChoiceGroup checkboxes;
	
	/**
	 * @param record the game to display
	 */
	public GameForm(Sega32XRecord record){
		super(record.getTitle());
		this.record=record;
		String title=record.getTitle();
		//load image
		try{
			StringBuffer imagePath=new StringBuffer(title.replace(' ','_'));
			int indexof=title.indexOf(':');
			if(indexof>0){
				imagePath.deleteCharAt(indexof);
			}
			if(title.endsWith(" (CD)")){
				imagePath.delete(imagePath.length()-5,imagePath.length());
			}
			imagePath.append(".gif");
			imagePath.insert(0,'/');
			ImageItem imageItem=new ImageItem(null,Image.createImage(imagePath.toString()),ImageItem.LAYOUT_CENTER,title);
			this.append(imageItem);
		} catch(Exception e){/* not implemented */}
		//add title and checkboxes
		this.append(new StringItem(title,null));
		this.append(new Spacer(0,20));
		this.checkboxes=new ChoiceGroup("My collection:",Choice.MULTIPLE);
		this.checkboxes.insert(CHECKBOX_HAS_GAME,"Cartridge",null);
		this.checkboxes.insert(CHECKBOX_HAS_BOX,"Box",null);
		this.checkboxes.insert(CHECKBOX_HAS_INSTRUCTIONS,"Instructions",null);
		this.checkboxes.setSelectedIndex(CHECKBOX_HAS_GAME,record.hasGame());
		this.checkboxes.setSelectedIndex(CHECKBOX_HAS_BOX,record.hasBox());
		this.checkboxes.setSelectedIndex(CHECKBOX_HAS_INSTRUCTIONS,record.hasInstructions());
		this.append(this.checkboxes);
		//set the item state listener
		this.setItemStateListener(this);
		//add a back command
		this.addCommand(new Command("BACK",Command.BACK,0));
	}

	/**
	 * @returns the record behind this form
	 */
	public Sega32XRecord getRecord(){
		return(this.record);
	}
	
	/**
	 * updates the record based on checkbox changes
	 */
	public void itemStateChanged(final Item item){
		this.record.setGame(this.checkboxes.isSelected(CHECKBOX_HAS_GAME));
		this.record.setBox(this.checkboxes.isSelected(CHECKBOX_HAS_BOX));
		this.record.setInstructions(this.checkboxes.isSelected(CHECKBOX_HAS_INSTRUCTIONS));
	}	
}
