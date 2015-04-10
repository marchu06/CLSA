package com.example.unmcampuslocatorapp;

import android.app.Activity;
import android.os.Bundle;

public class ZoomPicture extends Activity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this,
	            ErrorLoadingScreen.class));
        setContentView(R.layout.zoom_pic);
    }
}