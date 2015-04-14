package com.android.unmcampuslocatorapp;

import com.android.unmcampuslocatorapp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;

public class ErrorLoadingScreen extends Activity {
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3500;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this,
	            ErrorLoadingScreen.class));
        setContentView(R.layout.error_loading_activity);
        new AlertDialog.Builder(this).setTitle("ERROR").setMessage("This app has crashed due to an unexpected error. The app will now restart.").setNeutralButton("Close", null).show();
        
        AnimationUtils.loadAnimation(this, R.anim.anim_translate); //load animation method
        
        /* New Handler to start the Menu-Activity 
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
        	
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(ErrorLoadingScreen.this, MenuMain.class);
                ErrorLoadingScreen.this.startActivity(mainIntent);
                ErrorLoadingScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}