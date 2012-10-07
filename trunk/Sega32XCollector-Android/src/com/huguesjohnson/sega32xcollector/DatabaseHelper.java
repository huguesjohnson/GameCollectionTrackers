/*
Sega32XCollector - Mobile application to manage a collection of Sega 32X games
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

package com.huguesjohnson.sega32xcollector;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	private static String DB_NAME="sega32xcollector";
	private static String TABLE_NAME="my32xcollection";
	private SQLiteDatabase database;
	private static final int DATABASE_VERSION=1;
	
	/**
	 * constructor
	 * @param context
	 * @throws IOException if an error occurs trying to create the initial database
	 */
	public DatabaseHelper(Context context) throws IOException{
		super(context,DB_NAME,null,DATABASE_VERSION);
		this.database=this.getWritableDatabase();
		//check if database is empty
		Cursor cursor=this.database.rawQuery("SELECT recordId FROM "+TABLE_NAME+" WHERE recordId=1",null);
		if((cursor==null)||(cursor.getCount()<1)){
			this.loadGameList(context);
		}
		if((cursor!=null)&&(!cursor.isClosed())){
			cursor.close();
		}
	}

	private void loadGameList(Context context) throws IOException{
		InputStream inputStream=context.getResources().openRawResource(R.raw.gamelist);
		byte[] b=new byte[1024];
		inputStream.read(b);
		inputStream.close();
		String gameList=new String(b);
		int indexOf=gameList.indexOf("|");
		int startIndex=0;
		int recordId=0;
		while(indexOf>-1){
			String currentGame=gameList.substring(startIndex,indexOf);
			this.database.execSQL(buildInsert(recordId,currentGame));
			startIndex=indexOf+1;
			indexOf=gameList.indexOf("|",startIndex);
			recordId++;
		}
		//last record
		String currentGame=gameList.substring(startIndex).trim();
		this.database.execSQL(buildInsert(recordId,currentGame));
	}
	
	/**
	 * returns all games in the database
	 * @return
	 */
	public ArrayList<Sega32XRecord> getAllGames(){
		return(queryDatabase("SELECT recordId,title,game,box,instructions FROM "+TABLE_NAME));
	}

	/**
	 * returns all games the user has
	 * @return
	 */
	public ArrayList<Sega32XRecord> getMyGames(){
		return(queryDatabase("SELECT recordId,title,game,box,instructions FROM "+TABLE_NAME+" WHERE game<>0"));
	}

	/**
	 * returns all games the user has
	 * @return
	 */
	public ArrayList<Sega32XRecord> getMissingGames(boolean missingBoxes,boolean missingInstructions){
		StringBuffer sql=new StringBuffer();
		sql.append("SELECT recordId,title,game,box,instructions FROM ");
		sql.append(TABLE_NAME);
		sql.append(" WHERE (game=0)");
		if(missingBoxes||missingInstructions){
			if(missingBoxes&&missingInstructions){
				sql.append(" OR (game=1 AND (box=0 OR instructions=0))");
			} else if(missingBoxes){
				sql.append(" OR (game=1 AND box=0)");
			} else{
				sql.append(" OR (game=1 AND instructions=0)");
			}
		}
		return(queryDatabase(sql.toString()));
	}
	
	/**
	 * returns a single game
	 * @return
	 */
	public Sega32XRecord getGame(int recordId){
		String sql="SELECT recordId,title,game,box,instructions FROM "+TABLE_NAME+" WHERE recordId="+recordId;
		ArrayList<Sega32XRecord> list=queryDatabase(sql);
		return(list.get(0));
	}
	
	/**
	 * updates a record in the database
	 * @param record the new record to update
	 */
	public void updateGame(Sega32XRecord record){
		StringBuffer sql=new StringBuffer();
		sql.append("UPDATE ");
		sql.append(TABLE_NAME);
		sql.append(" SET game=");
		if(record.hasGame()){
			sql.append(1);
		} else{
			sql.append(0);
		}
		sql.append(", box=");
		if(record.hasBox()){
			sql.append(1);
		} else{
			sql.append(0);
		}
		sql.append(", instructions=");
		if(record.hasInstructions()){
			sql.append(1);
		} else{
			sql.append(0);
		}
		sql.append(" WHERE recordId=");
		sql.append(record.getRecordId());
		this.database.execSQL(sql.toString());
	}
	
	private ArrayList<Sega32XRecord> queryDatabase(String sql){
		ArrayList<Sega32XRecord> list=new ArrayList<Sega32XRecord>();
		Cursor cursor=this.database.rawQuery(sql,null);
		if(cursor!=null){
			int recordIdColumn=cursor.getColumnIndex("recordId");
			int titleColumn=cursor.getColumnIndexOrThrow("title");
			int gameColumn=cursor.getColumnIndex("game");
			int boxColumn=cursor.getColumnIndex("box");
			int instructionsColumn=cursor.getColumnIndex("instructions");
			while(cursor.moveToNext()){
				String title=cursor.getString(titleColumn).replace("#","'");
				Sega32XRecord record=new Sega32XRecord(title);
				if(recordIdColumn>=0){
					record.setRecordId(cursor.getInt(recordIdColumn));
				}
				if(gameColumn>=0){
					if(cursor.getInt(gameColumn)==0){
						record.setGame(false);
					} else{
						record.setGame(true);
					}
				}
				if(boxColumn>=0){
					if(cursor.getInt(boxColumn)==0){
						record.setBox(false);
					} else{
						record.setBox(true);
					}
				}
				if(instructionsColumn>=0){
					if(cursor.getInt(instructionsColumn)==0){
						record.setInstructions(false);
					} else{
						record.setInstructions(true);
					}
				}
				list.add(record);
			}
			cursor.close();
		}
		return(list);
	}
		
	private String buildInsert(int recordId,String gameTitle){
		StringBuffer insertInto=new StringBuffer();
		insertInto.append("INSERT INTO ");
		insertInto.append(TABLE_NAME);
		insertInto.append(" ('recordId','title','game','box','instructions') VALUES (");
		insertInto.append(recordId);
		insertInto.append(",'");
		insertInto.append(gameTitle);
		insertInto.append("',0,0,0)");
		return(insertInto.toString());
	}
	
	@Override
	public synchronized void close(){
		if(this.database!=null){database.close();}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME
                + " (recordId INT PRIMARY KEY, "
                + " title VARCHAR(53),"
                + " game BOOLEAN DEFAULT 'FALSE',"
                + " box BOOLEAN DEFAULT 'FALSE', "
                + " instructions BOOLEAN DEFAULT 'FALSE');");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){/* not implemented */}
}