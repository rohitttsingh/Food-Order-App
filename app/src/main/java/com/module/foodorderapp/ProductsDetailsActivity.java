package com.module.foodorderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.module.foodorderapp.Model.Product;
import com.module.foodorderapp.Prevalent.Prevalent;
import com.rey.material.widget.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class ProductsDetailsActivity extends AppCompatActivity {
private ImageView productimage;
private TextView productPrice,productDecsription, productName;
private Button addTocart , decreasebtn, increasebtn;
private String productid="";
TextView countv;
int count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_details);

        productimage=findViewById(R.id.product_image_details);
        productPrice=findViewById(R.id.product_price_details);
        productDecsription=findViewById(R.id.product_description_details);
        productName=findViewById(R.id.product_name_details);
        countv=findViewById(R.id.counttv);
        addTocart=findViewById(R.id.add_product_cart);
        decreasebtn=findViewById(R.id.btndecrease);
        increasebtn=findViewById(R.id.btnincrease);

        productid=getIntent().getStringExtra("pid");
        
        getProductDetails(productid);

        decreasebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count=count-1;
                if(count<1);
                else
                countv.setText(""+count);
            }
        });

        increasebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count=count+1;
                if(count>10);
                else
                countv.setText(""+count);
            }
        });
        
        addTocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addingToCartList();
            }
        });
    }

    private void addingToCartList() {
        String savecurrentTime, saveCurrentDate;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate= new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        savecurrentTime=currentTime.format(callForDate.getTime());

       final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");

       String countstring=String.valueOf(count);
        HashMap<String, Object> cartMap= new HashMap<>();
        cartMap.put("pid",productid);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",savecurrentTime);
        cartMap.put("quantity",countstring);
        cartMap.put("discount","");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productid)
                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                            .child("Products").child(productid)
                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(ProductsDetailsActivity.this, count+" "+productName.getText().toString()+" Added To Cart", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                            }

                        }
                    });
                }else{
                    
                }

            }
        });


    }


    private void getProductDetails(String productid) {

        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Product product=snapshot.getValue(Product.class);

                    productName.setText(product.getPname());
                    productDecsription.setText(product.getDescription());
                    productPrice.setText(product.getPrice());

                    Picasso.get().load(product.getImage()).into(productimage);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}