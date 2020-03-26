package com.example.shiningstar;

public class ParentData {


    String name;
    String email;
    String password;

    public ParentData(){

    }

    public ParentData(String name, String email, String password) {

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