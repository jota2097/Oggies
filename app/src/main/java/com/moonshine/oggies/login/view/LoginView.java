package com.moonshine.oggies.login.view;

public interface LoginView {

    void enableInputs();
    void disableInputs();

    void loginError(String error);

    void goSignin();
    void goHome();

}
