package com.module.foodorderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.module.foodorderapp.Model.Users;
import com.module.foodorderapp.Prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);
        loadingbar=new ProgressDialog(this);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);

        if (UserPasswordKey != "" && UserPhoneKey !=""){
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){
                AllowAccess(UserPhoneKey,UserPasswordKey);

                loadingbar.setTitle("Loading");
                loadingbar.setMessage("Please wait while we are checking your credentials");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
            }
        }

    }

    private void AllowAccess(final String phone,final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists())
                {
                    Users users=dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    if (users.getPhone().equals(phone)){
                        if (users.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this,"Logged SSuccessfully",Toast.LENGTH_LONG).show();
                            loadingbar.dismiss();
                            Prevalent.currentOnlineUser=users;
                            startActivity(new Intent(MainActivity.this,HomeActivity.class));

                        }
                        else Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MainActivity.this,"Account Doesn't Exists",Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void gotoSignup(View view){
        startActivity(new Intent(getApplicationContext(),SignupActivity.class));
    }
}