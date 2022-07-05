package com.orderandpickupforbusinesses.orderpickupforbusinesses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.model.Item;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.util.AppConstants;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.util.AppUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddProductActivity";
    public static final String KEY_PRODUCT_DOCUMENT_ID = "key_product_document_id";
    public static final String KEY_EDIT_EXISTING_DOCUMENT = "key_edit_existing_document";
    public static final String KEY_ADD_FROM_CATALGOUE = "key_add_new_product_from_catalgoue";
    public static final String KEY_CATALOGUE_PRODUCT_ID = "key_id_product_in_catalgoue";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private String userPhoneNo, photoUrl;

    ProgressBar mAddProductProgressBar;

    ImageView mProductImage;
    Uri imageUri, finalImageUri;
    Boolean uploadedphotoSucessfully, uploadphotoInProgress;
    String resultPath;

    EditText mProductName, mProductCategory, mProductPrice, mProductDiscountedPrice, mProductQuantity, mProductQuantityUnit, mProductDescription;
    Button mAddProduct;

    Boolean loadingExistingProductComplete = false;
    Item item;
    String productPhoto = null;
    ArrayList<String> AllCategorys = new ArrayList<>();
    ArrayList<Integer> AllCategoryIds = new ArrayList<>();

    //Bottom Sheet(Product Units)
    RelativeLayout bottomSheetLayoutUnits;
    BottomSheetBehavior bottomSheetBehaviorUnits;
    BottomSheetDialog bottomSheetDialog;
    ChipGroup chipsUnits;
    //Bottom Sheet(Category )
    RelativeLayout bottomSheetLayoutCategorys;
    BottomSheetBehavior bottomSheetBehaviorCategorys;
    ChipGroup chipsCategorys;
    ArrayList<String> categorys;
    List<String> selectedCategories = new ArrayList<>();
    //Bottom Sheet(Edittext)
    RelativeLayout bottomSheetLayoutEditText;
    BottomSheetBehavior bottomSheetBehaviorEditText;
    Boolean chooseUserCustomCategory = false;
    String userCustomCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mAddProductProgressBar = findViewById(R.id.add_product_progress_bar);
        mProductImage = findViewById(R.id.product_image);
        mProductName = findViewById(R.id.product_name);
        mProductCategory = findViewById(R.id.product_category);
        mProductPrice = findViewById(R.id.product_price);
        mProductDiscountedPrice = findViewById(R.id.product_discounted_price);
        mProductQuantity = findViewById(R.id.quantity_value);
        mProductQuantityUnit = findViewById(R.id.quantity_unit);
        mProductDescription = findViewById(R.id.product_description);

        mAddProduct = findViewById(R.id.btn_add_product);

        mAddProduct.setOnClickListener(this);
        mProductImage.setOnClickListener(this);
        mProductCategory.setOnClickListener(this);
        mProductQuantityUnit.setOnClickListener(this);

        findViewById(R.id.toolbar_back_btn).setOnClickListener(this);
        findViewById(R.id.btn_back_choose_product_unit).setOnClickListener(this);
        findViewById(R.id.btn_back_add_category).setOnClickListener(this);
        findViewById(R.id.btn_back_custom_category).setOnClickListener(this);
        chipsUnits = findViewById(R.id.chipsUnits);
        chipsCategorys = findViewById(R.id.chipsCategorys);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //UserSignedOut
        } else {
            userPhoneNo = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        }

        //CAUTION: Do not change the order of these initialisation
        initBottomSheet();
        initCategoryBottomSheet();
        initEditTextBottomSheet();

        if(getIntent().getBooleanExtra(KEY_EDIT_EXISTING_DOCUMENT, false)){
            Log.w(TAG, "Document Edit");
            findViewById(R.id.grey_shade).setVisibility(View.VISIBLE);
            mAddProductProgressBar.setVisibility(View.VISIBLE);
            loadProduct(false);
        }
        if(getIntent().getBooleanExtra(KEY_ADD_FROM_CATALGOUE, false)){
            Log.w(TAG, "Document Add product from catalogue");
            findViewById(R.id.grey_shade).setVisibility(View.VISIBLE);
            mAddProductProgressBar.setVisibility(View.VISIBLE);
            loadProduct(true);
        }

        mProductDiscountedPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String priceStr = mProductPrice.getText().toString();
                    String discountedPriceStr = mProductDiscountedPrice.getText().toString().trim();
                    if(priceStr.isEmpty() || discountedPriceStr.isEmpty()) {
                        return;
                    }
                    Double price = Double.parseDouble(priceStr);
                    Double discountedPrice = Double.parseDouble(discountedPriceStr);
                    if(price <= discountedPrice){
                        mProductDiscountedPrice.setText("");
                        showToastMsg(getString(R.string.discounted_price_must_be_less_than_mrp));
                        return;
                    }
                }catch (Exception e){
                    mProductDiscountedPrice.setError(getString(R.string.error_product_price_invalid));
                    return;
                }
            }
        });

        mProductPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String priceStr = mProductPrice.getText().toString();
                    String discountedPriceStr = mProductDiscountedPrice.getText().toString().trim();
                    if(priceStr.isEmpty() || discountedPriceStr.isEmpty()) {
                        return;
                    }
                    Double price = Double.parseDouble(priceStr);
                    Double discountedPrice = Double.parseDouble(discountedPriceStr);
                    if(price <= discountedPrice){
                        mProductDiscountedPrice.setText("");
                        showToastMsg(getString(R.string.discounted_price_must_be_less_than_mrp));
                        return;
                    }
                }catch (Exception e){
                    mProductPrice.setError(getString(R.string.error_product_price_invalid));
                    return;
                }
            }
        });

        findViewById(R.id.send_excel_file).setOnClickListener(this);
        findViewById(R.id.search_product_in_global_catalog).setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        if(bottomSheetBehaviorUnits.getState() != BottomSheetBehavior.STATE_HIDDEN){
            viewHideUnitsBottomSheet(true);
        }else if(bottomSheetBehaviorCategorys.getState() != BottomSheetBehavior.STATE_HIDDEN){
            viewHideCategorysBottomSheet(true);
        }else if(bottomSheetBehaviorEditText.getState() != BottomSheetBehavior.STATE_HIDDEN){
            viewHideEditTextBottomSheet(true, false);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_add_product){
            processProductDetails();
        }
        else if(id == R.id.toolbar_back_btn){
            onBackPressed();
        }
        else if(id == R.id.search_product_in_global_catalog){
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra(SearchActivity.OPEN_SEARCH, true);
            intent.putExtra(SearchActivity.KEY_SEARCH_TERM, mProductName.getText().toString());
            startActivity(intent);
        }
        else if(id == R.id.quantity_unit){
            viewHideUnitsBottomSheet(false);
        }
        else if(id == R.id.product_category){
            viewHideCategorysBottomSheet(false);
        }
        else if(id == R.id.btn_back_add_category){
            viewHideCategorysBottomSheet(true);
        }
        else if(id == R.id.btn_add_new_category){
            viewHideEditTextBottomSheet(false, true);
        }
        else if(id == R.id.btn_back_custom_category){
            viewHideEditTextBottomSheet(true, true);
        }
        else if(id == R.id.product_image){
            showToastMsg("Choose Image");
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, AppConstants.STORAGE_PERMISSION_CODE);
        }
        else if(id == R.id.btn_back_choose_product_unit){
            viewHideUnitsBottomSheet(true);
        }
        else if(id == R.id.send_excel_file){
            Context ctx = this;
            //showSnackbar("Excel Clicked");
            AlertDialog.Builder dialog = new AlertDialog.Builder(ctx, R.style.AlertDialogTheme);
            dialog.setTitle(getString(R.string.send_excel_dialog_title));
            //dialog.setIcon(R.drawable.logo_circular_leegum_shops);
            dialog.setCancelable(true);
            dialog.setMessage(getString(R.string.send_excel_dialog_msg));
            dialog.setPositiveButton("Email List", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppUtils.customerSupport(ctx, "", AppUtils.EMAIL, createInfoMsgForSupport(AppUtils.EMAIL), getString(R.string.send_excel_email_subject));
                    dialog.dismiss();
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        }
        else{

        }
    }

    private void processProductDetails(){
        if(getIntent().getBooleanExtra(KEY_EDIT_EXISTING_DOCUMENT, false) && !loadingExistingProductComplete){
            showSnackbar("Still Loading details of the product to edit");
            return;
        }

        String name = mProductName.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            mProductName.setError(getString(R.string.error_product_name_required));
            mProductName.requestFocus();
            return;
        }

        String category = mProductCategory.getText().toString().trim();
        if(TextUtils.isEmpty(category)) {
            mProductCategory.setError(getString(R.string.error_product_category_required));
            mProductCategory.requestFocus();
            return;
        }

        //No need
