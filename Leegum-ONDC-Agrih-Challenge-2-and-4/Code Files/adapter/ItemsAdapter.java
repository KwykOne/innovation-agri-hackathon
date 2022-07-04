package com.orderandpickupforbusinesses.orderpickupforbusinesses.adapter;

import android.content.res.Resources;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.ItemsListActivity;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.MainActivity;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.R;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.model.Item;
import com.google.firebase.firestore.Query;

import java.time.chrono.IsoChronology;

public class ItemsAdapter extends FirestoreAdapter<com.orderandpickupforbusinesses.orderpickupforbusinesses.adapter.ItemsAdapter.ViewHolder> {
    public static final int products_in_search_activity = 0;
    public static final int products_in_items_list_activity = 1;

    public interface OnItemAddedListener {
        /**
         *         int i is passed as an identifier for which view clicked
         *         ('0' for adding item from catalogue,
         *         '1' for plus button, '2' for minus button, '3' for add button
         *         , '4' to set In stock, '5' to set Out of stock
         *         ,'7' to delete product)
         */
        void onitemSelected(Item item, String id, int i);
        //void onitemSelected(Item item, String id);
    }
    private OnItemAddedListener mitemlistener;
    private int IsWhichActivity = -1;

    public ItemsAdapter(Query query, OnItemAddedListener listener, int activityName) {
        super(query);
        this.mitemlistener = listener;
        IsWhichActivity = activityName;
    }

    @NonNull
    @Override
    public com.orderandpickupforbusinesses.orderpickupforbusinesses.adapter.ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //return null;
//        if(IsWhichActivity == products_in_search_activity){
//            return new ViewHolder(LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_catalogue, parent, false), IsWhichActivity);
//        }
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sold_by_shop, parent, false), IsWhichActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull com.orderandpickupforbusinesses.orderpickupforbusinesses.adapter.ItemsAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position).toObject(Item.class),mitemlistener,getSnapshot(position).getId());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, discountInfo;
        ImageView itemImage;
        ImageView add, delete;//Now the edit symbol
        Button mAddFromCatalogueBtn;
        SwitchMaterial mStockAvailabilityToggle;
        ImageButton edit_item;
//        TextView itemQuantity;
//        Button plus, minus, add;
        int whichActivity = -1;

        public ViewHolder(View itemView, int IsWhichActivity) {
            super(itemView);
            whichActivity = IsWhichActivity;
            itemName = itemView.findViewById(R.id.item_text);
            itemPrice = itemView.findViewById(R.id.item_price_view);
            discountInfo = itemView.findViewById(R.id.discount_info);
            itemImage = itemView.findViewById(R.id.item_image);
            //itemQuantity = itemView.findViewById(R.id.quantity_input);
            //plus = itemView.findViewById(R.id.plus_button);
            //minus = itemView.findViewById(R.id.minus_button);
            //if(IsWhichActivity == products_in_items_list_activity) {
                add = itemView.findViewById(R.id.listview_button_add_item);
                delete = itemView.findViewById(R.id.delete_item);
                mStockAvailabilityToggle = itemView.findViewById(R.id.toggle_item_in_stock);
            //}
            //edit_item = itemView.findViewById(R.id.listview_button_add_item);
            mAddFromCatalogueBtn = itemView.findViewById(R.id.add_btn);
        }

        public void bind(Item item, OnItemAddedListener mitemlistener, String id) {
            itemImage.setVisibility(View.VISIBLE);
            if (item.getProductName() == null) {
                itemName.setText(item.getItemName());
            } else {
                String nameString = item.getProductName();
                if (item.getProductQuantity() != null)
                    nameString += " (" + item.getProductQuantity();
                if (item.getProductUnit() != null)
                    nameString += " " + item.getProductUnit() + ")";
                itemName.setText(nameString);
            }

            discountInfo.setVisibility(View.GONE);
            if (item.getProductPrice() == null) {
                //String price_item = item.getItemPrice();
                //price_item = price_item.replace("₹", "");
                itemPrice.setText(item.getItemPrice());
            } else {
                Integer priceDiscounted = -1;
                Integer price = Integer.parseInt(String.valueOf(item.getProductPrice()));
                itemPrice.setText("₹" + String.valueOf(item.getProductPrice()));

                if (item.getProductDiscountedPrice() != null)
                    priceDiscounted = Integer.parseInt(String.valueOf(item.getProductDiscountedPrice()));

                if (priceDiscounted > 0 && priceDiscounted < price) {
                    discountInfo.setVisibility(View.VISIBLE);
                    itemPrice.setText("₹" + priceDiscounted);
                    Strikethrough(discountInfo, "₹" + price, " ₹" + priceDiscounted, price, priceDiscounted);
                    //Strikethrough(itemPrice, "₹" + price, " ₹" + priceDiscounted, price, priceDiscounted);
                }
            }
            String productPhoto = null;
            if (item.getProductImage() != null && item.getProductImage().size() > 0) {
                int n = item.getProductImage().size() - 1;
                productPhoto = item.getProductImage().get(n);
            } else if (productPhoto == null) {
                productPhoto = item.getPhoto();
            }
//            Glide.with(itemImage.getContext())
//                    .load(item.getPhoto())
//                    .into(itemImage);
            if (productPhoto == null || productPhoto.isEmpty() || productPhoto.equals("NA")) {
                //if(item.getPhoto() == null || item.getPhoto().equals("NA")){
                //itemImage.setVisibility(View.GONE);
                itemImage.setImageResource(R.drawable.ic_baseline_shopping_bag_24);
            } else {
                //add.setVisibility(View.GONE);
                //mAddFromCatalogueBtn.setVisibility(View.VISIBLE);
                itemImage.setVisibility(View.VISIBLE);
                Glide.with(itemImage.getContext())
                        .load(productPhoto)
                        .into(itemImage);
            }

            Boolean catalogueProductFlag = false, productInStock = true;
            if (item.getFromCatalogue() != null) {
                catalogueProductFlag = item.getFromCatalogue();
            } else if (item.getCatalogue() != null && item.getCatalogue().equals("true")) {
                catalogueProductFlag = true;
            }
            if (item.getInStock() != null) {
                productInStock = item.getInStock();
            } else if (item.getStock() != null && !item.getStock()) {
                productInStock = false;
            }

            if ((catalogueProductFlag && whichActivity!=products_in_items_list_activity) || whichActivity == products_in_search_activity) {
                mStockAvailabilityToggle.setVisibility(View.GONE);
                add.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
                mAddFromCatalogueBtn.setVisibility(View.VISIBLE);
            } else {
                mStockAvailabilityToggle.setVisibility(View.VISIBLE);
                if (productInStock) {
                    mStockAvailabilityToggle.setChecked(true);
                    mStockAvailabilityToggle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                    mStockAvailabilityToggle.setText("In stock");
                } else {
                    mStockAvailabilityToggle.setChecked(false);
                    mStockAvailabilityToggle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                    mStockAvailabilityToggle.setText("Out of stock");
                }
            }

            //itemQuantity.setText("0");

//            plus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int n = Integer.parseInt(itemQuantity.getText().toString()) +1 ;
//                    Log.w("ItemsAdapter", "Value of n after plus press is: "+n);
//                    itemQuantity.setText(String.valueOf(n));
////                    if (mitemlistener != null)
////                        mitemlistener.onitemSelected(item, 1, n);
//                }
//            });
//            minus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int n = Integer.parseInt(itemQuantity.getText().toString());
//                    if(n>0){
//                        itemQuantity.setText(String.valueOf(n-1));
////                        if (mitemlistener != null)
////                            mitemlistener.onitemSelected(item, 2, n-1);
//                    }else{
//                        Snackbar.make(itemView.getRootView(),"Quantity Cannot go below Zero",Snackbar.LENGTH_SHORT).show();
//                    }
////                    if (listener != null) {
////                        listener.onRestaurantSelected(snapshot);
////                    }
//                }
//            });
            mAddFromCatalogueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mitemlistener != null)
                        mitemlistener.onitemSelected(item, id, 0);
                }
            });
            if(whichActivity == products_in_items_list_activity){
                add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mitemlistener != null)
                        mitemlistener.onitemSelected(item, id, 6);
                    //int n = Integer.parseInt(itemQuantity.getText().toString());
