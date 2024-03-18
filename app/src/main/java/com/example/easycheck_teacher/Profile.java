package com.example.easycheck_teacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private String userId, course;
    private String registrationNo, name, email, mobile, branch, degree, relegion, caste, gender, bloodgrp;
    private DatabaseReference teacherRef,courseRef;
    private TextView fname_tv,registration_tv, name_tv, email_tv, mobile_tv, branch_tv, degree_tv,branch_tv1;
    private TextView relegion_tv, caste_tv, blood_tv, gender_tv;

    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        userId = getIntent().getStringExtra("userId");
        course = getIntent().getStringExtra("course");

        fname_tv=findViewById(R.id.fname_tv);
        registration_tv = findViewById(R.id.registration_tv);
        name_tv = findViewById(R.id.name_tv);
        email_tv = findViewById(R.id.email_tv);
        mobile_tv = findViewById(R.id.mobile_tv);
        branch_tv = findViewById(R.id.branch_tv);
        degree_tv = findViewById(R.id.degree_tv);
        relegion_tv = findViewById(R.id.relegion_tv);
        caste_tv = findViewById(R.id.caste_tv);
        gender_tv = findViewById(R.id.gender_tv);
        blood_tv = findViewById(R.id.blood_tv);
        branch_tv1=findViewById(R.id.branch_tv1);

        profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        teacherRef = databaseReference.child("teachers");
        teacherRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    registrationNo = (String) snapshot.child("regNo").getValue();
                    name = snapshot.child("name").getValue(String.class);
                    email = snapshot.child("email").getValue().toString();
                    mobile = snapshot.child("mobile").getValue().toString();
                    branch = (String) snapshot.child("course").getValue();
                    relegion = (String) snapshot.child("relegion").getValue();
                    caste = (String) snapshot.child("caste").getValue();
                    gender = (String) snapshot.child("gender").getValue();
                    bloodgrp = (String) snapshot.child("bloodgroup").getValue();


                    setProfileData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        courseRef=databaseReference.child("courses");
        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot courseSnapshot : snapshot.getChildren()) {
                    String courseName = courseSnapshot.child("name").getValue(String.class);
                    if (courseName != null && courseName.equals(branch)) {
                        degree = courseSnapshot.child("fname").getValue(String.class);
                        break; // Once found, exit the loop
                    }
                }
                setProfileData();
                loadProfileImage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle each item click event
                Integer id = item.getItemId();
                if (id == R.id.navigation_profile) {
                    // Open DashboardActivity
                    item.setChecked(true);
                    return true;
                } else {
                    // Open ProfileActivity
                    item.setCheckable(false);
                    startActivity(new Intent(Profile.this, Dashboard.class));
                    return true;
                }
            }
        });
    }

    private void setProfileData() {
        Calendar calendar = Calendar.getInstance();
        Integer year=calendar.get(Calendar.YEAR);
        if(registrationNo==null || registrationNo.isEmpty()){
            registration_tv.setText(mobile.substring(1,4));
        }else{
            registration_tv.setText(registrationNo);
        }

        if (name == null || name.isEmpty()) {
            name_tv.setText("-");
        } else {
            name_tv.setText(name);
            fname_tv.setText(name.toUpperCase());
        }
        if (email == null || email.isEmpty()) {
            email_tv.setText("-");
        } else {
            email_tv.setText(email);
        }

        if (mobile == null || mobile.isEmpty()) {
            mobile_tv.setText("-");
        } else {
            mobile_tv.setText(mobile);
        }
        if (branch == null || branch.isEmpty()) {
            branch_tv.setText("-");
        } else {
            branch_tv.setText(branch);
            branch_tv1.setText(branch+" - "+year);
        }

        if (relegion == null || relegion.isEmpty()) {
            relegion_tv.setText("-");
        } else {
            relegion_tv.setText(relegion);
        }
        if (degree == null || degree.isEmpty()) {
            degree_tv.setText("-");
        } else {
            degree_tv.setText(degree);
        }

        if (caste == null || caste.isEmpty()) {
            caste_tv.setText("-");
        } else {
            caste_tv.setText(caste);
        }
        if (gender == null || gender.isEmpty()) {
            gender_tv.setText("-");
        } else {
            gender_tv.setText(gender);
        }

        if (bloodgrp == null || bloodgrp.isEmpty()) {
            blood_tv.setText("-");
        } else {
            blood_tv.setText(bloodgrp);
        }
    }

    private static final int PICK_IMAGE_REQUEST = 1;

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Set the selected image to the profile picture
            showSnackbar("Profile picture changed successfully");
            profileImage.setImageURI(imageUri);
            saveImageToFirebaseStorage(imageUri);
        }
    }

    private void saveImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images");
        final StorageReference imageRef = storageRef.child("profile_" + userId + ".jpg");
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Image uploaded successfully
                        // Get the download URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Save the download URL in the database
                                saveImageUrlToDatabase(uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failed upload
                        showSnackbar("Oops! We can't Change Profile Pic At this Moment");
                    }
                });
    }

    private void saveImageUrlToDatabase(String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("teachers").child(userId);
        userRef.child("profileImageUrl").setValue(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Image URL saved successfully
                        // You can show a success message or perform any other action
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failed operation
                    }
                });
    }

    private void loadProfileImage() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("teachers").child(userId);
        userRef.child("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.getValue(String.class);
                    // Load the image using Glide
                    Glide.with(Profile.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.profile_user_svgrepo_com__3_)
                            .error(R.drawable.profile_user_svgrepo_com__3_)
                            .into(profileImage);
                } else {
                    // Handle the case when the image URL doesn't exist in the database
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    public void editProfile(View view){
        Intent intent = new Intent(Profile.this,EditProfile.class);
        intent.putExtra("userId",userId);
        intent.putExtra("oldName",name);
        startActivity(intent);
    }
}