//        if(category.equals(selectedCategories.toString().replace("[", "")
//                    .replace("]", "").trim())){
//
//        }

        String priceStr = mProductPrice.getText().toString();
        if(TextUtils.isEmpty(priceStr)){
            mProductPrice.setError(getString(R.string.error_product_price_required));
            mProductPrice.requestFocus();
            return;
        }

        Long price;
        try {
            price = Long.parseLong(priceStr);
            if(price < 0){
                mProductPrice.setError(getString(R.string.error_product_price_invalid));
                return;
            }
        }catch (Exception e){
            mProductPrice.setError(getString(R.string.error_product_price_invalid));
            return;
        }

        String quantityStr = mProductQuantity.getText().toString();
        if(TextUtils.isEmpty(quantityStr)){
            mProductQuantity.setError(getString(R.string.error_product_quantity_invalid));
            mProductQuantity.requestFocus();
            return;
        }
        Long quantity;
        try {
            quantity = Long.parseLong(quantityStr);
            if(quantity < 0){
                mProductQuantity.setError(getString(R.string.error_product_quantity_invalid));
                return;
            }
        }catch (Exception e){
            mProductQuantity.setError(getString(R.string.error_product_quantity_invalid));
            return;
        }

        String unit = mProductQuantityUnit.getText().toString();
        if(TextUtils.isEmpty(unit)){
            unit = "NA";
        }

        String discountedPriceStr = mProductDiscountedPrice.getText().toString();
        Long discountedPrice;
        if(!TextUtils.isEmpty(discountedPriceStr)){
            try {
                discountedPrice = Long.parseLong(discountedPriceStr);
            }catch (Exception e){
                mProductDiscountedPrice.setError(getString(R.string.error_product_price_invalid));
                return;
            }
            if(price <= discountedPrice){
                mProductDiscountedPrice.setError(getString(R.string.error_product_price_invalid));
                showSnackbar(getString(R.string.discounted_price_must_be_less_than_mrp));
                return;
            }
        }else{
            discountedPrice = price;
        }

        String desc = mProductDescription.getText().toString().trim();
        if(TextUtils.isEmpty(desc)){
            desc = "NA";
        }

        //Add product in firebase Database
        ArrayList<String> images = new ArrayList<>();
        if(item!=null) {
            if (item.getProductImage() != null && item.getProductImage().size() > 0) {
                images = item.getProductImage();
            } else if (item.getPhoto() != null) {
                images.add(item.getPhoto());
            }
        }

        Item product = new Item(images, name, selectedCategories, price, discountedPrice, quantity, unit, desc, true, false, true, false);
        //Product product = new Product(images, name, selectedCategories, price, discountedPrice, quantity, unit, desc, true, false, true);

        if(finalImageUri == null) {
            if(images.isEmpty()) {
                images.add("NA");
                product.setProductImage(images);
            }
            addProductToDatabase(product);
        }else{
            uploadProductImage(product);
        }
    }

    private void loadProduct(Boolean fromCatalogue){
        String orderDocumentId = getIntent().getStringExtra(KEY_PRODUCT_DOCUMENT_ID);
        String catalogueDocumentId = getIntent().getStringExtra(KEY_CATALOGUE_PRODUCT_ID);
        //Log.w(TAG, "in load product, orderId - " + orderDocumentId);
        DocumentReference productDocRef;
        if(fromCatalogue){
            productDocRef = db.collection("inventory").document(catalogueDocumentId);
        }else {
            productDocRef = db.collection("storeowners").document(userPhoneNo).collection("items").document(orderDocumentId);
        }
        productDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                Log.w(TAG, "loaded doc - " + snapshot.toString());
                item = snapshot.toObject(Item.class);
                Log.w(TAG, "converted doc to item - " + item.toString());
                populateProductDetails(item);
                findViewById(R.id.grey_shade).setVisibility(View.GONE);
                mAddProductProgressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar("Cannot load product details to edit!");
                findViewById(R.id.grey_shade).setVisibility(View.GONE);
                mAddProductProgressBar.setVisibility(View.GONE);
                onBackPressed();
            }
        });
    }

    private void populateProductDetails(Item item){
        Log.w(TAG, "in populateProductDetails - " + item.toString());

        if(item.getProductName() == null){
            mProductName.setText(item.getItemName());
        }else{
            mProductName.setText(item.getProductName());
        }

        if(item.getProductPrice() == null){
            String price_item = item.getItemPrice();
            price_item = price_item.replace("₹", "");
            mProductPrice.setText(price_item);
        }else{
            mProductPrice.setText(String.valueOf(item.getProductPrice()));
        }
        if(item.getProductDiscountedPrice() != null && !item.getProductDiscountedPrice().equals(item.getProductPrice()))mProductDiscountedPrice.setText(String.valueOf(item.getProductDiscountedPrice()));
        if(item.getProductQuantity() != null)mProductQuantity.setText(String.valueOf(item.getProductQuantity()));
        if(item.getProductDescription() != null && !item.getProductDescription().equalsIgnoreCase("NA"))mProductDescription.setText(item.getProductDescription());

        if(item.getProductImage()!=null && item.getProductImage().size()>0) {
            int n = item.getProductImage().size()-1;
            productPhoto = item.getProductImage().get(n);
        }else if(productPhoto == null) {
            productPhoto = item.getPhoto();
        }
        if(productPhoto == null || productPhoto.isEmpty() || productPhoto.equals("NA")){
            //Do nothing
        }else {
            Glide.with(mProductImage.getContext())
                    .load(productPhoto)
                    .into(mProductImage);
        }

        if(item.getProductUnit()!=null && !item.getProductUnit().isEmpty()){
            mProductQuantityUnit.setText(item.getProductUnit());
            for (int i = 0; i < chipsUnits.getChildCount(); i++) {
                Chip chip = (Chip) chipsUnits.getChildAt(i);
                if(chip.getText().equals(item.getProductUnit())) {
                    chip.setChecked(true);
                    break;
                }
            }

//            List<String> unitsList = Arrays.asList(getResources().getStringArray(R.array.product_units));
//            int i = unitsList.indexOf(item.getProductUnit());
//            if(i>=0)chipsUnits.check(i);
        }

        List<String> myProductCategories = item.getProductCategory();
        if(item.getProductCategory()!=null){
            selectedCategories = myProductCategories;
            mProductCategory.setText(String.valueOf(item.getProductCategory())
                    .replace("[", "")  //remove the right bracket
                    .replace("]", "")  //remove the left bracket
                    .trim());

//            Log.w(TAG, AllCategorys + "now going to set product categories - " + myProductCategories);
//            for (String category : myProductCategories) {
//                int i = AllCategorys.indexOf(category);
//                Log.w(TAG,"match found with index: " + i +" for: "+ category);
//                if(i>=0)chipsCategorys.check(i);
//
//                //final int mChipId = AllCategoryIds.get(i);
//            }
            for (int i = 0; i < chipsCategorys.getChildCount(); i++) {
                Chip chip = (Chip) chipsCategorys.getChildAt(i);
                for (String category : myProductCategories) {
                    if(chip.getText().equals(category)){
                        chip.setChecked(true);
                    }
                }
                //chip.setChecked(false);
            }
        }

        findViewById(R.id.grey_shade).setVisibility(View.GONE);
        mAddProductProgressBar.setVisibility(View.GONE);
        loadingExistingProductComplete = true;
    }

    private void createCategory(List<String> categoryList, int progressBarIdentifier) {
        if(categoryList.size() > 0) {
            String[] category = categoryList.toArray(new String[0]);
            DocumentReference storeDocRef = db.collection("storeowners").document(userPhoneNo);
            storeDocRef.update("categories", FieldValue.arrayUnion(category)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    if (progressBarIdentifier == 0) {
                        findViewById(R.id.add_category_progress_bar).setVisibility(View.GONE);
                    } else if (progressBarIdentifier == 1) {
                        findViewById(R.id.creating_custom_category_progress_bar).setVisibility(View.GONE);
                        viewHideEditTextBottomSheet(true, true);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showSnackbar(getString(R.string.no_internet));
                    if (progressBarIdentifier == 0) {
                        findViewById(R.id.add_category_progress_bar).setVisibility(View.GONE);
                    } else if (progressBarIdentifier == 1) {
                        findViewById(R.id.creating_custom_category_progress_bar).setVisibility(View.GONE);
                    }
                }
            });
        }

