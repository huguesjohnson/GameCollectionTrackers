/*
SegaCDCollector - Mobile application to manage a collection of Sega CD games
Copyright (C) 2010-2014 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
