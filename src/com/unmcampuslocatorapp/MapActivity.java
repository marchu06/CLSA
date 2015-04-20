package com.unmcampuslocatorapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.*;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unmcampuslocatorapp.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity{

	private SupportMapFragment mapFragment;
	private GoogleMap map;
	public Location loc;
	protected Bundle intent;
	private LocationManager locManager;
	private static boolean RUN_ONCE = true;
	private LatLng unm = new LatLng(35.0843, -106.62);

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
			if (!isNetworkAvailable())
			{
				NoNetworkConnectionDialogFragment dialog = new NoNetworkConnectionDialogFragment();
				dialog.show(getFragmentManager(), "dialog");
				return true;
			}
			else
			{
				Intent i = new Intent(this, SubActivity.class);
				startActivityForResult(i, 100);
				return true;}
		case R.id.help:        //********//
			Intent i2 = new Intent(this, HelpPage.class);
			startActivity(i2);
			return true;	
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);		
		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		map = mapFragment.getMap();
		map.getUiSettings().setZoomControlsEnabled(true);  //********//
		map.getUiSettings().setZoomGesturesEnabled(true);  //********//
		map.getUiSettings().setRotateGesturesEnabled(true); //********//
		
		map.setMyLocationEnabled(true);
		locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if(locManager.isProviderEnabled(locManager.GPS_PROVIDER))
		{
			Location firstLoc = locManager.getLastKnownLocation(locManager.PASSIVE_PROVIDER);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(firstLoc.getLatitude(), firstLoc.getLongitude()), 17));	
		}
		else
		{
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(unm, 17));	
			runOnce();
		}

		// bundle and intent used to change camera location if user has selected coordinates from list
		intent = getIntent().getExtras();
		if (intent != null) {
			Location location = null;
			String latitude = intent.getString("latitude");
			String longitude = intent.getString("longitude");
			
			double lat = convertStringToDouble(latitude);
			double longi = convertStringToDouble(longitude);
						
			map.getMyLocation();
			
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			if (locManager.isProviderEnabled(locManager.GPS_PROVIDER))
			{
				location = locationManager.getLastKnownLocation(locationManager.PASSIVE_PROVIDER);

				double lat2 = location.getLatitude();
				double long2 = location.getLongitude();
			
				LatLng origin = new LatLng(lat2, long2);
				LatLng dest = new LatLng(lat, longi);

				// Getting URL to the Google Directions API
				String url = getDirectionsUrl(origin, dest);

				DownloadTask downloadTask = new DownloadTask();

				// Start downloading json data from Google Directions API
				downloadTask.execute(url);
			}
			
			
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 17);
			map.animateCamera(cameraUpdate);
			
			LatLng buildingSelected = new LatLng(lat, longi);
			Marker marker;
			if (intent.getString("title").equals("Centennial Library") ||
					intent.getString("title").equals("Electrical And Computer Engineering"))
				map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
			
			marker = map.addMarker(new MarkerOptions().title(intent.getString("title"))
						.snippet(intent.getString("abbr")).position(buildingSelected));
			marker.showInfoWindow();
		}
		
		while (map.equals(null))
			map = mapFragment.getMap();
		    
		getLocationListener(locManager.GPS_PROVIDER);
		
    	map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

	        @Override
	        public void onInfoWindowClick(Marker arg0) {
	            // TODO Auto-generated method stub
	        	if (arg0.getTitle().equals("Centennial Library"))
	        	{
	        		Intent intent = new Intent(getBaseContext(), ZoomPicture.class);
	        		startActivity(intent);
	        	}
	        }
    	});
		
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

	private void runOnce() {
		if (RUN_ONCE){
			RUN_ONCE = false;
			NoGpsEnabledDialogFragment dialog = new NoGpsEnabledDialogFragment();
			dialog.show(getFragmentManager(), "dialog");	
		}
	}

    private String getDirectionsUrl(LatLng origin,LatLng dest){
    	 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Travelling Mode
        String mode; //= "mode=driving";
 
        //if(rbWalking.isChecked()){
            mode = "mode=walking";
          //  mMode = 2;
        //}
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
    
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    
    private class DownloadTask extends AsyncTask<String, Void, String>{
    	 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            new MarkerOptions();
 
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
 
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
 
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
 
                // Changing the color polyline according to the mode
                //if(mMode==MODE_WALKING)
                    lineOptions.color(Color.RED);
                    lineOptions.width(5);
            }
 
            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }
 
            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }

	public static double convertStringToDouble (String arg) {
		double aDouble = Double.parseDouble(arg);
		return aDouble;
	}
		
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private void getLocationListener(String provider)
	{

		locManager.requestLocationUpdates(provider, 2000, 1, new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(
						new LatLng(arg0.getLatitude(), arg0
								.getLongitude()), 17));
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderDisabled(String provider) {

			}

			@Override
			public void onProviderEnabled(String provider) {
				RUN_ONCE = true;
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub	
			}	
			});
	}

	public class NoNetworkConnectionDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.no_connection)
	               .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   @Override
					public void onClick(DialogInterface dialog, int id) {
	                       dialog.cancel();
	                   }
	               })
	               .setNegativeButton(R.string.try_again, new DialogInterface.OnClickListener() {
	                   @Override
					public void onClick(DialogInterface dialog, int id) {
	                       dialog.dismiss();
	                       View view = findViewById(R.id.menu_button);
                    	   view.performClick(); 
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	
	public class NoGpsEnabledDialogFragment extends DialogFragment {
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setMessage(R.string.no_gps)
	               .setPositiveButton(R.string.cont, new DialogInterface.OnClickListener() {
	                   @Override
					public void onClick(DialogInterface dialog, int id) {
	                       dialog.cancel();
	                   }
	               })
	               .setNegativeButton(R.string.settings, new DialogInterface.OnClickListener() {
	                   @Override
					public void onClick(DialogInterface dialog, int id) {
	                       dialog.dismiss();
	                       startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}

	public class CustomInfoWindowAdapter implements InfoWindowAdapter 
	{		
	    public CustomInfoWindowAdapter() 
	    {	    	
	    }

		@Override
		public View getInfoContents(Marker arg0) {
			
			View v  = getLayoutInflater().inflate(R.layout.custom_info_window, null);
			
	        ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);
	        ImageView markerIcon2 = (ImageView) v.findViewById(R.id.marker_icon2);
	        ImageView markerIcon3 = (ImageView) v.findViewById(R.id.marker_icon3);

	        TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

	        if(intent.getString("title").equals("Centennial Library"))
	        {
		        markerIcon.setImageResource(R.drawable.centennial2);
		        markerIcon2.setImageResource(R.drawable.b1new);
		        markerIcon3.setImageResource(R.drawable.b2new);	
	        }
	        else if (intent.getString("title").equals("Electrical And Computer Engineering"))
	        {
		        markerIcon.setImageResource(R.drawable.ece);
	        }

	        SpannableString string = new SpannableString(intent.getString("title"));
	        string.setSpan(new StyleSpan(Typeface.BOLD), 0, string.length(), 0);
	        markerLabel.append(string);
	        if(intent.getString("abbr") != null)
	        	markerLabel.append("\n" + intent.getString("abbr"));
	        if (intent.getString("title").equals("Centennial Library") || intent.getString("title").equals(
	        		"Electrical And Computer Engineering"))
	        	markerLabel.append("\n" + intent.getString("description"));
	        
	        return v;
		
		
	    }
		
		
		@Override
		public View getInfoWindow(Marker arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}