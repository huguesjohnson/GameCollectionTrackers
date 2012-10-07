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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.StringItem;

/**
 * HomeForm
 * 
 * @author Hugues Johnson
 */
public class HomeForm extends BaseForm{
	private StringItem progress;
	
	/**
	 * add exit command and controls
	 */
	public HomeForm(){
		super();
		//append this first so the user can quit right away
		this.addCommand(new Command("EXIT",Command.EXIT,0));
		//add controls
		this.append(new StringItem("32X Collector",null));
		this.append(new Spacer(0,20));
		this.append(new StringItem("Loading...",null));
		this.append(new Spacer(0,20));
		this.progress=new StringItem(null,null);
		this.append(this.progress);
	}
	
	/**
	 * sets the value of the progress textfield
	 * @param label the label of the progress textfield
	 * @param text the text of the progress textfield
	 */
	public void setProgress(String label,String text){
		this.progress.setLabel(label);
		this.progress.setText(text);
	}
}