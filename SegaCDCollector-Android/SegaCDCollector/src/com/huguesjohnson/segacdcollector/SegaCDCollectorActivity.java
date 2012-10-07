/*
SegaCDCollector - Mobile application to manage a collection of Sega CD games
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

package com.huguesjohnson.segacdcollector;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TextView;

public class SegaCDCollectorActivity extends TabActivity{
	private final static String TAG="SegaCDCollectorActivity";
	
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