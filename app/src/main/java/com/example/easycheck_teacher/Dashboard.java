package com.example.easycheck_teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    private TextView userName;
    private String course,batch;
    private ListView list_batches,list_subjects;
    private DatabaseReference teacherRef;
    private Map<String, String> batches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        userName = findViewById(R.id.userName);
        list_subjects=findViewById(R.id.list_subjects);
        list_batches=findViewById(R.id.list_batches);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            teacherRef = FirebaseDatabase.getInstance().getReference("teachers");
            teacherRef.child(userId) // Directly query the user's data by UID
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String name = snapshot.child("name").getValue(String.class);
                                course=snapshot.child("course").getValue(String.class);
                                batches =(Map<String, String>) snapshot.child("batches").getValue();
                                        userName.setText(name + " !");

                                List<String> batchesNames = new ArrayList<>();
                                List<String> subjectNames = new ArrayList<>();
                                if (batches != null) {
                                    batchesNames.addAll(batches.keySet());
                                    subjectNames.addAll(batches.values());
                                }
                                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getBaseContext(),
                                        android.R.layout.simple_list_item_1, batchesNames);
                                list_batches.setAdapter(adapter1);
                                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getBaseContext(),
                                        android.R.layout.simple_list_item_1, subjectNames);
                                list_subjects.setAdapter(adapter2);
                            } else {
                                // Handle the case where user data is not found
                                Toast.makeText(Dashboard.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Dashboard.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case where no user is logged in
            // (You might want to redirect to the login screen)
        }
    }
public void markAttendace(View view){
        Intent intent= new Intent(this, MarkAttendance.class);
        startActivity(intent);
}
    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();
        // Optionally, navigate back to the login screen:
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}