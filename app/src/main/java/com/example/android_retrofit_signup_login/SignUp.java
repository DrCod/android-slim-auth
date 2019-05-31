package com.example.android_retrofit_signup_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SignUp extends AppCompatActivity {
    public static final String BASE_URL = "https://unshedding-desert.000webhostapp.com/";
    View focusView =null;
    EditText etEmail,etPassword;
    String email,password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_layout);

        etEmail =findViewById(R.id.textEmail);
        etPassword=findViewById(R.id.textPassword);

         email = String.valueOf(etEmail);
         password =String.valueOf(etPassword);

        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(email,password);
            }
        });
    }

    private void registerUser(final String email,String password) {
        boolean mCancel =this.registerValidation();
        if (mCancel){
            registerUsingRetrofit(email,password);

        }
        else{
            focusView.requestFocus();
        }

    }

    private void registerUsingRetrofit(final String email, String password) {

        ApInterface apInterface =this.getInterfaceService();

        Call<LoginModel> mService = apInterface.registration(email, password);
        mService.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                LoginModel mLoginObject = response.body();

                //showProgress(false);
                if (mLoginObject.getStatus().equals("1")) {
                    // redirect to Login page
                    Intent registerIntent = new Intent(SignUp.this, Login.class);
                    registerIntent.putExtra("EMAIL", email);
                    startActivity(registerIntent);
                }
                if (mLoginObject.getStatus().equals("0")) {
                    call.cancel();
                    Toast.makeText(SignUp.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                call.cancel();
                Toast.makeText(SignUp.this, "Check your network connection or internet permission", Toast.LENGTH_LONG).show();

            }
        });

    }

    private ApInterface getInterfaceService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final ApInterface mInterfaceService = retrofit.create(ApInterface.class);

            return mInterfaceService;
        }


    private boolean registerValidation() {
        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);

        boolean cancel = false;
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }
        return cancel;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length()>4;
    }
}
