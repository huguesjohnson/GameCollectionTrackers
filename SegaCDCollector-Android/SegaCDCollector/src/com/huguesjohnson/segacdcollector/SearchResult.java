/*
SegaCDCollector - Mobile application to manage a collection of Sega CD games
Copyright (C) 2010 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.segacdcollector;

import java.util.ArrayList;
import java.util.Collections;

public class SearchResult{
	public final static int RESULT_SUCCESS=0;
	public final static int RESULT_ERROR=1;
	private int resultCode;
	private Exception error;
	private ArrayList<Listing> listings;

	public SearchResult(){
		this.listings=new ArrayList<Listing>();
	}
	
	public void append(SearchResult toAppend){
		this.listings.addAll(toAppend.getListings());
	}
	
	public Exception getError(){
		return error;
	}
	public void setError(Exception error){
		this.error=error;
	}
	public ArrayList<Listing> getListings(){
		return listings;
	}
	public void setListings(ArrayList<Listing> listings){
		this.listings=listings;
	}

	public int getResultCode(){
		return resultCode;
	}

	public void setResultCode(int resultCode){
		this.resultCode=resultCode;
	}
	
	public void sort(){
		Collections.sort(this.listings);
	}
}
