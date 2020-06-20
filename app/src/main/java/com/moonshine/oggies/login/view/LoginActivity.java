package com.moonshine.oggies.login.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.moonshine.oggies.MenuActivity;
import com.moonshine.oggies.R;
import com.moonshine.oggies.login.presenter.LoginPresenter;
import com.moonshine.oggies.login.presenter.LoginPresenterImpl;

public class LoginActivity extends AppCompatActivity implements LoginView{

    private TextInputEditText username, password;
    private Button login;
    private LoginPresenter presenter;

    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    goHome();
                } else {
                    Log.w(TAG, "Usuario no logueado");
                }
            }
        };

        username = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);

        presenter = new LoginPresenterImpl(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (username.getText().toString().equals("") || password.getText().toString().equals("")){
                    if (username.getText().toString().equals("") && password.getText().toString().equals("")) {
                        validationEmailPassError();
                    } else if (username.getText().toString().equals("")) {
                        validationEmailError();
                    } else {
                        validationPassError();
                    }
                } else {
                    login(username.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    private void login(String username, String password) {
        presenter.logIn(username, password, this, firebaseAuth);
    }

    public void goSignin(View view) {
        goSignin();
    }

    public void goHome(View view) {
        goHome();
    }

    @Override
    public void enableInputs() {
        username.setEnabled(true);
        password.setEnabled(true);
        login.setEnabled(true);
    }

    @Override
    public void disableInputs() {
        username.setEnabled(false);
        password.setEnabled(false);
        login.setEnabled(false);
    }

    public void validationEmailError() {
        Toast.makeText(this, getString(R.string.email_validation_login), Toast.LENGTH_SHORT).show();
    }

    public void validationPassError() {
        Toast.makeText(this, getString(R.string.password_validation_login), Toast.LENGTH_SHORT).show();
    }

    public void validationEmailPassError() {
        Toast.makeText(this, getString(R.string.email_password_validation_login), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginError(String error) {
        Toast.makeText(this, getString(R.string.login_error_login) + error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void goSignin() {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
    }

    @Override
    public void goHome() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
