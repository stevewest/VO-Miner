/*
 * SectorDataSource.java
 * Copyright (C) 2011 Steve "Uru" West <uruwolf@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */
package com.uruwolf.vominer.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Acts as a layer between Objects and SQL for sectors
 * @author Steve "Uru" West <uruwolf@gmail.com>
 *
 */
public class SectorDataSource {
	
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	
	public SectorDataSource(Context context){
		dbHelper = new SQLiteHelper(context);
	}
	
	public void open() throws SQLException{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	/**
	 * Populates the given Sector with information from the database
	 * @param info Must have system, alpha and num coords
	 * @return
	 */
	public Sector populate(Sector info){
		if(info.getSystem() == "" || info.getAplhaCoord() == "" || info.getNumCoord() == "")
			throw new IllegalArgumentException("Given sector does not provide enough identification");
		
		Sector sector = loadSector(info);
		if(sector == null)
			return create(info);
		
		return sector;
	}
	
	/**
	 * Tries to load all the information on the given sector.
	 * @param id must have system, alpha coorrd and numerical coord
	 * @return null if a sector could not be loaded
	 */
	private Sector loadSector(Sector id){
		if(id.getSystem() == "" || id.getAplhaCoord() == "" || id.getNumCoord() == "")
			throw new IllegalArgumentException("Given sector does not provide enough identification");
		Sector sector = null;
		
		//Set up the where statement
		String whereString = SQLiteHelper.COL_SECTORS_SYSTEM+"=? AND "+
							 SQLiteHelper.COL_SECTORS_ALPHA+"=? AND "+
							 SQLiteHelper.COL_SECTORS_NUM+"=?";
		
		String[] whereList = {id.getSystem(), id.getAplhaCoord(), id.getNumCoord()};
		
		//Run the query and get the results
		Cursor cursor = database.query(SQLiteHelper.TABLE_SECTORS,
				null,
				whereString,
				whereList,
				null,
				null,
				null
				);
		//Check that we have a result to work with
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			//Populate the sector and load up minerals
			sector = populateMinerals(cursorToSector(cursor));
		}
		
		cursor.close();
		return sector;
	}
	
	/**
	 * Creates a sector with the given information. ID will be overwritten with the new insert ID
	 * @param sector
	 * @return
	 */
	private Sector create(Sector sector){
		//Set up the values to insert
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COL_SECTORS_SYSTEM, sector.getSystem());
		values.put(SQLiteHelper.COL_SECTORS_ALPHA, sector.getAplhaCoord());
		values.put(SQLiteHelper.COL_SECTORS_NUM, sector.getNumCoord());
		values.put(SQLiteHelper.COL_SECTORS_NOTES, sector.getNotes());
		//Perform the insert and get the ID for later
		database.insertOrThrow(SQLiteHelper.TABLE_SECTORS, null, values);
		
