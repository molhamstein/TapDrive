package com.brain_socket.tapdrive.controllers.inApp.fragments;

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
    @BindView(R.id.ivName)
    ImageView ivName;
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
                break;
            case R.id.arabic_language_button:
                break;
            case R.id.terms_of_service_button:
                break;
        }
    }
}
