package com.module.foodorderapp.ViewHolder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.module.foodorderapp.Interface.ItemClickListner;
import com.module.foodorderapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtproductname,txtproductdescription,txtproductprice;
    public ImageView imageView;
    public ItemClickListner listner;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView=itemView.findViewById(R.id.productimage);
        txtproductname=itemView.findViewById(R.id.productname);
        txtproductdescription=itemView.findViewById(R.id.productdesc);
        txtproductprice=itemView.findViewById(R.id.productprice);



    }

    public void setItemClicklitner(ItemClickListner listner){
        this.listner=listner;
    }

    @Override
    public void onClick(View view) {

        listner.onClick(view,getAdapterPosition(),false);
    }
}
