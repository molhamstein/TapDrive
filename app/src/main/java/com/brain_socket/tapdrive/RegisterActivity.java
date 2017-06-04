package com.brain_socket.tapdrive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.hbb20.CountryCodePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener,DatePickerDialog.OnDateSetListener {
    private enum Gender {Male,Female};
    private ArrayList<View> uiElements;
    private ViewAnimator vaRegisterSteps;
    private int registerStep = 1;
    private View tvStep1,tvStep2,tvStep3;
    private View vHorizontalBlackLine;
    private TextView btnNext;
    private EditText etEmail,etPassword,etPhone,etFullName;
    private View vGenderSelector;
    private EditText etBirthday;
    private CountryCodePicker ccp;
    private View llAlreadyHaveAccount;

    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String birthdate;
    private String address;
    private Gender gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        animateUiElementsArray();
    }

    private void init(){
        if(uiElements == null) uiElements = new ArrayList<View>();

        gender = Gender.Male;
        vGenderSelector = findViewById(R.id.vGenderSelector);
        View ivLogo = findViewById(R.id.ivLogo);
        View stepsView = findViewById(R.id.stepsView);
        View btnLogin = findViewById(R.id.btnLogin);
        btnNext = (TextView) findViewById(R.id.btnNext);
        tvStep1 = findViewById(R.id.tvStep1);
        tvStep2 = findViewById(R.id.tvStep2);
        tvStep3 = findViewById(R.id.tvStep3);
        vHorizontalBlackLine = findViewById(R.id.vHorizontalBlackLine);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etFullName = (EditText) findViewById(R.id.etFullName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        View tvMale = findViewById(R.id.tvMale);
        View tvFemale = findViewById(R.id.tvFemale);
        etBirthday = (EditText) findViewById(R.id.etBirthday);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        llAlreadyHaveAccount = findViewById(R.id.llAlreadyHaveAccount);

        btnNext.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvMale.setOnClickListener(this);
        tvFemale.setOnClickListener(this);
        vaRegisterSteps = (ViewAnimator)findViewById(R.id.vaRegisterSteps);
        vHorizontalBlackLine.getLayoutParams().width = 0;
        tvStep2.setVisibility(View.GONE);
        tvStep3.setVisibility(View.GONE);
        etBirthday.setOnClickListener(this);

        uiElements.add(ivLogo);
        uiElements.add(stepsView);
        uiElements.add(vaRegisterSteps);
        uiElements.add(btnNext);
        uiElements.add(btnLogin);

//        String phoneNumber = TapApp.getPhoneNumber();
//        if(phoneNumber != null){
//            String countryCode = phoneNumber.substring(0,3);
//
//        }

        hideUiElements();
    }

    private
    void hideUiElements(){
        if(uiElements != null && uiElements.size() > 0){
            for(View v : uiElements){
                v.setAlpha(0);
            }
        }
    }

    private void animateUiElementsArray(){
        if(uiElements != null && uiElements.size() > 0){
            for(int i=0;i<uiElements.size();i++){
                animatePageUiElements(uiElements.get(i),((i+1)*130));
            }
        }
    }

    private void animatePageUiElements(View v,int delay){
        com.github.florent37.viewanimator.ViewAnimator.animate(v).startDelay(delay).dp().translationY(30, 0).alpha(0,1).duration(1000)
                .interpolator(new OvershootInterpolator())
                .start();
    }

    private void nextRegisterStep(){
        boolean cancel = false;
        EditText focusView = null;

        if(registerStep == 1){
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();

            if(email.isEmpty()){
                cancel = true;
                focusView = etEmail;
            }
            if(password.isEmpty()){
                cancel = true;
                focusView = etPassword;
            }

            if(cancel){
                llAlreadyHaveAccount.setVisibility(View.VISIBLE);
                btnNext.setText(getString(R.string.activity_register_next));
                focusView.requestFocus();
                focusView.setError(getString(R.string.activity_register_field_required));
                return;
            }
            btnNext.setText(getString(R.string.activity_register_finish));
            llAlreadyHaveAccount.setVisibility(View.GONE);
            tvStep2.setVisibility(View.VISIBLE);
            com.github.florent37.viewanimator.ViewAnimator.animate(vHorizontalBlackLine).dp().width(125).duration(500)
                    .interpolator(new OvershootInterpolator())
                    .start();

            vaRegisterSteps.showNext();
            registerStep++;
            return;
        }
        if(registerStep == 2){
            fullName = etFullName.getText().toString();
            phone = etPhone.getText().toString();

            if(fullName.isEmpty()){
                cancel = true;
                focusView = etFullName;
            }
            if(phone.isEmpty()){
                cancel = true;
                focusView = etPhone;
            }

            if(cancel){
                btnNext.setText(getString(R.string.activity_register_finish));
                focusView.requestFocus();
                focusView.setError(getString(R.string.activity_register_field_required));
                return;
            }
            tvStep3.setVisibility(View.VISIBLE);
            com.github.florent37.viewanimator.ViewAnimator.animate(vHorizontalBlackLine).dp().width(250).duration(500)
                    .interpolator(new OvershootInterpolator())
                    .start();
            btnNext.setText(getString(R.string.activity_register_get_started));
            vaRegisterSteps.showNext();
            registerStep++;
            return;
        }
        if(registerStep == 3){
            registerStep = 0;
            finish();
            Intent i = new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(i);
        }
    }

    private void changeSelectedGender(Gender gen){
        if(gender != gen){
            if(gender == Gender.Male){
                com.github.florent37.viewanimator.ViewAnimator.animate(vGenderSelector).dp().translationX(126).duration(400)
                        .interpolator(new OvershootInterpolator())
                        .start();
                gender = Gender.Female;
            }else{
                com.github.florent37.viewanimator.ViewAnimator.animate(vGenderSelector).dp().translationX(0).duration(400)
                        .interpolator(new OvershootInterpolator())
                        .start();
                gender = Gender.Male;
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        birthdate = dayOfMonth+"/"+monthOfYear+"/"+year;
        etBirthday.setText(birthdate);
    }

    private void openDatePickerDialog(){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                RegisterActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.btnNext:
                nextRegisterStep();
                break;
            case R.id.btnLogin:
                this.finish();
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
        }
    }
}
