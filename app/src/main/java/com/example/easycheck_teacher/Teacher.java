package com.example.easycheck_teacher;

public class Teacher {
    private String name;
    private String email;
    private String mobile;
    private String course;
    public Teacher(){

    }
    public Teacher(String name,String email, String mobile, String course) {
        this.name = name;
        this.email=email;
        this.mobile = mobile;
        this.course = course;
    }

    // Getters and setters for each field (optional for now)
    public String getName() {
        return name;
    }
    public String getEmail(){
        return email;
    }
    public String getMobile() {
        return mobile;
    }


    public String getCourse() {
        return course;
    }


    // Additional methods for validation, data manipulation, etc. (optional)
}
