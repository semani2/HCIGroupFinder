package model;

import java.util.ArrayList;

public class Group {
    private String groupName, groupPic, course, place, date;
    
    
 
    public Group() {
    }
 
    public Group(String name, String groupPic, String course, String place,
            String date) {
        this.groupName = name;
        this.groupPic = groupPic;
        this.course = course;
        this.place = place;
        this.date = date;
    }
 
    public String getTitle() {
        return groupName;
    }
 
    public void setTitle(String name) {
        this.groupName = name;
    }
 
    public String getGroupPic() {
        return groupPic;
    }
 
    public void setGroupPic(String groupPic) {
        this.groupPic = groupPic;
    }
 
    public String getCourse() {
        return course;
    }
 
    public void setCourse(String course) {
        this.course = course;
    }
 
    public String getPlace() {
        return place;
    }
 
    public void setPlace(String place) {
        this.place = place;
    }
 
    public String getDate() {
        return date;
    }
 
    public void setDate(String date) {
        this.date = date;
    }
 
}
