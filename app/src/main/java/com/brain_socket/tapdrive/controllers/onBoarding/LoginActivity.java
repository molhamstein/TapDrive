package com.brain_socket.tapdrive.controllers.onBoarding;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.brain_socket.tapdrive.utils.Helpers;
import com.brain_socket.tapdrive.controllers.inApp.MainActivity;
import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.utils.TapApp;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.github.florent37.viewanimator.ViewAnimator;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView etEmail;
    private TextView etPassword;
    private ArrayList<View> uiElements;
    private Dialog loadingDialog;
    DataStore.DataRequestCallback loginCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            loadingDialog.dismiss();
            if (success) {
                UserModel me = (UserModel) result.getValue("appUser");
                if (me != null) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.activity_login_email_or_pass_is_wrong), Toast.LENGTH_LONG).show();
                }
            }
        }
    };

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

    private void init() {
        if (uiElements == null) uiElements = new ArrayList<View>();

        loadingDialog = TapApp.getNewLoadingDilaog(this);
        View ivLogo = findViewById(R.id.ivLogo);
        etEmail = (TextView) findViewById(R.id.etEmail);
        etPassword = (TextView) findViewById(R.id.etPassword);
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

    private void hideUiElement() {
        if (uiElements != null && uiElements.size() > 0) {
            for (View v : uiElements)
                v.setAlpha(0);
        }
    }

    private void createNewUser() {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
        finish();
    }

    private void animateUiElementsArray() {
        if (uiElements != null && uiElements.size() > 0) {
            for (int i = 0; i < uiElements.size(); i++) {
                animateLoginPageUiElements(uiElements.get(i), ((i + 1) * 130));
            }
        }
    }

    private void animateLoginPageUiElements(View v, int delay) {

        ViewAnimator.animate(v).startDelay(delay).dp().translationY(30, 0).alpha(0, 1).duration(500)
                .interpolator(new OvershootInterpolator())
                .start();
    }

    private void forgetPassword() {
        Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {
            case R.id.btnForgetPassword:
                forgetPassword();
                break;
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnFacebookLogin:
                break;
            case R.id.btnTwitterLogin:
                break;
            case R.id.btnNewUser:
                createNewUser();
                break;
            case R.id.btnSkipLogin:
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                break;
        }
    }

    private void login() {
        try {
            boolean cancel = false;
            View focusView = null;

            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty()) {
                cancel = true;
                etEmail.setError(getString(R.string.activity_login_field_required));
                focusView = etEmail;
            } else {
                if (!Helpers.isValidEmail(email)) {
                    cancel = true;
                    etEmail.setError(getString(R.string.activity_login_email_invalid));
                    focusView = etEmail;
                }
            }

            if (password.isEmpty()) {
                cancel = true;
                etPassword.setError(getString(R.string.activity_login_field_required));
                focusView = etPassword;
            }

            if (cancel) {
                focusView.requestFocus();
                return;
            }

            loadingDialog.show();
            DataStore.getInstance().attemptLogin(email, password, "", "", loginCallback);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
