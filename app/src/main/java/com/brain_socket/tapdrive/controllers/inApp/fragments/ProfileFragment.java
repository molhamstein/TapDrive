package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.customViews.EditTextCustomFont;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.bumptech.glide.Glide;
import com.hbb20.CountryCodePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_CODE_PICKER = 22;

    @BindView(R.id.user_profile_image)
    CircleImageView userProfileImage;
    @BindView(R.id.edit_icon)
    ImageView editIcon;
    @BindView(R.id.user_name_edit_text)
    EditTextCustomFont userNameEditText;
    @BindView(R.id.user_email_edit_text)
    EditTextCustomFont userEmailEditText;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    @BindView(R.id.etPhone)
    EditTextCustomFont userPhoneEditText;
    @BindView(R.id.vGenderSelector)
    View vGenderSelector;
    @BindView(R.id.tvMale)
    TextViewCustomFont tvMale;
    @BindView(R.id.tvFemale)
    TextViewCustomFont tvFemale;
    @BindView(R.id.etBirthday)
    EditTextCustomFont userBirthdayEditText;
    @BindView(R.id.finish_button)
    TextViewCustomFont finishButton;
    @BindView(R.id.phone_layout)
    FrameLayout phoneLayout;
    @BindView(R.id.gender_layout)
    FrameLayout genderLayout;
    Unbinder unbinder;

    private ArrayList<View> uiElements;

    private Gender gender;

    private UserModel me;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        userBirthdayEditText.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);

        me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, UserModel.class);

        if (uiElements == null) uiElements = new ArrayList<View>();
        uiElements.add(editIcon);
        uiElements.add(userProfileImage);
        uiElements.add(userNameEditText);
        uiElements.add(userEmailEditText);
        uiElements.add(phoneLayout);
        uiElements.add(genderLayout);
        uiElements.add(userBirthdayEditText);
        uiElements.add(finishButton);

        hideUiElements();

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindUserData();

    }

    @Override
    public void onStart() {
        super.onStart();
        animateUiElementsArray();
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
        com.github.florent37.viewanimator.ViewAnimator.animate(v).startDelay(delay).dp().translationY(30, 0).alpha(0, 1).duration(1000)
                .interpolator(new OvershootInterpolator())
                .start();
    }

    private void bindUserData() {

        Glide.with(getActivity()).load(me.getPhoto()).into(userProfileImage);
        userNameEditText.setText(me.getUsername());
        userEmailEditText.setText(me.getEmail());
        userPhoneEditText.setText(me.getPhone());
        userBirthdayEditText.setText(me.getBirthdate());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.edit_icon, R.id.tvMale, R.id.tvFemale, R.id.etBirthday, R.id.finish_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_icon:
                break;
            case R.id.tvMale:
                changeSelectedGender(Gender.Male);
                break;
            case R.id.tvFemale:
                changeSelectedGender(Gender.Female);
                break;
            case R.id.etBirthday:
                openDatePickerDialog();
                break;
            case R.id.finish_button:
                break;
        }
    }

    private void changeSelectedGender(Gender gen) {
        if (gender != gen) {
            if (gender == Gender.Male) {
                com.github.florent37.viewanimator.ViewAnimator.animate(vGenderSelector).dp().translationX(126).duration(400)
                        .interpolator(new OvershootInterpolator())
                        .start();
                gender = Gender.Female;
            } else {
                com.github.florent37.viewanimator.ViewAnimator.animate(vGenderSelector).dp().translationX(0).duration(400)
                        .interpolator(new OvershootInterpolator())
                        .start();
                gender = Gender.Male;
            }
        }
    }

    private void openDatePickerDialog() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                ProfileFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private enum Gender {Male, Female}

}
