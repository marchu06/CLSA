package com.example.unmcampuslocatorapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SubActivity extends Activity{

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sub_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{

		case R.id.help:
			Intent i = new Intent(this, HelpPage.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    setContentView(R.layout.sub_activity);
	    
	    RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
	    radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	    	@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
	    	{
	    		switch(checkedId)
	    		{
	    		case R.id.radioAlphabetically:
	    			//CALL METHOD TO SORT LIST ALPHABETICALLY BY NAME
	    		case R.id.radioNumerically:
	    			//CALL METHOD TO SORT LIST NUMERICALLY
	    			//
	    		}
	    	}
	    });
	   
	    // create object adapter from TXTAdapter class
	    TXTAdapter adapter;
	    
	    // get id of the listview we are using (only one)
	    ListView listview = (ListView)findViewById(R.id.listView1);
	    
	    // -1 value is dummy, constructor of TXTAdapter required it
	    adapter = new TXTAdapter(this, -1);
	    listview.setAdapter(adapter);
	    
	    // on item click what do we want to do?? 
	    /* in theory we want to send the lat and long coordinates to the 
	     * map methods to display the location on the map.
	     */
	    listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 
				// TODO Auto-generated method stub
				// set up an intent to pass data when item from row is clicked
				
				Intent intent = new Intent(SubActivity.this, MapActivity.class);
				
				//intent.putExtra("latitude", AssetsReader.getLatitude());
				//intent.putExtra("longitude", AssetsReader.getLongitude());
				//setResult(RESULT_OK, intent);
				startActivityForResult(intent, 0);

			}
			
	    });
	    
	    
	}
}
