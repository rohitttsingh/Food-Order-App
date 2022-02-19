package com.module.foodorderapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.module.foodorderapp.Interface.ItemClickListner;
import com.module.foodorderapp.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtProductname,txtproductprice,txtproductquqntity;
    public ItemClickListner itemClickListner;

    public CartViewHolder(@NonNull View itemView)
    {
        super(itemView);
        txtproductprice=itemView.findViewById(R.id.cart_product_price);
        txtproductquqntity=itemView.findViewById(R.id.cart_product_quantity);
        txtProductname=itemView.findViewById(R.id.cart_product_name);

    }

    @Override
    public void onClick(View view)

    {

        itemClickListner.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
