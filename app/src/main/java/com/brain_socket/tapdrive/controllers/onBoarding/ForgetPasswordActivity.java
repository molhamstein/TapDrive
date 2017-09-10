package com.brain_socket.tapdrive.controllers.onBoarding;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerAccess;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.utils.Helpers;
import com.brain_socket.tapdrive.utils.TapApp;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etEmail;
    private Dialog loadingDialog;
    private Boolean isPartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        try{
            isPartner = getIntent().getBooleanExtra("isPartner", false);
        }catch (Exception e){
            e.printStackTrace();
        }
        init();
    }

    private void init(){
        try{
            loadingDialog = TapApp.getNewLoadingDilaog(this);
            etEmail = (EditText)findViewById(R.id.etEmail);
            View btnSubmit = findViewById(R.id.btnSubmit);

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
            setSupportActionBar(mToolbar);

            btnSubmit.setOnClickListener(this);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void forgetPassword(){
        try{
            boolean cancel = false;
            View focusView = null;

            String email = etEmail.getText().toString();
            if(email.isEmpty()){
                cancel = true;
                etEmail.setError(getString(R.string.activity_register_field_required));
                focusView = etEmail;
            }else{
                if(!Helpers.isValidEmail(email)){
                    cancel = true;
                    focusView = etEmail;
                    etEmail.setError(getString(R.string.activity_register_email_invalid));
                }
            }

            if(cancel){
                focusView.requestFocus();
                return;
            }

            loadingDialog.show();
            if(isPartner)
                DataStore.getInstance().attemptForgetPartnerPassword(email,forgetPasswordCallback);
            else
                DataStore.getInstance().attemptForgetUserPassword(email,forgetPasswordCallback);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    DataStore.DataRequestCallback forgetPasswordCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            loadingDialog.dismiss();
            if(success){
                if(result.getApiError().equals("")){
                    Toast.makeText(ForgetPasswordActivity.this, getString(R.string.activity_forget_password_request_sent), Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(ForgetPasswordActivity.this, ResetPasswordActivity.class);
                            i.putExtra("email", etEmail.getText().toString());
                            i.putExtra("isPartner", isPartner);
                            startActivity(i);
                            finish();
                        }
                    },1500);
                }else{
                    if(result.getApiError().equals(ServerAccess.USER_NOT_EXIST))
                        Toast.makeText(ForgetPasswordActivity.this, getString(R.string.activity_forget_password_user_not_found), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(ForgetPasswordActivity.this, getString(R.string.activity_forget_password_request_failed), Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.btnSubmit:
                forgetPassword();
                break;
        }
    }
}
