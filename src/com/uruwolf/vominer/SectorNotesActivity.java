package com.uruwolf.vominer;

import com.uruwolf.vominer.data.Sector;
import com.uruwolf.vominer.data.SectorDataSource;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class SectorNotesActivity extends Activity{
	
	private String system;
	private String alpha;
	private String num;
	private SectorDataSource data;
	private Sector sector;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.sector_notes);
        
        data = new SectorDataSource(this);
        data.open();
        
        Bundle extras = getIntent().getExtras();
        system = extras.getString("system");
        alpha = extras.getString("alpha");
        num = extras.getString("num");
        
        String title = String.format(getResources().getString(R.string.sector_full_title), system, alpha, num);
        
        ((TextView) findViewById(R.id.sectorTitle)).setText(title);
	}
	
	@Override
    public void onPause(){
    	super.onPause();
    	
    	sector.setNotes(((EditText)findViewById(R.id.sectorNotes)).getText().toString());
    	data.updateSectorNotes(sector);
    	
    	data.close();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	data.open();
    	
    	//Load the sector
    	sector = data.populate(new Sector(system, alpha, num, -1, ""));
    	((EditText) findViewById(R.id.sectorNotes)).setText(sector.getNotes());
    }
}
