package com.example.easycheck_teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewAttendance extends AppCompatActivity {
    private Spinner teacherBatchSpinner, selectDateSpinner;
    private TextView selectedBatchTV, selectedBatchSubjectTV, addedStudentCountTv;
    private String userId, course, selectedBatchSubjectName, selectedBatch, currentDate, selectedDate;
    private DatabaseReference teacherRef, attendanceRef;
    private Map<String, String> batches;
    private List<String> batchNames;
    private List<Attendance> attendanceList;
    private RecyclerView recycler_view;
    private ViewAttendanceAdpter studentsAtdnAdapter;
    private ValueEventListener attendanceListener;
    private Button exportButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        // Initialize views and Firebase references
        initializeViews();
        initializeFirebase();

        // Fetch batches and set up batch spinner
        setupBatchSpinner();
    }

    private void initializeViews() {
        teacherBatchSpinner = findViewById(R.id.teacherBatchSpinner);
        selectedBatchTV = findViewById(R.id.selectedBatchTV);
        selectedBatchSubjectTV = findViewById(R.id.selectedBatchSubjectTV);
        addedStudentCountTv = findViewById(R.id.addedStudentCountTv);
        selectDateSpinner = findViewById(R.id.selectDateSpinner);
        recycler_view = findViewById(R.id.recycler_view);
        exportButton=findViewById(R.id.exportButton);
    }

    private void initializeFirebase() {
        userId = getIntent().getStringExtra("userId");
        course = getIntent().getStringExtra("course");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        teacherRef = databaseReference.child("teachers");
        attendanceRef = databaseReference.child("attendance");
    }

    private void setupBatchSpinner() {
        teacherRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    batches = (Map<String, String>) snapshot.child("batches").getValue();
                    if (batches != null) {
                        batchNames = new ArrayList<>(batches.keySet());
                        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getBaseContext(),
                                android.R.layout.simple_list_item_1, batchNames);
                        teacherBatchSpinner.setAdapter(subjectAdapter);
                    }
                } else {
                    Toast.makeText(ViewAttendance.this, "No batches found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewAttendance.this, "Failed to fetch batches: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        teacherBatchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBatch = teacherBatchSpinner.getSelectedItem().toString();
                selectedBatchSubjectName = batches.get(selectedBatch);
                selectedBatchTV.setText(selectedBatch);
                selectedBatchSubjectTV.setText(selectedBatchSubjectName);
                // Fetch attendance dates when batch is selected
                // Clear the selectDateSpinner
                ArrayAdapter<String> emptyDateAdapter = new ArrayAdapter<>(getBaseContext(),
                        android.R.layout.simple_list_item_1, new ArrayList<>());
                selectDateSpinner.setAdapter(emptyDateAdapter);

                // Fetch attendance dates when batch is selected
                fetchAttendanceDates(course, selectedBatch, selectedBatchSubjectName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void fetchAttendanceDates(String course, String batchName, String subjectName) {
        DatabaseReference attendanceDateRef = attendanceRef.child(course).child(batchName).child(subjectName);
        attendanceDateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> dates = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        dates.add(dateSnapshot.getKey());
                    }
                    ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(getBaseContext(),
                            android.R.layout.simple_list_item_1, dates);
                    selectDateSpinner.setAdapter(dateAdapter);
                    Log.d("ViewAttendance", "Attendance dates fetched successfully: " + dates);
                } else {
                    ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(getBaseContext(),
                            android.R.layout.simple_list_item_1, dates);
                    selectDateSpinner.setAdapter(dateAdapter);
                    Log.d("ViewAttendance", "No attendance data found."+dates);
                    Toast.makeText(ViewAttendance.this, "No attendance data found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewAttendance.this, "Failed to fetch attendance dates: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        selectDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = selectDateSpinner.getSelectedItem().toString();
                fetchAttendance(course, selectedBatch, selectedBatchSubjectName, selectedDate);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetchAttendance(String course, String selectedBatchName, String selectedBatchSubjectName, String selectedDate) {
        Log.d("ViewAttendance", "Fetching attendance for: " + selectedDate);
        DatabaseReference attendanceDateRef = attendanceRef.child(course).child(selectedBatchName).child(selectedBatchSubjectName).child(selectedDate);
        attendanceDateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    attendanceList = new ArrayList<>();
                    int addedStudentCount = 0;
                    for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                        Map<String, Object> studentAttendanceData = (Map<String, Object>) studentSnapshot.getValue();
                        String studentName = studentSnapshot.getKey();
                        String roll = (String) studentAttendanceData.get("roll");
                        String code = (String) studentAttendanceData.get("code");
                        String status = (String) studentAttendanceData.get("status");
                        String flag=(String) studentAttendanceData.get("flag");
                        if ("Present".equals(status) && "Completed".equals(flag)) {
                            addedStudentCount++;
                        }
                        Attendance attendance = new Attendance(roll, studentName, code, status, course, selectedBatchName, selectedBatchSubjectName, currentDate);
                        if(studentSnapshot.child("flag").exists() && studentSnapshot.child("flag").getValue(String.class).equals("Completed")) {
                            attendanceList.add(attendance);
                        }
                    }
                    addedStudentCountTv.setText(String.valueOf(addedStudentCount));
                    setRecyclerView(attendanceList);
                } else {
                    attendanceList.clear();
                    setRecyclerView(attendanceList);
                    Toast.makeText(ViewAttendance.this, "No attendance data found for selected date.", Toast.LENGTH_SHORT).show();
                    Log.d("ViewAttendance", "No attendance data found for: " + selectedDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewAttendance.this, "Failed to load attendance: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerView(List<Attendance> attendanceList) {
        if (studentsAtdnAdapter == null) {
            studentsAtdnAdapter = new ViewAttendanceAdpter(this, attendanceList, this);
            recycler_view.setLayoutManager(new LinearLayoutManager(this));
            recycler_view.setAdapter(studentsAtdnAdapter);
            studentsAtdnAdapter.notifyDataSetChanged();
        } else {
            studentsAtdnAdapter.notifyDataSetChanged();
            studentsAtdnAdapter.setAttendanceList(attendanceList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (attendanceListener != null) {
            attendanceRef.removeEventListener(attendanceListener);
        }
    }

    public void dashboard(View view) {
        startActivity(new Intent(this, Dashboard.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public void exportToExcelOnClick(View view) {
        try {
            if (selectedBatch != null && selectedDate != null) {
                // Call the exporter class to generate the Excel file
                String fileName ="/"+ selectedDate + "_" +selectedBatchSubjectName+"_"+ selectedBatch + "_attendance.xlsx";
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                String filePath = file.getAbsolutePath();
                AttendanceExporter.exportToExcel(attendanceList, filePath);

                // Provide option to download the file
                downloadExcelFile(filePath);
            } else {
                Toast.makeText(this, "Please select batch and date", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export attendance to Excel", Toast.LENGTH_SHORT).show();
        }
    }




    private void downloadExcelFile(String filePath) {
        // Create an intent to view the Excel file
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        Uri uri = FileProvider.getUriForFile(this, "com.example.easycheck_teacher.fileprovider", file);
        intent.setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Check if there's an application available to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No application available to view Excel files", Toast.LENGTH_SHORT).show();
        }
    }



}
