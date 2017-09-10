package com.brain_socket.tapdrive.controllers.onBoarding;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.brain_socket.tapdrive.data.FacebookProvider;
import com.brain_socket.tapdrive.data.FacebookProviderListener;
import com.brain_socket.tapdrive.utils.Helpers;
import com.brain_socket.tapdrive.controllers.inApp.MainActivity;
import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.utils.TapApp;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.facebook.Profile;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 7;

    private TextView etEmail;
    private TextView etPassword;
    private ArrayList<View> uiElements;
    private Dialog loadingDialog;

    private GoogleApiClient mGoogleApiClient;

    DataStore.DataRequestCallback loginCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            loadingDialog.hide();
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
        if (uiElements == null) uiElements = new ArrayList<>();

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

        // init toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        mToolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        setSupportActionBar(mToolbar);
        setTitle(R.string.activity_login_title);

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

        // hide all ui elements
        hideUiElement();

        btnForgetPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnFacebookLogin.setOnClickListener(this);
        btnTwitterLogin.setOnClickListener(this);
        btnNewUser.setOnClickListener(this);
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

        v.setAlpha(0);
        ViewAnimator.animate(v).startDelay(delay).dp().translationY(30, 0).alpha(0, 1).duration(500)
                .interpolator(new OvershootInterpolator())
                .start();
    }

    private void forgetPassword() {
        Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        i.putExtra("isPartner", false);
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
                attempFBtLogin();
                break;
            case R.id.btnTwitterLogin:
                googleSignin();
                break;
            case R.id.btnNewUser:
                createNewUser();
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
            DataStore.getInstance().attemptLogin(email, password, null, "", "", loginCallback);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FacebookProvider.getInstance().onActiviyResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
    }

    // facebook login
    FacebookProviderListener facebookLoginListner = new FacebookProviderListener() {

        @Override
        public void onFacebookSessionOpened(String accessToken, String userId, HashMap<String, Object> map) {

            //String FbToken = accessToken;
            Profile profile = com.facebook.Profile.getCurrentProfile();
            String fullName = profile.getFirstName()+" "+profile.getLastName();
            String id = profile.getId();
            String email = (String) map.get("email");

            FacebookProvider.getInstance().unregisterListener();
            DataStore.getInstance().attemptLogin(email, "", fullName, id, "Facebook", loginCallback);
        }

        @Override
        public void onFacebookSessionClosed() {
        }

        @Override
        public void onFacebookException(Exception exception) {

        }
    };

    /**
     * try login first using facebook if success then singning up to the API Server using the
     * facebook Id and phone number entered in the previous stage
     */
    public void attempFBtLogin() {
        //Session.openActiveSession(this, true, permissions, callback)
        FacebookProvider.getInstance().registerListener(facebookLoginListner);
        FacebookProvider.getInstance().requestFacebookLogin(this);
        //Session.StatusCallback callback =  new LoginStatsCallback() ;
        //Session.openActiveSession(LoginActivity.this, true, perm1, callback ) ;
        loadingDialog.show();
    }


    // google signup
    private void googleSignin(){
        loadingDialog.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

//    DataStore.DataRequestCallback attemptSocialLoginCallback = new DataStore.DataRequestCallback() {
//        @Override
//        public void onDataReady(ServerResult result, boolean success) {
//            try{
//                loadingDialog.hide();
//                UserModel me = DataStore.getInstance().getMe();
//
//                // if user logged in successfully, close login activity
//                if(me != null) {
//                    setResult(RESULT_OK);
//                    finish();
//                }else{
//                    TapApp.toast(getString(R.string.err_connection));
//                }
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//        }
//    };

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                String fullName = acct.getDisplayName();
                //String personPhotoUrl = acct.getPhotoUrl().toString();
                String email = acct.getEmail();
                String id = acct.getId();
                DataStore.getInstance().attemptLogin(email, "", fullName, id, "Google+", loginCallback);
            }
        } else {
          loadingDialog.dismiss();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
