package com.module.foodorderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.module.foodorderapp.Model.Users;
import com.module.foodorderapp.Prevalent.Prevalent;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class AdminLoginActivity extends AppCompatActivity {
    private Button LoginButton;
    private EditText  InputPhoneNumber, InputPassword;
    private ProgressDialog loadingbar;

     String parentDbName="Admins";
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        LoginButton=findViewById(R.id.login);
        InputPhoneNumber=findViewById(R.id.Phone);
        InputPassword=findViewById(R.id.Password);
        loadingbar=new ProgressDialog(this);
        checkBox=findViewById(R.id.checkbox);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        Paper.init(this);
    }

    private void LoginUser() {
        String phone= InputPhoneNumber.getText().toString();
        String password= InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(AdminLoginActivity.this,"Enter the value of phone",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(AdminLoginActivity.this,"Enter the value of password",Toast.LENGTH_LONG).show();
        }
        else{
            loadingbar.setTitle("Loading");
            loadingbar.setMessage("Please wait siji");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            AllowAccessToAccount(phone,password);
        }
    }

    private void AllowAccessToAccount(String phone, String password) {


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Admins").child(phone).exists())
                {
                    Users users=dataSnapshot.child("Admins").child(phone).getValue(Users.class);

                    if (users.getPhone().equals(phone)){
                        if (users.getPassword().equals(password)){
                            Toast.makeText(AdminLoginActivity.this,"login success",Toast.LENGTH_LONG).show();
                            loadingbar.dismiss();
                            startActivity(new Intent(AdminLoginActivity.this,AdminAddnewProductActivity.class));

                        }
                        else Toast.makeText(AdminLoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(AdminLoginActivity.this, "phone is incorrect", Toast.LENGTH_SHORT).show();


                }else{
                    Toast.makeText(AdminLoginActivity.this,"Account Doest Exsists",Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}