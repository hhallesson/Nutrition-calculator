package com.example.nutritioncalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class MealDetailsAdapter extends ArrayAdapter<MealItem> {

    public interface OnDeleteClickListener {
        void onDeleteClick(MealItem item);
    }

    private final OnDeleteClickListener deleteClickListener;
    private final CustomizationHandler customizationHandler;

    public MealDetailsAdapter(Context context, List<MealItem> items, CustomizationHandler customizationHandler, OnDeleteClickListener listener) {
        super(context, 0, items);
        this.deleteClickListener = listener;
        this.customizationHandler = customizationHandler;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MealItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_meal_detail, parent, false);
        }

        TextView mealItemText = convertView.findViewById(R.id.mealItemText);
        ImageButton deleteMealItemBtn = convertView.findViewById(R.id.deleteMealItemBtn);

        if (item != null) {
            mealItemText.setText(customizationHandler.formatMealItemText(item));

            deleteMealItemBtn.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(item);
                }
            });
        }

        AppStyler.styleListItemView(convertView, customizationHandler.getSettings());

        return convertView;
    }
}
