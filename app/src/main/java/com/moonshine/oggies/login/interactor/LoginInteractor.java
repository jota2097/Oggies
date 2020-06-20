package com.moonshine.oggies.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

public interface LoginInteractor {
    void logIn(String username, String password, Activity activity, FirebaseAuth firebaseAuth);
}
