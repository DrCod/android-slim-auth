package com.example.android_retrofit_signup_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {
    public static final String BASE_URL = "https://unshedding-desert.000webhostapp.com/";
    View focusView =null;
    EditText email,password;
    Button loginBtn;
    String emailAddress,userPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        email = findViewById(R.id.email);
        password=findViewById(R.id.password);
        loginBtn=findViewById(R.id.login);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 emailAddress = String.valueOf(email);
                 userPassword =String.valueOf(password);

                loginUser(emailAddress,userPassword);
            }
        });

    }

    private void loginUser(final String email, String pass) {

        boolean mCancel =this.loginValidation();
        if (mCancel){
            loginUsingRetrofit(email,pass);
        }
        else{
            focusView.requestFocus();

        }

    }

    private void loginUsingRetrofit(final String email, String pass) {
        ApInterface apInterface =this.getInterfaceService();

        Call<LoginModel> mService = apInterface.authenticate(email, pass);
        mService.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                 LoginModel mLoginObject = response.body();

                if (mLoginObject.getStatus().equals("1")) {
                    // redirect to Main Activity page
                    Intent loginIntent = new Intent(Login.this, MainActivity.class);
                    loginIntent.putExtra("EMAIL", email);
                    startActivity(loginIntent);
                }
                if (mLoginObject.getStatus().equals("0")) {
                   call.cancel();

                    Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                call.cancel();
                Toast.makeText(Login.this, "Check your network connection or internet permission", Toast.LENGTH_LONG).show();

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

    private boolean loginValidation() {
        // Reset errors.

        email.setError(null);
        password.setError(null);

        boolean cancel = false;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(userPassword) && !isPasswordValid(userPassword)) {
            password.setError(getString(R.string.error_invalid_password));
            focusView = password;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(emailAddress)) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid(emailAddress)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }
        return cancel;
        
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    private boolean isPasswordValid(String userPassword) {
        return userPassword.length()>4;
    }
}
