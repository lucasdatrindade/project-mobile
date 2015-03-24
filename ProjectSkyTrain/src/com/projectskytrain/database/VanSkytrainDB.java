package com.projectskytrain.database;

import com.projectskytrain.constants.StationEnum;
import com.projectskytrain.database.VanSkyTrainDBContract.BusDB;
import com.projectskytrain.database.VanSkyTrainDBContract.BusStationDB;
import com.projectskytrain.database.VanSkyTrainDBContract.StationDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VanSkytrainDB extends SQLiteOpenHelper {
	
	 public static final int DATABASE_VERSION = 1;
	 public static final String DATABASE_NAME = "vanskyDB.db";
	 
	 private final String SQL_CREATE_TABLE_STATION = "CREATE TABLE IF NOT EXISTS "+StationDB.TABLE_NAME+" ( "
			 +StationDB.COLUMN_ID+" INT PRIMARY KEY     NOT NULL, "
			 +StationDB.COLUMN_NAME+"	TEXT	NOT NULL, "
			 +StationDB.COLUMN_LINE+"	INT     NOT NULL, "
			 +StationDB.COLUMN_ZONE+"	INT     NOT NULL "
			 +")";
	
	 private static final String SQL_CREATE_TABLE_BUS = "CREATE TABLE IF NOT EXISTS "+BusDB.TABLE_NAME+"("
			 +BusDB.COLUMN_ID+" INT PRIMARY KEY     NOT NULL,"
			 +BusDB.COLUMN_NAME+"	TEXT	NOT NULL"
			 +")";
	 
	 private static final String SQL_CREATE_TABLE_BUS_STATION = "CREATE TABLE IF NOT EXISTS "+BusStationDB.TABLE_NAME+" ( "
			 +BusStationDB.COLUMN_STATION_ID+" INT    NOT NULL, "
			 +BusStationDB.COLUMN_BUS_ID+" INT   NOT NULL,"
			 +"FOREIGN KEY("+BusStationDB.COLUMN_STATION_ID+") REFERENCES "+StationDB.TABLE_NAME+"("+StationDB.COLUMN_ID+"),"
			 +"FOREIGN KEY("+BusStationDB.COLUMN_BUS_ID+") REFERENCES "+BusDB.TABLE_NAME+"("+BusDB.COLUMN_ID+"),"
			 +"PRIMARY KEY("+BusStationDB.COLUMN_STATION_ID+","+BusStationDB.COLUMN_BUS_ID+")"
			 +")";
	 
	public VanSkytrainDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
		Log.d("database", "The database has been opened");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		createTables(db);
		insertData(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	private void createTables(SQLiteDatabase db){
		try{
			db.execSQL(SQL_CREATE_TABLE_STATION);
			db.execSQL(SQL_CREATE_TABLE_BUS);
			db.execSQL(SQL_CREATE_TABLE_BUS_STATION);
			Log.d("database", " Tables have been created");
		}catch(SQLException ex){
			Log.d("database", ex.getMessage());
		}
	}
	
	private void insertData(SQLiteDatabase db){
		
		for (StationEnum station : StationEnum.values()) {
			
			ContentValues values = new ContentValues();
			values.put(StationDB.COLUMN_ID, station.getCode());
			values.put(StationDB.COLUMN_NAME, station.getName());
			values.put(StationDB.COLUMN_LINE, station.getLine());
			values.put(StationDB.COLUMN_ZONE, station.getZone());
			
			db.insert(StationDB.TABLE_NAME, null, values);
		}
	}

}
