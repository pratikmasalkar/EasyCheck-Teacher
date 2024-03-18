package com.example.easycheck_teacher; // Replace with your package name

public class Attendance {
    private String roll;
    private String name;
    private String code;
    private String status;
    private String attendanceTime;
    private String course;
    private String selectedBatchName;
    private String selectedBatchSubjectName;
    private String currentDate;

    // Constructors, getters, and setters

    public Attendance() {
    }

    public Attendance(String roll, String name, String attendanceCode,String status,
                      String course, String selectedBatchName, String selectedBatchSubjectName, String currentDate) {
        this.roll = roll;
        this.name = name;
        this.code = attendanceCode;
        this.status=status;
        this.course = course;
        this.selectedBatchName = selectedBatchName;
        this.selectedBatchSubjectName = selectedBatchSubjectName;
        this.currentDate = currentDate;
    }


    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String attendanceCode) {
        this.code = attendanceCode;
    }

    public String getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(String attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourse() {
        return course;
    }

    public String getSelectedBatchName() {
        return selectedBatchName;
    }

    public void setSelectedBatchName(String selectedBatchName) {
        this.selectedBatchName = selectedBatchName;
    }

    public String getSelectedBatchSubjectName() {
        return selectedBatchSubjectName;
    }

    public void setSelectedBatchSubjectName(String selectedBatchSubjectName) {
        this.selectedBatchSubjectName = selectedBatchSubjectName;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
