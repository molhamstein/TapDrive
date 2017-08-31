package com.brain_socket.tapdrive.controllers.onBoarding;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerAccess;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.utils.TapApp;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etToken;
    private EditText etNewPsw;
    private Dialog loadingDialog;

    // passed data
    private boolean isPartner;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        try{
            email = getIntent().getStringExtra("email");
            isPartner = getIntent().getBooleanExtra("isPartner", false);
        }catch (Exception e){
            e.printStackTrace();
        }
        init();
    }

    private void init(){
        try{
            loadingDialog = TapApp.getNewLoadingDilaog(this);
            etToken = (EditText)findViewById(R.id.etToken);
            etNewPsw = (EditText)findViewById(R.id.etNewPsw);
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

            String token = etToken.getText().toString();
            if(token.isEmpty()){
                cancel = true;
                etToken.setError(getString(R.string.activity_register_field_required));
                focusView = etToken;
            }

            String newPsw = etNewPsw.getText().toString();
            if(newPsw.isEmpty()){
                cancel = true;
                etNewPsw.setError(getString(R.string.activity_register_field_required));
                focusView = etNewPsw;
            }

            if(cancel){
                focusView.requestFocus();
                return;
            }

            loadingDialog.show();
            if (isPartner) {
                DataStore.getInstance().attemptResetPartnerPassword(email, token, newPsw, forgetPasswordCallback);
            } else {
                DataStore.getInstance().attemptResetUserPassword(email, token, newPsw, forgetPasswordCallback);
            }
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
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.activity_forget_password_request_sent), Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    if(result.getApiError().equals(ServerAccess.USER_NOT_EXIST))
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.activity_forget_password_user_not_found), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.activity_forget_password_request_failed), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.activity_forget_password_request_failed), Toast.LENGTH_LONG).show();
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
