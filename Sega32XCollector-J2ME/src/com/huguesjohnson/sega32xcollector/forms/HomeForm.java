/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2009 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
