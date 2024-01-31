package com.example.easycheck_teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Registration extends AppCompatActivity {

    private EditText etName, etEmail, etMobile, etPassword;
    private Spinner courseSpinner;

    private FirebaseAuth auth;
    private DatabaseReference coursesRef;
    private DatabaseReference teacherRef;
    private ArrayList<String> courseNames;
    private ArrayAdapter<String> courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.mobileNo);
        etPassword = findViewById(R.id.password);

        auth = FirebaseAuth.getInstance();
        teacherRef = FirebaseDatabase.getInstance().getReference("teachers");
        courseSpinner = findViewById(R.id.courseSpinner);
        coursesRef = FirebaseDatabase.getInstance().getReference("courses");
        courseNames = new ArrayList<>();

        loadCourses();

        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courseNames);
        courseSpinner.setAdapter(courseAdapter);

        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTeacher();
            }
        });
    }

    private void loadCourses() {
        coursesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String courseName = childSnapshot.child("name").getValue(String.class); // Assuming "fname" holds course name
                    courseNames.add(courseName);
                }
                courseAdapter.notifyDataSetChanged(); // Update spinner data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Registration.this, "Failed to load courses.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String name, email, mobile, course, password;

    private void registerTeacher() {

        this.name = etName.getText().toString();
        this.email = etEmail.getText().toString();
        this.mobile = etMobile.getText().toString();
        this.course = courseSpinner.getSelectedItem().toString();
        this.password = etPassword.getText().toString();


        // Input validation
        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || course.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.length() < 3) {
            etName.setError("Name should have at least 3 characters");
            return;
        } else if (!name.matches("[a-zA-Z ]+")) {
            etName.setError("Name should only contain letters and spaces");
            return;
        }
        if (mobile.length() != 10 || mobile.length() < 10 || mobile.length() > 10) {
            etMobile.setError("Mobile No Have 10 Digits Only");
            etMobile.requestFocus();
            return;
        }
        checkMobileNumberUniqueness(mobile);
    }

    private void checkMobileNumberUniqueness(String mobile) {
        teacherRef.orderByChild("mobile").equalTo(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    etMobile.setError("Mobile number already registered");
                    etMobile.requestFocus();
                } else {
                    createUserInFirebase(email, password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                displayErrorToUser("Error checking for existing users");
            }
        });
    }

    private void createUserInFirebase(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = auth.getCurrentUser().getUid();
                            saveTeacherData(uid, name, email, mobile, course);
                            Toast.makeText(Registration.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                            // Clear input fields
                            etName.setText("");
                            etEmail.setText("");
                            etMobile.setText("");
                            etPassword.setText("");
                            // Navigate to another activity

                            finish();

                        } else {
                            String errorMsg = task.getException().getMessage().toString();
                            if (errorMsg.contains("password")) {
                                etPassword.setError(errorMsg);
                                etPassword.requestFocus();
                            } else if (errorMsg.contains("email")) {
                                etEmail.setError(errorMsg);
                                etEmail.requestFocus();
                            } else {
                                Toast.makeText(Registration.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void saveTeacherData(String uid, String name, String email, String mobile, String course) {
        try {
            Teacher teacher = new Teacher(name, email, mobile, course);
            teacherRef.child(uid).setValue(teacher);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_LONG);
        }
    }

    private void displayErrorToUser(String errorCheckingForExistingUsers) {
        Toast.makeText(this, errorCheckingForExistingUsers, Toast.LENGTH_LONG);
    }

    public void onLogin(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

}