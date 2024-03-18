package com.example.easycheck_teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    private String userId, course;
    private String name, mobile, relegion, caste, gender, bloodgrp, abcid,oldName;
    private EditText edit_text_name, edit_text_mobile, edit_text_relegion, edit_text_caste, edit_text_blood, edit_text_abc;
    private DatabaseReference teacherRef;
    private Button button_save;
    private RadioGroup radioGroupGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userId = getIntent().getStringExtra("userId");
        oldName=getIntent().getStringExtra("oldName");

        edit_text_name = findViewById(R.id.edit_text_name);
        edit_text_mobile = findViewById(R.id.edit_text_mobile);
        edit_text_relegion = findViewById(R.id.edit_text_relegion);
        edit_text_caste = findViewById(R.id.edit_text_caste);
        edit_text_blood = findViewById(R.id.edit_text_blood);
        edit_text_abc = findViewById(R.id.edit_text_abc);
        radioGroupGender = findViewById(R.id.radio_group_gender);


        button_save = findViewById(R.id.button_save);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        teacherRef = databaseReference.child("teachers");
        teacherRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    name = snapshot.child("name").getValue(String.class);
                    mobile = snapshot.child("mobile").getValue().toString();
                    relegion = (String) snapshot.child("relegion").getValue();
                    caste = (String) snapshot.child("caste").getValue();
                    gender = (String) snapshot.child("gender").getValue();
                    bloodgrp = (String) snapshot.child("bloodgroup").getValue();
                    abcid = (String) snapshot.child("abcid").getValue();
                    setData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_name = edit_text_name.getText().toString();
                if(oldName.equals(new_name)==false) {
                updateBatchesAndTeachers(userId, new_name);
                }
                saveTeacher();
            }
        });
    }

    private void setData() {
        if (name == null || name.isEmpty()) {
            edit_text_name.setText("");
        } else {
            edit_text_name.setText(name);
        }

        if (mobile == null || mobile.isEmpty()) {
            edit_text_mobile.setText("");
        } else {
            edit_text_mobile.setText(mobile);
        }

        if (relegion == null || relegion.isEmpty()) {
            edit_text_relegion.setText("");
        } else {
            edit_text_relegion.setText(relegion);
        }

        if (caste == null || caste.isEmpty()) {
            edit_text_caste.setText("");
        } else {
            edit_text_caste.setText(caste);
        }

        if (bloodgrp == null || bloodgrp.isEmpty()) {
            edit_text_blood.setText("");
        } else {
            edit_text_blood.setText(bloodgrp);
        }

        if (abcid == null || abcid.isEmpty()) {
            edit_text_abc.setText("");
        } else {
            edit_text_abc.setText(abcid);
        }
    }

    private String new_name, new_mobile, new_relegion, new_caste, new_bloodgrp, new_gender, new_abcid;

    private void saveTeacher() {

        this.new_name = edit_text_name.getText().toString();
        this.new_mobile = edit_text_mobile.getText().toString();
        this.new_relegion = edit_text_relegion.getText().toString();
        this.new_caste = edit_text_caste.getText().toString();
        this.new_bloodgrp = edit_text_blood.getText().toString();
        this.new_abcid = edit_text_abc.getText().toString();

        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId);
            new_gender = radioButton.getText().toString();
        }


        // Input validation
        if (new_name.isEmpty() || new_mobile.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (new_name.length() < 3) {
            edit_text_name.setError("Name should have at least 3 characters");
            return;
        } else if (!new_name.matches("[a-zA-Z ]+")) {
            edit_text_name.setError("Name should only contain letters and spaces");
            return;
        }
        if (new_mobile.length() != 10 || new_mobile.length() < 10 || new_mobile.length() > 10) {
            edit_text_mobile.setError("Mobile No Have 10 Digits Only");
            edit_text_mobile.requestFocus();
            return;
        }
        saveTeacherData(new_name, new_mobile, new_relegion, new_caste, new_bloodgrp, new_gender, new_abcid);
    }


    private void saveTeacherData(String name, String mobile, String relegion, String caste, String blood, String gender, String abcid) {
        try {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("teachers").child(userId);

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("mobile", mobile);
            updates.put("relegion", relegion);
            updates.put("caste", caste);
            updates.put("bloodgroup", blood);
            updates.put("gender", gender);
            updates.put("abcid", abcid);

            userRef.updateChildren(updates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                edit_text_name.setText("");
                                edit_text_mobile.setText("");
                                edit_text_relegion.setText("");
                                edit_text_caste.setText("");
                                edit_text_abc.setText("");
                                edit_text_blood.setText("");
                                Intent intent=new Intent(EditProfile.this, Profile.class);
                                overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                                Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_LONG);
        }
    }
    private void updateBatchesAndTeachers(String userId, final String newTeacherName) {
        DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference().child("teachers").child(userId);

        teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get the batch and subject names from the teacher node
                    Map<String, String> batchSubjectMap = (Map<String, String>) snapshot.child("batches").getValue();

                    if (batchSubjectMap != null) {
                        DatabaseReference batchesRef = FirebaseDatabase.getInstance().getReference().child("batches");

                        batchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot batchesSnapshot) {
                                if (batchesSnapshot.exists()) {
                                    for (DataSnapshot batchSnapshot : batchesSnapshot.getChildren()) {
                                        String batchName = batchSnapshot.child("name").getValue(String.class);

                                        if (batchSubjectMap.containsKey(batchName)) {
                                            String subjectName = batchSubjectMap.get(batchName);

                                            // Update teacher name in the batch's 'teachers' node
                                            batchSnapshot.child("teachers")
                                                    .child(subjectName).getRef().setValue(newTeacherName);
                                        }
                                    }

                                    Toast.makeText(EditProfile.this, "Name Updated Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public void backToProfile(View view) {
        Intent intent=new Intent(this, Profile.class);
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}