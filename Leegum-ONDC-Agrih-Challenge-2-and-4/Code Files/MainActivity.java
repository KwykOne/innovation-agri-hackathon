package com.akashorderandpickup.akashadminonp;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akashorderandpickup.akashadminonp.model.Product;
import com.akashorderandpickup.akashadminonp.model.ProductDocument;
import com.akashorderandpickup.akashadminonp.services.ScreenCaptureService;
import com.akashorderandpickup.akashadminonp.util.AppUtils;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 7001;
    private static final int REQUEST_CODE = 100;
    private static final int LIMIT = 50;

    private FirebaseAuth mAuth;

    private FirebaseFirestore mFirestore;
    private EditText mEnterText;

    private static final String RESULT_CODE = "RESULT_CODE";
    private static final String DATA = "DATA";
    private static final String ACTION = "ACTION";
    private static final String START = "START";
    private static final String STOP = "STOP";
    private static final String SCREENCAP_NAME = "screencap";
    private static int IMAGES_PRODUCED;

    private MediaProjection mMediaProjection;
    private String mStoreDir;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    //private OrientationChangeCallback mOrientationChangeCallback;
    public Context ctx = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mAuth = FirebaseAuth.getInstance();
        //Display Login Status
        TextView user_info = findViewById(R.id.user_info);
        if(mAuth.getCurrentUser()==null){
            user_info.setText(getString(R.string.user_info_dummy));
            findViewById(R.id.login_view).setVisibility(View.VISIBLE);
//            findViewById(R.id.after_login_view).setVisibility(View.GONE);
        }else{
            findViewById(R.id.login_view).setVisibility(View.GONE);
//            findViewById(R.id.after_login_view).setVisibility(View.VISIBLE);
            user_info.setText(getString(R.string.user_info_dummy)
                    + '\n' + mAuth.getCurrentUser().getEmail()
                    + '\n' + mAuth.getCurrentUser().getDisplayName()
            );
        }

        initFirestore();
        mEnterText = findViewById(R.id.excel_url);
        mEnterText.setText("Akash 0   1 Pratap  Singh0");

        //Set CLick Listeners
        findViewById(R.id.google_sign_in_btn).setOnClickListener(this);
        findViewById(R.id.user_info).setOnClickListener(this);
        findViewById(R.id.btn_process_excel).setOnClickListener(this);
        findViewById(R.id.open_recycler).setOnClickListener(this);
        findViewById(R.id.approve_new_stores).setOnClickListener(this);
        findViewById(R.id.open_accessibility).setOnClickListener(this);
        findViewById(R.id.send_msg).setOnClickListener(this);
        findViewById(R.id.send_msg_auto).setOnClickListener(this);
        findViewById(R.id.send_msg_whatsapp).setOnClickListener(this);


        Dexter.withContext(this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

        Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

        // start projection
        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                startProjection();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initScreenShot();
                    }
                }, 4000);

                // get width and height
                mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
                mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
                //mVirtualDisplay = mMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
                //mImageReader.setOnImageAvailableListener(new ScreenCaptureService.ImageAvailableListener(), mHandler);
            }
        });

        // stop projection
        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopProjection();
            }
        });
        // create products collection
        Button mCreateProductsCollection = findViewById(R.id.make_products_collection);
        mCreateProductsCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String[] ids = {"0N6Gq0oEWeY0gF8gHvqs", "2jLBmMUMTLSeDn6UbSF8", "2jLBmMUMTLSeDn6UbSF8", "4CD8O6MkUFyrflPFNJE3", "7DqyojTZWFWGUMTEvrWi", "7DqyojTZWFWGUMTEvrWi", "8DN7TVpIXhbTUgZTv5Id", "9gGneC9fHB8q9flsUeDI", "9gGneC9fHB8q9flsUeDI", "CJPHztERRhPx08mKWotB", "CMIhop62QdSF1YQwCdpI", "CMIhop62QdSF1YQwCdpI", "DwyMIxjENU9SwIc50gVW", "H4mRewCRxEAacb1nubUQ", "H4mRewCRxEAacb1nubUQ", "HDVsjxssVg8EfmukJebI", "LtivKmYSPRq1IZGakMFW", "LtivKmYSPRq1IZGakMFW", "N5henQOYHe8TlEcqOfjq", "NfNSqSFQBsVw7aj2hzda", "NfNSqSFQBsVw7aj2hzda", "OabG4f1wmiU4I8DFuNlK", "OigAtMIvW6nBw1zrpiwB", "OigAtMIvW6nBw1zrpiwB", "PFYgheL2agC6qNfTq82g", "PbKMlXLA2RibliZnfL5k", "PbKMlXLA2RibliZnfL5k", "QbaoBQXCxay6CWrm80sP", "QrmWJsbsRvQXUENl9MSn", "QrmWJsbsRvQXUENl9MSn", "RtlU3h2VZ0NMoCqrrnkK", "VFJS1DDjutUXv1mmtS2D", "VFJS1DDjutUXv1mmtS2D", "WZJP5hKoT0QCZrr9busi", "WrIfB4dye5EYOEqfdVIZ", "WrIfB4dye5EYOEqfdVIZ", "X0vq8Ugk9fgzxV9PuTr5", "YHRF1XSrj83nc519ELBj", "YHRF1XSrj83nc519ELBj", "categories", "fb0lnh6tfMsCfKiptphU", "fb0lnh6tfMsCfKiptphU", "gp4ch8w9AdYdw2uXUpOn", "hpHNXG3z2YMybsCxzsbf", "hpHNXG3z2YMybsCxzsbf", "jAHxtg2s7156a1gGcLbR", "kWTzbz5MWpOQpuLstQPi", "kWTzbz5MWpOQpuLstQPi", "nnFl84RnZTIrwpoxm2T6", "oKf65PXZ8X9RSWnJRR7s", "oKf65PXZ8X9RSWnJRR7s", "omm4dMqIAD6lTy6fXQ2Q", "pDvQoLPLeXBaHfyRMH2v", "pDvQoLPLeXBaHfyRMH2v", "pNCCESdQnfBAVRyCX2QG", "qja8XWtRsB88H3ucEZoT", "qja8XWtRsB88H3ucEZoT", "r5ENAlKiSEegEDe9QQ8k", "tbQjilISdkQhFVEAQBK0", "tbQjilISdkQhFVEAQBK0", "ucWwXyCHOTX3eKMjPZjp", "vloH774K1xDHFnBvRzEj", "vloH774K1xDHFnBvRzEj", "x5z1R8kuVY7Il98y6Bag", "zTbGdUV2jNaHiUpNfUB2", "zTbGdUV2jNaHiUpNfUB2"};
                String[] ids = {"0N6Gq0oEWeY0gF8gHvqs", "2jLBmMUMTLSeDn6UbSF8", "2jLBmMUMTLSeDn6UbSF8", "4CD8O6MkUFyrflPFNJE3", "7DqyojTZWFWGUMTEvrWi", "7DqyojTZWFWGUMTEvrWi", "8DN7TVpIXhbTUgZTv5Id", "9gGneC9fHB8q9flsUeDI", "9gGneC9fHB8q9flsUeDI", "CJPHztERRhPx08mKWotB", "CMIhop62QdSF1YQwCdpI", "CMIhop62QdSF1YQwCdpI", "DwyMIxjENU9SwIc50gVW", "H4mRewCRxEAacb1nubUQ", "H4mRewCRxEAacb1nubUQ", "HDVsjxssVg8EfmukJebI", "LtivKmYSPRq1IZGakMFW", "LtivKmYSPRq1IZGakMFW", "N5henQOYHe8TlEcqOfjq", "NfNSqSFQBsVw7aj2hzda", "NfNSqSFQBsVw7aj2hzda", "OabG4f1wmiU4I8DFuNlK", "OigAtMIvW6nBw1zrpiwB", "OigAtMIvW6nBw1zrpiwB", "PFYgheL2agC6qNfTq82g", "PbKMlXLA2RibliZnfL5k", "PbKMlXLA2RibliZnfL5k", "QbaoBQXCxay6CWrm80sP", "QrmWJsbsRvQXUENl9MSn", "QrmWJsbsRvQXUENl9MSn", "RtlU3h2VZ0NMoCqrrnkK", "VFJS1DDjutUXv1mmtS2D", "VFJS1DDjutUXv1mmtS2D", "WZJP5hKoT0QCZrr9busi", "WrIfB4dye5EYOEqfdVIZ", "WrIfB4dye5EYOEqfdVIZ", "X0vq8Ugk9fgzxV9PuTr5", "YHRF1XSrj83nc519ELBj", "YHRF1XSrj83nc519ELBj", "categories", "fb0lnh6tfMsCfKiptphU", "fb0lnh6tfMsCfKiptphU", "gp4ch8w9AdYdw2uXUpOn", "hpHNXG3z2YMybsCxzsbf", "hpHNXG3z2YMybsCxzsbf", "jAHxtg2s7156a1gGcLbR", "kWTzbz5MWpOQpuLstQPi", "kWTzbz5MWpOQpuLstQPi", "nnFl84RnZTIrwpoxm2T6", "oKf65PXZ8X9RSWnJRR7s", "oKf65PXZ8X9RSWnJRR7s", "omm4dMqIAD6lTy6fXQ2Q", "pDvQoLPLeXBaHfyRMH2v", "pDvQoLPLeXBaHfyRMH2v", "pNCCESdQnfBAVRyCX2QG", "qja8XWtRsB88H3ucEZoT", "qja8XWtRsB88H3ucEZoT", "r5ENAlKiSEegEDe9QQ8k", "tbQjilISdkQhFVEAQBK0", "tbQjilISdkQhFVEAQBK0", "ucWwXyCHOTX3eKMjPZjp", "vloH774K1xDHFnBvRzEj", "vloH774K1xDHFnBvRzEj", "x5z1R8kuVY7Il98y6Bag", "zTbGdUV2jNaHiUpNfUB2", "zTbGdUV2jNaHiUpNfUB2"};
                ArrayList<String> docIds = new ArrayList<>(Arrays.asList(ids).subList(0, 6));
                //docIds.add("0N6Gq0oEWeY0gF8gHvqs");
                //docIds.add("2jLBmMUMTLSeDn6UbSF8");
                //docIds.add("4CD8O6MkUFyrflPFNJE3");
                //createIndex("Atta chawal dalmakhani");
                disperseProducts(docIds);
