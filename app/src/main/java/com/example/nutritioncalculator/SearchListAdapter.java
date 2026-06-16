package com.example.nutritioncalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SearchListAdapter extends ArrayAdapter<FoodItem> {

    private CustomizationSettings settings;
    private CustomizationHandler customizationHandler;

    public SearchListAdapter(Context context, List<FoodItem> items, CustomizationSettings settings) {
        super(context, 0, items);
        setCustomizationSettings(settings);
    }

    public void setCustomizationSettings(CustomizationSettings settings) {
        this.settings = settings;
        this.customizationHandler = new CustomizationHandler(settings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FoodItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        if (item != null) {
            text1.setText(item.getName());
            String details = customizationHandler.formatFoodListDetails(item);
            text2.setText(details);
            text2.setVisibility(details.isEmpty() ? View.GONE : View.VISIBLE);
        } else {
            text1.setText("");
            text2.setText("");
            text2.setVisibility(View.GONE);
        }

        AppStyler.styleListItemView(convertView, settings);

        return convertView;
    }
}
