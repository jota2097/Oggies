package com.moonshine.oggies.login.interactor;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;
import com.moonshine.oggies.login.presenter.LoginPresenter;
import com.moonshine.oggies.login.repository.LoginRepository;
import com.moonshine.oggies.login.repository.LoginRepositoryImpl;

public class LoginInteractorImpl implements LoginInteractor {

    private LoginPresenter presenter;
    private LoginRepository repository;

    public LoginInteractorImpl(LoginPresenter presenter) {
        this.presenter = presenter;
        repository = new LoginRepositoryImpl(presenter);
    }

    @Override
    public void logIn(String username, String password, Activity activity, FirebaseAuth firebaseAuth) {
        repository.logIn(username, password, activity, firebaseAuth);
    }
}