//                ArrayList<DocumentSnapshot> list = new ArrayList<>();
//                int[] totalDocs = {0};
//                Query iterateDocsQuery = mFirestore.collection("catalog").limit(3);
//                iterateThroughCollection(iterateDocsQuery, list, totalDocs);
//
//                Log.e(TAG, "\nTotal Docs in collection: " + totalDocs[0]);
//                Log.e(TAG, "\nIDs of All Docs in collection: \n" + docIds);
//                Log.e(TAG, "\nIDs of All Docs in collection: \n" + docIds.toArray());

                /*
                If you'll keep only below line inside onClick then
                this will create the index for string in EditText on top
                of page. You can log the index to see results.
                 */
                //ArrayList<String> index = AppUtils.createIndex(mEnterText.getText().toString(), true);
            }
        });
    }

    private void iterateThroughCollection(Query query, ArrayList<DocumentSnapshot> list, int[] totalDocs){
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot documentSnapshots = task.getResult();
                    int n = documentSnapshots.getDocuments().size();
                    if(n>0) {
                        list.addAll(documentSnapshots.getDocuments());
                        totalDocs[0] += n;
                    }
                    if (n < 3) {
                        Log.w(TAG, "All Docs Iterated, Breaking Loop\n");
                        Log.w(TAG, "\n------------------------\n");
                        Log.w(TAG, "\n_Total Docs in collection: " + totalDocs[0]);
                        ArrayList<String> docIds = new ArrayList<>();
                        for(DocumentSnapshot doc: list){
                            docIds.add(doc.getId());
                        }
                        Log.w(TAG, "\n_IDs of All Docs in collection: \n" + docIds);
                        Log.w(TAG, "\n_IDs of All Docs in collection: \n" + docIds.toArray());
                        Log.w(TAG, "\n------------------------\n");
                    } else {
                        Log.w(TAG, "Creating Next Query After Doc: "
                                + documentSnapshots.getDocuments().get(n - 1).getId() + "\n");
                        Query next = mFirestore.collection("catalog")
                                .startAt(documentSnapshots.getDocuments().get(n - 1))
                                .limit(3);
                        iterateThroughCollection(next, list, totalDocs);
                    }
                }
            }
        });

        /**
         *  Output with All doc IDs  - total count 67
         */
        //List containing IDs of All Docs.
        //[000, 0N6Gq0oEWeY0gF8gHvqs, 2jLBmMUMTLSeDn6UbSF8, 2jLBmMUMTLSeDn6UbSF8, 4CD8O6MkUFyrflPFNJE3, 7DqyojTZWFWGUMTEvrWi, 7DqyojTZWFWGUMTEvrWi, 8DN7TVpIXhbTUgZTv5Id, 9gGneC9fHB8q9flsUeDI, 9gGneC9fHB8q9flsUeDI, CJPHztERRhPx08mKWotB, CMIhop62QdSF1YQwCdpI, CMIhop62QdSF1YQwCdpI, DwyMIxjENU9SwIc50gVW, H4mRewCRxEAacb1nubUQ, H4mRewCRxEAacb1nubUQ, HDVsjxssVg8EfmukJebI, LtivKmYSPRq1IZGakMFW, LtivKmYSPRq1IZGakMFW, N5henQOYHe8TlEcqOfjq, NfNSqSFQBsVw7aj2hzda, NfNSqSFQBsVw7aj2hzda, OabG4f1wmiU4I8DFuNlK, OigAtMIvW6nBw1zrpiwB, OigAtMIvW6nBw1zrpiwB, PFYgheL2agC6qNfTq82g, PbKMlXLA2RibliZnfL5k, PbKMlXLA2RibliZnfL5k, QbaoBQXCxay6CWrm80sP, QrmWJsbsRvQXUENl9MSn, QrmWJsbsRvQXUENl9MSn, RtlU3h2VZ0NMoCqrrnkK, VFJS1DDjutUXv1mmtS2D, VFJS1DDjutUXv1mmtS2D, WZJP5hKoT0QCZrr9busi, WrIfB4dye5EYOEqfdVIZ, WrIfB4dye5EYOEqfdVIZ, X0vq8Ugk9fgzxV9PuTr5, YHRF1XSrj83nc519ELBj, YHRF1XSrj83nc519ELBj, categories, fb0lnh6tfMsCfKiptphU, fb0lnh6tfMsCfKiptphU, gp4ch8w9AdYdw2uXUpOn, hpHNXG3z2YMybsCxzsbf, hpHNXG3z2YMybsCxzsbf, jAHxtg2s7156a1gGcLbR, kWTzbz5MWpOQpuLstQPi, kWTzbz5MWpOQpuLstQPi, nnFl84RnZTIrwpoxm2T6, oKf65PXZ8X9RSWnJRR7s, oKf65PXZ8X9RSWnJRR7s, omm4dMqIAD6lTy6fXQ2Q, pDvQoLPLeXBaHfyRMH2v, pDvQoLPLeXBaHfyRMH2v, pNCCESdQnfBAVRyCX2QG, qja8XWtRsB88H3ucEZoT, qja8XWtRsB88H3ucEZoT, r5ENAlKiSEegEDe9QQ8k, tbQjilISdkQhFVEAQBK0, tbQjilISdkQhFVEAQBK0, ucWwXyCHOTX3eKMjPZjp, vloH774K1xDHFnBvRzEj, vloH774K1xDHFnBvRzEj, x5z1R8kuVY7Il98y6Bag, zTbGdUV2jNaHiUpNfUB2, zTbGdUV2jNaHiUpNfUB2]
    }

    public ArrayList<String> createIndex(String productName) {
        ArrayList<String> index = new ArrayList<>();
        while (productName.contains("  "))
            productName = productName.trim().replace("  ", " ");
        index.add(productName);
        productName = productName.trim().toLowerCase(Locale.ROOT);
        String[] wordsToRemove = {"a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "shall", "with"};
        List<String> wordsToRemoveList = Arrays.asList(wordsToRemove);
        String[] charsToRemove = {"_", "?", "|", "~", "!", "@", "#", "$", "₹", "%", "'", "*", "\"", "\\", "{", "}", "[", "]", "(", ")"};
        for(String character: charsToRemove)
            productName = productName.replace(character, " ");
        Log.e(TAG, productName);
        while (productName.contains("  "))
            productName = productName.trim().replace("  ", " ");
        Log.e(TAG, productName);

        if(productName.length()>3){
            String[] indexes = productName.split("[\\s,]+", 51);
            for(String i: indexes){
                if(!i.isEmpty() && !wordsToRemoveList.contains(i)){
                    index.add(i);
                    if(i.length()>3)
                        for(int l=3; l<i.length(); l++){
                            index.add(i.substring(0, l));
                        }
                }
            }
        }
        ArrayList<String> uniqueArrayList = new ArrayList<String>();
        for(int i = 0; i < index.size(); i++){
            if(!uniqueArrayList.contains(index.get(i))){ // If the value isn't in the list already
                uniqueArrayList.add(index.get(i));
            }
        }
        Log.w(TAG, "Index is: " + uniqueArrayList);
        return uniqueArrayList;
    }

    private void disperseProducts(ArrayList<String> docIds) {
        CollectionReference mCatalogRef = mFirestore.collection("catalog");
        for(String docId: docIds){
            //Query mQuery = mCatalogRef.get(docId);
            mCatalogRef.document(docId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();

                        HashMap<String, ArrayList<String>> indexMap = new HashMap<>();
                        ArrayList<String> index = new ArrayList<>();

                        ArrayList<Product> products = (ArrayList<Product>)document.toObject(ProductDocument.class).products;
                        int i = 0;
                        for(Product product: products){
                            Log.w(TAG, "Running Loop: " + i++);
                            index.add(product.getProductName());
                            //product.createIndex(product.getProductName());
                            product.setIndex(AppUtils.createIndex(product.getProductName(), true));
                            DocumentReference productDoc = mFirestore.collection("inventoryGlobal").document();
                            //mFirestore.collection("inventoryGlobal").add(product).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            int finalI = i;
                            productDoc.set(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.w(TAG, "product Added " + finalI + " : " + productDoc.getId());
                                    }else{
                                        Log.e(TAG, "Error Adding product " + finalI + " : " + productDoc.getId());
                                        Log.e(TAG, product.toString());
                                    }
                                }
                            });
                        }

                        indexMap.put("index", index);
                        updateProductSearchIndex(indexMap);

                    }else{
                        Log.e(TAG, "Error: " + task.getException());
                    }
                }
            });
        }
    }

    private void updateProductSearchIndex(HashMap<String, ArrayList<String>> indexMap){
        mFirestore.collection("searchIndex").document("productIndex").set(indexMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.w(TAG, "index Updated: \n" + indexMap.get("index"));
                }else{
                    Log.e(TAG, "Error updating index: \n" + indexMap.get("index"));
                }
            }
        });
    }

    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();
        //mQuery = mFirestore.collection("restaurants").orderBy("avgRating", Query.Direction.DESCENDING).limit(LIMIT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_process_excel){
            showTodoToast();
        }
        else if(id == R.id.user_info) {
            if(mAuth.getCurrentUser()!=null)showToastMsg(mAuth.getCurrentUser().getDisplayName() + "\n" + mAuth.getCurrentUser().getEmail());
        }else if(id == R.id.google_sign_in_btn) {
            logIn();
        }
        else if(id == R.id.open_recycler) {
            Intent intent = new Intent(this, ExcelActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.approve_new_stores) {
            Intent intent = new Intent(this, StoresActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.open_accessibility) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//            Intent intent = new Intent(this, StoresActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.send_msg) {
            Intent sendSms = new Intent(Intent.ACTION_SEND);
            sendSms.setType("text/plain");
            sendSms.putExtra(Intent.EXTRA_TEXT, "Message via Intent");
            startActivity(sendSms);
        }
        else if(id == R.id.send_msg_auto) {
            try{
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("8349318108", null, "Automatic Text Message send from Leegum Admin App", null, null);
                showSnackbar("Msg Sent", "");
            }catch (Exception e){
                showSnackbar("Failed: " + e.getMessage(), "");
            }
        }
        else if(id == R.id.send_msg_whatsapp) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//            Intent intent = new Intent(this, StoresActivity.class);
            startActivity(intent);
        }
        else{
            showTodoToast();
        }
    }

    private void enableAccessibilityService() {
//        String packageName = "com.akashorderandpickup.akashadminonp";
//        String className = "$packageName.service.accessibility.GlobalActionBarService";
//        String string = "enabled_accessibility_services";
//        String cmd = "settings put secure $string $packageName/$className";
//        InstrumentationRegistry.getInstrumentation()
//                .getUiAutomation(UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES)
//                .executeShellCommand(cmd)
//                .close();
//        TimeUnit.SECONDS.sleep(3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                finish();
                startActivity(getIntent());
                break;
            case R.id.your_orders:
                break;
            case R.id.go_to_cart:
                break;
            case R.id.show_pincode_bar:
                showTodoToast();
                break;
            case R.id.report_issue:
                 showTodoToast();
                break;
            case R.id.referral_section:
                showTodoToast();
                break;
            case R.id.log_in:
                logIn();
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                findViewById(R.id.login_view).setVisibility(View.VISIBLE);
                startSignIn();
                break;
//            case R.id.customer_support:
//                getCustomerSupport();
//                break;
//            case R.id.referral:
//                getReferralDetails();
//                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void startSignIn() {
        // Sign in with FirebaseUI
        Intent intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Collections.singletonList(
                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                        //new AuthUI.IdpConfig.EmailBuilder().build()))
                .setIsSmartLockEnabled(false)
                .build();

        startActivityForResult(intent, RC_SIGN_IN);
        //mViewModel.setIsSigningIn(true);
    }

    private void logIn() {
        if(mAuth.getCurrentUser()==null){
            startSignIn();
        }else {
            findViewById(R.id.login_view).setVisibility(View.GONE);
            try {
                showToastMsg("Already Logged In as "+mAuth.getCurrentUser().getDisplayName() +"("+mAuth.getCurrentUser().getEmail()+")");
            }catch (Exception e){
                showToastMsg("Already Logged In");
                Log.w(TAG, e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            //mViewModel.setIsSigningIn(false);
            if(resultCode == RESULT_OK){
                findViewById(R.id.login_view).setVisibility(View.GONE);
                showToastMsg("Logged In - "
                        + mAuth.getCurrentUser().getEmail()
                );

            }else{
                findViewById(R.id.login_view).setVisibility(View.VISIBLE);
                //signing in failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if(response==null){
                    Log.d(TAG, "onActivityResult: " + "user has cancelled the sign in request");
                }else{
                    Log.d(TAG, "onActivityResult: " +response.getError());
                    Toast.makeText(this,String.valueOf(response.getError()),Toast.LENGTH_SHORT).show();
                }

            }
        }
        else if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startService(ScreenCaptureService.getStartIntent(this, resultCode, data));
            }
        }
    }

    private void startProjection() {
        MediaProjectionManager mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        startService(ScreenCaptureService.getStopIntent(this));
    }

    private void initScreenShot(){
        Log.w(TAG, "inside initSS function");
        //String value = MyApplication.preferences.getString(ScreenCaptureService.SCREENSHOT_NAME, "filename");
        //Log.w(TAG, "value of filename: " + value);
        //SharedPreferences.Editor editor = MyApplication.preferences.edit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ScreenCaptureService.SCREENSHOT_CAPTURE, TRUE);
        editor.putBoolean(ScreenCaptureService.SCREENSHOT_SAVED, FALSE);
        editor.putString(ScreenCaptureService.SCREENSHOT_NAME, "screenshot_testing99");
        editor.commit();
    }

    private void showSnackbar(String str, String textBtn) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_activity_coordinator_layout)
                        , str
                        , Snackbar.LENGTH_LONG);

        if(!textBtn.isEmpty()) {
            snackbar.setAction(textBtn, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Perform Action On click
                    if(textBtn.equals(getString(R.string.ok_btn))){
                        showTodoToast();
                    }
                }
            });
        }
        //snackbar.setActionTextColor(getResources().getColor(R.color.action_color));
        snackbar.show();
    }
    private void showAlert(String title, String msg){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setCancelable(false);
        dialog.setMessage(msg);
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        dialog.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showTodoToast();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
    private void showTodoToast() {
        Toast.makeText(this, "TODO: Implement", Toast.LENGTH_SHORT).show();
    }
    private void showToastMsg(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}