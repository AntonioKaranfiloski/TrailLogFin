package com.progy.elan.trailrunlog;

public class UserInformation {
    private String title;
    private String description;
    private String kilometers;
    private String denivelation;

    public UserInformation(){

    }
    public UserInformation(String title, String description, String kilometers, String denivelation) {
        this.title = title;
        this.description = description;
        this.kilometers = kilometers;
        this.denivelation = denivelation;

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

    public String getKilometers() {
        return kilometers;
    }

    public void setKilometers(String kilometers) {
        this.kilometers = kilometers;
    }

    public String getDenivelation() {
        return denivelation;
    }

    public void setDenivelation(String denivelation) {
        this.denivelation = denivelation;
    }}
