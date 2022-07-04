package com.akashorderandpickup.akashadminonp.adapter;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akashorderandpickup.akashadminonp.R;
import com.akashorderandpickup.akashadminonp.util.StoreType;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class RestaurantAdapter extends FirestoreAdapter<RestaurantAdapter.ViewHolder> {
    public static final String TAG = "RestaurantAdapter";
    public static final int shops_in_stores_activity = 0;
    public static final int shops_in_cartstoreslist_activity = 1;

    public interface OnRestaurantSelectedListener {
        /**
         * @param i is used as Identifier for clicks
         *          0 - open store detail page
         *          1 - Toggle ApprovalStatus
         *          2 - Add Store pic
         */
        void onRestaurantSelected(DocumentSnapshot restaurant, int i, String value);
    }

    private OnRestaurantSelectedListener mListener;
    private Boolean IsStoresActivity = false;

    public RestaurantAdapter(Query query, OnRestaurantSelectedListener listener, int activityName) {
        super(query);
        mListener = listener;
        if(shops_in_stores_activity == activityName) IsStoresActivity = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener, IsStoresActivity);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewGroup bannerView, shopView;
        ImageView imageView, liveView, bannerImage;
        TextView nameView;
        RatingBar ratingBar;
        TextView numRatingsView;
        TextView priceView;
        TextView categoryView;
        TextView cityView;
        TextView ratingValue;
        //ViewGroup mRelativeLayout;
        TextView mApprovalStatus;
        EditText mApprovalStatusEditText;
        Button mApprove;

        public ViewHolder(View itemView) {
            super(itemView);
            shopView = itemView.findViewById(R.id.shop_view);
            liveView = itemView.findViewById(R.id.shop_is_live);
            imageView = itemView.findViewById(R.id.restaurant_item_image);
            bannerView = itemView.findViewById(R.id.banner_view);
            bannerImage = itemView.findViewById(R.id.banner_view_image);
            nameView = itemView.findViewById(R.id.restaurant_item_name);
            ratingBar = itemView.findViewById(R.id.restaurant_item_rating);
            ratingValue = itemView.findViewById(R.id.restaurant_rating_text);
            numRatingsView = itemView.findViewById(R.id.restaurant_item_num_ratings);
            priceView = itemView.findViewById(R.id.restaurant_item_price);
            categoryView = itemView.findViewById(R.id.restaurant_item_category);
            cityView = itemView.findViewById(R.id.restaurant_item_city);
            //mRelativeLayout = itemView.findViewById(R.id.main_layout_shop);
            mApprovalStatus = itemView.findViewById(R.id.approval_status_existing_value);
            mApprovalStatusEditText = itemView.findViewById(R.id.approval_status_new_value);
            mApprove = itemView.findViewById(R.id.btn_change_approval_status);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnRestaurantSelectedListener listener, Boolean isMainActivity) {

            //Restaurant restaurant = snapshot.toObject(Restaurant.class);
            //Log.w("RestaurantAdapter", restaurant.toString());
            Log.e(TAG, "Store: " + snapshot.toString());
            Resources resources = itemView.getResources();
            String toSetText = "";
            toSetText = new StoreType(itemView.getContext(), Integer.parseInt(snapshot.getString("storetype"))).getStoretypename();

            // Load image
            try{
                Glide.with(imageView.getContext())
                        .load(snapshot.getString("photo"))
                        .into(imageView);
            }catch (Exception e){e.printStackTrace();}

            if(snapshot.get("IsLive")!=null){
                if((Boolean) snapshot.get("IsLive")){
                    liveView.setVisibility(View.VISIBLE);
                }else{
                    liveView.setVisibility(View.GONE);
                }
            }else{
                liveView.setVisibility(View.GONE);
            }

            shopView.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.white)));
            bannerView.setVisibility(View.GONE);
            try {
                if (snapshot.get("banner") != null) {
                    Log.w("ResAdapter", snapshot.get("banner").toString());
                    //Log.w("ResAdapter", snapshot.get("bannerImage").toString());
                    if (snapshot.getBoolean("banner") && snapshot.get("bannerImage") != null) {
                        bannerView.setVisibility(View.VISIBLE);
                        Glide.with(bannerImage.getContext())
                                .load(snapshot.getString("bannerImage"))
                                .into(bannerImage);
                    }
                    if (snapshot.get("color") != null) {
                        Log.w("ResAdapter", snapshot.get("color").toString());
                        shopView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(snapshot.getString("color"))));
                    }
                }
            }catch (Exception e1){
                e1.printStackTrace();
                //Toast.makeText(itemView.getContext(), "Error:" + e1.getMessage(), Toast.LENGTH_SHORT);
            }

            /*liveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Live status of "+snapshot.getString("storename")+" is OPEN NOW", Toast.LENGTH_SHORT).show();
                }
            });*/

            priceView.setBackgroundColor(itemView.getResources().getColor(R.color.orange_matte));//So that recycler doesn't messes up
            if(snapshot.get("delivery")!=null){
                if((Boolean) snapshot.get("delivery") ){
                    if(snapshot.get("deliveryCategory")!=null &&
                            snapshot.getString("deliveryCategory").equalsIgnoreCase("free")){
                        priceView.setText("Free Delivery");
                        priceView.setBackgroundColor(itemView.getResources().getColor(R.color.teal_700));
                    }else{
                        priceView.setBackgroundColor(itemView.getResources().getColor(R.color.red_myntra));
                        if(snapshot.get("deliveryCharge")!=null){
                            String delivery_string = "₹" + snapshot.getString("deliveryCharge") + " Delivery charge ";
                            if(snapshot.getString("storename").contains("Leegum")){
                                delivery_string += "\nFree above ₹" + snapshot.getString("freeDeliveryAbove");
                                priceView.setBackgroundColor(itemView.getResources().getColor(R.color.teal_700));
                            }
                            priceView.setText(delivery_string);
                        }else{
                            priceView.setText("Paid Delivery");
                        }
                    }

                }else{
                    priceView.setText("Self Pickup");
                }
            }else{
                priceView.setText("Self Pickup");
            }

            nameView.setText(snapshot.getString("storename"));
            String pin  = snapshot.getString("Pincode");
            if(pin.equals("0")){
                cityView.setText(snapshot.getString("city"));
                //cityView.setText(R.string.all_cities_india);
            }else {
                cityView.setText(snapshot.getString("city") + ", " + pin);
            }
            categoryView.setText(toSetText);
            //ratingBar.setRating(Float.parseFloat(snapshot.get("AvgRating").toString()));
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.CEILING);
            String rating = String.valueOf(snapshot.get("AvgRating"));
            if(rating == null) rating = "null";
            else
            try {
                rating = rating.length() < 4 ? rating : rating.substring(0, 4);
                rating = df.format(snapshot.get("AvgRating"));
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "Error in parsing rating.");
                Log.e(TAG, "Store: " + snapshot.toString());
            }

            ratingValue.setText(" " + rating + " ");
            numRatingsView.setText("(" + snapshot.getLong("numRatings") + ")");

            //Double numRating = snapshot.getDouble("numRatings");
            Double numRating = 0D;
            if(snapshot.get("numRatings") == null){
                Log.e(TAG, "Error in parsing rating.");
                Log.e(TAG, "Store: " + snapshot.toString());
            }else numRating = Double.parseDouble(String.valueOf(snapshot.get("numRatings")));
            if(numRating < 1 || numRating==10
                    //&& snapshot.getDouble("AvgRating") > 4.5
            ){
                ratingValue.setText("  New  ");
                ratingBar.setVisibility(View.GONE);
                numRatingsView.setVisibility(View.GONE);
            }else{
                ratingBar.setVisibility(View.VISIBLE);
                numRatingsView.setVisibility(View.VISIBLE);
            }
            //priceView.setText(toSetText);
            
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRestaurantSelected(snapshot, 0, "");
                    }
                }
            });

            String curr_approval_status_value = snapshot.getString("ApprovalStatus");
            mApprovalStatus.setText(itemView.getResources()
                    .getString(R.string.approval_status
                            , curr_approval_status_value));
            if(curr_approval_status_value.equals("0")){
                mApprovalStatusEditText.setText("1");
                mApprove.setText("Remove");
            }else{
                mApprovalStatusEditText.setText("0");
                mApprove.setText("Approve");
            }
            mApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onRestaurantSelected(snapshot, 1, mApprovalStatusEditText.getText().toString());
                    }
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onRestaurantSelected(snapshot, 2, "");
                    }
                }
            });
        }

    }
}