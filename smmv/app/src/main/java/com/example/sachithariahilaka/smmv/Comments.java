package com.example.sachithariahilaka.smmv;

public class Comments {

    String Date,Time,Profile,Comment,Username;

    public Comments() {
    }

    public Comments(String date,String time, String profile, String comment, String username) {
        this.Date = date;
        this.Time= time;
        this.Profile= profile;
        this.Comment= comment;
        this.Username= username;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
