/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2009-2013 Hugues Johnson

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

/**
 * Sega32XRecord - Used to store 32X game records
 * 
 * @author Hugues Johnson
 */
public class Sega32XRecord{
	private String title;
	private String androidImageName;
	private boolean game;
	private boolean box;
	private boolean instructions;
	private int recordId;
	private boolean dirtyFlag;

	/**
	 * Creates the object from a byte array - used to retrieve from an RMS record
	 * @param bytes
	 */
	public Sega32XRecord(int recordId,byte[] bytes){
		String str=new String(bytes);
		this.game=str.substring(0,1).equals("1");
		this.box=str.substring(2,3).equals("1");
		this.instructions=str.substring(4,5).equals("1");
		this.title=str.substring(6);
		this.recordId=recordId;
		this.dirtyFlag=false;
		this.setAndroidImageName();
	}
	
	/**
	 * @param title
	 */
	public Sega32XRecord(String title){
		super();
		this.title=title;
		this.game=false;
		this.box=false;
		this.instructions=false;
		this.dirtyFlag=false;
		this.setAndroidImageName();
	}

	private void setAndroidImageName(){
		StringBuffer imageNameBuffer=new StringBuffer(this.title.replace(' ','_'));
		int indexof=this.title.indexOf(':');
		if(indexof>0){
			imageNameBuffer.deleteCharAt(indexof);
		}
		indexof=this.title.indexOf('\'');
		if(indexof>0){
			imageNameBuffer.deleteCharAt(indexof);
		}
		indexof=this.title.indexOf('-');
		if(indexof>0){
			imageNameBuffer.deleteCharAt(indexof);
		}
		if(this.title.endsWith(" (CD)")){
			imageNameBuffer.delete(imageNameBuffer.length()-5,imageNameBuffer.length());
		}
		this.androidImageName=imageNameBuffer.toString().toLowerCase();
	}
	
	/**
	 * return the title
	 */
	public String getTitle(){
		return(this.title);
	}

	/**
	 * return the image name
	 */
	public String getAndroidImageName(){
		return(this.androidImageName);
	}
	
	/**
	 * @return the game
	 */
	public boolean hasGame(){
		return(this.game);
	}

	/**
	 * @param game the game to set
	 */
	public void setGame(boolean game){
		this.game=game;
		this.dirtyFlag=true;
	}

	/**
	 * @return the box
	 */
	public boolean hasBox(){
		return(this.box);
	}

	/**
	 * @param box the box to set
	 */
	public void setBox(boolean box){
		this.box=box;
		this.dirtyFlag=true;
	}

	/**
	 * @return the instructions
	 */
	public boolean hasInstructions(){
		return(this.instructions);
	}

	/**
	 * @param instructions the instructions to set
	 */
	public void setInstructions(boolean instructions){
		this.instructions=instructions;
		this.dirtyFlag=true;
	}
	
	/**
	 * @return the recordId
	 */
	public int getRecordId(){
		return(this.recordId);
	}

	/**
	 * @param recordId the recordId to set
	 */
	public void setRecordId(int recordId){
		this.recordId=recordId;
	}

	/**
	 * @return true if the record has been modified and needs to be saved
	 */
	public boolean getDirtyFlag(){
		return(this.dirtyFlag);
	}
	
	/**
	 * Returns the object as a byte array - used to store as an RMS record
	 * @return the object as a byte array
	 */
	public byte[] toBytes(){
		StringBuffer str=new StringBuffer();
		if(this.game){ str.append("1|"); } else{str.append("0|"); }
		if(this.box){ str.append("1|"); } else{str.append("0|"); }
		if(this.instructions){ str.append("1|"); } else{str.append("0|"); }
		str.append(this.title);
		return(str.toString().getBytes());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		if(obj instanceof Sega32XRecord){
			return(this.getTitle().equals(((Sega32XRecord)obj).getTitle()));
		} 
		return(false);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return(this.getTitle().hashCode());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return(this.getTitle());
	}
}