//        Map<String, Object> cateogryMap = new HashMap<>();
//        cateogryMap.put("category", category);
//        cateogryMap.put("createdAt", FieldValue.serverTimestamp());
//        cateogryMap.put("productCount", 0);
//        DocumentReference documentReference = db.collection("storeowners").document(userPhoneNo)
//                .collection("products").document(category);
//        documentReference.set(cateogryMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//            }
//        });

    }

    private void addProductToDatabase(Item product) {
        createCategory(product.getProductCategory(), -1);//It will be required if catalogue products has new categories then they might not get added otherwise
        mAddProductProgressBar.setVisibility(View.VISIBLE);
        DocumentReference storeDocRef = db.collection("storeowners").document(userPhoneNo);
        if(!getIntent().getBooleanExtra(KEY_EDIT_EXISTING_DOCUMENT, false)) {
            storeDocRef.update("itemsCount", FieldValue.increment(1));//,"categories", FieldValue.arrayUnion(product.getProductCategory()));

            storeDocRef.collection("items").add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        showSnackbar("Successfully Saved!");
                        mAddProductProgressBar.setVisibility(View.GONE);
                        onBackPressed();
                    }else{
                        Exception e = task.getException();
                        Log.w(TAG, "Adding product to database failed", e);
                        e.printStackTrace();
                        mAddProductProgressBar.setVisibility(View.GONE);
                        showSnackbar("Failed with " + e.getMessage());
                    }
                }
            });
        }else {
            String orderDocumentId = getIntent().getStringExtra(KEY_PRODUCT_DOCUMENT_ID);
            DocumentReference documentReference = storeDocRef.collection("items").document(orderDocumentId);

            documentReference.set(product, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showSnackbar("Successfully Saved!");
                    mAddProductProgressBar.setVisibility(View.GONE);
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Adding product to database failed");
                    e.printStackTrace();
                    mAddProductProgressBar.setVisibility(View.GONE);
                    showSnackbar("Failed with " + e.getMessage());
                }
            });
        }
