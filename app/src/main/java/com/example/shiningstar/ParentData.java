package com.example.shiningstar;

public class ParentData {


    String name;
    String email;
    String password;
    String children;

    public ParentData(){

    }

    public ParentData(String name, String email, String password, String children) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.children = children;
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

    public String getChildren() {
        return children;
    }
}