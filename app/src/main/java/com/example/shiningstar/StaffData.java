package com.example.shiningstar;

public class StaffData {


    String name;
    String email;
    String password;
    String class_ids;

    public StaffData(){

    }

    public StaffData(String name, String email, String password, String class_ids) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.class_ids = class_ids;
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

    public String getClass_ids() {
        return class_ids;
    }
}