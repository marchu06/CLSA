package com.android.unmcampuslocatorapp;

import com.example.unmcampuslocatorapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HelpPage extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    setContentView(R.layout.help_activity);
	}

	public void openlists(View view) {
	    Intent i = new Intent(this , SubActivity.class);
	    startActivity(i);
	} 
}