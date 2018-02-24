/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2009 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.sega32xcollector;

/**
 * Sega32XRecord - Used to store 32X game records
 * 
 * @author Hugues Johnson
 */
public class Sega32XRecord{
	private String title;
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
	}

	/**
	 * return the title
	 */
	public String getTitle(){
		return(this.title);
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
}
