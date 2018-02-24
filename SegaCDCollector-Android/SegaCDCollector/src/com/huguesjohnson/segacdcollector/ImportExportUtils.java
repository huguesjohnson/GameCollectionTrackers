/*
Imported from: Sega32XCollector - Mobile application to manage a collection of Sega 32X games
Copyright (C) 2010-2014 Hugues Johnson

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.huguesjohnson.segacdcollector;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

public class ImportExportUtils{
	
	public static String gameListToJson(ArrayList<SegaCDRecord> records) throws Exception{
		  JSONObject root=new JSONObject();
		  JSONArray jsonArray=new JSONArray();
		  int length=records.size();
		  try{
			  for(int index=0;index<length;index++){
				  SegaCDRecord record=records.get(index);
				  JSONObject child=new JSONObject();
				  child.put("recordId",record.getRecordId());
				  child.put("title",record.getTitle());
				  child.put("game",record.hasGame());
				  child.put("box",record.hasBox());
				  child.put("case",record.hasCase());
				  child.put("instructions",record.hasInstructions());
				  jsonArray.put(child);
			  }
			  root.put("sega-cd-collection",jsonArray);
		  }catch(JSONException x){
			  Log.e("ImportExportUtils.gameListToJson",x.getMessage(),x);
			  throw(x);
		  }
		  return(root.toString());
	}
	
	public static ArrayList<SegaCDRecord> jsonStringToRecords(String jsonString) throws Exception{
		ArrayList<SegaCDRecord> records=new ArrayList<SegaCDRecord>();
		try{
			JSONObject root=new JSONObject(jsonString);
			JSONArray jsonArray=root.getJSONArray("sega-cd-collection");
			int length=jsonArray.length();
			for(int index=0;index<length;index++){
				  JSONObject child=(JSONObject)jsonArray.get(index);
				  SegaCDRecord record=new SegaCDRecord();
				  record.setTitle(child.getString("title"));
				  record.setRecordId(child.getInt("recordId"));
				  record.setGame(child.getBoolean("game"));
				  record.setBox(child.getBoolean("box"));
				  record.setCase(child.getBoolean("case"));
				  record.setInstructions(child.getBoolean("instructions"));
				  records.add(record);
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
