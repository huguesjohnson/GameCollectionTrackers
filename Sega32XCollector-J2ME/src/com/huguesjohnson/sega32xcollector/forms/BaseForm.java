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

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;

/**
 * BaseForm
 * 
 * @author Hugues Johnson
 */
public abstract class BaseForm extends Form{
	/**
	 * Add logo
	 */
	public BaseForm(){
		super("32X Collector");
		//load image
		try{
			ImageItem imageItem=new ImageItem(null,Image.createImage("/32x.gif"),ImageItem.LAYOUT_CENTER,"32X Collector");
			this.append(imageItem);
		} catch(Exception e){/* not implemented */}
	}
}