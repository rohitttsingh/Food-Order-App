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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddnewProductActivity extends AppCompatActivity {

    private String Description, price , pname , saveCurrentDate , saveCurrentTime ;
private Button Addproduct, ViewOrderBtn;
private EditText Productname,productdesc,productprice;
private ImageView productimage;
private static final int GalleryPick=1;
private Uri imageuri;
private String Productradomkey, downloadimageurl;
private StorageReference ProductImageRef;
private DatabaseReference Productref;
private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_addnew_product);

        Addproduct=findViewById(R.id.addbutton);
        Productname=findViewById(R.id.productname);
        productdesc=findViewById(R.id.productdescription);
        productprice=findViewById(R.id.productcost);
        productimage=findViewById(R.id.select_product_image);
        ViewOrderBtn=findViewById(R.id.viewOrder);
        ProductImageRef= FirebaseStorage.getInstance().getReference().child("Product Images");
        Productref=FirebaseDatabase.getInstance().getReference().child("Products");
        loadingbar=new ProgressDialog(this);

        productimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });
        
        Addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VaidateProductData();
            }
        });
        ViewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),OrderAdminNewActivity.class));
            }
        });
    }

    private void VaidateProductData() {
        Description=productdesc.getText().toString();
        pname=Productname.getText().toString();
        price=productprice.getText().toString();

        if (imageuri==null) {
            Toast.makeText(this, "Please Load the image first", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Please enter Description", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(pname)) {
            Toast.makeText(this, "Please enter Product Name", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "Please price", Toast.LENGTH_SHORT).show();

        }
        else{
            loadingbar.setTitle("Loading");
            loadingbar.setMessage("Please wait siji");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            StoreProductInformation();
        }

    }

    private void StoreProductInformation() {
        Calendar calendar= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        Productradomkey = saveCurrentDate+saveCurrentTime;

        StorageReference filePath= ProductImageRef.child(imageuri.getLastPathSegment() + Productradomkey);
        final UploadTask uploadTask=filePath.putFile(imageuri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message= e.toString();
                Toast.makeText(AdminAddnewProductActivity.this,"error"+ message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddnewProductActivity.this, "Image Added Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful()){

                            throw task.getException();
                        }

                        downloadimageurl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadimageurl=task.getResult().toString();
                            Toast.makeText(AdminAddnewProductActivity.this, "getting image url to db success", Toast.LENGTH_SHORT).show();

                            SaveProductiInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void SaveProductiInfoToDatabase() {
        HashMap<String, Object> productmap=new HashMap<>();

        productmap.put("pid",Productradomkey);
        productmap.put("date",saveCurrentDate);
        productmap.put("time",saveCurrentTime);
        productmap.put("description",Description);
        productmap.put("price",price);
        productmap.put("pname",pname);
        productmap.put("image",downloadimageurl);

        Productref.child(Productradomkey).updateChildren(productmap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            loadingbar.dismiss();

                            Toast.makeText(AdminAddnewProductActivity.this, "Product AddedSuccessfully", Toast.LENGTH_SHORT).show();
                        }
                        else{

                            String message=task.getException().toString();
                            loadingbar.dismiss();

                            Toast.makeText(AdminAddnewProductActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    private void OpenGallery() {
        Intent galleryIntent =new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            imageuri = data.getData();
            productimage.setImageURI(imageuri);
        }
    }
}