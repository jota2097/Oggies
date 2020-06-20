package com.moonshine.oggies.models;

public class User {
    private String id;
    private String email;
    private String name;
    private String user;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String id, String name, String email, String user) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.user = user;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

}
