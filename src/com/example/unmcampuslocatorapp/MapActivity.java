package com.example.unmcampuslocatorapp;

import android.content.Intent;
import android.location.*;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapActivity extends FragmentActivity{

	private SupportMapFragment mapFragment;
	private GoogleMap map;
	public Location loc;
	Marker location;
	String latitude;
	String longitude;
	double lat, longi;
	Intent intent = getIntent();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.menu_button:   //********//
			Intent i = new Intent(this, SubActivity.class);
			startActivityForResult(i, 100);
			return true;
		case R.id.help:        //********//
			Intent i2 = new Intent(this, HelpPage.class);
			startActivity(i2);
			return true;	
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 0 && data != null)
		{
			latitude = data.getStringExtra("latitude");
			longitude = data.getStringExtra("longitude");
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		Runtime runtime = Runtime.getRuntime();
	    long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
	    System.out.println("Used Memory before" + usedMemoryBefore);
	        // working code here
		
		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		map = mapFragment.getMap();
		map.getUiSettings().setZoomControlsEnabled(true);  //********//
		map.getUiSettings().setZoomGesturesEnabled(true);  //********//
		map.getUiSettings().setRotateGesturesEnabled(true); //********//
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);   //********//
		
		while (map.equals(null))
			map = mapFragment.getMap();
		    
		LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(arg0.getLatitude(), arg0
								.getLongitude()), 15));
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub	
			}	
			});
		
		long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
	    System.out.println("Memory increased:" + (usedMemoryAfter-usedMemoryBefore));
		
		
		map.setMyLocationEnabled(true);
		if (mapFragment != null) {
			map = mapFragment.getMap();
			
			if (map != null) {
			}
			else {			
				Toast.makeText(this, "Map failed! Please restart your device"
						+ "or see our Help page", Toast.LENGTH_LONG).show();
			}
		} 
		else {
			Toast.makeText(this, "Map failed! Please restart your device"
						+ "or see our Help page", Toast.LENGTH_LONG).show();
		}	
	}

}