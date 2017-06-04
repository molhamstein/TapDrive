package com.brain_socket.tapdrive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.github.florent37.viewanimator.ViewAnimator;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView etEmail;
    private TextView etPassword;
    private ArrayList<View> uiElements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animateUiElementsArray();
    }

    private void init(){
        if(uiElements == null) uiElements = new ArrayList<View>();

        View ivLogo = findViewById(R.id.ivLogo);
        etEmail = (TextView)findViewById(R.id.etEmail);
        etPassword = (TextView)findViewById(R.id.etPassword);
        View btnForgetPassword = findViewById(R.id.btnForgetPassword);
        View btnLogin = findViewById(R.id.btnLogin);
        View btnFacebookLogin = findViewById(R.id.btnFacebookLogin);
        View btnTwitterLogin = findViewById(R.id.btnTwitterLogin);
        View btnNewUser = findViewById(R.id.btnNewUser);
        View vSeperator = findViewById(R.id.vSeperator);
        View tvOrConnectWith = findViewById(R.id.tvOrConnectWith);
        View btnSkipLogin = findViewById(R.id.btnSkipLogin);

        uiElements.add(ivLogo);
        uiElements.add(etEmail);
        uiElements.add(etPassword);
        uiElements.add(btnForgetPassword);
        uiElements.add(btnLogin);
        uiElements.add(btnNewUser);
        uiElements.add(vSeperator);
        uiElements.add(tvOrConnectWith);
        uiElements.add(btnFacebookLogin);
        uiElements.add(btnTwitterLogin);
        uiElements.add(btnSkipLogin);

        // hide all ui elements
        hideUiElement();

        btnForgetPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnFacebookLogin.setOnClickListener(this);
        btnTwitterLogin.setOnClickListener(this);
        btnNewUser.setOnClickListener(this);
        btnSkipLogin.setOnClickListener(this);
    }

    private void hideUiElement(){
        if(uiElements != null && uiElements.size() > 0){
            for(View v : uiElements)
                v.setAlpha(0);
        }
    }

    private void createNewUser(){
        Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(i);
    }

    private void animateUiElementsArray(){
        if(uiElements != null && uiElements.size() > 0){
            for(int i=0;i<uiElements.size();i++){
                animateLoginPageUiElements(uiElements.get(i),((i+1)*130));
            }
        }
    }

    private void animateLoginPageUiElements(View v,int delay){

        ViewAnimator.animate(v).startDelay(delay).dp().translationY(30, 0).alpha(0,1).duration(1000)
                .interpolator(new OvershootInterpolator())
                .start();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId){
            case R.id.btnForgetPassword:
                break;
            case R.id.btnLogin:

                break;
            case R.id.btnFacebookLogin:
                break;
            case R.id.btnTwitterLogin:
                break;
            case R.id.btnNewUser:
                createNewUser();
                break;
            case R.id.btnSkipLogin:
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
                break;
        }
    }
}
