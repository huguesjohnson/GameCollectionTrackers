/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2009-2013 Hugues Johnson

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

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

public class ImportExportUtils{
	
	public static String gameListToJson(Sega32XRecord[] records) throws Exception{
		  JSONObject root=new JSONObject();
		  JSONArray jsonArray=new JSONArray();
		  int length=records.length;
		  try{
			  for(int index=0;index<length;index++){
					  JSONObject child=new JSONObject();
					  child.put("recordId",records[index].getRecordId());
					  child.put("title",records[index].getTitle());
					  child.put("game",records[index].hasGame());
					  child.put("box",records[index].hasBox());
					  child.put("instructions",records[index].hasInstructions());
					  jsonArray.put(child);
			  }
			  root.put("sega-32x-collection",jsonArray);
		  }catch(JSONException x){
			  Log.e("ImportExportUtils.gameListToJson",x.getMessage(),x);
			  throw(x);
		  }
		  return(root.toString());
	}
	
	public static ArrayList<Sega32XRecord> jsonStringToRecords(String jsonString) throws Exception{
		ArrayList<Sega32XRecord> records=new ArrayList<Sega32XRecord>();
		try{
			JSONObject root=new JSONObject(jsonString);
			JSONArray jsonArray=root.getJSONArray("sega-32x-collection");
			int length=jsonArray.length();
			for(int index=0;index<length;index++){
				  JSONObject child=(JSONObject)jsonArray.get(index);
				  String title=child.getString("title");
				  int recordId=child.getInt("recordId");
				  boolean hasGame=child.getBoolean("game");
				  boolean hasBox=child.getBoolean("box");
				  boolean hasInstructions=child.getBoolean("instructions");
				  records.add(new Sega32XRecord(title,recordId,hasGame,hasBox,hasInstructions));
			}
		  }catch(JSONException x){
			  Log.e("ImportExportUtils.gameListToJson",x.getMessage(),x);
			  throw(x);
		  }
		return(records);
	}
	
//	public static Sega32XRecord[] jsonToGameList(JSONObject root){
//		  int length=root.length();
//		  Sega32XRecord[] records=new Sega32XRecord[length];
//		  for(int index=0;index<length;index++){
//			  try{
//				  if(root.get)
//				  String title; int recordId; boolean hasGame; boolean hasBox; boolean hasInstructions;
//				  String title=""
//				  records[index]=Sega32XRecord();
//				  //				  JSONObject child=new JSONObject();
////				  child.put("recordId",records[index].getRecordId());
////				  child.put("game",records[index].hasGame());
////				  child.put("box",records[index].hasBox());
////				  child.put("instructions",records[index].hasInstructions());
////				  root.put("32x-collection-entry",child);
//			  }catch(JSONException x){
//				  Log.e("ImportExportUtils.jsonToGameList",x.getMessage(),x);
//			  }
//		  return(records);
//	}
	
	public static boolean checkMediaState() throws Exception{
		String state=Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return(true);
		}
		if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
			throw(new Exception("SD card is read-only. [state=MEDIA_MOUNTED_READ_ONLY]"));
		}else if(Environment.MEDIA_CHECKING.equals(state)){
			throw(new Exception("SD card is locked for disk checking. [state=MEDIA_CHECKING]"));
		}else if(Environment.MEDIA_BAD_REMOVAL.equals(state)){
			throw(new Exception("SD card was removed before it was unmounted. [state=MEDIA_BAD_REMOVAL]"));
		}else if(Environment.MEDIA_NOFS.equals(state)){
			throw(new Exception("SD card is blank or is using an unsupported filesystem. [state=MEDIA_NOFS]"));
		}else if(Environment.MEDIA_REMOVED.equals(state)){
			throw(new Exception("SD card is not present. [state=MEDIA_REMOVED]"));
		}else if(Environment.MEDIA_SHARED.equals(state)){
			throw(new Exception("SD card is shared. If you are connected to a PC via USB please disconnect and try again. [state=MEDIA_SHARED]"));
		}else if(Environment.MEDIA_UNMOUNTABLE.equals(state)){
			throw(new Exception("SD card is present but cannot be mounted. [state=MEDIA_UNMOUNTABLE]"));
		}else if(Environment.MEDIA_UNMOUNTED.equals(state)){
			throw(new Exception("SD card is not mounted. [state=MEDIA_UNMOUNTED]"));
		}else{
			throw(new Exception("Unknown media state. [state="+state+"]"));
		}
	}

}