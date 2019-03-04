package com.example.sachithariahilaka.smmv;

public class users {
    private String Username,Status,Profile,State;

    public users()
    {

    }

    public users(String Username,String Status,String Profile,String State) {
        this.Username=Username;
        this.Status=Status;
        this.Profile=Profile;
        this.State=State;


    }


    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String Profile) {
        this.Profile = Profile;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }
}
