/*
TurboGrafx16Collector - Mobile application to manage a collection of TurboGrafx-16 games
Copyright (C) 2010 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.tg16collector;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TextView;

public class TG16CollectorActivity extends TabActivity{
	private final static String TAG="TG16CollectorActivity";

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		try{
			//create the main layout
			setContentView(R.layout.main);
			Resources resources=this.getResources();
			TabHost tabHost=this.getTabHost();
			TabHost.TabSpec tabSpec;
			//add all games tab
			Intent intent=new Intent().setClass(this,AllGamesActivity.class);
			tabSpec=tabHost.newTabSpec("allgames").setIndicator("All Games",resources.getDrawable(R.drawable.icon_tab_star)).setContent(intent);
			tabHost.addTab(tabSpec);
			//add my games tab
			intent=new Intent().setClass(this,MyGamesActivity.class);
			tabSpec=tabHost.newTabSpec("mygames").setIndicator("My Games",resources.getDrawable(R.drawable.icon_tab_check)).setContent(intent);
			tabHost.addTab(tabSpec);
			//add missing games tab
			intent=new Intent().setClass(this,MissingGamesActivity.class);
			tabSpec=tabHost.newTabSpec("missinggames").setIndicator("Missing Games",resources.getDrawable(R.drawable.icon_tab_x)).setContent(intent);
			tabHost.addTab(tabSpec);
			//add wish list tab
			intent=new Intent().setClass(this,WishListActivity.class);
			tabSpec=tabHost.newTabSpec("wishlist").setIndicator("Wish List",resources.getDrawable(R.drawable.icon_tab_wishlist)).setContent(intent);
			tabHost.addTab(tabSpec);
			//set the default tab
			tabHost.setCurrentTab(0);
		}catch(Exception x){
			Log.e(TAG,"onCreate",x);
			TextView errorView=new TextView(this);
			errorView.setText(x.getMessage());
			this.setContentView(errorView);
		}
	}
}
