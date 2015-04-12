package com.example.unmcampuslocatorapp;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	Marker location;
	String latitude;
	String longitude;
	double lat, longi;
	Intent intent = getIntent();
	ImageButton btn_home;

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
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);   //********//
		
		map.setMyLocationEnabled(true);
		
		// bundle and intent used to change camera location if user has selected coordinates from list
		Bundle intent = getIntent().getExtras();
		//if (intent != null && intent.getString("latitude") != null && intent.getString("longitude") != null) {
		if (intent != null) {
			double lat, longi;
			
			
			latitude = intent.getString("latitude");
			longitude = intent.getString("longitude");
			
			lat = convertStringToDouble(latitude);
			longi = convertStringToDouble(longitude);
			
			//LatLng buildingSelected = new LatLng(lat, longi);
			
			map.getMyLocation();
			
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			String bestProvider = locationManager.getBestProvider(criteria, true);
			Location location = locationManager.getLastKnownLocation(bestProvider);
			
			double lat2 = location.getLatitude();
			double long2 = location.getLongitude();
			
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), 15);
			map.animateCamera(cameraUpdate);
			
            LatLng origin = new LatLng(lat2, long2);
            LatLng dest = new LatLng(lat, longi);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
			
			
			
			LatLng buildingSelected = new LatLng(lat, longi);
			map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
			Marker marker = map.addMarker(new MarkerOptions().position(buildingSelected));
			marker.showInfoWindow();
		}
		
		while (map.equals(null))
			map = mapFragment.getMap();
		    
		LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		/*
		LatLng ece = new LatLng(35.083166, -106.624075);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(ece, 18));
		map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		Marker marker = map.addMarker(new MarkerOptions().position(ece).title("Electrical and Computer Engineering").snippet("EECE"));
		marker.showInfoWindow(); */
		
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {
			//	map.moveCamera(CameraUpdateFactory.newLatLngZoom(
				//		new LatLng(arg0.getLatitude(), arg0
					//			.getLongitude()), 15));
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
		
    	map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

	        @Override
	        public void onInfoWindowClick(Marker arg0) {
	            // TODO Auto-generated method stub
	            Intent intent = new Intent(getBaseContext(),
	                    ZoomPicture.class);
	            startActivity(intent);
	        ;

	        }
    	});
		
		//map.setMyLocationEnabled(true);
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
		
		/*
        // Getting reference to rb_walking
        rbWalking = (RadioButton) findViewById(R.id.rb_walking);
 
        // Getting Reference to rg_modes
        rgModes = (RadioGroup) findViewById(R.id.rg_modes);
 
        rgModes.setOnCheckedChangeListener(new OnCheckedChangeListener() {
 
            public void onCheckedChanged(RadioGroup group, int checkedId) {
 
                // Checks, whether start and end locations are captured
                if(markerPoints.size() >= 2){
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);
 
                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);
 
                    DownloadTask downloadTask = new DownloadTask();
 
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            }
        });*/

        // Initializing
        //markerPoints = new ArrayList<LatLng>();
 
        // Getting reference to SupportMapFragment of the activity_main
        //SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
 
        // Getting Map for the SupportMapFragment
        //map = fm.getMap();
 
        // Enable MyLocation Button in the Map
        //map.setMyLocationEnabled(true);
 
        /*
        // Setting onclick event listener for the map
        map.setOnMapClickListener(new OnMapClickListener() {
 
            @Override
            public void onMapClick(LatLng point) {
 
                // Already two locations
                if(markerPoints.size()>1){
                    markerPoints.clear();
                    map.clear();
                }
 
                // Adding new item to the ArrayList
                markerPoints.add(point);
 
                // Draws Start and Stop markers on the Google Map
                drawStartStopMarkers();
 
                // Checks, whether start and end locations are captured
                if(markerPoints.size() >= 2){
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);
 
                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);
 
                    DownloadTask downloadTask = new DownloadTask();
 
                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);
                }
            }
        }); */
	}
	
	/*
    // Drawing Start and Stop locations
    private void drawStartStopMarkers(){
 
        for(int i = 0; i < markerPoints.size();i++){
 
            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();
 
            // Setting the position of the marker
            options.position(markerPoints.get(i) );
 
            if(i==0){
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }else if(i==1){
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
 
            // Add new marker to the Google Map Android API V2
            map.addMarker(options);
        }
    } */
    
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

	        markerIcon.setImageResource(R.drawable.centennial2);
	        markerIcon2.setImageResource(R.drawable.b1new);
	        markerIcon3.setImageResource(R.drawable.b2new);


	        SpannableString string = new SpannableString("Centennial Science & Engineering Library");
	        string.setSpan(new StyleSpan(Typeface.BOLD), 0, string.length(), 0);
	        markerLabel.append(string);
	        markerLabel.append("\n");
	        markerLabel.append("CSEL \n (Note: The new Math MaLL is located \n in the "
	        		+ "basement, in room L185) \n *Tap a pic below for a better view");
	        
	        return v;
		
		
	    }
		
		
		@Override
		public View getInfoWindow(Marker arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}