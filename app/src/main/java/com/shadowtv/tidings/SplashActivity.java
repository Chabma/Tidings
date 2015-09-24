package com.shadowtv.tidings;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Cameron Abma on 7/29/2015.
 */
public class SplashActivity extends Activity {

    private static String TAG = SplashActivity.class.getName();
    private static long MAX_SPLASH_TIME = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.splash1);

        new Thread() {
            @Override
            public void run() {
                synchronized (HomeActivity.SPLASH_LOCK) {
                    // wait for notify or time-out
                    try { HomeActivity.SPLASH_LOCK.wait(MAX_SPLASH_TIME); }
                    catch (InterruptedException ignored) {}
                }
                finish();
            }
        }.start();
    }

}