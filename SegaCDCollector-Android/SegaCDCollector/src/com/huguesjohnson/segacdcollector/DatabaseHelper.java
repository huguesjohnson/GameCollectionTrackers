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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	private final static String TAG="DatabaseHelper";
	private static SQLiteDatabase database;
	public static final int EXPORT_ALLGAMES=0;
	public static final int EXPORT_MYGAMES=1;
	public static final int EXPORT_MISSINGGAMES=2;
	public static final int EXPORT_WISHLIST=3;
	private static final String RECORD_DELIMITER="\r\n";
	private static final String FIELD_DELIMITER="\\|";
	private static final String DB_NAME="segacdcollector";
	private static final String TABLE_NAME="mysegacdcollection";
	private static final String SELECT_FROM="SELECT recordId,title,packagetype,havegame,havebox,havecase,haveinstructions,wishlist FROM ";
	private static final int DATABASE_VERSION=1;
	
	public DatabaseHelper(Context context) throws IOException,NotFoundException,SQLException{
		super(context,DB_NAME,null,DATABASE_VERSION);
		synchronized(this){
			if(database==null){database=this.getWritableDatabase();}
			//check if database is empty
			Cursor cursor=database.rawQuery("SELECT recordId FROM "+TABLE_NAME+" WHERE recordId=1",null);
			if((cursor==null)||(cursor.getCount()<1)){
				this.loadGameList(context);
			}
			if((cursor!=null)&&(!cursor.isClosed())){
				cursor.close();
			}
		}
	}	

	private void loadGameList(Context context) throws IOException,NotFoundException,SQLException{
		InputStream inputStream;
		try{
			inputStream=context.getResources().openRawResource(R.raw.gamelist);
		}catch(NotFoundException nfx){
			Log.e(TAG,"loadGameList",nfx);
			throw(nfx);
		}
		byte[] b=new byte[5100];
		try{
			inputStream.read(b);
			inputStream.close();
		}catch(IOException iox){
			Log.e(TAG,"loadGameList: inputStream="+inputStream.toString(),iox);
			throw(iox);
		}
		String gameList=new String(b);
		int indexOf=gameList.indexOf(RECORD_DELIMITER);
		int startIndex=0;
		int recordId=0;
		int startIndexIncrement=RECORD_DELIMITER.length();
		String currentGame;
		String currentPackageType;
		String currentKeyword;
		String[] split;
		while(indexOf>-1){
			split=gameList.substring(startIndex,indexOf).split(FIELD_DELIMITER);
			currentGame=split[0];
			currentPackageType=split[1];
			if((split.length>2)&&(split[2].length()>0)){
				currentKeyword=split[2];
			}else{
				currentKeyword=currentGame.replace(" ","+");
			}
			try{
				database.execSQL(buildInsert(recordId,currentGame,currentPackageType,currentKeyword));
			}catch(SQLException sqlx){
				Log.e(TAG,"loadGameList: currentGame="+currentGame,sqlx);
				throw(sqlx);
			}
			startIndex=indexOf+startIndexIncrement;
			indexOf=gameList.indexOf(RECORD_DELIMITER,startIndex);
			recordId++;
		}
		//last record
		split=gameList.substring(startIndex).trim().split(FIELD_DELIMITER);
		currentGame=split[0];
		currentPackageType=split[1];
		if((split.length>2)&&(split[2].length()>0)){
			currentKeyword=split[2];
		}else{
			currentKeyword=currentGame.replace(" ","+");
		}
		try{
			database.execSQL(buildInsert(recordId,currentGame,currentPackageType,currentKeyword));
		}catch(SQLException sqlx){
			Log.e(TAG,"loadGameList: currentGame="+currentGame,sqlx);
			throw(sqlx);
		}
	}	

	private String buildInsert(int recordId,String gameTitle,String packageType,String keyword){
		StringBuffer insertInto=new StringBuffer();
		insertInto.append("INSERT INTO ");
		insertInto.append(TABLE_NAME);
		insertInto.append(" ('recordId','title','packagetype','keyword','havegame','havecase','havebox','haveinstructions','wishlist') VALUES (");
		insertInto.append(recordId);
		insertInto.append(",'");
		insertInto.append(gameTitle);
		insertInto.append("','");
		insertInto.append(packageType);
		insertInto.append("','");
		insertInto.append(keyword);
		insertInto.append("',0,0,0,0,0)");
		return(insertInto.toString());
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) throws SQLException{
		try{
			db.execSQL("CREATE TABLE IF NOT EXISTS "
	                + TABLE_NAME
	                + " (recordId INT PRIMARY KEY, "
	                + " title TEXT UNIQUE,"
	                + " packagetype VARCHAR(2),"
	                + " keyword TEXT,"
	                + " havegame BOOLEAN DEFAULT 'FALSE',"
	                + " havebox BOOLEAN DEFAULT 'FALSE', "
	                + " havecase BOOLEAN DEFAULT 'FALSE', "
	                + " haveinstructions BOOLEAN DEFAULT 'FALSE', "
	                + " wishlist BOOLEAN DEFAULT 'FALSE');");
		}catch(SQLException sqlx){
			if(db!=null){
				Log.e(TAG,"onCreate: db="+db.toString(),sqlx);
			}else{
				Log.e(TAG,"onCreate: db is null",sqlx);
			}
			throw(sqlx);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){/* not implemented */}
	
	@Override
	public synchronized void close(){
		if(database!=null){database.close();}
		super.close();
	}
	
	public ArrayList<SegaCDRecord> getAllGames(){
		return(getAllGames(null));
	}
	
	public ArrayList<SegaCDRecord> getAllGames(String titleFilter){
		StringBuffer query=new StringBuffer(SELECT_FROM);
		query.append(TABLE_NAME);
		if((titleFilter!=null)&&(titleFilter.length()>0)){
			query.append(" WHERE title like '%");
			query.append(titleFilter);
			query.append("%'");
		}
		return(queryDatabase(query.toString()));
	}
	
	public ArrayList<SegaCDRecord> getMyGames(){
		return(getMyGames(null));
	}
	
	public ArrayList<SegaCDRecord> getMyGames(String titleFilter){
		StringBuffer query=new StringBuffer(SELECT_FROM);
		query.append(TABLE_NAME);
		query.append(" WHERE havegame<>0");
		if((titleFilter!=null)&&(titleFilter.length()>0)){
			query.append(" AND title like '%");
			query.append(titleFilter);
			query.append("%'");
		}
		return(queryDatabase(query.toString()));
	}

	public ArrayList<SegaCDRecord> getWishList(){
		return(getWishList(null));
	}

	public ArrayList<SegaCDRecord> getWishList(String titleFilter){
		StringBuffer query=new StringBuffer("SELECT recordId,title,keyword FROM ");
		query.append(TABLE_NAME);
		query.append(" WHERE wishlist<>0");
		if((titleFilter!=null)&&(titleFilter.length()>0)){
			query.append(" AND title like '%");
			query.append(titleFilter);
			query.append("%'");
		}
		return(queryDatabase(query.toString()));	
	}

	public ArrayList<String> getWishListKeywords(){
		ArrayList<String> keywords=new ArrayList<String>();
		StringBuffer query=new StringBuffer("SELECT keyword FROM ");
		query.append(TABLE_NAME);
		query.append(" WHERE wishlist<>0");
		Cursor cursor=database.rawQuery(query.toString(),null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				keywords.add(cursor.getString(0));
			}
		}
		if((cursor!=null)&&(!cursor.isClosed())){
			cursor.close();
		}
		return(keywords);
	}
	
	public ArrayList<SegaCDRecord> getMissingGames(boolean missingBoxes,boolean missingInstructions,boolean missingCase){
		return(getMissingGames(missingBoxes,missingInstructions,missingCase,null));
	}
	
	public ArrayList<SegaCDRecord> getMissingGames(boolean missingBoxes,boolean missingInstructions,boolean missingCase,String titleFilter){
		StringBuffer query=new StringBuffer();
		query.append(SELECT_FROM);
		query.append(TABLE_NAME);
		query.append(" WHERE ((havegame=0)");
		if(missingBoxes||missingInstructions||missingCase){
			//check if all three match
			if(missingBoxes&&missingInstructions&&missingCase){
				query.append(" OR (havegame=1 AND (havebox=0 OR haveinstructions=0 OR havecase=0))");
			//check if two of three match
			} else if(missingBoxes&&missingInstructions){
				query.append(" OR (havegame=1 AND (havebox=0 OR haveinstructions=0))");
			} else if(missingBoxes&&missingCase){
				query.append(" OR (havegame=1 AND (havebox=0 OR havecase=0))");
			} else if(missingInstructions&&missingCase){
				query.append(" OR (havegame=1 AND (haveinstructions=0 OR havecase=0))");
			} else if(missingBoxes){
				query.append(" OR (havegame=1 AND havebox=0)");
			} else if(missingInstructions){
				query.append(" OR (havegame=1 AND haveinstructions=0)");
			} else{
				query.append(" OR (havegame=1 AND havecase=0)");
			}
		}
		query.append(")");
		if((titleFilter!=null)&&(titleFilter.length()>0)){
			query.append(" AND title like '%");
			query.append(titleFilter);
			query.append("%'");
		}		
		return(queryDatabase(query.toString()));
	}
	
	public SegaCDRecord getGame(int recordId){
		String sql=SELECT_FROM+TABLE_NAME+" WHERE recordId="+recordId;
		ArrayList<SegaCDRecord> list=queryDatabase(sql);
		return(list.get(0));
	}
	
	public ArrayList<String> getRawData(int exportWhat) throws IllegalArgumentException{
		ArrayList<String> returnList=new ArrayList<String>();
		returnList.add("Title,HaveGame,HaveBox,HaveCase,HaveInstructions,Wishlist\n");
		StringBuffer query=new StringBuffer("SELECT title,havegame,havebox,havecase,haveinstructions,wishlist FROM ");
		query.append(TABLE_NAME);
		query.append(" WHERE ");
		switch(exportWhat){
		case EXPORT_ALLGAMES:{query.append("recordId>-1"); break;}
		case EXPORT_MYGAMES:{query.append("havegame=1"); break;}
		case EXPORT_MISSINGGAMES:{query.append("havegame=0"); break;}
		case EXPORT_WISHLIST:{query.append("wishlist=1"); break;}
		default: {query.append("recordId<0");}
		}
		Cursor cursor=database.rawQuery(query.toString(),null);
		if(cursor!=null){
			try{
				int titleColumn=cursor.getColumnIndexOrThrow("title");
				int gameColumn=cursor.getColumnIndexOrThrow("havegame");
				int boxColumn=cursor.getColumnIndexOrThrow("havebox");
				int caseColumn=cursor.getColumnIndexOrThrow("havecase");
				int instructionsColumn=cursor.getColumnIndexOrThrow("haveinstructions");
				int wishlistColumn=cursor.getColumnIndexOrThrow("wishlist");
				while(cursor.moveToNext()){
					StringBuffer currentRow=new StringBuffer();
					currentRow.append(cursor.getString(titleColumn));
					currentRow.append(",");
					currentRow.append(cursor.getString(gameColumn));
					currentRow.append(",");
					currentRow.append(cursor.getString(boxColumn));
					currentRow.append(",");
					currentRow.append(cursor.getString(caseColumn));
					currentRow.append(",");
					currentRow.append(cursor.getString(instructionsColumn));
					currentRow.append(",");
					currentRow.append(cursor.getString(wishlistColumn));
					currentRow.append("\n");
					returnList.add(currentRow.toString());
				}
			}catch(IllegalArgumentException iax){
				Log.e(TAG,"getRawData: exportWhat="+exportWhat,iax);
				throw(iax);
			}
		}
		if((cursor!=null)&&(!cursor.isClosed())){
			cursor.close();
		}
		return(returnList);
	}

	public void updateGame(SegaCDRecord record) throws SQLException{
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE ");
		sql.append(TABLE_NAME);
		sql.append(" SET havegame=");
		if(record.isHaveGame()){
			sql.append(1);
		} else{
			sql.append(0);
		}
		sql.append(", havebox=");
		if(record.isHaveBox()){
			sql.append(1);
		} else{
			sql.append(0);
		}
		sql.append(", haveinstructions=");
		if(record.isHaveInstructions()){
			sql.append(1);
		} else{
			sql.append(0);
		}
		sql.append(", havecase=");
		if(record.isHaveCase()){
			sql.append(1);
		} else{
			sql.append(0);
		}
		sql.append(", wishlist=");
		if(record.isOnWishlist()){
			sql.append(1);
		} else{
			sql.append(0);
		}
		sql.append(" WHERE recordId=");
		sql.append(record.getRecordId());
		try{
			database.execSQL(sql.toString());
		}catch(SQLException sqlx){
			Log.e(TAG,"updateGame: sql="+sql.toString(),sqlx);
			throw(sqlx);
		}
	}
	
	private ArrayList<SegaCDRecord> queryDatabase(String sql) throws IllegalArgumentException{
		ArrayList<SegaCDRecord> list=new ArrayList<SegaCDRecord>();
		Cursor cursor=database.rawQuery(sql,null);
		if(cursor!=null){
			int titleColumn;
			try{
				titleColumn=cursor.getColumnIndexOrThrow("title");
			}catch(IllegalArgumentException iax){
				Log.e(TAG,"queryDatabase: sql="+sql,iax);
				throw(iax);
			}
			int recordIdColumn=cursor.getColumnIndex("recordId");
			int packageTypeColumn=cursor.getColumnIndex("packagetype");
			int keywordColumn=cursor.getColumnIndex("keyword");
			int gameColumn=cursor.getColumnIndex("havegame");
			int boxColumn=cursor.getColumnIndex("havebox");
			int caseColumn=cursor.getColumnIndex("havecase");
			int instructionsColumn=cursor.getColumnIndex("haveinstructions");
			int wishlistColumn=cursor.getColumnIndex("wishlist");
			while(cursor.moveToNext()){
				String title=cursor.getString(titleColumn);
				SegaCDRecord record=new SegaCDRecord();
				record.setTitle(title);
				if(recordIdColumn>=0){
					record.setRecordId(cursor.getInt(recordIdColumn));
				}
				if(keywordColumn>=0){
					record.setKeyword(cursor.getString(keywordColumn));
				}
				if(packageTypeColumn>=0){
					record.setPackageType(cursor.getString(packageTypeColumn));
				}
				if(gameColumn>=0){
					if(cursor.getInt(gameColumn)==0){
						record.setHaveGame(false);
					} else{
						record.setHaveGame(true);
					}
				}
				if(boxColumn>=0){
					if(cursor.getInt(boxColumn)==0){
						record.setHaveBox(false);
					} else{
						record.setHaveBox(true);
					}
				}
				if(instructionsColumn>=0){
					if(cursor.getInt(instructionsColumn)==0){
						record.setHaveInstructions(false);
					} else{
						record.setHaveInstructions(true);
					}
				}
				if(caseColumn>=0){
					if(cursor.getInt(caseColumn)==0){
						record.setHaveCase(false);
					} else{
						record.setHaveCase(true);
					}
				}
				if(wishlistColumn>=0){
					if(cursor.getInt(wishlistColumn)==0){
						record.setOnWishlist(false);
					} else{
						record.setOnWishlist(true);
					}
				}
				list.add(record);
			}
			if((cursor!=null)&&(!cursor.isClosed())){
				cursor.close();
			}
		}
		return(list);
	}	
}