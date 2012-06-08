package com.uruwolf.vominer;

import com.uruwolf.vominer.data.Static;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MineralSearchActivity extends Activity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mineral_search);
		
		Spinner systemSpinner = (Spinner) findViewById(R.id.mineralSearchList);
        ArrayAdapter<String> systemAdapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1,
              	Static.mineralList);
        systemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        systemSpinner.setAdapter(systemAdapter);
        
        ((Button) findViewById(R.id.mineralSeachButton)).setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent = new Intent(this, SectorListActivity.class);
		intent.putExtra("mineral", (String) ((Spinner)findViewById(R.id.mineralSearchList)).getSelectedItem());
		startActivity(intent);
	}
}
