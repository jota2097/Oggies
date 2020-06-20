package com.moonshine.oggies.login.presenter;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

public interface LoginPresenter {
    void logIn(String username, String password, Activity activity, FirebaseAuth firebaseAuth);
    void loginSuccess();
    void loginError(String error);
}
