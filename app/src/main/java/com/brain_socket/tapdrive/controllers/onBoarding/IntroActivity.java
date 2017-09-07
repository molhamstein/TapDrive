package com.brain_socket.tapdrive.controllers.onBoarding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.data.DataStore;
import com.prolificinteractive.parallaxpager.ParallaxContainer;
import com.prolificinteractive.parallaxpager.ParallaxContextWrapper;


public class IntroActivity extends AppCompatActivity {

    ParallaxContainer parallaxContainer;

    int currentPageIndex = 0;

    OnClickListener onNextClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (currentPageIndex >= 2) {
                Intent i = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else {
                parallaxContainer.getViewPager().setCurrentItem(parallaxContainer.getViewPager().getCurrentItem() + 1, true);
            }
            currentPageIndex ++;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        init();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ParallaxContextWrapper(newBase));
    }

    private void init() {
        parallaxContainer = (ParallaxContainer) findViewById(R.id.pcPagesContainer);

        parallaxContainer.setLooping(false);
        // wrap the inflater and inflate children with custom attributes
        parallaxContainer.setupChildren(getLayoutInflater(),
                R.layout.layout_intro_1,
                R.layout.layout_intro_2,
                R.layout.layout_intro_3);
        parallaxContainer.getViewPager().addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

//                if (position == 2) {
//                    Intent i = new Intent(IntroActivity.this,LoginActivity.class);
//                    startActivity(i);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        try {
            View btn1 = findViewById(R.id.btnNext1);
            View btn2 = findViewById(R.id.btnNext2);
            View btn3 = findViewById(R.id.btnNext3);

            View root1 = findViewById(R.id.root1);
            View root2 = findViewById(R.id.root2);
            View root3 = findViewById(R.id.root3);

            btn1.setOnClickListener(onNextClick);
            btn2.setOnClickListener(onNextClick);
            btn3.setOnClickListener(onNextClick);
            root1.setOnClickListener(onNextClick);
            root2.setOnClickListener(onNextClick);
            root3.setOnClickListener(onNextClick);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
