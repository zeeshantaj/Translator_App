package com.example.langauage_translator_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.langauage_translator_app.R;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] data;
    private int[] flagResources; // Array of flag resource IDs

    public CustomSpinnerAdapter(Context context, int resource, String[] data, int[] flagResources) {
        super(context, resource, data);
        this.context = context;
        this.data = data;
        this.flagResources = flagResources;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.custom_spinner_dropdown_item, null);
        }

        TextView textView = view.findViewById(R.id.nameTxt);
        ImageView flagImageView = view.findViewById(R.id.flagImageView);

        if (textView != null) {
            textView.setText(data[position]);
        }

        if (flagImageView != null && position < flagResources.length) {
            flagImageView.setImageResource(flagResources[position]);
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
