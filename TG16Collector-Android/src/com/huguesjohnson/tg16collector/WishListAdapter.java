/*
TurboGrafx16Collector - Mobile application to manage a collection of TurboGrafx-16 games
Copyright (C) 2010 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.tg16collector;

import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WishListAdapter extends ArrayAdapter<Listing>{
	private final static String TAG="WishListAdapter";
	private Context context;
	private SearchResult listings;
	private int resourceId;
	private Drawable images[];
	
	public WishListAdapter(Context context,int resourceId,SearchResult listings){
		super(context,resourceId,listings.getListings());
		this.images=new Drawable[listings.getListings().size()];
		this.context=context;
		this.listings=listings;
		this.resourceId=resourceId;
	}

	@Override
	public View getView(int position,View convertView,ViewGroup parent){
		LayoutInflater inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout=inflater.inflate(resourceId,null);
		Listing listing=this.listings.getListings().get(position);
		TextView titleField=(TextView)layout.findViewById(R.id.wishlistitem_title);
		titleField.setText(listing.getTitle());
		TextView priceField=(TextView)layout.findViewById(R.id.wishlistitem_price);
		priceField.setText("Current price: "+listing.getCurrentPrice());
		TextView shippingField=(TextView)layout.findViewById(R.id.wishlistitem_shipping);
		shippingField.setText("Shipping: "+listing.getShippingCost());
		if(listing.getListingUrl()!=null){
			try{
				URL url=new URL(listing.getImageUrl());
				InputStream is=(InputStream)url.getContent();
				Drawable image=Drawable.createFromStream(is,"src");
				ImageView imageView=new ImageView(context);
				imageView=(ImageView)layout.findViewById(R.id.wishlistitem_image);
				imageView.setImageDrawable(image);
				this.images[position]=image;
			}catch(Exception x){
				Log.e(TAG,"getView - in if(listing.getListingUrl()!=null)",x);
			}
		}
		return(layout);  	
	}
	
	public Drawable getImage(int position){
		return(this.images[position]);
	}
}