//                    if(n==0) {
//                        //Toast.makeText(itemView.getContext(), "Quantity cannot be 0 ", Toast.LENGTH_SHORT).show();
//                        Snackbar.make(itemView.getRootView(),"Quantity cannot be 0 ", Snackbar.LENGTH_SHORT).show();
//                    }else{
//                        //Snackbar.make(itemView.getRootView(),"Item has been added to cart",Snackbar.LENGTH_SHORT).show();
//                        if (mitemlistener != null)
//                            mitemlistener.onitemSelected(item, 3, n);
//                    }

                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mitemlistener != null)
                        mitemlistener.onitemSelected(item, id, 7);
                }
            });
//            mStockAvailabilityToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//                        // The toggle is enabled
//                        if (mitemlistener != null)
//                            mitemlistener.onitemSelected(item, id, 4); // Pass 4 to set In stock
//                        mStockAvailabilityToggle.setText("In stock");
//                        mStockAvailabilityToggle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
//                    } else {
//                        if (mitemlistener != null)
//                            mitemlistener.onitemSelected(item, id, 5); // Pass 5 to set Out of stock
//                        // The toggle is disabled
//                        mStockAvailabilityToggle.setText("Out of stock");
//                        mStockAvailabilityToggle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
//                    }
//                }
//            });
            mStockAvailabilityToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mStockAvailabilityToggle.isChecked()) {
                        if (mitemlistener != null)
                            mitemlistener.onitemSelected(item, id, 4); // Pass 4 to set In stock
                        mStockAvailabilityToggle.setText("In stock");
                        mStockAvailabilityToggle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                    } else {
                        if (mitemlistener != null)
                            mitemlistener.onitemSelected(item, id, 5); // Pass 5 to set Out of stock
                        // The toggle is disabled
                        mStockAvailabilityToggle.setText("Out of stock");
                        mStockAvailabilityToggle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                    }
                }
            });

        }
        }

        private void Strikethrough(TextView tv, String toStrike, String noStrike, Integer price, Integer priceDiscounted){
            final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();
            final ForegroundColorSpan FOREGROUND_COLOR_SPAN = new ForegroundColorSpan(tv.getContext().getResources().getColor(R.color.purple_700));

            //String s = noStrike ;
            String s = "MRP: " + toStrike ;
            int discount = ((price-priceDiscounted)*100/price);
            String discountStr = " (<1% off)";;
            if(discount > 0) {
                discountStr = " (" + discount + "% off)";
            }
            String finalS = s + discountStr;
            tv.setText(finalS, TextView.BufferType.SPANNABLE);
            Spannable spannable = (Spannable) tv.getText();
            spannable.setSpan(FOREGROUND_COLOR_SPAN, s.length()+1, s.length()+discountStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(STRIKE_THROUGH_SPAN, 5, 5+ toStrike.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


            //String s = toStrike + " " + noStrike;
            //tv.setText(s, TextView.BufferType.SPANNABLE);
            //Spannable spannable = (Spannable) tv.getText();
            //spannable.setSpan(STRIKE_THROUGH_SPAN, 0, toStrike.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

    }
}
