package com.module.foodorderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.module.foodorderapp.Model.AdminOrders;

public class OrderAdminNewActivity extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference orderRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_admin_new);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        orderList= findViewById(R.id.order_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef, AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders,AdminOrderViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder,  final int position, @NonNull AdminOrders model) {


                holder.userName.setText("Name: "+model.getName());
                holder.userPhoneNumber.setText("Phone: "+model.getPhone());
                holder.userShippingAddress.setText("Shipping Address: "+model.getAddress()+", "+model.getCity());
                holder.userTotalprice.setText("Total Price: Rs "+model.getTotalAmount());
                holder.userDateTime.setText("Order At: "+model.getDate()+" Time: "+model.getTime());


                holder.ShowOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String uID = getRef(position).getKey();
                        Intent intent= new Intent(getApplicationContext(),AdminUserProductActivity.class);
                        intent.putExtra("uid",uID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[]= new CharSequence[]{
                                "Yes" , "No"
                        };

                        AlertDialog.Builder builder= new AlertDialog.Builder(OrderAdminNewActivity.this);
                        builder.setTitle("Have you Shipped The order Yet ?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i==0)
                                {
                                    String uID = getRef(position).getKey();
                                    removeOrder(uID);


                                }
                                else
                                {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);

                return new AdminOrderViewHolder(view);
            }
        };

        orderList.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeOrder(String uID) {
        orderRef.child(uID).removeValue();
    }

    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder{
        public TextView userName , userPhoneNumber, userShippingAddress,userDateTime,userTotalprice;
        public Button ShowOrderBtn;

        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            userDateTime=itemView.findViewById(R.id.order_date_time);
            userName=itemView.findViewById(R.id.order_user_name);
            userPhoneNumber=itemView.findViewById(R.id.order_phone_number);
            userTotalprice=itemView.findViewById(R.id.order_total_price);
            ShowOrderBtn=itemView.findViewById(R.id.show_all_products_btn);
            userShippingAddress=itemView.findViewById(R.id.order_address_city);


        }
    }
}