/*
Imported from: Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2010-2014 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.segacdcollector.ebay;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huguesjohnson.segacdcollector.R;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

public class EbayUtils{
	private final static String TAG="EbayUtils";
	private final static String REQUEST_TEMPLATE="^1services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=^2&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&keywords=^3&paginationInput.entriesPerPage=5&affiliate.networkId=9&affiliate.trackingId=********&affiliate.customId=sega32xcollector&sortOrder=StartTimeNewest";
	private SimpleDateFormat dateFormat;
	private String appID;
	private String ebayURL;
	private Resources resources;
	
	public EbayUtils(Context context){
		this.dateFormat=new SimpleDateFormat("[\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\"]");
		this.resources=context.getResources();
		if(Build.PRODUCT.toLowerCase().indexOf("sdk")>-1){
			/* 
			the sandbox URLs are pretty useless as they only return a success code but no results
			appID=this.resources.getString(R.string.ebay_appid_sandbox);
			ebayURL=this.resources.getString(R.string.ebay_wsurl_sandbox);
			*/
			appID=this.resources.getString(R.string.ebay_appid_production);
			ebayURL=this.resources.getString(R.string.ebay_wsurl_production);
		}else{
			appID=this.resources.getString(R.string.ebay_appid_production);
			ebayURL=this.resources.getString(R.string.ebay_wsurl_production);
		}
	}
	
	public SearchResult search(ArrayList<String> keywords){
		SearchResult results=new SearchResult();
		Iterator<String> iterator=keywords.iterator();
		while(iterator.hasNext()){
			results.append(this.search(iterator.next()));
		}
		results.sort();
		return(results);
	}
	
	public SearchResult search(String keyword){
		SearchResult results=new SearchResult();
		String jsonResponse=null;
		try{
			jsonResponse=invokeEbayRest(keyword);
			if((jsonResponse==null)||(jsonResponse.length()<1)){
				throw(new Exception("No result received from invokeEbayRest("+keyword+")"));
			}
			results.setListings(this.parseListings(jsonResponse));
			results.setResultCode(SearchResult.RESULT_SUCCESS);
		}catch(Exception x){
			if(jsonResponse==null){
				Log.e(TAG,"search: jsonResponse==null",x);
			}else{
				Log.e(TAG,"search: jsonResponse="+jsonResponse,x);
			}
			results.setError(x);
			results.setResultCode(SearchResult.RESULT_ERROR);
		}
		return(results);
	}
		
	private ArrayList<Listing> parseListings(String jsonResponse) throws Exception{
		ArrayList<Listing> listings=new ArrayList<Listing>();
		JSONObject rootObj=new JSONObject(jsonResponse);
		JSONArray itemList=rootObj
			.getJSONArray(this.resources.getString(R.string.ebay_tag_findItemsByKeywordsResponse))
			.getJSONObject(0)
			.getJSONArray(this.resources.getString(R.string.ebay_tag_searchResult))
			.getJSONObject(0)
			.getJSONArray(this.resources.getString(R.string.ebay_tag_item));
		int itemCount=itemList.length();
		for(int itemIndex=0;itemIndex<itemCount;itemIndex++){
			try{
				Listing listing=this.parseListing(itemList.getJSONObject(itemIndex));
				listing.setAuctionSource(this.resources.getString(R.string.ebay_source_name));
				listings.add(listing);
			}catch(JSONException jx){
				/* if something goes wrong log & move to the next item */
				Log.e(TAG,"parseListings: jsonResponse="+jsonResponse,jx);
			}
		}
		return(listings);
	}
	
	private Listing parseListing(JSONObject jsonObj) throws JSONException{
		/*
		 * Things outside of a try/catch block are fields that are required and should throw an exception if not found
		 * Things inside of a try/catch block are fields we can live without
		 */
		Listing listing=new Listing();
		/* get items at the root of the object
		 * id, title, and URL are required
		 * image and location are optional */
		listing.setId(jsonObj.getString(this.resources.getString(R.string.ebay_tag_itemId)));
		listing.setTitle(this.stripWrapper(jsonObj.getString(this.resources.getString(R.string.ebay_tag_title))));
		listing.setListingUrl(this.stripWrapper(jsonObj.getString(this.resources.getString(R.string.ebay_tag_viewItemURL))));
		try{
			listing.setImageUrl(this.stripWrapper(jsonObj.getString(this.resources.getString(R.string.ebay_tag_galleryURL))));
		}catch(JSONException jx){
			Log.e(TAG,"parseListing: parsing image URL",jx);
			listing.setImageUrl(null);
		}
		try{
			listing.setLocation(this.stripWrapper(jsonObj.getString(this.resources.getString(R.string.ebay_tag_location))));
		}catch(JSONException jx){
			Log.e(TAG,"parseListing: parsing location",jx);
			listing.setLocation(null);
		}
		//get stuff under sellingStatus - required
		JSONObject sellingStatusObj=jsonObj.getJSONArray(this.resources.getString(R.string.ebay_tag_sellingStatus)).getJSONObject(0);
		JSONObject currentPriceObj=sellingStatusObj.getJSONArray(this.resources.getString(R.string.ebay_tag_currentPrice)).getJSONObject(0);
		listing.setCurrentPrice(this.formatCurrency(currentPriceObj.getString(this.resources.getString(R.string.ebay_tag_value)),currentPriceObj.getString(this.resources.getString(R.string.ebay_tag_currencyId))));
		//get stuff under shippingInfo - optional
		try{
			JSONObject shippingInfoObj=jsonObj.getJSONArray(this.resources.getString(R.string.ebay_tag_shippingInfo)).getJSONObject(0);
			JSONObject shippingServiceCostObj=shippingInfoObj.getJSONArray(this.resources.getString(R.string.ebay_tag_shippingServiceCost)).getJSONObject(0);
			listing.setShippingCost(this.formatCurrency(shippingServiceCostObj.getString(this.resources.getString(R.string.ebay_tag_value)),currentPriceObj.getString(this.resources.getString(R.string.ebay_tag_currencyId))));
		}catch(JSONException jx){
			Log.e(TAG,"parseListing: parsing shipping cost",jx);
			listing.setShippingCost("Not listed");
		}
		//get stuff under listingInfo
		try{
			JSONObject listingInfoObj=jsonObj.getJSONArray(this.resources.getString(R.string.ebay_tag_listingInfo)).getJSONObject(0);
			try{
				String listingType=this.stripWrapper(listingInfoObj.getString(this.resources.getString(R.string.ebay_tag_listingType)));
				if(listingType.toLowerCase().indexOf(this.resources.getString(R.string.ebay_value_auction))>-1){
					listing.setAuction(true);
					try{
						String buyItNowAvailable=this.stripWrapper(listingInfoObj.getString(this.resources.getString(R.string.ebay_tag_buyItNowAvailable)));
						if(buyItNowAvailable.equalsIgnoreCase(this.resources.getString(R.string.ebay_value_true))){
							listing.setBuyItNow(true);
						}else{
							listing.setBuyItNow(false);
						}
					}catch(JSONException jx){
						Log.e(TAG,"parseListing: parsing but it now",jx);
					}
				}else{
					listing.setAuction(false);
					listing.setBuyItNow(true);
				}
			}catch(JSONException jx){
				Log.e(TAG,"parseListing: parsing listing type",jx);
			}
			//get start and end dates - optional
			try{
				Date startTime=this.dateFormat.parse(listingInfoObj.getString(this.resources.getString(R.string.ebay_tag_startTime)));
				listing.setStartTime(startTime);
				Date endTime=this.dateFormat.parse(listingInfoObj.getString(this.resources.getString(R.string.ebay_tag_endTime)));
				listing.setEndTime(endTime);
			}catch(Exception x){ //generic - both ParseException and JSONException can be thrown, same result either way
				Log.e(TAG,"parseListing: parsing start and end dates",x);
				listing.setStartTime(null);
				listing.setEndTime(null);
			}
		 }catch(JSONException jx){
			Log.e(TAG,"parseListing: parsing listing info",jx);
			listing.setStartTime(null);
			listing.setEndTime(null);
		 }
		//alright, all done
		return(listing);
	}

	private String invokeEbayRest(String keyword) throws Exception{
		String result=null;
		HttpClient httpClient=new DefaultHttpClient();  
		HttpGet httpGet=new HttpGet(this.getRequestURL(keyword));  
		HttpResponse response=httpClient.execute(httpGet);  
		HttpEntity httpEntity=response.getEntity();  
		if(httpEntity!=null){  
			InputStream in=httpEntity.getContent();  
	        BufferedReader reader=new BufferedReader(new InputStreamReader(in));
	        StringBuffer temp=new StringBuffer();
	        String currentLine=null;
	        while((currentLine=reader.readLine())!=null){
	           	temp.append(currentLine);
	        }
	        result=temp.toString();
			in.close();  
		}
		return(result);
	}
	
	private String getRequestURL(String keyword){
		CharSequence requestURL=TextUtils.expandTemplate(REQUEST_TEMPLATE,this.ebayURL,this.appID,keyword);
		return(requestURL.toString());
	}
	
	private String formatCurrency(String amount,String currencyCode){
		StringBuffer formattedText=new StringBuffer(amount);
		try{
			//add trailing zeros
			int indexOf=formattedText.indexOf(".");
			if(indexOf>=0){
				if(formattedText.length()-indexOf==2){
					formattedText.append("0");
				}
			}
			//add dollar sign
			if(currencyCode.equalsIgnoreCase("USD")){
				formattedText.insert(0,"$");
			}else{
				formattedText.append(" ");
				formattedText.append(currencyCode);
			}
		}catch(Exception x){
			Log.e(TAG,"formatCurrency",x);
		}
		return(formattedText.toString());
	}
	
	private String stripWrapper(String s){
		try{
			int end=s.length()-2;
			return(s.substring(2,end));
		}catch(Exception x){
			Log.e(TAG,"stripWrapper",x);
			return(s);
		}
	}
}
