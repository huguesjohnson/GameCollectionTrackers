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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class WishListActivity extends ListActivity{
	private final static String TAG="WishListActivity";
	private static DatabaseHelper dbHelper;
	private static EbayUtils ebayUtils;
	private static ProgressDialog progressDialog;
	private SearchResult listings;
	private WishListAdapter adapter;
	private Listing selectedListing;
	private int selectedPosition;
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
	private final static int MENU_QUIT=0;
	private final static int MENU_ABOUT=1;

	private class LoadListThread extends Thread{
        private Handler handler;
        private Context context;
        
        public LoadListThread(Handler handler){
            this.handler=handler;
        }
       
        public void run(){
    		if(ebayUtils==null){
    			ebayUtils=new EbayUtils(this.context);
    		}
    		listings=ebayUtils.search(dbHelper.getWishListKeywords());
        	this.handler.sendEmptyMessage(RESULT_OK);
        }
    }
	
    private final Handler loadListHandler=new Handler(){
    	public void handleMessage(Message message){
    		loadListAdapter();
    	}
	};
	
	private void loadListAdapter(){
		this.adapter=new WishListAdapter(this,R.layout.wishlistitem,listings);
		this.setListAdapter(this.adapter);
		if(progressDialog!=null){
			progressDialog.cancel();
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		try{
			if(progressDialog==null){
				progressDialog=new ProgressDialog(this);
			}
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Searching for auctions...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			LoadListThread loadListThread=new LoadListThread(this.loadListHandler);
			loadListThread.start();
		}catch(Exception x){
			Log.e(TAG,"onResume",x);
			if((progressDialog!=null)&&(progressDialog.isShowing())){
				progressDialog.dismiss();
			}
			this.showErrorDialog(x);
		}
	}
	
	OnItemClickListener selectItemListener=new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent,View view,int position,long id){
			try{
				selectedPosition=position;
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
			if(dbHelper==null){dbHelper=new DatabaseHelper(this.getApplicationContext());}
			if(ebayUtils==null){ebayUtils=new EbayUtils(this.getApplicationContext());}
			ListView listView=this.getListView();
			listView.setTextFilterEnabled(true);
			listView.setOnItemClickListener(selectItemListener);
		}catch(Exception x){
			Log.e(TAG,"onCreate",x);
			this.showErrorDialog(x);
		}
	}
	
	private void showAboutDialog(){
		try{
	        new AlertDialog.Builder(this)
	   		.setTitle(R.string.app_name)
	   		.setMessage(R.string.app_description)
	   		.setPositiveButton("Close", null)
	   		.show();	
		}catch(Exception x){
			Log.e(TAG,"showAboutDialog",x);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		try{
			menu.add(0,MENU_ABOUT,0,"About").setIcon(R.drawable.menu_about);
			menu.add(0,MENU_QUIT,1,"Quit").setIcon(R.drawable.menu_quit);
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
			case MENU_ABOUT:{this.showAboutDialog();return(true);}
			case MENU_QUIT:{this.finish();return(true);}
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
				View layout=inflater.inflate(R.layout.listingdetaildialog,(ViewGroup)findViewById(R.id.layout_listingdetaildialogroot));
				AlertDialog.Builder builder=new AlertDialog.Builder(this);
				builder.setView(layout);
				builder.setTitle(this.selectedListing.getTitle());
				builder.setPositiveButton(R.string.button_close,onListingDetailDialogCloseListener);
				this.imageViewImage=(ImageView)layout.findViewById(R.id.imageview_listingdetail_image);
				this.textViewStartTime=(TextView)layout.findViewById(R.id.textview_listingdetail_starttime);
				this.textViewEndTime=(TextView)layout.findViewById(R.id.textview_listingdetail_endtime);
				this.textViewListingType=(TextView)layout.findViewById(R.id.textview_listingdetail_listingtype);
				this.textViewPrice=(TextView)layout.findViewById(R.id.textview_listingdetail_price);
				this.textViewShipping=(TextView)layout.findViewById(R.id.textview_listingdetail_shipping);
				this.textViewLocation=(TextView)layout.findViewById(R.id.textview_listingdetail_location);
				this.textViewLink=(TextView)layout.findViewById(R.id.textview_listingdetail_link);
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
			this.imageViewImage.setImageDrawable(this.adapter.getImage(this.selectedPosition));
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