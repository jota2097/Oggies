package com.moonshine.oggies.login.presenter;

import android.app.Activity;

import com.google.firebase.auth.FirebaseAuth;
import com.moonshine.oggies.login.interactor.LoginInteractor;
import com.moonshine.oggies.login.interactor.LoginInteractorImpl;
import com.moonshine.oggies.login.view.LoginView;

public class LoginPresenterImpl implements LoginPresenter {

    private LoginView loginView;
    private LoginInteractor interactor;

    public LoginPresenterImpl(LoginView loginView) {
        this.loginView = loginView;
        interactor = new LoginInteractorImpl(this);
    }

    @Override
    public void logIn(String username, String password, Activity activity, FirebaseAuth firebaseAuth) {
        loginView.disableInputs();

        interactor.logIn(username, password, activity, firebaseAuth);
    }

    @Override
    public void loginSuccess() {
        loginView.goHome();
    }

    @Override
    public void loginError(String error) {
        loginView.enableInputs();
        loginView.loginError(error);
    }
}
