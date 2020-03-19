package com.example.shiningstar;

public class AuthenticationData {


    String name;
    String email;
    String password;

    public AuthenticationData(){

    }

    public AuthenticationData(String name, String email, String password) {

        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getname() {
        return name;
    }

    public String getemail() {
        return email;
    }

    public String getpassword() {
        return password;
    }

}
