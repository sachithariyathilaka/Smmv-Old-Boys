package com.example.sachithariahilaka.smmv;

public class messages
{
    private String Time,Date,Message,From;

    public messages()
    {
    }

    public messages(String time, String date, String message, String from) {
        this.Time = time;
        this.Date=date;
        this.Message=message;
        this.From=from;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getFrom() {
        return From;
    }


    public void setFrom(String from) {
        From = from;
    }
}
