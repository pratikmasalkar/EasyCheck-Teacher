package com.example.easycheck_teacher;

public class Teacher {
    private String name;
    private String email;
    private String mobile;
    private String course;


    private String relegion;
    private String caste;
    private String bloodgroup;
    private String gender;
    private String abcid;

    public Teacher() {

    }

    public Teacher(String name, String email, String mobile, String course) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.course = course;
    }
    public Teacher(String name, String mobile, String relegion,String caste,String bloodgroup,String gender,String abcId) {
        this.name = name;
        this.mobile = mobile;
        this.relegion = relegion;
        this.caste=caste;
        this.bloodgroup=bloodgroup;
        this.gender=gender;
        this.abcid=abcId;
    }

    public String getAbcid() {
        return abcid;
    }

    public void setAbcid(String abcid) {
        this.abcid = abcid;
    }

    // Getters and setters for each field (optional for now)
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }


    public String getCourse() {
        return course;
    }

    public String getCaste() {
        return caste;
    }

    public void setCaste(String caste) {
        this.caste = caste;
    }

    public String getRelegion() {
        return relegion;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public void setRelegion(String relegion) {
        this.relegion = relegion;
    }

    // Additional methods for validation, data manipulation, etc. (optional)
}