//        Map<String, Object> cateogryMap = new HashMap<>();
//        //cateogryMap.put("category", product.getProductCategory());
//        //cateogryMap.put("updatedAt", FieldValue.arrayUnion(System.currentTimeMillis()));
//        //cateogryMap.put("productCount", FieldValue.increment(1));
//        cateogryMap.put("products", FieldValue.arrayUnion(product));
//
//        //DocumentReference documentReference = db.collection("storeowners").document(userPhoneNo)
//        //        .collection("products").document("product");
    }

    private void uploadProductImage(Item product) {
        mAddProductProgressBar.setVisibility(View.VISIBLE);
        if(!uploadedphotoSucessfully){
            if(finalImageUri!=null){//finalImageUri is the local path of the Image
                uploadedphotoSucessfully = false;
                uploadphotoInProgress = true;
                Snackbar.make(findViewById(android.R.id.content), "Uploading Photo...", Snackbar.LENGTH_INDEFINITE).show();
                uploadToFirebase(finalImageUri, product);
            }else{
                addProductToDatabase(product);
//                mAddProductProgressBar.setVisibility(View.GONE);
//                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, AppConstants.STORAGE_PERMISSION_CODE);
//                //buildImagePickerAlert();
//                showToastMsg("Please Select Image");
            }
            return;
        }
    }

    private void initEditTextBottomSheet() {
        bottomSheetLayoutEditText = findViewById(R.id.bottom_sheet_edit_text);
        bottomSheetBehaviorEditText = BottomSheetBehavior.from(bottomSheetLayoutEditText);
        bottomSheetBehaviorEditText.setFitToContents(true);
        bottomSheetBehaviorEditText.setDraggable(false);

        findViewById(R.id.creating_custom_category_progress_bar).setVisibility(View.GONE);
        findViewById(R.id.grey_shade).setVisibility(View.GONE);
        bottomSheetBehaviorEditText.setState(BottomSheetBehavior.STATE_HIDDEN);
        //viewHideEditTextBottomSheet(true);//Don't do this to prevent unnecessary call to Categorys sheet and database during initialisation

        Button saveNewCategory = findViewById(R.id.btn_save_new_category);
        EditText newCateogry = findViewById(R.id.create_custom_category_edittext);
        saveNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userCustomCategory = newCateogry.getText().toString().trim();
                if(TextUtils.isEmpty(userCustomCategory)){
                    newCateogry.setError(getString(R.string.category_name_is_required));
                    newCateogry.requestFocus();
                }else{
                    findViewById(R.id.creating_custom_category_progress_bar).setVisibility(View.VISIBLE);
                    chooseUserCustomCategory = true;
                    List<String> l = new ArrayList<String>();
                    l.add(userCustomCategory);
                    createCategory(l, 1);
                }
            }
        });
    }

    private void initBottomSheet() {
        //Bottom Sheet
        bottomSheetLayoutUnits = findViewById(R.id.bottom_sheet_product_units);
        bottomSheetBehaviorUnits = BottomSheetBehavior.from(bottomSheetLayoutUnits);
        bottomSheetBehaviorUnits.setFitToContents(true);
        bottomSheetBehaviorUnits.setDraggable(false);
        viewHideUnitsBottomSheet(true);

        //Initialise its UI elements
        List<String> unitsList = Arrays.asList(getResources().getStringArray(R.array.product_units));
        ArrayList<String> units = new ArrayList<>(unitsList);
        setUnitChips(units);
        mProductQuantityUnit.setText(units.get(0));
    }

    private void initCategoryBottomSheet() {
        //Bottom Sheet
        bottomSheetLayoutCategorys = findViewById(R.id.bottom_sheet_choose_category);
        bottomSheetBehaviorCategorys = BottomSheetBehavior.from(bottomSheetLayoutCategorys);
        //bottomSheetBehaviorUnits.setFitToContents(true);
        bottomSheetBehaviorCategorys.setDraggable(false);
        viewHideCategorysBottomSheet(true);

        //Initialise its UI elements
        List<String> categorysList = Arrays.asList(getResources().getStringArray(R.array.product_categorys));
        categorys = new ArrayList<>(categorysList);
        setCategoryChips(categorys);
        //mProductQuantityUnit.setText(categorys.get(0));
        Button addNewCategory, selectCategory;
        addNewCategory = findViewById(R.id.btn_add_new_category);
        selectCategory = findViewById(R.id.btn_select_category);
        selectCategory.setEnabled(false);

        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show an editText and Let him type then add it as chipgroup and select it
                viewHideEditTextBottomSheet(false, true);
            }
        });

        selectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> selectedChipIds = chipsCategorys.getCheckedChipIds();
                if(selectedChipIds.size() > 0) {
                    selectedCategories.clear();
                    for(Integer id: selectedChipIds) {
                        CompoundButton compoundButton = findViewById(id);
                        String category = compoundButton.getText().toString();
                        selectedCategories.add(category);
                    }
                    findViewById(R.id.add_category_progress_bar).setVisibility(View.VISIBLE);
                    mProductCategory.setText(selectedCategories.toString()
                            .replace("[", "")  //remove the right bracket
                            .replace("]", "")  //remove the left bracket
                            .trim());
                    createCategory(selectedCategories, 0);
                    viewHideCategorysBottomSheet(true);
                }else{
                    showSnackbar(getString(R.string.error_product_category_required));
                }
