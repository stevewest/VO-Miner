package com.uruwolf.vominer;

import java.util.ArrayList;
import java.util.List;

import com.uruwolf.vominer.data.Sector;
import com.uruwolf.vominer.data.SectorDataSource;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Shows a list of sectors that contain the mineral given in the intent.
 * Must contain a string called "mineral" in the intent extras
 * @author Steve "Uru" West <uruwolf@gmail.com>
 *
 */
public class SectorListActivity extends Activity {

	private String mineral;
	private SectorDataSource data;
	private ArrayList<String> sectorStrings;
	private ArrayAdapter<String> listAdapter;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sector_list);
		
		//Make sure we can use the database
		data = new SectorDataSource(this);
        data.open();
		
		mineral = getIntent().getExtras().getString("mineral");
		((TextView)findViewById(R.id.search_result_explain)).setText(
				String.format(getResources().getString(R.string.search_result_explain), mineral)
			);
		
		ListView list = (ListView)findViewById(R.id.search_result);
		
		sectorStrings = new ArrayList<String>();
		
		listAdapter = new ArrayAdapter<String>(this,
	        		android.R.layout.simple_list_item_1,
	        		sectorStrings);
		
		list.setAdapter(listAdapter);
	}
	
	@Override
    public void onPause(){
    	super.onPause();
    	data.close();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	data.open();
    	
    	//Do something smart to build a list of sectors
    	List<Sector> sectors = data.getSectorsContainingMineral(mineral);
    	sectorStrings.clear();		
    	//Loop through the found sectors and build them into a list of Strings for the ListView
    	for(Sector sec : sectors){
    		sectorStrings.add(String.format(
    				getResources().getString(R.string.sector_full_title),
    				sec.getSystem(),
    				sec.getAplhaCoord(),
    				sec.getNumCoord()
    				));
    	}
    	
    	listAdapter.notifyDataSetChanged();
    }
}
