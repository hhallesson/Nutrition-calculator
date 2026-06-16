package com.example.nutritioncalculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class SavedMealsAdapter extends ArrayAdapter<String> {

    public interface OnDeleteClickListener {
        void onDeleteClick(String mealName);
    }

    public interface OnMealClickListener {
        void onMealClick(String mealName);
    }

    private final OnDeleteClickListener deleteClickListener;
    private final OnMealClickListener mealClickListener;
    private final CustomizationSettings settings;

    public SavedMealsAdapter(Context context,
                             List<String> mealNames,
                             CustomizationSettings settings,
                             OnDeleteClickListener deleteListener,
                             OnMealClickListener mealListener) {
        super(context, 0, mealNames);
        this.settings = settings;
        this.deleteClickListener = deleteListener;
        this.mealClickListener = mealListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String mealName = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_saved_meal, parent, false);
        }

        TextView savedMealNameText = convertView.findViewById(R.id.savedMealNameText);
        ImageButton deleteSavedMealBtn = convertView.findViewById(R.id.deleteSavedMealBtn);

        if (mealName != null) {
            savedMealNameText.setText(mealName);

            convertView.setOnClickListener(v -> {
                if (mealClickListener != null) {
                    mealClickListener.onMealClick(mealName);
                }
            });

            deleteSavedMealBtn.setOnClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(mealName);
                }
            });
        }

        AppStyler.styleListItemView(convertView, settings);

        return convertView;
    }
}
