package com.example.hellodb;

public class Users {
    String name;
    String no;
    String date;

    public Users(String name, String no, String date) {
        this.name = name;
        this.no = no;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getNo() {
        return no;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
