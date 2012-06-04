package com.uruwolf.vominer;

import com.uruwolf.vominer.data.Sector;
import com.uruwolf.vominer.data.SectorDataSource;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Shows notes relating to a sector. When called from an intent must have system, alpha and num in the intent extras.
 * @author Steve "Uru" West <uruwolf@gmail.com>
 * 
 */
public class SectorNotesActivity extends Activity{
	
	private String system;
	private String alpha;
	private String num;
	private SectorDataSource data;
	private Sector sector;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.sector_notes);
        
        //Make sure we can use the database
        data = new SectorDataSource(this);
        data.open();
        
        //Grab the extras so we know what sector we are dealing with
        Bundle extras = getIntent().getExtras();
        system = extras.getString("system");
        alpha = extras.getString("alpha");
        num = extras.getString("num");
        
        //Set the sector title
        String title = String.format(getResources().getString(R.string.sector_full_title), system, alpha, num);
        ((TextView) findViewById(R.id.sectorTitle)).setText(title);
        
        //Make sure the close button closes
        ((Button)findViewById(R.id.closeButton)).setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				shutdown();
			}
        });
	}
	
	/**
	 * Closes the activity
	 */
	private void shutdown(){
		this.finish();
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
