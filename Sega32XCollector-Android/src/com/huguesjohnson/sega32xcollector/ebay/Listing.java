/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2010-2013 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.sega32xcollector.ebay;

import java.util.Date;

public class Listing implements Comparable<Listing>{
	private String id;
	private String title;
	private String imageUrl;
	private String listingUrl;
	private String location;
	private String shippingCost;
	private String currentPrice;
	private String auctionSource;
	private Date startTime;
	private Date endTime;
	private boolean auction;
	private boolean buyItNow;

	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id=id;
	}
	public String getTitle(){
		return title;
	}
	public void setTitle(String title){
		this.title=title;
	}
	public String getImageUrl(){
		return imageUrl;
	}
	public void setImageUrl(String imageUrl){
		//fix for Android 2.3
		CharSequence target="\\/";
		CharSequence replace="/";
		String fixedUrl=imageUrl.replace(target,replace);
		this.imageUrl=fixedUrl;
	}
	public String getListingUrl(){
		return listingUrl;
	}
	public void setListingUrl(String listingUrl){
		//fix for Android 2.3
		CharSequence target="\\/";
		CharSequence replace="/";
		String fixedUrl=listingUrl.replace(target,replace);
		this.listingUrl=fixedUrl;
	}
	public String getLocation(){
		return location;
	}
	public void setLocation(String location){
		this.location=location;
	}
	public String getShippingCost(){
		return shippingCost;
	}
	public void setShippingCost(String shippingCost){
		this.shippingCost=shippingCost;
	}
	public String getCurrentPrice(){
		return currentPrice;
	}
	public void setCurrentPrice(String currentPrice){
		this.currentPrice=currentPrice;
	}
	public Date getStartTime(){
		return startTime;
	}
	public void setStartTime(Date startTime){
		this.startTime=startTime;
	}
	public Date getEndTime(){
		return endTime;
	}
	public void setEndTime(Date endTime){
		this.endTime=endTime;
	}
	public boolean isAuction(){
		return auction;
	}
	public void setAuction(boolean auction){
		this.auction=auction;
	}
	public boolean isBuyItNow(){
		return buyItNow;
	}
	public void setBuyItNow(boolean buyItNow){
		this.buyItNow=buyItNow;
	}
	public String getAuctionSource(){
		return auctionSource;
	}
	public void setAuctionSource(String auctionSource){
		this.auctionSource=auctionSource;
	}
	
	@Override
	public int compareTo(Listing another){
		return(another.startTime.compareTo(this.startTime));
	}
}