//                int id = chipsCategorys.getCheckedChipId();
//                if(id == -1) {
//                    showSnackbar(getString(R.string.error_product_category_required));
//                }else{
//                    CompoundButton compoundButton = findViewById(id);
//                    String category = compoundButton.getText().toString();
//                    findViewById(R.id.add_category_progress_bar).setVisibility(View.VISIBLE);
//                    createCategory(category, 0);
//                    viewHideCategorysBottomSheet(true);
//                    mProductCategory.setText(category);
//                }
            }
        });
    }

    private void viewHideEditTextBottomSheet(Boolean HideBottomSheet, Boolean changeCategorySheetVisibility){
        if(HideBottomSheet)
        {
            findViewById(R.id.creating_custom_category_progress_bar).setVisibility(View.GONE);
            findViewById(R.id.grey_shade).setVisibility(View.GONE);
            bottomSheetBehaviorEditText.setState(BottomSheetBehavior.STATE_HIDDEN);
            if(changeCategorySheetVisibility)
                viewHideCategorysBottomSheet(false);
        }else{
            if(changeCategorySheetVisibility)
                viewHideCategorysBottomSheet(true);
            findViewById(R.id.grey_shade).setVisibility(View.VISIBLE);
            bottomSheetBehaviorEditText.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void viewHideUnitsBottomSheet(Boolean HideBottomSheet){
        if(HideBottomSheet)
        {
            findViewById(R.id.grey_shade).setVisibility(View.GONE);
            bottomSheetBehaviorUnits.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else{
            findViewById(R.id.grey_shade).setVisibility(View.VISIBLE);
            bottomSheetBehaviorUnits.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void viewHideCategorysBottomSheet(Boolean HideBottomSheet){
        if(HideBottomSheet)
        {
            findViewById(R.id.add_category_progress_bar).setVisibility(View.GONE);
            findViewById(R.id.grey_shade).setVisibility(View.GONE);
            bottomSheetBehaviorCategorys.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else{
            //findViewById(R.id.grey_shade).setVisibility(View.VISIBLE);
            loadUserCategories();
            bottomSheetBehaviorCategorys.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void setUnitChips(ArrayList<String> units) {
        ArrayList<Integer> chipId = new ArrayList<Integer>();
        int i = 0;
        for (String unit :
                units) {
            Chip mChip = (Chip) this.getLayoutInflater().inflate(R.layout.item_chip_unit, null, false);
//            int id = View.generateViewId();
//            mChip.setId(id);
//            chipId.add(id);
            mChip.setText(unit);
            i++;
            if(i == 1) {
                mChip.setChecked(true);
            }
//            int paddingDp = (int) TypedValue.applyDimension(
//                    TypedValue.COMPLEX_UNIT_DIP, 10,
//                    getResources().getDisplayMetrics()
//            );
//            mChip.setPadding(paddingDp, 0, paddingDp, 0);
            mChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //Log.w("Product", "Id is- " + chipsUnits.getCheckedChipId());
                    if(b) {
                        viewHideUnitsBottomSheet(true);
                        mProductQuantityUnit.setText(compoundButton.getText().toString());
                    }
                }
            });
            chipsUnits.addView(mChip);
        }
    }

    public void setCategoryChips(ArrayList<String> categorys) {
        for (String category :
                categorys) {
            Chip mChip = (Chip) this.getLayoutInflater().inflate(R.layout.category_chip, null, false);
            mChip.setText(category);
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics()
            );
            mChip.setPadding(paddingDp, 0, paddingDp, 0);
            int idChip = View.generateViewId();
            mChip.setId(idChip);

            AllCategoryIds.add(idChip);
            AllCategorys.add(category);

            mChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(chipsCategorys.getCheckedChipIds().size() > 0){
                        findViewById(R.id.btn_select_category).setEnabled(true);
                    }else{
                        findViewById(R.id.btn_select_category).setEnabled(false);
                    }
                    //if(b) {
                        //viewHideUnitsBottomSheet(true);
                        //showSnackbar(compoundButton.getText().toString());
                        //mProductCategory.setText(compoundButton.getText().toString());
                    //}
                }
            });
            chipsCategorys.addView(mChip);

            //Chip recentInitialisedChip = findViewById(R.id.idChip);
            ViewGroup.LayoutParams params = mChip.getLayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mChip.setLayoutParams(params);

            if(chooseUserCustomCategory){
                if(category.equals(userCustomCategory)) {
                    chooseUserCustomCategory = false;
                    mChip.setChecked(true);
                }
            }
        }
    }

    private void loadUserCategories(){
        findViewById(R.id.add_category_progress_bar).setVisibility(View.VISIBLE);

        DocumentReference storeDocRef = db.collection("storeowners").document(userPhoneNo);
        storeDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Log.w(TAG, "Doc exists "  + doc);
                        List<String> userCategories;
                        try{
                            userCategories = (List<String>) doc.get("categories");
                            ArrayList<String> uniqueCategories = new ArrayList<>();
                            for(String category : userCategories){
                                if(!categorys.contains(category)){
                                    uniqueCategories.add(category);
                                    categorys.add(category);
                                }
                            }
                            setCategoryChips(uniqueCategories);
                            findViewById(R.id.add_category_progress_bar).setVisibility(View.GONE);
                        }catch (Exception e){
                            //Field in the document doesn't exist
                            e.printStackTrace();
                            Log.w(TAG, "Exception - "  + e.getMessage());
                            findViewById(R.id.add_category_progress_bar).setVisibility(View.GONE);
                        }

                    }else{
                        findViewById(R.id.add_category_progress_bar).setVisibility(View.GONE);
                        showSnackbar(getString(R.string.no_internet));
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                } else {
                    findViewById(R.id.add_category_progress_bar).setVisibility(View.GONE);
                    showSnackbar(getString(R.string.no_internet));
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void uploadToFirebase(Uri imageUri, Item product) {
        ProgressBar progressUploadImage = findViewById(R.id.progress_uploading_image);
        progressUploadImage.setVisibility(View.VISIBLE);
        String nameofProduct = mProductName.getText().toString().trim();
        String productCategory = mProductCategory.getText().toString();

        if(nameofProduct==null || nameofProduct.isEmpty()){
            nameofProduct += "NA";
        }
        if(productCategory==null || productCategory.isEmpty()){
            productCategory += "NA";
        }

        if(imageUri != null){ //when calling this function compressed finalimageuri has been passed

            Log.w(TAG, "Image Uri is not null "+ imageUri);

            StorageReference fileref = storageRef.child("products").child(userPhoneNo).child(productCategory)
                    .child(nameofProduct+"_"+System.currentTimeMillis()+"_"+"_1."+getFileExtension(imageUri));
            fileref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            uploadphotoInProgress = false;
                            uploadedphotoSucessfully = true;
                            photoUrl = String.valueOf(task.getResult());
                            Snackbar.make(findViewById(android.R.id.content), "Uploaded Photo Successfully.", Snackbar.LENGTH_SHORT).show();
                            mAddProductProgressBar.setVisibility(View.GONE);
                            progressUploadImage.setVisibility(View.GONE);
                            /**
                             * Photo Upload is complete, now add product with imageUrl
                             */
                            ArrayList<String> images = product.getProductImage();
                            images.add(photoUrl);
                            product.setProductImage(images);
                            addProductToDatabase(product);
                            //imageFile.delete();//Deleting Compressed Image from Cache
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    mAddProductProgressBar.setVisibility(View.VISIBLE);
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    int currentprogress = (int) progress;
                    progressUploadImage.setProgress(currentprogress);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    uploadphotoInProgress = false;
                    uploadedphotoSucessfully = false;
                    Snackbar.make(findViewById(android.R.id.content), "Upload FAILED, Check internet.", Snackbar.LENGTH_SHORT).show();
                    mAddProductProgressBar.setVisibility(View.GONE);
                    progressUploadImage.setVisibility(View.GONE);
                    //showTodoToast("Uploading Failed!!");
                }
            });
        }else{
            showToastMsg("Please Select a Photo First!");
            uploadedphotoSucessfully = false;
            uploadphotoInProgress = false;
        }
    }

    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }

    /**
     * Below functions deal with Choosing Product Image, compressing it, correcting the orientation
     * & setting the appropriate iamge into imageview
     * @param permission
     * @param requestCode
     */
    private void checkPermission(String permission, int requestCode){
        // Checking if the permissions are not granted.
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // If not granted requesting Read and  Write storage
            ActivityCompat.requestPermissions(this, /*You can ask for multiple request by adding
            more permissions to the string*/new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        } else {
            // If permissions are granted
            buildImagePickerAlert();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted
                buildImagePickerAlert();
            }
            else {
                //Permission Denied
                showToastMsg("Storage Permission is Required.");
            }
        }
    }

    private void buildImagePickerAlert(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        dialog.setCancelable(true);

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.alert_to_choose_image_from_gallery_or_camera, null);

        TextView mDialogTitle = dialogLayout.findViewById(R.id.choose_photo_text_view);
        mDialogTitle.setText(getString(R.string.choose_product_image));
        ImageView mDismissDialogue = dialogLayout.findViewById(R.id.pick_dismiss_dialog);
        View mChooseCamera = dialogLayout.findViewById(R.id.pick_camera);
        View mChooseGallery = dialogLayout.findViewById(R.id.pick_gallery);

        dialog.setView(dialogLayout);
        AlertDialog alertDialog = dialog.create();

        mDismissDialogue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        });
        mChooseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                String fileName = "Leegumproducts.jpg";
                // Create parameters for Intent with filename
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                imageUri = getContentResolver()
                        .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, AppConstants.CAMERA_REQUEST);
            }
        });
        mChooseGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                //showTodoToast("Chosen Gallery");
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                //galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, AppConstants.GALLERY_REQUEST);
                //galleryIntent.getClipData();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == AppConstants.GALLERY_REQUEST && resultCode == RESULT_OK && data!=null){
            uploadphotoInProgress = false;
            uploadedphotoSucessfully = false;
            imageUri = data.getData();
            Log.w(TAG, "Data is" + data.toString());
            Log.w(TAG, "Imageuri from gallery is :" +imageUri.toString());
            mProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            compressImage();
        }else if(requestCode == AppConstants.CAMERA_REQUEST && resultCode == RESULT_OK){
            uploadphotoInProgress = false;
            uploadedphotoSucessfully = false;
            //imageUri = data.getData();  //Always returns NULL with camera
            Log.w(TAG, "Imageuri from camera image is" +imageUri.toString());
            mProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            compressImage();

        }else {
            uploadphotoInProgress = false;
        }
    }


    public Uri compressImage(){
        Bitmap fullSizeBitmap = null;
        try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                fullSizeBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
            } else {
                fullSizeBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            }
        }catch (Exception e){
            //Exception in getting bitmap from ImageUri
            fullSizeBitmap = null;
        }
        if(fullSizeBitmap!=null) {
            Bitmap reducedBitmap = ImageResizer.reduceBitmapSize(fullSizeBitmap, 120000);//120000);//2073600

            Bitmap bOutput = reducedBitmap;// = rotateBitmap(reducedBitmap, 270);
            try {
                Integer orientation = getOrientation(this, imageUri);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        Log.w(TAG, "rotating 90");
                        bOutput = rotateBitmap(reducedBitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        Log.w(TAG, "rotating 180");
                        bOutput = rotateBitmap(reducedBitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        Log.w(TAG, "rotating 270");
                        bOutput = rotateBitmap(reducedBitmap, 270);
                        break;
                    default:
                        break;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            finalImageUri = addToFav("LeegumStoreProducts", bOutput);
            mProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mProductImage.setImageURI(finalImageUri);
            return finalImageUri;
        }else{
            //image Compression failed
            finalImageUri = imageUri;
            mProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mProductImage.setImageURI(finalImageUri);
            return finalImageUri;
        }
    }

    public Bitmap rotateBitmap(Bitmap bInput,float degrees){
        //Bitmap bInput /*your input bitmap*/, bOutput;
        Bitmap bOutput;
        //float degrees = 90; //rotation degree
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        bOutput = Bitmap.createBitmap(bInput, 0, 0, bInput.getWidth(), bInput.getHeight(), matrix, true);
        return  bOutput;
    }

    private static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            cursor.close();
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();
        cursor = null;
        return orientation;
        //orientation here can be 90, 180, 270!
    }

    public Uri addToFav(String dirName, Bitmap bitmap) {
        Uri reducedUri;
        resultPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+
                dirName + System.currentTimeMillis() + ".jpg";
        Log.e("resultpath",resultPath);
        new File(resultPath).getParentFile().mkdir();

        if (Build.VERSION.SDK_INT < 29){

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Photo");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Edited");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put("_data", resultPath);

            ContentResolver cr = getContentResolver();
            reducedUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try {
                OutputStream fileOutputStream = new FileOutputStream(resultPath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                if(fileOutputStream != null){
                    Log.w(TAG, "Image Saved successfully after compressing");
                    //Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();
                }else{
                    Log.w(TAG, " Fileoutputstream returned null, Image could not be Saved");
                    Toast.makeText(this, "Error, Image Couldn't be Saved", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }

        }else {

            OutputStream fos = null;
            File file = new File(resultPath);

            final String relativeLocation = Environment.DIRECTORY_PICTURES;
            final ContentValues  contentValues = new ContentValues();

            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation+"/"+dirName);
            contentValues.put(MediaStore.MediaColumns.TITLE, "Photo");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis ());
            contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
            contentValues.put(MediaStore.MediaColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
            contentValues.put(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));

            final ContentResolver resolver = getContentResolver();
            final Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uri = resolver.insert(contentUri, contentValues);
            reducedUri = uri;
            try {

                fos = resolver.openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            if(fos != null){
                Log.w(TAG, "Image Saved successfully after compressing");
                //Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();
            }else{
                Log.w(TAG, " Fileoutputstream returned null, Image could not be Saved");
                Toast.makeText(this, "Image Couldn't be Saved", Toast.LENGTH_SHORT).show();
            }

        }
        return reducedUri;
    }


    private String createInfoMsgForSupport(int forEmailorText){
        String line = "\n ---------------------------------------------- \n";
        String msg = line + "Type your message above, Do not edit information below" + line;
        FirebaseUser mAuthUser = FirebaseAuth.getInstance().getCurrentUser();
        msg += "Unique code: " + mAuthUser.getUid()
                + " \n, name - " + mAuthUser.getDisplayName()
                + " \n, registered phone - " + mAuthUser.getPhoneNumber()
                + " \n, email -" + mAuthUser.getEmail() + line;// + "Do not edit this message" + line;

        if(forEmailorText == AppUtils.EMAIL){
            msg += "Do not edit this message" + line;
        }else{
            msg += "Send this message for info and then tell us your query or concern" + line;
        }

        return msg;
    }

    private void showTodoToast() {
        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show();
    }
    private void showToastMsg(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    private void showSnackbar(String str) {
        Snackbar.make(findViewById(android.R.id.content), str, Snackbar.LENGTH_LONG).show();
    }
}