/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2009 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.sega32xcollector;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.List;

/**
 * Sega32XList - displays a list of Sega32XRecord objects
 * 
 * @author Hugues Johnson
 */
public class Sega32XGameList extends List{
	private Vector gameList;
	
	/**
	 * @param title the list title
	 */
	public Sega32XGameList(String title){
		super(title,IMPLICIT);
		this.gameList=new Vector(39);
		this.addCommand(new Command("BACK",Command.BACK,0));
	}
	
	/**
	 * adds a game to the list
	 * @param game the game to add
	 */
	public void addGame(Sega32XRecord game){
		this.gameList.addElement(game);
		this.append(game.getTitle(),null);
	}
	
	/**
	 * @return the selected game
	 */
	public Sega32XRecord getSelectedGame(){
		return((Sega32XRecord)this.gameList.elementAt(this.getSelectedIndex()));
	}

	/**
	 * selects a game in the list, defaults to first item if not found
	 * @param record the game to select
	 */
	public void setSelectedGame(Sega32XRecord record){
		int indexof=this.gameList.indexOf(record);
		if(indexof>-1){
			this.setSelectedIndex(indexof,true);
		} else{
			this.setSelectedIndex(0,true);
		}
	}
}