		return sector;
	}
	
	private Sector cursorToSector(Cursor cursor){
		//Populate the sector
		Sector sector = new Sector();
		sector.setId(cursor.getInt(0)); //Load the ID
		sector.setSystem(cursor.getString(1)); //Load the System
		sector.setAplhaCoord(cursor.getString(2)); //Load the alpha Coord
		sector.setNumCoord(cursor.getString(3)); //Load the num coord
		sector.setNotes(cursor.getString(4)); //Get the system notes
		return sector;
	}
	
	private Sector populateMinerals(Sector sector){
		if(sector.getId() < 0)
			throw new IllegalArgumentException("Sector needs a valid ID");
		
		//Set up the where statement
		String whereString = SQLiteHelper.COL_SECTOR_MINERALS_SECTOR+"=?";
		
		String[] whereList = {sector.getId()+""};
				
		//Run the query and get the results
		Cursor cursor = database.query(SQLiteHelper.TABLE_SECTOR_MINERALS,
				null,
				whereString,
				whereList,
				null,
				null,
				null
				);
		
		if(cursor.moveToFirst()){
			do{
				sector.addMineral(cursorToMineral(cursor));
			}while(cursor.moveToNext());
		}
		
		return sector;
	}
	
	/**
	 * Takes a cursor for a Mineral and converts it to a Mineral object
	 * @param cursor
	 * @return
	 */
	private Mineral cursorToMineral(Cursor cursor){
		Mineral mineral  = new Mineral();
		
		mineral.setMineral(cursor.getString(2));
		
		return mineral;
	}
	
	/**
	 * Attempts to add a mineral to the given sector
	 * @param sector Must have an id > 0
	 * @param mineral
	 */
	public void addMineralToSector(Sector sector, Mineral mineral){
		if(sector.getId() < 0)
			throw new IllegalArgumentException("Sector needs a valid id.");
		
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COL_SECTOR_MINERALS_MINERAL, mineral.getMineral());
		values.put(SQLiteHelper.COL_SECTOR_MINERALS_SECTOR, sector.getId());
		
		mineral.setId(database.insert(SQLiteHelper.TABLE_SECTOR_MINERALS, null, values));
		sector.addMineral(mineral);
	}
	
	/**
	 * Removes a mineral from a sector. 
	 * @param sector Must have an id > 0
	 * @param mineral
	 */
	public void removeMineralFromSector(Sector sector, Mineral mineral){
		if(sector.getId() < 0)
			throw new IllegalArgumentException("Sector needs a valid id.");
		
		String where = SQLiteHelper.COL_SECTOR_MINERALS_SECTOR+"=? AND "+
					   SQLiteHelper.COL_SECTOR_MINERALS_MINERAL+"=?";
		String[] whereData = {sector.getId()+"", mineral.getMineral()};
		
		database.delete(SQLiteHelper.TABLE_SECTOR_MINERALS, where, whereData);
	}
	
	/**
	 * Updates the given sector with the notes contained
	 * @param sector Must have a id > 0
	 */
	public void updateSectorNotes(Sector sector){
		if(sector.getId() <= 0)
			throw new IllegalArgumentException("Id must be > 0");
		
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COL_SECTORS_NOTES, sector.getNotes());
		
		database.update(SQLiteHelper.TABLE_SECTORS, values, SQLiteHelper.COL_ID+"="+sector.getId(), null);
	}
	
	/**
	 * Takes the given mineral name and returns all sectors with that mineral assigned
	 * @param mineral
	 * @return
	 */
	public ArrayList<Sector> getSectorsContainingMineral(String mineral){
		// Select all minerals with the name, return the sectors
		String query = "SELECT "+SQLiteHelper.TABLE_SECTORS+".*"+
					"FROM "+SQLiteHelper.TABLE_SECTORS+" "+
					"WHERE "+SQLiteHelper.TABLE_SECTORS+"."+SQLiteHelper.COL_ID+
					" IN ("+
						"SELECT "+SQLiteHelper.COL_SECTOR_MINERALS_SECTOR+
						" FROM "+SQLiteHelper.TABLE_SECTOR_MINERALS+
						" WHERE "+SQLiteHelper.COL_SECTOR_MINERALS_MINERAL+" = '"+mineral+"'"+
					");";
		
		//Do the query
		Cursor cursor = database.rawQuery(query, null);
		
		//Work through and build up the list
		ArrayList<Sector> sectorList = new ArrayList<Sector>();
		
		if(cursor.moveToFirst()){
			do{
				sectorList.add(cursorToSector(cursor));
			} while(cursor.moveToNext());
		}
		
		//Send back the list of sectors
		return sectorList;
	}
	
	/**
	 * Takes the given mineral name and returns all sectors with that mineral assigned
	 * @param mineral
	 * @return
	 */
	public ArrayList<Sector> getSectorsContainingMineral(Mineral mineral){
		return getSectorsContainingMineral(mineral.getMineral());
	}
}
