package com.codingcrew.memoir.models;

public class User {
    String email;
    String username;
    String password;
    String re_password;


    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", re_password='" + re_password + '\'' +
                '}';
    }

    public User(String email, String username, String password, String re_password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.re_password = re_password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRe_password() {
        return re_password;
    }

    public void setRe_password(String re_password) {
        this.re_password = re_password;
    }
}
