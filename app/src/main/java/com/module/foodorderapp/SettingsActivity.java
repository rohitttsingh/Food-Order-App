package com.module.foodorderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.module.foodorderapp.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private TextView profile_pic_change_btn, closeTextbtn, saveTextbtn;
    private EditText fullnameet,phoneet,addresset;
    private CircleImageView profileImageView;
    private Uri imageuri;
    private String myUrl="", checker="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageView=findViewById(R.id.profile_image);
        fullnameet=findViewById(R.id.setting_name);
        phoneet=findViewById(R.id.setting_phone);
        addresset=findViewById(R.id.setting_address);
        profile_pic_change_btn=findViewById(R.id.profile_image_change_btn);
        closeTextbtn=findViewById(R.id.close_settings_btn);
        saveTextbtn=findViewById(R.id.update_account_seetins);
        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile pictures");
        userInfoDisplay(profileImageView, fullnameet , phoneet , addresset );
        closeTextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        saveTextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checker.equals("clicked")){
                    userInfoSaved();

                }
                else {
                    updateOnlyUserInfo();

                }
            }
        });

        profile_pic_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker="clicked";

                CropImage.activity(imageuri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);


            }
        });

    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("Users");
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("name",fullnameet.getText().toString());
        hashMap.put("address",addresset.getText().toString());
        hashMap.put("phone",phoneet.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(hashMap);

        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
        && resultCode ==RESULT_OK && data!= null){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            imageuri= result.getUri();

            profileImageView.setImageURI(imageuri);
        }
        else{

            Toast.makeText(this, "Error, Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullnameet.getText().toString())){
            Toast.makeText(this, "name is mandate", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(addresset.getText().toString())){
            Toast.makeText(this, "address is mandate", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(phoneet.getText().toString())){
            Toast.makeText(this, "phone is mandate", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked")){

            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Updating your account please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageuri!=null){
            final StorageReference fileRef= storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhone()+".jpg");

            uploadTask = fileRef.putFile(imageuri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloaduri = task.getResult();

                        myUrl=downloaduri.toString();

                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                                .child("Users");
                        HashMap<String,Object> hashMap= new HashMap<>();
                        hashMap.put("name",fullnameet.getText().toString());
                        hashMap.put("address",addresset.getText().toString());
                        hashMap.put("phone",phoneet.getText().toString());
                        hashMap.put("image",myUrl);

                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(hashMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(SettingsActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void userInfoDisplay(CircleImageView profileImageView, EditText fullnameet,
                                 EditText phoneet, EditText addresset) {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").
                child(Prevalent.currentOnlineUser.getPhone());

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image=snapshot.child("image").getValue().toString();
                        String phone=snapshot.child("phone").getValue().toString();
                        String name=snapshot.child("name").getValue().toString();
                        String address=snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullnameet.setText(name);
                        phoneet.setText(phone);
                        addresset.setText(address);


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}