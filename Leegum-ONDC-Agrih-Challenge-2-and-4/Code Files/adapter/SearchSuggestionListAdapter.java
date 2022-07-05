package com.orderandpickupforbusinesses.orderpickupforbusinesses.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.orderandpickupforbusinesses.orderpickupforbusinesses.R;

import java.util.List;

public class SearchSuggestionListAdapter extends BaseAdapter {
    Context context;
    List<String> suggestions;
    LayoutInflater inflater;


    public interface OnSearchSuggestionSelectedListener {
        //identifier = 0 to search directly
        //identifier = 1 to edit query before search
        void onSuggestionSelected(String suggestion, int identifier);
    }

    private OnSearchSuggestionSelectedListener mListener;
    public SearchSuggestionListAdapter(Context context, OnSearchSuggestionSelectedListener listener, List<String> suggestions) {
        this.mListener = listener;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.suggestions = suggestions;
    }
    @Override
    public int getCount() {
        if (suggestions != null) {
            Log.w("TAG: SearchListAdapter", "suggestions not null");
            return suggestions.size();
        }else {
            Log.w("TAG: SearchListAdapter", "suggestions null");
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(suggestions ==null)return -1;
        return suggestions.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup vg;

        if (convertView != null) {
            vg = (ViewGroup) convertView;
        } else {
            vg = (ViewGroup) inflater.inflate(R.layout.search_suggestion_layout, null);
        }

        String suggestion = suggestions.get(position);

        final TextView txt = ((TextView) vg.findViewById(R.id.search_suggestion_text));
        final ImageView btn_edit_suggestion = ((ImageView) vg.findViewById(R.id.btn_edit_search_suggestion_text));
        txt.setText(suggestions.get(position));
        txt.setVisibility(View.VISIBLE);
        btn_edit_suggestion.setVisibility(View.VISIBLE);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSuggestionSelected(suggestions.get(position), 0);
                }
            }
        });
        btn_edit_suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSuggestionSelected(suggestions.get(position), 1);
                }
            }
        });
        return vg;
    }
}
