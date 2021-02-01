package com.codingcrew.memoir.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Journal implements Serializable {
    private String id;
    private String title;
    private String description;
    private String username;
    private String emotion;
    private String image;
    @SerializedName("published_date")
    private String date;
    private String category;


    public Journal(String id, String title, String description, String username, String emotion, String image, String date, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.username = username;
        this.emotion = emotion;
        this.image = image;
        this.date = date;
        this.category = category;
    }

    public Journal(String id, String title, String description, String username, String emotion, String image, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.username = username;
        this.emotion = emotion;
        this.image = image;
        this.date = date;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Journal{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", username='" + username + '\'' +
                ", emotion='" + emotion + '\'' +
                ", image='" + image + '\'' +
                ", date='" + date + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
