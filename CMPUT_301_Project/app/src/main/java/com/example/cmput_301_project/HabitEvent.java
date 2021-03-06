/**
 * Class to represent the habit event
 */
package com.example.cmput_301_project;

import java.io.Serializable;
import java.util.UUID;

/**
 * Habit event object class that has attributes that are meant to track the Habit Events the user wants to create
 */
public class HabitEvent implements Serializable {
    private String id;
    private String date;
    private String image;
    private HabitLocation location;
    private String comment;
    private String title;
    private TodayHabitViewHolder holder;
    private boolean isExpanded;
    private boolean completed;
    private boolean isDeleted;

    public HabitEvent(String date, String title) {
        this.date = date;
        this.id = UUID.randomUUID().toString();
        this.completed = false;
        this.title = title;
    }

    public HabitEvent(HabitEvent otherHabitEvent) {
        this.date = otherHabitEvent.getDate();
        this.id = otherHabitEvent.getId();
        this.completed = otherHabitEvent.isCompleted();
        this.title = otherHabitEvent.getTitle();
        this.image = otherHabitEvent.getImage();
        this.comment = otherHabitEvent.getComment();
        this.holder = otherHabitEvent.getHolder();
    }

    public HabitEvent() { /* required empty constructor */ }

    // Getters and Setters
    public boolean isDeleted() {
        return isDeleted;
    }

    public HabitLocation getLocation() {
        return location;
    }

    public void setLocation(HabitLocation location) {
        this.location = location;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComment() {
        return comment;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TodayHabitViewHolder getHolder() {
        return holder;
    }

    public void setHolder(TodayHabitViewHolder holder) {
        this.holder = holder;
    }
}
