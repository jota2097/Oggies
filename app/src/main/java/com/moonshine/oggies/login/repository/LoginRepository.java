package com.moonshine.oggies.login.repository;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;

public interface LoginRepository {
    void logIn(String username, String password, Activity activity, FirebaseAuth firebaseAuth);
}
