/*
SegaCDCollector - Mobile application to manage a collection of Sega CD games
Copyright (C) 2010-2014 Hugues Johnson

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

package com.huguesjohnson.segacdcollector;

public class SegaCDRecord{
	private String title;
	private String keyword;
	private String androidImageName;
	private String packageType;
	private boolean haveGame;
	private boolean haveBox;
	private boolean haveCase;
	private boolean haveInstructions;
	private boolean onWishlist;
	private int recordId;
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title=title;
	}
	
	public boolean hasGame(){
		return haveGame;
	}
	
	public void setGame(boolean haveGame){
		this.haveGame=haveGame;
	}
	
	public boolean hasBox(){
		return haveBox;
	}
	
	public void setBox(boolean haveBox){
		this.haveBox=haveBox;
	}
	
	public boolean hasCase(){
		return haveCase;
	}
	
	public void setCase(boolean haveCase){
		this.haveCase=haveCase;
	}
	
	public boolean hasInstructions(){
		return haveInstructions;
	}
	
	public void setInstructions(boolean haveInstructions){
		this.haveInstructions=haveInstructions;
	}
	
	public boolean isOnWishlist(){
		return onWishlist;
	}
	
	public void setOnWishlist(boolean onWishlist){
		this.onWishlist=onWishlist;
	}
	
	public int getRecordId(){
		return recordId;
	}
	
	public void setRecordId(int recordId){
		this.recordId=recordId;
	}
	
	public String getAndroidImageName(){
		return androidImageName;
	}
	
	public String getKeyword(){
		return keyword;
	}

	public void setKeyword(String keyword){
		this.keyword=keyword;
	}

	public String getPackageType(){
		return packageType;
	}

	public void setPackageType(String packageType){
		this.packageType=packageType;
	}
	
	/**
	 * Returns true if a box was made for this game, false if it only came in jewel case or cardboard packaging
	 * @return true if a box was made for this game, false if it only came in jewel case or cardboard packaging
	 */
	public boolean boxAvailable(){
		return(this.getPackageType().equals("CB"));
	}
	
	@Override
	public int hashCode(){
		return(this.getTitle().hashCode());
	}

	@Override
	public String toString(){
		return(this.getTitle());
	}
}