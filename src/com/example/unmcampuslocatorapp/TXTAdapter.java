package com.example.unmcampuslocatorapp;

/*
 * This is a custom adapter class that is made to take data from the abqbuildings.txt
 * and display it within the list view. eventually need to modify so that way it 
 * will gather data from all txt files and display in listview.
 * 
 * last method converts string to double and will be used when location is clicked on
 * so that lat and long are passed to map methods as doubles and not strings
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



public class TXTAdapter extends ArrayAdapter<AssetsReader> {
	Context ctx;

	public TXTAdapter(Context context, int resource) {
		super(context, resource);

		this.ctx = context;
		
		// load the data
		loadArrayFromFile();
		
		
	}
	
	@Override
	public View getView (final int pos, View convertView, final ViewGroup parent) {
		
		TextView mView = (TextView) convertView;
		
		if(null == mView) {
			mView = new TextView(parent.getContext());
			mView.setTextSize(16);
		}
		
		mView.setText(getItem(pos).getBuildingNum() + " " + getItem(pos).getTitle());
		//mView.setText(getItem(pos).getBuildingNum());
		
		return mView;
	}
	
	
	
	public void loadArrayFromFile() {
		
		
		try {
		// get stream and buffer reader for file
			InputStream IN = ctx.getAssets().open("abqbuildings.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(IN));
			String line;
		
		// read each line
			while ((line = reader.readLine()) != null) {
			
			// split to separate the attributes in text file
				String[] RowData = line.split(",");
			
			// create objects for rowdata
				AssetsReader current = new AssetsReader();
				current.setTitle(RowData[0]); // grab the title from file
				current.setBuildingNum(RowData[1]); // grab the building num
				current.setLatitude(RowData[2]); // grab lat
				current.setLongitude(RowData[3]); // grab long
			
			// add object to array list
				this.add(current);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
