package com.orderandpickupforbusinesses.orderpickupforbusinesses;

//import static com.orderandpickup.onp.RestaurantDetailActivity.KEY_RESTAURANT_ID;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.adapter.ItemsAdapter;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.adapter.SearchSuggestionListAdapter;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.model.Item;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.model.Product;
import com.orderandpickupforbusinesses.orderpickupforbusinesses.util.AppUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements
        View.OnClickListener, ItemsAdapter.OnItemAddedListener, SearchSuggestionListAdapter.OnSearchSuggestionSelectedListener {
    private static final String TAG = "SearchActivity";
    public static final String OPEN_SEARCH = "startingSearching";
    public static final String KEY_SEARCH_TERM = "keySearchTerm";
    private MenuItem searchViewItem = null;
    private SearchView searchView = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    //private DocumentReference mRestaurantRef;
    private Query mQuery;
    private DocumentSnapshot suggestionsDoc = null;

    ListView listView;
    ArrayAdapter suggestionsListAdapter;
    List<String> main_text = new ArrayList<>();
    List<String> sub_text = new ArrayList<>();

    //Suggestions List
    ArrayList<String> suggestions = new ArrayList<>();
    ListView list;
    RelativeLayout mRelativeLayout;
    SearchSuggestionListAdapter searchSuggestionListAdapter;

//    private ViewGroup mTypeCardBlankSearch;
//    private EditText mTypeCardBlankSearchItemName;
//    private EditText mTypeCardBlankSearchQuantity;
//    private Button mTypeCardBlankSearchAddBtn;
    private ViewGroup mEmptyView;

    private RecyclerView mAllItemsRecycler;
    private ItemsAdapter mItemsAdapter;
    private Query itemsQuery;

    private String restaurantId;
    private String uid;
    private String mItemNameTypeByUser = "";
    private String searchTermOpening;

    private ArrayList<String> suggestionsArray = new ArrayList<String>();
    private ArrayList<String> dummyArray = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));

        mToolbar.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        // Get restaurant ID & Search term from extras
         searchTermOpening = getIntent().getExtras().getString(KEY_SEARCH_TERM);
         if(searchTermOpening.isEmpty())searchTermOpening=null;
//        restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        //String restaurantId = getIntent().getStringExtra(KEY_RESTAURANT_ID);
//        if (restaurantId == null) {
//            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
//        }

        mEmptyView = findViewById(R.id.empty_view_search);
