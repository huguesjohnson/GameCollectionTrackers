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