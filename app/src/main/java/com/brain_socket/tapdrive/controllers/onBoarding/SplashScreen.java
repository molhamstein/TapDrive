package com.brain_socket.tapdrive.controllers.onBoarding;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.MainActivity;
import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.filters.Category;
import com.brain_socket.tapdrive.utils.TapApp;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity implements DataStore.DataRequestCallback {

    Handler handler;
    Runnable proceedToIntroRunnable = new Runnable() {
        @Override
        public void run() {
            goToIntro();
            finish();
        }
    };

    Runnable proceedToMainRunnable = new Runnable() {
        @Override
        public void run() {
            goToMain();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
//        handler.postDelayed(proceedToIntroRunnable, 3000);
        //AppEventsLogger.activateApp(this);
        TapApp.requestLastUserKnownLocation();

        setContentView(R.layout.activity_splash_screen);

        // animate
        View vName = findViewById(R.id.ivName);
        View vLogo = findViewById(R.id.ivLogo);

        vLogo.setAlpha(0);
        vName.setAlpha(0);
        ViewAnimator.animate(vLogo).startDelay(700).dp().translationY(100, 0).alpha(0, 1).duration(500)
                .interpolator(new OvershootInterpolator())
                .start();
        ViewAnimator.animate(vName).startDelay(900).dp().translationY(90, 0).alpha(0, 1).duration(500)
                .interpolator(new OvershootInterpolator())
                .start();

        //Call getCategories API
        DataStore.getInstance().getCategories(this);
        DataStore.getInstance().getCountries(null);

    }

    @Override
    public void onResume() {
        super.onResume();
        //registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregisterReceivers();
    }

    private void goToIntro() {
        Intent i = new Intent(this, IntroActivity.class);
        startActivity(i);
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onDataReady(ServerResult result, boolean success) {
        boolean isLoggedInUser = !DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN).equalsIgnoreCase("");
        if (success) {
            if (!isLoggedInUser && DataStore.getInstance().isFirstRun()) {
                handler.postDelayed(proceedToIntroRunnable, 1500);
            } else {
                handler.post(proceedToMainRunnable);
            }
        } else {
            ArrayList<Category> categories = DataCacheProvider.getInstance().getStoredArrayWithKey(DataCacheProvider.KEY_APP_CATEGORIES, new TypeToken<Category>() {
            }.getType());
            if (categories != null) {

                if (categories.size() > 0) {
                    handler.post(proceedToMainRunnable);
                    if (!isLoggedInUser && DataStore.getInstance().isFirstRun()) {
                        handler.postDelayed(proceedToIntroRunnable, 1500);
                    }
                } else {
                    showConnectionErrorDialog();
                }

            } else {
                showConnectionErrorDialog();
            }
        }
    }

    private void showConnectionErrorDialog() {

        new AlertDialog.Builder(this)
                .setMessage(R.string.err_connection)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataStore.getInstance().getCategories(SplashScreen.this);
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SplashScreen.this.finish();
                    }
                })
                .show();

    }

}
