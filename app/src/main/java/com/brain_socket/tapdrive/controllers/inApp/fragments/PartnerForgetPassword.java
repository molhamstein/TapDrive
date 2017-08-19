package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.customViews.EditTextCustomFont;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerAccess;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.utils.Helpers;
import com.brain_socket.tapdrive.utils.TapApp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PartnerForgetPassword extends Fragment {

    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.hint_text)
    TextViewCustomFont hintText;
    @BindView(R.id.etEmail)
    EditTextCustomFont etEmail;
    @BindView(R.id.btnSubmit)
    TextViewCustomFont btnSubmit;
    Unbinder unbinder;

    private Dialog loadingDialog;

    private ArrayList<View> uiElements;

    public static PartnerForgetPassword newInstance() {
        PartnerForgetPassword fragment = new PartnerForgetPassword();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_partner_login_forget, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);

        if (uiElements == null) uiElements = new ArrayList<View>();
        uiElements.add(ivLogo);
        uiElements.add(hintText);
        uiElements.add(etEmail);
        uiElements.add(btnSubmit);

        hideUiElements();

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = TapApp.getNewLoadingDilaog(getActivity());

    }

    private void hideUiElements() {
        if (uiElements != null && uiElements.size() > 0) {
            for (View v : uiElements) {
                v.setAlpha(0);
            }
        }
    }

    private void animateUiElementsArray() {
        if (uiElements != null && uiElements.size() > 0) {
            for (int i = 0; i < uiElements.size(); i++) {
                animatePageUiElements(uiElements.get(i), ((i + 1) * 130));
            }
        }
    }

    private void animatePageUiElements(View v, int delay) {
        com.github.florent37.viewanimator.ViewAnimator.animate(v).startDelay(delay).dp().translationY(30, 0).alpha(0, 1).duration(500)
                .interpolator(new OvershootInterpolator())
                .start();
    }

    @Override
    public void onStart() {
        super.onStart();
        animateUiElementsArray();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnSubmit)
    public void onViewClicked() {
        forgetPassword();
    }

    private void forgetPassword() {
        try {
            boolean cancel = false;
            View focusView = null;

            String email = etEmail.getText().toString();
            if (email.isEmpty()) {
                cancel = true;
                etEmail.setError(getString(R.string.activity_register_field_required));
                focusView = etEmail;
            } else {
                if (!Helpers.isValidEmail(email)) {
                    cancel = true;
                    focusView = etEmail;
                    etEmail.setError(getString(R.string.activity_register_email_invalid));
                }
            }

            if (cancel) {
                focusView.requestFocus();
                return;
            }

            loadingDialog.show();
            DataStore.getInstance().attemptForgetUserPassword(email, forgetPasswordCallback);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    DataStore.DataRequestCallback forgetPasswordCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            loadingDialog.dismiss();
            if (success) {
                if (result.getApiError().equals("")) {
                    Toast.makeText(getActivity(), "Your request was sent successfully, please check your email", Toast.LENGTH_LONG).show();
                } else {
                    if (result.getApiError().equals(ServerAccess.USER_NOT_EXIST)) {
                        Toast.makeText(getActivity(), getString(R.string.activity_forget_password_user_not_found), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.activity_forget_password_request_failed), Toast.LENGTH_LONG).show();
            }
        }
    };

}
