package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.MainActivity;
import com.brain_socket.tapdrive.controllers.onBoarding.LoginActivity;
import com.brain_socket.tapdrive.customViews.EditTextCustomFont;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.brain_socket.tapdrive.utils.Helpers;
import com.brain_socket.tapdrive.utils.TapApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PartnerLoginFragment extends Fragment {


    @BindView(R.id.etEmail)
    EditTextCustomFont etEmail;
    @BindView(R.id.etPassword)
    EditTextCustomFont etPassword;
    @BindView(R.id.btnForgetPassword)
    TextViewCustomFont btnForgetPassword;
    @BindView(R.id.btnLogin)
    TextViewCustomFont btnLogin;
    Unbinder unbinder;

    private Dialog loadingDialog;

    public PartnerLoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_partner_login, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);
        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = TapApp.getNewLoadingDilaog(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btnForgetPassword, R.id.btnLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnForgetPassword:
                break;
            case R.id.btnLogin:
                login();
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
            DataStore.getInstance().attemptPartnerLogin(email, password, "", "", loginCallback);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    DataStore.DataRequestCallback loginCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            loadingDialog.dismiss();
            if (success) {
                UserModel me = (UserModel) result.getValue("appUser");
                if (me != null) {
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.activity_login_email_or_pass_is_wrong), Toast.LENGTH_LONG).show();
                }
            }
        }
    };

}
