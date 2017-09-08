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
import com.brain_socket.tapdrive.controllers.inApp.MainActivity;
import com.brain_socket.tapdrive.customViews.EditTextCustomFont;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.brain_socket.tapdrive.utils.Helpers;
import com.brain_socket.tapdrive.utils.TapApp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class InnerLoginFragment extends Fragment {

    public static int PARTNER_LOGIN = 0;
    public static int USER_LOGIN = 1;

    @BindView(R.id.etEmail)
    EditTextCustomFont etEmail;
    @BindView(R.id.etPassword)
    EditTextCustomFont etPassword;
    @BindView(R.id.btnForgetPassword)
    TextViewCustomFont btnForgetPassword;
    @BindView(R.id.btnLogin)
    TextViewCustomFont btnLogin;
    @BindView(R.id.hint_text)
    TextViewCustomFont hintText;
    @BindView(R.id.app_logo)
    ImageView appLogo;
    Unbinder unbinder;

    private Dialog loadingDialog;

    private ArrayList<View> uiElements;

    private int screenType;

    public InnerLoginFragment() {
        // Required empty public constructor
    }

    public static InnerLoginFragment newInstance(int screenType) {
        InnerLoginFragment fragment = new InnerLoginFragment();
        fragment.setScreenType(screenType);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_partner_login, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);

        if (uiElements == null) uiElements = new ArrayList<View>();
        uiElements.add(appLogo);
        uiElements.add(hintText);
        uiElements.add(etEmail);
        uiElements.add(etPassword);
        uiElements.add(btnForgetPassword);
        uiElements.add(btnLogin);

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

    @OnClick({R.id.btnForgetPassword, R.id.btnLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnForgetPassword:
                ((MainActivity) getActivity()).openInnerForgetPasswordScreen(screenType);
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

            if (screenType == PARTNER_LOGIN) {
                DataStore.getInstance().attemptPartnerLogin(email, password, loginCallback);
            } else {
                DataStore.getInstance().attemptLogin(email, password, "", "", "", loginCallback);
            }
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
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.activity_login_email_or_pass_is_wrong), Toast.LENGTH_LONG).show();
            }
        }
    };

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }
}
