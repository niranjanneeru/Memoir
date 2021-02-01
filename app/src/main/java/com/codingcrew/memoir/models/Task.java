package com.codingcrew.memoir.models;

import com.google.gson.annotations.SerializedName;

public class Task {
    private String id;
    private String title;
    private String description;
    private String username;
    @SerializedName("published_date")
    private String date;
    @SerializedName("end_date")
    private String endDate;

    public Task(String id, String title, String description, String username, String date, String endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.username = username;
        this.date = date;
        this.endDate = endDate;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", username='" + username + '\'' +
                ", date='" + date + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
