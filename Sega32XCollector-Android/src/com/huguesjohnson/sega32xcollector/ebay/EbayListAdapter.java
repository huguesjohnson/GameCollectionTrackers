/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2010-2013 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.sega32xcollector.ebay;

import java.io.InputStream;
import java.net.URL;

import util.HttpFetch;
import util.ImageCache;

import com.huguesjohnson.sega32xcollector.R;
import com.huguesjohnson.sega32xcollector.R.id;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EbayListAdapter extends ArrayAdapter<Listing>{
	private final static String TAG="WishListAdapter";
	private Context context;
	private SearchResult listings;
	private int resourceId;
	private final static int IMAGE_SCALE=1;
	
	public EbayListAdapter(Context context,int resourceId,SearchResult listings){
		super(context,resourceId,listings.getListings());
		this.context=context;
		this.listings=listings;
		this.resourceId=resourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		LayoutInflater inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout=inflater.inflate(resourceId,null);
		Listing listing=this.listings.getListings().get(position);
		TextView titleField=(TextView)layout.findViewById(R.id.listitem_ebay_title);
		titleField.setText(listing.getTitle());
		TextView priceField=(TextView)layout.findViewById(R.id.listitem_ebay_price);
		priceField.setText("Current price: "+listing.getCurrentPrice());
		TextView shippingField=(TextView)layout.findViewById(R.id.listitem_ebay_shipping);
		shippingField.setText("Shipping: "+listing.getShippingCost());
		//TODO - use the method on 1MC app to load this async
		String imageUrl=listing.getImageUrl();
		if((imageUrl!=null)&&(imageUrl.length()>0)){
				DownloadImageTask dlTask=new DownloadImageTask();
				ImageView imageView=(ImageView)layout.findViewById(R.id.listitem_ebay_image);
				imageView.setTag(imageUrl);
				dlTask.setImageView(imageView);
				dlTask.execute(imageUrl);
			}
		return(layout);  	
	}
	
	private class DownloadImageTask extends AsyncTask<String,Void,Bitmap>{
		private ImageView imageView;
		public void setImageView(ImageView imageView){this.imageView=imageView;}
		private String imageUrl;
		
		public Bitmap doInBackground(String... params){
			try{
				imageUrl=params[0];
				Log.d(TAG,"DownloadFeaturedImageTask.doInBackground, imageUrl="+imageUrl);
				Bitmap bitmap=ImageCache.get(imageUrl);
				if(bitmap!=null){return(bitmap);}
        		long startTime=System.currentTimeMillis();
        		bitmap=HttpFetch.fetchBitmap(imageUrl,IMAGE_SCALE);
        		long endTime=System.currentTimeMillis();
        		Log.d(TAG,"DownloadFeaturedImageTask.doInBackground, Time to fetch bitmap="+(endTime-startTime)+"ms");
        		if(bitmap!=null)ImageCache.put(imageUrl,bitmap);
                return(bitmap);				
			}catch(Exception x){
				Log.e(TAG,"DownloadImageTask.doInBackground",x);
    	    	return(null);
			}
	     }

	     public void onPostExecute(Bitmap result){
	    	 try{
	    		 if(result!=null){
	    			 //check if this image is intended for a different imageview
	    			 if(imageUrl.equals(this.imageView.getTag())){
	    				 this.imageView.setImageBitmap(result);
	    			 }
	    		 }
			}catch(Exception x){
				Log.e(TAG,"DownloadImageTask.onPostExecute",x);
			}
	     }
	 }
}
