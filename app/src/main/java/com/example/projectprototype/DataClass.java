package com.example.projectprototype;

public class DataClass {
    private String imageURL;
    private String caption;
    private String date;
    private String audioUrl;

    // Default constructor required for calls to DataSnapshot.getValue(DataClass.class)
    public DataClass() {
    }

    public DataClass(String imageURL, String caption, String date, String audioUrl) {
        this.imageURL = imageURL;
        this.caption = caption;
        this.date = date;
        this.audioUrl = audioUrl;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
