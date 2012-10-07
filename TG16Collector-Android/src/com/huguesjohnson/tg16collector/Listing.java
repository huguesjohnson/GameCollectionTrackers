/*
TurboGrafx16Collector - Mobile application to manage a collection of TurboGrafx-16 games
Copyright (C) 2010 Hugues Johnson

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

package com.huguesjohnson.tg16collector;

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
		this.imageUrl=imageUrl;
	}
	public String getListingUrl(){
		return listingUrl;
	}
	public void setListingUrl(String listingUrl){
		this.listingUrl=listingUrl;
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