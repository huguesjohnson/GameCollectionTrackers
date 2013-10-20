/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2010-2013 Hugues Johnson

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

import util.ImageCache;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.huguesjohnson.sega32xcollector.ebay.EbayListAdapter;
import com.huguesjohnson.sega32xcollector.ebay.EbayUtils;
import com.huguesjohnson.sega32xcollector.ebay.Listing;
import com.huguesjohnson.sega32xcollector.ebay.SearchResult;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EbayActivity extends SherlockActivity{
	private final static String TAG="EbayActivity";
	private ListView listView;
	private static EbayUtils ebayUtils;
	private SearchResult listings;
	private EbayListAdapter adapter;
	private Listing selectedListing;
	//listing detail dialog
	private AlertDialog listingDetailDialog;
	private ImageView imageViewImage;
	private TextView textViewStartTime;
	private TextView textViewEndTime;
	private TextView textViewListingType;
	private TextView textViewPrice;
	private TextView textViewShipping;
	private TextView textViewLocation;
	private TextView textViewLink;
	//menu constants
	private final static int MENU_BACK=0;
	private final static int MENU_REFRESH=1;

	private static String originalSearchTerm;
	private static String _searchTerm;
	public static void setSearchTerm(String searchTerm){
		originalSearchTerm=searchTerm;
		_searchTerm=searchTerm.replace(" ","+").replace("(","").replace(")","");
	}
	
	private class DownloadAuctionListTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try{
	    		listings=ebayUtils.search(_searchTerm);
			}catch(Exception x){
				Log.e(TAG,"DownloadAuctionListTask.doInBackground",x);
			}
			return null;
		}
		
		@Override
		public void onPostExecute(Void result){
			try{
				hideProgress();
				loadListAdapter();
			}catch(Exception x){
				Log.e(TAG,"DownloadAuctionListTask.onPostExecute",x);
			}
		}
		
	}	
	
	private void loadListAdapter(){
		this.adapter=new EbayListAdapter(this,R.layout.ebaylistitem,listings);
		this.listView.setAdapter(this.adapter);
		LinearLayout layout=(LinearLayout)findViewById(R.id.auctionlayout_error);
		if(this.adapter.getCount()<1){
			TextView textView=(TextView)findViewById(R.id.auctionlist_error_searchterm);
			textView.setText(originalSearchTerm);
			layout.setVisibility(View.VISIBLE);
		}else{
			layout.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		try{
			this.refreshAuctionList();
		}catch(Exception x){
			Log.e(TAG,"onResume",x);
			this.showErrorDialog(x);
		}
	}
	
    private void refreshAuctionList(){
		try{
			LinearLayout layout=(LinearLayout)findViewById(R.id.auctionlayout_progressbanner);
			layout.setVisibility(View.VISIBLE);
			new DownloadAuctionListTask().execute();
		}catch(Exception x){
			Log.e(TAG,"refreshAuctionList",x);
		}
	}
	
	private void hideProgress(){
		try{
			LinearLayout layout=(LinearLayout)findViewById(R.id.auctionlayout_progressbanner);
			layout.setVisibility(View.GONE);
		}catch(Exception x){
			Log.e(TAG,"hideProgress",x);
			showErrorDialog(x);
		}
	}
	
	OnItemClickListener selectItemListener=new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent,View view,int position,long id){
			try{
				selectedListing=(Listing)adapter.getItem(position);
				showListingDetailDialog();
			}catch(Exception x){
				Log.e(TAG,"selectItemListener.onItemClick",x);
			}
		}
	};
	
	OnClickListener onListingDetailDialogCloseListener=new OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog,int which){/*not implemented*/}
	};	
	
	View.OnClickListener urlClickedListener=new View.OnClickListener(){
		@Override
		public void onClick(View v){
			launchBrowser();
		}
	};
	
	private void launchBrowser(){
		try{
			Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(this.selectedListing.getListingUrl()));
			this.startActivity(browserIntent);
		}catch(Exception x){
			Log.e(TAG,"launchBrowser",x);
			this.showErrorDialog(x);
		}
	}
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try{
			if(ebayUtils==null){ebayUtils=new EbayUtils(this.getApplicationContext());}
			//setup actionbar
			ActionBar actionBar=getSupportActionBar();
			actionBar.setIcon(R.drawable.banner);
			actionBar.setTitle("");
			//setup listview
			this.setContentView(R.layout.auctionlistlayout);
	        this.listView=(ListView)this.findViewById(R.id.auctionlayout_listview);
	        this.listView.setTextFilterEnabled(true);
	        this.listView.setOnItemClickListener(selectItemListener);
		}catch(Exception x){
			Log.e(TAG,"onCreate",x);
			this.showErrorDialog(x);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		try{
			menu.add(0,MENU_BACK,0,"Back").setIcon(R.drawable.ic_menu_back);
			menu.add(0,MENU_REFRESH,1,"Refresh").setIcon(R.drawable.ic_menu_refresh);
			return(true);
		}catch(Exception x){
			Log.e(TAG,"onCreateOptionsMenu",x);
			return(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		try{
			switch(item.getItemId()){
			case MENU_REFRESH:{this.refreshAuctionList();return(true);}
			case MENU_BACK:{this.finish();return(true);}
			default:{return(false);}
			}
		}catch(Exception x){
			Log.e(TAG,"onOptionsItemSelected",x);
			return(false);
		}
	}
	
	private void showListingDetailDialog(){
		try{
			//I don't think this can actually happen so this is just a sanity check
			if(this.selectedListing==null){return;}
			//create the listing detail dialog
			if(this.listingDetailDialog==null){
				LayoutInflater inflater=(LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				View layout=inflater.inflate(R.layout.listingdetaildialog,(ViewGroup)findViewById(R.id.listingdetail_dialogroot));
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setView(layout);
				builder.setTitle(this.selectedListing.getTitle());
				builder.setPositiveButton("Close",onListingDetailDialogCloseListener);
				this.imageViewImage=(ImageView)layout.findViewById(R.id.listingdetail_image);
				this.textViewStartTime=(TextView)layout.findViewById(R.id.listingdetail_starttime);
				this.textViewEndTime=(TextView)layout.findViewById(R.id.listingdetail_endtime);
				this.textViewListingType=(TextView)layout.findViewById(R.id.listingdetail_listingtype);
				this.textViewPrice=(TextView)layout.findViewById(R.id.listingdetail_price);
				this.textViewShipping=(TextView)layout.findViewById(R.id.listingdetail_shipping);
				this.textViewLocation=(TextView)layout.findViewById(R.id.listingdetail_location);
				this.textViewLink=(TextView)layout.findViewById(R.id.listingdetail_link);
				this.listingDetailDialog=builder.create();
			}
			//set the values
			Resources r=this.getResources();
			this.textViewStartTime.setText(Html.fromHtml("<b>"+r.getString(R.string.listingdetail_label_starttime)+"</b>&nbsp;&nbsp;"+this.selectedListing.getStartTime().toLocaleString()));
			this.textViewEndTime.setText(Html.fromHtml("<b>"+r.getString(R.string.listingdetail_label_endtime)+"</b>&nbsp;&nbsp;"+this.selectedListing.getEndTime().toLocaleString()));
			this.textViewPrice.setText(Html.fromHtml("<b>"+r.getString(R.string.listingdetail_label_price)+"</b>&nbsp;&nbsp;"+this.selectedListing.getCurrentPrice()));
			this.textViewShipping.setText(Html.fromHtml("<b>"+r.getString(R.string.listingdetail_label_shipping)+"</b>&nbsp;&nbsp;"+this.selectedListing.getShippingCost()));
			this.textViewLocation.setText(Html.fromHtml("<b>"+r.getString(R.string.listingdetail_label_location)+"</b>&nbsp;&nbsp;"+this.selectedListing.getLocation()));
			String listingType=new String("<b>"+r.getString(R.string.listingdetail_label_listingtype)+"</b>&nbsp;&nbsp;");
			if(this.selectedListing.isAuction()){
				listingType=listingType+r.getString(R.string.listingdetail_caption_auction);
				if(this.selectedListing.isBuyItNow()){
					listingType=listingType+", "+r.getString(R.string.listingdetail_caption_buyitnow);
				}
			}else if(this.selectedListing.isBuyItNow()){
				listingType=listingType+r.getString(R.string.listingdetail_caption_buyitnow);
			}else{
				listingType=listingType+r.getString(R.string.listingdetail_caption_unknown);
			}
			//url field
			this.textViewListingType.setText(Html.fromHtml(listingType));
			StringBuffer html=new StringBuffer("<a href='");
			html.append(this.selectedListing.getListingUrl());
			html.append("'>");
			html.append("View original listing on ");
			html.append(this.selectedListing.getAuctionSource());
			html.append("</a>");
			this.textViewLink.setText(Html.fromHtml(html.toString()));
			this.textViewLink.setOnClickListener(urlClickedListener);
			//set the image
			this.imageViewImage.setImageBitmap(ImageCache.get(this.selectedListing.getImageUrl()));
			//show the dialog
			this.listingDetailDialog.setTitle(this.selectedListing.getTitle());
			this.listingDetailDialog.show();
		}catch(Exception x){
			if((this.listingDetailDialog!=null)&&(this.listingDetailDialog.isShowing())){
				this.listingDetailDialog.dismiss();
			}
			Log.e(TAG,"showListingDetailDialog",x);
		}
	}
	
	private void showErrorDialog(Exception x){
		try{
	        new AlertDialog.Builder(this)
	   		.setTitle(R.string.app_name)
	   		.setMessage(x.getMessage())
	   		.setPositiveButton("Close", null)
	   		.show();	
		}catch(Exception reallyBadTimes){
			Log.e(TAG,"showErrorDialog",reallyBadTimes);
		}
	}
}