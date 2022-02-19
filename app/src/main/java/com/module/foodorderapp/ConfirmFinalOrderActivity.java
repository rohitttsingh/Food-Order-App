package com.module.foodorderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.module.foodorderapp.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
private EditText nameEditText , phoneEditText, addressEditText, ciyEditText;
private Button confirmOrderBtn;
private String totalAmount="";
private ProgressDialog progressDialog;
private TextView TotalPricetv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        nameEditText=findViewById(R.id.shippment_name);
        phoneEditText=findViewById(R.id.shippment_phone);
        addressEditText=findViewById(R.id.shippment_address);
        ciyEditText=findViewById(R.id.shippment_city);
        confirmOrderBtn=findViewById(R.id.confirmbtn);
        TotalPricetv=findViewById(R.id.pricetv);
        totalAmount =getIntent().getStringExtra("Total Price");
        TotalPricetv.setText("Total Amount: Rs."+ totalAmount);
        Toast.makeText(this, "Total Price is "+totalAmount, Toast.LENGTH_SHORT).show();
        progressDialog = new ProgressDialog(this);
        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });

    }

    private void Check() {

        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Please Provide your full name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "Please Provide your Phone Number", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Please Provide your Address", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(ciyEditText.getText().toString())){
            Toast.makeText(this, "Please Provide your City", Toast.LENGTH_SHORT).show();

        }
        else{
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please Wait, while we are  placing your order");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            ConfirmOrder();

        }
    }

    private void ConfirmOrder() {

        final String savecurrentTime, saveCurrentDate;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        savecurrentTime=currentTime.format(callForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object>  orderMap=new HashMap<>();

        orderMap.put("totalAmount",totalAmount);
        orderMap.put("name",nameEditText.getText().toString());
        orderMap.put("phone",phoneEditText.getText().toString());
        orderMap.put("address",addressEditText.getText().toString());
        orderMap.put("city",ciyEditText.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",savecurrentTime);
        orderMap.put("state","not shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(ConfirmFinalOrderActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                progressDialog.dismiss();

                                Toast.makeText(ConfirmFinalOrderActivity.this, "Some Error", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(ConfirmFinalOrderActivity.this, "Some Error", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}