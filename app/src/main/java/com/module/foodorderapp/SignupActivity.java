package com.module.foodorderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
private Button CreateAccountBtn;
private EditText InputName, InputPhoneNumber, InputPassword;
private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        CreateAccountBtn=findViewById(R.id.button);
        InputName=findViewById(R.id.editTextTextPersonName);
        InputPhoneNumber=findViewById(R.id.editTextPhone);
        InputPassword=findViewById(R.id.editTextTextPassword);
        loadingbar=new ProgressDialog(this);

        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
        CreateAccountBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(SignupActivity.this,AdminLoginActivity.class));
                return true;
            }
        });

    }

    private void createAccount() {
        String name= InputName.getText().toString();
        String phone= InputPhoneNumber.getText().toString();
        String password= InputPassword.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(SignupActivity.this,"Enter the value of name",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(SignupActivity.this,"Enter the value of phone",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(SignupActivity.this,"Enter the value of password",Toast.LENGTH_LONG).show();
        }
        else{
            loadingbar.setTitle("Loading");
            loadingbar.setMessage("Please wait while we are checking your credentials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            ValidatePhoneNumber(name,phone,password);
        }

        }

    private void ValidatePhoneNumber(String name, String phone, String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {

                if (!(datasnapshot.child("Users").child(phone).exists())){
                    HashMap<String, Object> userdataMap=new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);

                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                                        loadingbar.dismiss();
                                        startActivity(new Intent(SignupActivity.this,LoginActivity.class));

                                    }
                                    else{
                                        Toast.makeText(SignupActivity.this, "Network Error", Toast.LENGTH_LONG).show();

                                        loadingbar.dismiss();

                                    }
                                }
                            });

                }
                else{
                    Toast.makeText(SignupActivity.this, "Already Exists", Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                    startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void gotologin(View view){
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }

    public void uploaditlater(View view){
        Toast.makeText(SignupActivity.this, "You Can Upload It Later", Toast.LENGTH_LONG).show();

    }

}
