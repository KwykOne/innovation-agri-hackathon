package com.akashorderandpickup.akashadminonp.util;

import android.content.Context;

import com.akashorderandpickup.akashadminonp.R;

//import com.orderandpickup.onp.R;


public class StoreType {
    //private Context context = null;
    int storetype = 0;
    String storetypename = "";
    String[] stringArray;
    String[] stringArrayNonAbbreviated;

    public StoreType() { }

    public StoreType(Context context, int storetype) {
        this.storetype = storetype;
        //this.context = context;
        //stringArray = context.getResources().getStringArray(R.array.stype_array);
        stringArray = context.getResources().getStringArray(R.array.one_word_stype_array_to_display_category);
    }

    public StoreType(Context context, String storetypename) {
        //this.storetypename = storetypename;
        //this.context = context;
        stringArray = context.getResources().getStringArray(R.array.one_word_stype_array_to_display_category);
        stringArrayNonAbbreviated = context.getResources().getStringArray(R.array.categories);
        storetype =  find(stringArrayNonAbbreviated, storetypename);
    }

//    public StoreType(Context context, int storetype, String storetypename) {
//        this.storetype = storetype;
//        this.storetypename = storetypename;
//        //this.context = context;
//        stringArray = context.getResources().getStringArray(R.array.stype_array);
//    }

    public int getStoretype() {
        return storetype;
    }

    public void setStoretype(int storetype) {
        this.storetype = storetype;
    }

    public String getStoretypename() {
        if(stringArray != null && stringArray.length > 0
                && stringArray.length >= storetype ){
            this.storetypename = stringArray[storetype];
        }
        return storetypename;
    }

//    public void setStoretypename(String storetypename) {
//        this.storetypename = storetypename;
//    }

    // Generic method to find the index of an element in an object array in Java
    public static<T> int find(T[] a, T target)
    {
        for (int i = 0; i < a.length; i++)
        {
            if (target.equals(a[i])) {
                return i;
            }
        }

        return 0;
        //return -1;
    }

}
