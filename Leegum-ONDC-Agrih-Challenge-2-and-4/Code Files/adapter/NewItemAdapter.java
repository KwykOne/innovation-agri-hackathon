package com.akashorderandpickup.akashadminonp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akashorderandpickup.akashadminonp.R;
import com.akashorderandpickup.akashadminonp.model.Product;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * RecyclerView adapter for a list of Items to add into db from a spreadsheet/Excelsheet.
 * These were specifically written to Add Lovelocal products.
 */
public class NewItemAdapter extends RecyclerView.Adapter<NewItemAdapter.ViewHolder> {
    LayoutInflater inflater;
    private List<Product> myList;

    public NewItemAdapter(Context context, List<Product> list){
        this.inflater = LayoutInflater.from(context);
        this.myList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewItemAdapter.ViewHolder holder, int position) {
        //String name = myList.get(position);
        String name = myList.get(position).getProductName();
        holder.name.setText(name);
        //holder.type.setText(name);
        holder.price.setText(myList.get(position).getProductDiscountedPrice().toString());
        holder.mrp.setText("₹" + myList.get(position).getProductPrice().toString());
        holder.quantity.setText(myList.get(position).getProductQuantity() + " + " + myList.get(position).getProductUnit());
        holder.category.setText(myList.get(position).getProductCategory().toString());

        int num_imgs = myList.get(position).getProductImage().size();
        if(num_imgs == 1) {
            holder.description.setText(myList.get(position).getProductDescription() + "\n" +myList.get(position).getProductImage().get(0));
        }else if(num_imgs == 2){
            holder.description.setText(myList.get(position).getProductDescription() + "\n" +myList.get(position).getProductImage().get(0) + "\n" + myList.get(position).getProductImage().get(1));
        }

        if(!myList.get(position).getProductImage().get(0).equals("NA")) {
            Glide.with(holder.serverImage.getContext())
                    .load(myList.get(position).getProductImage().get(num_imgs-1))
                    .into(holder.serverImage);
        }else{
            holder.serverImage.setImageResource(R.drawable.fui_ic_check_circle_black_128dp);
        }
        if(num_imgs > 1){
            Glide.with(holder.driveImage.getContext())
                .load(myList.get(position).getProductImage().get(0))
                .into(holder.driveImage);
        }else{
            holder.driveImage.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        if(myList == null)
            return 0;
        return myList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name, price, mrp, quantity, description, category;
        public ImageView serverImage, driveImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            serverImage = itemView.findViewById(R.id.item_image);
            driveImage = itemView.findViewById(R.id.drive_image);

            name = itemView.findViewById(R.id.item_text);
            mrp = itemView.findViewById(R.id.item_full_price_text);
            price = itemView.findViewById(R.id.item_price_view);
            quantity = itemView.findViewById(R.id.discount_info);
            category = itemView.findViewById(R.id.category_text);
            description = itemView.findViewById(R.id.desc_text);
            //type = itemView.findViewById(R.id.restaurant_item_price);
        }
    }
}