//        mTypeCardBlankSearch = findViewById(R.id.type_card_blank_search);
//        mTypeCardBlankSearchItemName = findViewById(R.id.name_of_item);
//        mTypeCardBlankSearchQuantity = findViewById(R.id.quantity_of_item);
//        mTypeCardBlankSearchAddBtn = findViewById(R.id.button_add_item);

        //mTypeCardBlankSearchAddBtn.setOnClickListener(this);
        findViewById(R.id.btn_search_again).setOnClickListener(this);

        mAllItemsRecycler = findViewById(R.id.recycler_products);
        //listView = findViewById(R.id.search_suggestions);
        list = findViewById(R.id.search_suggestions);
        mRelativeLayout = findViewById(R.id.mySearchActivityLayout);
        //list.setVisibility(View.GONE);
        //listView.setVisibility(View.GONE);
        initListView();
        //Prepare product search suggestions
        initFirestore();
        fetchSuggestionsDoc();
        //initRecyclerView();
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("inventoryGlobal");//.limit(LIMIT);
        itemsQuery = mFirestore.collection("inventoryGlobal").limit(1);
    }

    private void initRecyclerView() {
        if (itemsQuery == null) {
            //mProgressBottom.setVisibility(View.GONE);
            Log.w(TAG, "No query, not initializing RecyclerView");
            return;
        }
        mItemsAdapter = new ItemsAdapter(itemsQuery, this, 0)
//                , getIntent().getExtras().getBoolean("acceptListOrder", true)
//                ,ItemsAdapter.products_in_search_activity, mItemNameTypeByUser)
        {
            @Override
            protected void onDataChanged() {
                Log.w(TAG, String.valueOf(getItemCount()));
                showTodoToast(getItemCount() + " matches");
                if(getItemCount()==0){
                    //Recycler View Item Typing Card won't be visible so make this one visible
                    //if(getIntent().getExtras().getBoolean("acceptListOrder", true))
                    //findViewById(R.id.card_for_typing_items).setVisibility(View.VISIBLE);

                    //mProgressTop.setVisibility(View.GONE);
                    //mProgressBottom.setVisibility(View.GONE);
                    //Toast.makeText(RestaurantDetailActivity.this,"Item count is 0", Toast.LENGTH_SHORT).show();
                    mAllItemsRecycler.setVisibility(View.GONE);
                    //mTypeCardBlankSearch.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.VISIBLE);
                    //findViewById(R.id.no_items_view).setVisibility(View.VISIBLE);
                    //mEmptyView.setVisibility(View.VISIBLE);
                    //findViewById(R.id.fab_categories).setVisibility(View.GONE);
                }else{
                    //Recycler View Item Typing Card will be visible so hide this one
                    //findViewById(R.id.card_for_typing_items).setVisibility(View.GONE);

                    //mProgressTop.setVisibility(View.GONE);
                    //mProgressBottom.setVisibility(View.GONE);
                    //Toast.makeText(RestaurantDetailActivity.this,"Item count is non - 0", Toast.LENGTH_SHORT).show();
                    mAllItemsRecycler.setVisibility(View.VISIBLE);
                    //mTypeCardBlankSearch.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.GONE);
                    //findViewById(R.id.no_items_view).setVisibility(View.GONE);
                    //mEmptyView.setVisibility(View.GONE);
                }
            }
        };
        mItemsAdapter.setQuery(itemsQuery);
        mAllItemsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAllItemsRecycler.setAdapter(mItemsAdapter);
        //mItemsAdapter.setQuery(itemsQuery);
        //mItemsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.toolbar || id==R.id.btn_search_again){
            expandSearch();
        }
        else if(id==R.id.button_add_item){
//            hideKeyboard();
//            String name = mTypeCardBlankSearchItemName.getText().toString().trim();
//            String quan = mTypeCardBlankSearchQuantity.getText().toString().trim();
//
//            if (TextUtils.isEmpty(name)) {
//                mTypeCardBlankSearchItemName.setError("PLEASE ENTER ITEM NAME");
//                mTypeCardBlankSearchItemName.requestFocus();
//                return;
//            } else if (TextUtils.isEmpty(quan)) {
//                mTypeCardBlankSearchQuantity.setError("PLEASE ENTER QUANTITY");
//                mTypeCardBlankSearchQuantity.requestFocus();
//                return;
//            }
//
//            for (int i = 0; i < quan.length(); i++) {
//                if(quan.charAt(i) >= '0' && quan.charAt(i) <= '9'){
//                    //Character is a digit
//                }else{
//                    mTypeCardBlankSearchQuantity.setError("QUANTITY MUST BE A NUMBER!");
//                    mTypeCardBlankSearchQuantity.requestFocus();
//                    return;
//                }
//            }
//
//            Item item_typed = new Item(name, "Unavailable");
//            item_typed.setProductName(name);
//            item_typed.setOrderQuantity(Long.parseLong(quan));
//
//            processHandTypedItem(item_typed);
//
//            mTypeCardBlankSearchItemName.setText("");
//            mTypeCardBlankSearchQuantity.setText("");
        }
    }

    private void expandSearch(){
        if(searchViewItem!=null){
            Log.e(TAG, "Expanding Search View");
            searchView.findViewById(R.id.search_button).performClick();
            //mTypeCardBlankSearch.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
        }else{
            Log.e(TAG, "SearchItemView NUll");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        searchViewItem = menu.findItem(R.id.search);
        if(searchViewItem!=null)
            searchView = (SearchView) searchViewItem.getActionView();
        //searchView.setBackgroundColor(Color.WHITE);
        //searchView.setQuery("maggi", false);
        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(true);
        if(searchTermOpening!=null) {
            Boolean submit = searchTermOpening.length()>2?true:false;
            searchView.setQuery(searchTermOpening, submit);
            searchTermOpening = null;
        }
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mAllItemsRecycler.setVisibility(View.GONE);
                    //mTypeCardBlankSearch.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.GONE);
                    //Make Suggestions list visible
                    list.setVisibility(View.VISIBLE);
                    //mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        });
        if(searchView!=null) {
            searchView.setQueryHint("Search Products...");
            searchView.setOnQueryTextListener(
                    new SearchView.OnQueryTextListener() {

                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            query = query.trim().toLowerCase(Locale.ROOT);
                            if(query.length()>2) {
                                hideKeyboard();
                                list.setVisibility(View.GONE);
                                //mRelativeLayout.setBackgroundColor(getResources().getColor(R.color.greyBackground));
                                searchView.clearFocus();
                                //mTypeCardBlankSearchItemName.setText(query);
                                mAllItemsRecycler.setVisibility(View.VISIBLE);
                                buildSearchQuery(query);
                                mItemNameTypeByUser = query;
                                initRecyclerView();
//                            if(mItemsAdapter!=null) {
//                                mItemsAdapter.setQuery(itemsQuery);
//                                mItemsAdapter.notifyDataSetChanged();
//                            }
                                //filter(query);
                            }else{
                                showTodoToast("Enter minimum 3 characters!");
                            }
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            displaySuggestions();
                            return false;
                        }
                    });
        }else{
            Log.e(TAG, "SearchView: NULL");
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void buildSearchQuery(String query) {
        query = query.trim().toLowerCase(Locale.ROOT);

        String[] searchTerms = query.toLowerCase(Locale.ROOT).split(" ");
        itemsQuery = mQuery.whereArrayContainsAny("index", Arrays.asList(searchTerms));
    }

    @Override
    public void onitemSelected(Item product, String id, int i) {
        if(i==0){
            ProgressDialog loading;
            loading = ProgressDialog.show(this, "Adding Item", "Please wait...", false, false);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userPhoneNo = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

                DocumentReference storeDocRef = mFirestore.collection("storeowners").document(userPhoneNo);
                DocumentReference productRef = storeDocRef.collection("items").document();
                String docId = productRef.getId();
                productRef.set(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.dismiss();
                        if (task.isSuccessful()) {
                            storeDocRef.update("itemsCount", FieldValue.increment(1));

                            String[] category = product.getProductCategory().toArray(new String[0]);
                            storeDocRef.update("categories", FieldValue.arrayUnion(category));

                            initAddProductActivity(product, docId);

                            //showSnackbar("Added " + product.getProductName());
                        } else {
                            Exception e = task.getException();
                            Log.w(TAG, "Adding product to database failed", e);
                            e.printStackTrace();
                            //showSnackbar("Failed with " + e.getMessage());
                        }
                    }
                });
            }

//            item.setListOrderIdentifier(1L);//Marking it as a global inventory item
//            item.setOrderQuantity((long) quantity);
//            item.setDocId(itemDoc.getId());
//            addItemToCartAsMap(item);
        }
    }

    private void initAddProductActivity(Item product, String docId){
        //if(!mSwitchAddDirectly.isChecked())//Send user to edit the item
        //{
            Intent intent = new Intent(SearchActivity.this, AddProductActivity.class);
            intent.putExtra(AddProductActivity.KEY_ADD_FROM_CATALGOUE, false);
            intent.putExtra(AddProductActivity.KEY_CATALOGUE_PRODUCT_ID, "");
            intent.putExtra(AddProductActivity.KEY_PRODUCT_DOCUMENT_ID, docId);
            intent.putExtra(AddProductActivity.KEY_EDIT_EXISTING_DOCUMENT, true);
            startActivity(intent);
        //}
    }


    private void processHandTypedItem(Item item){
//        item.setListOrderIdentifier(2L);//Marking it as a hand typed item
//        //get a new doc ID and then pass to addItemToCartAsMap
//        String docId = mFirestore.collection("inventoryGlobal").document().getId();
//        item.setDocId(docId);
//        addItemToCartAsMap(item);
    }

