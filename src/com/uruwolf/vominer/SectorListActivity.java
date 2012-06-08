package com.uruwolf.vominer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SectorListActivity extends Activity {

	private String mineral;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sector_list);
		
		mineral = getIntent().getExtras().getString("mineral");
		((TextView)findViewById(R.id.search_result_explain)).setText(
				String.format(getResources().getString(R.string.search_result_explain), mineral)
			);
		
		ListView list = (ListView)findViewById(R.id.search_result);
		//Do something smart to build a list of sectors
		List<String> sectors = new ArrayList<String>();
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
	        		android.R.layout.simple_list_item_1,
	        		sectors);
		
		list.setAdapter(listAdapter);
	}
}
