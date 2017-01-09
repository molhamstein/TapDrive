package com.brain_socket.tapdrive;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import com.github.florent37.viewanimator.ViewAnimator;

import com.facebook.appevents.AppEventsLogger;

public class SplashScreen extends AppCompatActivity {

    Handler handler;
    Runnable proceedRunnable = new Runnable() {
        @Override
        public void run() {
            goToMain();
            finish() ;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler() ;
        handler.postDelayed(proceedRunnable, 3000);
        //AppEventsLogger.activateApp(this);
        TapApp.requestLastUserKnownLocation();

        setContentView(R.layout.activity_splash_screen);

        // animate
        View vName = findViewById(R.id.ivName);
        View vLogo = findViewById(R.id.ivLogo);

        vLogo.setAlpha(0);
        vName.setAlpha(0);
        ViewAnimator.animate(vLogo).startDelay(700).dp().translationY(100, 0).alpha(0,1).duration(1000)
                .interpolator(new OvershootInterpolator())
                .start();
        ViewAnimator.animate(vName).startDelay(900).dp().translationY(90, 0).alpha(0,1).duration(1000)
                .interpolator(new OvershootInterpolator())
                .start();

    }

    @Override
    public void onResume(){
        super.onResume();
        //registerReceivers();
    }

    @Override
    public void onPause(){
        super.onPause();
        //unregisterReceivers();
    }


    private void goToMain(){
        Intent i = new Intent(this, IntroActivity.class);
        startActivity(i);
    }

}
