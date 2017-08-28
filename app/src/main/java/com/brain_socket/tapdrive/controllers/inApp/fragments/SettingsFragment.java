package com.brain_socket.tapdrive.controllers.inApp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.onBoarding.SplashScreen;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.utils.LocalizationHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingsFragment extends Fragment {

    @BindView(R.id.english_language_button)
    RadioButton englishLanguageButton;
    @BindView(R.id.arabic_language_button)
    RadioButton arabicLanguageButton;
    @BindView(R.id.terms_of_service_button)
    RelativeLayout termsOfServiceButton;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    Unbinder unbinder;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);
        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String locale = LocalizationHelper.getCurrentLocale();

        if (!locale.equalsIgnoreCase("")) {
            if (locale.equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                englishLanguageButton.setChecked(true);
            } else {
                arabicLanguageButton.setChecked(true);
            }
        } else {
            if (LocalizationHelper.getDeviceLocale().equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                englishLanguageButton.setChecked(true);
            } else {
                arabicLanguageButton.setChecked(true);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.english_language_button, R.id.arabic_language_button, R.id.terms_of_service_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.english_language_button:
                LocalizationHelper.setLocale(getActivity(), "en");
                DataCacheProvider.getInstance().storeStringWithKey(DataCacheProvider.KEY_APP_LOCALE, LocalizationHelper.ENGLISH_LOCALE);
                TextViewCustomFont.fontFaceBold = null;
                TextViewCustomFont.fontFaceRegular = null;
                Intent intent = new Intent(getActivity(), SplashScreen.class);
                getActivity().startActivity(intent);
                getActivity().finish();
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.arabic_language_button:
                LocalizationHelper.setLocale(getActivity(), "ar");
                DataCacheProvider.getInstance().storeStringWithKey(DataCacheProvider.KEY_APP_LOCALE, LocalizationHelper.ARABIC_LOCALE);
                TextViewCustomFont.fontFaceBold = null;
                TextViewCustomFont.fontFaceRegular = null;
                Intent intent1 = new Intent(getActivity(), SplashScreen.class);
                getActivity().startActivity(intent1);
                getActivity().finish();
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            case R.id.terms_of_service_button:
                break;
        }
    }
}
