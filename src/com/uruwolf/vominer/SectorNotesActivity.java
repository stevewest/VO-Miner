package com.uruwolf.vominer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class SectorNotesActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.sector_notes);
        
        Bundle extras = getIntent().getExtras();
        String message = extras.getString("system");
        
        ((TextView) findViewById(R.id.sectorTitle)).setText(message);
	}
}