//    private void addItemToCartAsMap(Item item) {
//        String path = "items." + item.getDocId();//Path: items.docId
//
//        HashMap<String, Object> items = new HashMap<>();
//        items.put(item.getDocId(), item);
//
//        //So that the product gets added inside a map named items
//        HashMap<String, Object> prods = new HashMap<>();
//        prods.put("items", items);
//
//        DocumentReference documentReference = mFirestore.collection("users").document(uid).collection("cart").document(restaurantId);
//        //FieldValue.delete()
//
//        documentReference.update(path, item).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(@NonNull Void unused) {
//                Snackbar.make(findViewById(android.R.id.content), "Added: "+item.getProductName(), Snackbar.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if(e.getMessage()!=null && e.getMessage().contains("NOT_FOUND")) {
//                    documentReference.set(prods).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(@NonNull Void unused) {
//                            Snackbar.make(findViewById(android.R.id.content), "Added: "+item.getProductName(), Snackbar.LENGTH_SHORT).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            showTodoToast("Failed!");
//                        }
//                    });
//                }
//                else{
//                    showTodoToast("Failed: " + e.getMessage());
//                }
//            }
//        });
//    }


    @Override
    public void onSuggestionSelected(String suggestion, int identifier) {
        if(identifier==0){
            searchView.setQuery(suggestion, true);
        }else if(identifier == 1){
            searchView.setQuery(suggestion, false);
        }
    }

    private void filter(String text) {
        text = text.trim();

        // creating a new array list to filter our data.
        List<String> filteredlist = new ArrayList<>();
        List<String> containslist = new ArrayList<>();
        // running a for loop to compare elements.
//        for (String item : originalSortedList) {
//            // checking if the entered string matched with any item of our recycler view.
//            text.replace("&", "and");
//            item.replace("&", "and");
//
//            if(item.toLowerCase().startsWith(text.toLowerCase())){
//                filteredlist.add(item);
//            }else if (item.toLowerCase().contains(text.toLowerCase())) {
//                // if the item is matched we are
//                // adding it to our filtered list.
//                containslist.add(item);
//            }
//        }
//        if(!containslist.isEmpty()){
//            Collections.sort(containslist);
//            filteredlist.addAll(containslist);
//        }
//
//        if (!filteredlist.isEmpty() && adapter!=null) {
//            adapter.filterList(filteredlist);
//        }
    }

    private void initListView() {
//        suggestionsListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, suggestionsArray) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
//                //TextView text2 = (TextView) view.findViewById(android.R.id.text2);
//
//                text1.setText(suggestionsArray.get(position));
//                //text2.setText(suggestionsArray.get(position));
//                return view;
//            }
//        };
//        listView.setAdapter(suggestionsListAdapter);
        searchSuggestionListAdapter = new SearchSuggestionListAdapter(this, this, suggestionsArray);
        list.setAdapter(searchSuggestionListAdapter);
        //chatListAdapter.notifyDataSetChanged();
    }

    private void displaySuggestions(){
        if(dummyArray!=null){
            if(searchView!= null
                    && searchView.getQuery()!=null
                    && !TextUtils.isEmpty(searchView.getQuery())){
                String query = searchView.getQuery().toString().toLowerCase(Locale.ROOT);
                suggestionsArray.clear();
                for(String i: dummyArray){//Proper filtering logic is pending
                    if(i.toLowerCase(Locale.ROOT).contains(query))
                        suggestionsArray.add(i);
                }
            }else{
                suggestionsArray = new ArrayList<>(dummyArray);
            }
            Log.w(TAG, suggestionsArray.toString());
            if(suggestionsArray == null || suggestionsArray.isEmpty()) {
                suggestionsArray = new ArrayList<>();
                //suggestionsArray = new ArrayList<>(dummyArray);//when empty show all suggestions in default order
            }
            if(suggestionsArray!=null) {
                searchSuggestionListAdapter = new SearchSuggestionListAdapter(this, this, suggestionsArray);
                list.setAdapter(searchSuggestionListAdapter);
                searchSuggestionListAdapter.notifyDataSetChanged();
//                suggestionsListAdapter.notifyDataSetChanged();
//                listView.setAdapter(suggestionsListAdapter);
//                listView.setVisibility(View.VISIBLE);
            }
        }else{
            Log.e(TAG, "Not setting suggestions array");
        }
    }

    private void fetchSuggestionsDoc(){
        mFirestore.collection("searchIndex").document("productIndex").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    suggestionsDoc = task.getResult();
                    suggestionsDocToArrayList(suggestionsDoc);
                    displaySuggestions();
                }else{
                    if(!AppUtils.isConnectionAvailable(SearchActivity.this)){
                        showTodoToast("Couldn't connect to internet!");
                    }else{
                        //showTodoToast(task.getException().getMessage());
                    }
                }
            }
        });
    }

    private void suggestionsDocToArrayList(DocumentSnapshot suggestionsDoc){
        if(suggestionsDoc!=null && suggestionsDoc.get("index")!=null){
            dummyArray = (ArrayList<String>) suggestionsDoc.get("index");
            Log.i(TAG, dummyArray.toString());
        }
    }

    private void showTodoToast(String msg){
        if(msg == null || msg.isEmpty())
            msg = "TODO: Implement";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}