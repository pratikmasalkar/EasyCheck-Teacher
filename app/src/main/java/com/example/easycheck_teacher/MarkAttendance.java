package com.example.easycheck_teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MarkAttendance extends AppCompatActivity {
    private Spinner teacherBatchSpinner;
    private String userId, course, selectedBatch, selectedBatchSubjectName, currentDate;
    private TextView selectedBatchTV, selectedBatchubjectTV, CurrentDateTv, addedStudentCountTv;
    private List<String> batchNames;
    private List<Attendance> attendanceList;
    private Map<String, String> batches;
    private RecyclerView recycler_view;
    private StudentsAtdnAdapter studentsAtdnAdapter;
    private int addedStudentCount;
    private DatabaseReference teacherRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        userId = getIntent().getStringExtra("userId");
        course = getIntent().getStringExtra("course");


        teacherBatchSpinner = findViewById(R.id.teacherBatchSpinner);
        selectedBatchTV = findViewById(R.id.selectedBatchTV);
        selectedBatchubjectTV = findViewById(R.id.selectedBatchubjectTV);
        CurrentDateTv = findViewById(R.id.CurrentDateTv);
        addedStudentCountTv = findViewById(R.id.addedStudentCountTv);
        recycler_view = findViewById(R.id.recycler_view);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        teacherRef = databaseReference.child("teachers");
        teacherRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    batches = (Map<String, String>) snapshot.child("batches").getValue();
                    batchNames = new ArrayList<>();
                    if (batches != null) {
                        batchNames.addAll(batches.keySet());
                        selectedBatchSubjectName = batches.get("selectedBatchName");
                    }
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getBaseContext(),
                            android.R.layout.simple_list_item_1, batchNames);
                    teacherBatchSpinner.setAdapter(subjectAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        teacherBatchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // ... (existing code to fetch batch details and date)
                selectedBatch = teacherBatchSpinner.getSelectedItem().toString();
                selectedBatchSubjectName = batches.get(selectedBatch);
                String currentDateAndTime = new SimpleDateFormat("dd-MM-yyyy , HH:mm aa", Locale.getDefault()).format(new Date());
                currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                fetchAttendance(course, selectedBatch, selectedBatchSubjectName, currentDateAndTime, currentDate);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            // ... (onNothingSelected)
        });


    }

    private void setRecyclerView(List<Attendance> attendanceList) {
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        studentsAtdnAdapter = new StudentsAtdnAdapter(this, attendanceList, this); // Assuming StudentsAdapter is implemented
        recycler_view.setAdapter(studentsAtdnAdapter);
    }

    private void fetchAttendance(String course, String selectedBatchName, String selectedBatchSubjectName, String currentDateAndTime, String currentDate) {
        selectedBatchTV.setText(selectedBatch);
        selectedBatchubjectTV.setText(selectedBatchSubjectName);
        CurrentDateTv.setText(currentDateAndTime);
        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance");
        attendanceRef
                .child(course)
                .child(selectedBatchName)
                .child(selectedBatchSubjectName)
                .child(currentDate)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        attendanceList = new ArrayList<>();
                        addedStudentCount = 0;
                        for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                            // Access student attendance data within the nested object
                            Map<String, Object> studentAttendanceData = (Map<String, Object>) studentSnapshot.getValue();

                            // Extract properties and create Attendance object
                            String studentName = studentSnapshot.getKey();
                            String roll = (String) studentAttendanceData.get("roll");
                            String code = (String) studentAttendanceData.get("code");
                            String status = (String) studentAttendanceData.get("status");
                            if (status.equals("Present")) {
                                addedStudentCount++;
                            }
                            Attendance attendance = new Attendance(roll, studentName, code, status, course, selectedBatchName, selectedBatchSubjectName, currentDate);
                            attendanceList.add(attendance);

                        }
                        addedStudentCountTv.setText(addedStudentCount + " ");
                        setRecyclerView(attendanceList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors here
                        Toast.makeText(MarkAttendance.this, "Failed to load attendance: "
                                + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void InvalidMarkStudentAttendance
            (String course, String selectedBatchName, String selectedBatchSubjectName,
             String currentDate, String studentName) {
        String attendancePath;
        if (studentName != null) {
            attendancePath = "attendance/" + course + "/" + selectedBatchName +
                    "/" + selectedBatchSubjectName + "/" + currentDate + "/" + studentName;
        } else {
            attendancePath = "attendance/" + course + "/" + selectedBatchName +
                    "/" + selectedBatchSubjectName + "/" + currentDate + "/" + studentName;
        }

        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference(attendancePath);
        attendanceRef.child("status").setValue("Invalid").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }


    public void ValidMarkStudentAttendance
            (String course, String selectedBatchName, String selectedBatchSubjectName,
             String currentDate, String studentName) {
        String attendancePath = "attendance/" + course + "/" + selectedBatchName +
                "/" + selectedBatchSubjectName + "/" + currentDate + "/" + studentName;

        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference(attendancePath);
        attendanceRef.child("status").setValue("Present").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    public void onCompleteAttendance(View view) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference("attendance");

        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                if (studentSnapshot.exists()) {
                    Map<String, Object> allStudents = (Map<String, Object>) studentSnapshot.getValue();

                    attendanceRef.child(course).child(selectedBatch).child(selectedBatchSubjectName).child(currentDate)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot existingAttendanceSnapshot) {
                                    Map<String, Object> existingAttendance = new HashMap<>();
                                    if (existingAttendanceSnapshot.exists()) {
                                        existingAttendance = (Map<String, Object>) existingAttendanceSnapshot.getValue();
                                    }

                                    for (String studentUid : allStudents.keySet()) {
                                        Map<String, Object> studentData = (Map<String, Object>) allStudents.get(studentUid);
                                        String studentCourse = (String) studentData.get("course");
                                        String studentBatch = (String) studentData.get("batch");
                                        String studentName = (String) studentData.get("name");

                                        if (studentCourse.equals(course) && studentBatch.equals(selectedBatch)) {
                                            String roll = (String) studentData.get("rollno");

                                            // Create attendance object if not already present
                                            Map<String, Object> attendanceData = existingAttendance.get(studentName) != null
                                                    ? (Map<String, Object>) existingAttendance.get(studentName)
                                                    : new HashMap<>();
                                            attendanceData.put("flag", "Completed");
                                            if (existingAttendance.containsKey(studentName) == false) {
                                                attendanceData.put("name", studentName); // Store student name within attendance
                                                attendanceData.put("roll", roll);
                                                attendanceData.put("status", "Absent");
                                                attendanceData.put("code", "-");
                                                attendanceData.put("attendanceTime", "-");

                                                existingAttendance.put(studentName, attendanceData); // Use student name as key
                                            }
                                        }
                                    }

                                    attendanceRef.child(course).child(selectedBatch).child(selectedBatchSubjectName).child(currentDate)
                                            .setValue(existingAttendance).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(MarkAttendance.this, "Attendance marked", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(MarkAttendance.this, "Failed to mark attendance: "
                                                                + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void dashboard(View view) {
        startActivity(new Intent(this, Dashboard.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}