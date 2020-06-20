package com.moonshine.oggies.login.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moonshine.oggies.MenuActivity;
import com.moonshine.oggies.R;
import com.moonshine.oggies.models.User;

public class SigninActivity extends AppCompatActivity {

    private static final String TAG = "SigninActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference referenceUsers;

    private Button btnSignin;
    private TextInputEditText edtEmail, edtName, edtUser, edtPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_signin);
        showToolbar(getResources().getString(R.string.toolbar_tittle_signin), true);

        btnSignin = (Button) findViewById(R.id.sigin_button);
        edtEmail = (TextInputEditText) findViewById(R.id.email);
        edtName = (TextInputEditText) findViewById(R.id.name);
        edtUser = (TextInputEditText) findViewById(R.id.user);
        edtPassword = (TextInputEditText) findViewById(R.id.password_signin);
        edtConfirmPassword = (TextInputEditText) findViewById(R.id.confirm_password_signin);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    Log.w(TAG, "Usuario logueado" + firebaseUser.getEmail());
                } else {
                    Log.w(TAG, "Usuario no logueado");
                }
            }
        };

        firebaseDatabase = FirebaseDatabase.getInstance();
        referenceUsers = firebaseDatabase.getReference("users");

        btnSignin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sigin();
            }
        });
    }

    private void sigin() {
        String email = edtEmail.getText().toString();
        final String name = edtName.getText().toString();
        String user = edtUser.getText().toString();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();
        String regexPatern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.equals("") || name.equals("") || user.equals("") || password.equals("") || confirmPassword.equals("")){
            if ((email.equals("") && password.equals("") && confirmPassword.equals("")) || name.equals("") || user.equals("")) {
                validationInputRequiredError();
            } else if (!email.matches(regexPatern)) {
                validationEmailError();
            } else if (password.length() < 8) {
                validationPassLengError();
            } else if (!password.equals(confirmPassword)) {
                validationPassError();
            }
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                addDisplayName(name);
                                addUserToDataBase();
                                signinSuccess();
                                Toast.makeText(SigninActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SigninActivity.this, "Ocurrió un error al crear la cuenta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void validationEmailError() {
        Toast.makeText(this, getString(R.string.email_validation_sigin), Toast.LENGTH_SHORT).show();
    }

    public void validationPassError() {
        Toast.makeText(this, getString(R.string.password_validation_sigin), Toast.LENGTH_SHORT).show();
    }

    public void validationPassLengError() {
        Toast.makeText(this, getString(R.string.password_leng_validation_sigin), Toast.LENGTH_SHORT).show();
    }

    public void validationInputRequiredError() {
        Toast.makeText(this, getString(R.string.validation_sigin), Toast.LENGTH_SHORT).show();
    }

    public void addDisplayName(String name) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();
        user.updateProfile(profileUpdates);
    }

    public void addUserToDataBase() {
        String id;
        String email = edtEmail.getText().toString();
        String name = edtName.getText().toString();
        String userName = edtUser.getText().toString();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        id = firebaseUser.getUid();

        User user = new User(id, email, name, userName);

        referenceUsers.child(id).setValue(user);
    }

    public void signinSuccess() {
        goHome();
    }

    public void goHome() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void showToolbar(String title, boolean upButton) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
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
