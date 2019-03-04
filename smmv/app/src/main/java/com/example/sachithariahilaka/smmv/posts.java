package com.example.sachithariahilaka.smmv;

public class posts {

    private String UID,Date,Description,PostImage,ProfilePic,Time,Username;

    public posts() {
    }

    public posts(String UID, String Date, String Description, String PostImage, String ProfilePic, String Time, String Username) {
        this.UID = UID;
        this.Date=Date;
        this.Description=Description;
        this.PostImage=PostImage;
        this.ProfilePic=ProfilePic;
        this.Time=Time;
        this.Username=Username;
    }

    public String getUID() {
        return UID;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        Date = Date;
    }

    public void setUID(String UID) {
        this.UID = UID;

    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        Description = Description;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String PostImage) {
        PostImage = PostImage;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String ProfilePic) {
        ProfilePic = ProfilePic;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        Time = Time;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        Username = Username;
    }
}
