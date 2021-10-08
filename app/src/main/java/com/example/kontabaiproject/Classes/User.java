package com.example.kontabaiproject.Classes;

public class User
{
    private  String pickuplocation;
    private  String status;

    public User(String pickuplocation, String status) {
        this.pickuplocation = pickuplocation;
        this.status = status;
    }

    public User() {
    }

    public String getPickuplocation() {
        return pickuplocation;
    }

    public void setPickuplocation(String pickuplocation) {
        this.pickuplocation = pickuplocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
